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
//Calls getLocationValues() if user has Geolocation or displays a warning message using showError().
//Updates Location automatically every 10 seconds.
function updateLocation() {
	if (navigator.geolocation) navigator.geolocation.getCurrentPosition(getLocationValues, showError);
	else alert("Geolocation is not supported by this browser.");
	setTimeout(updateLocation, 10000);
}//end updateLocation()
/************************************************************************************/

/********************************************************************************/
//Creates a snackbar to indicate an error in updating the location.
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
/********************************************************************************/

/********************************************************************************/
//Gets latitude and longitude from a given location.
 function getLocationValues(position) {
	 var lat = position.coords.latitude;
	 var lon = position.coords.longitude;
	 setLocation(lat, lon);
 }//end getLocationValues()
/********************************************************************************/

/********************************************************************************/
//Makes a server request to set the user's location to the given latitude and longitude.
function setLocation(lat, lon) {
	var xhttp = new XMLHttpRequest();
  	xhttp.onreadystatechange = function() {
		if (xhttp.readyState == 4 && xhttp.status == 200) {
			var jsonData = JSON.parse(xhttp.responseText);
			if (jsonData.status == "OK")  {/*Do nothing*/}
			else alert(jsonData.status + " - " + jsonData.message);
		}//end if ready
  	};
  	xhttp.open("GET", "https://uclan-thc.appspot.com/api/json/secure/updateLocation?lat=" + lat + "&lng=" + lon + "&session=" + sessionID, true);
  	xhttp.send();
}//end setLocation()
/********************************************************************************/