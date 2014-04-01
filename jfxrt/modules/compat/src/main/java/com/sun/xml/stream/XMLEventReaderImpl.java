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
 * $Id: XMLEventReaderImpl.java,v 1.9 2010-11-02 21:02:26 joehw Exp $
 */
package com.sun.xml.stream;

import com.sun.xml.stream.XMLStreamException2;
import com.sun.xml.stream.events.XMLEventAllocatorImpl;
import java.util.NoSuchElementException;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.EntityReference;
import javax.xml.stream.events.XMLEvent;
import javax.xml.stream.util.XMLEventAllocator;

/**
 * @author Neeraj Bajaj Sun Microsystems
 * @author Santiago.PericasGeertsen@sun.com
 */

public class XMLEventReaderImpl implements javax.xml.stream.XMLEventReader{
    
    protected XMLStreamReader fXMLReader ;
    protected XMLEventAllocator fXMLEventAllocator;
    
    //only constructor will do because we delegate everything to underlying XMLStreamReader
    public XMLEventReaderImpl(XMLStreamReader reader) throws  XMLStreamException {
        fXMLReader = reader ;
        fXMLEventAllocator = (XMLEventAllocator)reader.getProperty(XMLInputFactory.ALLOCATOR);
        if(fXMLEventAllocator == null){
            fXMLEventAllocator = new XMLEventAllocatorImpl();
        }
        fPeekedEvent = fXMLEventAllocator.allocate(fXMLReader);        
    }
    
    
    public boolean hasNext() {
        //if we have the peeked event return 'true'
        if(fPeekedEvent != null)return true;
        //this is strange XMLStreamReader throws XMLStreamException
        //XMLEventReader doesn't throw XMLStreamException
        boolean next = false ;
        try{
            next = fXMLReader.hasNext();
        }catch(XMLStreamException ex){
            return false;
        }
        return next ;
    }
    
    
    public XMLEvent nextEvent() throws XMLStreamException {
        //if application peeked return the peeked event
        if(fPeekedEvent != null){
            fLastEvent = fPeekedEvent ;
            fPeekedEvent = null;
            return fLastEvent ;
        }
        else if(fXMLReader.hasNext()){
            //advance the reader to next state.
            fXMLReader.next();
            return fLastEvent = fXMLEventAllocator.allocate(fXMLReader);
        }
        else{
            fLastEvent = null;
            throw new NoSuchElementException();
        }
    }
    
    public void remove(){
        //remove of the event is not supported.
        throw new java.lang.UnsupportedOperationException();
    }
    
    
    public void close() throws XMLStreamException {
        fXMLReader.close();
    }
    
    /** Reads the content of a text-only element. Precondition:
     * the current event is START_ELEMENT. Postcondition:
     * The current event is the corresponding END_ELEMENT.
     * @throws XMLStreamException if the current event is not a START_ELEMENT
     * or if a non text element is encountered
     */
    public String getElementText() throws XMLStreamException {
        //we have to keep reference to the 'last event' of the stream to be able
        //to make this check - is there another way ? - nb.
        if(fLastEvent.getEventType() != XMLEvent.START_ELEMENT){
            throw new XMLStreamException2(
            "parser must be on START_ELEMENT to read next text", fLastEvent.getLocation());
        }
        
        // STag content ETag
        //[43]   content   ::=   CharData? ((element | Reference | CDSect | PI | Comment) CharData?)*
        
        //<foo>....some long text say in KB and underlying parser reports multiple character
        // but getElementText() events....</foo>
        
        String data = null;
        //having a peeked event makes things really worse -- we have to test the first event
        if(fPeekedEvent != null){
            XMLEvent event = fPeekedEvent ;
            fPeekedEvent = null;
            int type = event.getEventType();
            
            
            if(  type == XMLEvent.CHARACTERS || type == XMLEvent.SPACE ||
            type == XMLEvent.CDATA){
                data = event.asCharacters().getData();
            }
            else if(type == XMLEvent.ENTITY_REFERENCE){
                data = ((EntityReference)event).getDeclaration().getReplacementText();
            }
            else if(type == XMLEvent.COMMENT || type == XMLEvent.PROCESSING_INSTRUCTION){
                //ignore
            } else if(type == XMLEvent.START_ELEMENT) {
                throw new XMLStreamException2(
                "elementGetText() function expects text only elment but START_ELEMENT was encountered.", event.getLocation());
            }else if(type == XMLEvent.END_ELEMENT){
                return "";
            }
            
            //create the string buffer and add initial data
            StringBuffer buffer = new StringBuffer();
            if(data != null && data.length() > 0 ) {
                buffer.append(data);
            }
            //get the next event -- we should stop at END_ELEMENT but it can be any thing
            //things are worse when implementing this function in XMLEventReader because
            //there isn't any function called getText() which can get values for
            //space, cdata, characters and entity reference
            //nextEvent() would also set the last event.
            event = nextEvent();
            while(event.getEventType() != XMLEvent.END_ELEMENT){
                if(  type == XMLEvent.CHARACTERS || type == XMLEvent.SPACE ||
                type == XMLEvent.CDATA){
                    data = event.asCharacters().getData();
                }
                else if(type == XMLEvent.ENTITY_REFERENCE){
                    data = ((EntityReference)event).getDeclaration().getReplacementText();
                }
                else if(type == XMLEvent.COMMENT || type == XMLEvent.PROCESSING_INSTRUCTION){
                    //ignore
                } else if(type == XMLEvent.END_DOCUMENT) {
                    throw new XMLStreamException2("unexpected end of document when reading element text content");
                } else if(type == XMLEvent.START_ELEMENT) {
                    throw new XMLStreamException2(
                    "elementGetText() function expects text only elment but START_ELEMENT was encountered.", event.getLocation());
                } else {
                    throw new XMLStreamException2(
                    "Unexpected event type "+ type, event.getLocation());
                }
                //add the data to the buffer
                if(data != null && data.length() > 0 ) {
                    buffer.append(data);
                }
                event = nextEvent();
            }
            return buffer.toString();
        }//if (fPeekedEvent != null)
        
        //if there was no peeked, delegate everything to fXMLReader
        //update the last event before returning the text
        data = fXMLReader.getElementText();
        fLastEvent = fXMLEventAllocator.allocate(fXMLReader);
        return data;
    }
    
