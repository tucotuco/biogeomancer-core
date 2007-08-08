// mapTools.js

// Global vars to save mouse position
var mouseX=0;
var mouseY=0;
var x1=0;
var y1=0;
var x2=0;
var y2=0;
var zminx=0;
var zmaxx=0;
var zmaxy=0;
var zminy=0;

var mapX = 0; 
var mapY = 0; 

var zooming=false;
var panning=false;
var state='zoomin';

function resetImages(clickedName) {
  
  var imageArray = parent.toolsFrame.document.images;
  var newsrc = "";
  var oldsrc = "";  
  
  for (var i=0; i < imageArray.length; i++) {
    if (imageArray[i].name!=clickedName) {
      //reset
      if (imageArray[i].src.search("2.gif")!=-1) {
        oldsrc=imageArray[i].src;
        newsrc=oldsrc.replace(/2.gif/i,"1.gif");
        imageArray[i].src=newsrc;
      }     
    }
  }

}

function setState(newState) {
  //alert(newState);
  if (ready) {
    state = newState; 	
    if (isIE) {
      if (state == "pan") {
        document.all.theMap.style.cursor = "move";
      }
      else {
        document.all.theMap.style.cursor = "crosshair";
      }
    }    
  }
}

function setCurrentExtent(min_x,min_y,max_x,max_y) {
  minx = min_x;
  miny = min_y;
  maxx = max_x;
  maxy = max_y;
}

function setNewExtent(min_x,min_y,max_x,max_y) {
  newMinx = min_x;
  newMiny = min_y;
  newMaxx = max_x;
  newMaxy = max_y;
}

function zoomFullExtent() {
  var url = getMapImage(fullMinx, fullMiny, fullMaxx, fullMaxy); 
  setCurrentExtent(fullMinx, fullMiny, fullMaxx, fullMaxy);
  document.mapImage.src = url;
}

function setZoomBoxSettings() {
  //alert("setZoomBoxSettings()");
  // Set up event capture for mouse movement
  if (isNav && is5up) {
    document.captureEvents(Event.MOUSEMOVE);
    document.captureEvents(Event.MOUSEDOWN);
    document.captureEvents(Event.MOUSEUP);
    document.onmousemove = getMouse;
    document.onmousedown = mapTool;
    document.onmouseup = chkMouseUp;
  } else if (isNav4) {
    // Otherwise the buttons don't work
    getLayer("theTop").captureEvents(Event.MOUSEMOVE);
    getLayer("theTop").captureEvents(Event.MOUSEDOWN);
    getLayer("theTop").captureEvents(Event.MOUSEUP);
    getLayer("theTop").onmousemove = getMouse;
    getLayer("theTop").onmousedown = mapTool;
    getLayer("theTop").onmouseup = chkMouseUp;
  } else {
    document.onmousemove = getMouse;
    document.onmousedown = mapTool;
    document.onmouseup = chkMouseUp;
  }
}

function disableClick() {
  if (isNav && is5up) {
    document.captureEvents(Event.MOUSEMOVE);
    document.captureEvents(Event.MOUSEDOWN);
    document.captureEvents(Event.MOUSEUP);
    document.onmousemove = disable;
    document.onmousedown = disable;
    document.onmouseup = disable;
  } else if (isNav4) {
    // Otherwise the buttons don't work
    getLayer("theTop").captureEvents(Event.MOUSEMOVE);
    getLayer("theTop").captureEvents(Event.MOUSEDOWN);
    getLayer("theTop").captureEvents(Event.MOUSEUP);
    getLayer("theTop").onmousemove = disable;
    getLayer("theTop").onmousedown = disable;
    getLayer("theTop").onmouseup = disable;
  } else {
    document.onmousemove = disable;
    document.onmousedown = disable;
    document.onmouseup = disable;
  }
}

// Check for mouseup
function chkMouseUp(e) { 
  if (zooming || panning) {
    if (mouseX<0)
      mouseX = 0;
    if (mouseX>iWidth)
      mouseX = iWidth;
    if (mouseY<0)
      mouseY = 0;
    if (mouseY>iHeight)
      mouseY = iHeight;
      mapTool(e);
  }
}

