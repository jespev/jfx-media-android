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
 * $Id: StartElementEvent.java,v 1.5 2010-11-02 21:02:28 joehw Exp $
 */
package com.sun.xml.stream.events ;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collection;

import javax.xml.namespace.QName;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.Attribute;
import javax.xml.namespace.NamespaceContext;
import java.io.Writer;
import com.sun.xml.stream.util.ReadOnlyIterator;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.events.Namespace;

/** Implementation of StartElementEvent.
 *
 * @author Neeraj Bajaj Sun Microsystems,Inc.
 * @author K.Venugopal Sun Microsystems,Inc.
 */

public class StartElementEvent extends DummyEvent
implements StartElement {
    
    private Map fAttributes;
    private List fNamespaces;
    private NamespaceContext fNamespaceContext = null;
    private QName fQName;
        
    public StartElementEvent(String prefix, String uri, String localpart) {
        this(new QName(uri, localpart, prefix));
    }
    
    public StartElementEvent(QName qname) {
        fQName = qname;
        init();
    }
    
    public StartElementEvent(StartElement startelement) {
        this(startelement.getName());
        addAttributes(startelement.getAttributes());
        addNamespaceAttributes(startelement.getNamespaces());
    }
    
    protected void init() {
        setEventType(XMLStreamConstants.START_ELEMENT);
        fAttributes = new HashMap();
        fNamespaces = new ArrayList();
    }
    
    public QName getName() {
        return fQName;
    }
    
    public void setName(QName qname) {
        this.fQName = qname;
    }
    
    public Iterator getAttributes() {
        if(fAttributes != null){
            Collection coll = fAttributes.values();
            return new ReadOnlyIterator(coll.iterator());
        }
        return new ReadOnlyIterator();
    }
    
    public Iterator getNamespaces() {
        if(fNamespaces != null){
            return new ReadOnlyIterator(fNamespaces.iterator());
        }
        return new ReadOnlyIterator();
    }
    
    public Attribute getAttributeByName(QName qname) {
        if(qname == null)
            return null;
        return (Attribute)fAttributes.get(qname);
    }
    
    public String getNamespace(){
        return fQName.getNamespaceURI();
    }
    
    public String getNamespaceURI(String prefix) {
        //check that URI was supplied when creating this startElement event and prefix matches 
        if( getNamespace() != null && fQName.getPrefix().equals(prefix)) return getNamespace();
        //else check the namespace context
        if(fNamespaceContext != null)
            return fNamespaceContext.getNamespaceURI(prefix);
        return null;
    }
    
    public String toString() {
        String s = "<" + nameAsString();
        
        if(fAttributes != null){
            Iterator it = this.getAttributes();
            Attribute attr = null;
            while(it.hasNext()){
                attr = (Attribute)it.next();
                s = s + " " + attr.toString();
            }
        }
        
        if(fNamespaces != null){
            Iterator it = fNamespaces.iterator();
            Namespace attr = null;
            while(it.hasNext()){
                attr = (Namespace)it.next();
                s = s + " " + attr.toString();
            }
        }
        s = s + ">";
        return s;
    }
    
    /** Return this event as String
     * @return String Event returned as string.
     */
    public String nameAsString() {
        if("".equals(fQName.getNamespaceURI()))
            return fQName.getLocalPart();
        if(fQName.getPrefix() != null)
            return "['" + fQName.getNamespaceURI() + "']:" + fQName.getPrefix() + ":" + fQName.getLocalPart();
        else
            return "['" + fQName.getNamespaceURI() + "']:" + fQName.getLocalPart();
    }
    
    
    /** Gets a read-only namespace context. If no context is
     * available this method will return an empty namespace context.
     * The NamespaceContext contains information about all namespaces
     * in scope for this StartElement.
     *
     * @return the current namespace context
     */
    public NamespaceContext getNamespaceContext() {
        return fNamespaceContext;
    }
    
    public void setNamespaceContext(NamespaceContext nc) {
        fNamespaceContext = nc;
    }
    
    protected void writeAsEncodedUnicodeEx(java.io.Writer writer) 
    throws java.io.IOException
    {
        writer.write(toString());
    }
    
    void addAttribute(Attribute attr){
        if(attr.isNamespace()){
            fNamespaces.add(attr);
        }else{
            fAttributes.put(attr.getName(),attr);
        }
    }
    
    void addAttributes(Iterator attrs){
        if(attrs == null)
            return;
        while(attrs.hasNext()){
            Attribute attr = (Attribute)attrs.next();
            fAttributes.put(attr.getName(),attr);
        }
    }
    
    void addNamespaceAttribute(Namespace attr){
        if(attr == null)
            return;
        fNamespaces.add(attr);
    }
    
    void addNamespaceAttributes(Iterator attrs){
        if(attrs == null)
            return;
        while(attrs.hasNext()){
            Namespace attr = (Namespace)attrs.next();
            fNamespaces.add(attr);
        }
    }
    
}
