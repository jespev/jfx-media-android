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

import java.util.Hashtable;
import java.util.Vector;

import org.apache.xerces.xni.parser.XMLComponentManager;
import org.apache.xerces.xni.parser.XMLConfigurationException;

/**
 * This class implements the basic operations for managing parser
 * configuration features and properties. This utility class can
 * be used as a base class for parser configurations or separately
 * to encapsulate a number of parser settings as a component
 * manager.
 * <p>
 * This class can be constructed with a "parent" settings object
 * (in the form of an <code>XMLComponentManager</code>) that allows
 * parser configuration settings to be "chained" together.
 *
 * @author Andy Clark, IBM
 *
 * @version $Id: ParserConfigurationSettings.java,v 1.4 2010-11-02 21:02:30 joehw Exp $
 */
public class ParserConfigurationSettings
    implements XMLComponentManager {

    //
    // Data
    //

    // data

    /** Recognized properties. */
    protected Vector fRecognizedProperties;

    /** Properties. */
    protected Hashtable fProperties;

    /** Recognized features. */
    protected Vector fRecognizedFeatures;

    /** Features. */
    protected Hashtable fFeatures;

    /** Parent parser configuration settings. */
    protected XMLComponentManager fParentSettings;

    //
    // Constructors
    //

    /** Default Constructor. */
    public ParserConfigurationSettings() {
        this(null);
    } // <init>()

    /**
     * Constructs a parser configuration settings object with a
     * parent settings object.
     */
    public ParserConfigurationSettings(XMLComponentManager parent) {

        // create storage for recognized features and properties
        fRecognizedFeatures = new Vector();
        fRecognizedProperties = new Vector();

        // create table for features and properties
        fFeatures = new Hashtable();
        fProperties = new Hashtable();

        // save parent
        fParentSettings = parent;

    } // <init>(XMLComponentManager)

    //
    // XMLParserConfiguration methods
    //

    /**
     * Allows a parser to add parser specific features to be recognized
     * and managed by the parser configuration.
     *
     * @param featureIds An array of the additional feature identifiers
     *                   to be recognized.
     */
    public void addRecognizedFeatures(String[] featureIds) {

        // add recognized features
        int featureIdsCount = featureIds != null ? featureIds.length : 0;
        for (int i = 0; i < featureIdsCount; i++) {
            String featureId = featureIds[i];
            if (!fRecognizedFeatures.contains(featureId)) {
                fRecognizedFeatures.addElement(featureId);
            }
        }

    } // addRecognizedFeatures(String[])

    /**
     * Set the state of a feature.
     *
     * Set the state of any feature in a SAX2 parser.  The parser
     * might not recognize the feature, and if it does recognize
     * it, it might not be able to fulfill the request.
     *
     * @param featureId The unique identifier (URI) of the feature.
     * @param state The requested state of the feature (true or false).
     *
     * @exception org.apache.xerces.xni.parser.XMLConfigurationException If the
     *            requested feature is not known.
     */
    static int counter = 1;
    public void setFeature(String featureId, boolean state)
        throws XMLConfigurationException {

        // check and store
        checkFeature(featureId);

        fFeatures.put(featureId, state ? Boolean.TRUE : Boolean.FALSE);
    } // setFeature(String,boolean)

    /**
     * Allows a parser to add parser specific properties to be recognized
     * and managed by the parser configuration.
     *
     * @param propertyIds An array of the additional property identifiers
     *                    to be recognized.
     */
    public void addRecognizedProperties(String[] propertyIds) {

        // add recognizedProperties
        int propertyIdsCount = propertyIds != null ? propertyIds.length : 0;
        for (int i = 0; i < propertyIdsCount; i++) {
            String propertyId = propertyIds[i];
            if (!fRecognizedProperties.contains(propertyId)) {
                fRecognizedProperties.addElement(propertyId);
            }
        }

    } // addRecognizedProperties(String[])

    /**
     * setProperty
     *
     * @param propertyId
     * @param value
     * @exception org.apache.xerces.xni.parser.XMLConfigurationException If the
     *            requested feature is not known.
     */
    public void setProperty(String propertyId, Object value)
        throws XMLConfigurationException {

        //System.out.println("propertId = " + propertyId ) ;
        //System.out.println("value = " + value ) ;
        // check and store
        checkProperty(propertyId);
        fProperties.put(propertyId, value);

    } // setProperty(String,Object)

    //
    // XMLComponentManager methods
    //

    /**
     * Returns the state of a feature.
     *
     * @param featureId The feature identifier.
		 * @return true if the feature is supported
     *
     * @throws XMLConfigurationException Thrown for configuration error.
     *                                   In general, components should
     *                                   only throw this exception if
     *                                   it is <strong>really</strong>
     *                                   a critical error.
     */
    public boolean getFeature(String featureId)
        throws XMLConfigurationException {

        Boolean state = (Boolean) fFeatures.get(featureId);

        if (state == null) {
            checkFeature(featureId);
            return false;
        }
        return state.booleanValue();

    } // getFeature(String):boolean

    /**
     * Returns the value of a property.
     *
     * @param propertyId The property identifier.
		 * @return the value of the property
     *
     * @throws XMLConfigurationException Thrown for configuration error.
     *                                   In general, components should
     *                                   only throw this exception if
     *                                   it is <strong>really</strong>
     *                                   a critical error.
     */
    public Object getProperty(String propertyId)
        throws XMLConfigurationException {

        Object propertyValue = fProperties.get(propertyId);

        if (propertyValue == null) {
            checkProperty(propertyId);
        }

        return propertyValue;

    } // getProperty(String):Object

    //
    // Protected methods
    //

    /**
     * Check a feature. If feature is known and supported, this method simply
     * returns. Otherwise, the appropriate exception is thrown.
     *
     * @param featureId The unique identifier (URI) of the feature.
     *
     * @exception org.apache.xerces.xni.parser.XMLConfigurationException If the
     *            requested feature is not known.
     */
    protected void checkFeature(String featureId)
        throws XMLConfigurationException {

        // check feature
        if (!fRecognizedFeatures.contains(featureId)) {
            if (fParentSettings != null) {
                fParentSettings.getFeature(featureId);
            }
            else {
                short type = XMLConfigurationException.NOT_RECOGNIZED;
                throw new XMLConfigurationException(type, featureId);
            }
        }

    } // checkFeature(String)

    /**
     * Check a property. If the property is known and supported, this method
     * simply returns. Otherwise, the appropriate exception is thrown.
     *
     * @param propertyId The unique identifier (URI) of the property
     *                   being set.
     * @exception org.apache.xerces.xni.parser.XMLConfigurationException If the
     *            requested feature is not known.
     */
    protected void checkProperty(String propertyId)
        throws XMLConfigurationException {

        // check property
        if (!fRecognizedProperties.contains(propertyId)) {
            if (fParentSettings != null) {
                fParentSettings.getProperty(propertyId);
            }
            else {
                short type = XMLConfigurationException.NOT_RECOGNIZED;
                throw new XMLConfigurationException(type, propertyId);
            }
        }

    } // checkProperty(String)

} // class ParserConfigurationSettings
