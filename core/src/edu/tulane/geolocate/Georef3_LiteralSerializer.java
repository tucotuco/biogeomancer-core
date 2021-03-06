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

public class Georef3_LiteralSerializer extends LiteralObjectSerializerBase
    implements Initializable {
  private static final javax.xml.namespace.QName ns1_vLocality_QNAME = new QName(
      "http://www.museum.tulane.edu/webservices/", "vLocality");
  private static final javax.xml.namespace.QName ns2_string_TYPE_QNAME = SchemaConstants.QNAME_TYPE_STRING;
  private static final javax.xml.namespace.QName ns1_vGeography_QNAME = new QName(
      "http://www.museum.tulane.edu/webservices/", "vGeography");
  private static final javax.xml.namespace.QName ns1_HwyX_QNAME = new QName(
      "http://www.museum.tulane.edu/webservices/", "HwyX");
  private static final javax.xml.namespace.QName ns2_boolean_TYPE_QNAME = SchemaConstants.QNAME_TYPE_BOOLEAN;
  private static final javax.xml.namespace.QName ns1_FindWaterbody_QNAME = new QName(
      "http://www.museum.tulane.edu/webservices/", "FindWaterbody");
  private CombinedSerializer ns2_myns2_string__java_lang_String_String_Serializer;
  private CombinedSerializer ns2_myns2__boolean__boolean_Boolean_Serializer;

  public Georef3_LiteralSerializer(javax.xml.namespace.QName type,
      java.lang.String encodingStyle) {
    this(type, encodingStyle, false);
  }

  public Georef3_LiteralSerializer(javax.xml.namespace.QName type,
      java.lang.String encodingStyle, boolean encodeType) {
    super(type, true, encodingStyle, encodeType);
  }

  public java.lang.Object doDeserialize(XMLReader reader,
      SOAPDeserializationContext context) throws java.lang.Exception {
    edu.tulane.geolocate.Georef3 instance = new edu.tulane.geolocate.Georef3();
    java.lang.Object member = null;
    javax.xml.namespace.QName elementName;
    java.util.List values;
    java.lang.Object value;

    reader.nextElementContent();
    elementName = reader.getName();
    if (reader.getState() == XMLReader.START) {
      if (elementName.equals(ns1_vLocality_QNAME)) {
        member = ns2_myns2_string__java_lang_String_String_Serializer
            .deserialize(ns1_vLocality_QNAME, reader, context);
        if (member == null) {
          throw new DeserializationException("literal.unexpectedNull");
        }
        instance.setVLocality((java.lang.String) member);
        reader.nextElementContent();
      }
    }
    elementName = reader.getName();
    if (reader.getState() == XMLReader.START) {
      if (elementName.equals(ns1_vGeography_QNAME)) {
        member = ns2_myns2_string__java_lang_String_String_Serializer
            .deserialize(ns1_vGeography_QNAME, reader, context);
        if (member == null) {
          throw new DeserializationException("literal.unexpectedNull");
        }
        instance.setVGeography((java.lang.String) member);
        reader.nextElementContent();
      }
    }
    elementName = reader.getName();
    if (reader.getState() == XMLReader.START) {
      if (elementName.equals(ns1_HwyX_QNAME)) {
        member = ns2_myns2__boolean__boolean_Boolean_Serializer.deserialize(
            ns1_HwyX_QNAME, reader, context);
        if (member == null) {
          throw new DeserializationException("literal.unexpectedNull");
        }
        instance.setHwyX(((Boolean) member).booleanValue());
        reader.nextElementContent();
      } else {
        throw new DeserializationException("literal.unexpectedElementName",
            new Object[] { ns1_HwyX_QNAME, reader.getName() });
      }
    } else {
      throw new DeserializationException("literal.expectedElementName", reader
          .getName().toString());
    }
    elementName = reader.getName();
    if (reader.getState() == XMLReader.START) {
      if (elementName.equals(ns1_FindWaterbody_QNAME)) {
        member = ns2_myns2__boolean__boolean_Boolean_Serializer.deserialize(
            ns1_FindWaterbody_QNAME, reader, context);
        if (member == null) {
          throw new DeserializationException("literal.unexpectedNull");
        }
        instance.setFindWaterbody(((Boolean) member).booleanValue());
        reader.nextElementContent();
      } else {
        throw new DeserializationException("literal.unexpectedElementName",
            new Object[] { ns1_FindWaterbody_QNAME, reader.getName() });
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
    edu.tulane.geolocate.Georef3 instance = (edu.tulane.geolocate.Georef3) obj;

    if (instance.getVLocality() != null) {
      ns2_myns2_string__java_lang_String_String_Serializer.serialize(instance
          .getVLocality(), ns1_vLocality_QNAME, null, writer, context);
    }
    if (instance.getVGeography() != null) {
      ns2_myns2_string__java_lang_String_String_Serializer.serialize(instance
          .getVGeography(), ns1_vGeography_QNAME, null, writer, context);
    }
    if (new Boolean(instance.isHwyX()) == null) {
      throw new SerializationException("literal.unexpectedNull");
    }
    ns2_myns2__boolean__boolean_Boolean_Serializer.serialize(new Boolean(
        instance.isHwyX()), ns1_HwyX_QNAME, null, writer, context);
    if (new Boolean(instance.isFindWaterbody()) == null) {
      throw new SerializationException("literal.unexpectedNull");
    }
    ns2_myns2__boolean__boolean_Boolean_Serializer.serialize(new Boolean(
        instance.isFindWaterbody()), ns1_FindWaterbody_QNAME, null, writer,
        context);
  }

  public void doSerializeAttributes(java.lang.Object obj, XMLWriter writer,
      SOAPSerializationContext context) throws java.lang.Exception {
    edu.tulane.geolocate.Georef3 instance = (edu.tulane.geolocate.Georef3) obj;

  }

  public void initialize(InternalTypeMappingRegistry registry) throws Exception {
    ns2_myns2_string__java_lang_String_String_Serializer = (CombinedSerializer) registry
        .getSerializer("", java.lang.String.class, ns2_string_TYPE_QNAME);
    ns2_myns2__boolean__boolean_Boolean_Serializer = (CombinedSerializer) registry
        .getSerializer("", boolean.class, ns2_boolean_TYPE_QNAME);
  }
}
