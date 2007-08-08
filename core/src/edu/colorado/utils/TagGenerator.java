/*
 * TagGenerator.java
 * Created on Aug 11, 2005
 * @author dneufeld
 */
package edu.colorado.utils;

//SAX classes.
import org.xml.sax.*;
import org.xml.sax.helpers.*;

import javax.xml.transform.sax.*; 


public class TagGenerator {

    public void addTag (TransformerHandler th, AttributesImpl atts, String tagName, String tagValue) throws SAXException {
        th.startElement("","", tagName, atts);
        th.characters(tagValue.toCharArray(),0,tagValue.length());     
        th.endElement("","",tagName);  
        atts.clear();
    }
    
    public static void main(String[] args) {
    }
}