// Perform appropriate action with mapTool
function mapTool (e) {
  //alert(state);
  if (state == "setActive") return true;
  getImageXY(e);
  if ((!zooming) && (!panning) && (mouseX>=0) && (mouseX<iWidth) && (mouseY>=0) && (mouseY<iHeight)) {
    if (state == "pan" ) {
      startPan(e);
    } else if (state == "identify") {
       identify(e);
    } else if (state == "georeference") {
       georeference(e);
    } else    
       startZoomBox(e);
    return false;    
  } else if (zooming) {
    getMouse(e);
    stopZoomBox(e);
    hideZoomBox();
  } else if (panning) {
    getMouse(e);
    stopPan(e);
  }
  return true;
}

// Convert mouse click xy's into map coordinates
function getMapXY(xIn,yIn) {

  // If we're completely zoomed out, the values might not be correct otherwise
  reaspect();

  mouseX = xIn;
  var pixelX = (maxx-minx) / iWidth;
  mapX = pixelX * mouseX + minx;
  mouseY = iHeight - yIn;
  var pixelY = (maxy-miny) / iHeight;
  mapY = pixelY * mouseY + miny;
}

function getImageXY(e) {
  if (isNav) {
    mouseX=e.pageX;
    mouseY=e.pageY;
  } else {
    mouseX=event.clientX + document.body.scrollLeft;
    mouseY=event.clientY + document.body.scrollTop;
  }
  // Subtract offsets from page left and right
  mouseX = mouseX-hspc;
  mouseY = mouseY-vspc;
}	

// Recenter map is the default option
function recenter(e) {

  hideZoomBox();
  getMapXY(mouseX,mouseY);
  var widthHalf = Math.abs(maxx - minx) / 2;
  var heightHalf = Math.abs(maxy - miny) / 2;
  newMinx = mapX - widthHalf;
  newMaxx = mapX + widthHalf;
  newMaxy = mapY + heightHalf;
  newMiny = mapY - heightHalf;

  if (state == "zoomin") {
      addPercent(-50);
  } else if (state == "zoomout") {
      addPercent(75);
  } 
  // update the extent
  setCurrentExtent(newMinx, newMiny, newMaxx, newMaxy);        
  if (presentationType=="queryPresentation") { //need to use getFeatures
      url = getFeatures(0, 0, newMinx, newMiny, newMaxx, newMaxy); 
      hideLayer("theMap");
      mapImage.src="images/other/gray.gif";
      parent.searchResultsFrame.document.location = url;            
  } else {
      url = getMapImage(newMinx, newMiny, newMaxx, newMaxy);
      document.mapImage.src = url; 
  }	
}

// Get the coords at mouse position
function getMouse(e) {
  window.status="";
  getImageXY(e);

  if (zooming) {
    if (mouseX<0)
      mouseX = 0;
    if (mouseX>iWidth)
      mouseX = iWidth;
    if (mouseY<0)
      mouseY = 0;
    if (mouseY>iHeight-bottomBorderHeight)
      mouseY = iHeight-bottomBorderHeight;
    x2=mouseX;
    y2=mouseY;
    setClip();
    return false;
  } else if (panning) {
    x2=mouseX;
    y2=mouseY;
    panMouse();	
    return false;
  } 
}

// Start zoom in.... box displayed
function startZoomBox(e) {
  //alert("startZoomBox(e)");
  if (extentOutsideMap) {
    hideZoomBox();
    //alert("extentOutsideMap=true");
    return;
  }
  getImageXY(e);	
	
  // Keep it within the MapImage
  if ((mouseX<iWidth) && (mouseY<iHeight-bottomBorderHeight)) {
    if (!zooming) {
      x1=mouseX;
      y1=mouseY;
      x2=x1+1;
      y2=y1+1;
      zooming=true;
      clipLayer("zoomBoxTop",x1,y1,x2,y2);
      clipLayer("zoomBoxLeft",x1,y1,x2,y2);
      clipLayer("zoomBoxRight",x1,y1,x2,y2);
      clipLayer("zoomBoxBottom",x1,y1,x2,y2);
      showZoomBox();
    }
  } else {
    if (zooming) {
      stopZoomBox(e);
    } 
  }
  return false;	
}

