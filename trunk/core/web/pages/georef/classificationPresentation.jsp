<%@ page contentType="text/html"%>

<%-- $Id: repeater.jsp,v 1.1.2.6 2004/02/12 03:27:54 jbenoit Exp $ --%>
<%@ taglib uri="http://java.sun.com/jsf/core"  prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsf/html"  prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/demo/components" prefix="d" %>
<%@ taglib uri="/tags/struts-tiles" prefix="tiles" %>

<f:view>
<html>
<head>
  <title>Classification Results:</title>
</head>
  <body bgcolor="#146D14">
    <h:messages        globalOnly="true"/>

    <h:form id="classification_presentation">

      <d:data_repeater         id="table"
        binding="#{ClassificationBean.data}" 
        rows="12"
        value="#{ClassificationBean.records}"
        var="record">

        <f:facet           name="header">
          <h:outputText    value="Classification Results:"/>
        </f:facet>

        <h:column>
          <%-- Visible checkbox for selection --%>
          <h:selectBooleanCheckbox
            id="checked"
            binding="#{ClassificationBean.checked}"/>
          <%-- Invisible checkbox for "created" flag --%>
          <h:selectBooleanCheckbox
            id="created"
            binding="#{ClassificationBean.created}"
            rendered="false"/>
        </h:column>

        <h:column>
          <f:facet  name="header">
            <h:outputText  value="Classification"/>
          </f:facet>
          <h:inputText         id="classification"
                          binding="#{ClassificationBean.classification}"
                         required="true"
                             size="55"
                            value="#{record.classification}">
          </h:inputText>
          <h:message          for="locality"/>
        </h:column>

      </d:data_repeater>
        <h:commandButton        id="create"
                        action="#{ClassificationBean.create}"
                        immediate="false"
                            value="Create New Row"
                             type="SUBMIT"/>

      <h:commandButton        id="delete"
                        action="#{ClassificationBean.delete}"
                        immediate="false"
                            value="Delete Checked"
                             type="SUBMIT"/>

      <h:commandButton        id="first"
                        action="#{ClassificationBean.first}"
                        immediate="true"
                            value="First Page"
                             type="SUBMIT"/>

      <h:commandButton        id="last"
                        action="#{ClassificationBean.last}"
                        immediate="true"
                            value="Last Page"
                             type="SUBMIT"/>

      <h:commandButton        id="next"
                        action="#{ClassificationBean.next}"
                        immediate="true"
                            value="Next Page"
                             type="SUBMIT"/>

      <h:commandButton        id="previous"
                        action="#{ClassificationBean.previous}"
                        immediate="true"
                            value="Prev Page"
                             type="SUBMIT"/>

      <h:commandButton        id="reset"
                        action="#{ClassificationBean.reset}"
                        immediate="true"
                            value="Reset Changes"
                             type="SUBMIT"/>

      <h:commandButton        id="update"
                        action="#{ClassificationBean.update}"
                        immediate="false"
                            value="Save Changes"
                             type="SUBMIT"/>

      <h:commandButton        id="cancel"
                        action="#{ClassificationBean.cancel}"
                        immediate="true"
                            value="Cancel"
                             type="SUBMIT"/>

    </h:form>

  </BODY>
</HTML>
</f:view>
