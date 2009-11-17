/**
 * Copyright 2007 University of California at Berkeley.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package edu.colorado.utils;

import java.io.FileOutputStream;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import org.xml.sax.helpers.AttributesImpl;

public class SLDFilterWriter {

  public static void main(String[] args) {
    // Unit test
    SLDFilterWriter sldWriter = new SLDFilterWriter(
        "4E29A21225531440D93DB7E88BE247931123600941806", "uid",
        "C:/temp/collections_uid_filter.xml");
    sldWriter.generateXML();
  }

  private final String uid;
  private final String uidFldName;

  private final String sldFilePath;

  public SLDFilterWriter(String uid, String uidFldName, String sldFilePath) {
    this.uid = uid;
    this.uidFldName = uidFldName;
    this.sldFilePath = sldFilePath;
  }

  public void generateXML() {
    // PrintWriter from a Servlet
    try {
      // StreamResult streamResult = new StreamResult(System.out);

      FileOutputStream fos = new FileOutputStream(this.sldFilePath);
      StreamResult streamResult = new StreamResult(fos);
      SAXTransformerFactory tf = (SAXTransformerFactory) SAXTransformerFactory
          .newInstance();
      // SAX2.0 ContentHandler.
      TransformerHandler hd = tf.newTransformerHandler();
      Transformer serializer = hd.getTransformer();
      // serializer.setOutputProperty(OutputKeys.ENCODING,"ISO-8859-1");
      // serializer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM,"users.dtd");
      serializer.setOutputProperty(OutputKeys.INDENT, "yes");
      hd.setResult(streamResult);
      hd.startDocument();
      TagGenerator tagGenerator = new TagGenerator();
      AttributesImpl atts = new AttributesImpl();

      // Start StyledLayerDescriptor tag
      atts.addAttribute("", "", "version", "CDATA", "1.0.0");
      hd.startElement("", "", "StyledLayerDescriptor", atts);
      atts.clear();

      // Start NamedLayer tag
      hd.startElement("", "", "NamedLayer", atts);

      // Start Name tag
      tagGenerator.addTag(hd, atts, "Name", "collections");
      // End Name

      // Start UserStyle tag
      hd.startElement("", "", "UserStyle", atts);

      // Start Title tag
      tagGenerator
          .addTag(hd, atts, "Title", "User Specific Collection Records");
      // End Title

      // Start FeatureTypeStyle tag
      hd.startElement("", "", "FeatureTypeStyle", atts);

      // Start Rule tag
      hd.startElement("", "", "Rule", atts);

      // Start Filter tag
      hd.startElement("", "", "Filter", atts);

      // Start PropertyIsEqualTo tag
      hd.startElement("", "", "PropertyIsEqualTo", atts);

      // Start PropertyName tag
      tagGenerator.addTag(hd, atts, "PropertyName", this.uidFldName);
      // End PropertyName

      // Start Literal tag
      tagGenerator.addTag(hd, atts, "Literal", this.uid);
      // End Literal

      hd.endElement("", "", "PropertyIsEqualTo");
      // End PropertyIsEqualTo tag

      hd.endElement("", "", "Filter");
      // End Filter tag

      // Start PointSymbolizer tag
      hd.startElement("", "", "PointSymbolizer", atts);

      // Start Geometry tag
      hd.startElement("", "", "Geometry", atts);

      // Start PropertyName tag
      tagGenerator.addTag(hd, atts, "PropertyName", "locatedAt");
      // End PropertyName

      hd.endElement("", "", "Geometry");
      // End Geometry tag

      // Start Graphic tag
      hd.startElement("", "", "Graphic", atts);

      // Start Mark tag
      hd.startElement("", "", "Mark", atts);

      // Start WellKnownName tag
      tagGenerator.addTag(hd, atts, "WellKnownName", "star");
      // End WellKnownName

      // Start Fill tag
      hd.startElement("", "", "Fill", atts);

      // Start CssParameter tag
      atts.addAttribute("", "", "name", "CDATA", "fill");
      tagGenerator.addTag(hd, atts, "CssParameter", "#ff0000");
      // End CssParameter

      hd.endElement("", "", "Fill");
      // End Fill tag

      hd.endElement("", "", "Mark");
      // End Mark tag

      // Start Size tag
      tagGenerator.addTag(hd, atts, "Size", "10.0");
      // End Size

      hd.endElement("", "", "Graphic");
      // End Graphic tag

      hd.endElement("", "", "PointSymbolizer");
      // End PointSymbolizer tag

      hd.endElement("", "", "Rule");
      // End Rule tag

      hd.endElement("", "", "FeatureTypeStyle");
      // End FeatureTypeStyle tag

      hd.endElement("", "", "UserStyle");
      // End UserStyle tag

      hd.endElement("", "", "NamedLayer");
      hd.endElement("", "", "StyledLayerDescriptor");

      hd.endDocument();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
