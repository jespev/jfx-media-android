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
 $Id: ZephyrParserFactory.java,v 1.8 2010-11-02 21:02:27 joehw Exp $
 */
package com.sun.xml.stream;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.CharArrayReader;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;

import javax.xml.stream.*;
import javax.xml.stream.util.XMLEventAllocator ;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import org.apache.xerces.xni.parser.XMLInputSource;

/** Factory Implementation for XMLInputFactory.
 * @author Neeraj Bajaj Sun Microsystems
 * @author K.Venugopal Sun Microsystems
 */

//xxx: Should we be reusing the XMLInputSource object
public class ZephyrParserFactory extends javax.xml.stream.XMLInputFactory {
    
    //List of supported properties and default values.
    private PropertyManager fPropertyManager = new PropertyManager(PropertyManager.CONTEXT_READER) ;
    private static final boolean DEBUG = false;
    
    //Maintain a reference to last reader instantiated.
    private XMLReaderImpl fTempReader = null ;
    
    boolean fPropertyChanged = false;
    //no reader reuse by default
    boolean fReuseInstance = false;
    
    /** Creates a new instance of ZephryParserFactory */
    public ZephyrParserFactory() {
        //fPropertyManager = new PropertyManager(PropertyManager.CONTEXT_READER) ;
    }
    
    void initEventReader(){
        fPropertyChanged = true;
    }
    /**
     * @param inputstream
     * @throws XMLStreamException
     * @return
     */
    public XMLEventReader createXMLEventReader(InputStream inputstream) throws XMLStreamException {
        initEventReader();
        //delegate everything to XMLStreamReader
        return new XMLEventReaderImpl(createXMLStreamReader(inputstream));
    }
    
    public XMLEventReader createXMLEventReader(Reader reader) throws XMLStreamException {
        initEventReader();
        //delegate everything to XMLStreamReader
        return new XMLEventReaderImpl(createXMLStreamReader(reader));
    }
    
    public XMLEventReader createXMLEventReader(Source source) throws XMLStreamException {
        initEventReader();
        //delegate everything to XMLStreamReader
        return new XMLEventReaderImpl(createXMLStreamReader(source));
    }
    
    public XMLEventReader createXMLEventReader(String systemId, InputStream inputstream) throws XMLStreamException {
        initEventReader();
        //delegate everything to XMLStreamReader
        return new XMLEventReaderImpl(createXMLStreamReader(systemId, inputstream));
    }
    
    public XMLEventReader createXMLEventReader(java.io.InputStream stream, String encoding) throws XMLStreamException {
        initEventReader();
        //delegate everything to XMLStreamReader
        return new XMLEventReaderImpl(createXMLStreamReader(stream, encoding));
    }
    
    public XMLEventReader createXMLEventReader(String systemId, Reader reader) throws XMLStreamException {
        initEventReader();
        //delegate everything to XMLStreamReader
        return new XMLEventReaderImpl(createXMLStreamReader(systemId, reader));
    }
    
    /** Create a new XMLEventReader from an XMLStreamReader.  After being used
     * to construct the XMLEventReader instance returned from this method
     * the XMLStreamReader must not be used.
     * @param reader the XMLStreamReader to read from (may not be modified)
     * @return a new XMLEventReader
     * @throws XMLStreamException
     */
    public XMLEventReader createXMLEventReader(XMLStreamReader reader) throws XMLStreamException {
        initEventReader();
        //xxx: what do we do now -- instance is passed from the application
        //probably we should check if the state is at the start document,
        //eventreader call to next() should return START_DOCUMENT and
        //then delegate every call to underlying streamReader
        return new XMLEventReaderImpl(reader) ;
    }
            
    public XMLStreamReader createXMLStreamReader(Reader reader) throws XMLStreamException {
        return createXMLStreamReader(null, reader);
    }
    
    public XMLStreamReader createXMLStreamReader(String systemId, Reader reader) throws XMLStreamException {        
        XMLInputSource inputSource = new XMLInputSource(null,systemId,null,reader,null);
        return getXMLStreamReaderImpl(inputSource);
    }
    
    public XMLStreamReader createXMLStreamReader(Source source) throws XMLStreamException {
        return new XMLReaderImpl(jaxpSourcetoXMLInputSource(source), 
                new PropertyManager(fPropertyManager));
    }
    
    public XMLStreamReader createXMLStreamReader(InputStream inputStream) throws XMLStreamException {
        return createXMLStreamReader(null, inputStream, null);
    }
    
    public XMLStreamReader createXMLStreamReader(String systemId, InputStream inputStream) throws XMLStreamException {
        return createXMLStreamReader(systemId, inputStream, null);
    }
    
    
    public XMLStreamReader createXMLStreamReader(InputStream inputStream, String encoding) throws XMLStreamException {
        return createXMLStreamReader(null, inputStream, encoding);
    }
    
    //API doesn't allow to set systemId and encoding simultaneously. I think that is a
    //mistake - Neeraj Bajaj
    //Making this function public in case any one wants to use.
    public XMLStreamReader createXMLStreamReader(String systemId, InputStream inputStream, String encoding) throws XMLStreamException {        
        XMLInputSource inputSource = new XMLInputSource(null,systemId,null,inputStream,encoding);
        return getXMLStreamReaderImpl(inputSource);
    }
    
