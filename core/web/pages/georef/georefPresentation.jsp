<%@ page contentType="text/html"%>

<%-- $Id: repeater.jsp,v 1.1.2.6 2004/02/12 03:27:54 jbenoit Exp $ --%>
<%@ taglib uri="http://java.sun.com/jsf/core"  prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsf/html"  prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/demo/components" prefix="d" %>
<%@ taglib uri="/tags/struts-tiles" prefix="tiles" %>

<f:view>
<html>
<head>
  <title>Georeferencing Results:</title>
</head>
<body bgcolor="#146D14">
<h:messages        globalOnly="true"/>

<h:form id="georef_presentation">

  <d:data_repeater         id="table"
    binding="#{GeorefBean.data}" 
	  rows="12"
    value="#{GeorefBean.records}"
    var="record">

    <f:facet           name="header">
      <h:outputText    value="Georeferencing Results:"/>
    </f:facet>

    <h:column>
      <%-- Visible checkbox for selection --%>
      <h:selectBooleanCheckbox
        id="checked"
        binding="#{GeorefBean.checked}"/>
      <%-- Invisible checkbox for "created" flag --%>
      <h:selectBooleanCheckbox
        id="created"
        binding="#{GeorefBean.created}"
        rendered="false"/>
    </h:column>

    <h:column>
      <f:facet  name="header">
        <h:outputText  value="Name"/>
      </f:facet>
      <h:inputText         id="locality"
                      binding="#{GeorefBean.locality}"
                     required="true"
                         size="55"
                        value="#{record.locality}">
      </h:inputText>
      <h:message          for="locality"/>
    </h:column>

    <h:column>
      <f:facet           name="header">
        <h:outputText  value="longitude"/>
      </f:facet>
      <h:inputText        id="longitude"
                     required="true"
                         size="15"
                        value="#{record.longitude}">
      </h:inputText>
      <h:message          for="longitude"/>
    </h:column>

    <h:column>
      <f:facet           name="header">
        <h:outputText  value="latitude"/>
      </f:facet>
      <h:inputText        id="latitude"
                     required="true"
                         size="15"
                        value="#{record.latitude}">
        <f:validateLength
                      maximum="15"
                      minimum="2"/>
      </h:inputText>
      <h:message          for="latitude"/>
    </h:column>
    
  </d:data_repeater>
    <h:commandButton        id="create"
                    action="#{GeorefBean.create}"
                    immediate="false"
                        value="Create New Row"
                         type="SUBMIT"/>

  <h:commandButton        id="delete"
                    action="#{GeorefBean.delete}"
                    immediate="false"
                        value="Delete Checked"
                         type="SUBMIT"/>

  <h:commandButton        id="first"
                    action="#{GeorefBean.first}"
                    immediate="true"
                        value="First Page"
                         type="SUBMIT"/>

  <h:commandButton        id="last"
                    action="#{GeorefBean.last}"
                    immediate="true"
                        value="Last Page"
                         type="SUBMIT"/>

  <h:commandButton        id="next"
                    action="#{GeorefBean.next}"
                    immediate="true"
                        value="Next Page"
                         type="SUBMIT"/>

  <h:commandButton        id="previous"
                    action="#{GeorefBean.previous}"
                    immediate="true"
                        value="Prev Page"
                         type="SUBMIT"/>

  <h:commandButton        id="reset"
                    action="#{GeorefBean.reset}"
                    immediate="true"
                        value="Reset Changes"
                         type="SUBMIT"/>

  <h:commandButton        id="update"
                    action="#{GeorefBean.update}"
                    immediate="false"
                        value="Save Changes"
                         type="SUBMIT"/>
                         
  <h:commandButton        id="cancel"
                    action="#{GeorefBean.cancel}"
                    immediate="true"
                        value="Cancel"
                         type="SUBMIT"/>

</h:form>

  </BODY>
</HTML>
</f:view>
