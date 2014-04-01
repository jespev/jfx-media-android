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
 *
 *
 * This file incorporates work covered by the following copyright and
 * permission notice:
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * $Id: XMLEntityManager.java,v 1.8 2010-11-02 21:02:26 joehw Exp $
 */
package com.sun.xml.stream ;

import java.io.*;
import java.io.BufferedReader;
import java.net.URL;
import java.util.*;

import org.apache.xerces.impl.io.*;
import org.apache.xerces.impl.msg.XMLMessageFormatter;
import org.apache.xerces.util.*;
import org.apache.xerces.xni.XMLResourceIdentifier;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.parser.*;

/**
 * Will keep track of current entity.
 *
 * The entity manager handles the registration of general and parameter
 * entities; resolves entities; and starts entities. The entity manager
 * is a central component in a standard parser configuration and this
 * class works directly with the entity scanner to manage the underlying
 * xni.
 * <p>
 * This component requires the following features and properties from the
 * component manager that uses it:
 * <ul>
 *  <li>http://xml.org/sax/features/validation</li>
 *  <li>http://xml.org/sax/features/external-general-entities</li>
 *  <li>http://xml.org/sax/features/external-parameter-entities</li>
 *  <li>http://apache.org/xml/features/allow-java-encodings</li>
 *  <li>http://apache.org/xml/properties/internal/symbol-table</li>
 *  <li>http://apache.org/xml/properties/internal/error-reporter</li>
 *  <li>http://apache.org/xml/properties/internal/entity-resolver</li>
 * </ul>
 *
 *
 * @author Andy Clark, IBM
 * @author Arnaud  Le Hors, IBM
 * @author K.Venugopal SUN Microsystems
 * @author Neeraj Bajaj SUN Microsystems
 *
 * @version $Id: XMLEntityManager.java,v 1.8 2010-11-02 21:02:26 joehw Exp $
 */
public class XMLEntityManager implements XMLComponent, XMLEntityResolver {
    
    //
    // Constants
    //
    
    /** Default buffer size (2048). */
    public static final int DEFAULT_BUFFER_SIZE = 8192;
    
    /** Default buffer size before we've finished with the XMLDecl:  */
    public static final int DEFAULT_XMLDECL_BUFFER_SIZE = 64;
    
    /** Default internal entity buffer size (1024). */
    public static final int DEFAULT_INTERNAL_BUFFER_SIZE = 1024;
    
    // feature identifiers
    
    /** Feature identifier: validation. */
    protected static final String VALIDATION =
    Constants.SAX_FEATURE_PREFIX + Constants.VALIDATION_FEATURE;
    
    /** Feature identifier: external general entities. */
    protected static final String EXTERNAL_GENERAL_ENTITIES =
    Constants.SAX_FEATURE_PREFIX + Constants.EXTERNAL_GENERAL_ENTITIES_FEATURE;
    
    /** Feature identifier: external parameter entities. */
    protected static final String EXTERNAL_PARAMETER_ENTITIES =
    Constants.SAX_FEATURE_PREFIX + Constants.EXTERNAL_PARAMETER_ENTITIES_FEATURE;
    
    /** Feature identifier: allow Java encodings. */
    protected static final String ALLOW_JAVA_ENCODINGS =
    Constants.XERCES_FEATURE_PREFIX + Constants.ALLOW_JAVA_ENCODINGS_FEATURE;
    
    /** Feature identifier: warn on duplicate EntityDef */
    protected static final String WARN_ON_DUPLICATE_ENTITYDEF =
    Constants.XERCES_FEATURE_PREFIX +Constants.WARN_ON_DUPLICATE_ENTITYDEF_FEATURE;
    
    // property identifiers
    
    /** Property identifier: symbol table. */
    protected static final String SYMBOL_TABLE =
    Constants.XERCES_PROPERTY_PREFIX + Constants.SYMBOL_TABLE_PROPERTY;
    
    /** Property identifier: error reporter. */
    protected static final String ERROR_REPORTER =
    Constants.XERCES_PROPERTY_PREFIX + Constants.ERROR_REPORTER_PROPERTY;
    
    /** Property identifier: entity resolver. */
    protected static final String ENTITY_RESOLVER =
    Constants.XERCES_PROPERTY_PREFIX + Constants.ENTITY_RESOLVER_PROPERTY;
    
    protected static final String STAX_ENTITY_RESOLVER =
    Constants.XERCES_PROPERTY_PREFIX + Constants.STAX_ENTITY_RESOLVER_PROPERTY;
    
    // property identifier:  ValidationManager
    protected static final String VALIDATION_MANAGER =
    Constants.XERCES_PROPERTY_PREFIX + Constants.VALIDATION_MANAGER_PROPERTY;
    
    /** property identifier: buffer size. */
    protected static final String BUFFER_SIZE =
    Constants.XERCES_PROPERTY_PREFIX + Constants.BUFFER_SIZE_PROPERTY;
    
    // recognized features and properties
    
    /** Recognized features. */
    private static final String[] RECOGNIZED_FEATURES = {
        VALIDATION,
        EXTERNAL_GENERAL_ENTITIES,
        EXTERNAL_PARAMETER_ENTITIES,
        ALLOW_JAVA_ENCODINGS,
        WARN_ON_DUPLICATE_ENTITYDEF
    };
    
    /** Feature defaults. */
    private static final Boolean[] FEATURE_DEFAULTS = {
        null,
        Boolean.TRUE,
        Boolean.TRUE,
        Boolean.FALSE,
        Boolean.FALSE,
    };
    
    /** Recognized properties. */
    private static final String[] RECOGNIZED_PROPERTIES = {
        SYMBOL_TABLE,
        ERROR_REPORTER,
        ENTITY_RESOLVER,
        VALIDATION_MANAGER,
        BUFFER_SIZE
    };
    
    /** Property defaults. */
    private static final Object[] PROPERTY_DEFAULTS = {
        null,
        null,
        null,
        null,
        new Integer(DEFAULT_BUFFER_SIZE),
    };
    
    private static final String XMLEntity = "[xml]".intern();
    private static final String DTDEntity = "[dtd]".intern();
    
    // debugging
    
    /**
     * Debug printing of buffer. This debugging flag works best when you
     * resize the DEFAULT_BUFFER_SIZE down to something reasonable like
     * 64 characters.
     */
    private static final boolean DEBUG_BUFFER = false;
    
    /** Debug some basic entities. */
    private static final boolean DEBUG_ENTITIES = false;
    
    /** Debug switching readers for encodings. */
    private static final boolean DEBUG_ENCODINGS = false;
    
    // should be diplayed trace resolving messages
    private static final boolean DEBUG_RESOLVER = false ;
    
    //
    // Data
    //
    
    // features
    
    /**
     * Validation. This feature identifier is:
     * http://xml.org/sax/features/validation
     */
    protected boolean fValidation;
    
    /**
     * External general entities. This feature identifier is:
     * http://xml.org/sax/features/external-general-entities
     */
    protected boolean fExternalGeneralEntities;
    
    /**
     * External parameter entities. This feature identifier is:
     * http://xml.org/sax/features/external-parameter-entities
     */
    protected boolean fExternalParameterEntities;
    