// Stop zoom box display... zoom in
function stopZoomBox(e) {
  //alert("stopZoomBox(e)");
  zooming=false;
  var width = Math.abs(maxx - minx);
  var height = Math.abs(maxy - miny); 
  if ((zmaxx <zminx+2) && (zmaxy < zminy+2)) {
      // If the zoom box is too small
      recenter(e);
  } else {
      var pixelX = width / iWidth;
      var theY = iHeight - zmaxy;
      var pixelY = height / iHeight;
      var url = "error.html";
      if (state == "zoomin") {
        newMinx = mouse2GeoX(iWidth,zminx); 
        newMiny = mouse2GeoY(iHeight,zminy); 
        newMaxx = mouse2GeoX(iWidth,zmaxx); 
        newMaxy = mouse2GeoY(iHeight,zmaxy);         
        geoConstraints(newMinx, newMiny, newMaxx, newMaxy);
      }
      if (state == "zoomout") {
	newMinx = minx - (width*2/2);
	newMaxx = maxx + (width*2/2);
	newMaxy = maxy + (height*2/2);
	newMiny = miny - (height*2/2);				       
      }
      if (state!="selectbyrect") {
        // update the extent
        setCurrentExtent(newMinx, newMiny, newMaxx, newMaxy); 
        //alert(presentationType);
        if (presentationType=="queryPresentation") { //need to use getFeatures
            url = getFeatures(0, 0, newMinx, newMiny, newMaxx, newMaxy); 
            hideLayer("theMap");
            mapImage.src="images/other/gray.gif";
            parent.searchResultsFrame.document.location = url;            
        } else {
            url = getMapImage(newMinx, newMiny, newMaxx, newMaxy);
            document.mapImage.src = url; 
        }	       
      } else {
        useLimitExtent = true;
        presentationType = "gmlPresentation";
        queryString = "";
        selMinx = mouse2GeoX(iWidth,zminx); 
        selMiny = mouse2GeoY(iHeight,zmaxy); 
        selMaxx = mouse2GeoX(iWidth,zmaxx); 
        selMaxy = mouse2GeoY(iHeight,zminy);
        url = getFeatures(0, 0, selMinx, selMiny, selMaxx, selMaxy, "");
        window.open(url,"GML_Export");
      }      
  } 			
  return true;
}

// Clip zoom box layer to mouse coords
function setClip() {	

  if (x1>x2) {
    zmaxx=x1;
    zminx=x2;
  } else {
    zminx=x1;
    zmaxx=x2;
  }
  if (y1>y2) {
    zminy=y1;
    zmaxy=y2;
  } else {
    zmaxy=y1;
    zminy=y2;
  }
	
  if ((x1 != x2) && (y1 != y2)) {
    var ovBoxSize = 1;
    clipLayer("zoomBoxTop",zminx, zmaxy, zmaxx, zmaxy+ovBoxSize);
    clipLayer("zoomBoxLeft",zminx, zmaxy, zminx+ovBoxSize,  zminy);
    clipLayer("zoomBoxRight",zmaxx-ovBoxSize, zmaxy, zmaxx, zminy);
    clipLayer("zoomBoxBottom",zminx, zminy-ovBoxSize, zmaxx, zminy);
  }
}

// Start pan.... image will move
function startPan(e) {

  if (extentOutsideMap) {
    hideZoomBox();
    //alert("extentOutsideMap=true");
    return;
  }
  getImageXY(e);
  // Keep it within the MapImage
  if ((mouseX<iWidth) && (mouseY<iHeight)) {
    if (panning) {
      stopPan(e);
    } else {
      x1=mouseX;
      y1=mouseY
      x2=x1+1;
      y2=y1+1;
      panning=true;
    }
  }
  return false;
}

// Stop moving image.... pan 
function stopPan(e) {
  showLayer("loadLayer");
  //alert("hello");
  //document.mapImage.src=background.src;
  if ((Math.abs(x2-x1) < 2) && (Math.abs(y2-y1) < 2)) {
    // The move is too small
    recenter(e);
    //alert("x1 "+x1+" x2 "+x2+" y1 "+y1+" y2 "+y2+" newMinx "+newMinx+" newMiny "+newMiny+" newMaxx "+newMaxx+" newMaxy "+newMaxy);
  } else  {
    hideLayer("theMap");  
    panning=false;
    var width = Math.abs(maxx - minx);
    var height = Math.abs(maxy - miny);
    var tempLeft=minx;
    var tempRight=maxx;
    var tempTop=maxy;
    var tempBottom=miny;
    var ixOffset = x2-x1;
    var iyOffset = y1-y2;
    pixelX = width / iWidth;
    var theY = iHeight - zmaxy;
    pixelY = height / iHeight;
    var xOffset = pixelX * ixOffset;
    var yOffset = pixelY * iyOffset;
    newMaxy = maxy - yOffset;
    newMaxx = maxx - xOffset;
    newMinx = minx - xOffset;
    newMiny = miny - yOffset;
    //alert("x1 "+x1+" x2 "+x2+" y1 "+y1+" y2 "+y2+" newMinx "+newMinx+" newMiny "+newMiny+" newMaxx "+newMaxx+" newMaxy "+newMaxy);
    // update the extent
    setCurrentExtent(newMinx, newMiny, newMaxx, newMaxy); 
    moveLayer("theMap",hspc,vspc);
    clipLayer("theMap", 0, 0, iWidth, iHeight);    
    if (parent.mapFrame.presentationType=="queryPresentation") { //need to use getFeatures
        url = getFeatures(0, 0, newMinx, newMiny, newMaxx, newMaxy); 
        hideLayer("theMap");
        mapImage.src="images/other/gray.gif";
        parent.searchResultsFrame.document.location = url;            
    } else {
        var url = getMapImage(newMinx, newMiny, newMaxx, newMaxy);
        document.mapImage.src = url;           
    } 
    setTimeout('showLayer("theMap")', 600);

  }		
  return true;	
}