    /** Get the value of a feature/property from the underlying implementation
     * @param name The name of the property
     * @return The value of the property
     * @throws IllegalArgumentException if the property is not supported
     */
    public Object getProperty(java.lang.String name) throws java.lang.IllegalArgumentException {
        return fXMLReader.getProperty(name) ;
    }
    
    /** Skips any insignificant space events until a START_ELEMENT or
     * END_ELEMENT is reached. If anything other than space characters are
     * encountered, an exception is thrown. This method should
     * be used when processing element-only content because
     * the parser is not able to recognize ignorable whitespace if
     * the DTD is missing or not interpreted.
     * @throws XMLStreamException if anything other than space characters are encountered
     */
    public XMLEvent nextTag() throws XMLStreamException {
        //its really a pain if there is peeked event before calling nextTag()
        if(fPeekedEvent != null){
            //check the peeked event first.
            XMLEvent event = fPeekedEvent;
            fPeekedEvent = null ;
            int eventType = event.getEventType();
            //if peeked event is whitespace move to the next event
            //if peeked event is PI or COMMENT move to the next event
            if( (event.isCharacters() && event.asCharacters().isWhiteSpace())
            || eventType == XMLStreamConstants.PROCESSING_INSTRUCTION
            || eventType == XMLStreamConstants.COMMENT
            || eventType == XMLStreamConstants.START_DOCUMENT){
                event = nextEvent();
                eventType = event.getEventType();
            }
            
            //we have to have the while loop because there can be many PI or comment event in sucession
            while((event.isCharacters() && event.asCharacters().isWhiteSpace())
            || eventType == XMLStreamConstants.PROCESSING_INSTRUCTION
            || eventType == XMLStreamConstants.COMMENT){
                
                event = nextEvent();
                eventType = event.getEventType();
            }
            
            if (eventType != XMLStreamConstants.START_ELEMENT && eventType != XMLStreamConstants.END_ELEMENT) {
                throw new XMLStreamException2("expected start or end tag", event.getLocation());
            }
            return event;
        }
        
        //if there is no peeked event -- delegate the work of getting next event to fXMLReader
        fXMLReader.nextTag();
        return (fLastEvent = fXMLEventAllocator.allocate(fXMLReader));
    }
    
    public Object next() {
        Object object = null;
        try{
            object = nextEvent();
        }catch(XMLStreamException streamException){
            fLastEvent = null ;
            //xxx: what should be done in this case ?
            throw new NoSuchElementException();
        }
        return object;
    }
    
    public XMLEvent peek() throws XMLStreamException{
        //if someone call peek() two times we should just return the peeked event
        //this is reset if we call next() or nextEvent()
        if(fPeekedEvent != null) return fPeekedEvent;
        
        if(hasNext()){
            //revisit: we can implement peek() by calling underlying reader to advance
            // the stream and returning the event without the knowledge of the user
            // that the stream was advanced but the point is we are advancing the stream
            //here. -- nb.
            
            // Is there any application that relies on this behavior ?
            //Can it be an application knows that there is particularly very large 'comment' section
            //or character data which it doesn't want to read or to be returned as event
            //But as of now we are creating every event but it can be optimized not to create
            // the event.
            fXMLReader.next();
            fPeekedEvent = fXMLEventAllocator.allocate(fXMLReader);
            return fPeekedEvent;
        }else{
            return null;
        }
    }//peek()
    
    private XMLEvent fPeekedEvent;
    private XMLEvent fLastEvent;
    
}//XMLEventReaderImpl
