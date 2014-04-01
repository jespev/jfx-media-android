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
 * $Id: ZephyrEventFactory.java,v 1.4 2010-11-02 21:02:28 joehw Exp $
 */
package com.sun.xml.stream.events;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.Location;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.EntityDeclaration;

/**
 *
 * @author  Neeraj Bajaj, k.venugopal@sun.com
 */
public class ZephyrEventFactory extends XMLEventFactory {
    
    Location location = null;
    /** Creates a new instance of ZephyrEventFactory */
    public ZephyrEventFactory() {
    }
    
    public javax.xml.stream.events.Attribute createAttribute(String localName, String value) {
        AttributeImpl attr =  new AttributeImpl(localName, value);
        if(location != null)attr.setLocation(location);
        return attr;
    }
    
    public javax.xml.stream.events.Attribute createAttribute(javax.xml.namespace.QName name, String value) {
        return createAttribute(name.getPrefix(), name.getNamespaceURI(), name.getLocalPart(), value);
    }
    
    public javax.xml.stream.events.Attribute createAttribute(String prefix, String namespaceURI, String localName, String value) {
        AttributeImpl attr =  new AttributeImpl(prefix, namespaceURI, localName, value, null);
        if(location != null)attr.setLocation(location);
        return attr;
    }
    
    public javax.xml.stream.events.Characters createCData(String content) {
        //stax doesn't have separate CDATA event. This is taken care by
        //CHRACTERS event setting the cdata flag to true.
        CharacterEvent charEvent =  new CharacterEvent(content, true);
        if(location != null)charEvent.setLocation(location);
        return charEvent;
    }
    
    public javax.xml.stream.events.Characters createCharacters(String content) {
        CharacterEvent charEvent =  new CharacterEvent(content);
        if(location != null)charEvent.setLocation(location);
        return charEvent;
    }
    
    public javax.xml.stream.events.Comment createComment(String text) {
        CommentEvent charEvent =  new CommentEvent(text);
        if(location != null)charEvent.setLocation(location);
        return charEvent;
    }
    
    public javax.xml.stream.events.DTD createDTD(String dtd) {
        DTDEvent dtdEvent = new DTDEvent(dtd);
        if(location != null)dtdEvent.setLocation(location);
        return dtdEvent;
    }
    
    public javax.xml.stream.events.EndDocument createEndDocument() {
        EndDocumentEvent event =new EndDocumentEvent();
        if(location != null)event.setLocation(location);
        return event;
    }
    
    public javax.xml.stream.events.EndElement createEndElement(javax.xml.namespace.QName name, java.util.Iterator namespaces) {
        return createEndElement(name.getPrefix(), name.getNamespaceURI(), name.getLocalPart());
    }
    
    public javax.xml.stream.events.EndElement createEndElement(String prefix, String namespaceUri, String localName) {
        EndElementEvent event =  new EndElementEvent(prefix, namespaceUri, localName);
        if(location != null)event.setLocation(location);
        return event;
    }
    
    public javax.xml.stream.events.EndElement createEndElement(String prefix, String namespaceUri, String localName, java.util.Iterator namespaces) {
        
        EndElementEvent event =  new EndElementEvent(prefix, namespaceUri, localName);
        if(namespaces!=null){
            while(namespaces.hasNext())
                event.addNamespace((Namespace)namespaces.next());
        }
        if(location != null)event.setLocation(location);
        return event;
    }
    
    public javax.xml.stream.events.EntityReference createEntityReference(String name, EntityDeclaration entityDeclaration) {
        EntityReferenceEvent event =  new EntityReferenceEvent(name, entityDeclaration);
        if(location != null)event.setLocation(location);
        return event;
    }
    
    public javax.xml.stream.events.Characters createIgnorableSpace(String content) {
        CharacterEvent event =  new CharacterEvent(content, false, true);
        if(location != null)event.setLocation(location);
        return event;
    }
    
    public javax.xml.stream.events.Namespace createNamespace(String namespaceURI) {
        NamespaceImpl event =  new NamespaceImpl(namespaceURI);
        if(location != null)event.setLocation(location);
        return event;
    }
    
    public javax.xml.stream.events.Namespace createNamespace(String prefix, String namespaceURI) {
        NamespaceImpl event =  new NamespaceImpl(prefix, namespaceURI);
        if(location != null)event.setLocation(location);
        return event;
    }
    
    public javax.xml.stream.events.ProcessingInstruction createProcessingInstruction(String target, String data) {
        ProcessingInstructionEvent event =  new ProcessingInstructionEvent(target, data);
        if(location != null)event.setLocation(location);
        return event;
    }
    
    public javax.xml.stream.events.Characters createSpace(String content) {
        CharacterEvent event =  new CharacterEvent(content);
        if(location != null)event.setLocation(location);
        return event;
    }
    
    public javax.xml.stream.events.StartDocument createStartDocument() {
        StartDocumentEvent event = new StartDocumentEvent();
        if(location != null)event.setLocation(location);
        return event;
    }
    
    public javax.xml.stream.events.StartDocument createStartDocument(String encoding) {
        StartDocumentEvent event =  new StartDocumentEvent(encoding);
        if(location != null)event.setLocation(location);
        return event;
    }
    
    public javax.xml.stream.events.StartDocument createStartDocument(String encoding, String version) {
        StartDocumentEvent event =  new StartDocumentEvent(encoding, version);
        if(location != null)event.setLocation(location);
        return event;
    }
    
    public javax.xml.stream.events.StartDocument createStartDocument(String encoding, String version, boolean standalone) {
        StartDocumentEvent event =  new StartDocumentEvent(encoding, version, standalone);
        if(location != null)event.setLocation(location);
        return event;
    }
    
    public javax.xml.stream.events.StartElement createStartElement(javax.xml.namespace.QName name, java.util.Iterator attributes, java.util.Iterator namespaces) {
        return createStartElement(name.getPrefix(), name.getNamespaceURI(), name.getLocalPart(), attributes, namespaces);
    }
    
    public javax.xml.stream.events.StartElement createStartElement(String prefix, String namespaceUri, String localName) {
        StartElementEvent event =  new StartElementEvent(prefix, namespaceUri, localName);
        if(location != null)event.setLocation(location);
        return event;
    }
    
    public javax.xml.stream.events.StartElement createStartElement(String prefix, String namespaceUri, String localName, java.util.Iterator attributes, java.util.Iterator namespaces) {
        return createStartElement(prefix, namespaceUri, localName, attributes, namespaces, null);
    }
    
    public javax.xml.stream.events.StartElement createStartElement(String prefix, String namespaceUri, String localName, java.util.Iterator attributes, java.util.Iterator namespaces, javax.xml.namespace.NamespaceContext context) {
        StartElementEvent elem =  new StartElementEvent(prefix, namespaceUri, localName);
        elem.addAttributes(attributes);
        elem.addNamespaceAttributes(namespaces);
        elem.setNamespaceContext(context);
        if(location != null)elem.setLocation(location);
        return elem;
    }
    
    public void setLocation(javax.xml.stream.Location location) {
        this.location = location;
    }
    
}
