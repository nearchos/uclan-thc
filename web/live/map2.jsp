<%@ page import="uk.ac.uclan.thc.model.Parameter" %>
<%@ page import="uk.ac.uclan.thc.data.ParameterFactory" %>
<%@ page import="java.util.logging.Logger" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta name="viewport" content="initial-scale=1.0, user-scalable=no"/>
    <title>Treasure hunt challange map</title>

    <script src="https://cdn.jsdelivr.net/npm/vue@2.5.13/dist/vue.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/moment.js/2.20.1/moment.min.js"></script>
    <script src="https://cdn.ably.io/lib/ably.min-1.js"></script>

    <script src="https://cdnjs.cloudflare.com/ajax/libs/lodash.js/4.17.5/lodash.min.js"></script>
    <script src="/live/vue-google-maps.js"></script>

    <style>
        /* Copied */

        html {
            height: 100%
        }

        body {
            height: 100%;
            margin: 0;
            padding: 0
        }

        #scoreboard {
            position: absolute;
            width: 400px;
            top: 20px;
            bottom: 20px;
            right: 20px;
            background-color: white;
            text-align: center;
            border: 2px solid #bbb;
            border-radius: 10px;
            overflow: auto;
        }

        /* Extracted */

        #teams {
            padding: 10px;
        }

        #header-container {
            margin: 0 auto;
            width: 350px;
            text-align: center;
        }

        #category-title {
            font-family: sans-serif;
            color: #CE0E41;
            font-weight: bold;
            font-size: 28px;
        }

        #category-status {
            font-family: sans-serif;
            font-weight: bolder;
            font-size: 16px;
            color: #f00;
        }

        #teams table {
            font-size: 20px;
            padding: 10px;
        }

        #teams table th, #teams table td {
            width: 100px;
        }

        /* Custom */

        .map-parent {
            height: 100%;
            width: 100%;
        }

        /* Scoreboard animations */

        .animated-table-body-move {
            transition: transform 0.7s;
        }

        #teams table tr {
            transition: all 0.7s;
        }

        .animated-table-body-enter, .animated-table-body-leave-to {
            opacity: 0;
            transform: translateY(30px);
        }

        .animated-table-body-leave-active {
            position: absolute;
        }
    </style>
</head>
<body>

<div id="vue-app"></div>

<script type="text/x-template" id="app-template">
    <div class="map-parent">
        <gmap-map style="width: 100%; height: 100%;"
            :center="{lat: 35.008154, lng: 33.6975}"
            :zoom="19"
            :options="mapOptions"
        >

            <gmap-marker :key="'marker-' + marker.session_id" v-for="marker in mapMarkers" :position="marker.position" :clickable="false"></gmap-marker>
            <gmap-info-window :position="marker.position" :key="'info-' + marker.session_id" v-for="marker in mapMarkers" :options="infoWindowOptions">
                <h3>{{ marker.player_name }}</h3>
                <p style="color: #CE0E41">{{ marker.score }} pts</p>
            </gmap-info-window>

        </gmap-map>

        <div id="scoreboard">
            <img src="/live/uclan_small.png" alt="UCLan Cyprus logo" />
            <div id="teams">
                <template v-if="!isLoaded">
                    Loading...
                </template>
                <template v-else>
                    <div id="header-container">
                        <h1 id="category-title">{{ category.name }}</h1>
                        <div id="category-status">{{ categoryStatus }}</div>
                    </div>
                    <table align="center">
                        <transition-group tag="tbody" :name="animate ? 'animated-table-body' : 'static-content'">
                            <tr key="header">
                                <th>Player</th>
                                <%--<th>App ID</th>--%>
                                <th>Score</th>
                                <th>Finished</th>
                            </tr>

                            <tr v-for="session in sortedList" :key="'row-' + session.uuid">
                                <td>{{ session.playerName }}</td>
                                <%--<td>{{ session.appId }}</td>--%>
                                <td>{{ session.score }}</td>
                                <td>{{ finishedStatus(session.finishTime) }}</td>
                            </tr>
                        </transition-group>
                    </table>
                </template>
            </div>
        </div>
    </div>
</script>

