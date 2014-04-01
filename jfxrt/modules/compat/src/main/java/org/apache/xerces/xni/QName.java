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

package org.apache.xerces.xni;

/**
 * A structure that holds the components of an XML Namespaces qualified
 * name.
 * <p>
 * To be used correctly, the strings must be identical references for
 * equal strings. Within the parser, these values are considered symbols
 * and should always be retrieved from the <code>SymbolTable</code>.
 *
 * @see <a href="../../../../../xerces2/org/apache/xerces/util/SymbolTable.html">org.apache.xerces.util.SymbolTable</a>
 *
 * @author Andy Clark, IBM
 *
 * @version $Id: QName.java,v 1.4 2010-11-02 21:02:32 joehw Exp $
 */
public class QName
implements Cloneable {
    
    //rawname
    public char [] characters = null;
    //
    // Data
    //
    
    /**
     * The qname prefix. For example, the prefix for the qname "a:foo"
     * is "a".
     */
    public String prefix;
    
    /**
     * The qname localpart. For example, the localpart for the qname "a:foo"
     * is "foo".
     */
    public String localpart;
    
    /**
     * The qname rawname. For example, the rawname for the qname "a:foo"
     * is "a:foo".
     */
    public String rawname;
    
    /**
     * The URI to which the qname prefix is bound. This binding must be
     * performed by a XML Namespaces aware processor.
     */
    public String uri;
    
    //
    // Constructors
    //
    
    /** Default constructor. */
    public QName() {
        clear();
    } // <init>()
    
    /** Constructs a QName with the specified values. */
    public QName(String prefix, String localpart, String rawname, String uri) {
        setValues(prefix, localpart, rawname, uri);
    } // <init>(String,String,String,String)
    
    /** Constructs a copy of the specified QName. */
    public QName(QName qname) {
        setValues(qname);
    } // <init>(QName)
    
    //
    // Public methods
    //
    
    /**
     * Convenience method to set the values of the qname components.
     *
     * @param QName The qualified name to be copied.
     */
    public void setValues(QName qname) {
        prefix = qname.prefix;
        localpart = qname.localpart;
        rawname = qname.rawname;
        uri = qname.uri;
        characters=qname.characters;
    } // setValues(QName)
    
    /**
     * Convenience method to set the values of the qname components.
     *
     * @param prefix    The qname prefix. (e.g. "a")
     * @param localpart The qname localpart. (e.g. "foo")
     * @param rawname   The qname rawname. (e.g. "a:foo")
     * @param uri       The URI binding. (e.g. "http://foo.com/mybinding")
     */
    public void setValues(String prefix, String localpart, String rawname,
    String uri) {
        this.prefix = prefix;
        this.localpart = localpart;
        this.rawname = rawname;
        this.uri = uri;
    } // setValues(String,String,String,String)
    
    /** Clears the values of the qname components. */
    public void clear() {
        prefix = null;
        localpart = null;
        rawname = null;
        uri = null;
    } // clear()
    
    //
    // Cloneable methods
    //
    
    /** Returns a clone of this object. */
    public Object clone() {
        return new QName(this);
    } // clone():Object
    
    //
    // Object methods
    //
    
    /** Returns the hashcode for this object. */
    public int hashCode() {
        if (uri != null) {
            return uri.hashCode() + localpart.hashCode();
        }
        return rawname.hashCode();
    } // hashCode():int
    
    /** Returns true if the two objects are equal. */
    public boolean equals(Object object) {
        if (object instanceof QName) {
            QName qname = (QName)object;
            if (qname.uri != null) {
                return uri == qname.uri && localpart == qname.localpart;
            }
            else if (uri == null) {
                return rawname == qname.rawname;
            }
            // fall through and return not equal
        }
        return false;
    } // equals(Object):boolean
    
    /** Returns a string representation of this object. */
    public String toString() {
        
        StringBuffer str = new StringBuffer();
        boolean comma = false;
        if (prefix != null) {
            str.append("prefix=\""+prefix+'"');
            comma = true;
        }
        if (localpart != null) {
            if (comma) {
                str.append(',');
            }
            str.append("localpart=\""+localpart+'"');
            comma = true;
        }
        if (rawname != null) {
            if (comma) {
                str.append(',');
            }
            str.append("rawname=\""+rawname+'"');
            comma = true;
        }
        if (uri != null) {
            if (comma) {
                str.append(',');
            }
            str.append("uri=\""+uri+'"');
        }
        return str.toString();
        
    } // toString():String
    
} // class QName