    /**
     * Allow Java encoding names. This feature identifier is:
     * http://apache.org/xml/features/allow-java-encodings
     */
    protected boolean fAllowJavaEncodings = true ;
    
    
    // properties
    
    /**
     * Symbol table. This property identifier is:
     * http://apache.org/xml/properties/internal/symbol-table
     */
    protected SymbolTable fSymbolTable;
    
    /**
     * Error reporter. This property identifier is:
     * http://apache.org/xml/properties/internal/error-reporter
     */
    protected XMLErrorReporter fErrorReporter;
    
    /**
     * Entity resolver. This property identifier is:
     * http://apache.org/xml/properties/internal/entity-resolver
     */
    protected XMLEntityResolver fEntityResolver;
    protected StaxEntityResolverWrapper fStaxEntityResolver;
    protected PropertyManager fPropertyManager ;
    
    // settings
    
    /**
     * Buffer size. We get this value from a property. The default size
     * is used if the input buffer size property is not specified.
     * REVISIT: do we need a property for internal entity buffer size?
     */
    protected int fBufferSize = DEFAULT_BUFFER_SIZE;
    
    /**
     * True if the document entity is standalone. This should really
     * only be set by the document source (e.g. XMLDocumentScanner).
     */
    protected boolean fStandalone;
    
    // are the entities being parsed in the external subset?
    // NOTE:  this *is not* the same as whether they're external entities!
    protected boolean fInExternalSubset = false;
    
    // handlers
    
    /** Entity handler. */
    protected XMLEntityHandler fEntityHandler;
    
    protected XMLEntityReaderImpl fEntityReader ;
    
    // entities
    
    /** Entities. */
    protected Hashtable fEntities = new Hashtable();
    
    /** Entity stack. */
    protected Stack fEntityStack = new Stack();
    
    /** Current entity. */
    protected Entity.ScannedEntity fCurrentEntity = null;
    
    // shared context
    
    /** Shared declared entities.
     * XXX understand it more deeply, why are we doing this ?? Is it really required ?
     */
    protected Hashtable fDeclaredEntities;
    
    protected XMLEntityStorage fEntityStorage ;
    
    protected final Object [] defaultEncoding = new Object[]{"UTF-8", null};
    
    
    // temp vars
    
    /** Resource identifer. */
    private final XMLResourceIdentifierImpl fResourceIdentifier = new XMLResourceIdentifierImpl();
    
    //
    // Constructors
    //
    
    /**
     * If this constructor is used to create the object, reset() should be invoked on this object
     */
    public XMLEntityManager() {
        //pass a reference to current entity being scanned
        //fEntityStorage = new XMLEntityStorage(fCurrentEntity) ;
        fEntityStorage = new XMLEntityStorage(this) ;
        fEntityReader = new XMLEntityReaderImpl(this) ;
    } // <init>()
    
    /** Default constructor. */
    public XMLEntityManager(PropertyManager propertyManager) {
        fPropertyManager = propertyManager ;
        //pass a reference to current entity being scanned
        //fEntityStorage = new XMLEntityStorage(fCurrentEntity) ;
        fEntityStorage = new XMLEntityStorage(this) ;
        fEntityReader = new XMLEntityReaderImpl(propertyManager, this) ;
        reset(propertyManager);
    } // <init>()
    
    
    /** get the entity storage object from entity manager */
    public XMLEntityStorage getEntityStore(){
        return fEntityStorage ;
    }
    
    /** return the entity responsible for reading the entity */
    public XMLEntityReader getEntityReader(){
        return fEntityReader ;
    }
    
    
    //
    // Public methods
    //
    
    /**
     * Sets whether the document entity is standalone.
     *
     * @param standalone True if document entity is standalone.
     */
    public void setStandalone(boolean standalone) {
        fStandalone = standalone;
    }
    // setStandalone(boolean)
    
    /** Returns true if the document entity is standalone. */
    public boolean isStandalone() {
        return fStandalone;
    }  //isStandalone():boolean
    
    
    /**
     * Sets the entity handler. When an entity starts and ends, the
     * entity handler is notified of the change.
     *
     * @param entityHandler The new entity handler.
     */
    public void setEntityHandler(XMLEntityHandler entityHandler) {
        fEntityHandler = entityHandler;
    } // setEntityHandler(XMLEntityHandler)
    
    //this function returns StaxXMLInputSource
    public StaxXMLInputSource resolveEntityAsPerStax(XMLResourceIdentifier resourceIdentifier) throws java.io.IOException{
        
        if(resourceIdentifier == null ) return null;
        
        String publicId = resourceIdentifier.getPublicId();
        String literalSystemId = resourceIdentifier.getLiteralSystemId();
        String baseSystemId = resourceIdentifier.getBaseSystemId();
        String expandedSystemId = resourceIdentifier.getExpandedSystemId();
        // if no base systemId given, assume that it's relative
        // to the systemId of the current scanned entity
        // Sometimes the system id is not (properly) expanded.
        // We need to expand the system id if:
        // a. the expanded one was null; or
        // b. the base system id was null, but becomes non-null from the current entity.
        boolean needExpand = (expandedSystemId == null);
        // REVISIT:  why would the baseSystemId ever be null?  if we
        // didn't have to make this check we wouldn't have to reuse the
        // fXMLResourceIdentifier object...
        if (baseSystemId == null && fCurrentEntity != null && fCurrentEntity.entityLocation != null) {
            baseSystemId = fCurrentEntity.entityLocation.getExpandedSystemId();
            if (baseSystemId != null)
                needExpand = true;
        }
        if (needExpand)
            expandedSystemId = expandSystemId(literalSystemId, baseSystemId);
        
        // give the entity resolver a chance
        StaxXMLInputSource xmlInputSource = null;
        if (fStaxEntityResolver != null) {
            if(DEBUG_RESOLVER){
                System.out.println("fStaxEntityResolver != null") ;
            }
            XMLResourceIdentifierImpl ri = null;
            if (resourceIdentifier instanceof XMLResourceIdentifierImpl) {
                ri = (XMLResourceIdentifierImpl)resourceIdentifier;
            }
            else {
                fResourceIdentifier.clear();
                ri = fResourceIdentifier;
            }
            ri.setValues(publicId, literalSystemId, baseSystemId, expandedSystemId);
            if(DEBUG_RESOLVER){
                System.out.println("BEFORE Calling resolveEntity") ;
            }
            xmlInputSource = fStaxEntityResolver.resolveEntity(ri);
        }
        
        // do default resolution
        if (xmlInputSource == null) {
            // REVISIT: when systemId is null, I think we should return null.
            //          is this the right solution? -SG
            //if (systemId != null)
            xmlInputSource = new StaxXMLInputSource(new XMLInputSource(publicId, literalSystemId, baseSystemId));
        }else if(xmlInputSource.hasXMLStreamOrXMLEventReader()){
            //Waiting for the clarification from EG. - nb
        }
        
        if (DEBUG_RESOLVER) {
            System.err.println("XMLEntityManager.resolveEntity(" + publicId + ")");
            System.err.println(" = " + xmlInputSource);
        }
        
        return xmlInputSource;
        
    }
    