// Move map image with mouse
function panMouse() {

  var xMove = x2-x1;
  var yMove = y2-y1;
  var cLeft = -xMove;
  var cTop = -yMove;
  var cRight = iWidth;
  var cBottom = iHeight;
  if (xMove>0) {
    cLeft = 0;
    cRight = iWidth - xMove;
  }
  if (yMove>0) {
    cTop = 0;
    cBottom = iHeight - yMove;
  }
  clipLayer("theMap",cLeft,cTop,cRight,cBottom);
  moveLayer("theMap",xMove+hspc,yMove+vspc);
  return false;
}

// identify feature
function identify(e) {
  var theX = mouseX;
  var theY = mouseY;
  getMapXY(theX,theY);
  presentationType = "identifyPresentation";
  var url = getFeatures(mapX, mapY, minx, miny, maxx, maxy);
  //alert(parent.searchResultsFrame.document.name);// = url;
  //parent.searchResultsFrame.document.URL = url;
  parent.searchResultsFrame.document.location = url;
}

// adjust point feature
function georeference(e) {
  var theX = mouseX;
  var theY = mouseY;
  getMapXY(theX,theY);
  presentationType = "identifyPresentation";
  var url = editFeature(mapX, mapY, UID);
  //alert(url);
  //parent.searchResultsFrame.document.URL = url;
  //parent.searchResultsFrame.document.location = url;
}

function hideZoomBox() {
  hideLayer("zoomBoxTop");
  hideLayer("zoomBoxLeft");
  hideLayer("zoomBoxRight");
  hideLayer("zoomBoxBottom");
}

function addPercent(value) {

  // add %
  value = 1 + value/100;
	
  var width = Math.abs(newMaxx-newMinx);
  var height = Math.abs(newMaxy-newMiny);
  var cx = newMinx + width/2;
  var cy = newMiny + height/2;
	
  newMinx = cx - width*value/2;
  newMiny = cy - height*value/2;
  newMaxx = cx + width*value/2;
  newMaxy = cy + height*value/2;
}

function showZoomBox() {
  showLayer("zoomBoxTop");
  showLayer("zoomBoxLeft");
  showLayer("zoomBoxRight");
  showLayer("zoomBoxBottom");
}

