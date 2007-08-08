<!-- REQUIRED BACKEND CODE AND FUTURE CHANGES -->
<!-- Need backend code that interpets results from engine and then displays them on the screen in the format we've put together -->
<!-- The "guess", "delete", and "validate" links need backend code to make them functional once we have real data -->
<!-- Need backend code to pass the results to the mapping interface on the next page so that the points are plotted -->
<!-- Build 2: the "save" feature needs backend code, so as to allow the user to save the recordset for a later time -->

<html>
<head>
     <title>BioGeomancer</title>
</head>
<center>
<body>
<form action="map_data.jsp" method=post id=form>
<table units=pixels width=800 align=center cellspacing=1>

<tr>
   <td width=800 align=center valign=center>
     <table units=pixels width=800 align=center>
        <tr><td align=center valign=center width=800>
        <img src="BGLogoHalf.jpg" units=pixels></img>
        </td></tr>
     </table>

   </td>
</tr>

<tr bgcolor=7B3F00>
   <td width=800 align=center bgcolor=7B3F00 colspan=2>
<a href="workbench.jsp" style="text-decoration:none"><font face=verdana size=1 color=FFA54F><b>IMPORT DATA&nbsp;&nbsp;>&nbsp;&nbsp; </b></font></a>
<a href="format_data.jsp" style="text-decoration:none"><font face=verdana size=1 color=FFA54F><b>FORMAT DATA&nbsp;&nbsp;>&nbsp;&nbsp; </b></font></a>
<a href="mode.jsp" style="text-decoration:none"><font face=verdana size=1 color=FFA54F><b>SELECT MODE&nbsp;&nbsp;>&nbsp;&nbsp; </b></font></a>
<a href="parse_georef_data.jsp" style="text-decoration:none"><font face=verdana size=1 color=FFA54F><b>PARSE/GEOREFERENCE&nbsp;&nbsp;>&nbsp;&nbsp;</b></font></a>
<font face=verdana size=1 color=white><b>MAP</b></font>
   </td>
</tr>

<tr><td>&nbsp;</td></tr>

    <tr>
      <td>
	<table units=pixels width=800 align=center cellspacing=0>
	<tr bgcolor=E3A869>
	<td align=left width=60><font face=verdana size=1 color=7B3F00><b>COUNTRY</b></font></td>
	<td align=left width=60><font face=verdana size=1 color=7B3F00><b>ADMIN1</b></font></td>
	<td align=left width=60><font face=verdana size=1 color=7B3F00><b>ADMIN2</b></font></td>
	<td align=left width=100><font face=verdana size=1 color=7B3F00><b>LOCALITY</b></font></td>
	<td align=left width=100><font face=verdana size=1 color=7B3F00><b>TAXON</b></font></td>
	<td align=left width=50><font face=verdana size=1 color=7B3F00><b>LAT</b></font></td>
	<td align=left width=50><font face=verdana size=1 color=7B3F00><b>LONG</b></font></td>
	<td align=left width=75><font face=verdana size=1 color=7B3F00><b>UNCERTAINTY</b></font></td>
	<td align=right><font size=1 color=7B3F00><a href="parse_georef_data.jsp" style="color=7B3F00">guess all</a></td>
	</tr>

	<tr bgcolor=FEF0DB>
	<td align=left><font face=verdana size=1 color=blue><a href='details.jsp' style='color=blue' target='_blank'>Australia</a></font></td>
	<td align=left bgcolor=FEF0DB><font face=verdana size=1 color=blue></font></td>
	<td align=left bgcolor=FEF0DB><font face=verdana size=1 color=blue></font></td>
	<td align=left bgcolor=FEF0DB><font face=verdana size=1 color=blue>12 km N of Lake Cargelligo</font></td>
	<td align=left bgcolor=FEF0DB><font face=verdana size=1 color=blue>Grevillea buxifolia</font></td>
	<td align=left bgcolor=FEF0DB><font face=verdana size=1 color=blue>-28.44</font></td>
	<td align=left bgcolor=FEF0DB><font face=verdana size=1 color=blue>149.92</font></td>
	<td align=left bgcolor=FEF0DB><font face=verdana size=1 color=blue>587.4 km</font></td>
	<td align=right bgcolor=FEF0DB><font size=1><a href='' style="color=red">guess</a>&nbsp;&nbsp;<a href='' style="color=7B3F00">delete</a>&nbsp;&nbsp;<a href="validate.jsp" target="_blank" style="color=7B3F00">validate</a>&nbsp;&nbsp;<a href="error_check.jsp" target="_blank" style="color=7B3F00">error check</a></font></td>
	</tr>
	<tr>
	<td align=left><font face=verdana size=1><a href='details.jsp' style='color=black' target='_blank'>Australia</a></font></td>
	<td align=left><font face=verdana size=1></font></td>
	<td align=left><font face=verdana size=1></font></td>
	<td align=left><font face=verdana size=1>2 miles W of Leura</font></td>
	<td align=left><font face=verdana size=1>Grevillea buxifolia</font></td>
	<td align=left><font face=verdana size=1>-33.18</font></td>
	<td align=left><font face=verdana size=1>146.39</font></td>
	<td align=left><font face=verdana size=1>1.2 km</font></td>
	<td align=right><font size=1><a href='' style="color=7B3F00">delete</a>&nbsp;&nbsp;<a href="validate.jsp" target="_blank" style="color=7B3F00">validate</a>&nbsp;&nbsp;<a href="error_check.jsp" target="_blank" style="color=7B3F00">error check</a></font></td>
	</tr>
       </table>
      </td>
    </tr>

   <tr><td>&nbsp;</td></tr>

    <tr>
      <td align=center valign=center>
         <br>
        <input type="submit" value="DOWNLOAD"><input type="submit" value="SAVE"><input type="submit" value="MAP">
      </td>
    </tr>


<!-- EXCLAMATION POINT: MEANS THAT THIS RECORD HAS 2 OR MORE LOCALITY INTERPRETATIONS -->
<!-- GUESS: MEANS THAT THE USER WANTS US TO CHOOSE THE BEST INTERPRETATION BASED UPON AN UNCERTAINTY CALCULATION (TBD) -->
<!-- DELETE: MEANS TO DELETE THIS RECORD FROM THE RECORDSET -->
<!-- VALIDATE: ALLOWS THE USER TO CHOSE THE CORRECT INTERPRETATION OF THE LOCALITY STRING AND VIEW THE POINTS ON A MAP -->

</table>
</form>
</body>
</center>
</html>
