<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/tags/struts-tiles" prefix="tiles" %>
<%@ page import="org.apache.struts.action.*,
                 java.util.Iterator, java.util.ArrayList,
                 bg.struts.forms.*,
                 org.apache.struts.Globals" %>
<% 
   ArrayList recs = (ArrayList) request.getAttribute("recs"); 
   String[] recElems = null;
%>

<!-- THIS PAGE REQUIRES THE FOLLOWING BACKEND CODE/FUTURE CHANGES -->
<!-- 1. Code to handle communication between the recordset and georef/parsing engine -->
<!-- 2. Code to handle the field and delimeter selection -->
<!-- 3. Build 2: need to change the field designation form to allow as many fields as the user wants-->

<html>
<head>
     <title>BioGeomancer</title>

     <script language="Javascript">
       function validateForm(form){
	      var id = 0;
	      var country = 0;
	      var admin1 = 0;
	      var admin2 = 0;
	      var admin = 0;
	      var locality = 0;
	      var taxon = 0;
	      var x = 0;
	      var y = 0;

              if (form.column1.value == "id"){id = id + 1;}
              if (form.column2.value == "id"){id = id + 1;}
              if (form.column3.value == "id"){id = id + 1;}
              if (form.column4.value == "id"){id = id + 1;}
              if (form.column5.value == "id"){id = id + 1;}

              if (form.column1.value == "country"){country = country + 1;}
              if (form.column2.value == "country"){country = country + 1;}
              if (form.column3.value == "country"){country = country + 1;}
              if (form.column4.value == "country"){country = country + 1;}
              if (form.column5.value == "country"){country = country + 1;}

              if (form.column1.value == "admin1"){admin1 = admin1 + 1;}
              if (form.column2.value == "admin1"){admin1 = admin1 + 1;}
              if (form.column3.value == "admin1"){admin1 = admin1 + 1;}
              if (form.column4.value == "admin1"){admin1 = admin1 + 1;}
              if (form.column5.value == "admin1"){admin1 = admin1 + 1;}

              if (form.column1.value == "admin2"){admin2 = admin2 + 1;}
              if (form.column2.value == "admin2"){admin2 = admin2 + 1;}
              if (form.column3.value == "admin2"){admin2 = admin2 + 1;}
              if (form.column4.value == "admin2"){admin2 = admin2 + 1;}
              if (form.column5.value == "admin2"){admin2 = admin2 + 1;}

              if (form.column1.value == "admin3"){admin3 = admin3 + 1;}
              if (form.column2.value == "admin3"){admin3 = admin3 + 1;}
              if (form.column3.value == "admin3"){admin3 = admin3 + 1;}
              if (form.column4.value == "admin3"){admin3 = admin3 + 1;}
              if (form.column5.value == "admin3"){admin3 = admin3 + 1;}

              if (form.column1.value == "locality"){locality = locality + 1;}
              if (form.column2.value == "locality"){locality = locality + 1;}
              if (form.column3.value == "locality"){locality = locality + 1;}
              if (form.column4.value == "locality"){locality = locality + 1;}
              if (form.column5.value == "locality"){locality = locality + 1;}

              if (form.column1.value == "x"){x = x + 1;}
              if (form.column2.value == "x"){x = x + 1;}
              if (form.column3.value == "x"){x = x + 1;}
              if (form.column4.value == "x"){x = x + 1;}
              if (form.column5.value == "x"){x = x + 1;}

              if (form.column1.value == "y"){y = y + 1;}
              if (form.column2.value == "y"){y = y + 1;}
              if (form.column3.value == "y"){y = y + 1;}
              if (form.column4.value == "y"){y = y + 1;}
              if (form.column5.value == "y"){y = y + 1;}

              if (form.column1.value == "taxon"){taxon = taxon + 1;}
              if (form.column2.value == "taxon"){taxon = taxon + 1;}
              if (form.column3.value == "taxon"){taxon = taxon + 1;}
              if (form.column4.value == "taxon"){taxon = taxon + 1;}
              if (form.column5.value == "taxon"){taxon = taxon + 1;}

              if ((form.column1.value == "none") && (form.column2.value == "none") && (form.column3.value == "none") && 
                  (form.column4.value == "none") && (form.column5.value == "none")){
		  alert ('You must designate at least one column header.');
		  return false;}	          

              if ((id > 1) || (country > 1) || (admin1 > 1) || (admin2 > 1) || (locality > 1) && (taxon > 1)){
		  	        alert('Column headers cannot be duplicated.');
				return false;}

              return true;
       }
     </script>
