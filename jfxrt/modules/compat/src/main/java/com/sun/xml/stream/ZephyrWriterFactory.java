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
 * $Id: ZephyrWriterFactory.java,v 1.9 2010-11-02 21:02:27 joehw Exp $
 */
package com.sun.xml.stream;

import com.sun.xml.stream.writers.XMLDOMWriterImpl;
import com.sun.xml.stream.writers.XMLEventWriterImpl;
import com.sun.xml.stream.writers.XMLStreamWriterImpl;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.XMLEventWriter;
import javax.xml.transform.Result;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamResult;
import java.io.OutputStream;
import java.io.Writer;

/**
 * This class provides the implementation of {@link XMLOutputFactory}.
 *
 * <p>
 * As long as {@link #fReuseInstance} is off, this implementation
 * is thread-safe, and the {@code create} methods can be invoked concurrently
 * from multiple threads safely.
 *
 * TODO: revisit if the instance reuse really makes sense.
 *
 * @author  Neeraj Bajaj,
 * @author k.venugopal@sun.com
 */
public class ZephyrWriterFactory extends XMLOutputFactory {
    
    //List of supported properties and default values.
    private PropertyManager fPropertyManager = new PropertyManager(PropertyManager.CONTEXT_WRITER);
    
    //cache the instance of XMLStreamWriterImpl
    private XMLStreamWriterImpl fStreamWriter = null;

    /**
     * TODO: at the current time, XMLStreamWriters are not Thread safe.
     */
    boolean fReuseInstance = false;
    
    public ZephyrWriterFactory() {
    }        
        
    public XMLEventWriter createXMLEventWriter(OutputStream outputStream) throws XMLStreamException {
        return createXMLEventWriter(outputStream,  null);
    }
    
    public XMLEventWriter createXMLEventWriter(OutputStream outputStream, String encoding) throws XMLStreamException {
        return new XMLEventWriterImpl(createXMLStreamWriter(outputStream, encoding));
    }
    
    public XMLEventWriter createXMLEventWriter(Result result) throws XMLStreamException {
        return new XMLEventWriterImpl(createXMLStreamWriter(result));
    }
    
    public XMLEventWriter createXMLEventWriter(Writer writer) throws XMLStreamException {
        return new XMLEventWriterImpl(createXMLStreamWriter(writer));
    }
    
    public XMLStreamWriter createXMLStreamWriter(Result result) throws XMLStreamException {

        if(result instanceof StreamResult){                
            return createXMLStreamWriter((StreamResult)result, null);
        }else if(result instanceof DOMResult){
            return new XMLDOMWriterImpl((DOMResult)result);
        }else if(result instanceof Result){
            return createXMLStreamWriter(new StreamResult(result.getSystemId()));
        }
        throw new java.lang.UnsupportedOperationException("result of type " + result + " is not supported");
    }

    public XMLStreamWriter createXMLStreamWriter(Writer writer) throws XMLStreamException {
        return createXMLStreamWriter(toStreamResult(null, writer, null) , null);
    }
    
    public XMLStreamWriter createXMLStreamWriter(OutputStream outputStream) throws XMLStreamException {
        return createXMLStreamWriter(outputStream, null);
    }
    
    public XMLStreamWriter createXMLStreamWriter(OutputStream outputStream, String encoding) throws XMLStreamException {
        return createXMLStreamWriter(toStreamResult(outputStream, null, null) , encoding);
    }
    
    public Object getProperty(String name) throws IllegalArgumentException {
        if(name == null){
            throw new IllegalArgumentException("Property not supported");
        }
        if(fPropertyManager.containsProperty(name))
            return fPropertyManager.getProperty(name);
        throw new IllegalArgumentException("Property not supported");
    }
    
    public boolean isPropertySupported(String name) {
        if(name == null){
            return false ;
        }
        else{
            return fPropertyManager.containsProperty(name);
        }
    }
    
    public void setProperty(String name, Object value) throws IllegalArgumentException {
        if(name == null || value == null || !fPropertyManager.containsProperty(name) ){
            throw new IllegalArgumentException("Property "+name+"is not supported");
        }
        if(name == Constants.REUSE_INSTANCE || name.equals(Constants.REUSE_INSTANCE)){
            fReuseInstance = ((Boolean)value).booleanValue();
            if(DEBUG)System.out.println("fReuseInstance is set to " + fReuseInstance);
            
            // TODO: XMLStreamWriters are not Thread safe,
            // don't let application think it is optimizing
            if (fReuseInstance) {
                throw new IllegalArgumentException(
                        "Property "
                        + name
                        + " is not supported: XMLStreamWriters are not Thread safe");
            }
        }else{//for any other property set the flag
            //REVISIT: Even in this case instance can be reused, by passing PropertyManager
            fPropertyChanged = true;
        }
        fPropertyManager.setProperty(name,value);        
    }
    
    private StreamResult toStreamResult(OutputStream os, Writer writer, String systemId){
        StreamResult sr = new StreamResult();
        sr.setOutputStream(os);
        sr.setWriter(writer);
        sr.setSystemId(systemId);
        return sr;
    }
    
    XMLStreamWriter createXMLStreamWriter(StreamResult sr, String encoding) throws XMLStreamException {
        //if factory is configured to reuse the instance & this instance can be reused 
        //& the setProperty() hasn't been called
        try{
            if(fReuseInstance && fStreamWriter != null && fStreamWriter.canReuse() && !fPropertyChanged){
                fStreamWriter.reset();
                fStreamWriter.setOutput(sr, encoding);
                if(DEBUG)System.out.println("reusing instance, object id : " + fStreamWriter);
                return fStreamWriter;
            }
            return fStreamWriter = new XMLStreamWriterImpl(sr, encoding, 
                    new PropertyManager(fPropertyManager));
        }catch(java.io.IOException io){
            throw new XMLStreamException2(io);
        }
    }//createXMLStreamWriter(StreamResult,String)
    
    private static final boolean DEBUG = false;
    
    /** This flag indicates the change of property. If true,
     * <code>PropertyManager</code> should be passed when creating 
     * <code>XMLStreamWriterImpl</code> */
    private boolean fPropertyChanged ;
}//ZephyrWriterFactory

