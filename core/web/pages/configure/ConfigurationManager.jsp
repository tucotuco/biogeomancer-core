<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="org.apache.struts.validator.ValidatorPlugIn" session="true" %>
<%@ page import="org.apache.struts.Globals" %>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>

<html:html locale="true">
<tiles:insert definition="configure"/>
<body bgcolor="white">

<logic:notPresent name="<%= Globals.MESSAGES_KEY %>" >
  <font color="red">
    ERROR:  Application resources not loaded -- check servlet container
    logs for error messages.
  </font>
</logic:notPresent>

<%-- :TODO: Need code to do this with moudles
<logic:notPresent name="<%= ValidatorPlugIn.VALIDATOR_KEY %>" >
  <font color="red">
    ERROR:  Validator resources not loaded -- check Commons Logging
    logs for error messages.
  </font>
</logic:notPresent>
--%>

<h3><bean:message key="registrationForm.title"/></h3>
<ul>
   <li><html:link action="/registration"><bean:message key="registrationForm.title"/></html:link></li>
   <!-- :TODO: Should have a non-JaveScript message-by-field example -->
   <li>
      <html:link action="/jsRegistration"><bean:message key="jsRegistrationForm.title"/></html:link> -
      <bean:message key="jsRegistrationForm.description"/>
   </li>
   <li>
      <html:link action="/multiRegistration"><bean:message key="multiRegistrationForm.title"/></html:link> -
      <bean:message key="multiRegistrationForm.description"/>
   </li>
</ul>

<p>&nbsp;</p>

<h3><bean:message key="parseConfig.title"/></h3>
<ul>
   <li>
      <html:link action="/parseConfig"><bean:message key="parseConfig.title"/></html:link> -
      <bean:message key="parseConfig.description"/>
   </li>
</ul>

<p>&nbsp;</p>

<h3>Change Language | Changez Le Langage</h3>
<ul>
   <li><html:link action="/locale?language=en">English | Anglais</html:link></li>
   <li>
      <html:link action="/locale?language=fr">French | Francais</html:link> -
      <bean:message key="localeForm.fr"/>
   </li>
   <li>
      <html:link action="/locale?language=fr&country=CA">French Canadian | Francais Canadien</html:link> -
      <bean:message key="localeForm.frCA"/>
   </li>
   <li>
      <html:link action="/locale?language=ja" useLocalEncoding="true">Japanese | Japonais</html:link> -
      <bean:message key="localeForm.ja"/>
   </li>
</ul>

<p>&nbsp;</p>

<html:link action="/Welcome"> <bean:message key="button.cancel"/> </html:link>

</body>
</html:html>
