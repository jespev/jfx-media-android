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
 * $Id: XMLWriter.java,v 1.5 2010-11-02 21:02:29 joehw Exp $
 */
package com.sun.xml.stream.writers;

import java.io.IOException;
import java.io.Writer;
import org.apache.xerces.util.XMLStringBuffer;

/**
 * XMLWriter.
 *
 * <code>XMLWriter</code> is not thread safe.
 *
 * For efficiency this writer buffers the input. Use <code>flush()</code> function
 * to explicitly write the data to underlying stream.
 *
 * This writer is designed in such a way that it atleast buffers the input to the 
 * <code>size</code> specified. Unless <code>flush</code> is called, it guarantees that
 * data in chunks of size equal to or more than <code>size</code> specified will be written.
 *
 *
 * <code>XMLWriter</code> instance can be reused. <code>setWriter()</code> internally clears the
 * buffer and stores the reference to newly supplied <code>Writer</code> instance.
 *
 * @author Neeraj Bajaj Sun Microsystems, inc.
 * @author Sunitha Reddy Sun Microsystems, inc.
 */
public class XMLWriter extends Writer {
    
    private Writer writer ;
    private int size ;
    //keep the size of internal buffer more than 'size' required to avoid resizing
    private XMLStringBuffer buffer = new XMLStringBuffer(6 * (1 << 11) ); // 6 KB
    private static final int THRESHHOLD_LENGTH = 1 << 12 ; // 4 KB
    private static final boolean DEBUG = false;
    
    /** Creates the instance of <code>XMLWriter</code>
     */
    
     public XMLWriter(Writer writer){
         this(writer, THRESHHOLD_LENGTH);
     }
     
     /**
      * Creates the instnace of <code>XMLWriter</code>.
      * 
      * atleast buffers the input to the 
      * <code>size</code> specified.
      */
     public XMLWriter(Writer writer, int size){
         this.writer = writer ;
         this.size = size;
     }
     
     /**
     * Write a single character.  The character to be written is contained in
     * the 16 low-order bits of the given integer value; the 16 high-order bits
     * are ignored.
     *
     * <p> Subclasses that intend to support efficient single-character output
     * should override this method.
     *
     * @param c  int specifying a character to be written.
     * @exception  IOException  If an I/O error occurs
     */
    
    public void write(int c) throws IOException {
        ensureOpen();
        buffer.append((char)c);
        conditionalWrite();
    }
    
    /**
     * Write an array of characters.
     *
     * @param  cbuf  Array of characters to be written
     *
     * @exception  IOException  If an I/O error occurs
     */
    
    public void write(char cbuf[]) throws IOException {
        write(cbuf, 0, cbuf.length);        
    }        
    
    /**
     * Write a portion of an array of characters.
     *
     * @param  cbuf  Array of characters
     * @param  off   Offset from which to start writing characters
     * @param  len   Number of characters to write
     *
     * @exception  IOException  If an I/O error occurs
     */
    
    public void write(char cbuf[], int off, int len) throws IOException{
        ensureOpen();
        //optimization: if data size to be written is more than the 'size' specified, 
        //do not buffer the data but write the data straight to the underlying stream
        if(len > size){
            //first write the data that may be present in the buffer
            writeBufferedData();
            //write directly to stream
            writer.write(cbuf, off, len);
        }else{
            buffer.append(cbuf, off, len);
            conditionalWrite();
        }
    }
    
    /**
     * Write a portion of a string.
     *
     * @param  str  A String
     * @param  off  Offset from which to start writing characters
     * @param  len  Number of characters to write
     *
     * @exception  IOException  If an I/O error occurs
     */
    public void write(String str, int off, int len) throws IOException {
        write(str.toCharArray(), off, len);
    }
    
    /**
     * Write a string.
     *
     * @param  str  String to be written
     *
     * @exception  IOException  If an I/O error occurs
     */
    public void write(String str) throws IOException {
        //optimization: if data size to be written is more than the 'size' specified, 
        //do not buffer the data but write the data straight to the underlying stream - nb.
        if(str.length() > size){
            //first write the data that may be present in the buffer
            writeBufferedData();
            //write directly to stream
            writer.write(str);
        }else{
            buffer.append(str);
            conditionalWrite();
        }        
    }
    
    /**
     * Close the stream, flushing it first.  Once a stream has been closed,
     * further write() or flush() invocations will cause an IOException to be
     * thrown.  Closing a previously-closed stream, however, has no effect.
     *
     * @exception  IOException  If an I/O error occurs
     */
    public void close() throws IOException {
        if(writer == null) return;
        //flush it first
        flush();
        writer.close();
        writer = null ;
    }
    
    /**
     * Flush the stream.  If the stream has saved any characters from the
     * various write() methods in a buffer, write them immediately to their
     * intended destination.  Then, if that destination is another character or
     * byte stream, flush it.  Thus one flush() invocation will flush all the
     * buffers in a chain of Writers and OutputStreams.
     *
     * @exception  IOException  If an I/O error occurs
     */
    
    public void flush() throws IOException {
        ensureOpen();
        //write current data present in the buffer
        writeBufferedData();
        writer.flush();
    }
    
    /** Reset this Writer. 
     *
     * see @setWriter()
     */
    public void reset(){
        this.writer = null;
        buffer.clear();
        this.size = THRESHHOLD_LENGTH;
    }
    
    /**
     * Set the given <code>Writer</code>.
     *
     * @param Writer Writer.
     */
    public void setWriter(Writer writer){
        this.writer = writer;
        buffer.clear();
        this.size = THRESHHOLD_LENGTH;
    }
    
    /** Set the given <code>Writer</code>
     *
     * @param Writer Writer.
     * @param int    Writer will buffer the character data size, after that data is written to stream.
     */
    public void setWriter(Writer writer, int size){
        this.writer = writer;
        this.size = size;
    }
    
    /**
     * Returns underlying <code>Writer</code>
     */
    protected Writer getWriter() {
        return writer;
    }
    
    /** write the buffer data, if the buffer size has increased the size specified
     */
    private void conditionalWrite() throws IOException {
        if(buffer.length > size){
            if(DEBUG){
                System.out.println("internal buffer length " + buffer.length + " increased size limit : " + size);
                System.out.println("Data: ('" + new String(buffer.ch, buffer.offset, buffer.length) + "')");
            }
            writeBufferedData();
        }
    }
    
    /** Write the data present in the buffer to the writer.
     *  buffer is cleared after write operation.
     */
    private void writeBufferedData() throws IOException {
        writer.write(buffer.ch, buffer.offset, buffer.length);
        buffer.clear();
    }
    
    /** Check to make sure that the stream has not been closed */
    private void ensureOpen() throws IOException {
        if (writer == null)throw new IOException("Stream closed");
    }
}
