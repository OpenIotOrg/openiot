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
 * GSNWebService_QueryResult.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.4.1  Built on : Aug 13, 2008 (05:03:41 LKT)
 */
            
                package gsn.webservice.standard.xsd;
            

            /**
            *  GSNWebService_QueryResult bean class
            */
        
        public  class GSNWebService_QueryResult
        implements org.apache.axis2.databinding.ADBBean{
        /* This type was generated from the piece of schema that had
                name = GSNWebService_QueryResult
                Namespace URI = http://standard.webservice.gsn/xsd
                Namespace Prefix = ns1
                */
            

        private static java.lang.String generatePrefix(java.lang.String namespace) {
            if(namespace.equals("http://standard.webservice.gsn/xsd")){
                return "ns1";
            }
            return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
        }

        

                        /**
                        * field for Format
                        */

                        
                                    protected gsn.webservice.standard.xsd.GSNWebService_StreamElement localFormat ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localFormatTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return gsn.webservice.standard.xsd.GSNWebService_StreamElement
                           */
                           public  gsn.webservice.standard.xsd.GSNWebService_StreamElement getFormat(){
                               return localFormat;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param Format
                               */
                               public void setFormat(gsn.webservice.standard.xsd.GSNWebService_StreamElement param){
                            
                                       if (param != null){
                                          //update the setting tracker
                                          localFormatTracker = true;
                                       } else {
                                          localFormatTracker = false;
                                              
                                       }
                                   
                                            this.localFormat=param;
                                    

                               }
                            

                        /**
                        * field for StreamElements
                        * This was an Array!
                        */

                        
                                    protected gsn.webservice.standard.xsd.GSNWebService_StreamElement[] localStreamElements ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localStreamElementsTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return gsn.webservice.standard.xsd.GSNWebService_StreamElement[]
                           */
                           public  gsn.webservice.standard.xsd.GSNWebService_StreamElement[] getStreamElements(){
                               return localStreamElements;
                           }

                           
                        


                               
                              /**
                               * validate the array for StreamElements
                               */
                              protected void validateStreamElements(gsn.webservice.standard.xsd.GSNWebService_StreamElement[] param){
                             
                              }


                             /**
                              * Auto generated setter method
                              * @param param StreamElements
                              */
                              public void setStreamElements(gsn.webservice.standard.xsd.GSNWebService_StreamElement[] param){
                              
                                   validateStreamElements(param);

                               
                                          if (param != null){
                                             //update the setting tracker
                                             localStreamElementsTracker = true;
                                          } else {
                                             localStreamElementsTracker = false;
                                                 
                                          }
                                      
                                      this.localStreamElements=param;
                              }

                               
                             
                             /**
                             * Auto generated add method for the array for convenience
                             * @param param gsn.webservice.standard.xsd.GSNWebService_StreamElement
                             */
                             public void addStreamElements(gsn.webservice.standard.xsd.GSNWebService_StreamElement param){
                                   if (localStreamElements == null){
                                   localStreamElements = new gsn.webservice.standard.xsd.GSNWebService_StreamElement[]{};
                                   }

                            
                                 //update the setting tracker
                                localStreamElementsTracker = true;
                            

                               java.util.List list =
                            org.apache.axis2.databinding.utils.ConverterUtil.toList(localStreamElements);
                               list.add(param);
                               this.localStreamElements =
                             (gsn.webservice.standard.xsd.GSNWebService_StreamElement[])list.toArray(
                            new gsn.webservice.standard.xsd.GSNWebService_StreamElement[list.size()]);

                             }
                             

                        /**
                        * field for ExecutedQuery
                        */

                        
                                    protected java.lang.String localExecutedQuery ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localExecutedQueryTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.String
                           */
                           public  java.lang.String getExecutedQuery(){
                               return localExecutedQuery;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param ExecutedQuery
                               */
                               public void setExecutedQuery(java.lang.String param){
                            
                                       if (param != null){
                                          //update the setting tracker
                                          localExecutedQueryTracker = true;
                                       } else {
                                          localExecutedQueryTracker = false;
                                              
                                       }
                                   
                                            this.localExecutedQuery=param;
                                    

                               }
                            

                        /**
                        * field for HasNext
                        * This was an Attribute!
                        */

                        
                                    protected boolean localHasNext ;
                                

                           /**
                           * Auto generated getter method
                           * @return boolean
                           */
                           public  boolean getHasNext(){
                               return localHasNext;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param HasNext
                               */
                               public void setHasNext(boolean param){
                            
                                            this.localHasNext=param;
                                    

                               }
                            

                        /**
                        * field for Vsname
                        * This was an Attribute!
                        */

                        
                                    protected java.lang.String localVsname ;
                                

                           /**
                           * Auto generated getter method
                           * @return java.lang.String
                           */
                           public  java.lang.String getVsname(){
                               return localVsname;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param Vsname
                               */
                               public void setVsname(java.lang.String param){
                            
                                            this.localVsname=param;
                                    

                               }
                            

                        /**
                        * field for Sid
                        * This was an Attribute!
                        */

                        
                                    protected java.lang.String localSid ;
                                

                           /**
                           * Auto generated getter method
                           * @return java.lang.String
                           */
                           public  java.lang.String getSid(){
                               return localSid;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param Sid
                               */
                               public void setSid(java.lang.String param){
                            
                                            this.localSid=param;
                                    

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
                       new org.apache.axis2.databinding.ADBDataSource(this,parentQName){

                 public void serialize(org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
                       GSNWebService_QueryResult.this.serialize(parentQName,factory,xmlWriter);
                 }
               };
               return new org.apache.axiom.om.impl.llom.OMSourcedElementImpl(
               parentQName,factory,dataSource);
            
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
               

                   java.lang.String namespacePrefix = registerPrefix(xmlWriter,"http://standard.webservice.gsn/xsd");
                   if ((namespacePrefix != null) && (namespacePrefix.trim().length() > 0)){
                       writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","type",
                           namespacePrefix+":GSNWebService_QueryResult",
                           xmlWriter);
                   } else {
                       writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","type",
                           "GSNWebService_QueryResult",
                           xmlWriter);
                   }

               
                   }
               
                                                   if (true) {
                                               
                                                writeAttribute("http://standard.webservice.gsn/xsd",
                                                         "hasNext",
                                                         org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localHasNext), xmlWriter);

                                            
                                      }
                                    
                                      else {
                                          throw new org.apache.axis2.databinding.ADBException("required attribute localHasNext is null");
                                      }
                                    
                                            if (localVsname != null){
                                        
                                                writeAttribute("http://standard.webservice.gsn/xsd",
                                                         "vsname",
                                                         org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localVsname), xmlWriter);

                                            
                                      }
                                    
                                      else {
                                          throw new org.apache.axis2.databinding.ADBException("required attribute localVsname is null");
                                      }
                                    
                                            if (localSid != null){
                                        
                                                writeAttribute("http://standard.webservice.gsn/xsd",
                                                         "sid",
                                                         org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localSid), xmlWriter);

                                            
                                      }
                                    
                                      else {
                                          throw new org.apache.axis2.databinding.ADBException("required attribute localSid is null");
                                      }
                                     if (localFormatTracker){
                                            if (localFormat==null){
                                                 throw new org.apache.axis2.databinding.ADBException("format cannot be null!!");
                                            }
                                           localFormat.serialize(new javax.xml.namespace.QName("http://standard.webservice.gsn/xsd","format"),
                                               factory,xmlWriter);
                                        } if (localStreamElementsTracker){
                                       if (localStreamElements!=null){
                                            for (int i = 0;i < localStreamElements.length;i++){
                                                if (localStreamElements[i] != null){
                                                 localStreamElements[i].serialize(new javax.xml.namespace.QName("http://standard.webservice.gsn/xsd","streamElements"),
                                                           factory,xmlWriter);
                                                } else {
                                                   
                                                        // we don't have to do any thing since minOccures is zero
                                                    
                                                }

                                            }
                                     } else {
                                        
                                               throw new org.apache.axis2.databinding.ADBException("streamElements cannot be null!!");
                                        
                                    }
                                 } if (localExecutedQueryTracker){
                                    namespace = "http://standard.webservice.gsn/xsd";
                                    if (! namespace.equals("")) {
                                        prefix = xmlWriter.getPrefix(namespace);

                                        if (prefix == null) {
                                            prefix = generatePrefix(namespace);

                                            xmlWriter.writeStartElement(prefix,"executedQuery", namespace);
                                            xmlWriter.writeNamespace(prefix, namespace);
                                            xmlWriter.setPrefix(prefix, namespace);

                                        } else {
                                            xmlWriter.writeStartElement(namespace,"executedQuery");
                                        }

                                    } else {
                                        xmlWriter.writeStartElement("executedQuery");
                                    }
                                

                                          if (localExecutedQuery==null){
                                              // write the nil attribute
                                              
                                                     throw new org.apache.axis2.databinding.ADBException("executedQuery cannot be null!!");
                                                  
                                          }else{

                                        
                                                   xmlWriter.writeCharacters(localExecutedQuery);
                                            
                                          }
                                    
                                   xmlWriter.writeEndElement();
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

                 if (localFormatTracker){
                            elementList.add(new javax.xml.namespace.QName("http://standard.webservice.gsn/xsd",
                                                                      "format"));
                            
                            
                                    if (localFormat==null){
                                         throw new org.apache.axis2.databinding.ADBException("format cannot be null!!");
                                    }
                                    elementList.add(localFormat);
                                } if (localStreamElementsTracker){
                             if (localStreamElements!=null) {
                                 for (int i = 0;i < localStreamElements.length;i++){

                                    if (localStreamElements[i] != null){
                                         elementList.add(new javax.xml.namespace.QName("http://standard.webservice.gsn/xsd",
                                                                          "streamElements"));
                                         elementList.add(localStreamElements[i]);
                                    } else {
                                        
                                                // nothing to do
                                            
                                    }

                                 }
                             } else {
                                 
                                        throw new org.apache.axis2.databinding.ADBException("streamElements cannot be null!!");
                                    
                             }

                        } if (localExecutedQueryTracker){
                                      elementList.add(new javax.xml.namespace.QName("http://standard.webservice.gsn/xsd",
                                                                      "executedQuery"));
                                 
                                        if (localExecutedQuery != null){
                                            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localExecutedQuery));
                                        } else {
                                           throw new org.apache.axis2.databinding.ADBException("executedQuery cannot be null!!");
                                        }
                                    }
                            attribList.add(
                            new javax.xml.namespace.QName("http://standard.webservice.gsn/xsd","hasNext"));
                            
                                      attribList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localHasNext));
                                
                            attribList.add(
                            new javax.xml.namespace.QName("http://standard.webservice.gsn/xsd","vsname"));
                            
                                      attribList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localVsname));
                                
                            attribList.add(
                            new javax.xml.namespace.QName("http://standard.webservice.gsn/xsd","sid"));
                            
                                      attribList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localSid));
                                

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
        public static GSNWebService_QueryResult parse(javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception{
            GSNWebService_QueryResult object =
                new GSNWebService_QueryResult();

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
                    
                            if (!"GSNWebService_QueryResult".equals(type)){
                                //find namespace for the prefix
                                java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
                                return (GSNWebService_QueryResult)gsn.webservice.standard.xsd.ExtensionMapper.getTypeObject(
                                     nsUri,type,reader);
                              }
                        

                  }
                

                }

                

                
                // Note all attributes that were handled. Used to differ normal attributes
                // from anyAttributes.
                java.util.Vector handledAttributes = new java.util.Vector();
                

                 
                    // handle attribute "hasNext"
                    java.lang.String tempAttribHasNext =
                        
                                reader.getAttributeValue("http://standard.webservice.gsn/xsd","hasNext");
                            
                   if (tempAttribHasNext!=null){
                         java.lang.String content = tempAttribHasNext;
                        
                                                 object.setHasNext(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToBoolean(tempAttribHasNext));
                                            
                    } else {
                       
                               throw new org.apache.axis2.databinding.ADBException("Required attribute hasNext is missing");
                           
                    }
                    handledAttributes.add("hasNext");
                    
                    // handle attribute "vsname"
                    java.lang.String tempAttribVsname =
                        
                                reader.getAttributeValue("http://standard.webservice.gsn/xsd","vsname");
                            
                   if (tempAttribVsname!=null){
                         java.lang.String content = tempAttribVsname;
                        
                                                 object.setVsname(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(tempAttribVsname));
                                            
                    } else {
                       
                               throw new org.apache.axis2.databinding.ADBException("Required attribute vsname is missing");
                           
                    }
                    handledAttributes.add("vsname");
                    
                    // handle attribute "sid"
                    java.lang.String tempAttribSid =
                        
                                reader.getAttributeValue("http://standard.webservice.gsn/xsd","sid");
                            
                   if (tempAttribSid!=null){
                         java.lang.String content = tempAttribSid;
                        
                                                 object.setSid(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(tempAttribSid));
                                            
                    } else {
                       
                               throw new org.apache.axis2.databinding.ADBException("Required attribute sid is missing");
                           
                    }
                    handledAttributes.add("sid");
                    
                    
                    reader.next();
                
                        java.util.ArrayList list2 = new java.util.ArrayList();
                    
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://standard.webservice.gsn/xsd","format").equals(reader.getName())){
                                
                                                object.setFormat(gsn.webservice.standard.xsd.GSNWebService_StreamElement.Factory.parse(reader));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://standard.webservice.gsn/xsd","streamElements").equals(reader.getName())){
                                
                                    
                                    
                                    // Process the array and step past its final element's end.
                                    list2.add(gsn.webservice.standard.xsd.GSNWebService_StreamElement.Factory.parse(reader));
                                                                
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
                                                                if (new javax.xml.namespace.QName("http://standard.webservice.gsn/xsd","streamElements").equals(reader.getName())){
                                                                    list2.add(gsn.webservice.standard.xsd.GSNWebService_StreamElement.Factory.parse(reader));
                                                                        
                                                                }else{
                                                                    loopDone2 = true;
                                                                }
                                                            }
                                                        }
                                                        // call the converter utility  to convert and set the array
                                                        
                                                        object.setStreamElements((gsn.webservice.standard.xsd.GSNWebService_StreamElement[])
                                                            org.apache.axis2.databinding.utils.ConverterUtil.convertToArray(
                                                                gsn.webservice.standard.xsd.GSNWebService_StreamElement.class,
                                                                list2));
                                                            
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://standard.webservice.gsn/xsd","executedQuery").equals(reader.getName())){
                                
                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setExecutedQuery(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                                              
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
           
          