/********************************************************************************
 ********************************************************************************
	File Name: 
		updateLocation.js
	Created by: 
		Nicos Kasenides (nkasenides@uclan.ac.uk / hfnovember@hotmail.com) 
		For InSPIRE - UCLan Cyprus
		June 2016
	Description:
		Contains functions used to update the user's location.
*********************************************************************************		
*********************************************************************************/




/********************************************************************************/
var GLOBAL_LAT; //Stores the updated latitude
var GLOBAL_LON; //Stores the updated longitude
var GLOBAL_LocationInitialized = false; //Determines if location was initialized manual at the start.
//NOTE: This variable is used to avoid a blank auto-update at page load.

/********************************************************************************/
//Updates the stored location locally every 10 seconds.
function client_updateLocation_Auto() {
	if (navigator.geolocation) navigator.geolocation.getCurrentPosition(client_updateGlobals, showError);
	else alert("Geolocation is not supported by this browser.");
	setTimeout(client_updateLocation_Auto, 10000);
}//end local_updateLocation()
/********************************************************************************/

/********************************************************************************/
//Receives location from client_updateLocation_Auto() and updates globals.
function client_updateGlobals(position) {
	GLOBAL_LAT = position.coords.latitude;
	GLOBAL_LON = position.coords.longitude;	
}//end client_updateGlobals()
/********************************************************************************/

/********************************************************************************/
//Makes an AUTOMATIC server request to set the user's location to the current globals.
//Interval: 60 seconds.
function server_updateLocation_Auto() {
	if (GLOBAL_LocationInitialized) {
		var xhttp = new XMLHttpRequest();
		xhttp.onreadystatechange = function() {
		if (xhttp.readyState == 4 && xhttp.status == 200) {/*DO NOTHING*/}
		};
	xhttp.open("GET", "https://uclan-thc.appspot.com/api/json/secure/updateLocation?lat=" + GLOBAL_LAT + "&lng=" + GLOBAL_LON + "&session=" + sessionID, true);
		xhttp.send();
	}//end if
	setTimeout(server_updateLocation_Auto, 60000);	
}//end server_updateLocation_Auto()
/****************************************************************************/

/****************************************************************************/
//Updates Location on user answer.
function updateLocation() {
	if (navigator.geolocation) navigator.geolocation.getCurrentPosition(server_updateLocation_Manual, showError);
	else alert("Geolocation is not supported by this browser.");
}//end updateLocation()
/****************************************************************************/

/****************************************************************************/
//Makes a MANUAL server request on user answer to update the user's location.
function server_updateLocation_Manual(position) {
	 var lat = position.coords.latitude;
	 var lon = position.coords.longitude;
	 
	 var xhttp = new XMLHttpRequest();
  	xhttp.onreadystatechange = function() {
    if (xhttp.readyState == 4 && xhttp.status == 200) {/*do nothing*/}
  	};
xhttp.open("GET", "https://uclan-thc.appspot.com/api/json/secure/updateLocation?lat=" + lat + "&lng=" + lon + "&session=" + sessionID, true);
  	xhttp.send();
}//end server_updateLocation_Manual()
/****************************************************************************/

/****************************************************************************/
//Creates an alert to indicate an error in updating the location.
function showError(error) {
	switch(error.code) 	{
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
}//end showError()
/****************************************************************************/
