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

// This class was generated by the JAXRPC SI, do not edit.
// Contents subject to change without notice.
// JAX-RPC Standard Implementation (1.1.3, build R1)
// Generated source version: 1.1.3
package edu.tulane.geolocate;

import javax.xml.namespace.QName;

import com.sun.xml.rpc.encoding.CombinedSerializer;
import com.sun.xml.rpc.encoding.DeserializationException;
import com.sun.xml.rpc.encoding.Initializable;
import com.sun.xml.rpc.encoding.InternalTypeMappingRegistry;
import com.sun.xml.rpc.encoding.SOAPDeserializationContext;
import com.sun.xml.rpc.encoding.SOAPSerializationContext;
import com.sun.xml.rpc.encoding.SerializationException;
import com.sun.xml.rpc.encoding.literal.LiteralObjectSerializerBase;
import com.sun.xml.rpc.streaming.XMLReader;
import com.sun.xml.rpc.streaming.XMLReaderUtil;
import com.sun.xml.rpc.streaming.XMLWriter;
import com.sun.xml.rpc.wsdl.document.schema.SchemaConstants;

public class SnapPointToNearestFoundWaterBody2_LiteralSerializer extends
    LiteralObjectSerializerBase implements Initializable {
  private static final javax.xml.namespace.QName ns1_Country_QNAME = new QName(
      "http://www.museum.tulane.edu/webservices/", "Country");
  private static final javax.xml.namespace.QName ns2_string_TYPE_QNAME = SchemaConstants.QNAME_TYPE_STRING;
  private static final javax.xml.namespace.QName ns1_State_QNAME = new QName(
      "http://www.museum.tulane.edu/webservices/", "State");
  private static final javax.xml.namespace.QName ns1_County_QNAME = new QName(
      "http://www.museum.tulane.edu/webservices/", "County");
  private static final javax.xml.namespace.QName ns1_LocalityString_QNAME = new QName(
      "http://www.museum.tulane.edu/webservices/", "LocalityString");
  private static final javax.xml.namespace.QName ns1_WGS84Latitude_QNAME = new QName(
      "http://www.museum.tulane.edu/webservices/", "WGS84Latitude");
  private static final javax.xml.namespace.QName ns2_double_TYPE_QNAME = SchemaConstants.QNAME_TYPE_DOUBLE;
  private static final javax.xml.namespace.QName ns1_WGS84Longitude_QNAME = new QName(
      "http://www.museum.tulane.edu/webservices/", "WGS84Longitude");
  private CombinedSerializer ns2_myns2_string__java_lang_String_String_Serializer;
  private CombinedSerializer ns2_myns2__double__double_Double_Serializer;

  public SnapPointToNearestFoundWaterBody2_LiteralSerializer(
      javax.xml.namespace.QName type, java.lang.String encodingStyle) {
    this(type, encodingStyle, false);
  }

  public SnapPointToNearestFoundWaterBody2_LiteralSerializer(
      javax.xml.namespace.QName type, java.lang.String encodingStyle,
      boolean encodeType) {
    super(type, true, encodingStyle, encodeType);
  }

  public java.lang.Object doDeserialize(XMLReader reader,
      SOAPDeserializationContext context) throws java.lang.Exception {
    edu.tulane.geolocate.SnapPointToNearestFoundWaterBody2 instance = new edu.tulane.geolocate.SnapPointToNearestFoundWaterBody2();
    java.lang.Object member = null;
    javax.xml.namespace.QName elementName;
    java.util.List values;
    java.lang.Object value;

    reader.nextElementContent();
    elementName = reader.getName();
    if (reader.getState() == XMLReader.START) {
      if (elementName.equals(ns1_Country_QNAME)) {
        member = ns2_myns2_string__java_lang_String_String_Serializer
            .deserialize(ns1_Country_QNAME, reader, context);
        if (member == null) {
          throw new DeserializationException("literal.unexpectedNull");
        }
        instance.setCountry((java.lang.String) member);
        reader.nextElementContent();
      }
    }
    elementName = reader.getName();
    if (reader.getState() == XMLReader.START) {
      if (elementName.equals(ns1_State_QNAME)) {
        member = ns2_myns2_string__java_lang_String_String_Serializer
            .deserialize(ns1_State_QNAME, reader, context);
        if (member == null) {
          throw new DeserializationException("literal.unexpectedNull");
        }
        instance.setState((java.lang.String) member);
        reader.nextElementContent();
      }
    }
    elementName = reader.getName();
    if (reader.getState() == XMLReader.START) {
      if (elementName.equals(ns1_County_QNAME)) {
        member = ns2_myns2_string__java_lang_String_String_Serializer
            .deserialize(ns1_County_QNAME, reader, context);
        if (member == null) {
          throw new DeserializationException("literal.unexpectedNull");
        }
        instance.setCounty((java.lang.String) member);
        reader.nextElementContent();
      }
    }
    elementName = reader.getName();
    if (reader.getState() == XMLReader.START) {
      if (elementName.equals(ns1_LocalityString_QNAME)) {
        member = ns2_myns2_string__java_lang_String_String_Serializer
            .deserialize(ns1_LocalityString_QNAME, reader, context);
        if (member == null) {
          throw new DeserializationException("literal.unexpectedNull");
        }
        instance.setLocalityString((java.lang.String) member);
        reader.nextElementContent();
      }
    }
    elementName = reader.getName();
    if (reader.getState() == XMLReader.START) {
      if (elementName.equals(ns1_WGS84Latitude_QNAME)) {
        member = ns2_myns2__double__double_Double_Serializer.deserialize(
            ns1_WGS84Latitude_QNAME, reader, context);
        if (member == null) {
          throw new DeserializationException("literal.unexpectedNull");
        }
        instance.setWGS84Latitude(((Double) member).doubleValue());
        reader.nextElementContent();
      } else {
        throw new DeserializationException("literal.unexpectedElementName",
            new Object[] { ns1_WGS84Latitude_QNAME, reader.getName() });
      }
    } else {
      throw new DeserializationException("literal.expectedElementName", reader
          .getName().toString());
    }
    elementName = reader.getName();
    if (reader.getState() == XMLReader.START) {
      if (elementName.equals(ns1_WGS84Longitude_QNAME)) {
        member = ns2_myns2__double__double_Double_Serializer.deserialize(
            ns1_WGS84Longitude_QNAME, reader, context);
        if (member == null) {
          throw new DeserializationException("literal.unexpectedNull");
        }
        instance.setWGS84Longitude(((Double) member).doubleValue());
        reader.nextElementContent();
      } else {
        throw new DeserializationException("literal.unexpectedElementName",
            new Object[] { ns1_WGS84Longitude_QNAME, reader.getName() });
      }
    } else {
      throw new DeserializationException("literal.expectedElementName", reader
          .getName().toString());
    }

    XMLReaderUtil.verifyReaderState(reader, XMLReader.END);
    return instance;
  }

  public void doSerialize(java.lang.Object obj, XMLWriter writer,
      SOAPSerializationContext context) throws java.lang.Exception {
    edu.tulane.geolocate.SnapPointToNearestFoundWaterBody2 instance = (edu.tulane.geolocate.SnapPointToNearestFoundWaterBody2) obj;

    if (instance.getCountry() != null) {
      ns2_myns2_string__java_lang_String_String_Serializer.serialize(instance
          .getCountry(), ns1_Country_QNAME, null, writer, context);
    }
    if (instance.getState() != null) {
      ns2_myns2_string__java_lang_String_String_Serializer.serialize(instance
          .getState(), ns1_State_QNAME, null, writer, context);
    }
    if (instance.getCounty() != null) {
      ns2_myns2_string__java_lang_String_String_Serializer.serialize(instance
          .getCounty(), ns1_County_QNAME, null, writer, context);
    }
    if (instance.getLocalityString() != null) {
      ns2_myns2_string__java_lang_String_String_Serializer
          .serialize(instance.getLocalityString(), ns1_LocalityString_QNAME,
              null, writer, context);
    }
    if (new Double(instance.getWGS84Latitude()) == null) {
      throw new SerializationException("literal.unexpectedNull");
    }
    ns2_myns2__double__double_Double_Serializer.serialize(new Double(instance
        .getWGS84Latitude()), ns1_WGS84Latitude_QNAME, null, writer, context);
    if (new Double(instance.getWGS84Longitude()) == null) {
      throw new SerializationException("literal.unexpectedNull");
    }
    ns2_myns2__double__double_Double_Serializer.serialize(new Double(instance
        .getWGS84Longitude()), ns1_WGS84Longitude_QNAME, null, writer, context);
  }

  public void doSerializeAttributes(java.lang.Object obj, XMLWriter writer,
      SOAPSerializationContext context) throws java.lang.Exception {
    edu.tulane.geolocate.SnapPointToNearestFoundWaterBody2 instance = (edu.tulane.geolocate.SnapPointToNearestFoundWaterBody2) obj;

  }

  public void initialize(InternalTypeMappingRegistry registry) throws Exception {
    ns2_myns2_string__java_lang_String_String_Serializer = (CombinedSerializer) registry
        .getSerializer("", java.lang.String.class, ns2_string_TYPE_QNAME);
    ns2_myns2__double__double_Double_Serializer = (CombinedSerializer) registry
        .getSerializer("", double.class, ns2_double_TYPE_QNAME);
  }
}
