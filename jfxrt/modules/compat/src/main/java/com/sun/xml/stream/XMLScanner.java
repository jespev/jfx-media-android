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
 * $Id: XMLScanner.java,v 1.7 2010-11-02 21:02:26 joehw Exp $
 */
package com.sun.xml.stream;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import javax.xml.stream.events.XMLEvent;
import org.apache.xerces.impl.msg.XMLMessageFormatter;
import org.apache.xerces.util.SymbolTable;
import org.apache.xerces.util.XMLChar;
import org.apache.xerces.util.XMLResourceIdentifierImpl;
import org.apache.xerces.util.XMLStringBuffer;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.XMLResourceIdentifier;
import org.apache.xerces.xni.XMLString;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.parser.XMLComponent;
import org.apache.xerces.xni.parser.XMLComponentManager;
import org.apache.xerces.xni.parser.XMLConfigurationException;

/**
 * This class is responsible for holding scanning methods common to
 * scanning the XML document structure and content as well as the DTD
 * structure and content. Both XMLDocumentScanner and XMLDTDScanner inherit
 * from this base class.
 *
 * <p>
 * This component requires the following features and properties from the
 * component manager that uses it:
 * <ul>
 *  <li>http://xml.org/sax/features/validation</li>
 *  <li>http://apache.org/xml/features/scanner/notify-char-refs</li>
 *  <li>http://apache.org/xml/properties/internal/symbol-table</li>
 *  <li>http://apache.org/xml/properties/internal/error-reporter</li>
 *  <li>http://apache.org/xml/properties/internal/entity-manager</li>
 * </ul>
 *
 * @author Andy Clark, IBM
 * @author Arnaud  Le Hors, IBM
 * @author Eric Ye, IBM
 * @author K.Venugopal SUN Microsystems
 *
 * @version $Id: XMLScanner.java,v 1.7 2010-11-02 21:02:26 joehw Exp $
 */
