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
 * $Id: AttributeImpl.java,v 1.5 2010-11-02 21:02:28 joehw Exp $
 */
package com.sun.xml.stream.events;

import javax.xml.namespace.QName;
import javax.xml.stream.events.Attribute;
import java.io.Writer;
import javax.xml.stream.events.XMLEvent;


//xxx: AttributeEvent is not really a first order event. Should we be renaming the class to AttributeImpl for consistent
//naming convention.

/**
 * Implementation of Attribute Event.
 *
 *@author Neeraj Bajaj, Sun Microsystems
 *@author K.Venugopal, Sun Microsystems
 *
 */

public class AttributeImpl extends DummyEvent implements Attribute

{
    //attribute value
    private String fValue;
    private String fNonNormalizedvalue;
    
    //name of the attribute
    private QName fQName;
    //attribute type
    private String fAttributeType = "CDATA";
    
    
    //A flag indicating whether this attribute was actually specified in the start-tag
    //of its element or was defaulted from the schema.
    private boolean fIsSpecified;
    
    public AttributeImpl(){
        init();
    }
    public AttributeImpl(String name, String value) {
        init();
        fQName = new QName(name);
        fValue = value;
    }
    
    public AttributeImpl(String prefix, String name, String value) {
        this(prefix, null,name, value, null,null,false );
    }
    
    public AttributeImpl(String prefix, String uri, String localPart, String value, String type) {
        this(prefix, uri, localPart, value, null, type, false);
    }
    
    public AttributeImpl(String prefix, String uri, String localPart, String value, String nonNormalizedvalue, String type, boolean isSpecified) {
        this(new QName(uri, localPart, prefix), value, nonNormalizedvalue, type, isSpecified);
    }
    
    
    public AttributeImpl(QName qname, String value, String nonNormalizedvalue, String type, boolean isSpecified) {
        init();
        fQName = qname ;
        fValue = value ;
        if(type != null && !type.equals(""))
            fAttributeType = type;
        
        fNonNormalizedvalue = nonNormalizedvalue;
        fIsSpecified = isSpecified ;
        
    }
    
    public String toString() {
        if( fQName.getPrefix() != null && fQName.getPrefix().length() > 0 )
            return fQName.getPrefix() + ":" + fQName.getLocalPart() + "='" + fValue + "'";
        else
            return fQName.getLocalPart() + "='" + fValue + "'";
    }
    
    public void setName(QName name){
        fQName = name ;
    }
    
    public QName getName() {
        return fQName;
    }
    
    public void setValue(String value){
        fValue = value;
    }
    
    public String getValue() {
        return fValue;
    }
    
    public void setNonNormalizedValue(String nonNormalizedvalue){
        fNonNormalizedvalue = nonNormalizedvalue;
    }
    
    public String getNonNormalizedValue(){
        return fNonNormalizedvalue ;
    }
    
    public void setAttributeType(String attributeType){
        fAttributeType = attributeType ;
    }
    
    /** Gets the type of this attribute, default is "CDATA   */
    // We dont need to take care of default value.. implementation takes care of it.
    public String getDTDType() {
        return fAttributeType;
    }
    
    /** is this attribute is specified in the instance document */
    
    public void setSpecified(boolean isSpecified){
        fIsSpecified = isSpecified ;
    }
    
    public boolean isSpecified() {
        return fIsSpecified ;
    }
    
    protected void writeAsEncodedUnicodeEx(java.io.Writer writer) 
    throws java.io.IOException
    {
        writer.write(toString());
    }
    
    protected void init(){
        setEventType(XMLEvent.ATTRIBUTE);
    }
    
    
    
    
}//AttributeImpl

