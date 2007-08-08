<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<HTML>
<HEAD>
<TITLE>MapTools Test</TITLE>
<META http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<SCRIPT type="text/javascript" language="JavaScript" src="javascript/initSettings.js"></SCRIPT>
<SCRIPT type="text/javascript" language="JavaScript" src="javascript/dynamicLayers.js"></SCRIPT>
<SCRIPT type="text/javascript" language="JavaScript" src="javascript/mapTools.js"></SCRIPT>
<SCRIPT type="text/javascript" language="JavaScript" src="javascript/controllerRequests.js"></SCRIPT>
</HEAD>
<SCRIPT type="text/javascript" language="JavaScript">

	window.focus();

    //setExtent(-180.0,-90.0,180.0,90.0);
    //setFullExtent(-180.0,-90.0,180.0,90.0);
    var uid = <%= request.getAttribute("uid") %>;
    //alert(<%= request.getAttribute("uid") %>);
    function initMap() {
	  // Load initial map
      setZoomBoxSettings();

      if (isIE && document.all.theMap) {
        if (state == "pan")
  	      document.all.theMap.style.cursor = "move";
        else
  	      document.all.theMap.style.cursor = "crosshair";
        }
        ready = true;
    }

    function PageQuery(q) {
	if(q.length > 1) this.q = q.substring(1, q.length);
	else this.q = null;
	this.keyValuePairs = new Array();
	if(q) {
	  for(var i=0; i < this.q.split("&").length; i++) {
	    this.keyValuePairs[i] = this.q.split("&")[i];
	  }
	}
	this.getKeyValuePairs = function() { return this.keyValuePairs; }
	this.getValue = function(s) {
	for(var j=0; j < this.keyValuePairs.length; j++) {
  	  if(this.keyValuePairs[j].split("=")[0] == s)
		return this.keyValuePairs[j].split("=")[1];
  	  }
		return false;
  	  }
	  this.getParameters = function() {
	  var a = new Array(this.getLength());
	  for(var j=0; j < this.keyValuePairs.length; j++) {
	    a[j] = this.keyValuePairs[j].split("=")[0];
      }
	return a;
    }
	this.getLength = function() { return this.keyValuePairs.length; } 
  }
  
  function queryString(key){
    //alert("key=" + key);
	var page = new PageQuery(window.location.search); 
	return unescape(page.getValue(key)); 
  }
  var uid = queryString("uid");
  //alert(uid);    
</SCRIPT>

<BODY bgcolor="DarkGray" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onLoad="initMap();">
<SCRIPT type="text/javascript" language="JavaScript1.2">
	// Loading Layer
	var content = '<IMG src="images/other/loading.gif" width="120" height="24">';
	createLayer("loadLayer",hspc+iWidth/2-60,vspc+iHeight/2-12,120,24,false,content);
    showLayer("loadLayer");

	// Map image
    content = '<IMG name="mapImage" src="' + getMapImage(minx,miny,maxx,maxy) +'" hspace=0 vspace=0 border=0 width=' + iWidth + ' height=' + iHeight + '>';
    //alert(content);
    createLayer("theMap",hspc,vspc,iWidth,iHeight,true,content);
    // needed so that pan works
    clipLayer("theMap",0,0,iWidth,iHeight);

    hideLayer("loadLayer");

	// Zoom Box
	content = '<IMG src="images/other/transparent.gif" width=1 height=1>';
	createLayer("zoomBoxTop",hspc,vspc,iWidth,iHeight,false,content);
	content = '<IMG src="images/other/transparent.gif" width=1 height=1>';
	createLayer("zoomBoxLeft",hspc,vspc,iWidth,iHeight,false,content);
	content = '<IMG src="images/other/transparent.gif" width=1 height=1>';
	createLayer("zoomBoxRight",hspc,vspc,iWidth,iHeight,false,content);
	content = '<IMG src="images/other/transparent.gif" width=1 height=1>';
	createLayer("zoomBoxBottom",hspc,vspc,iWidth,iHeight,false,content);

	// Zoom Box Color
	setLayerBackgroundColor("zoomBoxTop", "#ff0000");
	setLayerBackgroundColor("zoomBoxLeft", "#ff0000");
	setLayerBackgroundColor("zoomBoxRight", "#ff0000");
	setLayerBackgroundColor("zoomBoxBottom", "#ff0000");

	// Glass Layer for Mouse Click Handling
	content = '<IMG src="images/other/transparent.gif" width=1 height=1>';
	createLayer("theTop",hspc,vspc,iWidth,iHeight,true,content);

    //alert(document.mapImage.src);
    


</SCRIPT>
</BODY>
</HTML>