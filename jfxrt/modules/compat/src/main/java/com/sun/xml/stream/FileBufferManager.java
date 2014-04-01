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
 * $Id: FileBufferManager.java,v 1.4 2010-11-02 21:02:26 joehw Exp $
 */
package com.sun.xml.stream;

import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;

/**
 *
 * @author  Neeraj Bajaj, Sun Microsystems
 */
public class FileBufferManager extends BufferManager{
    
    static final int DEFAULT_LENGTH = 8192;
    static final int THRESH_HOLD = 10 * 8192;
    static final boolean DEBUG = false;
    
    CharsetDecoder decoder = null;
    FileChannel fChannel = null;
    CharBuffer charBuffer = null;
    boolean calledGetMore ;
    long remaining = -1 ;
    long filepos = 0;
    long filesize = -1;
    
    
    public FileBufferManager(FileInputStream stream, String encodingName) throws java.io.IOException{
        if(DEBUG)System.out.println("Encoding supplied = " + encodingName);
        init(stream);
        //setDecoder(encodingName);
        setDecoder("UTF-8");
    }
    
    void init(FileInputStream stream) throws java.io.IOException{
        //allocate the CharBuffer to the capacity of DEFAULT_LENGTH
        charBuffer = CharBuffer.allocate(2*DEFAULT_LENGTH);
        fChannel = stream.getChannel();
        filesize = fChannel.size();
        remaining = filesize;
        if(DEBUG)System.out.println("File size = " + remaining);
        
    }
    
    public boolean arrangeCapacity(int length) throws java.io.IOException{
        //this means some data has already been read from the file.
        if(!calledGetMore){
            getMore();
        }
        
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
        
    }//arrangeCapacity
    
    /**
     * This function gets more data from the file. If there is no more data a ByteBuffer of capacity 'zero'
     * is returned. This function always returns a new ByteBuffer.
     */
    public ByteBuffer getMoreBytes() throws java.io.IOException{
        int len = getLength();
        //if there are no more bytes to be read -- which means end of file.
        //allocate a buffer of capacity 'zero' probably we should be using observer pattern to let the scanner know
        //that end of file is reached.
        if(endOfStream) {
            return ByteBuffer.allocate(0);
        }
        ByteBuffer bb = null ;
        //if the filesize is more than 15 KB
        if(filesize > THRESH_HOLD){
            if(DEBUG)System.out.println("Using MappedByteBuffer");
            //use the map byte buffer for efficiency
            bb = fChannel.map(FileChannel.MapMode.READ_ONLY, filepos ,len);
            filepos = filepos + bb.limit();
        }
        else{
            if(DEBUG)System.out.println("Using ByteBuffer.allocate(" + getLength() + ")");
            bb = ByteBuffer.allocate(getLength());
            fChannel.read(bb);
            filepos = fChannel.position();
            //flip this buffer
            bb.flip();
        }
        
        //This should also be equivalent to "remaining = remaining - fChannel.position()"
        remaining = filesize - filepos;
        //remaining = remaining - mapBuffer.limit() ;
        if(remaining < 1){
            endOfStream = true;
        }
        return bb;
    }
    
    
    /**
     * This function returns true if some character data was loaded. Data is available via getCharBuffer().
     * If before calling this function CharBuffer had some data (i.e. remaining() > 0) then this function
     * first calls CharBuffer.compact() and then it is filled with more data.
     *
     * @see CharBuffer.compact()
     * @return true if some character data was loaded. False value can be assume to be end of current
     * entity.
     */
    public boolean getMore() throws java.io.IOException {
        
        calledGetMore = true;
        if(DEBUG){
            System.out.println("Remaining no. of bytes to be read = " + remaining);
        }
        if(endOfStream)return false;
        
        //1. getMoreBytes()
        ByteBuffer bb = getMoreBytes();
        
        //this function makes sure that after this call buffer position would be reset to '0' for reading
        //so if position is != '0' compact this buffer
        if(charBuffer.position() != 0){
            charBuffer.compact();
        }else{
            charBuffer.clear();
        }
        
        int before = charBuffer.position();
        
        if(DEBUG){
            printByteBuffer(bb);
            printCharBuffer(charBuffer);
        }
        
        //3. decode the bytes into given CharBuffer
        CoderResult cr = decoder.decode(bb, charBuffer, false);
        
        
        if(DEBUG){
            System.out.println("---------After first Decode---------");
            System.out.println("Coder = " + cr);
            printCharBuffer(charBuffer);
            printByteBuffer(bb);
        }
        
        //if there are still more number of bytes
        while(bb.remaining() > 0){
            if(cr.isOverflow()){
                //this might be a costly operation if the buffer need to be resized.
                //resizeCharBuffer(charBuffer.limit() + DEFAULT_LENGTH);
                resizeCharBuffer(charBuffer.limit() + bb.remaining());
            }
            //however if the output buffer got overflowed before the bytes were over resize the buffer -- this is costly but it's fine
            cr = decoder.decode(bb, charBuffer, true);
            if(DEBUG){
                System.out.println("-----In while Loop bb.remaining()----");
                printByteBuffer(bb);
                printCharBuffer(charBuffer);
            }
        }
        //is this step necessary ?
        if(cr.isUnderflow()){
            cr = decoder.decode(bb, charBuffer, true);
            decoder.flush(charBuffer);
        }
        
        //allow bytebuffer to be GCed.
        //bb = null ;
        //reset the decoder
        decoder.reset();
        
        if(DEBUG){
            System.out.print("CharBuffer Position, Before = " + before);
            System.out.println(" After= " + charBuffer.position());
        }
        
        if(charBuffer.position() > before ){
            //IMPORTANT: flip the buffer so that it is ready for get operations
            //-- set the position back to '0' before we return.
            charBuffer.flip();
            return true;
        }
        else{
            return false;
        }
    }
    
    
    public CharBuffer getCharBuffer(){
        return charBuffer;
    }
    
