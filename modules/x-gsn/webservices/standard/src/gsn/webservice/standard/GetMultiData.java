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
 * GetMultiData.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.4.1  Built on : Aug 13, 2008 (05:03:41 LKT)
 */
            
                package gsn.webservice.standard;
            

            /**
            *  GetMultiData bean class
            */
        
        public  class GetMultiData
        implements org.apache.axis2.databinding.ADBBean{
        
                public static final javax.xml.namespace.QName MY_QNAME = new javax.xml.namespace.QName(
                "http://standard.webservice.gsn",
                "getMultiData",
                "ns3");

            

        private static java.lang.String generatePrefix(java.lang.String namespace) {
            if(namespace.equals("http://standard.webservice.gsn")){
                return "ns3";
            }
            return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
        }

        

                        /**
                        * field for AcDetails
                        */

                        
                                    protected gsn.webservice.standard.xsd.GSNWebService_ACDetails localAcDetails ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localAcDetailsTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return gsn.webservice.standard.xsd.GSNWebService_ACDetails
                           */
                           public  gsn.webservice.standard.xsd.GSNWebService_ACDetails getAcDetails(){
                               return localAcDetails;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param AcDetails
                               */
                               public void setAcDetails(gsn.webservice.standard.xsd.GSNWebService_ACDetails param){
                            
                                       if (param != null){
                                          //update the setting tracker
                                          localAcDetailsTracker = true;
                                       } else {
                                          localAcDetailsTracker = false;
                                              
                                       }
                                   
                                            this.localAcDetails=param;
                                    

                               }
                            

                        /**
                        * field for FieldSelector
                        * This was an Array!
                        */

                        
                                    protected gsn.webservice.standard.xsd.GSNWebService_FieldSelector[] localFieldSelector ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localFieldSelectorTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return gsn.webservice.standard.xsd.GSNWebService_FieldSelector[]
                           */
                           public  gsn.webservice.standard.xsd.GSNWebService_FieldSelector[] getFieldSelector(){
                               return localFieldSelector;
                           }

                           
                        


                               
                              /**
                               * validate the array for FieldSelector
                               */
                              protected void validateFieldSelector(gsn.webservice.standard.xsd.GSNWebService_FieldSelector[] param){
                             
                              }


                             /**
                              * Auto generated setter method
                              * @param param FieldSelector
                              */
                              public void setFieldSelector(gsn.webservice.standard.xsd.GSNWebService_FieldSelector[] param){
                              
                                   validateFieldSelector(param);

                               
                                          if (param != null){
                                             //update the setting tracker
                                             localFieldSelectorTracker = true;
                                          } else {
                                             localFieldSelectorTracker = false;
                                                 
                                          }
                                      
                                      this.localFieldSelector=param;
                              }

                               
                             
                             /**
                             * Auto generated add method for the array for convenience
                             * @param param gsn.webservice.standard.xsd.GSNWebService_FieldSelector
                             */
                             public void addFieldSelector(gsn.webservice.standard.xsd.GSNWebService_FieldSelector param){
                                   if (localFieldSelector == null){
                                   localFieldSelector = new gsn.webservice.standard.xsd.GSNWebService_FieldSelector[]{};
                                   }

                            
                                 //update the setting tracker
                                localFieldSelectorTracker = true;
                            

                               java.util.List list =
                            org.apache.axis2.databinding.utils.ConverterUtil.toList(localFieldSelector);
                               list.add(param);
                               this.localFieldSelector =
                             (gsn.webservice.standard.xsd.GSNWebService_FieldSelector[])list.toArray(
                            new gsn.webservice.standard.xsd.GSNWebService_FieldSelector[list.size()]);

                             }
                             

                        /**
                        * field for Conditions
                        * This was an Array!
                        */

                        
                                    protected gsn.http.datarequest.xsd.StandardCriterion[] localConditions ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localConditionsTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return gsn.http.datarequest.xsd.StandardCriterion[]
                           */
                           public  gsn.http.datarequest.xsd.StandardCriterion[] getConditions(){
                               return localConditions;
                           }

                           
                        


                               
                              /**
                               * validate the array for Conditions
                               */
                              protected void validateConditions(gsn.http.datarequest.xsd.StandardCriterion[] param){
                             
                              }


                             /**
                              * Auto generated setter method
                              * @param param Conditions
                              */
                              public void setConditions(gsn.http.datarequest.xsd.StandardCriterion[] param){
                              
                                   validateConditions(param);

                               
                                          if (param != null){
                                             //update the setting tracker
                                             localConditionsTracker = true;
                                          } else {
                                             localConditionsTracker = false;
                                                 
                                          }
                                      
                                      this.localConditions=param;
                              }

                               
                             
                             /**
                             * Auto generated add method for the array for convenience
                             * @param param gsn.http.datarequest.xsd.StandardCriterion
                             */
                             public void addConditions(gsn.http.datarequest.xsd.StandardCriterion param){
                                   if (localConditions == null){
                                   localConditions = new gsn.http.datarequest.xsd.StandardCriterion[]{};
                                   }

                            
                                 //update the setting tracker
                                localConditionsTracker = true;
                            

                               java.util.List list =
                            org.apache.axis2.databinding.utils.ConverterUtil.toList(localConditions);
                               list.add(param);
                               this.localConditions =
                             (gsn.http.datarequest.xsd.StandardCriterion[])list.toArray(
                            new gsn.http.datarequest.xsd.StandardCriterion[list.size()]);

                             }
                             

                        /**
                        * field for Aggregation
                        */

                        
                                    protected gsn.http.datarequest.xsd.AggregationCriterion localAggregation ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localAggregationTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return gsn.http.datarequest.xsd.AggregationCriterion
                           */
                           public  gsn.http.datarequest.xsd.AggregationCriterion getAggregation(){
                               return localAggregation;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param Aggregation
                               */
                               public void setAggregation(gsn.http.datarequest.xsd.AggregationCriterion param){
                            
                                       if (param != null){
                                          //update the setting tracker
                                          localAggregationTracker = true;
                                       } else {
                                          localAggregationTracker = false;
                                              
                                       }
                                   
                                            this.localAggregation=param;
                                    

                               }
                            

                        /**
                        * field for From
                        * This was an Attribute!
                        */

                        
                                    protected long localFrom ;
                                

                           /**
                           * Auto generated getter method
                           * @return long
                           */
                           public  long getFrom(){
                               return localFrom;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param From
                               */
                               public void setFrom(long param){
                            
                                            this.localFrom=param;
                                    

                               }
                            

                        /**
                        * field for To
                        * This was an Attribute!
                        */

                        
                                    protected long localTo ;
                                

                           /**
                           * Auto generated getter method
                           * @return long
                           */
                           public  long getTo(){
                               return localTo;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param To
                               */
                               public void setTo(long param){
                            
                                            this.localTo=param;
                                    

                               }
                            

                        /**
                        * field for Nb
                        * This was an Attribute!
                        */

                        
                                    protected int localNb ;
                                

                           /**
                           * Auto generated getter method
                           * @return int
                           */
                           public  int getNb(){
                               return localNb;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param Nb
                               */
                               public void setNb(int param){
                            
                                            this.localNb=param;
                                    

                               }
                            

                        /**
                        * field for TimeFormat
                        * This was an Attribute!
                        */

                        
                                    protected java.lang.String localTimeFormat ;
                                

                           /**
                           * Auto generated getter method
                           * @return java.lang.String
                           */
                           public  java.lang.String getTimeFormat(){
                               return localTimeFormat;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param TimeFormat
                               */
                               public void setTimeFormat(java.lang.String param){
                            
                                            this.localTimeFormat=param;
                                    

                               }
                            

     /**
     * isReaderMTOMAware
     * @return true if the reader supports MTOM
     */
   public static boolean isReaderMTOMAware(javax.xml.stream.XMLStreamReader reader) {
        boolean isReaderMTOMAware = false;
        
        try{
          isReaderMTOMAware = java.lang.Boolean.TRUE.equals(reader.getProperty(org.apache.axiom.om.OMConstants.IS_DATA_HANDLERS_AWARE));
        }catch(java.lang.IllegalArgumentException e){
          isReaderMTOMAware = false;
        }
        return isReaderMTOMAware;
   }
     
     
        /**
        *
        * @param parentQName
        * @param factory
        * @return org.apache.axiom.om.OMElement
        */
       public org.apache.axiom.om.OMElement getOMElement (
               final javax.xml.namespace.QName parentQName,
               final org.apache.axiom.om.OMFactory factory) throws org.apache.axis2.databinding.ADBException{


        
                org.apache.axiom.om.OMDataSource dataSource =
                       new org.apache.axis2.databinding.ADBDataSource(this,MY_QNAME){

                 public void serialize(org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
                       GetMultiData.this.serialize(MY_QNAME,factory,xmlWriter);
                 }
               };
               return new org.apache.axiom.om.impl.llom.OMSourcedElementImpl(
               MY_QNAME,factory,dataSource);
            
       }

         public void serialize(final javax.xml.namespace.QName parentQName,
                                       final org.apache.axiom.om.OMFactory factory,
                                       org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter)
                                throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException{
                           serialize(parentQName,factory,xmlWriter,false);
         }

         public void serialize(final javax.xml.namespace.QName parentQName,
                               final org.apache.axiom.om.OMFactory factory,
                               org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter,
                               boolean serializeType)
            throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException{
            
                


                java.lang.String prefix = null;
                java.lang.String namespace = null;
                

                    prefix = parentQName.getPrefix();
                    namespace = parentQName.getNamespaceURI();

                    if ((namespace != null) && (namespace.trim().length() > 0)) {
                        java.lang.String writerPrefix = xmlWriter.getPrefix(namespace);
                        if (writerPrefix != null) {
                            xmlWriter.writeStartElement(namespace, parentQName.getLocalPart());
                        } else {
                            if (prefix == null) {
                                prefix = generatePrefix(namespace);
                            }

                            xmlWriter.writeStartElement(prefix, parentQName.getLocalPart(), namespace);
                            xmlWriter.writeNamespace(prefix, namespace);
                            xmlWriter.setPrefix(prefix, namespace);
                        }
                    } else {
                        xmlWriter.writeStartElement(parentQName.getLocalPart());
                    }
                
                  if (serializeType){
               

                   java.lang.String namespacePrefix = registerPrefix(xmlWriter,"http://standard.webservice.gsn");
                   if ((namespacePrefix != null) && (namespacePrefix.trim().length() > 0)){
                       writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","type",
                           namespacePrefix+":getMultiData",
                           xmlWriter);
                   } else {
                       writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","type",
                           "getMultiData",
                           xmlWriter);
                   }

               
                   }
               
                                                   if (localFrom!=java.lang.Long.MIN_VALUE) {
                                               
                                                writeAttribute("http://standard.webservice.gsn",
                                                         "from",
                                                         org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localFrom), xmlWriter);

                                            
                                      }
                                    
                                                   if (localTo!=java.lang.Long.MIN_VALUE) {
                                               
                                                writeAttribute("http://standard.webservice.gsn",
                                                         "to",
                                                         org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localTo), xmlWriter);

                                            
                                      }
                                    
                                                   if (localNb!=java.lang.Integer.MIN_VALUE) {
                                               
                                                writeAttribute("http://standard.webservice.gsn",
                                                         "nb",
                                                         org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localNb), xmlWriter);

                                            
                                      }
                                    
                                            if (localTimeFormat != null){
                                        
                                                writeAttribute("http://standard.webservice.gsn",
                                                         "timeFormat",
                                                         org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localTimeFormat), xmlWriter);

                                            
                                      }
                                     if (localAcDetailsTracker){
                                            if (localAcDetails==null){
                                                 throw new org.apache.axis2.databinding.ADBException("acDetails cannot be null!!");
                                            }
                                           localAcDetails.serialize(new javax.xml.namespace.QName("http://standard.webservice.gsn","acDetails"),
                                               factory,xmlWriter);
                                        } if (localFieldSelectorTracker){
                                       if (localFieldSelector!=null){
                                            for (int i = 0;i < localFieldSelector.length;i++){
                                                if (localFieldSelector[i] != null){
                                                 localFieldSelector[i].serialize(new javax.xml.namespace.QName("http://standard.webservice.gsn","fieldSelector"),
                                                           factory,xmlWriter);
                                                } else {
                                                   
                                                        // we don't have to do any thing since minOccures is zero
                                                    
                                                }

                                            }
                                     } else {
                                        
                                               throw new org.apache.axis2.databinding.ADBException("fieldSelector cannot be null!!");
                                        
                                    }
                                 } if (localConditionsTracker){
                                       if (localConditions!=null){
                                            for (int i = 0;i < localConditions.length;i++){
                                                if (localConditions[i] != null){
                                                 localConditions[i].serialize(new javax.xml.namespace.QName("http://standard.webservice.gsn","conditions"),
                                                           factory,xmlWriter);
                                                } else {
                                                   
                                                        // we don't have to do any thing since minOccures is zero
                                                    
                                                }

                                            }
                                     } else {
                                        
                                               throw new org.apache.axis2.databinding.ADBException("conditions cannot be null!!");
                                        
                                    }
                                 } if (localAggregationTracker){
                                            if (localAggregation==null){
                                                 throw new org.apache.axis2.databinding.ADBException("aggregation cannot be null!!");
                                            }
                                           localAggregation.serialize(new javax.xml.namespace.QName("http://standard.webservice.gsn","aggregation"),
                                               factory,xmlWriter);
                                        }
                    xmlWriter.writeEndElement();
               

        }

         /**
          * Util method to write an attribute with the ns prefix
          */
          private void writeAttribute(java.lang.String prefix,java.lang.String namespace,java.lang.String attName,
                                      java.lang.String attValue,javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException{
              if (xmlWriter.getPrefix(namespace) == null) {
                       xmlWriter.writeNamespace(prefix, namespace);
                       xmlWriter.setPrefix(prefix, namespace);

              }

              xmlWriter.writeAttribute(namespace,attName,attValue);

         }

        /**
          * Util method to write an attribute without the ns prefix
          */
          private void writeAttribute(java.lang.String namespace,java.lang.String attName,
                                      java.lang.String attValue,javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException{
                if (namespace.equals(""))
              {
                  xmlWriter.writeAttribute(attName,attValue);
              }
              else
              {
                  registerPrefix(xmlWriter, namespace);
                  xmlWriter.writeAttribute(namespace,attName,attValue);
              }
          }


           /**
             * Util method to write an attribute without the ns prefix
             */
            private void writeQNameAttribute(java.lang.String namespace, java.lang.String attName,
                                             javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {

                java.lang.String attributeNamespace = qname.getNamespaceURI();
                java.lang.String attributePrefix = xmlWriter.getPrefix(attributeNamespace);
                if (attributePrefix == null) {
                    attributePrefix = registerPrefix(xmlWriter, attributeNamespace);
                }
                java.lang.String attributeValue;
                if (attributePrefix.trim().length() > 0) {
                    attributeValue = attributePrefix + ":" + qname.getLocalPart();
                } else {
                    attributeValue = qname.getLocalPart();
                }

                if (namespace.equals("")) {
                    xmlWriter.writeAttribute(attName, attributeValue);
                } else {
                    registerPrefix(xmlWriter, namespace);
                    xmlWriter.writeAttribute(namespace, attName, attributeValue);
                }
            }
        /**
         *  method to handle Qnames
         */

        private void writeQName(javax.xml.namespace.QName qname,
                                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            java.lang.String namespaceURI = qname.getNamespaceURI();
            if (namespaceURI != null) {
                java.lang.String prefix = xmlWriter.getPrefix(namespaceURI);
                if (prefix == null) {
                    prefix = generatePrefix(namespaceURI);
                    xmlWriter.writeNamespace(prefix, namespaceURI);
                    xmlWriter.setPrefix(prefix,namespaceURI);
                }

                if (prefix.trim().length() > 0){
                    xmlWriter.writeCharacters(prefix + ":" + org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
                } else {
                    // i.e this is the default namespace
                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
                }

            } else {
                xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
            }
        }

        private void writeQNames(javax.xml.namespace.QName[] qnames,
                                 javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {

            if (qnames != null) {
                // we have to store this data until last moment since it is not possible to write any
                // namespace data after writing the charactor data
                java.lang.StringBuffer stringToWrite = new java.lang.StringBuffer();
                java.lang.String namespaceURI = null;
                java.lang.String prefix = null;

                for (int i = 0; i < qnames.length; i++) {
                    if (i > 0) {
                        stringToWrite.append(" ");
                    }
                    namespaceURI = qnames[i].getNamespaceURI();
                    if (namespaceURI != null) {
                        prefix = xmlWriter.getPrefix(namespaceURI);
                        if ((prefix == null) || (prefix.length() == 0)) {
                            prefix = generatePrefix(namespaceURI);
                            xmlWriter.writeNamespace(prefix, namespaceURI);
                            xmlWriter.setPrefix(prefix,namespaceURI);
                        }

                        if (prefix.trim().length() > 0){
                            stringToWrite.append(prefix).append(":").append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                        } else {
                            stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                        }
                    } else {
                        stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                    }
                }
                xmlWriter.writeCharacters(stringToWrite.toString());
            }

        }


         /**
         * Register a namespace prefix
         */
         private java.lang.String registerPrefix(javax.xml.stream.XMLStreamWriter xmlWriter, java.lang.String namespace) throws javax.xml.stream.XMLStreamException {
                java.lang.String prefix = xmlWriter.getPrefix(namespace);

                if (prefix == null) {
                    prefix = generatePrefix(namespace);

                    while (xmlWriter.getNamespaceContext().getNamespaceURI(prefix) != null) {
                        prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
                    }

                    xmlWriter.writeNamespace(prefix, namespace);
                    xmlWriter.setPrefix(prefix, namespace);
                }

                return prefix;
            }


  
        /**
        * databinding method to get an XML representation of this object
        *
        */
        public javax.xml.stream.XMLStreamReader getPullParser(javax.xml.namespace.QName qName)
                    throws org.apache.axis2.databinding.ADBException{


        
                 java.util.ArrayList elementList = new java.util.ArrayList();
                 java.util.ArrayList attribList = new java.util.ArrayList();

                 if (localAcDetailsTracker){
                            elementList.add(new javax.xml.namespace.QName("http://standard.webservice.gsn",
                                                                      "acDetails"));
                            
                            
                                    if (localAcDetails==null){
                                         throw new org.apache.axis2.databinding.ADBException("acDetails cannot be null!!");
                                    }
                                    elementList.add(localAcDetails);
                                } if (localFieldSelectorTracker){
                             if (localFieldSelector!=null) {
                                 for (int i = 0;i < localFieldSelector.length;i++){

                                    if (localFieldSelector[i] != null){
                                         elementList.add(new javax.xml.namespace.QName("http://standard.webservice.gsn",
                                                                          "fieldSelector"));
                                         elementList.add(localFieldSelector[i]);
                                    } else {
                                        
                                                // nothing to do
                                            
                                    }

                                 }
                             } else {
                                 
                                        throw new org.apache.axis2.databinding.ADBException("fieldSelector cannot be null!!");
                                    
                             }

                        } if (localConditionsTracker){
                             if (localConditions!=null) {
                                 for (int i = 0;i < localConditions.length;i++){

                                    if (localConditions[i] != null){
                                         elementList.add(new javax.xml.namespace.QName("http://standard.webservice.gsn",
                                                                          "conditions"));
                                         elementList.add(localConditions[i]);
                                    } else {
                                        
                                                // nothing to do
                                            
                                    }

                                 }
                             } else {
                                 
                                        throw new org.apache.axis2.databinding.ADBException("conditions cannot be null!!");
                                    
                             }

                        } if (localAggregationTracker){
                            elementList.add(new javax.xml.namespace.QName("http://standard.webservice.gsn",
                                                                      "aggregation"));
                            
                            
                                    if (localAggregation==null){
                                         throw new org.apache.axis2.databinding.ADBException("aggregation cannot be null!!");
                                    }
                                    elementList.add(localAggregation);
                                }
                            attribList.add(
                            new javax.xml.namespace.QName("http://standard.webservice.gsn","from"));
                            
                                      attribList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localFrom));
                                
                            attribList.add(
                            new javax.xml.namespace.QName("http://standard.webservice.gsn","to"));
                            
                                      attribList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localTo));
                                
                            attribList.add(
                            new javax.xml.namespace.QName("http://standard.webservice.gsn","nb"));
                            
                                      attribList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localNb));
                                
                            attribList.add(
                            new javax.xml.namespace.QName("http://standard.webservice.gsn","timeFormat"));
                            
                                      attribList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localTimeFormat));
                                

                return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(qName, elementList.toArray(), attribList.toArray());
            
            

        }

  

     /**
      *  Factory class that keeps the parse method
      */
    public static class Factory{

        
        

        /**
        * static method to create the object
        * Precondition:  If this object is an element, the current or next start element starts this object and any intervening reader events are ignorable
        *                If this object is not an element, it is a complex type and the reader is at the event just after the outer start element
        * Postcondition: If this object is an element, the reader is positioned at its end element
        *                If this object is a complex type, the reader is positioned at the end element of its outer element
        */
        public static GetMultiData parse(javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception{
            GetMultiData object =
                new GetMultiData();

            int event;
            java.lang.String nillableValue = null;
            java.lang.String prefix ="";
            java.lang.String namespaceuri ="";
            try {
                
                while (!reader.isStartElement() && !reader.isEndElement())
                    reader.next();

                
                if (reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","type")!=null){
                  java.lang.String fullTypeName = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance",
                        "type");
                  if (fullTypeName!=null){
                    java.lang.String nsPrefix = null;
                    if (fullTypeName.indexOf(":") > -1){
                        nsPrefix = fullTypeName.substring(0,fullTypeName.indexOf(":"));
                    }
                    nsPrefix = nsPrefix==null?"":nsPrefix;

                    java.lang.String type = fullTypeName.substring(fullTypeName.indexOf(":")+1);
                    
                            if (!"getMultiData".equals(type)){
                                //find namespace for the prefix
                                java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
                                return (GetMultiData)gsn.webservice.standard.xsd.ExtensionMapper.getTypeObject(
                                     nsUri,type,reader);
                              }
                        

                  }
                

                }

                

                
                // Note all attributes that were handled. Used to differ normal attributes
                // from anyAttributes.
                java.util.Vector handledAttributes = new java.util.Vector();
                

                 
                    // handle attribute "from"
                    java.lang.String tempAttribFrom =
                        
                                reader.getAttributeValue("http://standard.webservice.gsn","from");
                            
                   if (tempAttribFrom!=null){
                         java.lang.String content = tempAttribFrom;
                        
                                                 object.setFrom(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToLong(tempAttribFrom));
                                            
                    } else {
                       
                                           object.setFrom(java.lang.Long.MIN_VALUE);
                                       
                    }
                    handledAttributes.add("from");
                    
                    // handle attribute "to"
                    java.lang.String tempAttribTo =
                        
                                reader.getAttributeValue("http://standard.webservice.gsn","to");
                            
                   if (tempAttribTo!=null){
                         java.lang.String content = tempAttribTo;
                        
                                                 object.setTo(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToLong(tempAttribTo));
                                            
                    } else {
                       
                                           object.setTo(java.lang.Long.MIN_VALUE);
                                       
                    }
                    handledAttributes.add("to");
                    
                    // handle attribute "nb"
                    java.lang.String tempAttribNb =
                        
                                reader.getAttributeValue("http://standard.webservice.gsn","nb");
                            
                   if (tempAttribNb!=null){
                         java.lang.String content = tempAttribNb;
                        
                                                 object.setNb(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToInt(tempAttribNb));
                                            
                    } else {
                       
                                           object.setNb(java.lang.Integer.MIN_VALUE);
                                       
                    }
                    handledAttributes.add("nb");
                    
                    // handle attribute "timeFormat"
                    java.lang.String tempAttribTimeFormat =
                        
                                reader.getAttributeValue("http://standard.webservice.gsn","timeFormat");
                            
                   if (tempAttribTimeFormat!=null){
                         java.lang.String content = tempAttribTimeFormat;
                        
                                                 object.setTimeFormat(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(tempAttribTimeFormat));
                                            
                    } else {
                       
                    }
                    handledAttributes.add("timeFormat");
                    
                    
                    reader.next();
                
                        java.util.ArrayList list2 = new java.util.ArrayList();
                    
                        java.util.ArrayList list3 = new java.util.ArrayList();
                    
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://standard.webservice.gsn","acDetails").equals(reader.getName())){
                                
                                                object.setAcDetails(gsn.webservice.standard.xsd.GSNWebService_ACDetails.Factory.parse(reader));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://standard.webservice.gsn","fieldSelector").equals(reader.getName())){
                                
                                    
                                    
                                    // Process the array and step past its final element's end.
                                    list2.add(gsn.webservice.standard.xsd.GSNWebService_FieldSelector.Factory.parse(reader));
                                                                
                                                        //loop until we find a start element that is not part of this array
                                                        boolean loopDone2 = false;
                                                        while(!loopDone2){
                                                            // We should be at the end element, but make sure
                                                            while (!reader.isEndElement())
                                                                reader.next();
                                                            // Step out of this element
                                                            reader.next();
                                                            // Step to next element event.
                                                            while (!reader.isStartElement() && !reader.isEndElement())
                                                                reader.next();
                                                            if (reader.isEndElement()){
                                                                //two continuous end elements means we are exiting the xml structure
                                                                loopDone2 = true;
                                                            } else {
                                                                if (new javax.xml.namespace.QName("http://standard.webservice.gsn","fieldSelector").equals(reader.getName())){
                                                                    list2.add(gsn.webservice.standard.xsd.GSNWebService_FieldSelector.Factory.parse(reader));
                                                                        
                                                                }else{
                                                                    loopDone2 = true;
                                                                }
                                                            }
                                                        }
                                                        // call the converter utility  to convert and set the array
                                                        
                                                        object.setFieldSelector((gsn.webservice.standard.xsd.GSNWebService_FieldSelector[])
                                                            org.apache.axis2.databinding.utils.ConverterUtil.convertToArray(
                                                                gsn.webservice.standard.xsd.GSNWebService_FieldSelector.class,
                                                                list2));
                                                            
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://standard.webservice.gsn","conditions").equals(reader.getName())){
                                
                                    
                                    
                                    // Process the array and step past its final element's end.
                                    list3.add(gsn.http.datarequest.xsd.StandardCriterion.Factory.parse(reader));
                                                                
                                                        //loop until we find a start element that is not part of this array
                                                        boolean loopDone3 = false;
                                                        while(!loopDone3){
                                                            // We should be at the end element, but make sure
                                                            while (!reader.isEndElement())
                                                                reader.next();
                                                            // Step out of this element
                                                            reader.next();
                                                            // Step to next element event.
                                                            while (!reader.isStartElement() && !reader.isEndElement())
                                                                reader.next();
                                                            if (reader.isEndElement()){
                                                                //two continuous end elements means we are exiting the xml structure
                                                                loopDone3 = true;
                                                            } else {
                                                                if (new javax.xml.namespace.QName("http://standard.webservice.gsn","conditions").equals(reader.getName())){
                                                                    list3.add(gsn.http.datarequest.xsd.StandardCriterion.Factory.parse(reader));
                                                                        
                                                                }else{
                                                                    loopDone3 = true;
                                                                }
                                                            }
                                                        }
                                                        // call the converter utility  to convert and set the array
                                                        
                                                        object.setConditions((gsn.http.datarequest.xsd.StandardCriterion[])
                                                            org.apache.axis2.databinding.utils.ConverterUtil.convertToArray(
                                                                gsn.http.datarequest.xsd.StandardCriterion.class,
                                                                list3));
                                                            
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://standard.webservice.gsn","aggregation").equals(reader.getName())){
                                
                                                object.setAggregation(gsn.http.datarequest.xsd.AggregationCriterion.Factory.parse(reader));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                  
                            while (!reader.isStartElement() && !reader.isEndElement())
                                reader.next();
                            
                                if (reader.isStartElement())
                                // A start element we are not expecting indicates a trailing invalid property
                                throw new org.apache.axis2.databinding.ADBException("Unexpected subelement " + reader.getLocalName());
                            



            } catch (javax.xml.stream.XMLStreamException e) {
                throw new java.lang.Exception(e);
            }

            return object;
        }

        }//end of factory class

        

        }
           
          