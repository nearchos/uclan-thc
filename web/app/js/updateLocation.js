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
function updateLocation() {
	if (navigator.geolocation) navigator.geolocation.getCurrentPosition(getLocationValues, showError);
	else alert("Geolocation is not supported by this browser.");
}//end updateLocation()
/************************************************************************************/

/********************************************************************************/
//Creates a snackbar to indicate an error in updating the location.
function showError(error) {
	switch(error.code) 	{
		case error.PERMISSION_DENIED:
			createSnackbar("User denied the request for Geolocation. Please make sure you ALLOW Geolocation in your browser.", 5000);
			break;

		case error.POSITION_UNAVAILABLE:
			createSnackbar("Location information is unavailable.", 2500);
			break;

		case error.TIMEOUT:
			createSnackbar("The request to get user location timed out.", 2500);
			break;

		case error.UNKNOWN_ERROR:
			createSnackbar("An unknown error occurred.", 2500);
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
			if (jsonData.status == "OK")  {/*do nothing*/}
			else alert(jsonData.status + " - " + jsonData.message);
		}//end if ready
  	};
  	xhttp.open("GET", "https://uclan-thc.appspot.com/api/json/secure/updateLocation?lat=" + lat + "&lng=" + lon + "&session=" + sessionID, true);
  	xhttp.send();
}//end setLocation()
/********************************************************************************/