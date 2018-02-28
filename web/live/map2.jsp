<%@ page import="uk.ac.uclan.thc.model.Parameter" %>
<%@ page import="uk.ac.uclan.thc.data.ParameterFactory" %>
<%@ page import="java.util.logging.Logger" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta name="viewport" content="initial-scale=1.0, user-scalable=no"/>
    <title>Treasure hunt challenge map</title>

    <script src="https://cdn.jsdelivr.net/npm/vue@2.5.13/dist/vue.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/moment.js/2.20.1/moment.min.js"></script>
    <script src="https://cdn.ably.io/lib/ably.min-1.js"></script>

    <script src="https://cdnjs.cloudflare.com/ajax/libs/lodash.js/4.17.5/lodash.min.js"></script>
    <script src="/live/vue-google-maps.js"></script>

    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bulma/0.6.2/css/bulma.min.css">

    <style>
        /* Copied */

        html, body {
            height: 100%;
            overflow-y: hidden;
        }

        /* Custom/modified */

        .map-parent {
            height: 100%;
            width: 100%;
        }

        #scoreboard {
            position: absolute;
            width: 480px;
            top: 20px;
            bottom: 20px;
            right: 20px;
            background-color: white;
            text-align: center;
            border: 2px solid #bbb;
            border-radius: 10px;
            overflow-y: hidden;
        }

        #teams {
            padding: 10px;
        }

        #teams table {
            font-size: 20px;
            margin-bottom: 0;
        }

        #teams .player-icon {
            width: 25px;
            padding-bottom: 0;
            padding-right: 0;
            padding-left: 5px;
        }

        #uclan-logo {
            width: 192px;
            height: 180px;
            position: absolute;
            top: 20px;
            left: 20px;
            z-index: 10;

            border: 2px solid #bbb;
            border-radius: 10px;

            padding: 5px;
            background: white;
        }

        #header-wrap {
            top: 20px;
            left: 50%;
            position: absolute;

            z-index: 10;
        }
        #header {
            position: relative;
            left: -50%;

            width: 500px;
            padding: 7px;
            text-align: center;

            border: 2px solid #bbb;
            border-radius: 10px;
            background: white;
        }
        #category-title {
            color: #CE0E41;
            font-size: 40px;
        }
        #category-status {
            font-family: sans-serif;
            font-size: 22px;
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
        <img id="uclan-logo" src="/live/uclan_small.png" alt="UCLan Cyprus logo" />

        <template v-if="isLoaded">
            <div id="header-wrap">
                <div id="header">
                    <h1 id="category-title">{{ category.name }}</h1>
                    <category-status :category="category"></category-status>
                </div>
            </div>
        </template>

        <gmap-map style="width: 100%; height: 100%;"
            :center="{lat: 35.008154, lng: 33.6975}"
            :zoom="19"
            :options="mapOptions"
            mapTypeId="satellite"
        >
            <gmap-marker :key="'marker-' + marker.session_id" v-for="marker in mapMarkers"
                :position="marker.position" :clickable="false" :icon="marker.icon" />
        </gmap-map>

        <div id="scoreboard">
            <div id="teams">
                <template v-if="!isLoaded">
                    Loading...
                </template>
                <template v-else>
                    <table align="center" class="table is-narrow is-fullwidth">
                        <thead>
                            <tr>
                                <th class="player-icon"></th>
                                <th>Player</th>
                                <!--<th>App ID</th>-->
                                <th style="width: 72px; text-align: right">Score</th>
                                <th style="width: 125px; text-align: right">Finish time</th>
                            </tr>
                        </thead>

                        <transition-group tag="tbody" :name="animate ? 'animated-table-body' : 'static-content'">
                            <tr v-for="session in sortedList" :key="'row-' + session.uuid">
                                <td class="player-icon"><img :src="mapIconUrl(session)" /></td>
                                <td>{{ session.playerName | truncate(23) }}</td>
                                <!--<td>{{ session.appId }}</td>-->
                                <td style="width: 72px; text-align: right">{{ session.score }}</td>
                                <td style="width: 125px; text-align: right">{{ finishedStatus(session.finishTime) }}</td>
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
            key: '<%=ParameterFactory.getParameterValueWithDefault("GOOGLE_MAPS_KEY", "undefined")%>',
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

    var padWithZeroes = function (number, minLength) {
        number = number.toString();
        var length = number.length;

        if (length >= minLength) {
            return number;
        }

        return Array(minLength - length + 1).join('0') + number;
    };

    var formatDuration = function (moment) {
        var returnStr = '';

        returnStr += padWithZeroes(moment.minutes(), 2) + ":";
        returnStr += padWithZeroes(moment.seconds(), 2);

        return returnStr;
    };

    Vue.component('category-status', {
        props: {
            category: {
                type: Object,
                required: true
            }
        },
        render: function (createElement) {
            return createElement(
                'div', {attrs: {id: 'category-status'}}, 'Category status'
            )
        },
        mounted: function () {
            var vm = this;

            this.intervalId = setInterval(function () {
                var now = moment(),
                    result = '';

                if (now.isAfter(vm.category.end)) {
                    result = 'Ended ' + vm.category.end.from(now);
                } else if (now.isAfter(vm.category.start)) {
                    var diff = vm.category.end.diff(now);

                    if (diff < 300000) { // 5 minutes
                        result = 'Ending in ' + formatDuration(moment.duration(diff));
                    } else {
                        result = 'Ending ' + vm.category.end.from(now);
                    }
                } else {
                    var diff = vm.category.start.diff(now);

                    if (diff < 300000) { // 5 minutes
                        result = 'Going live in ' + formatDuration(moment.duration(diff));
                    } else {
                        result = 'Going live ' + vm.category.start.from(now);
                    }
                }

                vm.$el.innerHTML = result;
            }, 1);
        },

        beforeDestroy: function () {
            clearInterval(this.intervalId);
        }
    });

    // Based on https://github.com/imcvampire/vue-truncate-filter/blob/master/vue-truncate.js
    Vue.filter('truncate', function (text, length, clamp) {
        clamp = clamp || '...';
        length = length || 30;

        if (text.length <= length) return text;

        var tcText = text.slice(0, length - clamp.length),
            last = tcText.length - 1,
            rewind = 0;

        while (last > 0 && tcText[last] !== ' ' && tcText[last] !== clamp[0]) {
            last--;
            rewind++;
        }

        if (last === 0) {
            // Fix for case when text don't have any `space`
            last = length - clamp.length;
        } else if (rewind > 2) {
            // Truncate in middle of word if we can keep 2 characters
            last += rewind;
        }

        return tcText.slice(0, last) + clamp;
    });

    var mapLabels = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ',
        mapColours = ['red', 'yellow', 'green', 'orange'],
        nextMapLabelIndex = 0,
        nextMapColourIndex = 0,
        mapIconsMap = {}; // naming is hard....

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

                        // Both are zero or not
                        return a.finishTime < b.finishTime ? -1 : 1;
                    })
                    .slice(0, 21);
            },

            mapMarkers: function () {
                var vm = this;

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
                            },
                            icon: {
                                url: vm.mapIconUrl(session)
                            }
                        }
                    })
            }
        },

        methods : {
            finishedStatus: function (duration) {
                if (duration === 0) {
                    return 'Not yet';
                }

                duration = moment.duration(duration);
                var returnStr = '';

                returnStr += padWithZeroes(duration.minutes(), 2) + ":";
                returnStr += padWithZeroes(duration.seconds(), 2) + ".";
                returnStr += padWithZeroes(duration.milliseconds(), 3);

                return returnStr;
            },

            mapIconUrl: function (session) {
                if (!mapIconsMap.hasOwnProperty(session.uuid)) {
                    mapIconsMap[session.uuid] = {
                        label: mapLabels[nextMapLabelIndex],
                        colour: mapColours[nextMapColourIndex]
                    };

                    nextMapLabelIndex++;

                    if (nextMapLabelIndex >= mapLabels.length) {
                        nextMapLabelIndex = 0;
                        nextMapColourIndex++;
                    }
                }

                var icon = mapIconsMap[session.uuid];

                return '/live/map-markers/' + icon.colour + '_Marker' + icon.label + '.png';
            }
        },

        created: function () {
            var ably = new Ably.Realtime('<%= ParameterFactory.getParameterValueWithDefault("ABLY_PUBLIC_KEY", "undefined") %>'),
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
        }
    })
</script>

</body>
</html>