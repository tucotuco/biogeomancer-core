// imageSwap.js
/*
*  JavaScript template file for handling image swaps for onMouseOver onMouseOut events
*/
var preloadFlag = false;

function newImage(arg) {
	if (document.images) {
		rslt = new Image();
		rslt.src = arg;
		return rslt;
	}
}

function changeImages() {
	if (document.images && (preloadFlag == true)) {
		for (var i=0; i<changeImages.arguments.length; i+=2) {
		    if (changeImages.arguments[i+1] != null) {
			  document[changeImages.arguments[i]].src = changeImages.arguments[i+1].src;
			}
		}
	}
}


function preloadImages() {
	if (document.images) {
		aboutusover = newImage("images/aboutusover.gif");
		aboutus = newImage("images/aboutus.gif");
		visionover = newImage("images/visionover.gif");
		vision = newImage("images/vision.gif");
		resourcesover = newImage("images/resourcesover.gif");
		resources = newImage("images/resources.gif");
		servicesover = newImage("images/servicesover.gif");
		services = newImage("images/services.gif");
		homeover = newImage("images/homeover.gif");
		home = newImage("images/home.gif");
		contactusover = newImage("images/contactusover.gif");
		contactus = newImage("images/contactus.gif");
		preloadFlag = true;
	}
}
