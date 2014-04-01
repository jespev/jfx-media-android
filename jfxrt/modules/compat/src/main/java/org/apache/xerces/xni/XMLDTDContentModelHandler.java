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
 * The DTD content model handler interface defines callback methods 
 * to report information items in DTD content models of an element
 * declaration. Parser components interested in DTD content model
 * information implement this interface and are registered as the DTD
 * content model handler on the DTD content model source.
 *
 * @see XMLDTDHandler
 *
 * @author Andy Clark, IBM
 *
 * @version $Id: XMLDTDContentModelHandler.java,v 1.4 2010-11-02 21:02:32 joehw Exp $
 */
public interface XMLDTDContentModelHandler {

    //
    // Constants
    //

    // separators

    /** 
     * A choice separator for children and mixed content models. This
     * separator is used to specify that the allowed child is one of a
     * collection.
     * <p>
     * For example:
     * <pre>
     * &lt;!ELEMENT elem (foo|bar)&gt;
     * &lt;!ELEMENT elem (foo|bar+)&gt;
     * &lt;!ELEMENT elem (foo|bar|baz)&gt;
     * &lt;!ELEMENT elem (#PCDATA|foo|bar)*&gt;
     * </pre>
     *
     * @see #SEPARATOR_SEQUENCE
     */
    public static final short SEPARATOR_CHOICE = 0;

    /**
     * A sequence separator for children content models. This separator 
     * is used to specify that the allowed children must follow in the
     * specified sequence.
     * <p>
     * <pre>
     * &lt;!ELEMENT elem (foo,bar)&gt;
     * &lt;!ELEMENT elem (foo,bar*)&gt;
     * &lt;!ELEMENT elem (foo,bar,baz)&gt;
     * </pre>
     *
     * @see #SEPARATOR_CHOICE
     */
    public static final short SEPARATOR_SEQUENCE = 1;

    // occurrence counts

    /** 
     * This occurrence count limits the element, choice, or sequence in a
     * children content model to zero or one. In other words, the child
     * is optional.
     * <p>
     * For example:
     * <pre>
     * &lt;!ELEMENT elem (foo?)&gt;
     * </pre>
     *
     * @see #OCCURS_ZERO_OR_MORE
     * @see #OCCURS_ONE_OR_MORE
     */
    public static final short OCCURS_ZERO_OR_ONE = 2;

    /** 
     * This occurrence count limits the element, choice, or sequence in a
     * children content model to zero or more. In other words, the child
     * may appear an arbitrary number of times, or not at all. This
     * occurrence count is also used for mixed content models.
     * <p>
     * For example:
     * <pre>
     * &lt;!ELEMENT elem (foo*)&gt;
     * &lt;!ELEMENT elem (#PCDATA|foo|bar)*&gt;
     * </pre>
     *
     * @see #OCCURS_ZERO_OR_ONE
     * @see #OCCURS_ONE_OR_MORE
     */
    public static final short OCCURS_ZERO_OR_MORE = 3;

    /** 
     * This occurrence count limits the element, choice, or sequence in a
     * children content model to one or more. In other words, the child
     * may appear an arbitrary number of times, but must appear at least
     * once.
     * <p>
     * For example:
     * <pre>
     * &lt;!ELEMENT elem (foo+)&gt;
     * </pre>
     *
     * @see #OCCURS_ZERO_OR_ONE
     * @see #OCCURS_ZERO_OR_MORE
     */
    public static final short OCCURS_ONE_OR_MORE = 4;

    //
    // XMLDTDContentModelHandler methods
    //

    /**
     * The start of a content model. Depending on the type of the content
     * model, specific methods may be called between the call to the
     * startContentModel method and the call to the endContentModel method.
     * 
     * @param elementName The name of the element.
     * @param augmentations Additional information that may include infoset
     *                      augmentations.
     *
     * @throws XNIException Thrown by handler to signal an error.
     */
    public void startContentModel(String elementName, Augmentations augmentations)
        throws XNIException;

    /** 
     * A content model of ANY. 
     *
     * @param augmentations Additional information that may include infoset
     *                      augmentations.
     *
     * @throws XNIException Thrown by handler to signal an error.
     *
     * @see #empty
     * @see #startGroup
     */
    public void any(Augmentations augmentations) throws XNIException;

    /**
     * A content model of EMPTY.
     *
     * @throws XNIException Thrown by handler to signal an error.
     *
     * @param augmentations Additional information that may include infoset
     *                      augmentations.
     *
     * @see #any
     * @see #startGroup
     */
    public void empty(Augmentations augmentations) throws XNIException;

    /**
     * A start of either a mixed or children content model. A mixed
     * content model will immediately be followed by a call to the
     * <code>pcdata()</code> method. A children content model will
     * contain additional groups and/or elements.
     *
     * @param augmentations Additional information that may include infoset
     *                      augmentations.
     *
     * @throws XNIException Thrown by handler to signal an error.
     *
     * @see #any
     * @see #empty
     */
    public void startGroup(Augmentations augmentations) throws XNIException;

    /**
     * The appearance of "#PCDATA" within a group signifying a
     * mixed content model. This method will be the first called
     * following the content model's <code>startGroup()</code>.
     *
     * @param augmentations Additional information that may include infoset
     *                      augmentations.
     *     
     * @throws XNIException Thrown by handler to signal an error.
     *
     * @see #startGroup
     */
    public void pcdata(Augmentations augmentations) throws XNIException;

    /**
     * A referenced element in a mixed or children content model.
     * 
     * @param elementName The name of the referenced element.
     * @param augmentations Additional information that may include infoset
     *                      augmentations.
     *
     * @throws XNIException Thrown by handler to signal an error.
     */
    public void element(String elementName, Augmentations augmentations) 
        throws XNIException;

    /**
     * The separator between choices or sequences of a mixed or children
     * content model.
     * 
     * @param separator The type of children separator.
     * @param augmentations Additional information that may include infoset
     *                      augmentations.
     *
     * @throws XNIException Thrown by handler to signal an error.
     *
     * @see #SEPARATOR_CHOICE
     * @see #SEPARATOR_SEQUENCE
     */
    public void separator(short separator, Augmentations augmentations) 
        throws XNIException;

    /**
     * The occurrence count for a child in a children content model or
     * for the mixed content model group.
     * 
     * @param occurrence The occurrence count for the last element
     *                   or group.
     * @param augmentations Additional information that may include infoset
     *                      augmentations.
     *
     * @throws XNIException Thrown by handler to signal an error.
     *
     * @see #OCCURS_ZERO_OR_ONE
     * @see #OCCURS_ZERO_OR_MORE
     * @see #OCCURS_ONE_OR_MORE
     */
    public void occurrence(short occurrence, Augmentations augmentations) 
        throws XNIException;

    /**
     * The end of a group for mixed or children content models.
     *
     * @param augmentations Additional information that may include infoset
     *                      augmentations.
     *
     * @throws XNIException Thrown by handler to signal an error.
     */
    public void endGroup(Augmentations augmentations) throws XNIException;

    /**
     * The end of a content model.
     *
     * @param augmentations Additional information that may include infoset
     *                      augmentations.
     *
     * @throws XNIException Thrown by handler to signal an error.
     */
    public void endContentModel(Augmentations augmentations) throws XNIException;

} // interface XMLDTDContentModelHandler
