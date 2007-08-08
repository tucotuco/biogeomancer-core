<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/tags/struts-tiles" prefix="tiles" %>
<%@ page import="org.apache.struts.action.*,
                 java.util.*,      
                 org.apache.struts.Globals" %>
<%@ page import="bg.records.*" %>
<%
        RecSet recset = (RecSet) request.getAttribute("recset"); 
        Rec r = null;;
	    List recs = recset.recs;
	    List clauses = null;
	    ClauseSet cs = null;
	    List cs_l = null;
	    Clause clause = null;
        Iterator eiterator = recs.iterator();
        Iterator citerator = null;
        FeatureInfo fi = null;
        String vLocality = null;
        String cLocality = null;
%>


            
<html>
<head>
     <title>BioGeomancer</title>
</head>
<center>
<body>
<form action="map-submit.do?queryParam=Successful" method=post id=form>
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
	<table units=pixels width=800 align=center cellspacing=0>
	<tr bgcolor=E3A869>
	<td align=left width=60><font face=verdana size=1 color=7B3F00><b>COUNTRY</b></font></td>
	<td align=left width=60><font face=verdana size=1 color=7B3F00><b>ADMIN1</b></font></td>
	<td align=left width=60><font face=verdana size=1 color=7B3F00><b>ADMIN2</b></font></td>
	<td align=left width=100><font face=verdana size=1 color=7B3F00><b>LOCALITY</b></font></td>
	<td align=left width=100><font face=verdana size=1 color=7B3F00><b>CLAUSE</b></font></td>	
	<td align=left width=100><font face=verdana size=1 color=7B3F00><b>TAXON</b></font></td>
	<td align=left width=50><font face=verdana size=1 color=7B3F00><b>LAT</b></font></td>
	<td align=left width=50><font face=verdana size=1 color=7B3F00><b>LONG</b></font></td>
	<td align=left width=75><font face=verdana size=1 color=7B3F00><b>UNCERTAINTY</b></font></td>

	</tr>	


<%   
	    while(eiterator.hasNext()) {
	      r = (Rec) eiterator.next();
	      vLocality = r.vLocality;
	      cs_l = r.clauseSets;
	      citerator = cs_l.iterator();
			
	      while(citerator.hasNext()){
	        try {

	          cs = (ClauseSet) citerator.next();
	          clauses = cs.clauses;
	          
	          for (int i=0; i < clauses.size(); i++) {
                try {
                  clause = (Clause) clauses.get(i);
                  fi = clause.spatialDescription;
                  cLocality = clause.featureName1.name;
                  float lon = fi.longitude;
                  float lat = fi.latitude;

                  out.println("<tr bgcolor=FEF0DB>");
                  out.println("<td align=left><font face=verdana size=1 color=blue></font></td>");
                  out.println("<td align=left bgcolor=FEF0DB><font face=verdana size=1 color=blue></font></td>");
                  out.println("<td align=left bgcolor=FEF0DB><font face=verdana size=1 color=blue></font></td>");
                  out.println("<td align=left bgcolor=FEF0DB><font face=verdana size=1 color=blue>" + vLocality + "</font></td>");                  
                  out.println("<td align=left bgcolor=FEF0DB><font face=verdana size=1 color=blue>" + cLocality + "</font></td>");
                  out.println("<td align=left bgcolor=FEF0DB><font face=verdana size=1 color=blue>Taxon not supplied.</font></td>");
                  out.println("<td align=left bgcolor=FEF0DB><font face=verdana size=1 color=blue>" + lat + "</font></td>");
                  out.println("<td align=left bgcolor=FEF0DB><font face=verdana size=1 color=blue>" + lon + "</font></td>");
                  out.println("<td align=left bgcolor=FEF0DB><font face=verdana size=1 color=blue>Unknown</font></td>");
                  out.println("</tr>");
                } catch (Exception ex) {
                  System.out.println("table.jsp exception:");
                }
              } //end for 
            } catch (Exception e) {
              System.out.println("table.jsp exception:");
            }
            
          } //end while
          
        } //end while

%>


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



</table>
</form>
</body>
</center>
</html>
