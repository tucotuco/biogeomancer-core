<html>
<head>
     <title>BioGeomancer</title>
</head>
<center>
<body>
<form action="process_validation.jsp" method=post id=form>
<table units=pixels width=800 align=center cellspacing=1>
<tr><td width=800 align=center valign=center>
   <table units=pixels width=800 align=center>
     <tr>
        <td align=center valign=center width=800><img src="BGLogoHalf.jpg" units=pixels></img></td>
     </tr>
   </table>
</td></tr>

<tr bgcolor=7B3F00><td width=800>&nbsp;</td></tr> 

<tr><td>
   <table units=pixels width=800 height=200 align=center border=true>
      <tr><td align=center valign=center><font face=verdana size=1><b>Mapping Interface Placeholder</b></font></td></tr>
       
	<!-- <img src="http://www.geomuse.org/cgi-bin/mapserv.exe?MAP=E:/program%20files/apache%20software%20foundation/tomcat%205.5/webapps/ -->
        <!-- biogeomancer//sea/world.map&SERVICE=WMS&VERSION=1.1.1&REQUEST=GetMap&WIDTH=480&HEIGHT=240&BBOX=-180,-90,180,90&LAYERS=wsiearth,cntry, -->
        <!-- lakes,rivers,collections"></img> -->
   </table>
</td></tr>

<tr><td>
    <table units=pixels width=400 align=center>
      <tr><td>&nbsp;</td></tr>
      <tr><td align=center valign=center colspan=2><font face=verdana size=1><b>Choose the correct locality: </b></font></td></tr>

      <tr><td width=100>&nbsp;</td>
          <td align=left width=300>
             <input type="radio" name="choice" value="interpret_1" CHECKED><font face=verdana size=1>locality interpretation 1</font>
          </td>
      </tr>

      <tr><td width=100>&nbsp;</td>
          <td align=left width=300>
              <input type="radio" name="choice" value="interpret_2"><font face=verdana size=1>locality interpretation 2</font>
          </td>
      </tr>

      <tr><td align=center valign=center colspan=2><br><input type="submit" value="Submit"></td></tr>
     </table>
</td></tr>

<tr><td>&nbsp;</td></tr>

</table>
</form>
</body>
</center>
</html>
