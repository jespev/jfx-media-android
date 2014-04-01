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
 * $Id: CharacterEvent.java,v 1.5 2010-11-02 21:02:28 joehw Exp $
 */
package com.sun.xml.stream.events ;

import javax.xml.stream.events.Characters;
import java.io.Writer;
import java.io.IOException;
import javax.xml.stream.events.XMLEvent;
import org.apache.xerces.util.XMLChar;

/** Implementation of Character event.
 *
 *@author Neeraj Bajaj, Sun Microsystems
 *@author K.Venugopal, Sun Microsystems
 *
 */

public class CharacterEvent extends DummyEvent
implements Characters {
    /* data */
    private String fData;
    /*true if fData is CData */
    private boolean fIsCData;
    /* true if fData is ignorableWhitespace*/
    private boolean fIsIgnorableWhitespace;
    /* true if fData contet is whitespace*/
    private boolean fIsSpace = false;
    /*used to prevent scanning of  data multiple times */
    private boolean fCheckIfSpaceNeeded = true;
    
    public CharacterEvent() {
        fIsCData = false;
        init();
    }
    
    /**
     *
     * @param data Character Data.
     */
    public CharacterEvent(String data) {
        fIsCData = false;
        init();
        fData = data;
    }
    
    /**
     *
     * @param data Character Data.
     * @param flag true if CData
     */
    public CharacterEvent(String data, boolean flag) {
        init();
        fData = data;
        fIsCData = flag;
    }
    
    /**
     *
     * @param data Character Data.
     * @param flag true if CData
     * @param isIgnorableWhiteSpace true if data is ignorable whitespace.
     */
    public CharacterEvent(String data, boolean flag, boolean isIgnorableWhiteSpace) {
        init();
        fData = data;
        fIsCData = flag;
        fIsIgnorableWhitespace = isIgnorableWhiteSpace ;
    }
    
    protected void init() {
        setEventType(XMLEvent.CHARACTERS);
    }
    
    /**
     *
     * @return return data.
     */
    public String getData() {
        return fData;
    }
    
    /**
     *
     * @param String data
     */
    public void setData(String data){
        fData = data;
        fCheckIfSpaceNeeded = true;
    }
    
    /**
     *
     * @return boolean returns true if the data is CData
     */
    public boolean isCData() {
        return fIsCData;
    }
    
    /**
     *
     * @return String return the String representation of this event.
     */
    public String toString() {
        if(fIsCData)
            return "<![CDATA[" + getData() + "]]>";
        else
            return fData;
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
    protected void writeAsEncodedUnicodeEx(Writer writer) throws IOException
    {
        if (fIsCData) {
            writer.write("<![CDATA[" + getData() + "]]>");
        } else {
            charEncode(writer, fData);         
        }
    }    

    /**
     * Return true if this is ignorableWhiteSpace.  If
     * this event is ignorableWhiteSpace its event type will
     * be SPACE.
     * @return
     */
    public boolean isIgnorableWhiteSpace() {
        return fIsIgnorableWhitespace;
    }
    
    /**
     * Returns true if this set of Characters
     * is all whitespace.  Whitspace inside a document
     * is reported as CHARACTERS.  This method allows
     * checking of CHARACTERS events to see if they
     * are composed of only whitespace characters
     * @return
     */
    public boolean isWhiteSpace() {
        //no synchronization checks made.
        if(fCheckIfSpaceNeeded){
            checkWhiteSpace();
            fCheckIfSpaceNeeded = false;
        }
        return fIsSpace;
    }
    
    private void checkWhiteSpace(){
        //for now - remove dependancy of XMLChar
        if(fData != null && fData.length() >0 ){
            fIsSpace = true;
            for(int i=0;i<fData.length();i++){
                if(!XMLChar.isSpace(fData.charAt(i))){
                    fIsSpace = false;
                    break;
                }
            }
        }
    }
}
