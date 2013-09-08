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
 * StandardCriterion.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.4.1  Built on : Aug 13, 2008 (05:03:41 LKT)
 */
            
                package gsn.http.datarequest.xsd;
            

            /**
            *  StandardCriterion bean class
            */
        
        public  class StandardCriterion extends gsn.http.datarequest.xsd.AbstractCriterion
        implements org.apache.axis2.databinding.ADBBean{
        /* This type was generated from the piece of schema that had
                name = StandardCriterion
                Namespace URI = http://datarequest.http.gsn/xsd
                Namespace Prefix = ns2
                */
            

        private static java.lang.String generatePrefix(java.lang.String namespace) {
            if(namespace.equals("http://datarequest.http.gsn/xsd")){
                return "ns2";
            }
            return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
        }

        

                        /**
                        * field for CritJoin
                        * This was an Attribute!
                        */

                        
                                    protected java.lang.String localCritJoin ;
                                

                           /**
                           * Auto generated getter method
                           * @return java.lang.String
                           */
                           public  java.lang.String getCritJoin(){
                               return localCritJoin;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param CritJoin
                               */
                               public void setCritJoin(java.lang.String param){
                            
                                            this.localCritJoin=param;
                                    

                               }
                            

                        /**
                        * field for Field
                        * This was an Attribute!
                        */

                        
                                    protected java.lang.String localField ;
                                

                           /**
                           * Auto generated getter method
                           * @return java.lang.String
                           */
                           public  java.lang.String getField(){
                               return localField;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param Field
                               */
                               public void setField(java.lang.String param){
                            
                                            this.localField=param;
                                    

                               }
                            

                        /**
                        * field for Negation
                        * This was an Attribute!
                        */

                        
                                    protected java.lang.String localNegation ;
                                

                           /**
                           * Auto generated getter method
                           * @return java.lang.String
                           */
                           public  java.lang.String getNegation(){
                               return localNegation;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param Negation
                               */
                               public void setNegation(java.lang.String param){
                            
                                            this.localNegation=param;
                                    

                               }
                            

                        /**
                        * field for Operator
                        * This was an Attribute!
                        */

                        
                                    protected java.lang.String localOperator ;
                                

                           /**
                           * Auto generated getter method
                           * @return java.lang.String
                           */
                           public  java.lang.String getOperator(){
                               return localOperator;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param Operator
                               */
                               public void setOperator(java.lang.String param){
                            
                                            this.localOperator=param;
                                    

                               }
                            

                        /**
                        * field for Value
                        * This was an Attribute!
                        */

                        
                                    protected java.lang.String localValue ;
                                

                           /**
                           * Auto generated getter method
                           * @return java.lang.String
                           */
                           public  java.lang.String getValue(){
                               return localValue;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param Value
                               */
                               public void setValue(java.lang.String param){
                            
                                            this.localValue=param;
                                    

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
                       StandardCriterion.this.serialize(parentQName,factory,xmlWriter);
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
                

                   java.lang.String namespacePrefix = registerPrefix(xmlWriter,"http://datarequest.http.gsn/xsd");
                   if ((namespacePrefix != null) && (namespacePrefix.trim().length() > 0)){
                       writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","type",
                           namespacePrefix+":StandardCriterion",
                           xmlWriter);
                   } else {
                       writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","type",
                           "StandardCriterion",
                           xmlWriter);
                   }

               
                                            if (localCritJoin != null){
                                        
                                                writeAttribute("http://datarequest.http.gsn/xsd",
                                                         "critJoin",
                                                         org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localCritJoin), xmlWriter);

                                            
                                      }
                                    
                                      else {
                                          throw new org.apache.axis2.databinding.ADBException("required attribute localCritJoin is null");
                                      }
                                    
                                            if (localField != null){
                                        
                                                writeAttribute("http://datarequest.http.gsn/xsd",
                                                         "field",
                                                         org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localField), xmlWriter);

                                            
                                      }
                                    
                                      else {
                                          throw new org.apache.axis2.databinding.ADBException("required attribute localField is null");
                                      }
                                    
                                            if (localNegation != null){
                                        
                                                writeAttribute("http://datarequest.http.gsn/xsd",
                                                         "negation",
                                                         org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localNegation), xmlWriter);

                                            
                                      }
                                    
                                      else {
                                          throw new org.apache.axis2.databinding.ADBException("required attribute localNegation is null");
                                      }
                                    
                                            if (localOperator != null){
                                        
                                                writeAttribute("http://datarequest.http.gsn/xsd",
                                                         "operator",
                                                         org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localOperator), xmlWriter);

                                            
                                      }
                                    
                                      else {
                                          throw new org.apache.axis2.databinding.ADBException("required attribute localOperator is null");
                                      }
                                    
                                            if (localValue != null){
                                        
                                                writeAttribute("http://datarequest.http.gsn/xsd",
                                                         "value",
                                                         org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localValue), xmlWriter);

                                            
                                      }
                                    
                                      else {
                                          throw new org.apache.axis2.databinding.ADBException("required attribute localValue is null");
                                      }
                                    
                                            if (localVsname != null){
                                        
                                                writeAttribute("http://datarequest.http.gsn/xsd",
                                                         "vsname",
                                                         org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localVsname), xmlWriter);

                                            
                                      }
                                    
                                      else {
                                          throw new org.apache.axis2.databinding.ADBException("required attribute localVsname is null");
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

                
                    attribList.add(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema-instance","type"));
                    attribList.add(new javax.xml.namespace.QName("http://datarequest.http.gsn/xsd","StandardCriterion"));
                
                            attribList.add(
                            new javax.xml.namespace.QName("http://datarequest.http.gsn/xsd","critJoin"));
                            
                                      attribList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localCritJoin));
                                
                            attribList.add(
                            new javax.xml.namespace.QName("http://datarequest.http.gsn/xsd","field"));
                            
                                      attribList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localField));
                                
                            attribList.add(
                            new javax.xml.namespace.QName("http://datarequest.http.gsn/xsd","negation"));
                            
                                      attribList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localNegation));
                                
                            attribList.add(
                            new javax.xml.namespace.QName("http://datarequest.http.gsn/xsd","operator"));
                            
                                      attribList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localOperator));
                                
                            attribList.add(
                            new javax.xml.namespace.QName("http://datarequest.http.gsn/xsd","value"));
                            
                                      attribList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localValue));
                                
                            attribList.add(
                            new javax.xml.namespace.QName("http://datarequest.http.gsn/xsd","vsname"));
                            
                                      attribList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localVsname));
                                

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
        public static StandardCriterion parse(javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception{
            StandardCriterion object =
                new StandardCriterion();

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
                    
                            if (!"StandardCriterion".equals(type)){
                                //find namespace for the prefix
                                java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
                                return (StandardCriterion)gsn.webservice.standard.xsd.ExtensionMapper.getTypeObject(
                                     nsUri,type,reader);
                              }
                        

                  }
                

                }

                

                
                // Note all attributes that were handled. Used to differ normal attributes
                // from anyAttributes.
                java.util.Vector handledAttributes = new java.util.Vector();
                

                 
                    // handle attribute "critJoin"
                    java.lang.String tempAttribCritJoin =
                        
                                reader.getAttributeValue("http://datarequest.http.gsn/xsd","critJoin");
                            
                   if (tempAttribCritJoin!=null){
                         java.lang.String content = tempAttribCritJoin;
                        
                                                 object.setCritJoin(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(tempAttribCritJoin));
                                            
                    } else {
                       
                               throw new org.apache.axis2.databinding.ADBException("Required attribute critJoin is missing");
                           
                    }
                    handledAttributes.add("critJoin");
                    
                    // handle attribute "field"
                    java.lang.String tempAttribField =
                        
                                reader.getAttributeValue("http://datarequest.http.gsn/xsd","field");
                            
                   if (tempAttribField!=null){
                         java.lang.String content = tempAttribField;
                        
                                                 object.setField(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(tempAttribField));
                                            
                    } else {
                       
                               throw new org.apache.axis2.databinding.ADBException("Required attribute field is missing");
                           
                    }
                    handledAttributes.add("field");
                    
                    // handle attribute "negation"
                    java.lang.String tempAttribNegation =
                        
                                reader.getAttributeValue("http://datarequest.http.gsn/xsd","negation");
                            
                   if (tempAttribNegation!=null){
                         java.lang.String content = tempAttribNegation;
                        
                                                 object.setNegation(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(tempAttribNegation));
                                            
                    } else {
                       
                               throw new org.apache.axis2.databinding.ADBException("Required attribute negation is missing");
                           
                    }
                    handledAttributes.add("negation");
                    
                    // handle attribute "operator"
                    java.lang.String tempAttribOperator =
                        
                                reader.getAttributeValue("http://datarequest.http.gsn/xsd","operator");
                            
                   if (tempAttribOperator!=null){
                         java.lang.String content = tempAttribOperator;
                        
                                                 object.setOperator(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(tempAttribOperator));
                                            
                    } else {
                       
                               throw new org.apache.axis2.databinding.ADBException("Required attribute operator is missing");
                           
                    }
                    handledAttributes.add("operator");
                    
                    // handle attribute "value"
                    java.lang.String tempAttribValue =
                        
                                reader.getAttributeValue("http://datarequest.http.gsn/xsd","value");
                            
                   if (tempAttribValue!=null){
                         java.lang.String content = tempAttribValue;
                        
                                                 object.setValue(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(tempAttribValue));
                                            
                    } else {
                       
                               throw new org.apache.axis2.databinding.ADBException("Required attribute value is missing");
                           
                    }
                    handledAttributes.add("value");
                    
                    // handle attribute "vsname"
                    java.lang.String tempAttribVsname =
                        
                                reader.getAttributeValue("http://datarequest.http.gsn/xsd","vsname");
                            
                   if (tempAttribVsname!=null){
                         java.lang.String content = tempAttribVsname;
                        
                                                 object.setVsname(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(tempAttribVsname));
                                            
                    } else {
                       
                               throw new org.apache.axis2.databinding.ADBException("Required attribute vsname is missing");
                           
                    }
                    handledAttributes.add("vsname");
                    
                    
                    reader.next();
                



            } catch (javax.xml.stream.XMLStreamException e) {
                throw new java.lang.Exception(e);
            }

            return object;
        }

        }//end of factory class

        

        }
           
          