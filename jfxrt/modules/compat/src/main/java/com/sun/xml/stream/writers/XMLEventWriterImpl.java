/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://glassfish.dev.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

/*
 * $Id: XMLEventWriterImpl.java,v 1.6 2010-11-02 21:02:29 joehw Exp $
 */
package com.sun.xml.stream.writers;

import java.util.Iterator;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.Comment;
import javax.xml.stream.events.DTD;
import javax.xml.stream.events.EntityReference;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.ProcessingInstruction;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import javax.xml.stream.XMLStreamWriter;
import com.sun.xml.stream.XMLStreamException2;

/**
 *
 * @author  Neeraj Bajaj, Sun Microsystems.
 *
 */
public class XMLEventWriterImpl implements XMLEventWriter{
    
    //delegate everything to XMLStreamWriter..
    private XMLStreamWriter fStreamWriter ;
    private static final boolean DEBUG = false;
    /**
     *
     * @param streamWriter
     */
    public XMLEventWriterImpl(XMLStreamWriter streamWriter){
        fStreamWriter = streamWriter;
    }
    
    /**
     *
     * @param xMLEventReader
     * @throws XMLStreamException
     */
    public void add(javax.xml.stream.XMLEventReader xMLEventReader) throws javax.xml.stream.XMLStreamException {
        if(xMLEventReader == null) throw new XMLStreamException2("Event reader shouldn't be null");
        while(xMLEventReader.hasNext()){
            add(xMLEventReader.nextEvent());
        }
    }
    
    /**
     *
     * @param xMLEvent
     * @throws XMLStreamException
     */
    public void add(javax.xml.stream.events.XMLEvent xMLEvent) throws javax.xml.stream.XMLStreamException {
        int type = xMLEvent.getEventType();
        switch(type){
            case XMLEvent.DTD:{
                DTD dtd = (DTD)xMLEvent ;
                if (DEBUG)System.out.println("Adding DTD = " + dtd.toString());
                fStreamWriter.writeDTD(dtd.getDocumentTypeDeclaration());
                break;
            }
            case XMLEvent.START_DOCUMENT :{
                StartDocument startDocument = (StartDocument)xMLEvent ;
                if (DEBUG)System.out.println("Adding StartDocument = " + startDocument.toString());
                try {
                    fStreamWriter.writeStartDocument(startDocument.getCharacterEncodingScheme(), startDocument.getVersion());
                }catch(XMLStreamException e) {
                    fStreamWriter.writeStartDocument(startDocument.getVersion());
                }
                break;
            }
            case XMLEvent.START_ELEMENT :{
                StartElement startElement = xMLEvent.asStartElement() ;
                if (DEBUG)System.out.println("Adding startelement = " + startElement.toString());
                QName qname = startElement.getName();
                fStreamWriter.writeStartElement(qname.getPrefix(), qname.getLocalPart(), qname.getNamespaceURI());
                
                //getNamespaces() Returns an Iterator of namespaces declared on this element. This Iterator does not contain
                //previously declared namespaces unless they appear on the current START_ELEMENT. Therefore
                //this list may contain redeclared namespaces and duplicate namespace declarations. Use the
                //getNamespaceContext() method to get the current context of namespace declarations.
                
                //so we should be using getNamespaces() to write namespace declarations for this START_ELEMENT
                Iterator iterator = startElement.getNamespaces();
                while(iterator.hasNext()){
                    Namespace namespace = (Namespace)iterator.next();
                    fStreamWriter.writeNamespace(namespace.getPrefix(), namespace.getNamespaceURI());
                }
                //REVISIT: What about writing attributes ?
                Iterator attributes = startElement.getAttributes();
                while(attributes.hasNext()){
                    Attribute attribute = (Attribute)attributes.next();
                    QName aqname = attribute.getName();
                    fStreamWriter.writeAttribute(aqname.getPrefix(), aqname.getNamespaceURI(), aqname.getLocalPart(),attribute.getValue());
                }
                break;
            }
            case XMLEvent.NAMESPACE:{
                Namespace namespace = (Namespace)xMLEvent;
                if (DEBUG)System.out.println("Adding namespace = " + namespace.toString());
                fStreamWriter.writeNamespace(namespace.getPrefix(), namespace.getNamespaceURI());
                break ;
            }
            case XMLEvent.COMMENT: {
                Comment comment = (Comment)xMLEvent ;
                if (DEBUG)System.out.println("Adding comment = " + comment.toString());
                fStreamWriter.writeComment(comment.getText());
                break;
            }
            case XMLEvent.PROCESSING_INSTRUCTION:{
                ProcessingInstruction processingInstruction = (ProcessingInstruction)xMLEvent ;
                if (DEBUG)System.out.println("Adding processing instruction = " + processingInstruction.toString());
                fStreamWriter.writeProcessingInstruction(processingInstruction.getTarget(), processingInstruction.getData());
                break;
            }
            case XMLEvent.CHARACTERS:{
                Characters characters = xMLEvent.asCharacters();
                if (DEBUG)System.out.println("Adding characters = " + characters.toString());
                //check if the CHARACTERS are CDATA
                if(characters.isCData()){
                    fStreamWriter.writeCData(characters.getData());
                }
                else{
                    fStreamWriter.writeCharacters(characters.getData());
                }
                break;
            }
            case XMLEvent.ENTITY_REFERENCE:{
                EntityReference entityReference = (EntityReference)xMLEvent ;
                if (DEBUG)System.out.println("Adding Entity Reference = "+ entityReference.toString());
                fStreamWriter.writeEntityRef(entityReference.getName());
                break;
            }
            case XMLEvent.ATTRIBUTE:{
                Attribute attribute = (Attribute)xMLEvent;
                if (DEBUG)System.out.println("Adding Attribute = " + attribute.toString());
                QName qname = attribute.getName();
                fStreamWriter.writeAttribute(qname.getPrefix(), qname.getNamespaceURI(), qname.getLocalPart(),attribute.getValue());
                break;
            }
            case XMLEvent.CDATA:{
                //there is no separate CDATA datatype but CDATA event can be reported
                //by using vendor specific CDATA property.
                Characters characters = (Characters)xMLEvent;
                if (DEBUG)System.out.println("Adding characters = " + characters.toString());
                if(characters.isCData()){
                    fStreamWriter.writeCData(characters.getData());
                }
                break;
            }
            //xxx: Why there isn't any event called Notation.
            //case XMLEvent.NOTATION_DECLARATION:{
            //}
            
            case XMLEvent.END_ELEMENT:{
                //we dont need to typecast it.. just call writeEndElement() and fStreamWriter will take care of it.
                //EndElement endElement = (EndElement)xMLEvent;
                fStreamWriter.writeEndElement();
                break;
            }
            case XMLEvent.END_DOCUMENT:{
                //we dont need to typecast just call writeEndDocument() and fStreamWriter will take care rest.
                //EndDocument endDocument = (EndDocument)xMLEvent;
                fStreamWriter.writeEndDocument();
                break;
            }
            //throw new XMLStreamException2("Unknown Event type = " + type);
        };
        
    }
    
