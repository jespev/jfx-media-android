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
 * $Id: PropertyManager.java,v 1.10 2010-11-02 21:02:26 joehw Exp $
 */
package com.sun.xml.stream;

import java.util.HashMap;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLResolver;

/**
 *  This class manages different properties related to Stax specification and its implementation.
 * This class constructor also takes itself (PropertyManager object) as parameter and initializes the
 * object with the property taken from the object passed.
 *
 * @author  Neeraj Bajaj, neeraj.bajaj@sun.com
 * @author K.Venugopal@sun.com
 */

public class PropertyManager {
    
    
    public static final String STAX_NOTATIONS = "javax.xml.stream.notations";
    public static final String STAX_ENTITIES = "javax.xml.stream.entities";
    
    private static final String STRING_INTERNING = "http://xml.org/sax/features/string-interning";
    
            
    HashMap supportedProps = new HashMap();
    
    public static final int CONTEXT_READER = 1;
    public static final int CONTEXT_WRITER = 2;
    
    /** Creates a new instance of PropertyManager */
    public PropertyManager(int context) {
        switch(context){
            case CONTEXT_READER:{
                initConfigurableReaderProperties();
                break;
            }
            case CONTEXT_WRITER:{
                initWriterProps();
                break;
            }
        }
    }
    
    /**
     * Initialize this object with the properties taken from passed PropertyManager object.
     */
    public PropertyManager(PropertyManager propertyManager){
        
        HashMap properties = propertyManager.getProperties();
        supportedProps.putAll(properties);
    }
    
    private HashMap getProperties(){
        return supportedProps ;
    }
    
    
    /**
     * Important point:
     * 1. We are not exposing Xerces namespace property. Application should configure namespace through
     * Stax specific property.
     *
     */
    private void initConfigurableReaderProperties(){
        //spec default values
        supportedProps.put(XMLInputFactory.IS_NAMESPACE_AWARE, Boolean.TRUE);
        supportedProps.put(XMLInputFactory.IS_VALIDATING, Boolean.FALSE);
        supportedProps.put(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES, Boolean.TRUE);
        supportedProps.put(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, Boolean.TRUE);
        supportedProps.put(XMLInputFactory.IS_COALESCING, Boolean.FALSE);
        supportedProps.put(XMLInputFactory.SUPPORT_DTD, Boolean.TRUE);
        supportedProps.put(XMLInputFactory.REPORTER, null);
        supportedProps.put(XMLInputFactory.RESOLVER, null);
        supportedProps.put(XMLInputFactory.ALLOCATOR, null);
        supportedProps.put(STAX_NOTATIONS,null );
        
        //zephyr (implementation) specific properties which can be set by the application.
        //interning is always done
        supportedProps.put(Constants.SAX_FEATURE_PREFIX + Constants.STRING_INTERNING_FEATURE , Boolean.TRUE);
        //recognizing java encoding names by default
        supportedProps.put(Constants.XERCES_FEATURE_PREFIX + Constants.ALLOW_JAVA_ENCODINGS_FEATURE,  Boolean.TRUE) ;
        supportedProps.put(Constants.REUSE_INSTANCE, Boolean.FALSE);
        supportedProps.put(Constants.ZEPHYR_PROPERTY_PREFIX + Constants.STAX_REPORT_CDATA_EVENT , Boolean.FALSE);
        supportedProps.put(Constants.ZEPHYR_PROPERTY_PREFIX + Constants.IGNORE_EXTERNAL_DTD, Boolean.FALSE);
        supportedProps.put(Constants.XERCES_FEATURE_PREFIX + Constants.WARN_ON_DUPLICATE_ATTDEF_FEATURE, Boolean.FALSE);
        supportedProps.put(Constants.XERCES_FEATURE_PREFIX + Constants.WARN_ON_DUPLICATE_ENTITYDEF_FEATURE, Boolean.FALSE);
        supportedProps.put(Constants.XERCES_FEATURE_PREFIX + Constants.WARN_ON_UNDECLARED_ELEMDEF_FEATURE, Boolean.FALSE);
        supportedProps.put(Constants.ZEPHYR_PROPERTY_PREFIX + Constants.IMPLEMENTATION_NAME, "sjsxp");        
    }
    
    private void initWriterProps(){
        supportedProps.put(XMLOutputFactory.IS_REPAIRING_NAMESPACES , Boolean.FALSE);
        //default value of escaping characters is 'true'
        supportedProps.put(Constants.ESCAPE_CHARACTERS , Boolean.TRUE);
        supportedProps.put(Constants.REUSE_INSTANCE, Boolean.FALSE);
        supportedProps.put(Constants.ZEPHYR_PROPERTY_PREFIX + Constants.IMPLEMENTATION_NAME, "sjsxp");        
        supportedProps.put(Constants.ZEPHYR_PROPERTY_PREFIX + Constants.OUTPUTSTREAM, null);        
    }
    
    /**
     * public void reset(){
     * supportedProps.clear() ;
     * }
     */
    public boolean containsProperty(String property){
        return supportedProps.containsKey(property) ;
    }
    
    public Object getProperty(String property) {
        return supportedProps.get(property);
    }
    
    public void setProperty(String property, Object value){
        String equivalentProperty = null ;
        if(property == XMLInputFactory.IS_NAMESPACE_AWARE || property.equals(XMLInputFactory.IS_NAMESPACE_AWARE)){
            equivalentProperty = Constants.XERCES_FEATURE_PREFIX + Constants.NAMESPACES_FEATURE ;
        }
        else if(property == XMLInputFactory.IS_VALIDATING || property.equals(XMLInputFactory.IS_VALIDATING)){
            if( (value instanceof Boolean) && ((Boolean)value).booleanValue()){
                throw new java.lang.IllegalArgumentException("true value of isValidating not supported") ;
            }
        }
        else if(property == STRING_INTERNING || property.equals(STRING_INTERNING)){
            if( (value instanceof Boolean) && !((Boolean)value).booleanValue()){
                throw new java.lang.IllegalArgumentException("false value of " + STRING_INTERNING + "feature is not supported") ;
            }
        }
        else if(property == XMLInputFactory.RESOLVER || property.equals(XMLInputFactory.RESOLVER)){
            //add internal stax property
            supportedProps.put( Constants.XERCES_PROPERTY_PREFIX + Constants.STAX_ENTITY_RESOLVER_PROPERTY , new StaxEntityResolverWrapper((XMLResolver)value)) ;
        }
        supportedProps.put(property, value ) ;
        if(equivalentProperty != null){
            supportedProps.put(equivalentProperty, value ) ;
        }
    }
    
    public String toString(){
        return supportedProps.toString();
    }
    
}//PropertyManager
