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

import java.rmi.RemoteException;

import javax.xml.namespace.QName;
import javax.xml.rpc.JAXRPCException;
import javax.xml.rpc.handler.HandlerChain;

import com.sun.xml.rpc.client.SenderException;
import com.sun.xml.rpc.client.StreamingSenderState;
import com.sun.xml.rpc.client.http.HttpClientTransport;
import com.sun.xml.rpc.encoding.CombinedSerializer;
import com.sun.xml.rpc.encoding.InternalTypeMappingRegistry;
import com.sun.xml.rpc.encoding.SOAPDeserializationContext;
import com.sun.xml.rpc.encoding.SOAPDeserializationState;
import com.sun.xml.rpc.soap.message.InternalSOAPMessage;
import com.sun.xml.rpc.soap.message.SOAPBlockInfo;
import com.sun.xml.rpc.soap.streaming.SOAPNamespaceConstants;
import com.sun.xml.rpc.streaming.XMLReader;

public class GeolocatesvcSoap_Stub extends com.sun.xml.rpc.client.StubBase
    implements edu.tulane.geolocate.GeolocatesvcSoap {

  private static final javax.xml.namespace.QName _portName = new QName(
      "http://www.museum.tulane.edu/webservices/", "geolocatesvcSoap");

  private static final int SnapPointToNearestFoundWaterBody_OPCODE = 0;

  private static final int FindWaterBodiesWithinLocality_OPCODE = 1;

  private static final int Georef_OPCODE = 2;

  private static final int SnapPointToNearestFoundWaterBody2_OPCODE = 3;

  private static final int Georef2_OPCODE = 4;

  private static final int Georef3_OPCODE = 5;

  private static final javax.xml.namespace.QName ns1_SnapPointToNearestFoundWaterBody_SnapPointToNearestFoundWaterBody_QNAME = new QName(
      "http://www.museum.tulane.edu/webservices/",
      "SnapPointToNearestFoundWaterBody");

  private static final javax.xml.namespace.QName ns1_SnapPointToNearestFoundWaterBody_TYPE_QNAME = new QName(
      "http://www.museum.tulane.edu/webservices/",
      "SnapPointToNearestFoundWaterBody");

  private static final javax.xml.namespace.QName ns1_SnapPointToNearestFoundWaterBody_SnapPointToNearestFoundWaterBodyResponse_QNAME = new QName(
      "http://www.museum.tulane.edu/webservices/",
      "SnapPointToNearestFoundWaterBodyResponse");

  private static final javax.xml.namespace.QName ns1_SnapPointToNearestFoundWaterBodyResponse_TYPE_QNAME = new QName(
      "http://www.museum.tulane.edu/webservices/",
      "SnapPointToNearestFoundWaterBodyResponse");

  private static final javax.xml.namespace.QName ns1_FindWaterBodiesWithinLocality_FindWaterBodiesWithinLocality_QNAME = new QName(
      "http://www.museum.tulane.edu/webservices/",
      "FindWaterBodiesWithinLocality");

  private static final javax.xml.namespace.QName ns1_FindWaterBodiesWithinLocality_TYPE_QNAME = new QName(
      "http://www.museum.tulane.edu/webservices/",
      "FindWaterBodiesWithinLocality");

  private static final javax.xml.namespace.QName ns1_FindWaterBodiesWithinLocality_FindWaterBodiesWithinLocalityResponse_QNAME = new QName(
      "http://www.museum.tulane.edu/webservices/",
      "FindWaterBodiesWithinLocalityResponse");

  private static final javax.xml.namespace.QName ns1_FindWaterBodiesWithinLocalityResponse_TYPE_QNAME = new QName(
      "http://www.museum.tulane.edu/webservices/",
      "FindWaterBodiesWithinLocalityResponse");

  private static final javax.xml.namespace.QName ns1_Georef_Georef_QNAME = new QName(
      "http://www.museum.tulane.edu/webservices/", "Georef");

  private static final javax.xml.namespace.QName ns1_Georef_TYPE_QNAME = new QName(
      "http://www.museum.tulane.edu/webservices/", "Georef");

  private static final javax.xml.namespace.QName ns1_Georef_GeorefResponse_QNAME = new QName(
      "http://www.museum.tulane.edu/webservices/", "GeorefResponse");

  private static final javax.xml.namespace.QName ns1_GeorefResponse_TYPE_QNAME = new QName(
      "http://www.museum.tulane.edu/webservices/", "GeorefResponse");

  private static final javax.xml.namespace.QName ns1_SnapPointToNearestFoundWaterBody2_SnapPointToNearestFoundWaterBody2_QNAME = new QName(
      "http://www.museum.tulane.edu/webservices/",
      "SnapPointToNearestFoundWaterBody2");

  private static final javax.xml.namespace.QName ns1_SnapPointToNearestFoundWaterBody2_TYPE_QNAME = new QName(
      "http://www.museum.tulane.edu/webservices/",
      "SnapPointToNearestFoundWaterBody2");

  private static final javax.xml.namespace.QName ns1_SnapPointToNearestFoundWaterBody2_SnapPointToNearestFoundWaterBody2Response_QNAME = new QName(
      "http://www.museum.tulane.edu/webservices/",
      "SnapPointToNearestFoundWaterBody2Response");

  private static final javax.xml.namespace.QName ns1_SnapPointToNearestFoundWaterBody2Response_TYPE_QNAME = new QName(
      "http://www.museum.tulane.edu/webservices/",
      "SnapPointToNearestFoundWaterBody2Response");

  private static final javax.xml.namespace.QName ns1_Georef2_Georef2_QNAME = new QName(
      "http://www.museum.tulane.edu/webservices/", "Georef2");
  private static final javax.xml.namespace.QName ns1_Georef2_TYPE_QNAME = new QName(
      "http://www.museum.tulane.edu/webservices/", "Georef2");
  private static final javax.xml.namespace.QName ns1_Georef2_Georef2Response_QNAME = new QName(
      "http://www.museum.tulane.edu/webservices/", "Georef2Response");
  private static final javax.xml.namespace.QName ns1_Georef2Response_TYPE_QNAME = new QName(
      "http://www.museum.tulane.edu/webservices/", "Georef2Response");
  private static final javax.xml.namespace.QName ns1_Georef3_Georef3_QNAME = new QName(
      "http://www.museum.tulane.edu/webservices/", "Georef3");
  private static final javax.xml.namespace.QName ns1_Georef3_TYPE_QNAME = new QName(
      "http://www.museum.tulane.edu/webservices/", "Georef3");
  private static final javax.xml.namespace.QName ns1_Georef3_Georef3Response_QNAME = new QName(
      "http://www.museum.tulane.edu/webservices/", "Georef3Response");
  private static final javax.xml.namespace.QName ns1_Georef3Response_TYPE_QNAME = new QName(
      "http://www.museum.tulane.edu/webservices/", "Georef3Response");
  private static final java.lang.String[] myNamespace_declarations = new java.lang.String[] {
      "ns0", "http://www.museum.tulane.edu/webservices/" };
  private static final QName[] understoodHeaderNames = new QName[] {};
  private CombinedSerializer ns1_mySnapPointToNearestFoundWaterBody_LiteralSerializer;
  private CombinedSerializer ns1_mySnapPointToNearestFoundWaterBodyResponse_LiteralSerializer;
  private CombinedSerializer ns1_myFindWaterBodiesWithinLocality_LiteralSerializer;
  private CombinedSerializer ns1_myFindWaterBodiesWithinLocalityResponse_LiteralSerializer;
  private CombinedSerializer ns1_myGeoref_LiteralSerializer;
  private CombinedSerializer ns1_myGeorefResponse_LiteralSerializer;
  private CombinedSerializer ns1_mySnapPointToNearestFoundWaterBody2_LiteralSerializer;
  private CombinedSerializer ns1_mySnapPointToNearestFoundWaterBody2Response_LiteralSerializer;
  private CombinedSerializer ns1_myGeoref2_LiteralSerializer;
  private CombinedSerializer ns1_myGeoref2Response_LiteralSerializer;
  private CombinedSerializer ns1_myGeoref3_LiteralSerializer;
  private CombinedSerializer ns1_myGeoref3Response_LiteralSerializer;

  /*
   * public constructor
   */
  public GeolocatesvcSoap_Stub(HandlerChain handlerChain) {
    super(handlerChain);
    _setProperty(ENDPOINT_ADDRESS_PROPERTY,
        "http://199.227.217.250:8080/webservices/geolocatesvc/geolocatesvc.asmx");
  }

  public java.lang.String _getEncodingStyle() {
    return SOAPNamespaceConstants.ENCODING;
  }

  public java.lang.String _getImplicitEnvelopeEncodingStyle() {
    return "";
  }

  /*
   * This method returns an array containing the names of the headers we
   * understand.
   */
  public javax.xml.namespace.QName[] _getUnderstoodHeaders() {
    return understoodHeaderNames;
  }

  public void _initialize(InternalTypeMappingRegistry registry)
      throws Exception {
    super._initialize(registry);
    ns1_mySnapPointToNearestFoundWaterBodyResponse_LiteralSerializer = (CombinedSerializer) registry
        .getSerializer(
            "",
            edu.tulane.geolocate.SnapPointToNearestFoundWaterBodyResponse.class,
            ns1_SnapPointToNearestFoundWaterBodyResponse_TYPE_QNAME);
    ns1_myGeoref_LiteralSerializer = (CombinedSerializer) registry
        .getSerializer("", edu.tulane.geolocate.Georef.class,
            ns1_Georef_TYPE_QNAME);
    ns1_myGeoref3Response_LiteralSerializer = (CombinedSerializer) registry
        .getSerializer("", edu.tulane.geolocate.Georef3Response.class,
            ns1_Georef3Response_TYPE_QNAME);
    ns1_myGeoref2Response_LiteralSerializer = (CombinedSerializer) registry
        .getSerializer("", edu.tulane.geolocate.Georef2Response.class,
            ns1_Georef2Response_TYPE_QNAME);
    ns1_myFindWaterBodiesWithinLocalityResponse_LiteralSerializer = (CombinedSerializer) registry
        .getSerializer("",
            edu.tulane.geolocate.FindWaterBodiesWithinLocalityResponse.class,
            ns1_FindWaterBodiesWithinLocalityResponse_TYPE_QNAME);
    ns1_myGeoref2_LiteralSerializer = (CombinedSerializer) registry
        .getSerializer("", edu.tulane.geolocate.Georef2.class,
            ns1_Georef2_TYPE_QNAME);
    ns1_mySnapPointToNearestFoundWaterBody2_LiteralSerializer = (CombinedSerializer) registry
        .getSerializer("",
            edu.tulane.geolocate.SnapPointToNearestFoundWaterBody2.class,
            ns1_SnapPointToNearestFoundWaterBody2_TYPE_QNAME);
    ns1_myFindWaterBodiesWithinLocality_LiteralSerializer = (CombinedSerializer) registry
        .getSerializer("",
            edu.tulane.geolocate.FindWaterBodiesWithinLocality.class,
            ns1_FindWaterBodiesWithinLocality_TYPE_QNAME);
    ns1_mySnapPointToNearestFoundWaterBody2Response_LiteralSerializer = (CombinedSerializer) registry
        .getSerializer(
            "",
            edu.tulane.geolocate.SnapPointToNearestFoundWaterBody2Response.class,
            ns1_SnapPointToNearestFoundWaterBody2Response_TYPE_QNAME);
    ns1_myGeoref3_LiteralSerializer = (CombinedSerializer) registry
        .getSerializer("", edu.tulane.geolocate.Georef3.class,
            ns1_Georef3_TYPE_QNAME);
    ns1_myGeorefResponse_LiteralSerializer = (CombinedSerializer) registry
        .getSerializer("", edu.tulane.geolocate.GeorefResponse.class,
            ns1_GeorefResponse_TYPE_QNAME);
    ns1_mySnapPointToNearestFoundWaterBody_LiteralSerializer = (CombinedSerializer) registry
        .getSerializer("",
            edu.tulane.geolocate.SnapPointToNearestFoundWaterBody.class,
            ns1_SnapPointToNearestFoundWaterBody_TYPE_QNAME);
  }

  public void _setEncodingStyle(java.lang.String encodingStyle) {
    throw new UnsupportedOperationException("cannot set encoding style");
  }

  /*
   * implementation of findWaterBodiesWithinLocality
   */
  public edu.tulane.geolocate.ArrayOfString findWaterBodiesWithinLocality(
      edu.tulane.geolocate.LocalityDescription localityDescription)
      throws java.rmi.RemoteException {

    try {

      StreamingSenderState _state = _start(_handlerChain);

      InternalSOAPMessage _request = _state.getRequest();
      _request.setOperationCode(FindWaterBodiesWithinLocality_OPCODE);

      edu.tulane.geolocate.FindWaterBodiesWithinLocality _myFindWaterBodiesWithinLocality = new edu.tulane.geolocate.FindWaterBodiesWithinLocality();
      _myFindWaterBodiesWithinLocality
          .setLocalityDescription(localityDescription);

      SOAPBlockInfo _bodyBlock = new SOAPBlockInfo(
          ns1_FindWaterBodiesWithinLocality_FindWaterBodiesWithinLocality_QNAME);
      _bodyBlock.setValue(_myFindWaterBodiesWithinLocality);
      _bodyBlock
          .setSerializer(ns1_myFindWaterBodiesWithinLocality_LiteralSerializer);
      _request.setBody(_bodyBlock);

      _state
          .getMessageContext()
          .setProperty(HttpClientTransport.HTTP_SOAPACTION_PROPERTY,
              "http://www.museum.tulane.edu/webservices/FindWaterBodiesWithinLocality");

      _send((java.lang.String) _getProperty(ENDPOINT_ADDRESS_PROPERTY), _state);

      edu.tulane.geolocate.FindWaterBodiesWithinLocalityResponse _result = null;
      java.lang.Object _responseObj = _state.getResponse().getBody().getValue();
      if (_responseObj instanceof SOAPDeserializationState) {
        _result = (edu.tulane.geolocate.FindWaterBodiesWithinLocalityResponse) ((SOAPDeserializationState) _responseObj)
            .getInstance();
      } else {
        _result = (edu.tulane.geolocate.FindWaterBodiesWithinLocalityResponse) _responseObj;
      }

      return _result.getFindWaterBodiesWithinLocalityResult();

    } catch (RemoteException e) {
      // let this one through unchanged
      throw e;
    } catch (JAXRPCException e) {
      throw new RemoteException(e.getMessage(), e);
    } catch (Exception e) {
      if (e instanceof RuntimeException) {
        throw (RuntimeException) e;
      } else {
        throw new RemoteException(e.getMessage(), e);
      }
    }
  }

  /*
   * implementation of georef
   */
  public edu.tulane.geolocate.Georef_Result_Set georef(
      edu.tulane.geolocate.LocalityDescription localityDescription,
      boolean hwyX, boolean findWaterbody) throws java.rmi.RemoteException {

    try {

      StreamingSenderState _state = _start(_handlerChain);

      InternalSOAPMessage _request = _state.getRequest();
      _request.setOperationCode(Georef_OPCODE);

      edu.tulane.geolocate.Georef _myGeoref = new edu.tulane.geolocate.Georef();
      _myGeoref.setLocalityDescription(localityDescription);
      _myGeoref.setHwyX(hwyX);
      _myGeoref.setFindWaterbody(findWaterbody);

      SOAPBlockInfo _bodyBlock = new SOAPBlockInfo(ns1_Georef_Georef_QNAME);
      _bodyBlock.setValue(_myGeoref);
      _bodyBlock.setSerializer(ns1_myGeoref_LiteralSerializer);
      _request.setBody(_bodyBlock);

      _state.getMessageContext().setProperty(
          HttpClientTransport.HTTP_SOAPACTION_PROPERTY,
          "http://www.museum.tulane.edu/webservices/Georef");

      _send((java.lang.String) _getProperty(ENDPOINT_ADDRESS_PROPERTY), _state);

      edu.tulane.geolocate.GeorefResponse _result = null;
      java.lang.Object _responseObj = _state.getResponse().getBody().getValue();
      if (_responseObj instanceof SOAPDeserializationState) {
        _result = (edu.tulane.geolocate.GeorefResponse) ((SOAPDeserializationState) _responseObj)
            .getInstance();
      } else {
        _result = (edu.tulane.geolocate.GeorefResponse) _responseObj;
      }

      return _result.getResult();

    } catch (RemoteException e) {
      // let this one through unchanged
      throw e;
    } catch (JAXRPCException e) {
      throw new RemoteException(e.getMessage(), e);
    } catch (Exception e) {
      if (e instanceof RuntimeException) {
        throw (RuntimeException) e;
      } else {
        throw new RemoteException(e.getMessage(), e);
      }
    }
  }

  /*
   * implementation of georef2
   */
  public edu.tulane.geolocate.Georef_Result_Set georef2(
      java.lang.String country, java.lang.String state,
      java.lang.String county, java.lang.String localityString, boolean hwyX,
      boolean findWaterbody) throws java.rmi.RemoteException {

    try {

      StreamingSenderState _state = _start(_handlerChain);

      InternalSOAPMessage _request = _state.getRequest();
      _request.setOperationCode(Georef2_OPCODE);

      edu.tulane.geolocate.Georef2 _myGeoref2 = new edu.tulane.geolocate.Georef2();
      _myGeoref2.setCountry(country);
      _myGeoref2.setState(state);
      _myGeoref2.setCounty(county);
      _myGeoref2.setLocalityString(localityString);
      _myGeoref2.setHwyX(hwyX);
      _myGeoref2.setFindWaterbody(findWaterbody);

      SOAPBlockInfo _bodyBlock = new SOAPBlockInfo(ns1_Georef2_Georef2_QNAME);
      _bodyBlock.setValue(_myGeoref2);
      _bodyBlock.setSerializer(ns1_myGeoref2_LiteralSerializer);
      _request.setBody(_bodyBlock);

      _state.getMessageContext().setProperty(
          HttpClientTransport.HTTP_SOAPACTION_PROPERTY,
          "http://www.museum.tulane.edu/webservices/Georef2");

      _send((java.lang.String) _getProperty(ENDPOINT_ADDRESS_PROPERTY), _state);

      edu.tulane.geolocate.Georef2Response _result = null;
      java.lang.Object _responseObj = _state.getResponse().getBody().getValue();
      if (_responseObj instanceof SOAPDeserializationState) {
        _result = (edu.tulane.geolocate.Georef2Response) ((SOAPDeserializationState) _responseObj)
            .getInstance();
      } else {
        _result = (edu.tulane.geolocate.Georef2Response) _responseObj;
      }

      return _result.getResult();

    } catch (RemoteException e) {
      // let this one through unchanged
      throw e;
    } catch (JAXRPCException e) {
      throw new RemoteException(e.getMessage(), e);
    } catch (Exception e) {
      if (e instanceof RuntimeException) {
        throw (RuntimeException) e;
      } else {
        throw new RemoteException(e.getMessage(), e);
      }
    }
  }

  /*
   * implementation of georef3
   */
  public edu.tulane.geolocate.Georef_Result_Set georef3(
      java.lang.String vLocality, java.lang.String vGeography, boolean hwyX,
      boolean findWaterbody) throws java.rmi.RemoteException {

    try {

      StreamingSenderState _state = _start(_handlerChain);

      InternalSOAPMessage _request = _state.getRequest();
      _request.setOperationCode(Georef3_OPCODE);

      edu.tulane.geolocate.Georef3 _myGeoref3 = new edu.tulane.geolocate.Georef3();
      _myGeoref3.setVLocality(vLocality);
      _myGeoref3.setVGeography(vGeography);
      _myGeoref3.setHwyX(hwyX);
      _myGeoref3.setFindWaterbody(findWaterbody);

      SOAPBlockInfo _bodyBlock = new SOAPBlockInfo(ns1_Georef3_Georef3_QNAME);
      _bodyBlock.setValue(_myGeoref3);
      _bodyBlock.setSerializer(ns1_myGeoref3_LiteralSerializer);
      _request.setBody(_bodyBlock);

      _state.getMessageContext().setProperty(
          HttpClientTransport.HTTP_SOAPACTION_PROPERTY,
          "http://www.museum.tulane.edu/webservices/Georef3");

      _send((java.lang.String) _getProperty(ENDPOINT_ADDRESS_PROPERTY), _state);

      edu.tulane.geolocate.Georef3Response _result = null;
      java.lang.Object _responseObj = _state.getResponse().getBody().getValue();
      if (_responseObj instanceof SOAPDeserializationState) {
        _result = (edu.tulane.geolocate.Georef3Response) ((SOAPDeserializationState) _responseObj)
            .getInstance();
      } else {
        _result = (edu.tulane.geolocate.Georef3Response) _responseObj;
      }

      return _result.getResult();

    } catch (RemoteException e) {
      // let this one through unchanged
      throw e;
    } catch (JAXRPCException e) {
      throw new RemoteException(e.getMessage(), e);
    } catch (Exception e) {
      if (e instanceof RuntimeException) {
        throw (RuntimeException) e;
      } else {
        throw new RemoteException(e.getMessage(), e);
      }
    }
  }

  /*
   * implementation of snapPointToNearestFoundWaterBody
   */
  public edu.tulane.geolocate.GeographicPoint snapPointToNearestFoundWaterBody(
      edu.tulane.geolocate.LocalityDescription localityDescription,
      edu.tulane.geolocate.GeographicPoint WGS84Coordinate)
      throws java.rmi.RemoteException {

    try {

      StreamingSenderState _state = _start(_handlerChain);

      InternalSOAPMessage _request = _state.getRequest();
      _request.setOperationCode(SnapPointToNearestFoundWaterBody_OPCODE);

      edu.tulane.geolocate.SnapPointToNearestFoundWaterBody _mySnapPointToNearestFoundWaterBody = new edu.tulane.geolocate.SnapPointToNearestFoundWaterBody();
      _mySnapPointToNearestFoundWaterBody
          .setLocalityDescription(localityDescription);
      _mySnapPointToNearestFoundWaterBody.setWGS84Coordinate(WGS84Coordinate);

      SOAPBlockInfo _bodyBlock = new SOAPBlockInfo(
          ns1_SnapPointToNearestFoundWaterBody_SnapPointToNearestFoundWaterBody_QNAME);
      _bodyBlock.setValue(_mySnapPointToNearestFoundWaterBody);
      _bodyBlock
          .setSerializer(ns1_mySnapPointToNearestFoundWaterBody_LiteralSerializer);
      _request.setBody(_bodyBlock);

      _state
          .getMessageContext()
          .setProperty(HttpClientTransport.HTTP_SOAPACTION_PROPERTY,
              "http://www.museum.tulane.edu/webservices/SnapPointToNearestFoundWaterBody");

      _send((java.lang.String) _getProperty(ENDPOINT_ADDRESS_PROPERTY), _state);

      edu.tulane.geolocate.SnapPointToNearestFoundWaterBodyResponse _result = null;
      java.lang.Object _responseObj = _state.getResponse().getBody().getValue();
      if (_responseObj instanceof SOAPDeserializationState) {
        _result = (edu.tulane.geolocate.SnapPointToNearestFoundWaterBodyResponse) ((SOAPDeserializationState) _responseObj)
            .getInstance();
      } else {
        _result = (edu.tulane.geolocate.SnapPointToNearestFoundWaterBodyResponse) _responseObj;
      }

      return _result.getWGS84Coordinate();

    } catch (RemoteException e) {
      // let this one through unchanged
      throw e;
    } catch (JAXRPCException e) {
      throw new RemoteException(e.getMessage(), e);
    } catch (Exception e) {
      if (e instanceof RuntimeException) {
        throw (RuntimeException) e;
      } else {
        throw new RemoteException(e.getMessage(), e);
      }
    }
  }

  /*
   * implementation of snapPointToNearestFoundWaterBody2
   */
  public edu.tulane.geolocate.GeographicPoint snapPointToNearestFoundWaterBody2(
      java.lang.String country, java.lang.String state,
      java.lang.String county, java.lang.String localityString,
      double WGS84Latitude, double WGS84Longitude)
      throws java.rmi.RemoteException {

    try {

      StreamingSenderState _state = _start(_handlerChain);

      InternalSOAPMessage _request = _state.getRequest();
      _request.setOperationCode(SnapPointToNearestFoundWaterBody2_OPCODE);

      edu.tulane.geolocate.SnapPointToNearestFoundWaterBody2 _mySnapPointToNearestFoundWaterBody2 = new edu.tulane.geolocate.SnapPointToNearestFoundWaterBody2();
      _mySnapPointToNearestFoundWaterBody2.setCountry(country);
      _mySnapPointToNearestFoundWaterBody2.setState(state);
      _mySnapPointToNearestFoundWaterBody2.setCounty(county);
      _mySnapPointToNearestFoundWaterBody2.setLocalityString(localityString);
      _mySnapPointToNearestFoundWaterBody2.setWGS84Latitude(WGS84Latitude);
      _mySnapPointToNearestFoundWaterBody2.setWGS84Longitude(WGS84Longitude);

      SOAPBlockInfo _bodyBlock = new SOAPBlockInfo(
          ns1_SnapPointToNearestFoundWaterBody2_SnapPointToNearestFoundWaterBody2_QNAME);
      _bodyBlock.setValue(_mySnapPointToNearestFoundWaterBody2);
      _bodyBlock
          .setSerializer(ns1_mySnapPointToNearestFoundWaterBody2_LiteralSerializer);
      _request.setBody(_bodyBlock);

      _state
          .getMessageContext()
          .setProperty(HttpClientTransport.HTTP_SOAPACTION_PROPERTY,
              "http://www.museum.tulane.edu/webservices/SnapPointToNearestFoundWaterBody2");

      _send((java.lang.String) _getProperty(ENDPOINT_ADDRESS_PROPERTY), _state);

      edu.tulane.geolocate.SnapPointToNearestFoundWaterBody2Response _result = null;
      java.lang.Object _responseObj = _state.getResponse().getBody().getValue();
      if (_responseObj instanceof SOAPDeserializationState) {
        _result = (edu.tulane.geolocate.SnapPointToNearestFoundWaterBody2Response) ((SOAPDeserializationState) _responseObj)
            .getInstance();
      } else {
        _result = (edu.tulane.geolocate.SnapPointToNearestFoundWaterBody2Response) _responseObj;
      }

      return _result.getWGS84Coordinate();

    } catch (RemoteException e) {
      // let this one through unchanged
      throw e;
    } catch (JAXRPCException e) {
      throw new RemoteException(e.getMessage(), e);
    } catch (Exception e) {
      if (e instanceof RuntimeException) {
        throw (RuntimeException) e;
      } else {
        throw new RemoteException(e.getMessage(), e);
      }
    }
  }

  /*
   * This method deserializes the body of the FindWaterBodiesWithinLocality
   * operation.
   */
  private void _deserialize_FindWaterBodiesWithinLocality(XMLReader bodyReader,
      SOAPDeserializationContext deserializationContext,
      StreamingSenderState state) throws Exception {
    java.lang.Object myFindWaterBodiesWithinLocalityResponseObj = ns1_myFindWaterBodiesWithinLocalityResponse_LiteralSerializer
        .deserialize(
            ns1_FindWaterBodiesWithinLocality_FindWaterBodiesWithinLocalityResponse_QNAME,
            bodyReader, deserializationContext);

    SOAPBlockInfo bodyBlock = new SOAPBlockInfo(
        ns1_FindWaterBodiesWithinLocality_FindWaterBodiesWithinLocalityResponse_QNAME);
    bodyBlock.setValue(myFindWaterBodiesWithinLocalityResponseObj);
    state.getResponse().setBody(bodyBlock);
  }

  /*
   * This method deserializes the body of the Georef operation.
   */
  private void _deserialize_Georef(XMLReader bodyReader,
      SOAPDeserializationContext deserializationContext,
      StreamingSenderState state) throws Exception {
    java.lang.Object myGeorefResponseObj = ns1_myGeorefResponse_LiteralSerializer
        .deserialize(ns1_Georef_GeorefResponse_QNAME, bodyReader,
            deserializationContext);

    SOAPBlockInfo bodyBlock = new SOAPBlockInfo(ns1_Georef_GeorefResponse_QNAME);
    bodyBlock.setValue(myGeorefResponseObj);
    state.getResponse().setBody(bodyBlock);
  }

  /*
   * This method deserializes the body of the Georef2 operation.
   */
  private void _deserialize_Georef2(XMLReader bodyReader,
      SOAPDeserializationContext deserializationContext,
      StreamingSenderState state) throws Exception {
    java.lang.Object myGeoref2ResponseObj = ns1_myGeoref2Response_LiteralSerializer
        .deserialize(ns1_Georef2_Georef2Response_QNAME, bodyReader,
            deserializationContext);

    SOAPBlockInfo bodyBlock = new SOAPBlockInfo(
        ns1_Georef2_Georef2Response_QNAME);
    bodyBlock.setValue(myGeoref2ResponseObj);
    state.getResponse().setBody(bodyBlock);
  }

  /*
   * This method deserializes the body of the Georef3 operation.
   */
  private void _deserialize_Georef3(XMLReader bodyReader,
      SOAPDeserializationContext deserializationContext,
      StreamingSenderState state) throws Exception {
    java.lang.Object myGeoref3ResponseObj = ns1_myGeoref3Response_LiteralSerializer
        .deserialize(ns1_Georef3_Georef3Response_QNAME, bodyReader,
            deserializationContext);

    SOAPBlockInfo bodyBlock = new SOAPBlockInfo(
        ns1_Georef3_Georef3Response_QNAME);
    bodyBlock.setValue(myGeoref3ResponseObj);
    state.getResponse().setBody(bodyBlock);
  }

  /*
   * This method deserializes the body of the SnapPointToNearestFoundWaterBody
   * operation.
   */
  private void _deserialize_SnapPointToNearestFoundWaterBody(
      XMLReader bodyReader, SOAPDeserializationContext deserializationContext,
      StreamingSenderState state) throws Exception {
    java.lang.Object mySnapPointToNearestFoundWaterBodyResponseObj = ns1_mySnapPointToNearestFoundWaterBodyResponse_LiteralSerializer
        .deserialize(
            ns1_SnapPointToNearestFoundWaterBody_SnapPointToNearestFoundWaterBodyResponse_QNAME,
            bodyReader, deserializationContext);

    SOAPBlockInfo bodyBlock = new SOAPBlockInfo(
        ns1_SnapPointToNearestFoundWaterBody_SnapPointToNearestFoundWaterBodyResponse_QNAME);
    bodyBlock.setValue(mySnapPointToNearestFoundWaterBodyResponseObj);
    state.getResponse().setBody(bodyBlock);
  }

  /*
   * This method deserializes the body of the SnapPointToNearestFoundWaterBody2
   * operation.
   */
  private void _deserialize_SnapPointToNearestFoundWaterBody2(
      XMLReader bodyReader, SOAPDeserializationContext deserializationContext,
      StreamingSenderState state) throws Exception {
    java.lang.Object mySnapPointToNearestFoundWaterBody2ResponseObj = ns1_mySnapPointToNearestFoundWaterBody2Response_LiteralSerializer
        .deserialize(
            ns1_SnapPointToNearestFoundWaterBody2_SnapPointToNearestFoundWaterBody2Response_QNAME,
            bodyReader, deserializationContext);

    SOAPBlockInfo bodyBlock = new SOAPBlockInfo(
        ns1_SnapPointToNearestFoundWaterBody2_SnapPointToNearestFoundWaterBody2Response_QNAME);
    bodyBlock.setValue(mySnapPointToNearestFoundWaterBody2ResponseObj);
    state.getResponse().setBody(bodyBlock);
  }

  protected java.lang.String _getDefaultEnvelopeEncodingStyle() {
    return null;
  }

  /*
   * This method returns an array containing (prefix, nsURI) pairs.
   */
  protected java.lang.String[] _getNamespaceDeclarations() {
    return myNamespace_declarations;
  }

  protected void _preHandlingHook(StreamingSenderState state) throws Exception {
    super._preHandlingHook(state);
  }

  protected boolean _preRequestSendingHook(StreamingSenderState state)
      throws Exception {
    boolean bool = false;
    bool = super._preRequestSendingHook(state);
    return bool;
  }

  /*
   * this method deserializes the request/response structure in the body
   */
  protected void _readFirstBodyElement(XMLReader bodyReader,
      SOAPDeserializationContext deserializationContext,
      StreamingSenderState state) throws Exception {
    int opcode = state.getRequest().getOperationCode();
    switch (opcode) {
    case SnapPointToNearestFoundWaterBody_OPCODE:
      _deserialize_SnapPointToNearestFoundWaterBody(bodyReader,
          deserializationContext, state);
      break;
    case FindWaterBodiesWithinLocality_OPCODE:
      _deserialize_FindWaterBodiesWithinLocality(bodyReader,
          deserializationContext, state);
      break;
    case Georef_OPCODE:
      _deserialize_Georef(bodyReader, deserializationContext, state);
      break;
    case SnapPointToNearestFoundWaterBody2_OPCODE:
      _deserialize_SnapPointToNearestFoundWaterBody2(bodyReader,
          deserializationContext, state);
      break;
    case Georef2_OPCODE:
      _deserialize_Georef2(bodyReader, deserializationContext, state);
      break;
    case Georef3_OPCODE:
      _deserialize_Georef3(bodyReader, deserializationContext, state);
      break;
    default:
      throw new SenderException("sender.response.unrecognizedOperation",
          java.lang.Integer.toString(opcode));
    }
  }
}