</head>
<center>
<body>
<form action="format-submit.do?queryParam=Successful" method=post id=form onsubmit="return validateForm(form)">
<table units=pixels width=800 align=center cellspacing=1>

<tr>
   <td width=800 align=center valign=center>
     <table units=pixels width=800 align=center>
        <tr><td align=center valign=center width=800>
        <img src="/biogeomancer_<bean:message key="user"/>/images/BGLogoHalf.jpg" units=pixels></img>
        </td></tr>
     </table>
   </td>
</tr>

<tr bgcolor=7B3F00>
   <td width=800 align=center bgcolor=7B3F0>
<a href="workbench.jsp" style="text-decoration:none"><font face=verdana size=1 color=FFA54F><b>IMPORT DATA&nbsp;&nbsp;>&nbsp;&nbsp; </b></font></a>
<a href="format_data.jsp" style="text-decoration:none"><font face=verdana size=1 color=FFA54F><b>FORMAT DATA&nbsp;&nbsp;>&nbsp;&nbsp; </b></font></a>
<font face=verdana size=1 color=white><b>SELECT MODE&nbsp;&nbsp;>&nbsp;&nbsp; </b></font>
<font face=verdana size=1 color=white><b>PARSE/GEOREFERENCE&nbsp;&nbsp;>&nbsp;&nbsp;</b></font>
<font face=verdana size=1 color=white><b>MAP</b></font>
   </td>
</tr>

