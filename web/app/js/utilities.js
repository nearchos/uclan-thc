/********************************************************************************
 ********************************************************************************
	File Name: 
		utilities.js
	Created by: 
		Nicos Kasenides (nkasenides@uclan.ac.uk / hfnovember@hotmail.com) 
		For InSPIRE - UCLan Cyprus
		June 2016
	Description:
		Contains various utility functions used to correctly run the application.
*********************************************************************************		
*********************************************************************************/




/********************************************************************************/
//Checks if the text answer field is empty. Returns false if empty, true if filled.
function validateAnswerField() {
	if (document.getElementById('answer').value != "") return true;
	else return false;	
}//end validateAnswerField()
/********************************************************************************/

/********************************************************************************/
//Retrieves a GET variable from the current URL.
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
}//end fetchGetVariable()
/********************************************************************************/

/********************************************************************************/
//Creates a snackbar with a given message, duration and action and displays it.
//Example Usage:     
//   <button id="mySnackbar" onClick="createSnackbar('Text');"> Display Snackbar </button>
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

/********************************************************************************/

/********************************************************************************/
//Displays a modal with a given ID.
function showModal(name) {
	document.getElementById(name).style.display='block'
}//end showModal()
/********************************************************************************/

/********************************************************************************/
//Hides a modal with a given ID.
function hideModal(name) {
	document.getElementById(name).style.display='none'
}//end hideModal()
/********************************************************************************/

/********************************************************************************/
//Converts a UNIX timestamp to a time in HH:MM:SS:ms. Returns string.
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
}//end timestampToTime()
/********************************************************************************/

/********************************************************************************/
//Returns a suffix according to a number (e.g "1st" if 1 or "2nd" if 2.)
function getSuffix(i) {
    var j = i % 10, k = i % 100;
    if (j == 1 && k != 11) return i + "st";
    if (j == 2 && k != 12) return i + "nd";
    if (j == 3 && k != 13) return i + "rd";
    return i + "th";	
}//end getSuffix()
/********************************************************************************/