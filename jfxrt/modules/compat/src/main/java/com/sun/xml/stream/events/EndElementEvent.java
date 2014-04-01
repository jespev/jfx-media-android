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
 * $Id: EndElementEvent.java,v 1.5 2010-11-02 21:02:28 joehw Exp $
 */
package com.sun.xml.stream.events ;

import java.util.List;
import java.util.ArrayList;

import javax.xml.namespace.QName;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.Namespace;
import java.io.Writer;
import java.util.Iterator;
import javax.xml.stream.events.XMLEvent;
import com.sun.xml.stream.util.ReadOnlyIterator;

/** Implementation of EndElement event.
 *
 * @author Neeraj Bajaj Sun Microsystems,Inc.
 * @author K.Venugopal Sun Microsystems,Inc.
 */

public class EndElementEvent extends DummyEvent
implements EndElement {
    
    List fNamespaces = null;
    QName fQName ;
    
    public EndElementEvent() {
        init();
    }
    
    protected void init() {
        setEventType(XMLEvent.END_ELEMENT);
        fNamespaces = new ArrayList();
    }
    
    
    public EndElementEvent(String prefix,  String uri, String localpart) {
        this(new QName(uri,localpart,prefix));
    }
    
    public EndElementEvent(QName qname) {
        this.fQName = qname;
        init();
    }
    
    public QName getName() {
        return fQName;
    }
    
    public void setName(QName qname) {
        this.fQName = qname;
    }
    
    
    protected void writeAsEncodedUnicodeEx(java.io.Writer writer) 
    throws java.io.IOException
    {
        writer.write("</");
        String prefix = fQName.getPrefix();
        if (prefix != null && prefix.length() > 0) {
            writer.write(prefix);
            writer.write(':');
        }
        writer.write(fQName.getLocalPart());
        writer.write('>');
    }    

    
    /** Returns an Iterator of namespaces that have gone out
     * of scope.  Returns an empty iterator if no namespaces have gone
     * out of scope.
     * @return an Iterator over Namespace interfaces, or an
     * empty iterator
     */
    public Iterator getNamespaces() {
        if(fNamespaces != null)
            fNamespaces.iterator();
        return new ReadOnlyIterator();
    }
    
    void addNamespace(Namespace attr){
        if(attr != null){
            fNamespaces.add(attr);
        }
    }
    
    public String toString() {
        String s = "</" + nameAsString();
        s = s + ">";
        return s;
    }
    
    public String nameAsString() {
        if("".equals(fQName.getNamespaceURI()))
            return fQName.getLocalPart();
        if(fQName.getPrefix() != null)
            return "['" + fQName.getNamespaceURI() + "']:" + fQName.getPrefix() + ":" + fQName.getLocalPart();
        else
            return "['" + fQName.getNamespaceURI() + "']:" + fQName.getLocalPart();
    }
    
}
