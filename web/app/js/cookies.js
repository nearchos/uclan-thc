/********************************************************************************
	File Name: 
		cookies.js
	Description:
		Contains functions related to setting, changing, deleting cookies and 
		getting their values.
*********************************************************************************/


/**
 * Sets a cookie with a specific name, value and expiration time.
 * If a cookie with the same name already exists, the cookie is replaced.
 * @param cname The name of the cookie.
 * @param cvalue The value of the cookie.
 * @param exdays The expiration time of the cookie.
 */
function setCookie(cname, cvalue, exdays) {
    var d = new Date();
    d.setTime(d.getTime() + (exdays*24*60*60*1000));
    var expires = "expires=" + d.toUTCString();
    document.cookie = cname + "=" + cvalue + "; " + expires;
}

/**
 * Returns a cookie's value or empty string if it does not exist.
 * @param cname The name of the cookie to get.
 * @returns {string}
 */
function getCookie(cname) {
    var name = cname + "=";
    var ca = document.cookie.split(';');
    for(var i = 0; i <ca.length; i++) {
        var c = ca[i];
        while (c.charAt(0) == ' ') c = c.substring(1);
        if (c.indexOf(name) == 0) return c.substring(name.length,c.length);
    }//end for
    return "";
}

/**
 * Returns true if cookie exists, false if it does not.
 * @param cname The name of the cookie to check.
 * @returns {boolean}
 */
function cookieExists(cname) {
    if (getCookie(cname) != "") return true; else return false;
}

/**
 * Deletes a given cookie.
 * @param cname The name of the cookie to delete.
 */
function deleteCookie(cname) {
	var cvalue = "";
	var d = new Date();
    d.setTime(d.getTime() - 1);
    var expires = "expires="+ d.toUTCString();
    document.cookie = cname + "=" + cvalue + "; " + expires;
}

/*---COOKIE NAMES ---*/
const COOKIE_CATEGORY_NAME = "THCWebApp-categoryName";
const COOKIE_PLAYER_NAME = "THCWebApp-playerName";
const COOKIE_SESSION_ID = "THCWebApp-sessionID";
const COOKIE_NUM_OF_QUESTIONS = "THCWebApp-numOfQuestions";