    /**
     * Resolves the specified public and system identifiers. This
     * method first attempts to resolve the entity based on the
     * EntityResolver registered by the application. If no entity
     * resolver is registered or if the registered entity handler
     * is unable to resolve the entity, then default entity
     * resolution will occur.
     *
     * @param publicId     The public identifier of the entity.
     * @param systemId     The system identifier of the entity.
     * @param baseSystemId The base system identifier of the entity.
     *                     This is the system identifier of the current
     *                     entity and is used to expand the system
     *                     identifier when the system identifier is a
     *                     relative URI.
     *
     * @return Returns an input source that wraps the resolved entity.
     *         This method will never return null.
     *
     * @throws IOException  Thrown on i/o error.
     * @throws XNIException Thrown by entity resolver to signal an error.
     */
    public XMLInputSource resolveEntity(XMLResourceIdentifier resourceIdentifier) throws IOException, XNIException {
        if(resourceIdentifier == null ) return null;
        String publicId = resourceIdentifier.getPublicId();
        String literalSystemId = resourceIdentifier.getLiteralSystemId();
        String baseSystemId = resourceIdentifier.getBaseSystemId();
        String expandedSystemId = resourceIdentifier.getExpandedSystemId();
        // if no base systemId given, assume that it's relative
        // to the systemId of the current scanned entity
        // Sometimes the system id is not (properly) expanded.
        // We need to expand the system id if:
        // a. the expanded one was null; or
        // b. the base system id was null, but becomes non-null from the current entity.
        boolean needExpand = (expandedSystemId == null);
        // REVISIT:  why would the baseSystemId ever be null?  if we
        // didn't have to make this check we wouldn't have to reuse the
        // fXMLResourceIdentifier object...
        if (baseSystemId == null && fCurrentEntity != null && fCurrentEntity.entityLocation != null) {
            baseSystemId = fCurrentEntity.entityLocation.getExpandedSystemId();
            if (baseSystemId != null)
                needExpand = true;
        }
        if (needExpand)
            expandedSystemId = expandSystemId(literalSystemId, baseSystemId);
        
        // give the entity resolver a chance
        XMLInputSource xmlInputSource = null;
        if (fEntityResolver != null) {
            XMLResourceIdentifierImpl ri = null;
            if (resourceIdentifier instanceof XMLResourceIdentifierImpl) {
                ri = (XMLResourceIdentifierImpl)resourceIdentifier;
            }
            else {
                fResourceIdentifier.clear();
                ri = fResourceIdentifier;
            }
            ri.setValues(publicId, literalSystemId, baseSystemId, expandedSystemId);
            xmlInputSource = fEntityResolver.resolveEntity(ri);
        }
        
        // do default resolution
        // REVISIT: what's the correct behavior if the user provided an entity
        // resolver (fEntityResolver != null), but resolveEntity doesn't return
        // an input source (xmlInputSource == null)?
        // do we do default resolution, or do we just return null? -SG
        if (xmlInputSource == null) {
            // REVISIT: when systemId is null, I think we should return null.
            //          is this the right solution? -SG
            //if (systemId != null)
            xmlInputSource = new XMLInputSource(publicId, literalSystemId, baseSystemId);
        }
        
        if (DEBUG_RESOLVER) {
            System.err.println("XMLEntityManager.resolveEntity(" + publicId + ")");
            System.err.println(" = " + xmlInputSource);
        }
        
        return xmlInputSource;
        
    } // resolveEntity(XMLResourceIdentifier):XMLInputSource
    
    /**
     * Starts a named entity.
     *
     * @param entityName The name of the entity to start.
     * @param literal    True if this entity is started within a literal
     *                   value.
     *
     * @throws IOException  Thrown on i/o error.
     * @throws XNIException Thrown by entity handler to signal an error.
     */
    public void startEntity(String entityName, boolean literal)
    throws IOException, XNIException {
        
        // was entity declared?
        Entity entity = (Entity)fEntityStorage.getDeclaredEntities().get(entityName);
        if (entity == null) {
            if (fEntityHandler != null) {
                String encoding = null;
                fResourceIdentifier.clear();
                fEntityHandler.startEntity(entityName, fResourceIdentifier, encoding);
                fEntityHandler.endEntity(entityName);
            }
            return;
        }
        
        // should we skip external entities?
        boolean external = entity.isExternal();
        if (external) {
            boolean unparsed = entity.isUnparsed();
            boolean parameter = entityName.startsWith("%");
            boolean general = !parameter;
            if (unparsed || (general && !fExternalGeneralEntities) ||
            (parameter && !fExternalParameterEntities)) {
                
                if (fEntityHandler != null) {
                    fResourceIdentifier.clear();
                    final String encoding = null;
                    Entity.ExternalEntity externalEntity = (Entity.ExternalEntity)entity;
                    //REVISIT:  since we're storing expandedSystemId in the
                    // externalEntity, how could this have got here if it wasn't already
                    // expanded??? - neilg
                    String extLitSysId = (externalEntity.entityLocation != null ? externalEntity.entityLocation.getLiteralSystemId() : null);
                    String extBaseSysId = (externalEntity.entityLocation != null ? externalEntity.entityLocation.getBaseSystemId() : null);
                    String expandedSystemId = expandSystemId(extLitSysId, extBaseSysId);
                    fResourceIdentifier.setValues(
                    (externalEntity.entityLocation != null ? externalEntity.entityLocation.getPublicId() : null),
                    extLitSysId, extBaseSysId, expandedSystemId);
                    fEntityHandler.startEntity(entityName, fResourceIdentifier, encoding);
                    fEntityHandler.endEntity(entityName);
                }
                return;
            }
        }
        
        // is entity recursive?
        int size = fEntityStack.size();
        for (int i = size; i >= 0; i--) {
            Entity activeEntity = i == size
            ? fCurrentEntity
            : (Entity)fEntityStack.elementAt(i);
            if (activeEntity.name == entityName) {
                String path = entityName;
                for (int j = i + 1; j < size; j++) {
                    activeEntity = (Entity)fEntityStack.elementAt(j);
                    path = path + " -> " + activeEntity.name;
                }
                path = path + " -> " + fCurrentEntity.name;
                path = path + " -> " + entityName;
                fErrorReporter.reportError(this.getEntityReader(),XMLMessageFormatter.XML_DOMAIN,
                "RecursiveReference",
                new Object[] { entityName, path },
                XMLErrorReporter.SEVERITY_FATAL_ERROR);
                
                if (fEntityHandler != null) {
                    fResourceIdentifier.clear();
                    final String encoding = null;
                    if (external) {
                        Entity.ExternalEntity externalEntity = (Entity.ExternalEntity)entity;
                        // REVISIT:  for the same reason above...
                        String extLitSysId = (externalEntity.entityLocation != null ? externalEntity.entityLocation.getLiteralSystemId() : null);
                        String extBaseSysId = (externalEntity.entityLocation != null ? externalEntity.entityLocation.getBaseSystemId() : null);
                        String expandedSystemId = expandSystemId(extLitSysId, extBaseSysId);
                        fResourceIdentifier.setValues(
                        (externalEntity.entityLocation != null ? externalEntity.entityLocation.getPublicId() : null),
                        extLitSysId, extBaseSysId, expandedSystemId);
                    }
                    fEntityHandler.startEntity(entityName, fResourceIdentifier, encoding);
                    //xxx
                    fEntityHandler.endEntity(entityName);
                }
                
                return;
            }
        }
        
        // resolve external entity
        StaxXMLInputSource staxInputSource = null;
        XMLInputSource xmlInputSource = null ;
        
        if (external) {
            Entity.ExternalEntity externalEntity = (Entity.ExternalEntity)entity;
            staxInputSource = resolveEntityAsPerStax(externalEntity.entityLocation);
            /** xxx:  Waiting from the EG
             * //simply return if there was entity resolver registered and application
             * //returns either XMLStreamReader or XMLEventReader.
             * if(staxInputSource.hasXMLStreamOrXMLEventReader()) return ;
             */
            xmlInputSource = staxInputSource.getXMLInputSource() ;
        }
        // wrap internal entity
        else {
            Entity.InternalEntity internalEntity = (Entity.InternalEntity)entity;
            Reader reader = new StringReader(internalEntity.text);
            xmlInputSource = new XMLInputSource(null, null, null, reader, null);
        }
        
        // start the entity
        startEntity(entityName, xmlInputSource, literal, external);
        
    } // startEntity(String,boolean)
    
