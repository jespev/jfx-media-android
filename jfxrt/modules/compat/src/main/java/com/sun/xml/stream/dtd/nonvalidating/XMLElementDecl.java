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
 * $Id: XMLElementDecl.java,v 1.4 2010-11-02 21:02:27 joehw Exp $
 */
package com.sun.xml.stream.dtd.nonvalidating;

import org.apache.xerces.xni.QName;
/**
 * @version $Id: XMLElementDecl.java,v 1.4 2010-11-02 21:02:27 joehw Exp $
 */
public class XMLElementDecl {
    
    /** TYPE_ANY */
    public static final short TYPE_ANY = 0;
    
    /** TYPE_EMPTY */
    public static final short TYPE_EMPTY = 1;
    
    /** TYPE_MIXED */
    public static final short TYPE_MIXED = 2;
    
    /** TYPE_CHILDREN */
    public static final short TYPE_CHILDREN = 3;
    
    /** TYPE_SIMPLE */
    public static final short TYPE_SIMPLE = 4;
    
    
    /** name */
    public final QName name = new QName();
    
    /** scope */
    public int scope = -1;
    
    /** type */
    public short type = -1;
    
    
    /** simpleType */
    public final XMLSimpleType simpleType = new XMLSimpleType();
    
    /**
     * setValues
     *
     * @param name
     * @param scope
     * @param type
     * @param contentModelValidator
     * @param simpleType
     */
    public void setValues(QName name, int scope, short type, XMLSimpleType simpleType) {
        this.name.setValues(name);
        this.scope                 = scope;
        this.type                  = type;
        this.simpleType.setValues(simpleType);
    } // setValues
    
    /**
     * clear
     */
    public void clear() {
        this.name.clear();
        this.type          = -1;
        this.scope         = -1;
        this.simpleType.clear();
    } // clear
    
} // class XMLElementDecl