function reaspect() {

  // Check if extent is too big or too small
  width = maxx - minx;
  height = maxy - miny;
  if (height == 0) height = 0.0001;
    fullWidth = fullMaxx - fullMinx;
    fullHeight = fullMaxy - fullMiny;
    if (fullHeight == 0) fullHeight = 0.0001; 

  if (width/fullWidth > height/fullHeight)
    percent = width/fullWidth;	
  else 
    percent = height/fullHeight;
  if (percent < minScale) {
    midX = minx + ((maxx-minx) / 2); 
    midY = miny + ((maxy - miny) / 2); 
    minx = midX - (fullWidth / 2 * minScale);
    maxy = midY + (fullHeight / 2 * minScale);
    maxx = midX + (fullWidth / 2 * minScale);
    miny = midY - (fullHeight / 2 * minScale);
    width = maxx - minx;
    height = maxy - miny;
  } else if (percent > maxScale) {
    midX = minx + ((maxx-minx) / 2);
    midY = miny + ((maxy - miny) / 2);
    minx = midX - (fullWidth / 2 * maxScale);
    maxy = midY + (fullHeight / 2 * maxScale);
    maxx = midX + (fullWidth / 2 * maxScale);
    miny = midY - (fullHeight / 2 * maxScale);
    width = maxx - minx;
    height = maxy - miny;
  }

  // If we move or change the extent of the map 
  // and the map is somewhere at the side
  // place it inside full extent 

  // Stretch the image to the maxx size
  ratio = iWidth / iHeight;
  if (ratio == 0) ratio = 0.0001; 
  if (width/height < ratio) {
    // Stretch x 
    mid = minx + width/2;
    minx = mid - (height * ratio)/2;
    maxx = mid + (height * ratio)/2;
    width = maxx - minx;
  } else {
    // Stretch y
    mid = miny + height/2;
    miny = mid - (width / ratio)/2;
    maxy = mid + (width / ratio)/2;
    height = maxy - miny;
  }

  // Now position the image maxx
  if ((minx + width > fullMaxx) && (maxx - width >= fullMinx)) {
    minx = fullMaxx - width;
    maxx = fullMaxx;
  }
  if ((maxx - width < fullMinx) && (minx + width <= fullMaxx)) {
    minx = fullMinx;
    maxx = fullMinx + width; 
  }
  if ((minx + width >= fullMaxx) && (maxx - width <= fullMinx)) {
    minx = (fullMaxx+fullMinx)/2 - width/2;
    maxx = (fullMaxx+fullMinx)/2 + width/2;
  }

  if ((miny + height > fullMaxy) && (maxy - height >= fullMiny)) {
    miny = fullMaxy - height; 
    maxy = fullMaxy;
  }
  if ((maxy - height < fullMiny) && (miny + height <= fullMaxy)) {
    miny = fullMiny;
    maxy = fullMiny + height; 
  }
  if ((miny + height >= fullMaxy) && (maxy - height <= fullMiny)) {
    miny = (fullMaxy+fullMiny)/2 - height/2;
    maxy = (fullMaxy+fullMiny)/2 + height/2;
  }
}

// ******* geoConstraints(obRef) *************
// Check for the geographical and proportions constrains of the bounding box
function geoConstraints(x1, y1, x2, y2)
{  	
  // check min and max values
  if (x1>x2){
  	var tmpX1=x1;
  	x1=x2;
	x2=tmpX1;
	}
  if (y1>y2){
  	var tmpY1=y1;
  	y1=y2;
	y2=tmpY1;
	}				
  // check first if current BBox obeys the initial proportions	  
  var initPropor = (fullMaxx - fullMinx) / (fullMaxy - fullMiny);    
  var Propor = (x2 - x1) / (y2 - y1);   
  if (initPropor!=Propor)
  	{
		var sizeX = x2 - x1;
		var sizeY = y2 - y1;
		if (sizeY<sizeX){
			var midY= y1 + (sizeY)/2;
			var newSizeY= (sizeX)/initPropor;
			y1= midY - newSizeY / 2;
			y2= midY + newSizeY / 2;
			}
		else{
			var midX= x1 + (sizeX)/2;
			var newSizeX= (sizeY)*initPropor;
			x1= midX - newSizeX / 2;
			x2= midX + newSizeX / 2;
			}			
	}
  
  // check if both BBox axis are inside the initial BBox
  // check X-axis 
  var SizeX = x2 - x1;
  if (x1<fullMinx)
     {      
	      x1 = fullMinx;
          x2 = x1 + SizeX;
          if (x2>fullMaxx) { x2=fullMaxx}
     }
  else
	{
       if (x2>fullMaxx)
          {
            x2 = fullMaxx;
            x1 = x2 - SizeX;
          }
     }
  // check Y-axis 
  var SizeY = (y2 - y1);
  
  if (y1<fullMiny)
     {
       y1 = fullMiny;
       y2 = y1 + SizeY;
       if (y2>fullMaxy) {y2=fullMaxy}
     }
  else
     {
       if (y2>fullMaxy)
          {
            y2= fullMaxy;
            y1= y2 - SizeY;
          }
     }
  setNewExtent(x1,y1,x2,y2)   
  
}

// *** Mouse to geographical conversion function
function mouse2GeoX(width,mX) {
  return minx + mX * (maxx - minx)/width;
}
function mouse2GeoY(height,mY) {
  return maxy - mY * (maxy - miny)/height;
}

// ***  Geographical to mouse conversion function
function geo2MouseX(obRef,cX) {
  return Math.round(obRef.width * (cX - x1)/ (x2 - x1) )
}
function geo2MouseY(obRef,cY) { 
  return Math.round( obRef.height * (y2 - cY) / ( y2 - y1 )); 
}

function checkImageStatus(url) {
    if(url.indexOf("png") != -1 ) {
       return true;
    } else {
       return false;
    }
} 
  
