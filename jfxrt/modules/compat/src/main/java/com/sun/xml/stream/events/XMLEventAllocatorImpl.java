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
 * $Id: XMLEventAllocatorImpl.java,v 1.8 2010-11-02 21:02:28 joehw Exp $
 */
package com.sun.xml.stream.events;

import com.sun.xml.stream.PropertyManager;
import com.sun.xml.stream.XMLStreamException2;
import java.util.List;
import javax.xml.stream.util.XMLEventAllocator;
import javax.xml.stream.*;
import javax.xml.stream.events.*;
import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import org.apache.xerces.util.NamespaceContextWrapper;
import org.apache.xerces.util.NamespaceSupport;

/**
 * Implementation of XMLEvent Allocator.
 * @author Neeraj.bajaj@sun.com, k.venugopal@sun.com
 */
public class XMLEventAllocatorImpl implements XMLEventAllocator {
    
    /** Creates a new instance of XMLEventAllocator */
    public XMLEventAllocatorImpl() {
    }
    
    public javax.xml.stream.events.XMLEvent allocate(javax.xml.stream.XMLStreamReader xMLStreamReader) throws javax.xml.stream.XMLStreamException {
        if(xMLStreamReader == null )
            throw new XMLStreamException2("Reader cannot be null");
        //        allocate is not supposed to change the state of the reader so we shouldn't be calling next.
        //        return getNextEvent(xMLStreamReader);
        return getXMLEvent(xMLStreamReader);
    }
    
    public void allocate(javax.xml.stream.XMLStreamReader xMLStreamReader, javax.xml.stream.util.XMLEventConsumer xMLEventConsumer) throws javax.xml.stream.XMLStreamException {
        XMLEvent currentEvent = getXMLEvent(xMLStreamReader);
        if(currentEvent != null )
            xMLEventConsumer.add(currentEvent);
        
        return;
    }
    
    public javax.xml.stream.util.XMLEventAllocator newInstance() {
        return new XMLEventAllocatorImpl();
    }
    
    //REVISIT: shouldn't we be using XMLEventFactory to create events.
    XMLEvent getXMLEvent(XMLStreamReader streamReader){
        XMLEvent event = null;
        //returns the current event
        int eventType = streamReader.getEventType();
        switch(eventType){
            
            case XMLEvent.START_ELEMENT:{
                StartElementEvent startElementEvent = new StartElementEvent(getQName(streamReader));
                fillAttributes(startElementEvent,streamReader);
                //we might have different XMLStreamReader so check every time for the namespace aware property
                //we should be setting namespace related values only when isNamespaceAware is 'true'
                if( ((Boolean)streamReader.getProperty(XMLInputFactory.IS_NAMESPACE_AWARE)).booleanValue() ){
                    fillNamespaceAttributes(startElementEvent, streamReader);
                    setNamespaceContext(startElementEvent,streamReader);
                }
                
                startElementEvent.setLocation(streamReader.getLocation());
                event = startElementEvent ;
                break;
            }
            case XMLEvent.END_ELEMENT:{
                EndElementEvent endElementEvent = new EndElementEvent(getQName(streamReader));
                endElementEvent.setLocation(streamReader.getLocation());
                
                if( ((Boolean)streamReader.getProperty(XMLInputFactory.IS_NAMESPACE_AWARE)).booleanValue() ){
                    fillNamespaceAttributes(endElementEvent,streamReader);
                }
                event = endElementEvent ;
                break;
            }
            case XMLEvent.PROCESSING_INSTRUCTION:{
                ProcessingInstructionEvent piEvent = new ProcessingInstructionEvent(streamReader.getPITarget(),streamReader.getPIData());
                piEvent.setLocation(streamReader.getLocation());
                event = piEvent ;
                break;
            }
            case XMLEvent.CHARACTERS:{
                CharacterEvent cDataEvent = new CharacterEvent(streamReader.getText());
                cDataEvent.setLocation(streamReader.getLocation());
                event = cDataEvent ;
                break;
            }
            case XMLEvent.COMMENT:{
                CommentEvent commentEvent = new CommentEvent(streamReader.getText());
                commentEvent.setLocation(streamReader.getLocation());
                event = commentEvent ;
                break;
            }
            case XMLEvent.START_DOCUMENT:{
                StartDocumentEvent sdEvent = new StartDocumentEvent();
                sdEvent.setVersion(streamReader.getVersion());
                sdEvent.setEncoding(streamReader.getEncoding());
                if(streamReader.getCharacterEncodingScheme() != null){
                    sdEvent.setDeclaredEncoding(true);
                }else{
                    sdEvent.setDeclaredEncoding(false);
                }
                sdEvent.setStandalone(streamReader.isStandalone());
                sdEvent.setLocation(streamReader.getLocation());
                event = sdEvent ;
                break;
            }
            case XMLEvent.END_DOCUMENT:{
                EndDocumentEvent endDocumentEvent = new EndDocumentEvent() ;
                endDocumentEvent.setLocation(streamReader.getLocation());
                event = endDocumentEvent ;
                break;
            }
            case XMLEvent.ENTITY_REFERENCE:{
                EntityReferenceEvent entityEvent =  new EntityReferenceEvent(streamReader.getLocalName(), new EntityDeclarationImpl(streamReader.getLocalName(),streamReader.getText()));
                entityEvent.setLocation(streamReader.getLocation());
                event = entityEvent;
                break;
                
            }
            case XMLEvent.ATTRIBUTE:{
                event = null ;
                break;
            }
            case XMLEvent.DTD:{
                DTDEvent dtdEvent = new DTDEvent(streamReader.getText());
                dtdEvent.setLocation(streamReader.getLocation());
                List entities = (List)streamReader.getProperty(PropertyManager.STAX_ENTITIES);
                if (entities != null && entities.size() != 0) dtdEvent.setEntities(entities);
                List notations = (List)streamReader.getProperty(PropertyManager.STAX_NOTATIONS);
                if (notations != null && notations.size() != 0) dtdEvent.setNotations(notations);                
                event = dtdEvent;
                break;
            }
            case XMLEvent.CDATA:{
                CharacterEvent cDataEvent = new CharacterEvent(streamReader.getText(),true);
                cDataEvent.setLocation(streamReader.getLocation());
                event = cDataEvent ;
                break;
            }
            case XMLEvent.SPACE:{
                CharacterEvent spaceEvent = new CharacterEvent(streamReader.getText(),false,true);
                spaceEvent.setLocation(streamReader.getLocation());
                event = spaceEvent ;
                break;
            }
        }
        return event ;
    }
    
