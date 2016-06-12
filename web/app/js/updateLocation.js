/************************************************************************************/

function updateLocation() {
	if (navigator.geolocation) navigator.geolocation.getCurrentPosition(getLocationValues, showError);
	else alert("Geolocation is not supported by this browser.");
}//end updateLocation()

/************************************************************************************/

function showError(error) {
	switch(error.code) 	{
		case error.PERMISSION_DENIED:
			alert("User denied the request for Geolocation.");
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

/************************************************************************************/

 function getLocationValues(position) {
	 var lat = position.coords.latitude;
	 var lon = position.coords.longitude;
	 setLocation(lat, lon);
 }//end getLocationValues()
 
/************************************************************************************/

function setLocation(lat, lon) {
	//lat = 35.00831985473633;-------DEBUG ONLY
	//lon = 33.69698715209961;-------DEBUG ONLY
	
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