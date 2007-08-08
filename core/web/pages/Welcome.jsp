<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>

<html:html locale="true">
<head>
<title><bean:message key="welcome.title"/></title>
<html:base/>
<tiles:insert definition="layout"/>
</head>
<body bgcolor="white">

<logic:notPresent name="org.apache.struts.action.MESSAGE" scope="application">
  <font color="red">
    ERROR:  Application resources not loaded -- check servlet container
    logs for error messages.
  </font>
</logic:notPresent>

<CENTER>
<TABLE width=800 align=center units="pixels">
  <TBODY>
  <TR>
    <TD width=800 height=350>
      <P align=justify><BR><FONT face=verdana size=2>The BioGeomancer consortium
      is developing online workbench, web services, and desktop applications
      that will provide georeferencing for collectors, curators and users of
      natural history specimens, including software tools to allow natural
      language processing of archival data records that were collected in many
      different formats. <BR><BR>Over the past 250 years, biologists have gone
      into the field to collect specimens and associated environmental
      information documenting the range of life. The results of these
      explorations are an irreplaceable archive of Earth's biological diversity
      that plays a fundamental role in generating new knowledge and guiding
      conservation decisions. Yet, roughly one billion specimen records, and
      even more species observation records, remain practically unusable in
      their current form. <BR><BR>Georeferenced biocollection data is in high
      demand. Mapping species occurrence data is fundamental to describing and
      analysing biotic distributions. This information is also critical for
      conservation planning, reserving selection, monitoring, and the
      examination of the potential effects of climate change on biodiversity.
      Increasing the availability of georeferenced species distribution data
      will vastly increase our ability to understand patterns of biodiversity
      and to make balanced conservation-related decisions. Most data in these
      analyses come from natural history collections, which provide unique and
      irreplaceable information, especially for areas that have undergone
      habitat change due to clearing for agriculture or ubanization. <BR><BR>We
      expect that BioGeomancer will have an immediate positive impact on the
      availability of data from natural history collections. BioGeomancer will
      bring the cost to value ratio down to the point where every collection
      that seeks to make its data public will also seek to georeference those
      records. For example, for the ORNIS project, the existence of BioGeomancer
      will make the difference between being able to georeference only North
      American localities and being able to georeference all of the localities
      of bird specimen from 30 participating institutions during the course of
      the project. On the global scale, BioGeomancer will have an impact on
      standards development within GBIF and the Taxonomic Database Working
      Group, and the tools developed here are explicitly targeted for
      interoperability with the GBIF portal. <BR><BR>The BioGeomancer research
      consortium is coordinated by the University of California at Berkeley and
      is developing a universal system for geo-referencing the diverse specimen
      records in natural history collections. <BR><BR></FONT></P></TD></TR>
  <TR>
    <TD>
      <TABLE width=800 align=center units="pixels">
        <TBODY>
        <TR>
          <TD vAlign=center align=middle width=133><A
            href="http://www.austmus.gov.au/collections/" target=_blank><FONT
            face=verdana size=1><B>AUSTRALIAN MUSEUM</B></FONT></A> </TD>
          <TD vAlign=center align=middle width=133><A
            href="http://www.conabio.gob.mx/" target=_blank><IMG
            src="/biogeomancer_<bean:message key="user"/>/images/logo/conabio_logo.gif" border=0
            units="pixels"></IMG></A> </TD>
          <TD vAlign=center align=middle width=133><FONT face=verdana
            size=1><B>CHAPMAN</B></FONT> </TD>
          <TD vAlign=center align=middle width=133><A
            href="http://www.inram.org/" target=_blank><IMG
            src="/biogeomancer_<bean:message key="user"/>/images/logo/inram_logo.gif" border=0
            units="pixels"></IMG></A> </TD>
          <TD vAlign=center align=middle width=133><FONT face=verdana
            size=1><B>DIBNER</B></FONT> </TD>
          <TD vAlign=center align=middle width=133><A
            href="http://www.peabody.yale.edu/" target=_blank><IMG
            src="/biogeomancer_<bean:message key="user"/>/images/logo/peabody_logo.jpg" border=0
            units="pixels"></IMG></A> </TD></TR>
        <TR>
          <TD vAlign=center align=middle width=133><A
            href="http://www.moore.org/" target=_blank><IMG
            src="/biogeomancer_<bean:message key="user"/>/images/logo/moore_logo.gif" border=0
            units="pixels"></IMG></A> </TD>
          <TD vAlign=center align=middle width=133><FONT face=verdana
            size=1><B>FITZKE</B></FONT> </TD>
          <TD vAlign=center align=middle width=133><A
            href="http://www.gbif.org/" target=_blank><IMG
            src="/biogeomancer/images/logo/gbif_logo.jpg" border=0
            units="pixels"></IMG></A> </TD>
          <TD vAlign=center align=middle width=133><A
            href="http://www.calacademy.org/" target=_blank><FONT face=verdana
            size=1><B>CALIFORNIA ACADEMY OF SCIENCES</B></FONT></A> </TD>
          <TD vAlign=center align=middle width=133><A
            href="http://nhm.ku.edu/" target=_blank><IMG
            src="/biogeomancer_<bean:message key="user"/>/images/logo/kansas_logo.jpg" border=0
            units="pixels"></IMG></A> </TD>
          <TD vAlign=center align=middle width=133><FONT face=verdana
            size=1><B>REID</B></FONT> </TD></TR>
        <TR>
          <TD vAlign=center align=middle width=133><A
            href="http://www.tulane.edu/" target=_blank><IMG
            src="/biogeomancer_<bean:message key="user"/>/images/logo/tulane_logo.gif" border=0
            units="pixels"></IMG></A> </TD>
          <TD vAlign=center align=middle width=133><A
            href="http://www.uiuc.edu/" target=_blank><IMG
            src="/biogeomancer_<bean:message key="user"/>/images/logo/uiuc_logo.gif" border=0
            units="pixels"></IMG></A> </TD>
          <TD vAlign=center align=middle width=133><A
            href="http://cumuseum.colorado.edu/" target=_blank><IMG
            src="/biogeomancer_<bean:message key="user"/>/images/logo/colorado_logo.gif" border=0
            units="pixels"></IMG></A> </TD>
          <TD vAlign=center align=middle width=133><A
            href="http://www.ucsb.edu/" target=_blank><IMG
            src="/biogeomancer_<bean:message key="user"/>/images/logo/ucsb_logo.jpg" border=0
            units="pixels"></IMG></A> </TD>
          <TD vAlign=center align=middle width=133><A
            href="http://mvz.berkeley.edu/" target=_blank><IMG
            src="/biogeomancer_<bean:message key="user"/>/images/logo/berk_logo.jpg" border=0
            units="pixels"></IMG></A> </TD>
          <TD vAlign=center align=middle width=133><A
            href="http://www.cria.org.br/" target=_blank><IMG
            src="/biogeomancer_<bean:message key="user"/>/images/logo/cria_logo.gif" border=0
            units="pixels"></IMG></A> </TD></TR></TBODY></TABLE></TD></TR>
  <TR>
    <TD>&nbsp;</TD></TR>
  <TR>
    <TD bgColor=#000033></TD></TR>
  <TR>
    <TD vAlign=center align=left><FONT face=verdana size=2>Funding for the
      project is provided by:</FONT> &nbsp; <IMG
      src="/biogeomancer/images/logo/moore_logo.gif" border=0 units="pixels"></IMG>
      &nbsp; <IMG src="/biogeomancer_<bean:message key="user"/>/images/logo/gbif_logo_small.jpg" align=bottom
      border=0 units="pixels"></IMG> &nbsp; <IMG
      src="/biogeomancer_<bean:message key="user"/>/images/logo/nsf_logo.gif" align=bottom border=0
      units="pixels"></IMG> </TD></TR>
  <TR>
    <TD bgColor=#000033></TD></TR></TBODY></TABLE></CENTER></BODY></HTML>

</body>
</html:html>
