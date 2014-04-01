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

package org.apache.xerces.xni.parser;

import java.io.IOException;

import org.apache.xerces.xni.XNIException;

/**
 * This interface defines a generic DTD scanner. This interface
 * allows a scanner to be used interchangably in existing parser
 * configurations.
 * <p>
 * If the parser configuration uses a DTD scanner that implements
 * this interface, components should be able to query the scanner
 * instance from the component manager using the following property 
 * identifier:
 * <blockquote>
 *  "http://apache.org/xml/properties/internal/dtd-scanner"
 * </blockquote>
 *
 * @author Andy Clark, IBM
 *
 * @version $Id: XMLDTDScanner.java,v 1.4 2010-11-02 21:02:32 joehw Exp $
 */
public interface XMLDTDScanner 
    extends XMLDTDSource, XMLDTDContentModelSource {

    //
    // XMLDTDScanner methods
    //

    /** 
     * Sets the input source. 
     *
     * @param inputSource The input source or null.
     *
     * @throws IOException Thrown on i/o error.
     */
    public void setInputSource(XMLInputSource inputSource) throws IOException;

    /** 
     * Scans the internal subset of the document.
     *
     * @param complete True if the scanner should scan the document
     *                 completely, pushing all events to the registered
     *                 document handler. A value of false indicates that
     *                 that the scanner should only scan the next portion
     *                 of the document and return. A scanner instance is
     *                 permitted to completely scan a document if it does
     *                 not support this "pull" scanning model.
     * @param standalone True if the document was specified as standalone.
     *                   This value is important for verifying certain
     *                   well-formedness constraints.
     * @param hasExternalDTD True if the document has an external DTD.
     *                       This allows the scanner to properly notify
     *                       the handler of the end of the DTD in the
     *                       absence of an external subset.
     *
     * @returns True if there is more to scan, false otherwise.
     */
    public boolean scanDTDInternalSubset(boolean complete, boolean standalone,
                                         boolean hasExternalSubset)
        throws IOException, XNIException;

    /**
     * Scans the external subset of the document.
     *
     * @param complete True if the scanner should scan the document
     *                 completely, pushing all events to the registered
     *                 document handler. A value of false indicates that
     *                 that the scanner should only scan the next portion
     *                 of the document and return. A scanner instance is
     *                 permitted to completely scan a document if it does
     *                 not support this "pull" scanning model.
     *
     * @returns True if there is more to scan, false otherwise.
     */
    public boolean scanDTDExternalSubset(boolean complete) 
        throws IOException, XNIException;

} // interface XMLDTDScanner
