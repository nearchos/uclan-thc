/********************************************************************************
 ********************************************************************************
	File Name: 
		cookies.js
	Created by: 
		Nicos Kasenides (nkasenides@uclan.ac.uk / hfnovember@hotmail.com) 
		For InSPIRE - UCLan Cyprus
		June 2016
	Description:
		Contains functions related to setting, changing, deleting cookies and 
		getting their values.
*********************************************************************************		
*********************************************************************************/




/********************************************************************************/
//Sets a cookie with a specific name, value and expiration time.
//If a cookie with the same name already exists, the cookie is replaced.
function setCookie(cname, cvalue, exdays) {
    var d = new Date();
    d.setTime(d.getTime() + (exdays*24*60*60*1000));
    var expires = "expires=" + d.toUTCString();
    document.cookie = cname + "=" + cvalue + "; " + expires;
}//end setCookie()
/********************************************************************************/

/********************************************************************************/
//Returns a cookie's value or empty string if it does not exist.
function getCookie(cname) {
    var name = cname + "=";
    var ca = document.cookie.split(';');
    for(var i = 0; i <ca.length; i++) {
        var c = ca[i];
        while (c.charAt(0) == ' ') c = c.substring(1);
        if (c.indexOf(name) == 0) return c.substring(name.length,c.length);
    }//end for
    return "";
}//end getCookie()
/********************************************************************************/

/********************************************************************************/
//Returns true if cookie exists, false if it does not.
function cookieExists(cname) {
    if (getCookie(cname) != "") return true; else return false;
}//end cookieExists()
/********************************************************************************/

/********************************************************************************/
//Deletes a given cookie.
function deleteCookie(cname) {
	var cvalue = "";
	var expires = "-5";
	var d = new Date();
    d.setTime(d.getTime() -1);
    var expires = "expires="+ d.toUTCString();
    document.cookie = cname + "=" + cvalue + "; " + expires;
}//end deleteCookie()
/********************************************************************************/