<script>
    Vue.use(VueGoogleMaps, {
        load: {
            <%
                final Parameter parameter = ParameterFactory.getParameter("GOOGLE_MAPS_KEY");
                final String googleMapsKey = parameter == null ? "undefined" : parameter.getValue();
            %>
            key: '<%=googleMapsKey%>',
            v: '3.30'
        }
    });

    var hydrateCategory = function (data) {
        return {
            name: data.name,
            start: moment(data.validFrom),
            end: moment(data.validUntil)
        };
    };

    new Vue({
        el: '#vue-app',
        template: '#app-template',
        data: {
            sessions: [],
            category: {
                name: '',
                start: null,
                end: null
            },

            // Force Vue to recompute status every 1000 ms (see mounted())
            // intervalId should not be reactive and thus is not defined here
            now: moment(),

            // Rendering control
            animate: false,
            isLoaded: false,

            // Map
            mapOptions: {
                mapTypeControl: false,
                draggable: false,
                scaleControl: false,
                panControl: false,
                scrollwheel: false,
                navigationControl: false,
                zoomControl: false,
                numZoomLevels: 24,
                MAX_ZOOM_LEVEL: 24,
                streetViewControl: false
            },
            infoWindowOptions: {
                disableAutoPan: true,
                pixelOffset: {
                    width: 0,
                    height: -35
                }
            }
        },

        computed: {
            sortedList: function () {
                return this.sessions
                    .concat() // Make a new array, prevent sort in-place
                    .sort(function (a, b) {
                        if (a.score > b.score) {
                            return -1;
                        }

                        if (a.score < b.score) {
                            return 1;
                        }

                        // Try to sort by finish time
                        if (a.finishTime !== 0 && b.finishTime === 0) {
                            return -1;
                        }

                        if (a.finishTime === 0 && b.finishTime !== 0) {
                            return 1;
                        }

                        // Bother are zero or not
                        return a.finishTime < b.finishTime ? -1 : 1;
                    })
                    .slice(0, 10);
            },

            categoryStatus: function () {
                if (this.now.isAfter(this.category.end)) {
                    return 'Ended ' + this.category.end.from(this.now);
                }

                if (this.now.isAfter(this.category.start)) {
                    return 'Ending ' + this.category.end.from(this.now);
                }

                return 'Going live ' + this.category.start.from(this.now);
            },

            mapMarkers: function () {
                return this.sessions
                    .filter(function (session) {
                        return session.lat !== 0 && session.lng !== 0;
                    })
                    .map(function (session) {
                        return {
                            session_id: session.uuid,
                            player_name: session.playerName,
                            score: session.score,
                            position: {
                                lat: session.lat,
                                lng: session.lng
                            }
                        }
                    })
            }
        },

        methods : {
            finishedStatus: function (duration) {
                if (duration === 0) {
                    return 'No';
                }

                duration = moment.duration(duration);
                var returnStr = '';

                returnStr += duration.minutes() + ":";
                returnStr += duration.seconds() + ".";
                returnStr += duration.milliseconds();

                return returnStr;
            }
        },

        //todo eliminate hardcoded values by introducing /admin/parameters API for GMaps, Ably
        created: function () {
            <%
            final Parameter parameter = ParameterFactory.getParameter("ABLY_PUBLIC_KEY");
            if(parameter == null) {
                final Logger log = Logger.getLogger(getClass().getSimpleName());
                log.severe("Could not fine parameter for 'ABLY_PUBLIC_KEY'");
            }
            final String ablyPublicKey = parameter == null ? "undefined" : parameter.getKey();

            %>
            var ably = new Ably.Realtime('<%= ablyPublicKey %>'),
                channel = ably.channels.get('category-<%= request.getParameter("uuid") %>'),
                vm = this;

            fetch('/api/json/scoreBoardWithLocations?categoryUUID=<%= request.getParameter("uuid") %>')
                .then(function (response) {
                    return response.json();
                })
                .catch(function (error) {
                    // Network error
                    console.error('Error:', error);
                })
                .then(function (response) {
                    if (response.status !== 'OK') {
                        // Http or server error
                        console.error('Error:', response);
                        return;
                    }

                    vm.sessions = response.scoreBoard;
                    vm.category = hydrateCategory(response.category);

                    vm.isLoaded = true;

                    Vue.nextTick(function () {
                        // Enable animations only after initial data was flushed to DOM
                        vm.animate = true;
                    })
                });


            channel.subscribe(function (message) {
                if (!vm.isLoaded) {
                    return;
                }

                switch (message.name) {
                    case 'new_session':
                        vm.sessions.push(JSON.parse(message.data));
                        break;

                    case 'session_update':
                        var data = JSON.parse(message.data),
                            index = vm.sessions.findIndex(function (session) {
                                return session.uuid === data.uuid;
                            });

                        if (index >= 0) {
                            vm.sessions.splice(index, 1, data);
                        } else {
                            vm.sessions.push(data);
                        }

                        break;

                    case 'category_update':
                        vm.category = hydrateCategory(JSON.parse(message.data));
                        break;
                }
            });
        },

        mounted: function () {
            var vm = this;

            this.intervalId = setInterval(function () {
                vm.now = moment();
            }, 1000);
        },

        beforeDestroy: function () {
            clearInterval(this.intervalId);
        }
    })
</script>

</body>
</html>