    /**
     *
     * @throws XMLStreamException
     */
    public void close() throws javax.xml.stream.XMLStreamException {
        fStreamWriter.close();
    }
    
    /**
     *
     * @throws XMLStreamException will inturn call flush on the stream to which data is being
     * written.
     */
    public void flush() throws javax.xml.stream.XMLStreamException {
        fStreamWriter.flush();
    }
    
    /**
     *
     * @return
     */
    public javax.xml.namespace.NamespaceContext getNamespaceContext() {
        return fStreamWriter.getNamespaceContext();
    }
    
    /**
     *
     * @param namespaceURI Namespace URI
     * @throws XMLStreamException
     * @return prefix associated with the URI.
     */
    public String getPrefix(String namespaceURI) throws javax.xml.stream.XMLStreamException {
        return fStreamWriter.getPrefix(namespaceURI);
    }
    
    /**
     *
     * @param uri Namespace URI
     * @throws XMLStreamException
     */
    public void setDefaultNamespace(String uri) throws javax.xml.stream.XMLStreamException {
        fStreamWriter.setDefaultNamespace(uri);
    }
    
    /**
     *
     * @param namespaceContext Namespace Context
     * @throws XMLStreamException
     */
    public void setNamespaceContext(javax.xml.namespace.NamespaceContext namespaceContext) throws javax.xml.stream.XMLStreamException {
        fStreamWriter.setNamespaceContext(namespaceContext);
    }
    
    /**
     *
     * @param prefix namespace prefix associated with the uri.
     * @param uri Namespace URI
     * @throws XMLStreamException
     */
    public void setPrefix(String prefix, String uri) throws javax.xml.stream.XMLStreamException {
        fStreamWriter.setPrefix(prefix, uri);
    }
    
}//XMLEventWriterImpl
