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

package org.apache.xerces.util;

import org.apache.xerces.xni.XMLResourceIdentifier;

/**
 * The XMLResourceIdentifierImpl class is an implementation of the 
 * XMLResourceIdentifier interface which defines the location identity
 * of a resource.
 *
 * @author Andy Clark 
 *
 * @version $Id: XMLResourceIdentifierImpl.java,v 1.4 2010-11-02 21:02:31 joehw Exp $
 */
public class XMLResourceIdentifierImpl
    implements XMLResourceIdentifier {

    //
    // Data
    //

    /** The public identifier. */
    protected String fPublicId;

    /** The literal system identifier. */
    protected String fLiteralSystemId;

    /** The base system identifier. */
    protected String fBaseSystemId;

    /** The expanded system identifier. */
    protected String fExpandedSystemId;

    //
    // Constructors
    //

    /** Constructs an empty resource identifier. */
    public XMLResourceIdentifierImpl() {} // <init>()

    /**
     * Constructs a resource identifier.
     *
     * @param publicId The public identifier.
     * @param literalSystemId The literal system identifier.
     * @param baseSystemId The base system identifier.
     * @param expandedSystemId The expanded system identifier.
     */
    public XMLResourceIdentifierImpl(String publicId,
                                     String literalSystemId, String baseSystemId,
                                     String expandedSystemId) {
        setValues(publicId, literalSystemId, baseSystemId, expandedSystemId);
    } // <init>(String,String,String,String)

    //
    // Public methods
    //

    /** Sets the values of the resource identifier. */
    public void setValues(String publicId, String literalSystemId,
                          String baseSystemId, String expandedSystemId) {
        fPublicId = publicId;
        fLiteralSystemId = literalSystemId;
        fBaseSystemId = baseSystemId;
        fExpandedSystemId = expandedSystemId;
    } // setValues(String,String,String,String)

    /** Clears the values. */
    public void clear() {
        fPublicId = null;
        fLiteralSystemId = null;
        fBaseSystemId = null;
        fExpandedSystemId = null;
    } // clear()

    /** Sets the public identifier. */
    public void setPublicId(String publicId) {
        fPublicId = publicId;
    } // setPublicId(String)

    /** Sets the literal system identifier. */
    public void setLiteralSystemId(String literalSystemId) {
        fLiteralSystemId = literalSystemId;
    } // setLiteralSystemId(String)

    /** Sets the base system identifier. */
    public void setBaseSystemId(String baseSystemId) {
        fBaseSystemId = baseSystemId;
    } // setBaseSystemId(String)

    /** Sets the expanded system identifier. */
    public void setExpandedSystemId(String expandedSystemId) {
        fExpandedSystemId = expandedSystemId;
    } // setExpandedSystemId(String)

    //
    // XMLResourceIdentifier methods
    //

    /** Returns the public identifier. */
    public String getPublicId() {
        return fPublicId;
    } // getPublicId():String

    /** Returns the literal system identifier. */
    public String getLiteralSystemId() {
        return fLiteralSystemId;
    } // getLiteralSystemId():String

    /** 
     * Returns the base URI against which the literal SystemId is to be resolved.
     */
    public String getBaseSystemId() {
        return fBaseSystemId;
    } // getBaseSystemId():String

    /** Returns the expanded system identifier. */
    public String getExpandedSystemId() {
        return fExpandedSystemId;
    } // getExpandedSystemId():String

    //
    // Object methods
    //

    /** Returns a hash code for this object. */
    public int hashCode() {
        int code = 0;
        if (fPublicId != null) {
            code += fPublicId.hashCode();
        }
        if (fLiteralSystemId != null) {
            code += fLiteralSystemId.hashCode();
        }
        if (fBaseSystemId != null) {
            code += fBaseSystemId.hashCode();
        }
        if (fExpandedSystemId != null) {
            code += fExpandedSystemId.hashCode();
        }
        return code;
    } // hashCode():int

    /** Returns a string representation of this object. */
    public String toString() {
        StringBuffer str = new StringBuffer();
        if (fPublicId != null) {
            str.append(fPublicId);
        }
        str.append(':');
        if (fLiteralSystemId != null) {
            str.append(fLiteralSystemId);
        }
        str.append(':');
        if (fBaseSystemId != null) {
            str.append(fBaseSystemId);
        }
        str.append(':');
        if (fExpandedSystemId != null) {
            str.append(fExpandedSystemId);
        }
        return str.toString();
    } // toString():String

} // class XMLAttributesImpl
