// initSettings.js

// Map image placement
var hspc = 0;
var vspc = 0;
	
// Image Size
var iWidth = 480;
var iHeight = 240;
var format = "PNG";
var styles = "";
var pixelTolerance = 2;
	
// Initial Extent
var minx = -180.0;
var miny = -90.0;
var maxx = 180.0;
var maxy = 90.0;

var newMinx = -180.0;
var newMiny = -90.0;
var newMaxx = 180.0;
var newMaxy = 90.0;

// Select Extent
var selMinx = 0;
var selMaxx = 0;
var selMaxy = 0;
var selMiny = 0;

// Full Extent
var fullMinx = -180.0;
var fullMiny = -90.0;
var fullMaxx = 180.0;
var fullMaxy = 90.0;

// Scale settings
var minScale = 0;
var maxScale = 1;

// ZoomBox function
var extentOutsideMap = false;

// Query settings
var activeLayerName = "collections";
var subFields = "#ALL#";
var useLimitExtent = false;
var selectCount = 0;
var queryString = "";
var presentationType = "";
var selectType = "point";

//Edit settings
var UID = "";

// Client application settings.
var ready = true;
var state = "zoomin";
var visibleLayers = "";
var bottomBorderHeight = 0; //number of pixels for border at bottom of map
var host = "geomobile";
var baseUrl =  host + "/cgi-bin/mapserv.exe?MAP=C:/program%20files/apache%20software%20foundation/apache2/htdocs/biogeomancer/world.map";
var baseEditUrl =  host + "/geoserver/wfs/TestWfsPost";
//var baseUrl =  "bg.berkeley.edu/cgi-bin/mapserv?MAP=/home/dneufeld/bgproj/java/webapp/sea/world.map";
//var baseEditUrl =  "bg.berkeley.edu/geoserver/wfs/TestWfsPost";