    //this function is not used..
    protected XMLEvent getNextEvent(XMLStreamReader streamReader) throws XMLStreamException{
        //advance the reader to next event.
        streamReader.next();
        return getXMLEvent(streamReader);
    }
    
    protected void fillAttributes(StartElementEvent event,XMLStreamReader xmlr){
        
        int len = xmlr.getAttributeCount();
        QName qname = null;
        String prefix = null;
        String localpart = null;
        AttributeImpl attr = null;
        NamespaceImpl nattr = null;
        for(int i=0; i<len ;i++){
            qname = xmlr.getAttributeName(i);
            prefix = qname.getPrefix();
            localpart = qname.getLocalPart();
            //this method doesn't include namespace declarations
            //so we can be sure that there wont be any namespace declaration as part of this function call
            //we can avoid this check - nb.
            /**
             * if (prefix.equals(XMLConstants.XMLNS_ATTRIBUTE) ) {
             * attr = new NamespaceImpl(localpart,xmlr.getAttributeValue(i));
             * }else if (prefix.equals(XMLConstants.DEFAULT_NS_PREFIX)){
             * attr = new NamespaceImpl(xmlr.getAttributeValue(i));
             * }else{
             * attr = new AttributeImpl();
             * attr.setName(qname);
             * }
             **/
            attr = new AttributeImpl();
            attr.setName(qname);
            attr.setAttributeType(xmlr.getAttributeType(i));
            attr.setSpecified(xmlr.isAttributeSpecified(i));
            attr.setValue(xmlr.getAttributeValue(i));
            event.addAttribute(attr);
        }
    }
    
    protected void fillNamespaceAttributes(StartElementEvent event,XMLStreamReader xmlr){
        int count = xmlr.getNamespaceCount();
        String uri = null;
        String prefix = null;
        NamespaceImpl attr = null;
        for(int i=0;i< count;i++){
            uri = xmlr.getNamespaceURI(i);
            prefix = xmlr.getNamespacePrefix(i);
            if(prefix == null){
                prefix = XMLConstants.DEFAULT_NS_PREFIX;
            }
            attr = new NamespaceImpl(prefix,uri);
            event.addNamespaceAttribute(attr);
        }
    }
    
    protected void fillNamespaceAttributes(EndElementEvent event,XMLStreamReader xmlr){
        int count = xmlr.getNamespaceCount();
        String uri = null;
        String prefix = null;
        NamespaceImpl attr = null;
        for(int i=0;i< count;i++){
            uri = xmlr.getNamespaceURI(i);
            prefix = xmlr.getNamespacePrefix(i);
            if(prefix == null){
                prefix = XMLConstants.DEFAULT_NS_PREFIX;
            }
            attr = new NamespaceImpl(prefix,uri);
            event.addNamespace(attr);
        }
    }
    
    //Revisit : Creating a new Namespacecontext for now.
    //see if we can do better job.
    private void setNamespaceContext(StartElementEvent event , XMLStreamReader xmlr){
        NamespaceContextWrapper contextWrapper =(NamespaceContextWrapper) xmlr.getNamespaceContext();
        NamespaceSupport ns = new NamespaceSupport(contextWrapper.getNamespaceContext());
        event.setNamespaceContext(new NamespaceContextWrapper(ns));
    }
    
    private QName getQName(XMLStreamReader xmlr) {
        return new QName(xmlr.getNamespaceURI(), xmlr.getLocalName(), 
                xmlr.getPrefix());
    }
}
