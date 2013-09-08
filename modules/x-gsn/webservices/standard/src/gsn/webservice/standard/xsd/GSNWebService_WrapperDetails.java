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
 * GSNWebService_WrapperDetails.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.4.1  Built on : Aug 13, 2008 (05:03:41 LKT)
 */
            
                package gsn.webservice.standard.xsd;
            

            /**
            *  GSNWebService_WrapperDetails bean class
            */
        
        public  class GSNWebService_WrapperDetails
        implements org.apache.axis2.databinding.ADBBean{
        /* This type was generated from the piece of schema that had
                name = GSNWebService_WrapperDetails
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
                        * field for WrapperURLs
                        */

                        
                                    protected gsn.webservice.standard.xsd.GSNWebService_WrapperURL localWrapperURLs ;
                                

                           /**
                           * Auto generated getter method
                           * @return gsn.webservice.standard.xsd.GSNWebService_WrapperURL
                           */
                           public  gsn.webservice.standard.xsd.GSNWebService_WrapperURL getWrapperURLs(){
                               return localWrapperURLs;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param WrapperURLs
                               */
                               public void setWrapperURLs(gsn.webservice.standard.xsd.GSNWebService_WrapperURL param){
                            
                                            this.localWrapperURLs=param;
                                    

                               }
                            

                        /**
                        * field for OutputFormat
                        * This was an Array!
                        */

                        
                                    protected gsn.webservice.standard.xsd.GSNWebService_DataField[] localOutputFormat ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localOutputFormatTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return gsn.webservice.standard.xsd.GSNWebService_DataField[]
                           */
                           public  gsn.webservice.standard.xsd.GSNWebService_DataField[] getOutputFormat(){
                               return localOutputFormat;
                           }

                           
                        


                               
                              /**
                               * validate the array for OutputFormat
                               */
                              protected void validateOutputFormat(gsn.webservice.standard.xsd.GSNWebService_DataField[] param){
                             
                              }


                             /**
                              * Auto generated setter method
                              * @param param OutputFormat
                              */
                              public void setOutputFormat(gsn.webservice.standard.xsd.GSNWebService_DataField[] param){
                              
                                   validateOutputFormat(param);

                               
                                          if (param != null){
                                             //update the setting tracker
                                             localOutputFormatTracker = true;
                                          } else {
                                             localOutputFormatTracker = false;
                                                 
                                          }
                                      
                                      this.localOutputFormat=param;
                              }

                               
                             
                             /**
                             * Auto generated add method for the array for convenience
                             * @param param gsn.webservice.standard.xsd.GSNWebService_DataField
                             */
                             public void addOutputFormat(gsn.webservice.standard.xsd.GSNWebService_DataField param){
                                   if (localOutputFormat == null){
                                   localOutputFormat = new gsn.webservice.standard.xsd.GSNWebService_DataField[]{};
                                   }

                            
                                 //update the setting tracker
                                localOutputFormatTracker = true;
                            

                               java.util.List list =
                            org.apache.axis2.databinding.utils.ConverterUtil.toList(localOutputFormat);
                               list.add(param);
                               this.localOutputFormat =
                             (gsn.webservice.standard.xsd.GSNWebService_DataField[])list.toArray(
                            new gsn.webservice.standard.xsd.GSNWebService_DataField[list.size()]);

                             }
                             

                        /**
                        * field for Predicates
                        * This was an Array!
                        */

                        
                                    protected gsn.webservice.standard.xsd.GSNWebService_ConfPredicate[] localPredicates ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localPredicatesTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return gsn.webservice.standard.xsd.GSNWebService_ConfPredicate[]
                           */
                           public  gsn.webservice.standard.xsd.GSNWebService_ConfPredicate[] getPredicates(){
                               return localPredicates;
                           }

                           
                        


                               
                              /**
                               * validate the array for Predicates
                               */
                              protected void validatePredicates(gsn.webservice.standard.xsd.GSNWebService_ConfPredicate[] param){
                             
                              }


                             /**
                              * Auto generated setter method
                              * @param param Predicates
                              */
                              public void setPredicates(gsn.webservice.standard.xsd.GSNWebService_ConfPredicate[] param){
                              
                                   validatePredicates(param);

                               
                                          if (param != null){
                                             //update the setting tracker
                                             localPredicatesTracker = true;
                                          } else {
                                             localPredicatesTracker = false;
                                                 
                                          }
                                      
                                      this.localPredicates=param;
                              }

                               
                             
                             /**
                             * Auto generated add method for the array for convenience
                             * @param param gsn.webservice.standard.xsd.GSNWebService_ConfPredicate
                             */
                             public void addPredicates(gsn.webservice.standard.xsd.GSNWebService_ConfPredicate param){
                                   if (localPredicates == null){
                                   localPredicates = new gsn.webservice.standard.xsd.GSNWebService_ConfPredicate[]{};
                                   }

                            
                                 //update the setting tracker
                                localPredicatesTracker = true;
                            

                               java.util.List list =
                            org.apache.axis2.databinding.utils.ConverterUtil.toList(localPredicates);
                               list.add(param);
                               this.localPredicates =
                             (gsn.webservice.standard.xsd.GSNWebService_ConfPredicate[])list.toArray(
                            new gsn.webservice.standard.xsd.GSNWebService_ConfPredicate[list.size()]);

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
                       GSNWebService_WrapperDetails.this.serialize(parentQName,factory,xmlWriter);
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
                           namespacePrefix+":GSNWebService_WrapperDetails",
                           xmlWriter);
                   } else {
                       writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","type",
                           "GSNWebService_WrapperDetails",
                           xmlWriter);
                   }

               
                   }
               
                                            if (localWrapperURLs==null){
                                                 throw new org.apache.axis2.databinding.ADBException("wrapperURLs cannot be null!!");
                                            }
                                           localWrapperURLs.serialize(new javax.xml.namespace.QName("http://standard.webservice.gsn/xsd","wrapperURLs"),
                                               factory,xmlWriter);
                                         if (localOutputFormatTracker){
                                       if (localOutputFormat!=null){
                                            for (int i = 0;i < localOutputFormat.length;i++){
                                                if (localOutputFormat[i] != null){
                                                 localOutputFormat[i].serialize(new javax.xml.namespace.QName("http://standard.webservice.gsn/xsd","outputFormat"),
                                                           factory,xmlWriter);
                                                } else {
                                                   
                                                        // we don't have to do any thing since minOccures is zero
                                                    
                                                }

                                            }
                                     } else {
                                        
                                               throw new org.apache.axis2.databinding.ADBException("outputFormat cannot be null!!");
                                        
                                    }
                                 } if (localPredicatesTracker){
                                       if (localPredicates!=null){
                                            for (int i = 0;i < localPredicates.length;i++){
                                                if (localPredicates[i] != null){
                                                 localPredicates[i].serialize(new javax.xml.namespace.QName("http://standard.webservice.gsn/xsd","predicates"),
                                                           factory,xmlWriter);
                                                } else {
                                                   
                                                        // we don't have to do any thing since minOccures is zero
                                                    
                                                }

                                            }
                                     } else {
                                        
                                               throw new org.apache.axis2.databinding.ADBException("predicates cannot be null!!");
                                        
                                    }
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

                
                            elementList.add(new javax.xml.namespace.QName("http://standard.webservice.gsn/xsd",
                                                                      "wrapperURLs"));
                            
                            
                                    if (localWrapperURLs==null){
                                         throw new org.apache.axis2.databinding.ADBException("wrapperURLs cannot be null!!");
                                    }
                                    elementList.add(localWrapperURLs);
                                 if (localOutputFormatTracker){
                             if (localOutputFormat!=null) {
                                 for (int i = 0;i < localOutputFormat.length;i++){

                                    if (localOutputFormat[i] != null){
                                         elementList.add(new javax.xml.namespace.QName("http://standard.webservice.gsn/xsd",
                                                                          "outputFormat"));
                                         elementList.add(localOutputFormat[i]);
                                    } else {
                                        
                                                // nothing to do
                                            
                                    }

                                 }
                             } else {
                                 
                                        throw new org.apache.axis2.databinding.ADBException("outputFormat cannot be null!!");
                                    
                             }

                        } if (localPredicatesTracker){
                             if (localPredicates!=null) {
                                 for (int i = 0;i < localPredicates.length;i++){

                                    if (localPredicates[i] != null){
                                         elementList.add(new javax.xml.namespace.QName("http://standard.webservice.gsn/xsd",
                                                                          "predicates"));
                                         elementList.add(localPredicates[i]);
                                    } else {
                                        
                                                // nothing to do
                                            
                                    }

                                 }
                             } else {
                                 
                                        throw new org.apache.axis2.databinding.ADBException("predicates cannot be null!!");
                                    
                             }

                        }

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
        public static GSNWebService_WrapperDetails parse(javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception{
            GSNWebService_WrapperDetails object =
                new GSNWebService_WrapperDetails();

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
                    
                            if (!"GSNWebService_WrapperDetails".equals(type)){
                                //find namespace for the prefix
                                java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
                                return (GSNWebService_WrapperDetails)gsn.webservice.standard.xsd.ExtensionMapper.getTypeObject(
                                     nsUri,type,reader);
                              }
                        

                  }
                

                }

                

                
                // Note all attributes that were handled. Used to differ normal attributes
                // from anyAttributes.
                java.util.Vector handledAttributes = new java.util.Vector();
                

                 
                    
                    reader.next();
                
                        java.util.ArrayList list2 = new java.util.ArrayList();
                    
                        java.util.ArrayList list3 = new java.util.ArrayList();
                    
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://standard.webservice.gsn/xsd","wrapperURLs").equals(reader.getName())){
                                
                                                object.setWrapperURLs(gsn.webservice.standard.xsd.GSNWebService_WrapperURL.Factory.parse(reader));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                else{
                                    // A start element we are not expecting indicates an invalid parameter was passed
                                    throw new org.apache.axis2.databinding.ADBException("Unexpected subelement " + reader.getLocalName());
                                }
                            
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://standard.webservice.gsn/xsd","outputFormat").equals(reader.getName())){
                                
                                    
                                    
                                    // Process the array and step past its final element's end.
                                    list2.add(gsn.webservice.standard.xsd.GSNWebService_DataField.Factory.parse(reader));
                                                                
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
                                                                if (new javax.xml.namespace.QName("http://standard.webservice.gsn/xsd","outputFormat").equals(reader.getName())){
                                                                    list2.add(gsn.webservice.standard.xsd.GSNWebService_DataField.Factory.parse(reader));
                                                                        
                                                                }else{
                                                                    loopDone2 = true;
                                                                }
                                                            }
                                                        }
                                                        // call the converter utility  to convert and set the array
                                                        
                                                        object.setOutputFormat((gsn.webservice.standard.xsd.GSNWebService_DataField[])
                                                            org.apache.axis2.databinding.utils.ConverterUtil.convertToArray(
                                                                gsn.webservice.standard.xsd.GSNWebService_DataField.class,
                                                                list2));
                                                            
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://standard.webservice.gsn/xsd","predicates").equals(reader.getName())){
                                
                                    
                                    
                                    // Process the array and step past its final element's end.
                                    list3.add(gsn.webservice.standard.xsd.GSNWebService_ConfPredicate.Factory.parse(reader));
                                                                
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
                                                                if (new javax.xml.namespace.QName("http://standard.webservice.gsn/xsd","predicates").equals(reader.getName())){
                                                                    list3.add(gsn.webservice.standard.xsd.GSNWebService_ConfPredicate.Factory.parse(reader));
                                                                        
                                                                }else{
                                                                    loopDone3 = true;
                                                                }
                                                            }
                                                        }
                                                        // call the converter utility  to convert and set the array
                                                        
                                                        object.setPredicates((gsn.webservice.standard.xsd.GSNWebService_ConfPredicate[])
                                                            org.apache.axis2.databinding.utils.ConverterUtil.convertToArray(
                                                                gsn.webservice.standard.xsd.GSNWebService_ConfPredicate.class,
                                                                list3));
                                                            
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
           
          