    /**
     * Starts the document entity. The document entity has the "[xml]"
     * pseudo-name.
     *
     * @param xmlInputSource The input source of the document entity.
     *
     * @throws IOException  Thrown on i/o error.
     * @throws XNIException Thrown by entity handler to signal an error.
     */
    public void startDocumentEntity(XMLInputSource xmlInputSource)
    throws IOException, XNIException {
        startEntity(XMLEntity, xmlInputSource, false, true);
    } // startDocumentEntity(XMLInputSource)
    
    //xxx these methods are not required.
    /**
     * Starts the DTD entity. The DTD entity has the "[dtd]"
     * pseudo-name.
     *
     * @param xmlInputSource The input source of the DTD entity.
     *
     * @throws IOException  Thrown on i/o error.
     * @throws XNIException Thrown by entity handler to signal an error.
     */
    public void startDTDEntity(XMLInputSource xmlInputSource)
    throws IOException, XNIException {
        startEntity(DTDEntity, xmlInputSource, false, true);
    } // startDTDEntity(XMLInputSource)
    
    // indicate start of external subset so that
    // location of entity decls can be tracked
    public void startExternalSubset() {
        fInExternalSubset = true;
    }
    
    public void endExternalSubset() {
        fInExternalSubset = false;
    }
    
    /**
     * Starts an entity.
     * <p>
     * This method can be used to insert an application defined XML
     * entity stream into the parsing stream.
     *
     * @param name           The name of the entity.
     * @param xmlInputSource The input source of the entity.
     * @param literal        True if this entity is started within a
     *                       literal value.
     * @param isExternal    whether this entity should be treated as an internal or external entity.
     *
     * @throws IOException  Thrown on i/o error.
     * @throws XNIException Thrown by entity handler to signal an error.
     */
    public void startEntity(String name,
    XMLInputSource xmlInputSource,
    boolean literal, boolean isExternal)
    throws IOException, XNIException {
        
        final String publicId = xmlInputSource.getPublicId();
        final String literalSystemId = xmlInputSource.getSystemId();
        String baseSystemId = xmlInputSource.getBaseSystemId();
        String encoding = xmlInputSource.getEncoding();
        Boolean isBigEndian = null;
        
        // create reader
        InputStream stream = null;
        Reader reader = xmlInputSource.getCharacterStream();
        String expandedSystemId = expandSystemId(literalSystemId, baseSystemId);
        if (baseSystemId == null) {
            baseSystemId = expandedSystemId;
        }
        if (reader == null) {
            stream = xmlInputSource.getByteStream();
            if (stream == null) {
                //create the buffered inputstream.
                stream = new BufferedInputStream(new URL(expandedSystemId).openStream());
            }
            // wrap this stream in RewindableInputStream
            stream = new RewindableInputStream(stream);
            
            // perform auto-detect of encoding if necessary
            if (encoding == null) {
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
                    reader = createReader(stream, encoding, isBigEndian);
                }
                else {
                    reader = createReader(stream, encoding, isBigEndian);
                }
            }
            
            // use specified encoding
            else {
                reader = createReader(stream, encoding, isBigEndian);
            }
            
            // read one character at a time so we don't jump too far
            // ahead, converting characters from the byte stream in
            // the wrong encoding
            if (DEBUG_ENCODINGS) {
                System.out.println("$$$ no longer wrapping reader in OneCharReader");
            }
            //reader = new OneCharReader(reader);
        }
        
        // we've seen a new Reader. put it in a list, so that
        // we can close it later.
        //fOwnReaders.addElement(reader);
        
        // push entity on stack  before we open new entity
        if(fCurrentEntity != null){
            fEntityStack.push(fCurrentEntity);
        }
        
        //System.out.println("Setting the fCurrentEntity, entity name = " + name );
        // create entity
        fCurrentEntity = new Entity.ScannedEntity(name,
        new XMLResourceIdentifierImpl(publicId, literalSystemId, baseSystemId, expandedSystemId),
        stream, reader, encoding, literal, false, isExternal);
        
