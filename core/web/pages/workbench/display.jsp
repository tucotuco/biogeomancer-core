<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/tags/struts-tiles" prefix="tiles" %>
<%@ page import="org.apache.struts.action.*,
                 java.util.Iterator,
                 edu.colorado.struts.webapp.upload.UploadForm, 
                 org.apache.struts.Globals" %>

<html>
  <!--tiles:insert definition="display"/ -->
  <head>
       <title>BioGeomancer</title>
     
  </head>
  <body>
    <logic:messagesPresent>
       <ul>
       <html:messages id="error">
          <li><bean:write name="error"/></li>
       </html:messages>
       </ul><hr />
    </logic:messagesPresent>
    
<table width=600 unit=pixels align=center>
<tr>
   <td width=800 align=center valign=center colspan=2>
     <table units=pixels width=800 align=center>
        <tr><td align=center valign=center width=800>
        <img src="/biogeomancer_<bean:message key="user"/>/images/BGLogoHalf.jpg" units=pixels></img>
        </td></tr>
     </table>
   </td>
</tr>
<tr><td>
<p>
<b>Email Address:</b>&nbsp;<%= request.getAttribute("text") %>
</p>

<p>
<b>The number of records uploaded:</b>&nbsp;<%= request.getAttribute("numrecs") %>
</p>
<p>
<b>Assigned unique id:</b>&nbsp;<%= request.getAttribute("uid") %>
</p>
<p>
<b>To map and edit the position of the records click <A HREF='/biogeomancer_<bean:message key="user"/>/sea/client/index.jsp?uid=<%= request.getAttribute("uid") %>' TARGET='_self'>here.</A></b>
</p>
</td>
</tr>
</table>

</body>
</html>