<tr>
   <td width=800 align=left valign=top><br><br>
     <table width=600 unit=pixels align=center cellspacing=0>
       <tr>
          <td colspan=9 align=center valign=center><font face=verdana size=1><b>Please verify fields:</b></font></td>
       </tr>

       <tr>
          <td colspan=9>&nbsp;</td>
       </tr>

       <tr bgcolor=E3A869>
          <td align=left valign=center><font face=verdana size=1><b>COLUMN 1</b></font></td>
          <td align=left valign=center><font face=verdana size=1><b>COLUMN 2</b></font></td>
          <td align=left valign=center><font face=verdana size=1><b>COLUMN 3</b></font></td>
          <td align=left valign=center><font face=verdana size=1><b>COLUMN 4</b></font></td>
          <td align=left valign=center><font face=verdana size=1><b>COLUMN 5</b></font></td>
          <td align=left valign=center><font face=verdana size=1><b>COLUMN 6</b></font></td>
          <td align=left valign=center><font face=verdana size=1><b>COLUMN 7</b></font></td>
          <td align=left valign=center><font face=verdana size=1><b>COLUMN 8</b></font></td>
          <td align=left valign=center><font face=verdana size=1><b>COLUMN 9</b></font></td>
          <td align=left valign=center><font face=verdana size=1><b>COLUMN 10</b></font></td>		  		  
       </tr>

       <tr bgcolor=E3A869>
          <td align=left valign=center>
            <select name="column1">
             <option value="ignore">
             <option value="country">Country
             <option value="admin1">Admin 1
             <option value="admin2">Admin 2
             <option value="admin3">Admin 3
             <option value="locality">Locality
             <option value="taxon">Taxon
             <option value="x">X
             <option value="y">Y
            </select>
          </td>
          <td align=left valign=center>
            <select name="column2">
             <option value="ignore">
             <option value="country">Country
             <option value="admin1">Admin 1
             <option value="admin2">Admin 2
             <option value="admin3">Admin 3
             <option value="locality">Locality
             <option value="taxon">Taxon
             <option value="x">X
             <option value="y">Y
            </select>
          </td>
          <td align=left valign=center>
            <select name="column3">
             <option value="ignore">
             <option value="country">Country
             <option value="admin1">Admin 1
             <option value="admin2">Admin 2
             <option value="admin3">Admin 3
             <option value="locality">Locality
             <option value="taxon">Taxon
             <option value="x">X
             <option value="y">Y
            </select>
          </td>
          <td align=left valign=center>
            <select name="column4">
             <option value="ignore">
             <option value="country">Country
             <option value="admin1">Admin 1
             <option value="admin2">Admin 2
             <option value="admin3">Admin 3
             <option value="locality">Locality
             <option value="taxon">Taxon
             <option value="x">X
             <option value="y">Y
            </select>
          </td>
          <td align=left valign=center>
            <select name="column5">
             <option value="ignore">
             <option value="country">Country
             <option value="admin1">Admin 1
             <option value="admin2">Admin 2
             <option value="admin3">Admin 3
             <option value="locality">Locality
             <option value="taxon">Taxon
             <option value="x">X
             <option value="y">Y
            </select>
          </td>
         <td align=left valign=center>
            <select name="column6">
             <option value="ignore">
             <option value="country">Country
             <option value="admin1">Admin 1
             <option value="admin2">Admin 2
             <option value="admin3">Admin 3
             <option value="locality">Locality
             <option value="taxon">Taxon
             <option value="x">X
             <option value="y">Y
            </select>
          </td>
          <td align=left valign=center>
            <select name="column7">
             <option value="ignore">
             <option value="country">Country
             <option value="admin1">Admin 1
             <option value="admin2">Admin 2
             <option value="admin3">Admin 3
             <option value="locality">Locality
             <option value="taxon">Taxon
             <option value="x">X
             <option value="y">Y
            </select>
          </td>
          <td align=left valign=center>
            <select name="column8">
             <option value="ignore">
             <option value="country">Country
             <option value="admin1">Admin 1
             <option value="admin2">Admin 2
             <option value="admin3">Admin 3
             <option value="locality">Locality
             <option value="taxon">Taxon
             <option value="x">X
             <option value="y">Y
            </select>
          </td>
          <td align=left valign=center>
            <select name="column9">
             <option value="ignore">
             <option value="country">Country
             <option value="admin1">Admin 1
             <option value="admin2">Admin 2
             <option value="admin3">Admin 3
             <option value="locality">Locality
             <option value="taxon">Taxon
             <option value="x">X
             <option value="y">Y
            </select>
          </td>
          <td align=left valign=center>
            <select name="column10">
             <option value="ignore">
             <option value="country">Country
             <option value="admin1">Admin 1
             <option value="admin2">Admin 2
             <option value="admin3">Admin 3
             <option value="locality">Locality
             <option value="taxon">Taxon
             <option value="x">X
             <option value="y">Y
            </select>
          </td>		  		  
	</tr>
	<% 
	for (int i = 0; i < recs.size(); i++) {
	  if (i==1 || i==3 || i == 5 || i == 7 || i == 9) {
	    out.println("<tr>");
	  } else {
	    out.println("<tr bgcolor=FEF0DB>");
	  }
	  recElems = (String[]) recs.get(i);

				 for (int j = 0; j < recElems.length; j++) {				  
				  out.println("<td align=left><font face=verdana size=1>" + recElems[j] + "</font></td>");
				  if (j==9) break;
				 }
	  out.println("</tr>");
	}			 
    %>

     </table>
  </td>
</tr>

<tr>
   <td>&nbsp;</td>
</tr>

<!-- ON SUBMISSION SPARSE VALIDATION OF DATA (I.E. COUNTRY NAMES) AND FEEDBACK TO USER -->

<tr>
   <td colspan=2 align=center valign=center><br><input type="submit" value="NEXT"></td>
</tr>
</table>
<input type="hidden" name="recordsfilename" value="<%= request.getAttribute("recordsfilename") %>">

</form>

</body>
</center>
</html>


