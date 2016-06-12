/***************************************************************************/

function setCookie(cname, cvalue, exdays) {
    var d = new Date();
    d.setTime(d.getTime() + (exdays*24*60*60*1000));
    var expires = "expires="+ d.toUTCString();
    document.cookie = cname + "=" + cvalue + "; " + expires;
}//end setCookie()

/***************************************************************************/
//returns string: cookie value
function getCookie(cname) {
    var name = cname + "=";
    var ca = document.cookie.split(';');
    for(var i = 0; i <ca.length; i++) {
        var c = ca[i];
        while (c.charAt(0)==' ') c = c.substring(1);
        if (c.indexOf(name) == 0) return c.substring(name.length,c.length);
    }//end for
    return "";
}//end getCookie()

/***************************************************************************/
//returns boolean: cookie exists
function cookieExists(cname) {
    var cookie = getCookie(cname);
    if (cookie != "") return true; else return false;
}//end checkCookie()

/***************************************************************************/

function deleteCookie(cname) {
	var cvalue = "";
	var expires = "-5";
	var d = new Date();
    d.setTime(d.getTime() -1);
    var expires = "expires="+ d.toUTCString();
    document.cookie = cname + "=" + cvalue + "; " + expires;
}//end deleteCookie()

/***************************************************************************/