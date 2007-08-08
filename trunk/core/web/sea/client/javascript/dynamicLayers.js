// dynamicLayers.js

// setup test for Nav 4.0
var isIE = false;
var isNav = (navigator.appName.indexOf("Netscape")>=0);
var isNav4 = false;
var isIE4 = false;
var is5up = false;
var browserDetectPopup = false;

if (isNav) {
	if (parseFloat(navigator.appVersion)<5) {
		isNav4=true;
  	} else {
	is5up = true;
  }
} else {
  isIE4=true;
  isIE=true;
  if (navigator.appVersion.indexOf("MSIE 5")>0) {
 	isIE4 = false;
	is5up = true;
  }
}

// Browser alert
if (browserDetectPopup) {
  if (isNav)
	alert("Nav");
  if (isNav4)
	alert("Nav4");
  if (is5up)
	alert("Nav6 or IE");
  if (isIE4)
	alert("IE4");
  if (isIE)
	alert("IE");
}

// Create a DHTML layer
function createLayer(name, left, top, width, height, visible, content) {
  var layer;
  if (isNav4) {
    document.writeln('<layer name="' + name + '" left=' + left + ' top=' + top + ' width=' + width + ' height=' + height +  ' visibility=' + (visible ? '"show"' : '"hide"') +  '>');
    document.writeln(content);
    document.writeln('</layer>');
    layer = getLayer(name);
    layer.width = width;
    layer.height = height;
  } else {
    document.writeln('<div id="' + name + '" style="position:absolute; overflow:none; left:' + left + 'px; top:' + top + 'px; width:' + width + 'px; height:' + height + 'px;' + ' visibility:' + (visible ? 'visible;' : 'hidden;') +  '">');
    document.writeln(content);
    document.writeln('</div>');
  }
  clipLayer(name, 0, 0, width, height);
}

// Get the layer object called "name"
function getLayer(name) {
  if (isNav4)
    return(document.layers[name]);
  else if (isIE4) {
  	if ( eval('document.all.' + name) != null) {
	    layer = eval('document.all.' + name + '.style');
	    return(layer);
	} else  
	  return(null);
  } else if (is5up) {
	var theObj = document.getElementById(name);
	return theObj.style
  } else
    return(null);
}

// Move layer to x,y
function moveLayer(name, x, y) {		
  var layer = getLayer(name);		
  if (layer != null) {
    if (isNav4)
	layer.moveTo(x, y);
    else if (isIE) {
	layer.left = x + "px";
	layer.top  = y + "px";
    } else {
	layer.height = iHeight - y;
	layer.width	= iWidth - x;
	layer.left = x + "px";
	layer.top  = y + "px";
    }
  }
}

// Set layer background color
function setLayerBackgroundColor(name, color) {		
  var layer = getLayer(name);		
  if (layer != null) {
    if (isNav4) 
      layer.bgColor = color;
    else //if (document.all)
      layer.backgroundColor = color;
  }
}

// Toggle layer to invisible
function hideLayer(name) {		
  var layer = getLayer(name);		
  if (layer != null) {
    if (isNav4)
      layer.visibility = "hide";
    else
     layer.visibility = "hidden";
  }
}

// toggle layer to visible
function showLayer(name) {		
  var layer = getLayer(name);		
  if (layer != null) {
    if (isNav4)
      layer.visibility = "show";
    else
      layer.visibility = "visible";
  }
}

// clip layer display to clipleft, cliptip, clipright, clipbottom
function clipLayer(name, clipleft, cliptop, clipright, clipbottom) {		
  var layer = getLayer(name);		
  if (layer != null) {
    if (isNav4) {
      layer.clip.left   = clipleft;
      layer.clip.top    = cliptop;
      layer.clip.right  = clipright;
      layer.clip.bottom = clipbottom;
    } else if (isIE) {
      layer.clip = 'rect(' + cliptop + ' ' +  clipright + ' ' + clipbottom + ' ' + clipleft +')';
    } else {
      layer.height = clipbottom - cliptop;
      layer.width	= clipright - clipleft;
      layer.top	= (cliptop+vspc) + "px";
      layer.left	= (clipleft+hspc) + "px";
    }
  }
}

// Replace layer's content with new content
function replaceLayerContent(name, content) {
  if (isNav4) {
    var layer = getLayer(name);
    if (layer != null) {
      layer.document.open();
      layer.document.writeln(content);
      layer.document.close();
    }
  }  else if (isIE) {
    if (eval("document.all." + name) != null) {
      content = content.replace(/\'/g,"\\'");
      var str = "document.all." + name + ".innerHTML = '" + content + "'";
      eval(str);
    }
  }
}

function boxIt(theLeft,theTop,theRight,theBottom) {
  clipLayer("zoomBoxTop",theLeft,theTop,theRight,theTop+ovBoxSize);
  clipLayer("zoomBoxLeft",theLeft,theTop,theLeft+ovBoxSize,theBottom);
  clipLayer("zoomBoxRight",theRight-ovBoxSize,theTop,theRight,theBottom);
  clipLayer("zoomBoxBottom",theLeft,theBottom-ovBoxSize,theRight,theBottom);	
  showLayer("zoomBoxTop");
  showLayer("zoomBoxLeft");
  showLayer("zoomBoxRight");
  showLayer("zoomBoxBottom");
}


//alert("dynamic layers");