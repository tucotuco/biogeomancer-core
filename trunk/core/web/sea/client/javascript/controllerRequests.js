// controllerRequests.js	
		
function getMapImage(minx, miny, maxx, maxy) {
  var pUrl ="http://" + baseUrl;
  pUrl += "&SERVICE=WMS";  
  pUrl += "&VERSION=1.1.1";
  pUrl += "&REQUEST=GetMap";
  pUrl += "&WIDTH=" + iWidth;
  pUrl += "&HEIGHT="+ iHeight;	
  pUrl += "&BBOX=" + minx + "," + miny + "," + maxx + "," + maxy;

  pUrl += "&LAYERS=wsiearth,cntry,lakes,rivers,collections"			
  //if (transparent) pUrl += "&TRANSPARENT=TRUE";
  pUrl += "&SLD=http://" + host + ":8080/biogeomancer_dneufeld/sea/client/sld/collections_" + uid + "_filter.xml"
  pUrl += "&FORMAT=GIF";
						
  if (styles!="") pUrl += "&STYLES=" + styles;
  //window.open(pUrl);		
  return pUrl;
}


function getFeatures(mapx, mapy, minx, miny, maxx, maxy) {
  var pUrl ="http://"+ baseUrl;
  //alert(presentationType);
  if (presentationType=="identifyPresentation") {
      pUrl += "&MODE=query";
      pUrl += "&IMGEXT="+ minx + "+" + miny + "+" + maxx + "+" + maxy;
      pUrl += "&IMGSIZE=" + iWidth + "+" +  iHeight;
      pUrl += "&MAPXY=" + mapx + "+" + mapy;
      pUrl += "&LAYERS=" + activeLayerName;
      pUrl += "&presentationType=" + presentationType;
      //alert(pUrl);      
  } else if (presentationType=="queryPresentation") {
      pUrl += "&MODE=itemnquery";
      pUrl += "&MAPEXT="+ minx + "+" + miny + "+" + maxx + "+" + maxy;      
      pUrl += "&IMGSIZE=" + iWidth + "+" +  iHeight;      
      pUrl += "&LAYERS=cntry";      
      pUrl += "&QLAYER=" + activeLayerName;
      pUrl += queryString;
      pUrl += "&presentationType=" + presentationType;
      //alert(pUrl);
  } else {
      pUrl += "&SERVICE=WFS";
      pUrl += "&VERSION=1.0.0"; 
      pUrl += "&REQUEST=GetFeature";
      pUrl += "&TYPENAME=" + activeLayerName;
      pUrl += "&BBOX=" + minx + "," + miny + "," + maxx + "," + maxy;
  }
  //window.open(pUrl);	  
  return pUrl;
}

function updateMapImage() {
   var url = getMapImage(minx, miny, maxx, maxy);
   //alert(url);
   document.mapImage.src = url;     
}

function editFeature(mapx, mapy, UID) {
  var pUrl ="http://"+ baseEditUrl;
  var wfsRequest = "";
    
  wfsRequest += '<!-- Transactions do not work on all systems with Shapefiles, which are    -->';
  wfsRequest += '<!-- the default for this demo.  PostGIS serves as a much better backend.  -->';
  wfsRequest += '<!-- Sometimes success will be reported but the transaction will not go    -->';
  wfsRequest += '<!-- through.  We include this transaction as a sample for updates.        -->';
  wfsRequest += '<wfs:Transaction service="WFS" version="1.0.0"';
  wfsRequest += '  xmlns:topp="http://www.openplans.org/topp"';
  wfsRequest += '  xmlns:ogc="http://www.opengis.net/ogc"';
  wfsRequest += '  xmlns:wfs="http://www.opengis.net/wfs"';
  wfsRequest += '  xmlns:gml="http://www.opengis.net/gml">';
  wfsRequest += '  <wfs:Update typeName="topp:collections">';
  wfsRequest += '    <wfs:Property>';
  wfsRequest += '      <wfs:Name>the_geom</wfs:Name>';
  wfsRequest += '      <wfs:Value>';
  wfsRequest += '        <gml:Point>';
  wfsRequest += '          <gml:coordinates>' + mapx + ',' + mapy + '</gml:coordinates>';
  wfsRequest += '        </gml:Point>';
  wfsRequest += '      </wfs:Value>';
  wfsRequest += '    </wfs:Property>';
  wfsRequest += '    <ogc:Filter>';
  wfsRequest += '      <ogc:PropertyIsEqualTo>';
  wfsRequest += '        <ogc:PropertyName>object_key</ogc:PropertyName>';  
  wfsRequest += '        <ogc:Literal>' + UID + '</ogc:Literal>';
  //wfsRequest += '        <ogc:Literal>"234602.0"</ogc:Literal>';
  wfsRequest += '      </ogc:PropertyIsEqualTo>';  
  wfsRequest += '    </ogc:Filter>';
  wfsRequest += '  </wfs:Update>';
  wfsRequest += '</wfs:Transaction>';

  parent.searchResultsFrame.document.forms[0].body.value=wfsRequest;
  parent.searchResultsFrame.document.forms[0].submit();

  // wait for update the request new map
  //alert(parent.searchResultsFrame.document.forms[0].body.value);
  setTimeout(updateMapImage,2000);
  pUrl += wfsRequest;
  //window.open(pUrl);	  
  //return pUrl;
}
