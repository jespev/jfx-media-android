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
import java.util.Enumeration;

import org.apache.xerces.xni.Augmentations;

/**
 * This class provides an implementation for Augmentations interface. 
 * Augmentations interface defines a hashtable of additional data that could
 * be passed along the document pipeline. The information can contain extra
 * arguments or infoset augmentations, for example PSVI. This additional
 * information is identified by a String key.
 * <p>
 * 
 * @author Elena Litani, IBM
 * @version $Id: AugmentationsImpl.java,v 1.4 2010-11-02 21:02:30 joehw Exp $
 */
public class AugmentationsImpl implements Augmentations{
    
    private AugmentationsItemsContainer fAugmentationsContainer =
                                        new SmallContainer();
    
    /**
     * Add additional information identified by a key to the Augmentations structure.
     * 
     * @param key    Identifier, can't be <code>null</code>
     * @param item   Additional information
     *
     * @return the previous value of the specified key in the Augmentations strucutre,
     *         or <code>null</code> if it did not have one.
     */
    public Object putItem (String key, Object item){
        Object oldValue = fAugmentationsContainer.putItem(key, item);

        if (oldValue == null && fAugmentationsContainer.isFull()) {
            fAugmentationsContainer = fAugmentationsContainer.expand();
        }

        return oldValue;
    }


    /**
     * Get information identified by a key from the Augmentations structure
     * 
     * @param key    Identifier, can't be <code>null</code>
     *
     * @return the value to which the key is mapped in the Augmentations structure;
     *         <code>null</code> if the key is not mapped to any value.
     */
    public Object getItem(String key){
        return fAugmentationsContainer.getItem(key);
    }
    
    
    /**
     * Remove additional info from the Augmentations structure
     * 
     * @param key    Identifier, can't be <code>null</code>
     */
    public Object removeItem (String key){
        return fAugmentationsContainer.removeItem(key);
    }

    /**
     * Returns an enumeration of the keys in the Augmentations structure
     *
     */
    public Enumeration keys (){
        return fAugmentationsContainer.keys();
    }

    /**
     * Remove all objects from the Augmentations structure.
     */
    public void removeAllItems() {
        fAugmentationsContainer.clear();
    }

    public String toString() {
        return fAugmentationsContainer.toString();
    }

    abstract class AugmentationsItemsContainer {
        abstract public Object putItem(Object key, Object item);
        abstract public Object getItem(Object key);
        abstract public Object removeItem(Object key);
        abstract public Enumeration keys();
        abstract public void clear();
        abstract public boolean isFull();
        abstract public AugmentationsItemsContainer expand();
    }

    class SmallContainer extends AugmentationsItemsContainer {
        final static int SIZE_LIMIT = 10;
        final Object[] fAugmentations = new Object[SIZE_LIMIT*2];
        int fNumEntries = 0;

        public Enumeration keys() {
            return new SmallContainerKeyEnumeration();
        }

        public Object getItem(Object key) {
            for (int i = 0; i < fNumEntries*2; i = i + 2) {
                if (fAugmentations[i].equals(key)) {
                    return fAugmentations[i+1];
                }
            }

            return null;
        }

        public Object putItem(Object key, Object item) {
            for (int i = 0; i < fNumEntries*2; i = i + 2) {
                if (fAugmentations[i].equals(key)) {
                    Object oldValue = fAugmentations[i+1];
                    fAugmentations[i+1] = item;

                    return oldValue;
                }
            }

            fAugmentations[fNumEntries*2] = key;
            fAugmentations[fNumEntries*2+1] = item;
            fNumEntries++;

            return null;
        }


        public Object removeItem(Object key) {
            for (int i = 0; i < fNumEntries*2; i = i + 2) {
                if (fAugmentations[i].equals(key)) {
                    Object oldValue = fAugmentations[i+1];

                    for (int j = i; j < fNumEntries*2 - 2; j = j + 2) {
                        fAugmentations[j] = fAugmentations[j+2];
                        fAugmentations[j+1] = fAugmentations[j+3];
                    }

                    fAugmentations[fNumEntries*2-2] = null;
                    fAugmentations[fNumEntries*2-1] = null;
                    fNumEntries--;

                    return oldValue;
                }
            }

            return null;
        }

        public void clear() {
            for (int i = 0; i < fNumEntries*2; i = i + 2) {
                fAugmentations[i] = null;
                fAugmentations[i+1] = null;
            }

            fNumEntries = 0;
        }

        public boolean isFull() {
            return (fNumEntries == SIZE_LIMIT);
        }

        public AugmentationsItemsContainer expand() {
            LargeContainer expandedContainer = new LargeContainer();

            for (int i = 0; i < fNumEntries*2; i = i + 2) {
                expandedContainer.putItem(fAugmentations[i],
                                          fAugmentations[i+1]);
            }

            return expandedContainer;
        }

        public String toString() {
            StringBuffer buff = new StringBuffer();
            buff.append("SmallContainer - fNumEntries == " + fNumEntries);

            for (int i = 0; i < SIZE_LIMIT*2; i=i+2) {
                buff.append("\nfAugmentations[");
                buff.append(i);
                buff.append("] == ");
                buff.append(fAugmentations[i]);
                buff.append("; fAugmentations[");
                buff.append(i+1);
                buff.append("] == ");
                buff.append(fAugmentations[i+1]);
            }

            return buff.toString();
        }

        class SmallContainerKeyEnumeration implements Enumeration {
            Object [] enumArray = new Object[fNumEntries];
            int next = 0;

            SmallContainerKeyEnumeration() {
                for (int i = 0; i < fNumEntries; i++) {
                    enumArray[i] = fAugmentations[i*2];
                }
            }

            public boolean hasMoreElements() {
                return next < enumArray.length;
            }

            public Object nextElement() {
                if (next >= enumArray.length) {
                    throw new java.util.NoSuchElementException();
                }

                Object nextVal = enumArray[next];
                enumArray[next] = null;
                next++;

                return nextVal;
            }
        }
    }

    class LargeContainer extends AugmentationsItemsContainer {
        final Hashtable fAugmentations = new Hashtable();

        public Object getItem(Object key) {
            return fAugmentations.get(key);
        }

        public Object putItem(Object key, Object item) {
            return fAugmentations.put(key, item);
        }

        public Object removeItem(Object key) {
            return fAugmentations.remove(key);
        }

        public Enumeration keys() {
            return fAugmentations.keys();
        }

        public void clear() {
            fAugmentations.clear();
        }

        public boolean isFull() {
            return false;
        }

        public AugmentationsItemsContainer expand() {
            return this;
        }

        public String toString() {
            StringBuffer buff = new StringBuffer();
            buff.append("LargeContainer");
            Enumeration keys = fAugmentations.keys();

            while (keys.hasMoreElements()) {
                Object key = keys.nextElement();
                buff.append("\nkey == ");
                buff.append(key);
                buff.append("; value == ");
                buff.append(fAugmentations.get(key));
            }

            return buff.toString();
        }
    }
}
