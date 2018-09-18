/********************************************************************************
	File Name: 
		utilities.js
	Description:
		Contains various utility functions used to correctly run the application.
*********************************************************************************/

//API Page URLs:
const API_BASE_URL = "https://codecyprus.org/th/api/";
const API_SELECT_CATEGORY = API_BASE_URL + "list";
const API_START = API_BASE_URL + "start";
const API_QUESTION = API_BASE_URL + "question";
const API_ANSWER = API_BASE_URL + "answer";
const API_SKIP = API_BASE_URL + "skip";
const API_SCORE = API_BASE_URL + "score";
const API_LEADERBOARD = API_BASE_URL + "leaderboard";
const API_LOCATION = API_BASE_URL + "location";

//Question Types:
const QUESTION_BOOLEAN = "BOOLEAN";
const QUESTION_INTEGER = "INTEGER";
const QUESTION_NUMERIC = "NUMERIC";
const QUESTION_MCQ = "MCQ";
const QUESTION_TEXT = "TEXT";

/**
 * Checks if the text answer field is empty. Returns false if empty, true if filled.
 * @returns {boolean}
 */
function validateAnswerField() {
	return document.getElementById('answer').value != "";
}

/**
 * Retrieves a GET variable from the current URL.
 * @param varName
 * @returns {*} string?
 */
function fetchGetVariable(varName) {
	var $_GET = {};
	if(document.location.toString().indexOf('?') !== -1) {
    	var query = document.location
                   .toString()
                   .replace(/^.*?\?/, '')
                   .replace(/#.*$/, '')
                   .split('&');

	    for(var i=0, l=query.length; i<l; i++) {
    	   var aux = decodeURIComponent(query[i]).split('=');
       		$_GET[aux[0]] = aux[1];
    	}//end for
	}//end if
	return ($_GET[varName]);
}

/**
 * Creates a snackbar with a given message, duration and action and displays it.
 * Example Usage:
 * <button id="mySnackbar" onClick="createSnackbar('Text');"> Display Snackbar </button>
 */
var createSnackbar = (function() {
  var previous = null;
  return function(message, time, actionText, action) {
    if (previous) previous.dismiss();
	if (typeof time == 'undefined') time = 1500;
    var snackbar = document.createElement('div');
    snackbar.className = 'paper-snackbar';
    snackbar.dismiss = function() {
      this.style.opacity = 0;
    };//end dismiss()
    var text = document.createTextNode(message);
    snackbar.appendChild(text);
	if (actionText) {
      if (!action) action = snackbar.dismiss.bind(snackbar);
      var actionButton = document.createElement('button');
      actionButton.className = 'action';
      actionButton.innerHTML = actionText;
      actionButton.addEventListener('click', action);
      snackbar.appendChild(actionButton);
    }//end if actionText
    setTimeout(function() {
      if (previous === this) previous.dismiss();
    }.bind(snackbar), time);//end setTimeout()
    
    snackbar.addEventListener('transitionend', function(event, elapsed) {
      if (event.propertyName === 'opacity' && this.style.opacity == 0) {
        this.parentElement.removeChild(this);
        if (previous === this) previous = null;
      }//end if
    }.bind(snackbar));//end addEventListener

    previous = snackbar;
    document.body.appendChild(snackbar);
    getComputedStyle(snackbar).bottom;
    snackbar.style.bottom = '0px';
    snackbar.style.opacity = 0.97;
  };//end function
})();//end createSnackbar()

/**
 * Displays a modal with a given ID.
 * @param name
 */
function showModal(name) {
	document.getElementById(name).style.display='block'
}

/**
 * Hides a modal with a given ID.
 * @param name
 */
function hideModal(name) {
	document.getElementById(name).style.display='none'
}

/**
 * Converts a UNIX timestamp to a time in HH:MM:SS:ms. Returns string.
 * @param finishTime
 * @returns {string|*}
 */
function timestampToTime(finishTime) {
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
	
	return fts;
}

/**
 * Returns a suffix according to a number (e.g "1st" if 1 or "2nd" if 2.)
 * @param i
 * @returns {string}
 */
function getSuffix(i) {
    var j = i % 10, k = i % 100;
    if (j == 1 && k != 11) return i + "st";
    if (j == 2 && k != 12) return i + "nd";
    if (j == 3 && k != 13) return i + "rd";
    return i + "th";	
}

/**
 * Detects if a device is an Android and displays a Google Play badge on index page
 */
function isAndroid() {
	var ua = navigator.userAgent.toLowerCase();
	var isAndroid = ua.indexOf("android") > -1;
	if(isAndroid) document.getElementById("gappsBadge").style.display = "block";
}