public abstract class XMLScanner
    implements XMLComponent {
    
    //
    // Constants
    //
    
    // feature identifiers
    
    /** Feature identifier: validation. */
    protected static final String VALIDATION =
            Constants.SAX_FEATURE_PREFIX + Constants.VALIDATION_FEATURE;
    
    /** Feature identifier: notify character references. */
    protected static final String NOTIFY_CHAR_REFS =
            Constants.XERCES_FEATURE_PREFIX + Constants.NOTIFY_CHAR_REFS_FEATURE;
    
    // property identifiers
    
    /** Property identifier: symbol table. */
    protected static final String SYMBOL_TABLE =
            Constants.XERCES_PROPERTY_PREFIX + Constants.SYMBOL_TABLE_PROPERTY;
    
    /** Property identifier: error reporter. */
    protected static final String ERROR_REPORTER =
            Constants.XERCES_PROPERTY_PREFIX + Constants.ERROR_REPORTER_PROPERTY;
    
    /** Property identifier: entity manager. */
    protected static final String ENTITY_MANAGER =
            Constants.XERCES_PROPERTY_PREFIX + Constants.ENTITY_MANAGER_PROPERTY;
    
    // debugging
    
    /** Debug attribute normalization. */
    protected static final boolean DEBUG_ATTR_NORMALIZATION = false;
    
    
    //xxx: setting the default value as false, as we dont need to calculate this value
    //we should have a feature when set to true computes this value
    private boolean fNeedNonNormalizedValue = false;
    
    protected ArrayList attributeValueCache = new ArrayList();
    protected ArrayList stringBufferCache = new ArrayList();
    protected int fStringBufferIndex = 0;
    protected boolean fAttributeCacheInitDone = false;
    protected int fAttributeCacheUsedCount = 0;
    
    //
    // Data
    //
    
    // features
    
    /**
     * Validation. This feature identifier is:
     * http://xml.org/sax/features/validation
     */
    protected boolean fValidation = false;
    
    /** Character references notification. */
    protected boolean fNotifyCharRefs = false;
    
    // properties
    
    protected PropertyManager fPropertyManager = null ;
    /** Symbol table. */
    protected SymbolTable fSymbolTable;
    
    /** Error reporter. */
    protected XMLErrorReporter fErrorReporter;
    
    /** Entity manager. */
    //protected XMLEntityManager fEntityManager = PropertyManager.getEntityManager();
    protected XMLEntityManager fEntityManager = null ;
    
    /** xxx this should be available from EntityManager Entity storage */
    protected XMLEntityStorage fEntityStore = null ;
    
    // protected data
    
    /** event type */
    protected XMLEvent fEvent ;
    
    /** Entity scanner, this alwasy works on last entity that was opened. */
    protected XMLEntityReaderImpl fEntityScanner = null;
    
    /** Entity depth. */
    protected int fEntityDepth;
    
    /** Literal value of the last character refence scanned. */
    protected String fCharRefLiteral = null;
    
    /** Scanning attribute. */
    protected boolean fScanningAttribute;
    
    /** Report entity boundary. */
    protected boolean fReportEntity;
    
    // symbols
    
    /** Symbol: "version". */
    protected final static String fVersionSymbol = "version".intern();
    
    /** Symbol: "encoding". */
    protected final static String fEncodingSymbol = "encoding".intern();
    
    /** Symbol: "standalone". */
    protected final static String fStandaloneSymbol = "standalone".intern();
    
    /** Symbol: "amp". */
    protected final static String fAmpSymbol = "amp".intern();
    
    /** Symbol: "lt". */
    protected final static String fLtSymbol = "lt".intern();
    
    /** Symbol: "gt". */
    protected final static String fGtSymbol = "gt".intern();
    
    /** Symbol: "quot". */
    protected final static String fQuotSymbol = "quot".intern();
    
    /** Symbol: "apos". */
    protected final static String fAposSymbol = "apos".intern();
    
    // temporary variables
    
    // NOTE: These objects are private to help prevent accidental modification
    //       of values by a subclass. If there were protected *and* the sub-
    //       modified the values, it would be difficult to track down the real
    //       cause of the bug. By making these private, we avoid this
    //       possibility.
    
    /** String. */
    private XMLString fString = new XMLString();
    
    /** String buffer. */
    private XMLStringBuffer fStringBuffer = new XMLStringBuffer();
    
    /** String buffer. */
    private XMLStringBuffer fStringBuffer2 = new XMLStringBuffer();
    
    /** String buffer. */
    private XMLStringBuffer fStringBuffer3 = new XMLStringBuffer();
    
    // temporary location for Resource identification information.
    protected XMLResourceIdentifierImpl fResourceIdentifier = new XMLResourceIdentifierImpl();
    int initialCacheCount = 6;
    //
    // XMLComponent methods
    //
    
    /**
     *
     *
     * @param componentManager The component manager.
     *
     * @throws SAXException Throws exception if required features and
     *                      properties cannot be found.
     */
    public void reset(XMLComponentManager componentManager)
    throws XMLConfigurationException {
        
        // Xerces properties
        fSymbolTable = (SymbolTable)componentManager.getProperty(SYMBOL_TABLE);
        fErrorReporter = (XMLErrorReporter)componentManager.getProperty(ERROR_REPORTER);
        fEntityManager = (XMLEntityManager)componentManager.getProperty(ENTITY_MANAGER);
        
        init();
        // sax features
        try {
            fValidation = componentManager.getFeature(VALIDATION);
        } catch (XMLConfigurationException e) {
            fValidation = false;
        }
        try {
            fNotifyCharRefs = componentManager.getFeature(NOTIFY_CHAR_REFS);
        }
        catch (XMLConfigurationException e) {
            fNotifyCharRefs = false;
        }
        
    } // reset(XMLComponentManager)
    
    protected void setPropertyManager(PropertyManager propertyManager){
        fPropertyManager = propertyManager ;
    }
    
    /**
     * Sets the value of a property during parsing.
     *
     * @param propertyId
     * @param value
     */
    public void setProperty(String propertyId, Object value)
    throws XMLConfigurationException {
        
        // Xerces properties
        if (propertyId.startsWith(Constants.XERCES_PROPERTY_PREFIX)) {
            String property =
                    propertyId.substring(Constants.XERCES_PROPERTY_PREFIX.length());
            if (property.equals(Constants.SYMBOL_TABLE_PROPERTY)) {
                fSymbolTable = (SymbolTable)value;
            } else if (property.equals(Constants.ERROR_REPORTER_PROPERTY)) {
                fErrorReporter = (XMLErrorReporter)value;
            } else if (property.equals(Constants.ENTITY_MANAGER_PROPERTY)) {
                fEntityManager = (XMLEntityManager)value;
            }
        }
                /*else if(propertyId.equals(Constants.STAX_PROPERTIES)){
            fStaxProperties = (HashMap)value;
            //TODO::discuss with neeraj what are his thoughts on passing properties.
            //For now use this
        }*/
        
    } // setProperty(String,Object)
    
    /*
     * Sets the feature of the scanner.
     */
    public void setFeature(String featureId, boolean value)
    throws XMLConfigurationException {
        
        if (VALIDATION.equals(featureId)) {
            fValidation = value;
        } else if (NOTIFY_CHAR_REFS.equals(featureId)) {
            fNotifyCharRefs = value;
        }
    }
    
    /*
     * Gets the state of the feature of the scanner.
     */
    public boolean getFeature(String featureId)
    throws XMLConfigurationException {
        
        if (VALIDATION.equals(featureId)) {
            return fValidation;
        } else if (NOTIFY_CHAR_REFS.equals(featureId)) {
            return fNotifyCharRefs;
        }
        throw new XMLConfigurationException(XMLConfigurationException.NOT_RECOGNIZED, featureId);
    }
    
    //
    // Protected methods
    //
    
    // anybody calling this had better have set Symtoltable!
    public void reset(PropertyManager propertyManager) {
        init();
        // Xerces properties
        fSymbolTable = (SymbolTable)propertyManager.getProperty(Constants.XERCES_PROPERTY_PREFIX + Constants.SYMBOL_TABLE_PROPERTY);
        
        fErrorReporter = (XMLErrorReporter)propertyManager.getProperty(Constants.XERCES_PROPERTY_PREFIX + Constants.ERROR_REPORTER_PROPERTY);
        
        fEntityManager = (XMLEntityManager)propertyManager.getProperty(ENTITY_MANAGER);
        fEntityStore = fEntityManager.getEntityStore() ;
        fEntityScanner = (XMLEntityReaderImpl)fEntityManager.getEntityReader() ;
        //fEntityManager.reset();
        // DTD preparsing defaults:
        fValidation = false;
        fNotifyCharRefs = false;
        
    }
    // common scanning methods
    
    /**
     * Scans an XML or text declaration.
     * <p>
     * <pre>
     * [23] XMLDecl ::= '<?xml' VersionInfo EncodingDecl? SDDecl? S? '?>'
     * [24] VersionInfo ::= S 'version' Eq (' VersionNum ' | " VersionNum ")
     * [80] EncodingDecl ::= S 'encoding' Eq ('"' EncName '"' |  "'" EncName "'" )
     * [81] EncName ::= [A-Za-z] ([A-Za-z0-9._] | '-')*
     * [32] SDDecl ::= S 'standalone' Eq (("'" ('yes' | 'no') "'")
     *                 | ('"' ('yes' | 'no') '"'))
     *
     * [77] TextDecl ::= '<?xml' VersionInfo? EncodingDecl S? '?>'
     * </pre>
     *
     * @param scanningTextDecl True if a text declaration is to
     *                         be scanned instead of an XML
     *                         declaration.
     * @param pseudoAttributeValues An array of size 3 to return the version,
     *                         encoding and standalone pseudo attribute values
     *                         (in that order).
     *
     * <strong>Note:</strong> This method uses fString, anything in it
     * at the time of calling is lost.
     */
    protected void scanXMLDeclOrTextDecl(boolean scanningTextDecl,
            String[] pseudoAttributeValues)
            throws IOException, XNIException {
        
        // pseudo-attribute values
        String version = null;
        String encoding = null;
        String standalone = null;
        
        // scan pseudo-attributes
        final int STATE_VERSION = 0;
        final int STATE_ENCODING = 1;
        final int STATE_STANDALONE = 2;
        final int STATE_DONE = 3;
        int state = STATE_VERSION;
        
        boolean dataFoundForTarget = false;
        boolean sawSpace = fEntityScanner.skipSpaces();
        while (fEntityScanner.peekChar() != '?') {
            dataFoundForTarget = true;
            String name = scanPseudoAttribute(scanningTextDecl, fString);
            switch (state) {
                case STATE_VERSION: {
                    if (name.equals(fVersionSymbol)) {
                        if (!sawSpace) {
                            reportFatalError(scanningTextDecl
                                    ? "SpaceRequiredBeforeVersionInTextDecl"
                                    : "SpaceRequiredBeforeVersionInXMLDecl",
                                    null);
                        }
                        version = fString.toString();
                        state = STATE_ENCODING;
                        if (!versionSupported(version)) {
                            reportFatalError("VersionNotSupported",
                                    new Object[]{version});
                        }
                    } else if (name.equals(fEncodingSymbol)) {
                        if (!scanningTextDecl) {
                            reportFatalError("VersionInfoRequired", null);
                        }
                        if (!sawSpace) {
                            reportFatalError(scanningTextDecl
                                    ? "SpaceRequiredBeforeEncodingInTextDecl"
                                    : "SpaceRequiredBeforeEncodingInXMLDecl",
                                    null);
                        }
                        encoding = fString.toString();
                        state = scanningTextDecl ? STATE_DONE : STATE_STANDALONE;
                    } else {
                        if (scanningTextDecl) {
                            reportFatalError("EncodingDeclRequired", null);
                        } else {
                            reportFatalError("VersionInfoRequired", null);
                        }
                    }
                    break;
                }
                case STATE_ENCODING: {
                    if (name.equals(fEncodingSymbol)) {
                        if (!sawSpace) {
                            reportFatalError(scanningTextDecl
                                    ? "SpaceRequiredBeforeEncodingInTextDecl"
                                    : "SpaceRequiredBeforeEncodingInXMLDecl",
                                    null);
                        }
                        encoding = fString.toString();
                        state = scanningTextDecl ? STATE_DONE : STATE_STANDALONE;
                        // TODO: check encoding name; set encoding on
                        //       entity scanner
                    } else if (!scanningTextDecl && name.equals(fStandaloneSymbol)) {
                        if (!sawSpace) {
                            reportFatalError("SpaceRequiredBeforeStandalone",
                                    null);
                        }
                        standalone = fString.toString();
                        state = STATE_DONE;
                        if (!standalone.equals("yes") && !standalone.equals("no")) {
                            reportFatalError("SDDeclInvalid", null);
                        }
                    } else {
                        reportFatalError("EncodingDeclRequired", null);
                    }
                    break;
                }
                case STATE_STANDALONE: {
                    if (name.equals(fStandaloneSymbol)) {
                        if (!sawSpace) {
                            reportFatalError("SpaceRequiredBeforeStandalone",
                                    null);
                        }
                        standalone = fString.toString();
                        state = STATE_DONE;
                        if (!standalone.equals("yes") && !standalone.equals("no")) {
                            reportFatalError("SDDeclInvalid", null);
                        }
                    } else {
                        reportFatalError("EncodingDeclRequired", null);
                    }
                    break;
                }
                default: {
                    reportFatalError("NoMorePseudoAttributes", null);
                }
            }
            sawSpace = fEntityScanner.skipSpaces();
        }
        // REVISIT: should we remove this error reporting?
        if (scanningTextDecl && state != STATE_DONE) {
            reportFatalError("MorePseudoAttributes", null);
        }
        
        // If there is no data in the xml or text decl then we fail to report error
        // for version or encoding info above.
        if (scanningTextDecl) {
            if (!dataFoundForTarget && encoding == null) {
                reportFatalError("EncodingDeclRequired", null);
            }
        } else {
            if (!dataFoundForTarget && version == null) {
                reportFatalError("VersionInfoRequired", null);
            }
        }
        
        // end
        if (!fEntityScanner.skipChar('?')) {
            reportFatalError("XMLDeclUnterminated", null);
        }
        if (!fEntityScanner.skipChar('>')) {
            reportFatalError("XMLDeclUnterminated", null);
            
        }
        
        // fill in return array
        pseudoAttributeValues[0] = version;
        pseudoAttributeValues[1] = encoding;
        pseudoAttributeValues[2] = standalone;
        
    } // scanXMLDeclOrTextDecl(boolean)
    
    /**
     * Scans a pseudo attribute.
     *
     * @param scanningTextDecl True if scanning this pseudo-attribute for a
     *                         TextDecl; false if scanning XMLDecl. This
     *                         flag is needed to report the correct type of
     *                         error.
     * @param value            The string to fill in with the attribute
     *                         value.
     *
     * @return The name of the attribute
     *
     * <strong>Note:</strong> This method uses fStringBuffer2, anything in it
     * at the time of calling is lost.
     */
    public String scanPseudoAttribute(boolean scanningTextDecl,
            XMLString value)
            throws IOException, XNIException {
        
        String name = fEntityScanner.scanName();
        // XMLEntityManager.print(fEntityManager.getCurrentEntity());
        
        if (name == null) {
            reportFatalError("PseudoAttrNameExpected", null);
        }
        fEntityScanner.skipSpaces();
        if (!fEntityScanner.skipChar('=')) {
            reportFatalError(scanningTextDecl ? "EqRequiredInTextDecl"
                    : "EqRequiredInXMLDecl", new Object[]{name});
        }
        fEntityScanner.skipSpaces();
        int quote = fEntityScanner.peekChar();
        if (quote != '\'' && quote != '"') {
            reportFatalError(scanningTextDecl ? "QuoteRequiredInTextDecl"
                    : "QuoteRequiredInXMLDecl" , new Object[]{name});
        }
        fEntityScanner.scanChar();
        int c = fEntityScanner.scanLiteral(quote, value);
        if (c != quote) {
            fStringBuffer2.clear();
            do {
                fStringBuffer2.append(value);
                if (c != -1) {
                    if (c == '&' || c == '%' || c == '<' || c == ']') {
                        fStringBuffer2.append((char)fEntityScanner.scanChar());
                    } else if (XMLChar.isHighSurrogate(c)) {
                        scanSurrogates(fStringBuffer2);
                    } else if (isInvalidLiteral(c)) {
                        String key = scanningTextDecl
                                ? "InvalidCharInTextDecl" : "InvalidCharInXMLDecl";
                        reportFatalError(key,
                        new Object[] {Integer.toString(c, 16)});
                        fEntityScanner.scanChar();
                    }
                }
                c = fEntityScanner.scanLiteral(quote, value);
            } while (c != quote);
            fStringBuffer2.append(value);
            value.setValues(fStringBuffer2);
        }
        if (!fEntityScanner.skipChar(quote)) {
            reportFatalError(scanningTextDecl ? "CloseQuoteMissingInTextDecl"
            : "CloseQuoteMissingInXMLDecl",
            new Object[]{name});
        }
        
        // return
        return name;
        
    } // scanPseudoAttribute(XMLString):String
    
    /**
     * Scans a processing instruction.
     * <p>
     * <pre>
     * [16] PI ::= '&lt;?' PITarget (S (Char* - (Char* '?>' Char*)))? '?>'
     * [17] PITarget ::= Name - (('X' | 'x') ('M' | 'm') ('L' | 'l'))
     * </pre>
     */
    //CHANGED:
    //EARLIER: scanPI()
    //NOW: scanPI(XMLStringBuffer)
    //it makes things more easy if XMLStringBUffer is passed. Motivation for this change is same
    // as that for scanContent()
    
    protected void scanPI(XMLStringBuffer data) throws IOException, XNIException {
        
        // target
        fReportEntity = false;
        String target = fEntityScanner.scanName();
        if (target == null) {
            reportFatalError("PITargetRequired", null);
        }
        
        // scan data
        scanPIData(target, data);
        fReportEntity = true;
        
    } // scanPI(XMLStringBuffer)
    
    /**
     * Scans a processing data. This is needed to handle the situation
     * where a document starts with a processing instruction whose
     * target name <em>starts with</em> "xml". (e.g. xmlfoo)
     *
     * This method would always read the whole data. We have while loop and data is buffered
     * until delimeter is encountered.
     *
     * @param target The PI target
     * @param data The string to fill in with the data
     */
    
    //CHANGED:
    //Earlier:This method uses the fStringBuffer and later buffer values are set to
    //the supplied XMLString....
    //Now: Changed the signature of this function to pass XMLStringBuffer.. and data would
    //be appended to that buffer
    
    protected void scanPIData(String target, XMLStringBuffer data)
    throws IOException, XNIException {
        
        // check target
        if (target.length() == 3) {
            char c0 = Character.toLowerCase(target.charAt(0));
            char c1 = Character.toLowerCase(target.charAt(1));
            char c2 = Character.toLowerCase(target.charAt(2));
            if (c0 == 'x' && c1 == 'm' && c2 == 'l') {
                reportFatalError("ReservedPITarget", null);
            }
        }
        
        // spaces
        if (!fEntityScanner.skipSpaces()) {
            if (fEntityScanner.skipString("?>")) {
                // we found the end, there is no data just return
                return;
            } else {
                // if there is data there should be some space
                reportFatalError("SpaceRequiredInPI", null);
            }
        }
        
        // since scanData appends the parsed data to the buffer passed
        // a while loop would append the whole of parsed data to the buffer(data:XMLStringBuffer)
        //until all of the data is buffered.
        if (fEntityScanner.scanData("?>", data)) {
            do {
                int c = fEntityScanner.peekChar();
                if (c != -1) {
                    if (XMLChar.isHighSurrogate(c)) {
                        scanSurrogates(data);
                    } else if (isInvalidLiteral(c)) {
                        reportFatalError("InvalidCharInPI",
                        new Object[]{Integer.toHexString(c)});
                        fEntityScanner.scanChar();
                    }
                }
            } while (fEntityScanner.scanData("?>", data));
        }
        
    } // scanPIData(String,XMLString)
    
    /**
     * Scans a comment.
     * <p>
     * <pre>
     * [15] Comment ::= '&lt!--' ((Char - '-') | ('-' (Char - '-')))* '-->'
     * </pre>
     * <p>
     * <strong>Note:</strong> Called after scanning past '&lt;!--'
     * <strong>Note:</strong> This method uses fString, anything in it
     * at the time of calling is lost.
     *
     * @param text The buffer to fill in with the text.
     */
    protected void scanComment(XMLStringBuffer text)
    throws IOException, XNIException {
        
        //System.out.println( "XMLScanner#scanComment# In Scan Comment" );
        // text
        // REVISIT: handle invalid character, eof
        text.clear();
        while (fEntityScanner.scanData("--", text)) {
            int c = fEntityScanner.peekChar();
            
            //System.out.println( "XMLScanner#scanComment#text.toString() == " + text.toString() );
            //System.out.println( "XMLScanner#scanComment#c == " + c );
            
            if (c != -1) {
                if (XMLChar.isHighSurrogate(c)) {
                    scanSurrogates(text);
                }
                if (isInvalidLiteral(c)) {
                    reportFatalError("InvalidCharInComment",
                    new Object[] { Integer.toHexString(c) });
                    fEntityScanner.scanChar();
                }
            }
        }
        if (!fEntityScanner.skipChar('>')) {
            reportFatalError("DashDashInComment", null);
        }
        
    } // scanComment()
    
    /**
     * Scans an attribute value and normalizes whitespace converting all
     * whitespace characters to space characters.
     *
     * [10] AttValue ::= '"' ([^<&"] | Reference)* '"' | "'" ([^<&'] | Reference)* "'"
     *
     * @param value The XMLString to fill in with the value.
     * @param nonNormalizedValue The XMLString to fill in with the
     *                           non-normalized value.
     * @param atName The name of the attribute being parsed (for error msgs).
     * @param attributes The attributes list for the scanned attribute.
     * @param attrIndex The index of the attribute to use from the list.
     * @param checkEntities true if undeclared entities should be reported as VC violation,
     *                      false if undeclared entities should be reported as WFC violation.
     *
     * <strong>Note:</strong> This method uses fStringBuffer2, anything in it
     * at the time of calling is lost.
     **/
    protected void scanAttributeValue(XMLString value,
    XMLString nonNormalizedValue,
    String atName,
    XMLAttributes attributes, int attrIndex,
    boolean checkEntities)
    throws IOException, XNIException {
        XMLStringBuffer stringBuffer = null;
        // quote
        int quote = fEntityScanner.peekChar();
        if (quote != '\'' && quote != '"') {
            reportFatalError("OpenQuoteExpected", new Object[]{atName});
        }
        
        fEntityScanner.scanChar();
        int entityDepth = fEntityDepth;
        
        int c = fEntityScanner.scanLiteral(quote, value);
        if (DEBUG_ATTR_NORMALIZATION) {
            System.out.println("** scanLiteral -> \""
            + value.toString() + "\"");
        }
        if(fNeedNonNormalizedValue){
            fStringBuffer2.clear();
            fStringBuffer2.append(value);
        }
        if(fEntityScanner.whiteSpaceLen > 0)
            normalizeWhitespace(value);
        if (DEBUG_ATTR_NORMALIZATION) {
            System.out.println("** normalizeWhitespace -> \""
            + value.toString() + "\"");
        }
        if (c != quote) {
            fScanningAttribute = true;
            stringBuffer = getStringBuffer();
            stringBuffer.clear();
            do {
                stringBuffer.append(value);
                if (DEBUG_ATTR_NORMALIZATION) {
                    System.out.println("** value2: \""
                    + stringBuffer.toString() + "\"");
                }
                if (c == '&') {
                    fEntityScanner.skipChar('&');
                    if (entityDepth == fEntityDepth && fNeedNonNormalizedValue ) {
                        fStringBuffer2.append('&');
                    }
                    if (fEntityScanner.skipChar('#')) {
                        if (entityDepth == fEntityDepth && fNeedNonNormalizedValue ) {
                            fStringBuffer2.append('#');
                        }
                        int ch ;
                        if (fNeedNonNormalizedValue)
                            ch = scanCharReferenceValue(stringBuffer, fStringBuffer2);
                        else
                            ch = scanCharReferenceValue(stringBuffer, null);
                        
                        if (ch != -1) {
                            if (DEBUG_ATTR_NORMALIZATION) {
                                System.out.println("** value3: \""
                                + stringBuffer.toString()
                                + "\"");
                            }
                        }
                    } else {
                        String entityName = fEntityScanner.scanName();
                        if (entityName == null) {
                            reportFatalError("NameRequiredInReference", null);
                        } else if (entityDepth == fEntityDepth && fNeedNonNormalizedValue) {
                            fStringBuffer2.append(entityName);
                        }
                        if (!fEntityScanner.skipChar(';')) {
                            reportFatalError("SemicolonRequiredInReference",
                            new Object []{entityName});
                        } else if (entityDepth == fEntityDepth && fNeedNonNormalizedValue) {
                            fStringBuffer2.append(';');
                        }
                        if (entityName == fAmpSymbol) {
                            stringBuffer.append('&');
                            if (DEBUG_ATTR_NORMALIZATION) {
                                System.out.println("** value5: \""
                                + stringBuffer.toString()
                                + "\"");
                            }
                        } else if (entityName == fAposSymbol) {
                            stringBuffer.append('\'');
                            if (DEBUG_ATTR_NORMALIZATION) {
                                System.out.println("** value7: \""
                                + stringBuffer.toString()
                                + "\"");
                            }
                        } else if (entityName == fLtSymbol) {
                            stringBuffer.append('<');
                            if (DEBUG_ATTR_NORMALIZATION) {
                                System.out.println("** value9: \""
                                + stringBuffer.toString()
                                + "\"");
                            }
                        } else if (entityName == fGtSymbol) {
                            stringBuffer.append('>');
                            if (DEBUG_ATTR_NORMALIZATION) {
                                System.out.println("** valueB: \""
                                + stringBuffer.toString()
                                + "\"");
                            }
                        } else if (entityName == fQuotSymbol) {
                            stringBuffer.append('"');
                            if (DEBUG_ATTR_NORMALIZATION) {
                                System.out.println("** valueD: \""
                                + stringBuffer.toString()
                                + "\"");
                            }
                        } else {
                            if (fEntityStore.isExternalEntity(entityName)) {
                                reportFatalError("ReferenceToExternalEntity",
                                new Object[] { entityName });
                            } else {
                                if (!fEntityStore.isDeclaredEntity(entityName)) {
                                    //WFC & VC: Entity Declared
                                    if (checkEntities) {
                                        if (fValidation) {
                                            fErrorReporter.reportError(fEntityScanner,XMLMessageFormatter.XML_DOMAIN,
                                            "EntityNotDeclared",
                                            new Object[]{entityName},
                                            XMLErrorReporter.SEVERITY_ERROR);
                                        }
                                    } else {
                                        reportFatalError("EntityNotDeclared",
                                        new Object[]{entityName});
                                    }
                                }
                                fEntityManager.startEntity(entityName, true);
                            }
                        }
                    }
                } else if (c == '<') {
                    reportFatalError("LessthanInAttValue",
                    new Object[] { null, atName });
                    fEntityScanner.scanChar();
                    if (entityDepth == fEntityDepth && fNeedNonNormalizedValue) {
                        fStringBuffer2.append((char)c);
                    }
                } else if (c == '%' || c == ']') {
                    fEntityScanner.scanChar();
                    stringBuffer.append((char)c);
                    if (entityDepth == fEntityDepth && fNeedNonNormalizedValue) {
                        fStringBuffer2.append((char)c);
                    }
                    if (DEBUG_ATTR_NORMALIZATION) {
                        System.out.println("** valueF: \""
                        + stringBuffer.toString() + "\"");
                    }
                } else if (c == '\n' || c == '\r') {
                    fEntityScanner.scanChar();
                    stringBuffer.append(' ');
                    if (entityDepth == fEntityDepth && fNeedNonNormalizedValue) {
                        fStringBuffer2.append('\n');
                    }
                } else if (c != -1 && XMLChar.isHighSurrogate(c)) {
                    if (scanSurrogates(fStringBuffer3)) {
                        stringBuffer.append(fStringBuffer3);
                        if (entityDepth == fEntityDepth && fNeedNonNormalizedValue) {
                            fStringBuffer2.append(fStringBuffer3);
                        }
                        if (DEBUG_ATTR_NORMALIZATION) {
                            System.out.println("** valueI: \""
                            + stringBuffer.toString()
                            + "\"");
                        }
                    }
                } else if (c != -1 && isInvalidLiteral(c)) {
                    reportFatalError("InvalidCharInAttValue",
                    new Object[] {Integer.toString(c, 16)});
                    fEntityScanner.scanChar();
                    if (entityDepth == fEntityDepth && fNeedNonNormalizedValue) {
                        fStringBuffer2.append((char)c);
                    }
                }
                c = fEntityScanner.scanLiteral(quote, value);
                if (entityDepth == fEntityDepth && fNeedNonNormalizedValue) {
                    fStringBuffer2.append(value);
                }
                if(fEntityScanner.whiteSpaceLen > 0)
                    normalizeWhitespace(value);
                //Todo ::Move this check  to Attributes , do conversion
                //only if attribute is being accessed. -Venu
            } while (c != quote || entityDepth != fEntityDepth);
            stringBuffer.append(value);
            if (DEBUG_ATTR_NORMALIZATION) {
                System.out.println("** valueN: \""
                + stringBuffer.toString() + "\"");
            }
            value.setValues(stringBuffer);
            fScanningAttribute = false;
        }
        if(fNeedNonNormalizedValue)
            nonNormalizedValue.setValues(fStringBuffer2);
        
        // quote
        int cquote = fEntityScanner.scanChar();
        if (cquote != quote) {
            reportFatalError("CloseQuoteExpected", new Object[]{atName});
        }
    } // scanAttributeValue()
    
    
    /**
     * Scans External ID and return the public and system IDs.
     *
     * @param identifiers An array of size 2 to return the system id,
     *                    and public id (in that order).
     * @param optionalSystemId Specifies whether the system id is optional.
     *
     * <strong>Note:</strong> This method uses fString and fStringBuffer,
     * anything in them at the time of calling is lost.
     */
    protected void scanExternalID(String[] identifiers,
    boolean optionalSystemId)
    throws IOException, XNIException {
        
        String systemId = null;
        String publicId = null;
        if (fEntityScanner.skipString("PUBLIC")) {
            if (!fEntityScanner.skipSpaces()) {
                reportFatalError("SpaceRequiredAfterPUBLIC", null);
            }
            scanPubidLiteral(fString);
            publicId = fString.toString();
            
            if (!fEntityScanner.skipSpaces() && !optionalSystemId) {
                reportFatalError("SpaceRequiredBetweenPublicAndSystem", null);
            }
        }
        
        if (publicId != null || fEntityScanner.skipString("SYSTEM")) {
            if (publicId == null && !fEntityScanner.skipSpaces()) {
                reportFatalError("SpaceRequiredAfterSYSTEM", null);
            }
            int quote = fEntityScanner.peekChar();
            if (quote != '\'' && quote != '"') {
                if (publicId != null && optionalSystemId) {
                    // looks like we don't have any system id
                    // simply return the public id
                    identifiers[0] = null;
                    identifiers[1] = publicId;
                    return;
                }
                reportFatalError("QuoteRequiredInSystemID", null);
            }
            fEntityScanner.scanChar();
            XMLString ident = fString;
            if (fEntityScanner.scanLiteral(quote, ident) != quote) {
                fStringBuffer.clear();
                do {
                    fStringBuffer.append(ident);
                    int c = fEntityScanner.peekChar();
                    if (XMLChar.isMarkup(c) || c == ']') {
                        fStringBuffer.append((char)fEntityScanner.scanChar());
                    }
                } while (fEntityScanner.scanLiteral(quote, ident) != quote);
                fStringBuffer.append(ident);
                ident = fStringBuffer;
            }
            systemId = ident.toString();
            if (!fEntityScanner.skipChar(quote)) {
                reportFatalError("SystemIDUnterminated", null);
            }
        }
        
        // store result in array
        identifiers[0] = systemId;
        identifiers[1] = publicId;
    }
    
    
    /**
     * Scans public ID literal.
     *
     * [12] PubidLiteral ::= '"' PubidChar* '"' | "'" (PubidChar - "'")* "'"
     * [13] PubidChar::= #x20 | #xD | #xA | [a-zA-Z0-9] | [-'()+,./:=?;!*#@$_%]
     *
     * The returned string is normalized according to the following rule,
     * from http://www.w3.org/TR/REC-xml#dt-pubid:
     *
     * Before a match is attempted, all strings of white space in the public
     * identifier must be normalized to single space characters (#x20), and
     * leading and trailing white space must be removed.
     *
     * @param literal The string to fill in with the public ID literal.
     * @return True on success.
     *
     * <strong>Note:</strong> This method uses fStringBuffer, anything in it at
     * the time of calling is lost.
     */
    protected boolean scanPubidLiteral(XMLString literal)
    throws IOException, XNIException {
        int quote = fEntityScanner.scanChar();
        if (quote != '\'' && quote != '"') {
            reportFatalError("QuoteRequiredInPublicID", null);
            return false;
        }
        
        fStringBuffer.clear();
        // skip leading whitespace
        boolean skipSpace = true;
        boolean dataok = true;
        while (true) {
            int c = fEntityScanner.scanChar();
            if (c == ' ' || c == '\n' || c == '\r') {
                if (!skipSpace) {
                    // take the first whitespace as a space and skip the others
                    fStringBuffer.append(' ');
                    skipSpace = true;
                }
            } else if (c == quote) {
                if (skipSpace) {
                    // if we finished on a space let's trim it
                    fStringBuffer.length--;
                }
                literal.setValues(fStringBuffer);
                break;
            } else if (XMLChar.isPubid(c)) {
                fStringBuffer.append((char)c);
                skipSpace = false;
            } else if (c == -1) {
                reportFatalError("PublicIDUnterminated", null);
                return false;
            } else {
                dataok = false;
                reportFatalError("InvalidCharInPublicID",
                new Object[]{Integer.toHexString(c)});
            }
        }
        return dataok;
    }
    
    
    /**
     * Normalize whitespace in an XMLString converting all whitespace
     * characters to space characters.
     */
    protected void normalizeWhitespace(XMLString value) {
        int i=0;
        int j=0;
        int [] buff = fEntityScanner.whiteSpaceLookup;
        int buffLen = fEntityScanner.whiteSpaceLen;
        int end = value.offset + value.length;
        while(i < buffLen){
            j = buff[i];
            if(j < end ){
                value.ch[j] = ' ';
            }
            i++;
        }
    }
    
    //
    // XMLEntityHandler methods
    //
    
    /**
     * This method notifies of the start of an entity. The document entity
     * has the pseudo-name of "[xml]" the DTD has the pseudo-name of "[dtd]"
     * parameter entity names start with '%'; and general entities are just
     * specified by their name.
     *
     * @param name     The name of the entity.
     * @param identifier The resource identifier.
     * @param encoding The auto-detected IANA encoding name of the entity
     *                 stream. This value will be null in those situations
     *                 where the entity encoding is not auto-detected (e.g.
     *                 internal entities or a document entity that is
     *                 parsed from a java.io.Reader).
     *
     * @throws XNIException Thrown by handler to signal an error.
     */
    public void startEntity(String name,
    XMLResourceIdentifier identifier,
    String encoding) throws XNIException {
        
        // keep track of the entity depth
        fEntityDepth++;
        
    } // startEntity(String,XMLResourceIdentifier,String)
    
    /**
     * This method notifies the end of an entity. The document entity has
     * the pseudo-name of "[xml]" the DTD has the pseudo-name of "[dtd]"
     * parameter entity names start with '%'; and general entities are just
     * specified by their name.
     *
     * @param name The name of the entity.
     *
     * @throws XNIException Thrown by handler to signal an error.
     */
    public void endEntity(String name) throws IOException, XNIException {
        
        // keep track of the entity depth
        fEntityDepth--;
        
    } // endEntity(String)
    
    /**
     * Scans a character reference and append the corresponding chars to the
     * specified buffer.
     *
     * <p>
     * <pre>
     * [66] CharRef ::= '&#' [0-9]+ ';' | '&#x' [0-9a-fA-F]+ ';'
     * </pre>
     *
     * <strong>Note:</strong> This method uses fStringBuffer, anything in it
     * at the time of calling is lost.
     *
     * @param buf the character buffer to append chars to
     * @param buf2 the character buffer to append non-normalized chars to
     *
     * @return the character value or (-1) on conversion failure
     */
    protected int scanCharReferenceValue(XMLStringBuffer buf, XMLStringBuffer buf2)
    throws IOException, XNIException {
        // scan hexadecimal value
        boolean hex = false;
        if (fEntityScanner.skipChar('x')) {
            if (buf2 != null) { buf2.append('x'); }
            hex = true;
            fStringBuffer3.clear();
            boolean digit = true;
            
            int c = fEntityScanner.peekChar();
            digit = (c >= '0' && c <= '9') ||
            (c >= 'a' && c <= 'f') ||
            (c >= 'A' && c <= 'F');
            if (digit) {
                if (buf2 != null) { buf2.append((char)c); }
                fEntityScanner.scanChar();
                fStringBuffer3.append((char)c);
                
                do {
                    c = fEntityScanner.peekChar();
                    digit = (c >= '0' && c <= '9') ||
                    (c >= 'a' && c <= 'f') ||
                    (c >= 'A' && c <= 'F');
                    if (digit) {
                        if (buf2 != null) { buf2.append((char)c); }
                        fEntityScanner.scanChar();
                        fStringBuffer3.append((char)c);
                    }
                } while (digit);
            } else {
                reportFatalError("HexdigitRequiredInCharRef", null);
            }
        }
        
        // scan decimal value
        else {
            fStringBuffer3.clear();
            boolean digit = true;
            
            int c = fEntityScanner.peekChar();
            digit = c >= '0' && c <= '9';
            if (digit) {
                if (buf2 != null) { buf2.append((char)c); }
                fEntityScanner.scanChar();
                fStringBuffer3.append((char)c);
                
                do {
                    c = fEntityScanner.peekChar();
                    digit = c >= '0' && c <= '9';
                    if (digit) {
                        if (buf2 != null) { buf2.append((char)c); }
                        fEntityScanner.scanChar();
                        fStringBuffer3.append((char)c);
                    }
                } while (digit);
            } else {
                reportFatalError("DigitRequiredInCharRef", null);
            }
        }
        
        // end
        if (!fEntityScanner.skipChar(';')) {
            reportFatalError("SemicolonRequiredInCharRef", null);
        }
        if (buf2 != null) { buf2.append(';'); }
        
        // convert string to number
        int value = -1;
        try {
            value = Integer.parseInt(fStringBuffer3.toString(),
            hex ? 16 : 10);
            
            // character reference must be a valid XML character
            if (isInvalid(value)) {
                StringBuffer errorBuf = new StringBuffer(fStringBuffer3.length + 1);
                if (hex) errorBuf.append('x');
                errorBuf.append(fStringBuffer3.ch, fStringBuffer3.offset, fStringBuffer3.length);
                reportFatalError("InvalidCharRef",
                new Object[]{errorBuf.toString()});
            }
        } catch (NumberFormatException e) {
            // Conversion failed, let -1 value drop through.
            // If we end up here, the character reference was invalid.
            StringBuffer errorBuf = new StringBuffer(fStringBuffer3.length + 1);
            if (hex) errorBuf.append('x');
            errorBuf.append(fStringBuffer3.ch, fStringBuffer3.offset, fStringBuffer3.length);
            reportFatalError("InvalidCharRef",
            new Object[]{errorBuf.toString()});
        }
        
        // append corresponding chars to the given buffer
        if (!XMLChar.isSupplemental(value)) {
            buf.append((char) value);
        } else {
            // character is supplemental, split it into surrogate chars
            buf.append(XMLChar.highSurrogate(value));
            buf.append(XMLChar.lowSurrogate(value));
        }
        
        // char refs notification code
        if (fNotifyCharRefs && value != -1) {
            String literal = "#" + (hex ? "x" : "") + fStringBuffer3.toString();
            if (!fScanningAttribute) {
                fCharRefLiteral = literal;
            }
        }
        
        return value;
    }
    // returns true if the given character is not
    // valid with respect to the version of
    // XML understood by this scanner.
    protected static boolean isInvalid(int value) {
        return (XMLChar.isInvalid(value));
    } // isInvalid(int):  boolean
    
    // returns true if the given character is not
    // valid or may not be used outside a character reference
    // with respect to the version of XML understood by this scanner.
    protected static boolean isInvalidLiteral(int value) {
        return (XMLChar.isInvalid(value));
    } // isInvalidLiteral(int):  boolean
    
    // returns true if the given character is
    // a valid nameChar with respect to the version of
    // XML understood by this scanner.
    protected static boolean isValidNameChar(int value) {
        return (XMLChar.isName(value));
    } // isValidNameChar(int):  boolean
    
    // returns true if the given character is
    // a valid NCName character with respect to the version of
    // XML understood by this scanner.
    protected static boolean isValidNCName(int value) {
        return (XMLChar.isNCName(value));
    } // isValidNCName(int):  boolean
    
    // returns true if the given character is
    // a valid nameStartChar with respect to the version of
    // XML understood by this scanner.
    protected static boolean isValidNameStartChar(int value) {
        return (XMLChar.isNameStart(value));
    } // isValidNameStartChar(int):  boolean
    
    protected boolean versionSupported(String version ) {
        return version.equals("1.0");
    } // version Supported
    
    /**
     * Scans surrogates and append them to the specified buffer.
     * <p>
     * <strong>Note:</strong> This assumes the current char has already been
     * identified as a high surrogate.
     *
     * @param buf The StringBuffer to append the read surrogates to.
     * @return True if it succeeded.
     */
    protected boolean scanSurrogates(XMLStringBuffer buf)
    throws IOException, XNIException {
        
        int high = fEntityScanner.scanChar();
        int low = fEntityScanner.peekChar();
        if (!XMLChar.isLowSurrogate(low)) {
            reportFatalError("InvalidCharInContent",
            new Object[] {Integer.toString(high, 16)});
            return false;
        }
        fEntityScanner.scanChar();
        
        // convert surrogates to supplemental character
        int c = XMLChar.supplemental((char)high, (char)low);
        
        // supplemental character must be a valid XML character
        if (isInvalid(c)) {
            reportFatalError("InvalidCharInContent",
            new Object[]{Integer.toString(c, 16)});
            return false;
        }
        
        // fill in the buffer
        buf.append((char)high);
        buf.append((char)low);
        
        return true;
        
    } // scanSurrogates():boolean
    
    
    /**
     * Convenience function used in all XML scanners.
     */
    protected void reportFatalError(String msgId, Object[] args)
    throws XNIException {
        fErrorReporter.reportError(fEntityScanner, XMLMessageFormatter.XML_DOMAIN,
        msgId, args,
        XMLErrorReporter.SEVERITY_FATAL_ERROR);
    }
    
    // private methods
    private void init() {
        // initialize scanner
        //fEntityScanner = XMLEntityReaderImpl.getEntityScanner();
        
        // initialize vars
        fEntityDepth = 0;
        fReportEntity = true;
        fResourceIdentifier.clear();
    }
    
    XMLStringBuffer getStringBuffer(){
        if((fStringBufferIndex < initialCacheCount )|| (fStringBufferIndex < stringBufferCache.size())){
            return (XMLStringBuffer)stringBufferCache.get(fStringBufferIndex++);
        }else{
            XMLStringBuffer tmpObj = new XMLStringBuffer();
            stringBufferCache.add(fStringBufferIndex, tmpObj);
            return tmpObj;
        }
    }
    
    
} // class XMLScanner
