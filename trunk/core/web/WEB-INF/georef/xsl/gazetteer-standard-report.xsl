<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
  xmlns:gaz="http://www.alexandria.ucsb.edu/gazetteer" 
  xmlns:gml="http://www.opengis.net/gml" 
  xmlns:xlink="http://www.w3.org/1999/xlink" 
  xmlns:xs="http://www.w3.org/2001/XMLSchema" 
  xmlns="http://parse.greg.colorado.biogeomancer.edu" 
  xmlns:xalan="http://xml.apache.org/xslt"      
  version="1.0" 
  exclude-result-prefixes="gaz gml xlink xs">
  <xsl:output method="xml" 
              encoding="UTF-8"
              indent="yes" 
              xalan:indent-amount="2"/>
              
  <xsl:template match="query-response">
    <xsl:apply-templates/>     
  </xsl:template> 
  <xsl:template match="gaz:error">
    <error>
      <xsl:for-each select="gaz:code">
        <code>
          <xsl:value-of select="."/>
        </code>
      </xsl:for-each>
      <xsl:for-each select="gaz:description">
        <description>
          <xsl:value-of select="."/>
        </description>
      </xsl:for-each>
    </error>
  </xsl:template>       
  <xsl:template match="gaz:standard-reports"> 
    <gazetteer-standard-reports>
      <xsl:attribute name="xsi:schemaLocation">
        http://parse.greg.colorado.biogeomancer.edu /georef/xsd/gazetteer-standard-report.xsd
      </xsl:attribute>
      <xsl:apply-templates/>
    </gazetteer-standard-reports>
  </xsl:template>
  <xsl:template match="gaz:gazetteer-standard-report">
    <gazetteer-standard-report>
      <xsl:for-each select="gaz:identifier">
        <identifier>
          <xsl:value-of select="."/>
        </identifier>
      </xsl:for-each>
      <xsl:for-each select="gaz:codes">
        <codes>
          <xsl:for-each select="gaz:code">
            <code>
              <xsl:for-each select="@scheme">
                <xsl:copy/>
              </xsl:for-each>
              <xsl:value-of select="."/>
            </code>
          </xsl:for-each>
        </codes>
      </xsl:for-each>
      <xsl:for-each select="gaz:display-name">
        <display-name>
          <xsl:value-of select="."/>
        </display-name>
      </xsl:for-each>
      <xsl:for-each select="gaz:names">
        <names>
        <xsl:for-each select="gaz:name">
          <name>
            <xsl:for-each select="@primary">
              <xsl:copy/>
            </xsl:for-each>
            <xsl:for-each select="@status">
              <xsl:copy/>
            </xsl:for-each>
            <xsl:value-of select="."/>
          </name>
        </xsl:for-each>
        </names>
      </xsl:for-each>
      <xsl:for-each select="gaz:bounding-box">
        <bounding-box>
          <xsl:for-each select="gml:coord">
            <coord>
              <xsl:for-each select="gml:X">
                <X>
                  <xsl:value-of select="."/>
                </X>
              </xsl:for-each>
              <xsl:for-each select="gml:Y">
                <Y>
                  <xsl:value-of select="."/>
                </Y>
              </xsl:for-each>
              <xsl:for-each select="gml:Z">
                <Z>
                  <xsl:value-of select="."/>
                </Z>
              </xsl:for-each>
            </coord>
          </xsl:for-each>
        </bounding-box>
      </xsl:for-each>
      <xsl:for-each select="gaz:footprints">
        <footprints>
          <xsl:for-each select="gaz:footprint">
            <footprint>
              <xsl:for-each select="gml:Point">
                <Point>
                  <xsl:for-each select="gml:coord">
                    <coord>
                      <xsl:for-each select="gml:X">
                        <X>
                          <xsl:value-of select="."/>
                        </X>
                      </xsl:for-each>
                      <xsl:for-each select="gml:Y">
                        <Y>
                          <xsl:value-of select="."/>
                        </Y>
                      </xsl:for-each>
                      <xsl:for-each select="gml:Z">
                        <Z>
                          <xsl:value-of select="."/>
                        </Z>
                      </xsl:for-each>
                    </coord>
                  </xsl:for-each>
                </Point>
              </xsl:for-each>
              <xsl:for-each select="gml:Box">
                <bounding-box>
                  <xsl:for-each select="gml:coord">
                    <coord>
                      <xsl:for-each select="gml:X">
                        <X>
                          <xsl:value-of select="."/>
                        </X>
                      </xsl:for-each>
                      <xsl:for-each select="gml:Y">
                        <Y>
                          <xsl:value-of select="."/>
                        </Y>
                      </xsl:for-each>
                      <xsl:for-each select="gml:Z">
                        <Z>
                          <xsl:value-of select="."/>
                        </Z>
                      </xsl:for-each>
                    </coord>
                  </xsl:for-each>
                </bounding-box>
              </xsl:for-each>
            </footprint>
          </xsl:for-each>
        </footprints>
      </xsl:for-each>
      <xsl:for-each select="gaz:classes">
        <glasses>
          <xsl:for-each select="gaz:class">
            <glass>
              <xsl:for-each select="@primary">
                <xsl:copy/>
              </xsl:for-each>
              <xsl:for-each select="@thesaurus">
                <xsl:copy/>
              </xsl:for-each>
              <xsl:value-of select="."/>
            </glass>
          </xsl:for-each>
        </glasses>
      </xsl:for-each>
      <xsl:for-each select="gaz:relationships">
        <relationships>
          <xsl:for-each select="gaz:relationship">
            <relationship>
             <xsl:for-each select="@relation">
                <xsl:copy/>
              </xsl:for-each>
              <xsl:for-each select="@target-name">
                <xsl:copy/>
              </xsl:for-each>
              <xsl:value-of select="."/>
            </relationship>
          </xsl:for-each>
        </relationships>
      </xsl:for-each>
     </gazetteer-standard-report>
  </xsl:template>
</xsl:stylesheet>
