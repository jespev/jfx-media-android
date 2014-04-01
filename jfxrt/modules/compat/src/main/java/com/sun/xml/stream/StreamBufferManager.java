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
 * $Id: StreamBufferManager.java,v 1.4 2010-11-02 21:02:26 joehw Exp $
 */
package com.sun.xml.stream;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.CharBuffer;
import java.util.Locale;
import org.apache.xerces.impl.io.ASCIIReader;
import org.apache.xerces.impl.io.UCSReader;
import org.apache.xerces.impl.io.UTF8Reader;
import org.apache.xerces.util.EncodingMap;
import org.apache.xerces.util.XMLChar;


/**
 *
 * @author  Neeraj Bajaj, Sun Microsystems
 */
public class StreamBufferManager extends BufferManager{
    
    static final int DEFAULT_LENGTH = 8192;
    static final boolean DEBUG = false;
    CharBuffer charBuffer = null;
    Reader fReader = null;
    boolean fAllowJavaEncodings = true;
    
    /** Creates a new instance of StreamBufferManager */
    public StreamBufferManager(InputStream stream, String encoding) throws java.io.IOException{
        if(DEBUG)System.out.println("Encoding supplied = " + encoding);
        init(stream, encoding);
    }
    
    void init(InputStream istream, String encoding) throws java.io.IOException{
        Boolean isBigEndian = null;
        // wrap this stream in RewindableInputStream
        InputStream stream = new RewindableInputStream(istream);
        if(DEBUG){
            System.out.println("stream = " + stream);
        }
        // perform auto-detect of encoding if necessary
        if (encoding == null) {
            if(DEBUG){
                System.out.println("Autodetecting the encoding");
            }
            // read first four bytes and determine encoding
            final byte[] b4 = new byte[4];
            int count = 0;
            for (; count<4; count++ ) {
                b4[count] = (byte)stream.read();
            }
            if (count == 4) {
                Object [] encodingDesc = getEncodingName(b4, count);
                encoding = (String)(encodingDesc[0]);
                isBigEndian = (Boolean)(encodingDesc[1]);
                stream.reset();
                int offset = 0;
                // Special case UTF-8 files with BOM created by Microsoft
                // tools. It's more efficient to consume the BOM than make
                // the reader perform extra checks. -Ac
                if (count > 2 && encoding.equals("UTF-8")) {
                    int b0 = b4[0] & 0xFF;
                    int b1 = b4[1] & 0xFF;
                    int b2 = b4[2] & 0xFF;
                    //consume the byte order mark
                    if (b0 == 0xEF && b1 == 0xBB && b2 == 0xBF) {
                        // ignore first three bytes...
                        stream.skip(3);
                    }
                }
                if(DEBUG){
                    System.out.println("Encoding after autodetection = " + encoding);
                }
                fReader = createReader(stream, encoding, isBigEndian);
            }
            else {
                fReader = createReader(stream, encoding, isBigEndian);
            }
        }
        // use specified encoding
        else {
            fReader = createReader(stream, encoding, isBigEndian);
        }
        
        charBuffer = CharBuffer.allocate(DEFAULT_LENGTH);
    }
    
    
    public CharBuffer getCharBuffer(){
        return charBuffer;
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
    public boolean getMore() throws java.io.IOException{
        //compact() changes the position of the buffer
        if(charBuffer.position() != 0){
            charBuffer.compact();
        }
        char [] ch = charBuffer.array();
        int offset = charBuffer.position();
        //xxx: JDK 1.5 gives option to directly read into CharBuffer
        int count = fReader.read(ch, offset, charBuffer.capacity());
        
        if(DEBUG){
            System.out.println("No. of characters read = " + count);
        }
        if(count == -1){
            endOfStream = true;
            return false;
        }
        charBuffer = charBuffer.wrap(ch);
        //set the limit to the count of characters read
        charBuffer.limit(count);
        //xxx: what should be done if the characters read are 'zero' but still the end of the
        //stream is not reached.
        if(count > 0){
            return true;
        }
        else return false;
    }
    
    /**
     * Creates a reader capable of reading the given input stream in
     * the specified encoding.
     *
     * @param inputStream  The input stream.
     * @param encoding     The encoding name that the input stream is
     *                     encoded using. If the user has specified that
     *                     Java encoding names are allowed, then the
     *                     encoding name may be a Java encoding name;
     *                     otherwise, it is an ianaEncoding name.
     * @param isBigEndian   For encodings (like uCS-4), whose names cannot
     *                      specify a byte order, this tells whether the order is bigEndian.  null menas
     *                      unknown or not relevant.
     *
     * @return Returns a reader.
     */
    
    protected Reader createReader(InputStream inputStream, String encoding, Boolean isBigEndian) throws IOException {
        
        // normalize encoding name
        if (encoding == null) {
            encoding = "UTF-8";
        }
        
        // try to use an optimized reader
        String ENCODING = encoding.toUpperCase(Locale.ENGLISH);
        if (ENCODING.equals("UTF-8")) {
            if(DEBUG){
                System.out.println("Creating UTF-8 Reader");
            }
            //xxx: we are not passing ErrorReporter
            return new UTF8Reader(inputStream, DEFAULT_LENGTH, null, Locale.getDefault());
        }
        if (ENCODING.equals("US-ASCII")) {
            if (DEBUG) {
                System.out.println("$$$ creating ASCIIReader");
            }//xxx: we are not passing ErrorReporter
            return new ASCIIReader(inputStream, DEFAULT_LENGTH, null, Locale.getDefault());
        }
        if(ENCODING.equals("ISO-10646-UCS-4")) {
            if(isBigEndian != null) {
                boolean isBE = isBigEndian.booleanValue();
                if(isBE) {
                    return new UCSReader(inputStream, UCSReader.UCS4BE);
                } else {
                    return new UCSReader(inputStream, UCSReader.UCS4LE);
                }
            } else {
                throw new java.io.IOException("Encoding byte order not supported");
            }
        }
        if(ENCODING.equals("ISO-10646-UCS-2")) {
            if(isBigEndian != null) { // sould never happen with this encoding...
                boolean isBE = isBigEndian.booleanValue();
                if(isBE) {
                    return new UCSReader(inputStream, UCSReader.UCS2BE);
                } else {
                    return new UCSReader(inputStream, UCSReader.UCS2LE);
                }
            } else {
                throw new java.io.IOException("Encoding byte order not supported");
            }
        }
        
        // check for valid name
        boolean validIANA = XMLChar.isValidIANAEncoding(encoding);
        boolean validJava = XMLChar.isValidJavaEncoding(encoding);
        if (!validIANA || (fAllowJavaEncodings && !validJava)) {
            throw new java.io.IOException("Encoding declaration " + encoding + "not valid");
        }
        // try to use a Java reader
        String javaEncoding = EncodingMap.getIANA2JavaMapping(ENCODING);
        if (javaEncoding == null) {
            if(fAllowJavaEncodings) {
                javaEncoding = encoding;
            } else {
                throw new java.io.IOException("Encoding " + encoding + " not supported");
            }
        }
        if (DEBUG) {
            System.out.print("$$$ creating Java InputStreamReader: encoding="+javaEncoding);
            if (javaEncoding == encoding) {
                System.out.print(" (IANA encoding)");
            }
            System.out.println();
        }
        return new BufferedReader( new InputStreamReader(inputStream, javaEncoding));
        
    } // createReader(InputStream,String, Boolean): Reader
    
    int getLength(){
        //decide the number of bytes that need to read
        return DEFAULT_LENGTH ;
    }
    
    
    public static void main(String [] args){
        try{
            File file = new File(args[0]);
            System.out.println("url parameter = " + file.toURI().toString());
            URL url = new URL(file.toURI().toString());
            StreamBufferManager sb = new StreamBufferManager(url.openStream(),"UTF-8");
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
    
    public void close() throws java.io.IOException {
        if(fReader != null){
            fReader.close();
        }
    }
    
    public void setEncoding(String encoding) throws java.io.IOException {
        //xxx: this need to be implemented. if the encoding is different than the current encoding we need
        //to change the reader with the newly created reader
    }
    
    public boolean arrangeCapacity(int length) throws java.io.IOException {
        return false;
    }
    
    // This class wraps the byte inputstreams we're presented with.
    // We need it because java.io.InputStreams don't provide
    // functionality to reread processed bytes, and they have a habit
    // of reading more than one character when you call their read()
    // methods.  This means that, once we discover the true (declared)
    // encoding of a document, we can neither backtrack to read the
    // whole doc again nor start reading where we are with a new
    // reader.
    //
    // This class allows rewinding an inputStream by allowing a mark
    // to be set, and the stream reset to that position.  <strong>The
    // class assumes that it needs to read one character per
    // invocation when it's read() method is inovked, but uses the
    // underlying InputStream's read(char[], offset length) method--it
    // won't buffer data read this way!</strong>
    //
    // @author Neil Graham, IBM
    // @author Glenn Marcy, IBM
    
    protected final class RewindableInputStream extends InputStream {
        
        private InputStream fInputStream;
        private byte[] fData;
        private int fStartOffset;
        private int fEndOffset;
        private int fOffset;
        private int fLength;
        private int fMark;
        static final int DEFAULT_XMLDECL_BUFFER_SIZE = 64;
        
        public RewindableInputStream(InputStream is) {
            fData = new byte[DEFAULT_XMLDECL_BUFFER_SIZE];
            fInputStream = is;
            fStartOffset = 0;
            fEndOffset = -1;
            fOffset = 0;
            fLength = 0;
            fMark = 0;
        }
        
        public void setStartOffset(int offset) {
            fStartOffset = offset;
        }
        
        public void rewind() {
            fOffset = fStartOffset;
        }
        
        public int read() throws IOException {
            int b = 0;
            if (fOffset < fLength) {
                return fData[fOffset++] & 0xff;
            }
            if (fOffset == fEndOffset) {
                return -1;
            }
            if (fOffset == fData.length) {
                byte[] newData = new byte[fOffset << 1];
                System.arraycopy(fData, 0, newData, 0, fOffset);
                fData = newData;
            }
            b = fInputStream.read();
            if (b == -1) {
                fEndOffset = fOffset;
                return -1;
            }
            fData[fLength++] = (byte)b;
            fOffset++;
            return b & 0xff;
        }
        
        public int read(byte[] b, int off, int len) throws IOException {
            int bytesLeft = fLength - fOffset;
            if (bytesLeft == 0) {
                if (fOffset == fEndOffset) {
                    return -1;
                }
                return fInputStream.read(b, off, len);
                /**
                 * //System.out.println("fCurrentEntitty = " + fCurrentEntity );
                 * //System.out.println("fInputStream = " + fInputStream );
                 * // better get some more for the voracious reader...
                 * if(fCurrentEntity.mayReadChunks) {
                 * return fInputStream.read(b, off, len);
                 * }
                 *
                 * int returnedVal = read();
                 * if(returnedVal == -1) {
                 * fEndOffset = fOffset;
                 * return -1;
                 * }
                 * b[off] = (byte)returnedVal;
                 * return 1;
                 */
            }
            if (len < bytesLeft) {
                if (len <= 0) {
                    return 0;
                }
            }
            else {
                len = bytesLeft;
            }
            if (b != null) {
                System.arraycopy(fData, fOffset, b, off, len);
            }
            fOffset += len;
            return len;
        }
        
        public long skip(long n)
        throws IOException {
            int bytesLeft;
            if (n <= 0) {
                return 0;
            }
            bytesLeft = fLength - fOffset;
            if (bytesLeft == 0) {
                if (fOffset == fEndOffset) {
                    return 0;
                }
                return fInputStream.skip(n);
            }
            if (n <= bytesLeft) {
                fOffset += n;
                return n;
            }
            fOffset += bytesLeft;
            if (fOffset == fEndOffset) {
                return bytesLeft;
            }
            n -= bytesLeft;
           /*
            * In a manner of speaking, when this class isn't permitting more
            * than one byte at a time to be read, it is "blocking".  The
            * available() method should indicate how much can be read without
            * blocking, so while we're in this mode, it should only indicate
            * that bytes in its buffer are available; otherwise, the result of
            * available() on the underlying InputStream is appropriate.
            */
            return fInputStream.skip(n) + bytesLeft;
        }
        
        public int available() throws IOException {
            int bytesLeft = fLength - fOffset;
            if (bytesLeft == 0) {
                if (fOffset == fEndOffset) {
                    return -1;
                }
                return fInputStream.available();
            }
            return bytesLeft;
        }
        
        public void mark(int howMuch) {
            fMark = fOffset;
        }
        
        public void reset() {
            fOffset = fMark;
            //test();
        }
        
        public boolean markSupported() {
            return true;
        }
        
        public void close() throws IOException {
            if (fInputStream != null) {
                fInputStream.close();
                fInputStream = null;
            }
        }
    } // end of RewindableInputStream class
    
}
