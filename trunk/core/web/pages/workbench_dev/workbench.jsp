<!-- THIS PAGE REQUIRES THE FOLLOWING BACKEND CODE/FUTURE CHANGES: -->
<!-- 1. Based upon the submission (single, single typed, multiple) add this information to record object -->
<!-- 2. If the submission is either an upload or paste, check file size -->
<!-- 3. Incorporate the code already written by Dave/Greg for file uploads into the workbench.jsp page -->
<!-- 4. Once backend code for georeferencing and parsing works, remove text in textarea -->
<html>
<head>
     <title>BioGeomancer</title>
   
     <script language="Javascript">
       function validateSingle(form){
	if ((form.single_typed_country.value == "") && 
            (form.single_typed_admin1.value == "") &&
            (form.single_typed_admin2.value == "") &&
            (form.single_typed_locality.value == "")){
	  alert ('You must enter at least one field to continue.');
	  return false;}
 
	return true;
	}

        function validateMultiple(form){
	if (form.data_structure[1].checked && form.delimiter.value == "none"){
	  alert ('You must enter a delimeter to continue.');
	  return false;}

	return true;
        }
     </script>
</head>
<center>

<body>
<form action="mode.jsp" method=post id=form1 onsubmit="return validateSingle(form1)">
<table units=pixels width=800 align=center cellspacing=1>

<tr>
   <td width=800 align=center valign=center colspan=2>
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
<font face=verdana size=1 color=white><b>FORMAT DATA&nbsp;&nbsp;>&nbsp;&nbsp; </b></font> 
<font face=verdana size=1 color=white><b>SELECT MODE&nbsp;&nbsp;>&nbsp;&nbsp; </b></font>
<font face=verdana size=1 color=white><b>PARSE/GEOREFERENCE&nbsp;&nbsp;>&nbsp;&nbsp;</b></font>
<font face=verdana size=1 color=white><b>MAP</b></font>
   </td>
</tr>
<tr><td align=center valign=center colspan=2><br><font face=verdana size=1><b>Please type, paste or import your data for georeferencing:</b></font></td></tr>

<tr><td colspan=2>&nbsp;</td></tr>
<tr><td colspan=2><hr width=700></td></tr>
<tr><td colspn=2 align=right><font face=verdana size=1><b>Type in values:</b></td></tr>
<tr>
   <td width=250 align=right><font face=verdana size=1>Country:</font></td>
   <td width=550 align=left><input type="text" name="single_typed_country" size="50"><font face=verdana size=1 color=gray>&nbsp;(e.g. Mexico or MX)</font></td>
</tr>
<tr>
   <td width=250 align=right><font face=verdana size=1>Admin 1:</font></td>
   <td width=550 align=left><input type="text" name="single_typed_admin1" size="50"><font face=verdana size=1 color=gray>&nbsp;(state or province)</font></td>
</tr>
<tr>
   <td width=250 align=right><font face=verdana size=1>Admin 2:</font></td>
   <td width=550 align=left><input type="text" name="single_typed_admin2" size="50"><font face=verdana size=1 color=gray>&nbsp;(district, county or shire)</td>
</tr>
<tr>
   <td width=250 align=right><font face=verdana size=1>Locality:</font></td>
   <td width=550 align=left><input type="text" name="single_typed_locality" size="50"><font face=verdana size=1 color=gray>&nbsp;(e.g. 12 km NW of Catemaco)</font></td>
</tr>

<tr><td colspan=2>&nbsp;</td></tr>

<tr><td colspan=2 align=center><input type="submit" value="NEXT"></td></tr>

<tr><td colspan=2>&nbsp;</td></tr>
</form>

<form action="format_data.jsp" method=post id=form2 onsubmit="return validateMultiple(form2)">
<tr><td colspan=2><hr width=700></td></tr>
<tr>
   <td width=250 align=right><font face=verdana size=1><b>Paste one or more records <br>below OR upload a file:</b></font></td>
   <td width=550 align=left><font face=verdana size=1><input type="text" name="upload_file" size="30">
   <input type="submit" value="browse"></font></td>
</tr>
<tr><td colspan=2>&nbsp;</td></tr>

<tr>
   <td colspan=2><font face=verdana size=1 color=gray>
   &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
   &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
   Records are limited to 5 fields and must be in the following order: country, admin1, admin2, locality, taxon</font>
   </td>
</tr>

<tr>
   <td colspan=2 align=center>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
<textarea cols=70 name=file_data rows=13>
"Australia","","","2 miles W of Leura","Grevillea buxifolia"
"Australia","","","12 km N of Lake Cargelligo","Grevillea buxifolia"
</textarea>	
   </td>
</tr>

<tr>
   <td colspan=2 align=center><font face=verdana size=1>
   <input type="radio" name="data_structure" value="unstructured"> Unstructured &nbsp;&nbsp;
   <input type="radio" name="data_structure" value="structured" checked> Structured &nbsp;
   <select name="delimiter">
    <option value="none">none
    <option value="white_space">white space
    <option value="tab">tab
    <option value="comma">,
    <option value="colon">:
    <option value="semi_colon">;
    <option value="dash">-
    <option value="forward_slash">/
    <option value="back_slash">\
    <option value="vertical_line">|
  </select>
  <font face=verdana size=1 color=red>*</font>
  </font>
  </td>
</tr>

<tr>
   <td colspan=2 align=center>
   <font face=verdana size=1 color=gray>(Are your data fields separated by a character(s)?)</font>
   </td>
</tr>

<tr><td colspan=2>&nbsp;</td></tr>

<tr><td colspan=2 align=center><input type="submit" value="NEXT"></td></tr>

</table>
</form>
</body>
</center>
</html>
