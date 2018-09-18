/********************************************************************************
	File Name: 
		updateLocation.js
	Description:
		Contains functions used to update the user's location.
*********************************************************************************/

var GLOBAL_LAT; //Stores the updated latitude
var GLOBAL_LON; //Stores the updated longitude
var GLOBAL_LocationInitialized = false; //Determines if location was initialized manually at the start.
//NOTE: This variable is used to avoid a blank auto-update at page load.

/**
 * Updates the stored location locally every 10 seconds.
 */
function client_updateLocation_Auto() {
	if (navigator.geolocation) navigator.geolocation.getCurrentPosition(client_updateGlobals, showError);
	else alert("Geolocation is not supported by this browser.");
	setTimeout(client_updateLocation_Auto, 10000);
}

/**
 * Receives location from client_updateLocation_Auto() and updates globals.
 * @param position
 */
function client_updateGlobals(position) {
	GLOBAL_LAT = position.coords.latitude;
	GLOBAL_LON = position.coords.longitude;	
}


/**
 * Makes an AUTOMATIC server request to set the user's location to the current globals.
 * Interval: 60 seconds.
 */
function server_updateLocation_Auto() {
	if (GLOBAL_LocationInitialized) {
		var xhttp = new XMLHttpRequest();
		xhttp.onreadystatechange = function() {
		if (xhttp.readyState == 4 && xhttp.status == 200) {/*DO NOTHING*/}
		};
	xhttp.open("GET", API_LOCATION + "?latitude=" + GLOBAL_LAT + "&longitude=" + GLOBAL_LON + "&session=" + sessionID, true);
		xhttp.send();
	}//end if
	setTimeout(server_updateLocation_Auto, 60000);	
}

/**
 * Updates Location on user answer.
 */
function updateLocation() {
	if (navigator.geolocation) navigator.geolocation.getCurrentPosition(server_updateLocation_Manual, showError);
	else alert("Geolocation is not supported by this browser.");
}

/**
 * Makes a MANUAL server request on user answer to update the user's location.
 * @param position
 */
function server_updateLocation_Manual(position) {
	 var lat = position.coords.latitude;
	 var lon = position.coords.longitude;
	 
	 var xhttp = new XMLHttpRequest();
  	xhttp.onreadystatechange = function() {
    if (xhttp.readyState == 4 && xhttp.status == 200) {/*do nothing*/}
  	};
xhttp.open("GET", API_LOCATION + "?latitude=" + lat + "&longitude=" + lon + "&session=" + sessionID, true);
  	xhttp.send();
}

/**
 * Creates an alert to indicate an error in updating the location.
 * @param error
 */
function showError(error) {
    switch (error.code) {
        case error.PERMISSION_DENIED:
            alert("User denied the request for Geolocation. Please make sure you ALLOW Geolocation in your browser.");
            break;

        case error.POSITION_UNAVAILABLE:
            alert("Location information is unavailable.");
            break;

        case error.TIMEOUT:
            alert("The request to get user location timed out.");
            break;

        case error.UNKNOWN_ERROR:
            alert("An unknown error occurred.");
            break;
    }//end switch
}