        //xxx provide the current entity to the reader, is this right way to do ??
        fEntityReader.setCurrentEntity(fCurrentEntity) ;
        //System.out.println(" fCurrentEntity set = " + fCurrentEntity );
        
        
        fResourceIdentifier.setValues(publicId, literalSystemId, baseSystemId, expandedSystemId);
        // call handler
        if (fEntityHandler != null) {
            fEntityHandler.startEntity(name, fResourceIdentifier, encoding);
        }
        
        
    } // startEntity(String,XMLInputSource)
    
    /**
     * Return the current entity being scanned. Current entity is SET using startEntity function.
     * @return Entity.ScannedEntity
     */
    
    public Entity.ScannedEntity getCurrentEntity(){
        return fCurrentEntity ;
    }
    
    // a list of Readers ever seen
    protected Vector fOwnReaders = new Vector();
    
    /**
     * Close all opened InputStreams and Readers opened by this parser.
     */
    public void closeReaders() {
        // close all readers
        for (int i = fOwnReaders.size()-1; i >= 0; i--) {
            try {
                ((Reader)fOwnReaders.elementAt(i)).close();
            } catch (IOException e) {
                // ignore
            }
        }
        // and clear the list
        fOwnReaders.removeAllElements();
    }
    
    public void endEntity() throws IOException, XNIException {
        
        // call handler
        if (DEBUG_BUFFER) {
            System.out.print("(endEntity: ");
            print();
            System.out.println();
        }
        //System.out.println("Closing the Entity = " + fCurrentEntity.name);
        
        if (fEntityHandler != null) {
            fEntityHandler.endEntity(fCurrentEntity.name);
        }
        
        //close the reader
        if(fCurrentEntity != null){
            //close the reader
            try{
                fCurrentEntity.close();
            }catch(IOException ex){
                throw new XNIException(ex);
            }
        }
        // pop stack
        // REVISIT: we are done with the current entity, should close
        //          the associated reader
        //fCurrentEntity.reader.close();
        // Now we close all readers after we finish parsing
        
        //fCurrentEntity = fEntityStack.size() > 0 ? (Entity.ScannedEntity)fEntityStack.pop() : null;
        //close the reader
        
        fCurrentEntity = fEntityStack.size() > 0 ? (Entity.ScannedEntity)fEntityStack.pop() : null;
        
        fEntityReader.setCurrentEntity(fCurrentEntity);
        
        //check if there are any entity left in the stack -- if there are
        //no entries EOF has been reached.
        //REVISIT: throwing an exception is very unclean -- and takes
        //lot of time too
        /**
         * if(fCurrentEntity == null ){
         * throw new EOFException() ;
         * }
         */
        if (DEBUG_BUFFER) {
            System.out.print(")endEntity: ");
            print();
            System.out.println();
        }
        
    } // endEntity()
    
    
    //
    // XMLComponent methods
    //
    public void reset(PropertyManager propertyManager){
        //reset fEntityStorage
        fEntityStorage.reset(propertyManager);
        //reset XMLEntityReaderImpl
        fEntityReader.reset(propertyManager);
        // xerces properties
        fSymbolTable = (SymbolTable)propertyManager.getProperty(Constants.XERCES_PROPERTY_PREFIX + Constants.SYMBOL_TABLE_PROPERTY);
        fErrorReporter = (XMLErrorReporter)propertyManager.getProperty(Constants.XERCES_PROPERTY_PREFIX + Constants.ERROR_REPORTER_PROPERTY);
        try {
            fStaxEntityResolver = (StaxEntityResolverWrapper)propertyManager.getProperty(STAX_ENTITY_RESOLVER);
        }
        catch (XMLConfigurationException e) {
            fStaxEntityResolver = null;
        }
        
        // initialize state
        //fStandalone = false;
        fEntities.clear();
        fEntityStack.removeAllElements();
        fCurrentEntity = null;
        fValidation = false;
        fExternalGeneralEntities = true;
        fExternalParameterEntities = true;
        fAllowJavaEncodings = true ;
        
        //test();
    }
    /**
     * public void reset(){
     * fEntities.clear();
     * fEntityStack.removeAllElements();
     * fCurrentEntity = null;
     * //test();
     * }
     */
    /**
     * Resets the component. The component can query the component manager
     * about any features and properties that affect the operation of the
     * component.
     *
     * @param componentManager The component manager.
     *
     * @throws SAXException Thrown by component on initialization error.
     *                      For example, if a feature or property is
     *                      required for the operation of the component, the
     *                      component manager may throw a
     *                      SAXNotRecognizedException or a
     *                      SAXNotSupportedException.
     */
    public void reset(XMLComponentManager componentManager)
    throws XMLConfigurationException {
        
        // sax features
        try {
            fValidation = componentManager.getFeature(VALIDATION);
        }
        catch (XMLConfigurationException e) {
            fValidation = false;
        }
        try {
            fExternalGeneralEntities = componentManager.getFeature(EXTERNAL_GENERAL_ENTITIES);
        }
        catch (XMLConfigurationException e) {
            fExternalGeneralEntities = true;
        }
        try {
            fExternalParameterEntities = componentManager.getFeature(EXTERNAL_PARAMETER_ENTITIES);
        }
        catch (XMLConfigurationException e) {
            fExternalParameterEntities = true;
        }
        
        // xerces features
        try {
            fAllowJavaEncodings = componentManager.getFeature(ALLOW_JAVA_ENCODINGS);
        }
        catch (XMLConfigurationException e) {
            fAllowJavaEncodings = false;
        }
        
        
        // xerces properties
        fSymbolTable = (SymbolTable)componentManager.getProperty(SYMBOL_TABLE);
        fErrorReporter = (XMLErrorReporter)componentManager.getProperty(ERROR_REPORTER);
        try {
            fEntityResolver = (XMLEntityResolver)componentManager.getProperty(ENTITY_RESOLVER);
        }
        catch (XMLConfigurationException e) {
            fEntityResolver = null;
        }
        
        try {
            fStaxEntityResolver = (StaxEntityResolverWrapper)componentManager.getProperty(STAX_ENTITY_RESOLVER);
        }
        catch (XMLConfigurationException e) {
            fStaxEntityResolver = null;
        }
        
        // initialize state
        fStandalone = false;
        fEntities.clear();
        fEntityStack.removeAllElements();
        
        fCurrentEntity = null;
        /**
         * // DEBUG
         * if (DEBUG_ENTITIES) {
         * addInternalEntity("text", "Hello World.");
         * addInternalEntity("empty-element", "<foo/>");
         * addInternalEntity("balanced-element", "<foo></foo>");
         * addInternalEntity("balanced-element-with-text", "<foo>Hello, World</foo>");
         * addInternalEntity("balanced-element-with-entity", "<foo>&text;</foo>");
         * addInternalEntity("unbalanced-entity", "<foo>");
         * addInternalEntity("recursive-entity", "<foo>&recursive-entity2;</foo>");
         * addInternalEntity("recursive-entity2", "<bar>&recursive-entity3;</bar>");
         * addInternalEntity("recursive-entity3", "<baz>&recursive-entity;</baz>");
         *
         * addExternalEntity("external-text", null, "external-text.ent", "test/external-text.xml");
         * addExternalEntity("external-balanced-element", null, "external-balanced-element.ent", "test/external-balanced-element.xml");
         * addExternalEntity("one", null, "ent/one.ent", "test/external-entity.xml");
         * addExternalEntity("two", null, "ent/two.ent", "test/ent/one.xml");
         * }
         */
        // copy declared entities
        //test();
        if (fDeclaredEntities != null) {
            java.util.Enumeration keys = fDeclaredEntities.keys();
            while (keys.hasMoreElements()) {
                Object key = keys.nextElement();
                Object value = fDeclaredEntities.get(key);
                fEntities.put(key, value);
            }
        }
        
    } // reset(XMLComponentManager)
    
    /**
     * Returns a list of feature identifiers that are recognized by
     * this component. This method may return null if no features
     * are recognized by this component.
     */
    public String[] getRecognizedFeatures() {
        return (String[])(RECOGNIZED_FEATURES.clone());
    } // getRecognizedFeatures():String[]
    
    /**
     * Sets the state of a feature. This method is called by the component
     * manager any time after reset when a feature changes state.
     * <p>
     * <strong>Note:</strong> Components should silently ignore features
     * that do not affect the operation of the component.
     *
     * @param featureId The feature identifier.
     * @param state     The state of the feature.
     *
     * @throws SAXNotRecognizedException The component should not throw
     *                                   this exception.
     * @throws SAXNotSupportedException The component should not throw
     *                                  this exception.
     */
    public void setFeature(String featureId, boolean state)
    throws XMLConfigurationException {
        
        // xerces features
        if (featureId.startsWith(Constants.XERCES_FEATURE_PREFIX)) {
            String feature = featureId.substring(Constants.XERCES_FEATURE_PREFIX.length());
            if (feature.equals(Constants.ALLOW_JAVA_ENCODINGS_FEATURE)) {
                fAllowJavaEncodings = state;
            }
        }
        
    } // setFeature(String,boolean)
    
    public void setProperty(String name, Object value){}
    /**
     * Returns a list of property identifiers that are recognized by
     * this component. This method may return null if no properties
     * are recognized by this component.
     */
    public String[] getRecognizedProperties() {
        return (String[])(RECOGNIZED_PROPERTIES.clone());
    } // getRecognizedProperties():String[]
    /**
     * Returns the default state for a feature, or null if this
     * component does not want to report a default value for this
     * feature.
     *
     * @param featureId The feature identifier.
     *
     * @since Xerces 2.2.0
     */
    public Boolean getFeatureDefault(String featureId) {
        for (int i = 0; i < RECOGNIZED_FEATURES.length; i++) {
            if (RECOGNIZED_FEATURES[i].equals(featureId)) {
                return FEATURE_DEFAULTS[i];
            }
        }
        return null;
    } // getFeatureDefault(String):Boolean
    
    /**
     * Returns the default state for a property, or null if this
     * component does not want to report a default value for this
     * property.
     *
     * @param propertyId The property identifier.
     *
     * @since Xerces 2.2.0
     */
    public Object getPropertyDefault(String propertyId) {
        for (int i = 0; i < RECOGNIZED_PROPERTIES.length; i++) {
            if (RECOGNIZED_PROPERTIES[i].equals(propertyId)) {
                return PROPERTY_DEFAULTS[i];
            }
        }
        return null;
    } // getPropertyDefault(String):Object
    
    //
    // Public static methods
    //
    
    /**
     * Expands a system id and returns the system id as a URI, if
     * it can be expanded. A return value of null means that the
     * identifier is already expanded. An exception thrown
     * indicates a failure to expand the id.
     *
     * @param systemId The systemId to be expanded.
     *
     * @return Returns the URI string representing the expanded system
     *         identifier. A null value indicates that the given
     *         system identifier is already expanded.
     *
     */
    public static String expandSystemId(String systemId) {
        return expandSystemId(systemId, null);
    } // expandSystemId(String):String
    
    // current value of the "user.dir" property
    private static String gUserDir;
    // escaped value of the current "user.dir" property
    private static String gEscapedUserDir;
    // which ASCII characters need to be escaped
    private static boolean gNeedEscaping[] = new boolean[128];
    // the first hex character if a character needs to be escaped
    private static char gAfterEscaping1[] = new char[128];
    // the second hex character if a character needs to be escaped
    private static char gAfterEscaping2[] = new char[128];
    private static char[] gHexChs = {'0', '1', '2', '3', '4', '5', '6', '7',
    '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    // initialize the above 3 arrays
    static {
        for (int i = 0; i <= 0x1f; i++) {
            gNeedEscaping[i] = true;
            gAfterEscaping1[i] = gHexChs[i >> 4];
            gAfterEscaping2[i] = gHexChs[i & 0xf];
        }
        gNeedEscaping[0x7f] = true;
        gAfterEscaping1[0x7f] = '7';
        gAfterEscaping2[0x7f] = 'F';
        char[] escChs = {' ', '<', '>', '#', '%', '"', '{', '}',
        '|', '\\', '^', '~', '[', ']', '`'};
        int len = escChs.length;
        char ch;
        for (int i = 0; i < len; i++) {
            ch = escChs[i];
            gNeedEscaping[ch] = true;
            gAfterEscaping1[ch] = gHexChs[ch >> 4];
            gAfterEscaping2[ch] = gHexChs[ch & 0xf];
        }
    }
    // To escape the "user.dir" system property, by using %HH to represent
    // special ASCII characters: 0x00~0x1F, 0x7F, ' ', '<', '>', '#', '%'
    // and '"'. It's a static method, so needs to be synchronized.
    // this method looks heavy, but since the system property isn't expected
    // to change often, so in most cases, we only need to return the string
    // that was escaped before.
    // According to the URI spec, non-ASCII characters (whose value >= 128)
    // need to be escaped too.
    // REVISIT: don't know how to escape non-ASCII characters, especially
    // which encoding to use. Leave them for now.
    private static synchronized String getUserDir() {
        // get the user.dir property
        String userDir = "";
        try {
            userDir = System.getProperty("user.dir");
        }
        catch (SecurityException se) {
        }
        
        // return empty string if property value is empty string.
        if (userDir.length() == 0)
            return "";
        
        // compute the new escaped value if the new property value doesn't
        // match the previous one
        if (userDir.equals(gUserDir)) {
            return gEscapedUserDir;
        }
        
        // record the new value as the global property value
        gUserDir = userDir;
        
        char separator = java.io.File.separatorChar;
        userDir = userDir.replace(separator, '/');
        
        int len = userDir.length(), ch;
        StringBuffer buffer = new StringBuffer(len*3);
        // change C:/blah to /C:/blah
        if (len >= 2 && userDir.charAt(1) == ':') {
            ch = Character.toUpperCase(userDir.charAt(0));
            if (ch >= 'A' && ch <= 'Z') {
                buffer.append('/');
            }
        }
        
        // for each character in the path
        int i = 0;
        for (; i < len; i++) {
            ch = userDir.charAt(i);
            // if it's not an ASCII character, break here, and use UTF-8 encoding
            if (ch >= 128)
                break;
            if (gNeedEscaping[ch]) {
                buffer.append('%');
                buffer.append(gAfterEscaping1[ch]);
                buffer.append(gAfterEscaping2[ch]);
                // record the fact that it's escaped
            }
            else {
                buffer.append((char)ch);
            }
        }
        
        // we saw some non-ascii character
        if (i < len) {
            // get UTF-8 bytes for the remaining sub-string
            byte[] bytes = null;
            byte b;
            try {
                bytes = userDir.substring(i).getBytes("UTF-8");
            } catch (java.io.UnsupportedEncodingException e) {
                // should never happen
                return userDir;
            }
            len = bytes.length;
            
            // for each byte
            for (i = 0; i < len; i++) {
                b = bytes[i];
                // for non-ascii character: make it positive, then escape
                if (b < 0) {
                    ch = b + 256;
                    buffer.append('%');
                    buffer.append(gHexChs[ch >> 4]);
                    buffer.append(gHexChs[ch & 0xf]);
                }
                else if (gNeedEscaping[b]) {
                    buffer.append('%');
                    buffer.append(gAfterEscaping1[b]);
                    buffer.append(gAfterEscaping2[b]);
                }
                else {
                    buffer.append((char)b);
                }
            }
        }
        
        // change blah/blah to blah/blah/
        if (!userDir.endsWith("/"))
            buffer.append('/');
        
        gEscapedUserDir = buffer.toString();
        
        return gEscapedUserDir;
    }
    
    /**
     * Expands a system id and returns the system id as a URI, if
     * it can be expanded. A return value of null means that the
     * identifier is already expanded. An exception thrown
     * indicates a failure to expand the id.
     *
     * @param systemId The systemId to be expanded.
     *
     * @return Returns the URI string representing the expanded system
     *         identifier. A null value indicates that the given
     *         system identifier is already expanded.
     *
     */
    public static String expandSystemId(String systemId, String baseSystemId) {
        
        // check for bad parameters id
        if (systemId == null || systemId.length() == 0) {
            return systemId;
        }
        // if id already expanded, return
        try {
            URI uri = new URI(systemId);
            if (uri != null) {
                return systemId;
            }
        }
        catch (URI.MalformedURIException e) {
            // continue on...
        }
        // normalize id
        String id = fixURI(systemId);
        
        // normalize base
        URI base = null;
        URI uri = null;
        try {
            if (baseSystemId == null || baseSystemId.length() == 0 ||
            baseSystemId.equals(systemId)) {
                String dir = getUserDir();
                base = new URI("file", "", dir, null, null);
            }
            else {
                try {
                    base = new URI(fixURI(baseSystemId));
                }
                catch (URI.MalformedURIException e) {
                    if (baseSystemId.indexOf(':') != -1) {
                        // for xml schemas we might have baseURI with
                        // a specified drive
                        base = new URI("file", "", fixURI(baseSystemId), null, null);
                    }
                    else {
                        String dir = getUserDir();
                        dir = dir + fixURI(baseSystemId);
                        base = new URI("file", "", dir, null, null);
                    }
                }
            }
            // expand id
            uri = new URI(base, id);
        }
        catch (Exception e) {
            // let it go through
            
        }
        
        if (uri == null) {
            return systemId;
        }
        return uri.toString();
        
    } // expandSystemId(String,String):String
    
    //
    // Protected methods
    //
    
    
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
            return defaultEncoding;
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
            return defaultEncoding;
        }
        
        // UTF-8 with a BOM
        int b2 = b4[2] & 0xFF;
        if (b0 == 0xEF && b1 == 0xBB && b2 == 0xBF) {
            return defaultEncoding;
        }
        
        // default to UTF-8 if we don't have enough bytes to make a
        // good determination of the encoding
        if (count < 4) {
            return defaultEncoding;
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
        
        return defaultEncoding;
        
    } // getEncodingName(byte[],int):Object[]
    
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
    protected Reader createReader(InputStream inputStream, String encoding, Boolean isBigEndian)
    throws IOException {
        
        // normalize encoding name
        if (encoding == null) {
            encoding = "UTF-8";
        }
        
        // try to use an optimized reader
        String ENCODING = encoding.toUpperCase(Locale.ENGLISH);
        if (ENCODING.equals("UTF-8")) {
            if (DEBUG_ENCODINGS) {
                System.out.println("$$$ creating UTF8Reader");
            }
            return new UTF8Reader(inputStream, fBufferSize, fErrorReporter.getMessageFormatter(XMLMessageFormatter.XML_DOMAIN), fErrorReporter.getLocale() );
        }
        if (ENCODING.equals("US-ASCII")) {
            if (DEBUG_ENCODINGS) {
                System.out.println("$$$ creating ASCIIReader");
            }
            return new ASCIIReader(inputStream, fBufferSize, fErrorReporter.getMessageFormatter(XMLMessageFormatter.XML_DOMAIN), fErrorReporter.getLocale());
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
                fErrorReporter.reportError(this.getEntityReader(),XMLMessageFormatter.XML_DOMAIN,
                "EncodingByteOrderUnsupported",
                new Object[] { encoding },
                XMLErrorReporter.SEVERITY_FATAL_ERROR);
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
                fErrorReporter.reportError(this.getEntityReader(),XMLMessageFormatter.XML_DOMAIN,
                "EncodingByteOrderUnsupported",
                new Object[] { encoding },
                XMLErrorReporter.SEVERITY_FATAL_ERROR);
            }
        }
        
        // check for valid name
        boolean validIANA = XMLChar.isValidIANAEncoding(encoding);
        boolean validJava = XMLChar.isValidJavaEncoding(encoding);
        if (!validIANA || (fAllowJavaEncodings && !validJava)) {
            fErrorReporter.reportError(this.getEntityReader(),XMLMessageFormatter.XML_DOMAIN,
            "EncodingDeclInvalid",
            new Object[] { encoding },
            XMLErrorReporter.SEVERITY_FATAL_ERROR);
            // NOTE: AndyH suggested that, on failure, we use ISO Latin 1
            //       because every byte is a valid ISO Latin 1 character.
            //       It may not translate correctly but if we failed on
            //       the encoding anyway, then we're expecting the content
            //       of the document to be bad. This will just prevent an
            //       invalid UTF-8 sequence to be detected. This is only
            //       important when continue-after-fatal-error is turned
            //       on. -Ac
            encoding = "ISO-8859-1";
        }
        
        // try to use a Java reader
        String javaEncoding = EncodingMap.getIANA2JavaMapping(ENCODING);
        if (javaEncoding == null) {
            if(fAllowJavaEncodings) {
                javaEncoding = encoding;
            } else {
                fErrorReporter.reportError(this.getEntityReader(),XMLMessageFormatter.XML_DOMAIN,
                "EncodingDeclInvalid",
                new Object[] { encoding },
                XMLErrorReporter.SEVERITY_FATAL_ERROR);
                // see comment above.
                javaEncoding = "ISO8859_1";
            }
        }
        if (DEBUG_ENCODINGS) {
            System.out.print("$$$ creating Java InputStreamReader: encoding="+javaEncoding);
            if (javaEncoding == encoding) {
                System.out.print(" (IANA encoding)");
            }
            System.out.println();
        }
        return new BufferedReader( new InputStreamReader(inputStream, javaEncoding));
        
    } // createReader(InputStream,String, Boolean): Reader
    
    
    /**
     * Return the public identifier for the current document event.
     * <p>
     * The return value is the public identifier of the document
     * entity or of the external parsed entity in which the markup
     * triggering the event appears.
     *
     * @return A string containing the public identifier, or
     *         null if none is available.
     */
    public String getPublicId() {
        return (fCurrentEntity != null && fCurrentEntity.entityLocation != null) ? fCurrentEntity.entityLocation.getPublicId() : null;
    } // getPublicId():String
    
    /**
     * Return the expanded system identifier for the current document event.
     * <p>
     * The return value is the expanded system identifier of the document
     * entity or of the external parsed entity in which the markup
     * triggering the event appears.
     * <p>
     * If the system identifier is a URL, the parser must resolve it
     * fully before passing it to the application.
     *
     * @return A string containing the expanded system identifier, or null
     *         if none is available.
     */
    public String getExpandedSystemId() {
        if (fCurrentEntity != null) {
            if (fCurrentEntity.entityLocation != null &&
            fCurrentEntity.entityLocation.getExpandedSystemId() != null ) {
                return fCurrentEntity.entityLocation.getExpandedSystemId();
            }
            else {
                // search for the first external entity on the stack
                int size = fEntityStack.size();
                for (int i = size - 1; i >= 0 ; i--) {
                    Entity.ScannedEntity externalEntity =
                    (Entity.ScannedEntity)fEntityStack.elementAt(i);
                    
                    if (externalEntity.entityLocation != null &&
                    externalEntity.entityLocation.getExpandedSystemId() != null) {
                        return externalEntity.entityLocation.getExpandedSystemId();
                    }
                }
            }
        }
        return null;
    } // getExpandedSystemId():String
    
    /**
     * Return the literal system identifier for the current document event.
     * <p>
     * The return value is the literal system identifier of the document
     * entity or of the external parsed entity in which the markup
     * triggering the event appears.
     * <p>
     * @return A string containing the literal system identifier, or null
     *         if none is available.
     */
    public String getLiteralSystemId() {
        if (fCurrentEntity != null) {
            if (fCurrentEntity.entityLocation != null &&
            fCurrentEntity.entityLocation.getLiteralSystemId() != null ) {
                return fCurrentEntity.entityLocation.getLiteralSystemId();
            }
            else {
                // search for the first external entity on the stack
                int size = fEntityStack.size();
                for (int i = size - 1; i >= 0 ; i--) {
                    Entity.ScannedEntity externalEntity =
                    (Entity.ScannedEntity)fEntityStack.elementAt(i);
                    
                    if (externalEntity.entityLocation != null &&
                    externalEntity.entityLocation.getLiteralSystemId() != null) {
                        return externalEntity.entityLocation.getLiteralSystemId();
                    }
                }
            }
        }
        return null;
    } // getLiteralSystemId():String
    
    /**
     * Return the line number where the current document event ends.
     * <p>
     * <strong>Warning:</strong> The return value from the method
     * is intended only as an approximation for the sake of error
     * reporting; it is not intended to provide sufficient information
     * to edit the character content of the original XML document.
     * <p>
     * The return value is an approximation of the line number
     * in the document entity or external parsed entity where the
     * markup triggering the event appears.
     * <p>
     * If possible, the SAX driver should provide the line position
     * of the first character after the text associated with the document
     * event.  The first line in the document is line 1.
     *
     * @return The line number, or -1 if none is available.
     */
    public int getLineNumber() {
        if (fCurrentEntity != null) {
            if (fCurrentEntity.isExternal()) {
                return fCurrentEntity.lineNumber;
            }
            else {
                // search for the first external entity on the stack
                int size = fEntityStack.size();
                for (int i=size-1; i>0 ; i--) {
                    Entity.ScannedEntity firstExternalEntity = (Entity.ScannedEntity)fEntityStack.elementAt(i);
                    if (firstExternalEntity.isExternal()) {
                        return firstExternalEntity.lineNumber;
                    }
                }
            }
        }
        
        return -1;
        
    } // getLineNumber():int
    
    /**
     * Return the column number where the current document event ends.
     * <p>
     * <strong>Warning:</strong> The return value from the method
     * is intended only as an approximation for the sake of error
     * reporting; it is not intended to provide sufficient information
     * to edit the character content of the original XML document.
     * <p>
     * The return value is an approximation of the column number
     * in the document entity or external parsed entity where the
     * markup triggering the event appears.
     * <p>
     * If possible, the SAX driver should provide the line position
     * of the first character after the text associated with the document
     * event.
     * <p>
     * If possible, the SAX driver should provide the line position
     * of the first character after the text associated with the document
     * event.  The first column in each line is column 1.
     *
     * @return The column number, or -1 if none is available.
     */
    public int getColumnNumber() {
        if (fCurrentEntity != null) {
            if (fCurrentEntity.isExternal()) {
                return fCurrentEntity.columnNumber;
            }
            else {
                // search for the first external entity on the stack
                int size = fEntityStack.size();
                for (int i=size-1; i>0 ; i--) {
                    Entity.ScannedEntity firstExternalEntity = (Entity.ScannedEntity)fEntityStack.elementAt(i);
                    if (firstExternalEntity.isExternal()) {
                        return firstExternalEntity.columnNumber;
                    }
                }
            }
        }
        
        return -1;
    } // getColumnNumber():int
    
    
    //
    // Protected static methods
    //
    
    /**
     * Fixes a platform dependent filename to standard URI form.
     *
     * @param str The string to fix.
     *
     * @return Returns the fixed URI string.
     */
    protected static String fixURI(String str) {
        
        // handle platform dependent strings
        str = str.replace(java.io.File.separatorChar, '/');
        
        // Windows fix
        if (str.length() >= 2) {
            char ch1 = str.charAt(1);
            // change "C:blah" to "/C:blah"
            if (ch1 == ':') {
                char ch0 = Character.toUpperCase(str.charAt(0));
                if (ch0 >= 'A' && ch0 <= 'Z') {
                    str = "/" + str;
                }
            }
            // change "//blah" to "file://blah"
            else if (ch1 == '/' && str.charAt(0) == '/') {
                str = "file:" + str;
            }
        }
        
        // done
        return str;
        
    } // fixURI(String):String
    
    //
    // Package visible methods
    //
    /** Prints the contents of the buffer. */
    final void print() {
        if (DEBUG_BUFFER) {
            if (fCurrentEntity != null) {
                System.out.print('[');
                System.out.print(fCurrentEntity.count);
                System.out.print(' ');
                System.out.print(fCurrentEntity.position);
                if (fCurrentEntity.count > 0) {
                    System.out.print(" \"");
                    for (int i = 0; i < fCurrentEntity.count; i++) {
                        if (i == fCurrentEntity.position) {
                            System.out.print('^');
                        }
                        char c = fCurrentEntity.ch[i];
                        switch (c) {
                            case '\n': {
                                System.out.print("\\n");
                                break;
                            }
                            case '\r': {
                                System.out.print("\\r");
                                break;
                            }
                            case '\t': {
                                System.out.print("\\t");
                                break;
                            }
                            case '\\': {
                                System.out.print("\\\\");
                                break;
                            }
                            default: {
                                System.out.print(c);
                            }
                        }
                    }
                    if (fCurrentEntity.position == fCurrentEntity.count) {
                        System.out.print('^');
                    }
                    System.out.print('"');
                }
                System.out.print(']');
                System.out.print(" @ ");
                System.out.print(fCurrentEntity.lineNumber);
                System.out.print(',');
                System.out.print(fCurrentEntity.columnNumber);
            }
            else {
                System.out.print("*NO CURRENT ENTITY*");
            }
        }
    } // print()
    
    
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
                return fCurrentEntity.mayReadChunks ? fInputStream.available()
                : 0;
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
    
    public void test(){
        //System.out.println("TESTING: Added familytree to entityManager");
        //Usecase1
        fEntityStorage.addExternalEntity("entityUsecase1",null,
        "/space/home/stax/sun/6thJan2004/zephyr/data/test.txt",
        "/space/home/stax/sun/6thJan2004/zephyr/data/entity.xml");
        
        //Usecase2
        fEntityStorage.addInternalEntity("entityUsecase2","<Test>value</Test>");
        fEntityStorage.addInternalEntity("entityUsecase3","value3");
        fEntityStorage.addInternalEntity("text", "Hello World.");
        fEntityStorage.addInternalEntity("empty-element", "<foo/>");
        fEntityStorage.addInternalEntity("balanced-element", "<foo></foo>");
        fEntityStorage.addInternalEntity("balanced-element-with-text", "<foo>Hello, World</foo>");
        fEntityStorage.addInternalEntity("balanced-element-with-entity", "<foo>&text;</foo>");
        fEntityStorage.addInternalEntity("unbalanced-entity", "<foo>");
        fEntityStorage.addInternalEntity("recursive-entity", "<foo>&recursive-entity2;</foo>");
        fEntityStorage.addInternalEntity("recursive-entity2", "<bar>&recursive-entity3;</bar>");
        fEntityStorage.addInternalEntity("recursive-entity3", "<baz>&recursive-entity;</baz>");
        fEntityStorage.addInternalEntity("ch","&#x00A9;");
        fEntityStorage.addInternalEntity("ch1","&#84;");
        fEntityStorage.addInternalEntity("% ch2","param");
    }
    
} // class XMLEntityManager
