<%@ page import="uk.ac.uclan.thc.data.ParameterFactory" %>
<%@ page import="uk.ac.uclan.thc.model.Parameter" %>
<!DOCTYPE html>
<!--
  ~ This file is part of UCLan-THC server.
  ~
  ~     UCLan-THC server is free software: you can redistribute it and/or
  ~     modify it under the terms of the GNU General Public License as
  ~     published by the Free Software Foundation, either version 3 of
  ~     the License, or (at your option) any later version.
  ~
  ~     UCLan-THC server is distributed in the hope that it will be useful,
  ~     but WITHOUT ANY WARRANTY; without even the implied warranty of
  ~     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  ~     GNU General Public License for more details.
  ~
  ~     You should have received a copy of the GNU General Public License
  ~     along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
  -->

<html>
<head>
    <meta name="viewport" content="initial-scale=1.0, user-scalable=no"/>
    <style type="text/css">
        html {
            height: 100%
        }

        body {
            height: 100%;
            margin: 0;
            padding: 0
        }

        #map-canvas {
            position: relative;
            height: 100%;
        }

        #score-board {
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
    </style>
    <%
        final Parameter parameter = ParameterFactory.getParameter("GOOGLE_MAPS_KEY");
        final String googleMapsKey = parameter == null ? "undefined" : parameter.getValue();
    %>
    <script type="text/javascript"
            src="https://maps.googleapis.com/maps/api/js?key=<%=googleMapsKey%>&sensor=false">
    </script>
    <script type="text/javascript">

        function getParam(sname)
        {
            var params = location.search.substr(location.search.indexOf("?")+1);
            var sval = "";
            params = params.split("&");
            // split param and value into individual pieces
            for (var i=0; i<params.length; i++)
            {
                temp = params[i].split("=");
                if ( [temp[0]] == sname ) { sval = temp[1]; }
            }
            return decodeURI(sval);
        }

        var UCLan_Cyprus_position = new google.maps.LatLng(35.008154, 33.6975);
        var center_lat = getParam("lat");
        var center_lng = getParam("lng");
        var center_position;
        if(center_lat == "" || center_lng == "")
            center_position = new google.maps.LatLng(35.008154, 33.6975);
        else
            center_position = new google.maps.LatLng(center_lat, center_lng);

        var map = {};

        function timeInText(duration)
        {
            var SECOND = 1000;
            var MINUTE = 60 * SECOND;
            var HOUR = 60 * MINUTE;
            var DAY = 24 * HOUR;
            var WEEK = 7 * DAY;

            if(duration < SECOND)
            {
                return "just now";
            }
            else if(duration < 2 * MINUTE)
            {
                return Math.floor(duration / SECOND) + " seconds";
            }
            else if(duration < 2 * HOUR)
            {
                return Math.floor(duration / MINUTE) + " minutes";
            }
            else if(duration < 2 * DAY)
            {
                return Math.floor(duration / HOUR) + " hours";
            }
            else if(duration < 2 * WEEK)
            {
                return Math.floor(duration / DAY) + " days";
            }
            else
            {
                return Math.floor(duration / WEEK) + " weeks";
            }
        }

        var overlay;
        USGSOverlay.prototype = new google.maps.OverlayView();

        function initialize() {
            var center_zoom = 19;
            if(getParam("zoom") != "")
                center_zoom = parseInt(getParam("zoom"));
            var mapOptions = {
                center: center_position,
                zoom: center_zoom,
                mapTypeControl: false,
                draggable: false,
                scaleControl: false,
                panControl: false,
                scrollwheel: false,
                navigationControl: false,
                zoomControl: false,
                numZoomLevels: 24,
                MAX_ZOOM_LEVEL: 24,
                streetViewControl: false,
                mapTypeId: google.maps.MapTypeId.ROADMAP
            };
            map = new google.maps.Map(document.getElementById("map-canvas"), mapOptions);

            // synedriako
            var swBound = new google.maps.LatLng(35.154844, 33.378664);
            var neBound = new google.maps.LatLng(35.155401, 33.379468);
            var bounds = new google.maps.LatLngBounds(swBound, neBound);

            // The photograph is courtesy of the U.S. Geological Survey.
            var srcImage = 'http://nearchos.aspectsense.com/Synedriako.png';

            // The custom USGSOverlay object contains the USGS image,
            // the bounds of the image, and a reference to the map.
            overlay = new USGSOverlay(bounds, srcImage, map);
        }
        google.maps.event.addDomListener(window, 'load', initialize);

        // ------- Begin overlay mechanism ------- //

        /** @constructor */
        function USGSOverlay(bounds, image, map) {

            // Initialize all properties.
            this.bounds_ = bounds;
            this.image_ = image;
            this.map_ = map;

            // Define a property to hold the image's div. We'll
            // actually create this div upon receipt of the onAdd()
            // method so we'll leave it null for now.
            this.div_ = null;

            // Explicitly call setMap on this overlay.
            this.setMap(map);
        }

        /**
         * onAdd is called when the map's panes are ready and the overlay has been
         * added to the map.
         */
        USGSOverlay.prototype.onAdd = function() {

            var div = document.createElement('div');
            div.style.borderStyle = 'none';
            div.style.borderWidth = '0px';
            div.style.position = 'absolute';

            // Create the img element and attach it to the div.
            var img = document.createElement('img');
            img.src = this.image_;
            img.style.width = '100%';
            img.style.height = '100%';
            img.style.position = 'absolute';
            div.appendChild(img);

            this.div_ = div;

            // Add the element to the "overlayLayer" pane.
            var panes = this.getPanes();
            panes.overlayLayer.appendChild(div);
        };

        USGSOverlay.prototype.draw = function() {

            // We use the south-west and north-east
            // coordinates of the overlay to peg it to the correct position and size.
            // To do this, we need to retrieve the projection from the overlay.
            var overlayProjection = this.getProjection();

            // Retrieve the south-west and north-east coordinates of this overlay
            // in LatLngs and convert them to pixel coordinates.
            // We'll use these coordinates to resize the div.
            var sw = overlayProjection.fromLatLngToDivPixel(this.bounds_.getSouthWest());
            var ne = overlayProjection.fromLatLngToDivPixel(this.bounds_.getNorthEast());

            // Resize the image's div to fit the indicated dimensions.
            var div = this.div_;
            div.style.left = sw.x + 'px';
            div.style.top = ne.y + 'px';
            div.style.width = (ne.x - sw.x) + 'px';
            div.style.height = (sw.y - ne.y) + 'px';
        };

        // The onRemove() method will be called automatically from the API if
        // we ever set the overlay's map property to 'null'.
        USGSOverlay.prototype.onRemove = function() {
            this.div_.parentNode.removeChild(this.div_);
            this.div_ = null;
        };
        // ------- End of overlay mechanism ------- //

        //var uuid = "agtzfnVjbGFuLXRoY3IVCxIIQ2F0ZWdvcnkYgICAgIDcsgsM";
        var uuid = getParam("uuid");
        var url = "https://uclan-thc.appspot.com/api/json/scoreBoardWithLocations?sorted&categoryUUID=" + uuid;
        var month_names = new Array("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec");

        var markers = {};

        function update() {
            var client = new XMLHttpRequest();
            client.open("GET", url, true);
            client.setRequestHeader("Content-Type", "text/plain");
            client.onreadystatechange = function () {
                if (client.readyState == 4 && client.status == 200) {
                    var d = new Date();
                    var a_p = "";
                    var curr_date = d.getDate();
                    var curr_month = d.getMonth();
                    var curr_year = d.getFullYear();
                    var curr_hour = d.getHours();
                    if (curr_hour < 12) { a_p = "AM"; } else { a_p = "PM"; }
                    if (curr_hour == 0) { curr_hour = 12; }
                    if (curr_hour > 12) { curr_hour = curr_hour - 12; }
                    var curr_min = d.getMinutes();
                    curr_min = curr_min + "";
                    if (curr_min.length == 1) { curr_min = "0" + curr_min; }
                    var curr_sec = d.getSeconds();
                    curr_sec = curr_sec + "";
                    if(curr_sec.length == 1) { curr_sec = "0" + curr_sec; }

                    var now = curr_date + "-" + month_names[curr_month] + "-" + curr_year + " at " + curr_hour + ":" + curr_min + ":" + curr_sec + " " + a_p;

                    var rootObject = JSON.parse(client.responseText);
                    var s = "<p style='color: gray; font-style: italic;'>Last updated: " + now + "</p>" +
                            "<table style='border: 1px solid gray; font-size: 20px;'>" +
                            "<tr style='font-weight: bolder'><th style='width: 80px;'>Player</th><th style='width: 80px;'>App ID</th><th style='width: 80px;'>Score</th><th style='width: 80px;'>Finished</th></tr>"
                    var counter = 1;
                    var categoryName = rootObject.categoryName;
                    if(categoryName.length > 0) category_title.innerHTML = categoryName;
                    var validFrom = rootObject.validFrom;
                    var validUntil = rootObject.validUntil;
                    var now = Date.now();
                    if(now < validFrom)
                    {
                        time_and_date.innerHTML = "Going live in " + timeInText(validFrom-now);
                    }
                    else if(now < validUntil)
                    {
                        time_and_date.innerHTML = "Ending in " + timeInText(validUntil-now);
                    }
                    else // now >= validUntil
                    {
                        time_and_date.innerHTML = "Ended " + timeInText(now-validUntil) + " ago";
                    }
                    for(var p in rootObject.scoreBoard) {
                        var finishTime = rootObject.scoreBoard[p].finishTime;
                        var fts;
                        var milliseconds = finishTime % 1000;
                        var millisecondsS = milliseconds >= 100 ? milliseconds : milliseconds >= 10 ? "0" + milliseconds : "00" + milliseconds;
                        var seconds = Math.floor(finishTime / 1000);
                        var secondsS = (seconds % 60) < 10 ? "0" + (seconds % 60) : seconds % 60;
                        var minutes = Math.floor(seconds / 60);
                        var minutesS = (minutes % 60) < 10 ? "0" + (minutes % 60) : minutes % 60;
                        var hours = Math.floor(minutes / 60);
                        var hoursS = hours % 60;

                        fts = finishTime == 0 ? "unfinished"
                                : hoursS + ":" + minutesS + ":" + secondsS + "." + millisecondsS;

                        var pna = rootObject.scoreBoard[p].playerName;
                        var aid = rootObject.scoreBoard[p].appID;
                        var sco = rootObject.scoreBoard[p].score;
                        s = s + "<tr><td>" + pna + "</td><td>" + aid + "</td><td>" + sco + "</td><td>" + fts + "</td></tr>";

                        var lat = rootObject.scoreBoard[p].lat;
                        var lng = rootObject.scoreBoard[p].lng;

                        if(lat != 0 && lng != 0) { // ignorer zero values
                            var key = pna+"$"+aid;
                            if(markers[key] != null)
                            {
                                markers[key].setPosition(new google.maps.LatLng(lat, lng));
                            }
                            else
                            {
                                markers[key] = new google.maps.InfoWindow({
                                    position: new google.maps.LatLng(lat, lng),
                                    content: "<h3>" + pna + "</h3><p style='color: #CE0E41;'>" + sco + " pts</p>",
                                    map: map,
                                    disableAutoPan : true
                                });
                            }
                        }
                    }
                    s = s + "</table>";
                    teams.innerHTML = s;
                    map.setCenter(center_position);
                }
            };

            client.send(null);
            setTimeout(update, 15000); // repeat update after 15 seconds
        }
        update();

    </script>
    <title>Live map - UClan Treasure Hunt Challenge</title>
</head>
<body>
    <div id="map-canvas"></div>
    <div id="score-board">
        <img src="uclan_small.png" title="UCLan Cyprus" alt="UCLan Cyprus"/>
        <h1 style="font-family: sans-serif; color: #CE0E41; font-weight: bold; font-size: 28px;" id="category_title">Treasure Hunt Challenge</h1>
        <h1 style="font-family: sans-serif; font-weight: bolder; font-size: 20px;">Live score board</h1>
        <h2 style="font-family: sans-serif; font-weight: bolder; font-size: 16px; color: #f00;" id="time_and_date"></h2>
        <div id="teams" style="padding: 10px;">
            <table align="center" style='border: 1px solid gray; font-size: 20px; padding: 10px'>
                <tr style='font-weight: bolder'><th style='width: 80px;'>Player</th><th style='width: 80px;'>App ID</th><th style='width: 80px;'>Score</th><th style='width: 80px;'>Finished</th></tr>
            </table>
        </div>
    </div>

    <script type="text/javascript">
        document.getElementById("category_title").innerHTML=getParam("category_title");
    </script>

</body>
</html>