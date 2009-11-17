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

package edu.tulane.geolocate;

import java.util.ArrayList;

import javax.xml.namespace.QName;

import com.sun.xml.rpc.encoding.CombinedSerializer;
import com.sun.xml.rpc.encoding.Initializable;
import com.sun.xml.rpc.encoding.InternalTypeMappingRegistry;
import com.sun.xml.rpc.encoding.SOAPDeserializationContext;
import com.sun.xml.rpc.encoding.SOAPSerializationContext;
import com.sun.xml.rpc.encoding.literal.LiteralObjectSerializerBase;
import com.sun.xml.rpc.streaming.XMLReader;
import com.sun.xml.rpc.streaming.XMLReaderUtil;
import com.sun.xml.rpc.streaming.XMLWriter;
import com.sun.xml.rpc.wsdl.document.schema.SchemaConstants;

public class ArrayOfString_LiteralSerializer extends
    LiteralObjectSerializerBase implements Initializable {
  private static final javax.xml.namespace.QName ns1_string_QNAME = new QName(
      "http://www.museum.tulane.edu/webservices/", "string");
  private static final javax.xml.namespace.QName ns2_string_TYPE_QNAME = SchemaConstants.QNAME_TYPE_STRING;
  private CombinedSerializer ns2_myns2_string__java_lang_String_String_Serializer;

  public ArrayOfString_LiteralSerializer(javax.xml.namespace.QName type,
      java.lang.String encodingStyle) {
    this(type, encodingStyle, false);
  }

  public ArrayOfString_LiteralSerializer(javax.xml.namespace.QName type,
      java.lang.String encodingStyle, boolean encodeType) {
    super(type, true, encodingStyle, encodeType);
  }

  public java.lang.Object doDeserialize(XMLReader reader,
      SOAPDeserializationContext context) throws java.lang.Exception {
    edu.tulane.geolocate.ArrayOfString instance = new edu.tulane.geolocate.ArrayOfString();
    java.lang.Object member = null;
    javax.xml.namespace.QName elementName;
    java.util.List values;
    java.lang.Object value;

    reader.nextElementContent();
    elementName = reader.getName();
    if ((reader.getState() == XMLReader.START)
        && (elementName.equals(ns1_string_QNAME))) {
      values = new ArrayList();
      for (;;) {
        elementName = reader.getName();
        if ((reader.getState() == XMLReader.START)
            && (elementName.equals(ns1_string_QNAME))) {
          value = ns2_myns2_string__java_lang_String_String_Serializer
              .deserialize(ns1_string_QNAME, reader, context);
          values.add(value);
          reader.nextElementContent();
        } else {
          break;
        }
      }
      member = new java.lang.String[values.size()];
      member = values.toArray((Object[]) member);
      instance.setString((java.lang.String[]) member);
    } else {
      instance.setString(new java.lang.String[0]);
    }

    XMLReaderUtil.verifyReaderState(reader, XMLReader.END);
    return instance;
  }

  public void doSerialize(java.lang.Object obj, XMLWriter writer,
      SOAPSerializationContext context) throws java.lang.Exception {
    edu.tulane.geolocate.ArrayOfString instance = (edu.tulane.geolocate.ArrayOfString) obj;

    if (instance.getString() != null) {
      for (int i = 0; i < instance.getString().length; ++i) {
        ns2_myns2_string__java_lang_String_String_Serializer.serialize(instance
            .getString()[i], ns1_string_QNAME, null, writer, context);
      }
    }
  }

  public void doSerializeAttributes(java.lang.Object obj, XMLWriter writer,
      SOAPSerializationContext context) throws java.lang.Exception {
    edu.tulane.geolocate.ArrayOfString instance = (edu.tulane.geolocate.ArrayOfString) obj;

  }

  public void initialize(InternalTypeMappingRegistry registry) throws Exception {
    ns2_myns2_string__java_lang_String_String_Serializer = (CombinedSerializer) registry
        .getSerializer("", java.lang.String.class, ns2_string_TYPE_QNAME);
  }
}
