<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
<title>bioGeomancer Client</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
</head>
  <SCRIPT type="text/javascript" language="JavaScript1.2">

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
	var page = new PageQuery(window.location.search); 
	return unescape(page.getValue(key)); 
  }
  var uid = queryString("uid");
  //alert(uid);

  document.writeln('<frameset rows="*" cols="*,800,*" framespacing="0" frameborder="NO" border="0">');
  document.writeln('  <frame src="border.htm" name="leftFrame" scrolling="NO" noresize>');
  document.writeln('  <frameset rows="120,30,240,*" cols="*" framespacing="0" frameborder="NO" border="0">');
  document.writeln('    <frame src="top.jsp" name="bannerFrame" scrolling="NO" noresize>');
  document.writeln('    <frameset cols="*,320" framespacing="0" frameborder="NO" border="0">');
  document.writeln('      <frame src="tools.htm" name="toolsFrame" scrolling="NO" noresize>');
  document.writeln('      <frame src="sidebar.htm" name="sidebarFrame" scrolling="NO" noresize>');
  document.writeln('    </frameset>');
  document.writeln('    <frameset cols="*,320" framespacing="0" frameborder="NO" border="0">');
  document.writeln('      <frame src="map.jsp?uid=' + uid + '" name="mapFrame" scrolling="NO">');
  document.writeln('      <frame src="search.htm" name="searchFrame" scrolling="NO" noresize>');
  document.writeln('    </frameset>');
  document.writeln('    <frame src="searchResults.htm" name="searchResultsFrame">');
  document.writeln('  </frameset>');
  document.writeln('  <frame src="border.htm" name="rightFrame" scrolling="NO" noresize>');
  document.writeln('</frameset>');  
  </SCRIPT>

<noframes>
<body>

</body>
</noframes>
</html>
