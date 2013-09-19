/**
*    Copyright (c) 2011-2014, OpenIoT
*   
*    This file is part of OpenIoT.
*
*    OpenIoT is free software: you can redistribute it and/or modify
*    it under the terms of the GNU Lesser General Public License as published by
*    the Free Software Foundation, version 3 of the License.
*
*    OpenIoT is distributed in the hope that it will be useful,
*    but WITHOUT ANY WARRANTY; without even the implied warranty of
*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*    GNU Lesser General Public License for more details.
*
*    You should have received a copy of the GNU Lesser General Public License
*    along with OpenIoT.  If not, see <http://www.gnu.org/licenses/>.
*
*     Contact: OpenIoT mailto: info@openiot.eu
*/


/**
 * GSNWebServiceMessageReceiverInOut.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.4.1  Built on : Aug 13, 2008 (05:03:35 LKT)
 */
        package gsn.webservice.standard;

        /**
        *  GSNWebServiceMessageReceiverInOut message receiver
        */

        public class GSNWebServiceMessageReceiverInOut extends org.apache.axis2.receivers.AbstractInOutMessageReceiver{


        public void invokeBusinessLogic(org.apache.axis2.context.MessageContext msgContext, org.apache.axis2.context.MessageContext newMsgContext)
        throws org.apache.axis2.AxisFault{

        try {

        // get the implementation class for the Web Service
        Object obj = getTheImplementationObject(msgContext);

        GSNWebServiceSkeleton skel = (GSNWebServiceSkeleton)obj;
        //Out Envelop
        org.apache.axiom.soap.SOAPEnvelope envelope = null;
        //Find the axisOperation that has been set by the Dispatch phase.
        org.apache.axis2.description.AxisOperation op = msgContext.getOperationContext().getAxisOperation();
        if (op == null) {
        throw new org.apache.axis2.AxisFault("Operation is not located, if this is doclit style the SOAP-ACTION should specified via the SOAP Action to use the RawXMLProvider");
        }

        java.lang.String methodName;
        if((op.getName() != null) && ((methodName = org.apache.axis2.util.JavaUtils.xmlNameToJava(op.getName().getLocalPart())) != null)){

        

            if("getVirtualSensorsDetails".equals(methodName)){
                
                gsn.webservice.standard.GetVirtualSensorsDetailsResponse getVirtualSensorsDetailsResponse1 = null;
	                        gsn.webservice.standard.GetVirtualSensorsDetails wrappedParam =
                                                             (gsn.webservice.standard.GetVirtualSensorsDetails)fromOM(
                                    msgContext.getEnvelope().getBody().getFirstElement(),
                                    gsn.webservice.standard.GetVirtualSensorsDetails.class,
                                    getEnvelopeNamespaces(msgContext.getEnvelope()));
                                                
                                               getVirtualSensorsDetailsResponse1 =
                                                   
                                                   
                                                         skel.getVirtualSensorsDetails(wrappedParam)
                                                    ;
                                            
                                        envelope = toEnvelope(getSOAPFactory(msgContext), getVirtualSensorsDetailsResponse1, false);
                                    } else 

            if("getNextData".equals(methodName)){
                
                gsn.webservice.standard.GetNextDataResponse getNextDataResponse3 = null;
	                        gsn.webservice.standard.GetNextData wrappedParam =
                                                             (gsn.webservice.standard.GetNextData)fromOM(
                                    msgContext.getEnvelope().getBody().getFirstElement(),
                                    gsn.webservice.standard.GetNextData.class,
                                    getEnvelopeNamespaces(msgContext.getEnvelope()));
                                                
                                               getNextDataResponse3 =
                                                   
                                                   
                                                         skel.getNextData(wrappedParam)
                                                    ;
                                            
                                        envelope = toEnvelope(getSOAPFactory(msgContext), getNextDataResponse3, false);
                                    } else 

            if("listWrapperURLs".equals(methodName)){
                
                gsn.webservice.standard.ListWrapperURLsResponse listWrapperURLsResponse5 = null;
	                        gsn.webservice.standard.ListWrapperURLs wrappedParam =
                                                             (gsn.webservice.standard.ListWrapperURLs)fromOM(
                                    msgContext.getEnvelope().getBody().getFirstElement(),
                                    gsn.webservice.standard.ListWrapperURLs.class,
                                    getEnvelopeNamespaces(msgContext.getEnvelope()));
                                                
                                               listWrapperURLsResponse5 =
                                                   
                                                   
                                                         skel.listWrapperURLs(wrappedParam)
                                                    ;
                                            
                                        envelope = toEnvelope(getSOAPFactory(msgContext), listWrapperURLsResponse5, false);
                                    } else 

            if("getLatestMultiData".equals(methodName)){
                
                gsn.webservice.standard.GetLatestMultiDataResponse getLatestMultiDataResponse7 = null;
	                        gsn.webservice.standard.GetLatestMultiData wrappedParam =
                                                             (gsn.webservice.standard.GetLatestMultiData)fromOM(
                                    msgContext.getEnvelope().getBody().getFirstElement(),
                                    gsn.webservice.standard.GetLatestMultiData.class,
                                    getEnvelopeNamespaces(msgContext.getEnvelope()));
                                                
                                               getLatestMultiDataResponse7 =
                                                   
                                                   
                                                         skel.getLatestMultiData(wrappedParam)
                                                    ;
                                            
                                        envelope = toEnvelope(getSOAPFactory(msgContext), getLatestMultiDataResponse7, false);
                                    } else 

            if("unregisterQuery".equals(methodName)){
                
                gsn.webservice.standard.UnregisterQueryResponse unregisterQueryResponse9 = null;
	                        gsn.webservice.standard.UnregisterQuery wrappedParam =
                                                             (gsn.webservice.standard.UnregisterQuery)fromOM(
                                    msgContext.getEnvelope().getBody().getFirstElement(),
                                    gsn.webservice.standard.UnregisterQuery.class,
                                    getEnvelopeNamespaces(msgContext.getEnvelope()));
                                                
                                               unregisterQueryResponse9 =
                                                   
                                                   
                                                         skel.unregisterQuery(wrappedParam)
                                                    ;
                                            
                                        envelope = toEnvelope(getSOAPFactory(msgContext), unregisterQueryResponse9, false);
                                    } else 

            if("createVirtualSensor".equals(methodName)){
                
                gsn.webservice.standard.CreateVirtualSensorResponse createVirtualSensorResponse11 = null;
	                        gsn.webservice.standard.CreateVirtualSensor wrappedParam =
                                                             (gsn.webservice.standard.CreateVirtualSensor)fromOM(
                                    msgContext.getEnvelope().getBody().getFirstElement(),
                                    gsn.webservice.standard.CreateVirtualSensor.class,
                                    getEnvelopeNamespaces(msgContext.getEnvelope()));
                                                
                                               createVirtualSensorResponse11 =
                                                   
                                                   
                                                         skel.createVirtualSensor(wrappedParam)
                                                    ;
                                            
                                        envelope = toEnvelope(getSOAPFactory(msgContext), createVirtualSensorResponse11, false);
                                    } else 

            if("getMultiData".equals(methodName)){
                
                gsn.webservice.standard.GetMultiDataResponse getMultiDataResponse13 = null;
	                        gsn.webservice.standard.GetMultiData wrappedParam =
                                                             (gsn.webservice.standard.GetMultiData)fromOM(
                                    msgContext.getEnvelope().getBody().getFirstElement(),
                                    gsn.webservice.standard.GetMultiData.class,
                                    getEnvelopeNamespaces(msgContext.getEnvelope()));
                                                
                                               getMultiDataResponse13 =
                                                   
                                                   
                                                         skel.getMultiData(wrappedParam)
                                                    ;
                                            
                                        envelope = toEnvelope(getSOAPFactory(msgContext), getMultiDataResponse13, false);
                                    } else 

            if("registerQuery".equals(methodName)){
                
                gsn.webservice.standard.RegisterQueryResponse registerQueryResponse15 = null;
	                        gsn.webservice.standard.RegisterQuery wrappedParam =
                                                             (gsn.webservice.standard.RegisterQuery)fromOM(
                                    msgContext.getEnvelope().getBody().getFirstElement(),
                                    gsn.webservice.standard.RegisterQuery.class,
                                    getEnvelopeNamespaces(msgContext.getEnvelope()));
                                                
                                               registerQueryResponse15 =
                                                   
                                                   
                                                         skel.registerQuery(wrappedParam)
                                                    ;
                                            
                                        envelope = toEnvelope(getSOAPFactory(msgContext), registerQueryResponse15, false);
                                    } else 

            if("getContainerInfo".equals(methodName)){
                
                gsn.webservice.standard.GetContainerInfoResponse getContainerInfoResponse17 = null;
	                        gsn.webservice.standard.GetContainerInfo wrappedParam =
                                                             (gsn.webservice.standard.GetContainerInfo)fromOM(
                                    msgContext.getEnvelope().getBody().getFirstElement(),
                                    gsn.webservice.standard.GetContainerInfo.class,
                                    getEnvelopeNamespaces(msgContext.getEnvelope()));
                                                
                                               getContainerInfoResponse17 =
                                                   
                                                   
                                                         skel.getContainerInfo(wrappedParam)
                                                    ;
                                            
                                        envelope = toEnvelope(getSOAPFactory(msgContext), getContainerInfoResponse17, false);
                                    } else 

            if("listVirtualSensorNames".equals(methodName)){
                
                gsn.webservice.standard.ListVirtualSensorNamesResponse listVirtualSensorNamesResponse19 = null;
	                        gsn.webservice.standard.ListVirtualSensorNames wrappedParam =
                                                             (gsn.webservice.standard.ListVirtualSensorNames)fromOM(
                                    msgContext.getEnvelope().getBody().getFirstElement(),
                                    gsn.webservice.standard.ListVirtualSensorNames.class,
                                    getEnvelopeNamespaces(msgContext.getEnvelope()));
                                                
                                               listVirtualSensorNamesResponse19 =
                                                   
                                                   
                                                         skel.listVirtualSensorNames(wrappedParam)
                                                    ;
                                            
                                        envelope = toEnvelope(getSOAPFactory(msgContext), listVirtualSensorNamesResponse19, false);
                                    } else 

            if("deleteVirtualSensor".equals(methodName)){
                
                gsn.webservice.standard.DeleteVirtualSensorResponse deleteVirtualSensorResponse21 = null;
	                        gsn.webservice.standard.DeleteVirtualSensor wrappedParam =
                                                             (gsn.webservice.standard.DeleteVirtualSensor)fromOM(
                                    msgContext.getEnvelope().getBody().getFirstElement(),
                                    gsn.webservice.standard.DeleteVirtualSensor.class,
                                    getEnvelopeNamespaces(msgContext.getEnvelope()));
                                                
                                               deleteVirtualSensorResponse21 =
                                                   
                                                   
                                                         skel.deleteVirtualSensor(wrappedParam)
                                                    ;
                                            
                                        envelope = toEnvelope(getSOAPFactory(msgContext), deleteVirtualSensorResponse21, false);
                                    
            } else {
              throw new java.lang.RuntimeException("method not found");
            }
        

        newMsgContext.setEnvelope(envelope);
        }
        }
        catch (java.lang.Exception e) {
        throw org.apache.axis2.AxisFault.makeFault(e);
        }
        }
        
        //
            private  org.apache.axiom.om.OMElement  toOM(gsn.webservice.standard.GetVirtualSensorsDetails param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(gsn.webservice.standard.GetVirtualSensorsDetails.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(gsn.webservice.standard.GetVirtualSensorsDetailsResponse param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(gsn.webservice.standard.GetVirtualSensorsDetailsResponse.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(gsn.webservice.standard.GetNextData param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(gsn.webservice.standard.GetNextData.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(gsn.webservice.standard.GetNextDataResponse param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(gsn.webservice.standard.GetNextDataResponse.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(gsn.webservice.standard.ListWrapperURLs param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(gsn.webservice.standard.ListWrapperURLs.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(gsn.webservice.standard.ListWrapperURLsResponse param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(gsn.webservice.standard.ListWrapperURLsResponse.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(gsn.webservice.standard.GetLatestMultiData param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(gsn.webservice.standard.GetLatestMultiData.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(gsn.webservice.standard.GetLatestMultiDataResponse param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(gsn.webservice.standard.GetLatestMultiDataResponse.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(gsn.webservice.standard.UnregisterQuery param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(gsn.webservice.standard.UnregisterQuery.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(gsn.webservice.standard.UnregisterQueryResponse param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(gsn.webservice.standard.UnregisterQueryResponse.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(gsn.webservice.standard.CreateVirtualSensor param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(gsn.webservice.standard.CreateVirtualSensor.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(gsn.webservice.standard.CreateVirtualSensorResponse param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(gsn.webservice.standard.CreateVirtualSensorResponse.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(gsn.webservice.standard.GetMultiData param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(gsn.webservice.standard.GetMultiData.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(gsn.webservice.standard.GetMultiDataResponse param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(gsn.webservice.standard.GetMultiDataResponse.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(gsn.webservice.standard.RegisterQuery param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(gsn.webservice.standard.RegisterQuery.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(gsn.webservice.standard.RegisterQueryResponse param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(gsn.webservice.standard.RegisterQueryResponse.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(gsn.webservice.standard.GetContainerInfo param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(gsn.webservice.standard.GetContainerInfo.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(gsn.webservice.standard.GetContainerInfoResponse param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(gsn.webservice.standard.GetContainerInfoResponse.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(gsn.webservice.standard.ListVirtualSensorNames param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(gsn.webservice.standard.ListVirtualSensorNames.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(gsn.webservice.standard.ListVirtualSensorNamesResponse param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(gsn.webservice.standard.ListVirtualSensorNamesResponse.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(gsn.webservice.standard.DeleteVirtualSensor param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(gsn.webservice.standard.DeleteVirtualSensor.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(gsn.webservice.standard.DeleteVirtualSensorResponse param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(gsn.webservice.standard.DeleteVirtualSensorResponse.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
                    private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, gsn.webservice.standard.GetVirtualSensorsDetailsResponse param, boolean optimizeContent)
                        throws org.apache.axis2.AxisFault{
                      try{
                          org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
                           
                                    emptyEnvelope.getBody().addChild(param.getOMElement(gsn.webservice.standard.GetVirtualSensorsDetailsResponse.MY_QNAME,factory));
                                

                         return emptyEnvelope;
                    } catch(org.apache.axis2.databinding.ADBException e){
                        throw org.apache.axis2.AxisFault.makeFault(e);
                    }
                    }
                    
                         private gsn.webservice.standard.GetVirtualSensorsDetailsResponse wrapgetVirtualSensorsDetails(){
                                gsn.webservice.standard.GetVirtualSensorsDetailsResponse wrappedElement = new gsn.webservice.standard.GetVirtualSensorsDetailsResponse();
                                return wrappedElement;
                         }
                    
                    private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, gsn.webservice.standard.GetNextDataResponse param, boolean optimizeContent)
                        throws org.apache.axis2.AxisFault{
                      try{
                          org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
                           
                                    emptyEnvelope.getBody().addChild(param.getOMElement(gsn.webservice.standard.GetNextDataResponse.MY_QNAME,factory));
                                

                         return emptyEnvelope;
                    } catch(org.apache.axis2.databinding.ADBException e){
                        throw org.apache.axis2.AxisFault.makeFault(e);
                    }
                    }
                    
                         private gsn.webservice.standard.GetNextDataResponse wrapgetNextData(){
                                gsn.webservice.standard.GetNextDataResponse wrappedElement = new gsn.webservice.standard.GetNextDataResponse();
                                return wrappedElement;
                         }
                    
                    private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, gsn.webservice.standard.ListWrapperURLsResponse param, boolean optimizeContent)
                        throws org.apache.axis2.AxisFault{
                      try{
                          org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
                           
                                    emptyEnvelope.getBody().addChild(param.getOMElement(gsn.webservice.standard.ListWrapperURLsResponse.MY_QNAME,factory));
                                

                         return emptyEnvelope;
                    } catch(org.apache.axis2.databinding.ADBException e){
                        throw org.apache.axis2.AxisFault.makeFault(e);
                    }
                    }
                    
                         private gsn.webservice.standard.ListWrapperURLsResponse wraplistWrapperURLs(){
                                gsn.webservice.standard.ListWrapperURLsResponse wrappedElement = new gsn.webservice.standard.ListWrapperURLsResponse();
                                return wrappedElement;
                         }
                    
                    private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, gsn.webservice.standard.GetLatestMultiDataResponse param, boolean optimizeContent)
                        throws org.apache.axis2.AxisFault{
                      try{
                          org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
                           
                                    emptyEnvelope.getBody().addChild(param.getOMElement(gsn.webservice.standard.GetLatestMultiDataResponse.MY_QNAME,factory));
                                

                         return emptyEnvelope;
                    } catch(org.apache.axis2.databinding.ADBException e){
                        throw org.apache.axis2.AxisFault.makeFault(e);
                    }
                    }
                    
                         private gsn.webservice.standard.GetLatestMultiDataResponse wrapgetLatestMultiData(){
                                gsn.webservice.standard.GetLatestMultiDataResponse wrappedElement = new gsn.webservice.standard.GetLatestMultiDataResponse();
                                return wrappedElement;
                         }
                    
                    private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, gsn.webservice.standard.UnregisterQueryResponse param, boolean optimizeContent)
                        throws org.apache.axis2.AxisFault{
                      try{
                          org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
                           
                                    emptyEnvelope.getBody().addChild(param.getOMElement(gsn.webservice.standard.UnregisterQueryResponse.MY_QNAME,factory));
                                

                         return emptyEnvelope;
                    } catch(org.apache.axis2.databinding.ADBException e){
                        throw org.apache.axis2.AxisFault.makeFault(e);
                    }
                    }
                    
                         private gsn.webservice.standard.UnregisterQueryResponse wrapunregisterQuery(){
                                gsn.webservice.standard.UnregisterQueryResponse wrappedElement = new gsn.webservice.standard.UnregisterQueryResponse();
                                return wrappedElement;
                         }
                    
                    private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, gsn.webservice.standard.CreateVirtualSensorResponse param, boolean optimizeContent)
                        throws org.apache.axis2.AxisFault{
                      try{
                          org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
                           
                                    emptyEnvelope.getBody().addChild(param.getOMElement(gsn.webservice.standard.CreateVirtualSensorResponse.MY_QNAME,factory));
                                

                         return emptyEnvelope;
                    } catch(org.apache.axis2.databinding.ADBException e){
                        throw org.apache.axis2.AxisFault.makeFault(e);
                    }
                    }
                    
                         private gsn.webservice.standard.CreateVirtualSensorResponse wrapcreateVirtualSensor(){
                                gsn.webservice.standard.CreateVirtualSensorResponse wrappedElement = new gsn.webservice.standard.CreateVirtualSensorResponse();
                                return wrappedElement;
                         }
                    
                    private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, gsn.webservice.standard.GetMultiDataResponse param, boolean optimizeContent)
                        throws org.apache.axis2.AxisFault{
                      try{
                          org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
                           
                                    emptyEnvelope.getBody().addChild(param.getOMElement(gsn.webservice.standard.GetMultiDataResponse.MY_QNAME,factory));
                                

                         return emptyEnvelope;
                    } catch(org.apache.axis2.databinding.ADBException e){
                        throw org.apache.axis2.AxisFault.makeFault(e);
                    }
                    }
                    
                         private gsn.webservice.standard.GetMultiDataResponse wrapgetMultiData(){
                                gsn.webservice.standard.GetMultiDataResponse wrappedElement = new gsn.webservice.standard.GetMultiDataResponse();
                                return wrappedElement;
                         }
                    
                    private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, gsn.webservice.standard.RegisterQueryResponse param, boolean optimizeContent)
                        throws org.apache.axis2.AxisFault{
                      try{
                          org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
                           
                                    emptyEnvelope.getBody().addChild(param.getOMElement(gsn.webservice.standard.RegisterQueryResponse.MY_QNAME,factory));
                                

                         return emptyEnvelope;
                    } catch(org.apache.axis2.databinding.ADBException e){
                        throw org.apache.axis2.AxisFault.makeFault(e);
                    }
                    }
                    
                         private gsn.webservice.standard.RegisterQueryResponse wrapregisterQuery(){
                                gsn.webservice.standard.RegisterQueryResponse wrappedElement = new gsn.webservice.standard.RegisterQueryResponse();
                                return wrappedElement;
                         }
                    
                    private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, gsn.webservice.standard.GetContainerInfoResponse param, boolean optimizeContent)
                        throws org.apache.axis2.AxisFault{
                      try{
                          org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
                           
                                    emptyEnvelope.getBody().addChild(param.getOMElement(gsn.webservice.standard.GetContainerInfoResponse.MY_QNAME,factory));
                                

                         return emptyEnvelope;
                    } catch(org.apache.axis2.databinding.ADBException e){
                        throw org.apache.axis2.AxisFault.makeFault(e);
                    }
                    }
                    
                         private gsn.webservice.standard.GetContainerInfoResponse wrapgetContainerInfo(){
                                gsn.webservice.standard.GetContainerInfoResponse wrappedElement = new gsn.webservice.standard.GetContainerInfoResponse();
                                return wrappedElement;
                         }
                    
                    private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, gsn.webservice.standard.ListVirtualSensorNamesResponse param, boolean optimizeContent)
                        throws org.apache.axis2.AxisFault{
                      try{
                          org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
                           
                                    emptyEnvelope.getBody().addChild(param.getOMElement(gsn.webservice.standard.ListVirtualSensorNamesResponse.MY_QNAME,factory));
                                

                         return emptyEnvelope;
                    } catch(org.apache.axis2.databinding.ADBException e){
                        throw org.apache.axis2.AxisFault.makeFault(e);
                    }
                    }
                    
                         private gsn.webservice.standard.ListVirtualSensorNamesResponse wraplistVirtualSensorNames(){
                                gsn.webservice.standard.ListVirtualSensorNamesResponse wrappedElement = new gsn.webservice.standard.ListVirtualSensorNamesResponse();
                                return wrappedElement;
                         }
                    
                    private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, gsn.webservice.standard.DeleteVirtualSensorResponse param, boolean optimizeContent)
                        throws org.apache.axis2.AxisFault{
                      try{
                          org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
                           
                                    emptyEnvelope.getBody().addChild(param.getOMElement(gsn.webservice.standard.DeleteVirtualSensorResponse.MY_QNAME,factory));
                                

                         return emptyEnvelope;
                    } catch(org.apache.axis2.databinding.ADBException e){
                        throw org.apache.axis2.AxisFault.makeFault(e);
                    }
                    }
                    
                         private gsn.webservice.standard.DeleteVirtualSensorResponse wrapdeleteVirtualSensor(){
                                gsn.webservice.standard.DeleteVirtualSensorResponse wrappedElement = new gsn.webservice.standard.DeleteVirtualSensorResponse();
                                return wrappedElement;
                         }
                    


        /**
        *  get the default envelope
        */
        private org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory){
        return factory.getDefaultEnvelope();
        }


        private  java.lang.Object fromOM(
        org.apache.axiom.om.OMElement param,
        java.lang.Class type,
        java.util.Map extraNamespaces) throws org.apache.axis2.AxisFault{

        try {
        
                if (gsn.webservice.standard.GetVirtualSensorsDetails.class.equals(type)){
                
                           return gsn.webservice.standard.GetVirtualSensorsDetails.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (gsn.webservice.standard.GetVirtualSensorsDetailsResponse.class.equals(type)){
                
                           return gsn.webservice.standard.GetVirtualSensorsDetailsResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (gsn.webservice.standard.GetNextData.class.equals(type)){
                
                           return gsn.webservice.standard.GetNextData.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (gsn.webservice.standard.GetNextDataResponse.class.equals(type)){
                
                           return gsn.webservice.standard.GetNextDataResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (gsn.webservice.standard.ListWrapperURLs.class.equals(type)){
                
                           return gsn.webservice.standard.ListWrapperURLs.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (gsn.webservice.standard.ListWrapperURLsResponse.class.equals(type)){
                
                           return gsn.webservice.standard.ListWrapperURLsResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (gsn.webservice.standard.GetLatestMultiData.class.equals(type)){
                
                           return gsn.webservice.standard.GetLatestMultiData.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (gsn.webservice.standard.GetLatestMultiDataResponse.class.equals(type)){
                
                           return gsn.webservice.standard.GetLatestMultiDataResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (gsn.webservice.standard.UnregisterQuery.class.equals(type)){
                
                           return gsn.webservice.standard.UnregisterQuery.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (gsn.webservice.standard.UnregisterQueryResponse.class.equals(type)){
                
                           return gsn.webservice.standard.UnregisterQueryResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (gsn.webservice.standard.CreateVirtualSensor.class.equals(type)){
                
                           return gsn.webservice.standard.CreateVirtualSensor.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (gsn.webservice.standard.CreateVirtualSensorResponse.class.equals(type)){
                
                           return gsn.webservice.standard.CreateVirtualSensorResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (gsn.webservice.standard.GetMultiData.class.equals(type)){
                
                           return gsn.webservice.standard.GetMultiData.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (gsn.webservice.standard.GetMultiDataResponse.class.equals(type)){
                
                           return gsn.webservice.standard.GetMultiDataResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (gsn.webservice.standard.RegisterQuery.class.equals(type)){
                
                           return gsn.webservice.standard.RegisterQuery.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (gsn.webservice.standard.RegisterQueryResponse.class.equals(type)){
                
                           return gsn.webservice.standard.RegisterQueryResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (gsn.webservice.standard.GetContainerInfo.class.equals(type)){
                
                           return gsn.webservice.standard.GetContainerInfo.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (gsn.webservice.standard.GetContainerInfoResponse.class.equals(type)){
                
                           return gsn.webservice.standard.GetContainerInfoResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (gsn.webservice.standard.ListVirtualSensorNames.class.equals(type)){
                
                           return gsn.webservice.standard.ListVirtualSensorNames.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (gsn.webservice.standard.ListVirtualSensorNamesResponse.class.equals(type)){
                
                           return gsn.webservice.standard.ListVirtualSensorNamesResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (gsn.webservice.standard.DeleteVirtualSensor.class.equals(type)){
                
                           return gsn.webservice.standard.DeleteVirtualSensor.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (gsn.webservice.standard.DeleteVirtualSensorResponse.class.equals(type)){
                
                           return gsn.webservice.standard.DeleteVirtualSensorResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
        } catch (java.lang.Exception e) {
        throw org.apache.axis2.AxisFault.makeFault(e);
        }
           return null;
        }



    

        /**
        *  A utility method that copies the namepaces from the SOAPEnvelope
        */
        private java.util.Map getEnvelopeNamespaces(org.apache.axiom.soap.SOAPEnvelope env){
        java.util.Map returnMap = new java.util.HashMap();
        java.util.Iterator namespaceIterator = env.getAllDeclaredNamespaces();
        while (namespaceIterator.hasNext()) {
        org.apache.axiom.om.OMNamespace ns = (org.apache.axiom.om.OMNamespace) namespaceIterator.next();
        returnMap.put(ns.getPrefix(),ns.getNamespaceURI());
        }
        return returnMap;
        }

        private org.apache.axis2.AxisFault createAxisFault(java.lang.Exception e) {
        org.apache.axis2.AxisFault f;
        Throwable cause = e.getCause();
        if (cause != null) {
            f = new org.apache.axis2.AxisFault(e.getMessage(), cause);
        } else {
            f = new org.apache.axis2.AxisFault(e.getMessage());
        }

        return f;
    }

        }//end of class
    