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
 * $Id: XMLNSDocumentScannerImpl.java,v 1.9 2010-11-02 21:02:26 joehw Exp $
 */
package com.sun.xml.stream;

import java.io.IOException;
import javax.xml.stream.events.XMLEvent;
import org.apache.xerces.xni.XMLString;
//import org.apache.xerces.impl.dtd.XMLDTDValidatorFilter;
import org.apache.xerces.impl.msg.XMLMessageFormatter;
import org.apache.xerces.util.XMLAttributesImpl;
import org.apache.xerces.util.XMLStringBuffer;
import org.apache.xerces.util.XMLSymbols;
import org.apache.xerces.xni.NamespaceContext;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.parser.XMLComponentManager;
import org.apache.xerces.xni.parser.XMLConfigurationException;

/**
 * This class adds the functionality of namespace processing.
 *
 * This class has been modified as per the new design which is more suited to
 * efficiently build pull parser. Lot of improvements have been done and
 * the code has been added to support stax functionality/features.
 *
 *
 * This class scans an XML document, checks if document has a DTD, and if
 * DTD is not found the scanner will remove the DTD Validator from the pipeline and perform
 * namespace binding.
 *
 *
 * @author Neeraj Bajaj, Sun Microsystems
 * @author Venugopal Rao K, Sun Microsystems
 * @author Elena Litani, IBM
 * @version $Id: XMLNSDocumentScannerImpl.java,v 1.9 2010-11-02 21:02:26 joehw Exp $
 */
