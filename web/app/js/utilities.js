function validateAnswerField() {
	if (document.getElementById('answer').value != "") return true;
	else return false;	
}//end validateAnswerField()

/**********************************************************/

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

/*********************************************************/

function topNavHandler() {
    document.getElementsByClassName("topnav")[0].classList.toggle("responsive");
}//end topNavHandler()

/*********************************************************/
//Example Usage:     
//   <button id="mySnackbar" onClick="createSnackbar('Text');"> Display Snackbar </button>

var createSnackbar = (function() {
  var previous = null;
  return function(message, actionText, action) {
    if (previous) {
      previous.dismiss();
    }
    var snackbar = document.createElement('div');
    snackbar.className = 'paper-snackbar';
    snackbar.dismiss = function() {
      this.style.opacity = 0;
    };
    var text = document.createTextNode(message);
    snackbar.appendChild(text);
    if (actionText) {
      if (!action) {
        action = snackbar.dismiss.bind(snackbar);
      }
      var actionButton = document.createElement('button');
      actionButton.className = 'action';
      actionButton.innerHTML = actionText;
      actionButton.addEventListener('click', action);
      snackbar.appendChild(actionButton);
    }
    setTimeout(function() {
      if (previous === this) {
        previous.dismiss();
      }
    }.bind(snackbar), 1500);
    
    snackbar.addEventListener('transitionend', function(event, elapsed) {
      if (event.propertyName === 'opacity' && this.style.opacity == 0) {
        this.parentElement.removeChild(this);
        if (previous === this) {
          previous = null;
        }
      }
    }.bind(snackbar));

    previous = snackbar;
    document.body.appendChild(snackbar);
    // In order for the animations to trigger, I have to force the original style to be computed, and then change it.
    getComputedStyle(snackbar).bottom;
    snackbar.style.bottom = '0px';
    snackbar.style.opacity = 0.97;
  };
})();

/*********************************************************/

function showModal(name) {
	document.getElementById(name).style.display='block'
}//end showModal()

/*********************************************************/

function hideModal(name) {
	document.getElementById(name).style.display='none'
}//end hideModal()

/*********************************************************/