    //get the remaining data from existing char buffer
    CharSequence getCharSequence(){
        return charBuffer.subSequence(0, charBuffer.remaining());
    }
    
    //allocate a new CharBuffer for given capacity with the content filled with the CharSequence
    CharBuffer resizeCharBuffer(int capacity){
        if(DEBUG){
            System.out.println("RESIZING THE CHAR BUFFER FOR CAPACITY " + capacity);
            System.out.println("BEFORE RESIZING CHAR BUFFER DETAILS");
            printCharBuffer(charBuffer);
        }
        //allocate a new buffer of given capacity
        CharBuffer cb = CharBuffer.allocate(capacity);
        //we need to put the current charBuffer content to the new array
        //so flip the current charBuffer so that it is ready for the new buffer
        charBuffer = cb.put((CharBuffer)charBuffer.flip());
        if(DEBUG){
            System.out.println("AFTER RESIZING CHAR BUFFER DETAIL");
            printCharBuffer(charBuffer);
        }
        return charBuffer;
    }
    
    int getLength(){
        //decide the number of bytes that need to read
        return remaining < 2*DEFAULT_LENGTH ? (int)remaining: 2*DEFAULT_LENGTH;
    }
    
    
    void setDecoder(String encoding) throws java.io.IOException{
        //do we need to anything special for UTF-8 Reader
        if(encoding != null){
            decoder = Charset.forName(encoding).newDecoder();
        }
        else{
            ByteBuffer byteBuffer = ByteBuffer.allocate(4);
            fChannel.read(byteBuffer);
            if(DEBUG){
                System.out.println("Bytes remaining in the buffer = " + byteBuffer.remaining());
            }
            byte [] b = new byte[4];
            byteBuffer.get(b);
            Object [] array = getEncodingName(b, 4);
            if(DEBUG){
                System.out.println("Encoding autodetected = " + array[0]);
            }
            decoder = Charset.forName((String)array[0]).newDecoder();
        }
    }
    
    static void printByteBuffer(ByteBuffer bb){
        System.out.println("------------ByteBuffer Details---------");
        System.out.println("bb.position = " + bb.position());
        System.out.println("bb.remaining() = " + bb.remaining());
        System.out.println("bb.limit = " + bb.limit());
        System.out.println("bb.capacity = " + bb.capacity());
    }
    static void printCharBuffer(CharBuffer bb){
        System.out.println("----------- CharBuffer Details---------");
        System.out.println("bb.position = " + bb.position());
        System.out.println("bb.remaining() = " + bb.remaining());
        System.out.println("bb.limit = " + bb.limit());
        System.out.println("bb.capacity = " + bb.capacity());
    }
    
    public static void main(String [] args){
        try{
            FileBufferManager fb = new FileBufferManager(new FileInputStream(args[0]),"UTF-8");
            CharBuffer cb = fb.getCharBuffer();
            int i = 0 ;
            while(fb.getMore()){
                System.out.println("Loop " + i++ + " = " + fb.getCharBuffer().toString());
                System.out.println("------------Loop CharBuffer details--------" );
                printCharBuffer(cb);
            }
            System.out.println("End of file reached = " + fb.endOfStream() );
            System.out.println("Total no. of loops required = " + i);
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }
    
    public void close() throws java.io.IOException {
        if(fChannel != null){
            fChannel.close();
        }
    }
    
    public void setEncoding(String encoding) throws java.io.IOException {
        //xxx: this need to be implemented. if the encoding is different than the current encoding we need
        //to change the reader with the newly created reader
    }
    
}
