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
<form action="mode.jsp" method=post id=form onsubmit="return validateForm(form)">
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
          <td colspan=6 align=center valign=center><font face=verdana size=1><b>Please verify fields:</b></font></td>
       </tr>

       <tr>
          <td colspan=6>&nbsp;</td>
       </tr>

       <tr bgcolor=E3A869>
          <td width=100 align=left valign=center><font face=verdana size=1><b>COLUMN 1</b></font></td>
          <td width=100 align=left valign=center><font face=verdana size=1><b>COLUMN 2</b></font></td>
          <td width=100 align=left valign=center><font face=verdana size=1><b>COLUMN 3</b></font></td>
          <td width=200 align=left valign=center><font face=verdana size=1><b>COLUMN 4</b></font></td>
          <td width=100 align=left valign=center><font face=verdana size=1><b>COLUMN 5</b></font></td>
       </tr>

       <tr bgcolor=E3A869>
          <td width=100 align=left valign=center>
            <select name="column1">
             <option value="ignore">ignore
             <option value="country" selected>Country
             <option value="admin1">Admin 1
             <option value="admin2">Admin 2
             <option value="admin3">Admin 3
             <option value="locality">Locality
             <option value="taxon">Taxon
             <option value="x">X
             <option value="y">Y
            </select>
          </td>
          <td width=100 align=left valign=center>
            <select name="column2">
             <option value="ignore">ignore
             <option value="country">Country
             <option value="admin1" selected>Admin 1
             <option value="admin2">Admin 2
             <option value="admin3">Admin 3
             <option value="locality">Locality
             <option value="taxon">Taxon
             <option value="x">X
             <option value="y">Y
            </select>
          </td>
          <td width=100 align=left valign=center>
            <select name="column3">
             <option value="ignore">ignore
             <option value="country">Country
             <option value="admin1">Admin 1
             <option value="admin2" selected>Admin 2
             <option value="admin3">Admin 3
             <option value="locality">Locality
             <option value="taxon">Taxon
             <option value="x">X
             <option value="y">Y
            </select>
          </td>
          <td width=200 align=left valign=center>
            <select name="column4">
             <option value="ignore">ignore
             <option value="country">Country
             <option value="admin1">Admin 1
             <option value="admin2">Admin 2
             <option value="admin3">Admin 3
             <option value="locality" selected>Locality
             <option value="taxon">Taxon
             <option value="x">X
             <option value="y">Y
            </select>
          </td>
          <td width=100 align=left valign=center>
            <select name="column5">
             <option value="ignore">ignore
             <option value="country">Country
             <option value="admin1">Admin 1
             <option value="admin2">Admin 2
             <option value="admin3">Admin 3
             <option value="locality">Locality
             <option value="taxon" selected>Taxon
             <option value="x">X
             <option value="y">Y
            </select>
          </td>
	<tr>
  	     <td align=left><font face=verdana size=1>Australia</font></td>
             <td align=left><font face=verdana size=1>&nbsp;</font></td>
             <td align=left><font face=verdana size=1>&nbsp;</font></td>
             <td align=left><font face=verdana size=1>2 miles W of Leura</font></td>
             <td align=left><font face=verdana size=1>Grevillea buxifolia</font></td>
	</tr>
	<tr bgcolor=FEF0DB>
  	     <td align=left><font face=verdana size=1>Australia</font></td>
             <td align=left><font face=verdana size=1>&nbsp;</font></td>
             <td align=left><font face=verdana size=1>&nbsp;</font></td>
             <td align=left><font face=verdana size=1>12 km N of Lake Cargelligo</font></td>
             <td align=left><font face=verdana size=1>Grevillea buxifolia</font></td>
	</tr>
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
</form>
</body>
</center>
</html>
