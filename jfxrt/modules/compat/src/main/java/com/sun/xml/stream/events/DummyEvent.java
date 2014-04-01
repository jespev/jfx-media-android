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
 * $Id: DummyEvent.java,v 1.6 2010-11-02 21:02:28 joehw Exp $
 */
package com.sun.xml.stream.events ;


import java.io.IOException;
import java.io.Writer;

import javax.xml.stream.events.XMLEvent;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import com.sun.xml.stream.XMLStreamException2;

/** DummyEvent is an abstract class. It provides functionality for most of the
 * function of XMLEvent.
 *
 * @author Neeraj Bajaj Sun Microsystems,Inc.
 * @author K.Venugopal Sun Microsystems,Inc.
 *
 */

public abstract class DummyEvent implements XMLEvent {
    
    /* Event type this event corresponds to */
    private int fEventType;
    protected Location fLocation = null;
    
    public DummyEvent() {
        
    }
    
    public DummyEvent(int i) {
        fEventType = i;
    }
    
    public int getEventType() {
        return fEventType;
    }
    
    protected void setEventType(int eventType){
        fEventType = eventType;
    }
    
    
    public boolean isStartElement() {
        return fEventType == XMLEvent.START_ELEMENT;
    }
    
    public boolean isEndElement() {
        return fEventType == XMLEvent.END_ELEMENT;
    }
    
    public boolean isEntityReference() {
        return fEventType == XMLEvent.ENTITY_REFERENCE;
    }
    
    public boolean isProcessingInstruction() {
        return fEventType == XMLEvent.PROCESSING_INSTRUCTION;
    }
    
    public boolean isCharacterData() {
        return fEventType == XMLEvent.CHARACTERS;
    }
    
    public boolean isStartDocument() {
        return fEventType == XMLEvent.START_DOCUMENT;
    }
    
    public boolean isEndDocument() {
        return fEventType == XMLEvent.END_DOCUMENT;
    }
    
    public Location getLocation(){
        return fLocation;
    }
    
    void setLocation(Location loc){
        fLocation = loc;
    }
    
    /** Returns this event as Characters, may result in
     * a class cast exception if this event is not Characters.
     */
    public Characters asCharacters() {
        return (Characters)this;
    }
    
    /** Returns this event as an end  element event, may result in
     * a class cast exception if this event is not a end element.
     */
    public EndElement asEndElement() {
        return (EndElement)this;
    }
    
    /** Returns this event as a start element event, may result in
     * a class cast exception if this event is not a start element.
     */
    public StartElement asStartElement() {
        return (StartElement)this;
    }
    
    /** This method is provided for implementations to provide
     * optional type information about the associated event.
     * It is optional and will return null if no information
     * is available.
     */
    public QName getSchemaType() {
        //Base class will take care of providing extra information about this event.
        return null;
    }
    
    /** A utility function to check if this event is an Attribute.
     * @see Attribute
     */
    public boolean isAttribute() {
        return fEventType == XMLEvent.ATTRIBUTE;
    }
    
    /** A utility function to check if this event is Characters.
     * @see Characters
     */
    public boolean isCharacters() {
        return fEventType == XMLEvent.CHARACTERS;
    }
    
    /** A utility function to check if this event is a Namespace.
     * @see Namespace
     */
    public boolean isNamespace() {
        return fEventType == XMLEvent.NAMESPACE;
    }
    
    /** This method will write the XMLEvent as per the XML 1.0 specification as Unicode characters.
     * No indentation or whitespace should be outputted.
     *
     * Any user defined event type SHALL have this method
     * called when being written to on an output stream.
     * Built in Event types MUST implement this method,
     * but implementations MAY choose not call these methods
     * for optimizations reasons when writing out built in
     * Events to an output stream.
     * The output generated MUST be equivalent in terms of the
     * infoset expressed.
     *
     * @param writer The writer that will output the data
     * @throws XMLStreamException if there is a fatal error writing the event
     */
    public void writeAsEncodedUnicode(Writer writer) throws XMLStreamException {
        try {
            writeAsEncodedUnicodeEx(writer);
        } catch (IOException e) {
            throw new XMLStreamException2(e);
        }
    }
    /** Helper method in order to expose IOException.
     * @param writer The writer that will output the data
     * @throws XMLStreamException if there is a fatal error writing the event
     * @throws IOException if there is an IO error
     */
    protected abstract void writeAsEncodedUnicodeEx(Writer writer) 
        throws IOException, XMLStreamException;

    /** Helper method to escape < > & for characters event and
     *  quotes, lt and amps for Entity
     */
    protected void charEncode(Writer writer, String data) 
        throws IOException
    {
        if (data == null || data == "") return;
        int i = 0, start = 0;
        int len = data.length();

        loop:
        for (; i < len; ++i) {
            switch (data.charAt(i)) {
            case '<':
                writer.write(data, start, i - start);
                writer.write("&lt;");
                start = i + 1;
                break;

            case '&':
                writer.write(data, start, i - start);
                writer.write("&amp;");
                start = i + 1;
                break;

            case '>':
                writer.write(data, start, i - start);
                writer.write("&gt;");
                start = i + 1;
                break;
            case '"':
                writer.write(data, start, i - start);
                writer.write("&quot;");
                start = i + 1;
                break;
            }
        }
        // Write any pending data
        writer.write(data, start, len - start);                  
    }
}
