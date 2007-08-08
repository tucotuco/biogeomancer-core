// This class was generated by the JAXRPC SI, do not edit.
// Contents subject to change without notice.
// JAX-RPC Standard Implementation (1.1.3, build R1)
// Generated source version: 1.1.3

package edu.tulane.geolocate;

import com.sun.xml.rpc.client.BasicService;
import com.sun.xml.rpc.encoding.*;
import com.sun.xml.rpc.encoding.simpletype.*;
import com.sun.xml.rpc.encoding.soap.*;
import com.sun.xml.rpc.encoding.literal.*;
import com.sun.xml.rpc.soap.SOAPVersion;
import com.sun.xml.rpc.wsdl.document.schema.SchemaConstants;
import javax.xml.rpc.*;
import javax.xml.rpc.encoding.*;
import javax.xml.namespace.QName;

public class Geolocatesvc_SerializerRegistry implements SerializerConstants {
    public Geolocatesvc_SerializerRegistry() {
    }
    
    public TypeMappingRegistry getRegistry() {
        
        TypeMappingRegistry registry = BasicService.createStandardTypeMappingRegistry();
        TypeMapping mapping12 = registry.getTypeMapping(SOAP12Constants.NS_SOAP_ENCODING);
        TypeMapping mapping = registry.getTypeMapping(SOAPConstants.NS_SOAP_ENCODING);
        TypeMapping mapping2 = registry.getTypeMapping("");
        {
            QName type = new QName("http://www.museum.tulane.edu/webservices/", "LocalityDescription");
            CombinedSerializer serializer = new edu.tulane.geolocate.LocalityDescription_LiteralSerializer(type, "", DONT_ENCODE_TYPE);
            registerSerializer(mapping2,edu.tulane.geolocate.LocalityDescription.class, type, serializer);
        }
        {
            QName type = new QName("http://www.museum.tulane.edu/webservices/", "Georef3Response");
            CombinedSerializer serializer = new edu.tulane.geolocate.Georef3Response_LiteralSerializer(type, "", DONT_ENCODE_TYPE);
            registerSerializer(mapping2,edu.tulane.geolocate.Georef3Response.class, type, serializer);
        }
        {
            QName type = new QName("http://www.museum.tulane.edu/webservices/", "SnapPointToNearestFoundWaterBody2");
            CombinedSerializer serializer = new edu.tulane.geolocate.SnapPointToNearestFoundWaterBody2_LiteralSerializer(type, "", DONT_ENCODE_TYPE);
            registerSerializer(mapping2,edu.tulane.geolocate.SnapPointToNearestFoundWaterBody2.class, type, serializer);
        }
        {
            QName type = new QName("http://www.museum.tulane.edu/webservices/", "SnapPointToNearestFoundWaterBody");
            CombinedSerializer serializer = new edu.tulane.geolocate.SnapPointToNearestFoundWaterBody_LiteralSerializer(type, "", DONT_ENCODE_TYPE);
            registerSerializer(mapping2,edu.tulane.geolocate.SnapPointToNearestFoundWaterBody.class, type, serializer);
        }
        {
            QName type = new QName("http://www.museum.tulane.edu/webservices/", "Georef");
            CombinedSerializer serializer = new edu.tulane.geolocate.Georef_LiteralSerializer(type, "", DONT_ENCODE_TYPE);
            registerSerializer(mapping2,edu.tulane.geolocate.Georef.class, type, serializer);
        }
        {
            QName type = new QName("http://www.museum.tulane.edu/webservices/", "GeographicPoint");
            CombinedSerializer serializer = new edu.tulane.geolocate.GeographicPoint_LiteralSerializer(type, "", DONT_ENCODE_TYPE);
            registerSerializer(mapping2,edu.tulane.geolocate.GeographicPoint.class, type, serializer);
        }
        {
            QName type = new QName("http://www.museum.tulane.edu/webservices/", "GeorefResponse");
            CombinedSerializer serializer = new edu.tulane.geolocate.GeorefResponse_LiteralSerializer(type, "", DONT_ENCODE_TYPE);
            registerSerializer(mapping2,edu.tulane.geolocate.GeorefResponse.class, type, serializer);
        }
        {
            QName type = new QName("http://www.museum.tulane.edu/webservices/", "Georef2");
            CombinedSerializer serializer = new edu.tulane.geolocate.Georef2_LiteralSerializer(type, "", DONT_ENCODE_TYPE);
            registerSerializer(mapping2,edu.tulane.geolocate.Georef2.class, type, serializer);
        }
        {
            QName type = new QName("http://www.museum.tulane.edu/webservices/", "Georef_Result_Set");
            CombinedSerializer serializer = new edu.tulane.geolocate.Georef_Result_Set_LiteralSerializer(type, "", DONT_ENCODE_TYPE);
            registerSerializer(mapping2,edu.tulane.geolocate.Georef_Result_Set.class, type, serializer);
        }
        {
            QName type = new QName("http://www.museum.tulane.edu/webservices/", "Georef2Response");
            CombinedSerializer serializer = new edu.tulane.geolocate.Georef2Response_LiteralSerializer(type, "", DONT_ENCODE_TYPE);
            registerSerializer(mapping2,edu.tulane.geolocate.Georef2Response.class, type, serializer);
        }
        {
            QName type = new QName("http://www.museum.tulane.edu/webservices/", "Georef3");
            CombinedSerializer serializer = new edu.tulane.geolocate.Georef3_LiteralSerializer(type, "", DONT_ENCODE_TYPE);
            registerSerializer(mapping2,edu.tulane.geolocate.Georef3.class, type, serializer);
        }
        {
            QName type = new QName("http://www.museum.tulane.edu/webservices/", "ArrayOfString");
            CombinedSerializer serializer = new edu.tulane.geolocate.ArrayOfString_LiteralSerializer(type, "", DONT_ENCODE_TYPE);
            registerSerializer(mapping2,edu.tulane.geolocate.ArrayOfString.class, type, serializer);
        }
        {
            QName type = new QName("http://www.museum.tulane.edu/webservices/", "SnapPointToNearestFoundWaterBodyResponse");
            CombinedSerializer serializer = new edu.tulane.geolocate.SnapPointToNearestFoundWaterBodyResponse_LiteralSerializer(type, "", DONT_ENCODE_TYPE);
            registerSerializer(mapping2,edu.tulane.geolocate.SnapPointToNearestFoundWaterBodyResponse.class, type, serializer);
        }
        {
            QName type = new QName("http://www.museum.tulane.edu/webservices/", "FindWaterBodiesWithinLocality");
            CombinedSerializer serializer = new edu.tulane.geolocate.FindWaterBodiesWithinLocality_LiteralSerializer(type, "", DONT_ENCODE_TYPE);
            registerSerializer(mapping2,edu.tulane.geolocate.FindWaterBodiesWithinLocality.class, type, serializer);
        }
        {
            QName type = new QName("http://www.museum.tulane.edu/webservices/", "SnapPointToNearestFoundWaterBody2Response");
            CombinedSerializer serializer = new edu.tulane.geolocate.SnapPointToNearestFoundWaterBody2Response_LiteralSerializer(type, "", DONT_ENCODE_TYPE);
            registerSerializer(mapping2,edu.tulane.geolocate.SnapPointToNearestFoundWaterBody2Response.class, type, serializer);
        }
        {
            QName type = new QName("http://www.museum.tulane.edu/webservices/", "FindWaterBodiesWithinLocalityResponse");
            CombinedSerializer serializer = new edu.tulane.geolocate.FindWaterBodiesWithinLocalityResponse_LiteralSerializer(type, "", DONT_ENCODE_TYPE);
            registerSerializer(mapping2,edu.tulane.geolocate.FindWaterBodiesWithinLocalityResponse.class, type, serializer);
        }
        {
            QName type = new QName("http://www.museum.tulane.edu/webservices/", "Georef_Result");
            CombinedSerializer serializer = new edu.tulane.geolocate.Georef_Result_LiteralSerializer(type, "", DONT_ENCODE_TYPE);
            registerSerializer(mapping2,edu.tulane.geolocate.Georef_Result.class, type, serializer);
        }
        return registry;
    }
    
    private static void registerSerializer(TypeMapping mapping, java.lang.Class javaType, javax.xml.namespace.QName xmlType,
        Serializer ser) {
        mapping.register(javaType, xmlType, new SingletonSerializerFactory(ser),
            new SingletonDeserializerFactory((Deserializer)ser));
    }
    
}
