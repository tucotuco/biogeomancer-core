<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/tags/struts-nested" prefix="nested" %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>

<% 
  String x_min = (String)request.getParameter("x_min");
  if (x_min == null) {
    x_min = "-81";
  }
  String x_max = (String)request.getParameter("x_max");
  if (x_max == null) {
    x_max = "-79";
  }
  String y_min = (String)request.getParameter("y_min");
  if (y_min == null) {
    y_min = "20";
  }
  String y_max = (String)request.getParameter("y_max");
  if (y_max == null) {
    y_max = "22";
  }
%>

<html:html>
  <tiles:insert definition="georeference"/>
  <body bgcolor="white">
    <p><p>
		<bean:message key="georefFormGeoref.title" />
    <p><p>
    <logic:messagesPresent>
      <bean:message key="errors.header" />
      <ul>
        <html:messages id="error" property="byte">
          <li>
            <bean:write name="error" />
          </li>
        </html:messages>
        <html:messages id="error" property="short">
          <li>
            <bean:write name="error" />
          </li>
        </html:messages>
        <html:messages id="error" property="integer">
          <li>
            <bean:write name="error" />
          </li>
        </html:messages>
        <html:messages id="error" property="long">
          <li>
            <bean:write name="error" />
          </li>
        </html:messages>
        <html:messages id="error" property="float">
          <li>
            <bean:write name="error" />
          </li>
        </html:messages>
        <html:messages id="error" property="floatRange">
          <li>
            <bean:write name="error" />
          </li>
        </html:messages>
        <html:messages id="error" property="double">
          <li>
            <bean:write name="error" />
          </li>
        </html:messages>
        <html:messages id="error" property="doubleRange">
          <li>
            <bean:write name="error" />
          </li>
        </html:messages>
        <html:messages id="error" property="date">
          <li>
            <bean:write name="error" />
          </li>
        </html:messages>
        <html:messages id="error" property="creditCard">
          <li>
            <bean:write name="error" />
          </li>
        </html:messages>
      </ul>
      <hr />
    </logic:messagesPresent>
    <html:form action="/georef-submit">
      <html:hidden property="action" />
      <table border="0">
        <tr>
          <th align="left">
            <bean:message key="georefForm.locality.displayname" />
          </th>
          <td align="left">
            <html:text property="locality" size="32" maxlength="51"  />
          </td>
        </tr>
        <tr>
          <th align="left">
            <bean:message key="georefForm.adm1.displayname" />
          </th>
          <td align="left">
            <html:text property="adm1" size="32" maxlength="51" />
          </td>
        </tr>
        <tr>
          <th align="left">
            <bean:message key="georefForm.adm2.displayname" />
          </th>
          <td align="left">
            <html:text property="adm2" size="32" maxlength="51" />
          </td>
        </tr>
        <tr>
          <th align="left">
            <bean:message key="georefForm.taxon.displayname" />
          </th>
          <td align="left">
            <html:text property="taxon" size="32" maxlength="51" />
          </td>
        </tr>
        <tr>
          <th align="left">
            <bean:message key="georefForm.elevation.displayname" />
          </th>
          <td align="left">
            <html:text property="elevation" size="32" maxlength="51"/>
          </td>
        </tr>
        <tr>
          <th align="left">
            <bean:message key="georefForm.longitudeRangeMin.displayname" />
          </th>
          <td align="left">
            <html:text property="longitudeRangeMin" size="32" maxlength="51" value="<%=x_min%>" />
          </td>
        </tr>
        <tr>
          <th align="left">
            <bean:message key="georefForm.latitudeRangeMin.displayname" />
          </th>
          <td align="left">
            <html:text property="latitudeRangeMin" size="32" maxlength="51" value="<%=y_min%>"/>
          </td>
        </tr>
        <tr>
          <th align="left">
            <bean:message key="georefForm.longitudeRangeMax.displayname" />
          </th>
          <td align="left">
            <html:text property="longitudeRangeMax" size="32" maxlength="51" value="<%=x_max%>" />
          </td>
        </tr>
        <tr>
          <th align="left">
            <bean:message key="georefForm.latitudeRangeMax.displayname" />
          </th>
          <td align="left">
            <html:text property="latitudeRangeMax" size="32" maxlength="51" value="<%=y_max%>"/>
          </td>
        </tr>
        <tr>
          <th align="left">
            <bean:message key="georefForm.date.displayname" />
          </th>
          <td align="left">
            <html:text property="date" size="15" maxlength="15" />
          </td>
        </tr>
        <tr>
          <th align="left">
            <bean:message key="georefForm.classifications" />
          </th>
          <td align="left">&#160;</td>
        </tr>
        <nested:iterate property="nameList">
          <tr>
            <th align="left">&#160;</th>
            <td align="left">
              <nested:messagesPresent property="value">
                <br />
                <ul>
                  <nested:messages id="error" property="value">
                    <li>
                      <bean:write name="error" />
                    </li>
                  </nested:messages>
                </ul>
              </nested:messagesPresent>
              <nested:text property="value" size="32" maxlength="51" />
            </td>
          </tr>
        </nested:iterate>
        <tr>
           <td colspan="2"><bean:message key="query_type"/></td>
           <td><bean:message key="query_logic"/></td>
         </tr>
         <!--The values below correspond to Constants.java ints for these keys.-->
        <tr>
          <td>
            <html:select property="queryType" titleKey="query_type" size="8">
              <html:option key="identifier_query" value="0"/>
              <html:option key="code_query" value="1"/>
              <html:option key="place_status_query" value="2"/>
              <html:option key="name_query" value="3"/>
              <html:option key="footprint_query" value="4"/>
              <html:option key="class_query" value="5"/>
              <html:option key="relationship_query" value="6"/>
            </html:select>
          </td>    
          <td>
            <html:select property="queryLogic" titleKey="query_logic" size="8">
              <html:option key="and" value="and_query"/>
              <html:option key="or" value="or_query"/>
              <html:option key="and_not" value="and-not_query"/>
            </html:select>
          </td>
        </tr>

        <tr>
          <td>
            <html:submit property="property" value="Append">
              <bean:message key="button.append" />
            </html:submit>
            <html:submit property="property" value="Georef">
              <bean:message key="button.georef" />
            </html:submit>&#160; 
            <html:submit property="property" value="Reset Records">
              <bean:message key="button.reset.recset" />
            </html:submit>&#160; 
            <html:reset>
              <bean:message key="button.reset" />
            </html:reset>&#160; 
            <html:cancel>
              <bean:message key="button.cancel" />
            </html:cancel>&#160; 
          </td>
        </tr>
      </table>
    </html:form>
  </body>
</html:html>
