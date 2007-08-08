<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<CENTER>
  <TABLE width=800 align=center units="pixels">
    <TBODY>
      <TR>
        <TD vAlign=center align=middle width=800>
          <TABLE width=800 align=center units="pixels">
            <TBODY>
              <TR>
                <TD vAlign=center align=middle width=800>
                  <html:image page="/images/logo/BGLogoHalf.jpg" >
                  </html:image>
                </TD>
              </TR>
            </TBODY>
          </TABLE>
        </TD>
      </TR>
      <TR>
        <TD vAlign=center align=middle width=800 bgColor=#7B3F00>

          <A style="TEXT-DECORATION: none"
          href="/biogeomancer_<bean:message key="user"/>/pages/upload/upload.jsp">
          <FONT face=verdana color=white size=1><B>IMPORT DATA&nbsp;&nbsp;>&nbsp;&nbsp;</B></FONT></A> 

          <A style="TEXT-DECORATION: none" href="/biogeomancer_<bean:message key="user"/>/pages/workbench_dev/format_data.jsp">
          <FONT face=verdana color=#FFFFFF size=1><B>FORMAT DATA&nbsp;&nbsp;>&nbsp;&nbsp;</B></FONT></A> 

          <A style="TEXT-DECORATION: none"
          href="/biogeomancer_<bean:message key="user"/>/pages/workbench_dev/mode.jsp">
          <FONT face=verdana color=#FFFFFF size=1><B>SELECT MODE&nbsp;&nbsp;>&nbsp;&nbsp;</B></FONT></A>

          <A style="TEXT-DECORATION: none"
          href="/biogeomancer_<bean:message key="user"/>/pages/workbench_dev/parse_georef_data.jsp">
          <FONT face=verdana color=#FFFFFF size=1><B>PARSE/GEOREFERENCE&nbsp;&nbsp;>&nbsp;&nbsp;</B></FONT></A>

          <A style="TEXT-DECORATION: none" 
          href="/biogeomancer_<bean:message key="user"/>/pages/workbench_dev/map_data.jsp">
          <FONT face=verdana color=#FFFFFF size=1><B>MAP DATA&nbsp;&nbsp;>&nbsp;&nbsp;</B></FONT></A> 
           
        </TD>
      </TR>
    </TBODY>
  </TABLE>
</CENTER>
