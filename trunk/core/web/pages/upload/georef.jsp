<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/tags/struts-tiles" prefix="tiles" %>
<%@ page import="org.apache.struts.action.*,
                 java.util.Iterator,
                 edu.colorado.struts.webapp.format.FormatForm, 
                 org.apache.struts.Globals" %>
                 
<!-- BACKEND CODE AND FUTURE CHANGES REQUIRED-->
<!-- Need the appropriate engines selections -->
<!-- Need a backend object to handle the mode selection and georef/parsing engine selections if selected -->
<!-- Need to pass the recordset object to the georeferencing/parsing engines so that the results can be displayed on the next page -->

<html>
<head>
     <title>BioGeomancer</title>
     <script language="Javascript">
        function validateCheckboxes(form){
	 var checkbox_choices=0;
	 for (counter = 0; counter < form.gazetteer.length; counter++){
	   if(form.gazetteer[counter].checked){
	     checkbox_choices = checkbox_choices + 1;}
	 }

         if (checkbox_choices == 0){
	    alert('You must select at least one gazetteer.');
	    return false;}

	 return true;
        }
     </script>



</head>
<center>
<body>
<form action="georef-submit.do?queryParam=Successful" method=post id=form onclick="return validateCheckboxes(form)">
<table units=pixels width=800 align=center cellspacing=0>

<tr>
   <td width=800 align=center valign=center colspan=2>
     <table units=pixels width=800 align=center>
        <tr><td align=center valign=center width=800>
        <img src="/biogeomancer_<bean:message key="user"/>/images/BGLogoHalf.jpg" units=pixels></img>
        </td></tr>
     </table>

   </td>
</tr>

<tr>
  <td width=800 align=center bgcolor=7B3F00 colspan=2>
<a href="workbench.jsp" style="text-decoration:none"><font face=verdana size=1 color=FFA54F><b>IMPORT DATA&nbsp;&nbsp;>&nbsp;&nbsp; </b></font></a> 
<a href="format_data.jsp" style="text-decoration:none"><font face=verdana size=1 color=FFA54F><b>FORMAT DATA&nbsp;&nbsp;>&nbsp;&nbsp; </b></font></a>
<a href="mode.jsp" style="text-decoration:none"><font face=verdana size=1 color=FFA54F><b>SELECT MODE&nbsp;&nbsp;>&nbsp;&nbsp; </b></font></a>
<font face=verdana size=1 color=white><b>PARSE/GEOREFERENCE&nbsp;&nbsp;>&nbsp;&nbsp;</b></font>
<font face=verdana size=1 color=white><b>MAP</b></font> 
</td>
</tr>
   
<tr><td colspan=2>&nbsp;</td></tr>

<tr bgcolor=FEF0DB><td colspan=2>&nbsp;</td></tr>

<tr bgcolor=FEF0DB>
   <td align=center valign=center colspan=2>
   <input type="radio" name="mode" value="default" checked>&nbsp;<font face=verdana size=1><b>Use default settings</b></font>
   </td>
</tr>

<tr bgcolor=FEF0DB><td colspan=2>&nbsp;</td></tr>

<tr><td align=center colspan=2><br><font face=verdana size=1><b>or</b></font><br><br></td></tr>

<tr bgcolor=FEF0DB><td colspan=2>&nbsp;</td></tr>

<tr bgcolor=FEF0DB>
   <td align=center valign=center colspan=2>
     <input type="radio" name="mode" value="manual">&nbsp;<font face=verdana size=1><b>Specify settings</b></font>
   </td>
</tr>

<tr bgcolor=FEF0DB><td colspan=2>&nbsp;</td></tr>

<tr bgcolor=FEF0DB>
  <td>
   <table units=pixels width=600 align=center cellspacing=0> 
     <tr>
	<td colspan=2 width=200 align=center><font face=verdana size=1><b>Gazetteer</b></font></td>
	<td colspan=2 width=200 align=center><font face=verdana size=1><b>GeoParsing Engine</b></font></td>
	<td colspan=2 width=200 align=center><font face=verdana size=1><b>Validation Engine</b></font></td>
     </tr>
     <tr>
	<td width=100 align=right><input type="checkbox" name="gazetteer" value="adl" checked></td>
	<td width=100 align=left><font face=verdana size=1>ADL</font></td>
	<td width=100 align=right><input type="radio" name="geoparse_engine" value="yale" checked></td>
	<td width=100 align=left><font face=verdana size=1>Yale</font></td>
	<td width=100 align=right><input type="radio" name="validation_engine" value="cria" checked></td>
	<td width=100 align=left><font face=verdana size=1>CRIA Engine</td>
     </tr>
     <tr>
	<td width=100 align=right><input type="checkbox" name="gazetteer" value="getty"></td>
	<td width=100 align=left><font face=verdana size=1>Getty</font></td>
	<td width=100 align=right><input type="radio" name="geoparse_engine" value="tulane"></td>
	<td width=100 align=left><font face=verdana size=1>Tulane</font></td>
	<td width=100 align=right>&nbsp;</td>
	<td width=100 align=left><font face=verdana size=1>&nbsp;</td>
     </tr>
     <tr>
	<td width=100 align=right><input type="checkbox" name="gazetteer" value="biogeomancer"></td>
	<td width=100 align=left><font face=verdana size=1>BioGeomancer</font></td>
	<td width=100 align=right><input type="radio" name="geoparse_engine" value="d2k"></td>
	<td width=100 align=left><font face=verdana size=1>D2K</font></td>
	<td width=100 align=right>&nbsp;</td>
	<td width=100 align=left><font face=verdana size=1>&nbsp;</td>
     </tr>
     <tr>
	<td width=100 align=right>&nbsp;</td>
	<td width=100 align=left>&nbsp;</td>
	<td width=100 align=right><input type="radio" name="geoparse_engine" value="inxight"></td>
	<td width=100 align=left><font face=verdana size=1>Inxight</font></td>
	<td width=100 align=right>&nbsp;</td>
	<td width=100 align=left>&nbsp;</td>
     </tr>
   </table>
  </td>
</tr>

<tr bgcolor=FEF0DB>
   <td align=right valign=center colspan=2>&nbsp;</td>
</tr>

<tr>
   <td colspan=2>&nbsp;</td>
</tr>

<tr>
   <td colspan=2 align=center><input type="submit" value="NEXT"></td>
</tr>
</table>
<input type="hidden" name="recordsfilename" value="<%= request.getAttribute("recordsfilename") %>">
<input type="hidden" name="locality" value="<%= request.getAttribute("locality") %>">
<input type="hidden" name="admin1" value="<%= request.getAttribute("admin1") %>">
<input type="hidden" name="admin2" value="<%= request.getAttribute("admin2") %>">
<input type="hidden" name="admin3" value="<%= request.getAttribute("admin3") %>">
<input type="hidden" name="country" value="<%= request.getAttribute("country") %>">
</form>
</body>
</center>
</html>