public class XMLNSDocumentScannerImpl
extends XMLDocumentScannerImpl {
    
    /** If validating parser, make sure we report an error in the
     *   scanner if DTD grammar is missing.*/
    protected boolean fPerformValidation;
    private boolean fEmptyElement = false;    
    private XMLBufferListenerImpl listener = new XMLBufferListenerImpl();
   // private boolean fClearAttributes = true;

    private boolean fXmlnsDeclared = false;

    /** Resets the fields of this scanner.
     */
    public void reset(PropertyManager propertyManager) {
        setPropertyManager(propertyManager);
        super.reset(propertyManager);
        try{
            if(!fAttributeCacheInitDone){
                for(int i=0; i<initialCacheCount;i++){
                    attributeValueCache.add(new XMLString());
                    stringBufferCache.add(new XMLStringBuffer());
                }
                fAttributeCacheInitDone = true;
            }
            fStringBufferIndex =0;
            fAttributeCacheUsedCount = 0;
            fEntityScanner.registerListener(listener);
            dtdGrammarUtil=null;
            //fClearAttributes = true;
        }catch(RuntimeException ex){
        }
    }
    
    // private data
    //
    
    /** DTD validator */
    //private XMLDTDValidatorFilter fDTDValidator;
    
    /**
     * Return the information about the element -- If it is EndELement, QName values
     * are computed dynamically.
     *
     * @returns org.apache.xerces.xni.QName
     */
    
    public org.apache.xerces.xni.QName getElementQName(){
        if(fScannerLastState == XMLEvent.END_ELEMENT){
            fElementQName.setValues(fElementStack.getLastPoppedElement());
        }
        return fElementQName ;
    }
    
    
    /**
     * Scans a start element. This method will handle the binding of
     * namespace information and notifying the handler of the start
     * of the element.
     * <p>
     * <pre>
     * [44] EmptyElemTag ::= '&lt;' Name (S Attribute)* S? '/>'
     * [40] STag ::= '&lt;' Name (S Attribute)* S? '>'
     * </pre>
     * <p>
     * <strong>Note:</strong> This method assumes that the leading
     * '&lt;' character has been consumed.
     * <p>
     * <strong>Note:</strong> This method uses the fElementQName and
     * fAttributes variables. The contents of these variables will be
     * destroyed. The caller should copy important information out of
     * these variables before calling this method.
     *
     * @return True if element is empty. (i.e. It matches
     *          production [44].
     */
    protected boolean scanStartElement()
    throws IOException, XNIException {
        
        if (DEBUG_CONTENT_SCANNING) System.out.println(">>> scanStartElement()");
        //when skipping is true and no more elements should be added
        if(fSkip && !fAdd){
            //get the stored element -- if everything goes right this should match the
            //token in the buffer
            
            QName name = fElementStack.getNext();
            
            if(DEBUG_SKIP_ALGORITHM){
                System.out.println("Trying to skip String = " + name.rawname);
            }
            
            //Be conservative -- if skipping fails -- stop.
            fSkip = fEntityScanner.skipString(name.characters); // skipQElement(name);
            
            if(fSkip){
                if(DEBUG_SKIP_ALGORITHM){
                    System.out.println("Element SUCESSFULLY skipped = " + name.rawname);
                }
                fElementStack.push();
                fElementQName = name;
            }else{
                //if skipping fails reposition the stack or fallback to normal way of processing
                fElementStack.reposition();
                if(DEBUG_SKIP_ALGORITHM){
                    System.out.println("Element was NOT skipped, REPOSITIONING stack" );
                }
            }
        }
        
        //we are still at the stage of adding elements
        //the elements were not matched or
        //fSkip is not set to true
        if(!fSkip || fAdd){
            //get the next element from the stack
            fElementQName = fElementStack.nextElement();
            // name
            if (fBindNamespaces) {
                fEntityScanner.scanQName(fElementQName);
            }
            else {
                String name = fEntityScanner.scanName();
                fElementQName.setValues(null, name, name, null);
                //XXX: THIS IS UGLY -- THIS SHOULD BE CHANGED.
                //WE SHOULD DO IT AS PART OF QNAME -- NB.
                fElementQName.characters = fEntityScanner.scannedName;
            }
            
            if(DEBUG_SKIP_ALGORITHM){
                if(fAdd){
                    System.out.println("Elements are being ADDED -- elemet added is = " + fElementQName.rawname + " at count = " + fElementStack.fCount);
                }
            }
            
        }
        
        //when the elements are being added , we need to check if we are set for skipping the elements
        if(fAdd){
            //this sets the value of fAdd variable
            fElementStack.matchElement(fElementQName);
        }
        
        //xxx: We dont need another pointer, fCurrentElement, we can use fElementQName
        fCurrentElement = fElementQName;
        
        String rawname = fElementQName.rawname;        
        if (fBindNamespaces) {
            fNamespaceContext.pushContext();
            if (fScannerState == SCANNER_STATE_ROOT_ELEMENT) {
                if (fPerformValidation) {
                    fErrorReporter.reportError(XMLMessageFormatter.XML_DOMAIN,
                    "MSG_GRAMMAR_NOT_FOUND",
                    new Object[]{ rawname},
                    XMLErrorReporter.SEVERITY_ERROR);
                    
                    if (fDoctypeName == null || !fDoctypeName.equals(rawname)) {
                        fErrorReporter.reportError( XMLMessageFormatter.XML_DOMAIN,
                        "RootElementTypeMustMatchDoctypedecl",
                        new Object[]{fDoctypeName, rawname},
                        XMLErrorReporter.SEVERITY_ERROR);
                    }
                }
            }
        }
        
        
        fEmptyElement = false;
        // attributes must be always cleared
        //if(fClearAttributes)
        fAttributes.removeAllAttributes();
        if(!seekCloseOfStartTag()){
            fReadingAttributes = true;
            fAttributeCacheUsedCount =0;
            fStringBufferIndex =0;
            //fClearAttributes=true;
            fAddDefaultAttr = true;
            fXmlnsDeclared = false;
            
            do {
                scanAttribute(fAttributes);
            } while (!seekCloseOfStartTag());
            fReadingAttributes=false;
        } /*else{
            fClearAttributes=false; 
        }*/
        
        if (fBindNamespaces) {
            // REVISIT: is it required? forbit xmlns prefix for element
            if (fElementQName.prefix == XMLSymbols.PREFIX_XMLNS) {
                fErrorReporter.reportError(XMLMessageFormatter.XMLNS_DOMAIN,
                "ElementXMLNSPrefix",
                new Object[]{fElementQName.rawname},
                XMLErrorReporter.SEVERITY_FATAL_ERROR);
            }
            
            // bind the element
            String prefix = fElementQName.prefix != null
            ? fElementQName.prefix : XMLSymbols.EMPTY_STRING;
            // assign uri to the element
            fElementQName.uri = fNamespaceContext.getURI(prefix);
            // make sure that object in the element stack is updated as well
            fCurrentElement.uri = fElementQName.uri;
            
            if (fElementQName.prefix == null && fElementQName.uri != null) {
                fElementQName.prefix = XMLSymbols.EMPTY_STRING;
            }
            if (fElementQName.prefix != null && fElementQName.uri == null) {
                fErrorReporter.reportError(XMLMessageFormatter.XMLNS_DOMAIN,
                "ElementPrefixUnbound",
                new Object[]{fElementQName.prefix, fElementQName.rawname},
                XMLErrorReporter.SEVERITY_FATAL_ERROR);
            }
            
            // bind attributes (xmlns are already bound bellow)
            int length = fAttributes.getLength();
            // fLength = 0; //initialize structure
            for (int i = 0; i < length; i++) {
                fAttributes.getName(i, fAttributeQName);
                
                String aprefix = fAttributeQName.prefix != null
                ? fAttributeQName.prefix : XMLSymbols.EMPTY_STRING;
                String uri = fNamespaceContext.getURI(aprefix);
                // REVISIT: try removing the first "if" and see if it is faster.
                //
                if (fAttributeQName.uri != null && fAttributeQName.uri == uri) {
                    // checkDuplicates(fAttributeQName, fAttributes);
                    continue;
                }
                if (aprefix != XMLSymbols.EMPTY_STRING) {
                    fAttributeQName.uri = uri;
                    if (uri == null) {
                        fErrorReporter.reportError(XMLMessageFormatter.XMLNS_DOMAIN,
                        "AttributePrefixUnbound",
                        new Object[]{fElementQName.rawname,fAttributeQName.rawname,aprefix},
                        XMLErrorReporter.SEVERITY_FATAL_ERROR);
                    }
                    fAttributes.setURI(i, uri);
                    // checkDuplicates(fAttributeQName, fAttributes);
                }
            }
            
            if (length > 1) {
                QName name = fAttributes.checkDuplicatesNS();
                if (name != null) {
                    if (name.uri != null) {
                        fErrorReporter.reportError(XMLMessageFormatter.XMLNS_DOMAIN,
                        "AttributeNSNotUnique",
                        new Object[]{fElementQName.rawname, name.localpart, name.uri},
                        XMLErrorReporter.SEVERITY_FATAL_ERROR);
                    }
                    else {
                        fErrorReporter.reportError(XMLMessageFormatter.XMLNS_DOMAIN,
                        "AttributeNotUnique",
                        new Object[]{fElementQName.rawname, name.rawname},
                        XMLErrorReporter.SEVERITY_FATAL_ERROR);
                    }
                }
            }
        }
        
        
        if (fEmptyElement) {
            //decrease the markup depth..
            fMarkupDepth--;
            
            // check that this element was opened in the same entity
            if (fMarkupDepth < fEntityStack[fEntityDepth - 1]) {
                reportFatalError("ElementEntityMismatch",
                new Object[]{fCurrentElement.rawname});
            }
            // call handler
            if (fDocumentHandler != null) {
                fDocumentHandler.emptyElement(fElementQName, fAttributes, null);
            }
            
            //We should not be popping out the context here in endELement becaause the namespace context is still
            //valid when parser is at the endElement state.
            fScanEndElement = true;
            //if (fBindNamespaces) {
            //  fNamespaceContext.popContext();
            //}
            
            //pop the element off the stack..
            fElementStack.popElement();
            
        } else {
            if(dtdGrammarUtil != null){
                dtdGrammarUtil.startElement(fElementQName, fAttributes); 
            }
            if(fDocumentHandler != null){
                //fDocumentHandler.startElement(fElementQName, fAttributes, null);
            }
        }
        
        
        if (DEBUG_CONTENT_SCANNING) System.out.println("<<< scanStartElement(): "+fEmptyElement);
        return fEmptyElement;
        
    } // scanStartElement():boolean
    
    /**
     * Looks for the close of start tag, i.e. if it finds '>' or '/>'
     * Characters are consumed.
     */
    private boolean seekCloseOfStartTag() throws IOException, XNIException {
        // spaces
        boolean sawSpace = fEntityScanner.skipSpaces();
        
        // end tag?
        final int c = fEntityScanner.peekChar();
        if (c == '>') {
            fEntityScanner.scanChar();
            return true;
        }
        else if (c == '/') {
            fEntityScanner.scanChar();
            if (!fEntityScanner.skipChar('>')) {
                reportFatalError("ElementUnterminated",
                new Object[]{fElementQName.rawname});
            }
            fEmptyElement = true;
            return true;
        }
        else if (!isValidNameStartChar(c) || !sawSpace) {
            reportFatalError("ElementUnterminated", new Object[]{fElementQName.rawname});
        }
        
        return false;
    }
    
    
    
    /**
     * Scans an attribute.
     * <p>
     * <pre>
     * [41] Attribute ::= Name Eq AttValue
     * </pre>
     * <p>
     * <strong>Note:</strong> This method assumes that the next
     * character on the stream is the first character of the attribute
     * name.
     * <p>
     * <strong>Note:</strong> This method uses the fAttributeQName and
     * fQName variables. The contents of these variables will be
     * destroyed.
     *
     * @param attributes The attributes list for the scanned attribute.
     */
    protected void scanAttribute(XMLAttributesImpl attributes)
    throws IOException, XNIException {
        if (DEBUG_CONTENT_SCANNING) System.out.println(">>> scanAttribute()");
        
        // name
        fEntityScanner.scanQName(fAttributeQName);
        
        // equals
        fEntityScanner.skipSpaces();
        if (!fEntityScanner.skipChar('=')) {
            reportFatalError("EqRequiredInAttribute",
            new Object[]{fCurrentElement.rawname,fAttributeQName.rawname});
        }
        fEntityScanner.skipSpaces();
        
        // content
        int attrIndex = 0 ;
        
        
        //REVISIT: one more case needs to be included: external PE and standalone is no
        boolean isVC =  fHasExternalDTD && !fStandalone;
        
        // REVISIT: it seems that this function should not take attributes, and length
        //fTempString would store attribute value
        ///fTempString2 would store attribute non-normalized value
        
        //this function doesn't use 'attIndex'. We are adding the attribute later
        //after we have figured out that current attribute is not namespace declaration
        //since scanAttributeValue doesn't use attIndex parameter therefore we
        //can safely add the attribute later..
        XMLString tmpStr = getString();
        scanAttributeValue(tmpStr, fTempString2,
        fAttributeQName.rawname, attributes,
        attrIndex, isVC);
        
        String value = null;
        //fTempString.toString();
        
        // record namespace declarations if any.
        if (fBindNamespaces) {
            
            String localpart = fAttributeQName.localpart;
            String prefix = fAttributeQName.prefix != null
            ? fAttributeQName.prefix : XMLSymbols.EMPTY_STRING;
            
            // when it's of form xmlns="..." or xmlns:prefix="...",
            // it's a namespace declaration. but prefix:xmlns="..." isn't.
            if (prefix == XMLSymbols.PREFIX_XMLNS ||
            prefix == XMLSymbols.EMPTY_STRING && localpart == XMLSymbols.PREFIX_XMLNS) {
                
                // get the internalized value of this attribute
                String uri = fSymbolTable.addSymbol(tmpStr.ch,tmpStr.offset,tmpStr.length);
                value = uri;
                // 1. "xmlns" can't be bound to any namespace
                if (prefix == XMLSymbols.PREFIX_XMLNS && localpart == XMLSymbols.PREFIX_XMLNS) {
                    fErrorReporter.reportError(XMLMessageFormatter.XMLNS_DOMAIN,
                    "CantBindXMLNS",
                    new Object[]{fAttributeQName},
                    XMLErrorReporter.SEVERITY_FATAL_ERROR);
                }
                
                // 2. the namespace for "xmlns" can't be bound to any prefix
                if (uri == NamespaceContext.XMLNS_URI) {
                    fErrorReporter.reportError(XMLMessageFormatter.XMLNS_DOMAIN,
                    "CantBindXMLNS",
                    new Object[]{fAttributeQName},
                    XMLErrorReporter.SEVERITY_FATAL_ERROR);
                }
                
                // 3. "xml" can't be bound to any other namespace than it's own
                if (localpart == XMLSymbols.PREFIX_XML) {
                    if (uri != NamespaceContext.XML_URI) {
                        fErrorReporter.reportError(XMLMessageFormatter.XMLNS_DOMAIN,
                        "CantBindXML",
                        new Object[]{fAttributeQName},
                        XMLErrorReporter.SEVERITY_FATAL_ERROR);
                    }
                }
                // 4. the namespace for "xml" can't be bound to any other prefix
                else {
                    if (uri ==NamespaceContext.XML_URI) {
                        fErrorReporter.reportError(XMLMessageFormatter.XMLNS_DOMAIN,
                        "CantBindXML",
                        new Object[]{fAttributeQName},
                        XMLErrorReporter.SEVERITY_FATAL_ERROR);
                    }
                }
                
                prefix = localpart != XMLSymbols.PREFIX_XMLNS ? localpart : XMLSymbols.EMPTY_STRING;
                
                // http://www.w3.org/TR/1999/REC-xml-names-19990114/#dt-prefix
                // We should only report an error if there is a prefix,
                // that is, the local part is not "xmlns". -SG
                if (uri == XMLSymbols.EMPTY_STRING && localpart != XMLSymbols.PREFIX_XMLNS) {
                    fErrorReporter.reportError(XMLMessageFormatter.XMLNS_DOMAIN,
                    "EmptyPrefixedAttName",
                    new Object[]{fAttributeQName},
                    XMLErrorReporter.SEVERITY_FATAL_ERROR);
                }
                                
                // check for duplicate prefix bindings
                if (((org.apache.xerces.util.NamespaceSupport) fNamespaceContext).containsPrefixInCurrentContext(prefix)) {
                    reportFatalError("AttributeNotUnique",
                            new Object[]{fCurrentElement.rawname,
                            fAttributeQName.rawname});
                }

                // declare prefix in context
                boolean declared = fNamespaceContext.declarePrefix(prefix, uri.length() != 0 ? uri : null);
                
                // check for duplicate xmlns declarations
                if (!declared) { // by convention, prefix == "xmlns" | "xml"
                    // error if duplicate declaration
                    if (fXmlnsDeclared) {
                        reportFatalError("AttributeNotUnique",
                                new Object[]{fCurrentElement.rawname,
                                fAttributeQName.rawname});
                    }
                    
                    // xmlns declared
                    fXmlnsDeclared = true;
                }

                //we are not adding namespace declarations in the XMLAttributes.
                //Namespace declarations are available from fNamespaceContext
                return ;
                // bind namespace attribute to a namespace
                //we are not adding namespace declarations tothe list of attribute
                //attributes.setURI(attrIndex, fNamespaceContext.getURI(XMLSymbols.PREFIX_XMLNS));
                
            }
            
        }
        
        //add the attributes to the list of attributes
        if (fBindNamespaces) {
            attrIndex = attributes.getLength();
            attributes.addAttributeNS(fAttributeQName, XMLSymbols.fCDATASymbol, null);
        }
        else {
            int oldLen = attributes.getLength();
            attrIndex = attributes.addAttribute(fAttributeQName, XMLSymbols.fCDATASymbol, null);
            
            // WFC: Unique Att Spec
            if (oldLen == attributes.getLength()) {
                reportFatalError("AttributeNotUnique",
                new Object[]{fCurrentElement.rawname,
                fAttributeQName.rawname});
            }
        }
        
        attributes.setValue(attrIndex, value,tmpStr);
        //attributes.setNonNormalizedValue(attrIndex, fTempString2.toString());
        //removing  as we are not using non-normalized values . -Venu
        attributes.setSpecified(attrIndex, true);
        
        // attempt to bind attribute
        if (fAttributeQName.prefix != null) {
            attributes.setURI(attrIndex, fNamespaceContext.getURI(fAttributeQName.prefix));
        }
        
        if (DEBUG_CONTENT_SCANNING) System.out.println("<<< scanAttribute()");
    } // scanAttribute(XMLAttributes)
    
    
    
    /**
     * Scans an end element.
     * <p>
     * <pre>
     * [42] ETag ::= '&lt;/' Name S? '>'
     * </pre>
     * <p>
     * <strong>Note:</strong> This method uses the fElementQName variable.
     * The contents of this variable will be destroyed. The caller should
     * copy the needed information out of this variable before calling
     * this method.
     *
     * @return The element depth.
     */
    protected int scanEndElement() throws IOException, XNIException {
        if (DEBUG_CONTENT_SCANNING) System.out.println(">>> scanEndElement()");
        
        // pop context
        QName endElementName = fElementStack.popElement();
        
        String rawname = endElementName.rawname;
        
        // Take advantage of the fact that next string _should_ be "fElementQName.rawName",
        //In scanners most of the time is consumed on checks done for XML characters, we can
        // optimize on it and avoid the checks done for endElement,
        //we will also avoid symbol table lookup - neeraj.bajaj@sun.com
        
        // this should work both for namespace processing true or false...
        
        //REVISIT: if the string is not the same as expected.. we need to do better error handling..
        //We can skip this for now... In any case if the string doesn't match -- document is not well formed.
        
        //Pass characters instead of string , this gives better performance than strings.
        //Use the character array for rawname present in the symboltable. -Venu
        if (!fEntityScanner.skipString(endElementName.characters)) {
            reportFatalError("ETagRequired", new Object[]{rawname});
        }
        
        // end
        fEntityScanner.skipSpaces();
        if (!fEntityScanner.skipChar('>')) {
            reportFatalError("ETagUnterminated",
            new Object[]{rawname});
        }
        fMarkupDepth--;
        
        //we have increased the depth for two markup "<" characters
        fMarkupDepth--;
        
        // check that this element was opened in the same entity
        if (fMarkupDepth < fEntityStack[fEntityDepth - 1]) {
            reportFatalError("ElementEntityMismatch",
            new Object[]{rawname});
        }
        
        //We should not be popping out the context here in endELement becaause the namespace context is still
        //valid when parser is at the endElement state.
        
        //if (fBindNamespaces) {
        //  fNamespaceContext.popContext();
        //}
        
        // call handler
        if (fDocumentHandler != null ) {
            //xxx: Commenting this now since we are not passing any information
            //along the pipeline. However, we do need to set the values if we
            //need to pass the  values along the pipeline.
            
            //fDocumentHandler.endElement(fElementQName, null);
        }
        if(dtdGrammarUtil != null)
            dtdGrammarUtil.endElement(endElementName);
        
        fScanEndElement = true;
        return fMarkupDepth;
        
    } // scanEndElement():int
    
    //getNamespaceContext
    public NamespaceContext getNamespaceContext(){
        return fNamespaceContext ;
    }
    
    public void reset(XMLComponentManager componentManager)
    throws XMLConfigurationException {
        
        super.reset(componentManager);
        fPerformValidation = false;
        fBindNamespaces = false;
    }
    
    /** Creates a content driver. */
    protected Driver createContentDriver() {
        return new NSContentDriver();
    } // createContentDriver():Driver
    
    /**
     * Driver to handle content scanning.
     */
    protected final class NSContentDriver
    extends ContentDriver {
        /**
         * Scan for root element hook. This method is a hook for
         * subclasses to add code that handles scanning for the root
         * element. This method will also attempt to remove DTD validator
         * from the pipeline, if there is no DTD grammar. If DTD validator
         * is no longer in the pipeline bind namespaces in the scanner.
         *
         *
         * @return True if the caller should stop and return true which
         *          allows the scanner to switch to a new scanning
         *          driver. A return value of false indicates that
         *          the content driver should continue as normal.
         */
        protected boolean scanRootElementHook()
        throws IOException, XNIException {
            
            if (scanStartElement()) {
                setScannerState(SCANNER_STATE_TRAILING_MISC);
                setDriver(fTrailingMiscDriver);
                return true;
            }
            return false;
            
        } // scanRootElementHook():boolean
    }
    
    XMLString getString(){
        if(fAttributeCacheUsedCount < initialCacheCount || fAttributeCacheUsedCount < attributeValueCache.size()){
            return (XMLString)attributeValueCache.get(fAttributeCacheUsedCount++);
        }
        else{
            XMLString str = new XMLString();
            fAttributeCacheUsedCount++;
            attributeValueCache.add(str);
            return str;
        }
    }
    public XMLStringBuffer getDTDDecl(){
        Entity entity = fEntityScanner.getCurrentEntity();
        fDTDDecl.append(((Entity.ScannedEntity)entity).ch,fStartPos , fEndPos-fStartPos);
        if(fSeenInternalSubset)
            fDTDDecl.append("]>");
        return fDTDDecl;
    }
    
    public String getCharacterEncodingScheme(){
        return fDeclaredEncoding;
    }
    
       
} // class XMLNSDocumentScannerImpl
