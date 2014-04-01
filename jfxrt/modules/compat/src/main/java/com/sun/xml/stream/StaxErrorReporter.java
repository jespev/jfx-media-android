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
 * $Id: StaxErrorReporter.java,v 1.4 2010-11-02 21:02:26 joehw Exp $ 
 */
package com.sun.xml.stream;



import javax.xml.stream.Location;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLReporter;
import javax.xml.stream.XMLStreamException;
import org.apache.xerces.impl.msg.XMLMessageFormatter;
import org.apache.xerces.util.MessageFormatter;
import org.apache.xerces.xni.XMLLocator;
import org.apache.xerces.xni.XNIException;

/**
 *
 * @author  neeraj
 */

public class StaxErrorReporter extends XMLErrorReporter {
    
    protected XMLReporter fXMLReporter = null ;
    
    /** Creates a new instance of StaxErrorReporter */
    public StaxErrorReporter(PropertyManager propertyManager) {
        super();
        putMessageFormatter(XMLMessageFormatter.XML_DOMAIN, new XMLMessageFormatter());
        reset(propertyManager);
    }
    
    /** Creates a new instance of StaxErrorReporter
     * If this constructor is used to create the object, one must invoke reset() on this object.
     */
    public StaxErrorReporter() {
        super();
        putMessageFormatter(XMLMessageFormatter.XML_DOMAIN, new XMLMessageFormatter());
    }
    
    /**
     *One must call reset before using any of the function.
     */
    public void reset(PropertyManager propertyManager){
        fXMLReporter = (XMLReporter)propertyManager.getProperty(XMLInputFactory.REPORTER);
    }
    /**
     * Reports an error at a specific location.
     *
     * @param location  The error location.
     * @param domain    The error domain.
     * @param key       The key of the error message.
     * @param arguments The replacement arguments for the error message,
     *                  if needed.
     * @param severity  The severity of the error.
     *
     * @see #SEVERITY_WARNING
     * @see #SEVERITY_ERROR
     * @see #SEVERITY_FATAL_ERROR
     */
    public void reportError(XMLLocator location,
    String domain, String key, Object[] arguments,
    short severity) throws XNIException {
        // format error message and create parse exception
        MessageFormatter messageFormatter = getMessageFormatter(domain);
        String message;
        if (messageFormatter != null) {
            message = messageFormatter.formatMessage(fLocale, key, arguments);
        }
        else {
            StringBuffer str = new StringBuffer();
            str.append(domain);
            str.append('#');
            str.append(key);
            int argCount = arguments != null ? arguments.length : 0;
            if (argCount > 0) {
                str.append('?');
                for (int i = 0; i < argCount; i++) {
                    str.append(arguments[i]);
                    if (i < argCount -1) {
                        str.append('&');
                    }
                }
            }
            message = str.toString();
        }
        
        
        
        //no reporter was specified
        /**
         * if (reporter == null) {
         * reporter = new DefaultStaxErrorReporter();
         * }
         */
        
        // call error handler
        switch (severity) {
            case SEVERITY_WARNING: {
                try{
                    if(fXMLReporter!= null){
                        fXMLReporter.report(message, "WARNING", null, convertToStaxLocation(location) );
                    }
                }catch(XMLStreamException ex){
                    //what we should be doing ?? if the user throws XMLStreamException
                    //REVISIT:
                    throw new XNIException(ex);
                }
                break;
            }
            case SEVERITY_ERROR: {
                try{
                    if(fXMLReporter!= null){
                        fXMLReporter.report(message, "ERROR", null, convertToStaxLocation(location) );
                    }
                }catch(XMLStreamException ex){
                    //what we should be doing ?? if the user throws XMLStreamException
                    //REVISIT:
                    throw new XNIException(ex);
                }
                break;
            }
            case SEVERITY_FATAL_ERROR: {
                if (!fContinueAfterFatalError) {
                    throw new XNIException(message);
                }
                break;
            }
        }
        
    }
    
    
    Location convertToStaxLocation(final XMLLocator location){
        return new Location(){
            public int getColumnNumber(){
                return location.getColumnNumber();
            }
            
            public int getLineNumber(){
                return location.getLineNumber();
            }
            
            public String getPublicId(){
                return location.getPublicId();
            }
            
            public String getSystemId(){
                return location.getLiteralSystemId();
            }
            
            public int getCharacterOffset(){
                return location.getCharacterOffset();
            }
            
        };
    }
    
}