    public XMLEventAllocator getEventAllocator() {
        return (XMLEventAllocator)getProperty(XMLInputFactory.ALLOCATOR);
    }
    
    public XMLReporter getXMLReporter() {
        return (XMLReporter)fPropertyManager.getProperty(XMLInputFactory.REPORTER);
    }
    
    public XMLResolver getXMLResolver() {
        Object object = fPropertyManager.getProperty(XMLInputFactory.RESOLVER);
        return (XMLResolver)object;
        //return (XMLResolver)fPropertyManager.getProperty(XMLInputFactory.RESOLVER);
    }
    
    public void setXMLReporter(XMLReporter xmlreporter) {
        fPropertyManager.setProperty(XMLInputFactory.REPORTER, xmlreporter);
    }
    
    public void setXMLResolver(XMLResolver xmlresolver) {
        fPropertyManager.setProperty(XMLInputFactory.RESOLVER, xmlresolver);
    }
    
    /** Create a filtered event reader that wraps the filter around the event reader
     * @param reader the event reader to wrap
     * @param filter the filter to apply to the event reader
     * @throws XMLStreamException
     */
    public XMLEventReader createFilteredReader(XMLEventReader reader, EventFilter filter) throws XMLStreamException {
        return new EventFilterSupport(reader, filter);
    }
    
    /** Create a filtered reader that wraps the filter around the reader
     * @param reader the reader to filter
     * @param filter the filter to apply to the reader
     * @throws XMLStreamException
     */
    public XMLStreamReader createFilteredReader(XMLStreamReader reader, StreamFilter filter) throws XMLStreamException {
        if( reader != null && filter != null )
            return new XMLStreamFilterImpl(reader,filter);
        
        return null;
    }
    
    
    
    /** Get the value of a feature/property from the underlying implementation
     * @param name The name of the property (may not be null)
     * @return The value of the property
     * @throws IllegalArgumentException if the property is not supported
     */
    public Object getProperty(java.lang.String name) throws java.lang.IllegalArgumentException {
        if(name == null){
            throw new IllegalArgumentException("Property not supported");
        }
        if(fPropertyManager.containsProperty(name))
            return fPropertyManager.getProperty(name);
        throw new IllegalArgumentException("Property not supported");
    }
    
    /** Query the set of fProperties that this factory supports.
     *
     * @param name The name of the property (may not be null)
     * @return true if the property is supported and false otherwise
     */
    public boolean isPropertySupported(String name) {
        if(name == null)
            return false ;
        else
            return fPropertyManager.containsProperty(name);
    }
    
    /** Set a user defined event allocator for events
     * @param allocator the user defined allocator
     */
    public void setEventAllocator(XMLEventAllocator allocator) {
        fPropertyManager.setProperty(XMLInputFactory.ALLOCATOR, allocator);
    }
    
    /** Allows the user to set specific feature/property on the underlying implementation. The underlying implementation
     * is not required to support every setting of every property in the specification and may use IllegalArgumentException
     * to signal that an unsupported property may not be set with the specified value.
     * @param name The name of the property (may not be null)
     * @param value The value of the property
     * @throws java.lang.IllegalArgumentException if the property is not supported
     */
    public void setProperty(java.lang.String name, Object value) throws java.lang.IllegalArgumentException {
        
        if(name == null || value == null || !fPropertyManager.containsProperty(name) ){
            throw new IllegalArgumentException("Property "+name+" is not supported");
        }
        if(name == Constants.REUSE_INSTANCE || name.equals(Constants.REUSE_INSTANCE)){
            fReuseInstance = ((Boolean)value).booleanValue();
            if(DEBUG)System.out.println("fReuseInstance is set to " + fReuseInstance);
        }else{//for any other property set the flag
            //REVISIT: Even in this case instance can be reused, by passing PropertyManager
            fPropertyChanged = true;
        }
        
        fPropertyManager.setProperty(name,value);
    }
    
    XMLStreamReader getXMLStreamReaderImpl(XMLInputSource inputSource) throws javax.xml.stream.XMLStreamException{
        //1. if the temp reader is null -- create the instance and return
        if(fTempReader == null){
            fPropertyChanged = false;
            if(DEBUG)System.out.println("New Instance is being returned");
            return fTempReader = new XMLReaderImpl(inputSource, 
                                    new PropertyManager(fPropertyManager));
        }
        //if factory is configured to reuse the instance & this instance can be reused 
        //& the setProperty() hasn't been called
        if(fReuseInstance && fTempReader.canReuse() && !fPropertyChanged){
            if(DEBUG)System.out.println("Reusing the instance");
            //we can make setInputSource() call reset() and this way there wont be two function calls
            fTempReader.reset();
            fTempReader.setInputSource(inputSource);
            fPropertyChanged = false;
            return fTempReader;
        }else{
            fPropertyChanged = false;
            if(DEBUG)System.out.println("New Instance is being returned");
            //just return the new instance.. note that we are not setting  fTempReader to the newly created instance
            return fTempReader = new XMLReaderImpl(inputSource, 
                                    new PropertyManager(fPropertyManager));
        }
    }

    XMLInputSource jaxpSourcetoXMLInputSource(Source source){
            throw new UnsupportedOperationException("Cannot create " +
                "XMLStreamReader or XMLEventReader from a " +
                source.getClass().getName());        
    }
}//ZephyrParserFactory
