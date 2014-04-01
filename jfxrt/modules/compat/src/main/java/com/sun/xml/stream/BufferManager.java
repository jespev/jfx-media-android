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
 * $Id: BufferManager.java,v 1.4 2010-11-02 21:02:25 joehw Exp $
 */
package com.sun.xml.stream;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.CharBuffer;
import org.apache.xerces.xni.parser.XMLInputSource;

/**
 * @author  Neeraj Bajaj, Sun Microsystems
 */
public abstract class BufferManager {
            
    protected boolean endOfStream = false;
    static boolean DEBUG = false;
    

    public static BufferManager getBufferManager(XMLInputSource inputSource) throws java.io.IOException{
        
        InputStream stream = inputSource.getByteStream();
        if(stream instanceof FileInputStream){
            if(DEBUG){
                System.out.println("Using FileBufferManager");
            }
            return new FileBufferManager((FileInputStream)stream,inputSource.getEncoding());
        }else{
            if(DEBUG){
                System.out.println("Using StreamBufferManager");
            }
            return new StreamBufferManager(stream, inputSource.getEncoding());
        }        
    }
    
    /**
     * This function returns true if some character data was loaded. Data is available via getCharBuffer().
     * If before calling this function CharBuffer had some data (i.e. remaining() > 0) then this function 
     * first calls CharBuffer.compact() and then it is filled with more data. After calling this function
     * CharBuffer.position() is always 'zero'.
     *
     * @see CharBuffer.compact()
     * @return true if some character data was loaded. False value can be assume to be end of current 
     * entity.
     */
    
    public abstract boolean getMore() throws java.io.IOException;
    
    public abstract CharBuffer getCharBuffer();
    
    /**
     *xxx: This should be an abstract method because in StreamBufferManager
     * CharBuffer capacity doesn't grow
     */
    public abstract boolean arrangeCapacity(int length) throws java.io.IOException;
    
    /**{
        if(getCharBuffer().limit() - getCharBuffer().position() >= length){
            return true;
        }
        while( (getCharBuffer().limit() - getCharBuffer().position()) < length){            
            if(endOfStream())break;
            getMore();
        }
        if(getCharBuffer().limit() - getCharBuffer().position() >= length){
            return true;
        }else{        
            return false;        
        }
    }*/
    
    /** This file signals the end of file
     * @return true/false signals the end of file.
     */
    public boolean endOfStream(){
        return endOfStream;
    }

    public abstract void close() throws java.io.IOException;
    
    public abstract void setEncoding(String encoding) throws java.io.IOException;
    
    /**
     * Returns the IANA encoding name that is auto-detected from
     * the bytes specified, with the endian-ness of that encoding where appropriate.
     *
     * @param b4    The first four bytes of the input.
     * @param count The number of bytes actually read.
     * @return a 2-element array:  the first element, an IANA-encoding string,
     *  the second element a Boolean which is true iff the document is big endian, false
     *  if it's little-endian, and null if the distinction isn't relevant.
     */
    protected Object[] getEncodingName(byte[] b4, int count) {
        
        if (count < 2) {
            return new Object[]{"UTF-8", null};
        }
        
        // UTF-16, with BOM
        int b0 = b4[0] & 0xFF;
        int b1 = b4[1] & 0xFF;
        if (b0 == 0xFE && b1 == 0xFF) {
            // UTF-16, big-endian
            return new Object [] {"UTF-16BE", new Boolean(true)};
        }
        if (b0 == 0xFF && b1 == 0xFE) {
            // UTF-16, little-endian
            return new Object [] {"UTF-16LE", new Boolean(false)};
        }
        
        // default to UTF-8 if we don't have enough bytes to make a
        // good determination of the encoding
        if (count < 3) {
            return new Object [] {"UTF-8", null};
        }
        
        // UTF-8 with a BOM
        int b2 = b4[2] & 0xFF;
        if (b0 == 0xEF && b1 == 0xBB && b2 == 0xBF) {
            return new Object [] {"UTF-8", null};
        }
        
        // default to UTF-8 if we don't have enough bytes to make a
        // good determination of the encoding
        if (count < 4) {
            return new Object [] {"UTF-8", null};
        }
        
        // other encodings
        int b3 = b4[3] & 0xFF;
        if (b0 == 0x00 && b1 == 0x00 && b2 == 0x00 && b3 == 0x3C) {
            // UCS-4, big endian (1234)
            return new Object [] {"ISO-10646-UCS-4", new Boolean(true)};
        }
        if (b0 == 0x3C && b1 == 0x00 && b2 == 0x00 && b3 == 0x00) {
            // UCS-4, little endian (4321)
            return new Object [] {"ISO-10646-UCS-4", new Boolean(false)};
        }
        if (b0 == 0x00 && b1 == 0x00 && b2 == 0x3C && b3 == 0x00) {
            // UCS-4, unusual octet order (2143)
            // REVISIT: What should this be?
            return new Object [] {"ISO-10646-UCS-4", null};
        }
        if (b0 == 0x00 && b1 == 0x3C && b2 == 0x00 && b3 == 0x00) {
            // UCS-4, unusual octect order (3412)
            // REVISIT: What should this be?
            return new Object [] {"ISO-10646-UCS-4", null};
        }
        if (b0 == 0x00 && b1 == 0x3C && b2 == 0x00 && b3 == 0x3F) {
            // UTF-16, big-endian, no BOM
            // (or could turn out to be UCS-2...
            // REVISIT: What should this be?
            return new Object [] {"UTF-16BE", new Boolean(true)};
        }
        if (b0 == 0x3C && b1 == 0x00 && b2 == 0x3F && b3 == 0x00) {
            // UTF-16, little-endian, no BOM
            // (or could turn out to be UCS-2...
            return new Object [] {"UTF-16LE", new Boolean(false)};
        }
        if (b0 == 0x4C && b1 == 0x6F && b2 == 0xA7 && b3 == 0x94) {
            // EBCDIC
            // a la xerces1, return CP037 instead of EBCDIC here
            return new Object [] {"CP037", null};
        }
        
        // default encoding
        return new Object [] {"UTF-8", null};
        
    } // getEncodingName(byte[],int):Object[]
    
    public static void main(String [] args){
        try{
            File file = new File(args[0]);
            System.out.println("url parameter = " + file.toURI().toString());
            URL url = new URL(file.toURI().toString());
            XMLInputSource inputSource = new XMLInputSource(null,null,null,new FileInputStream(file),"UTF-8");
            BufferManager sb = BufferManager.getBufferManager(inputSource);
            CharBuffer cb = sb.getCharBuffer();
            int i = 0 ;
            while(sb.getMore()){
                System.out.println("Loop " + i++ + " = " + sb.getCharBuffer());
            }
            System.out.println("End of stream reached = " + sb.endOfStream() );        
            System.out.println("Total no. of loops required = " + i);
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }
}
