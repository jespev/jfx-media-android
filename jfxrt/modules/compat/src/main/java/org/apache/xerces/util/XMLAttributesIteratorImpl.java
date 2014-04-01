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

package org.apache.xerces.util;

//java imports
import java.util.Iterator ;
import java.util.NoSuchElementException;

//xerces imports
import org.apache.xerces.util.XMLAttributesImpl ;

/**
 *
 * @author  Neeraj Bajaj, Sun Microsystems
 */

/**
 * Its better to extend the functionality of existing XMLAttributesImpl and also make it of type Iterator.
 * We can directly  give an object of type iterator from StartElement event. We should also have
 * Attribute object of type javax.xml.stream.Attribute internally. It would avoid the need of creating
 * new javax.xml.stream.Attribute object at the later stage.
 *
 * Should we change XMLAttributes interface to implement Iteraotr ? I think its better avoid touching XNI as
 * much as possible. - NB.
 */

public class XMLAttributesIteratorImpl extends XMLAttributesImpl implements Iterator {
    
    //pointer to current position.
    protected int fCurrent = 0 ;
    
    protected XMLAttributesImpl.Attribute fLastReturnedItem ;
    
    /** Creates a new instance of XMLAttributesIteratorImpl */
    public XMLAttributesIteratorImpl() {
    }
    
    public boolean hasNext() {
        return fCurrent < getLength() ? true : false ;
    }//hasNext()
    
    public Object next() {
        if(hasNext()){
            // should this be of type javax.xml.stream.Attribute ?
            return fLastReturnedItem = fAttributes[fCurrent++] ;
        }
        else{
            throw new NoSuchElementException() ;
        }
    }//next
    
    public void remove() {
        //make sure that only last returned item can be removed.
        if(fLastReturnedItem == fAttributes[fCurrent - 1]){
            //remove the attribute at current index and lower the current position by 1.
            removeAttributeAt(fCurrent--) ;
        }
        else {
            //either the next method has been called yet, or the remove method has already been called
            //after the last call to the next method.
            throw new IllegalStateException();
        }
    }//remove
    
    public void removeAllAttributes() {
        super.removeAllAttributes() ;
        fCurrent = 0 ;
    }
    /** xxx: should we be doing this way ? Attribute event defines so many functions which doesn't make any sense
     *for Attribute.
     *
     */
    /*
    class AttributeImpl extends org.apache.xerces.util.XMLAttributesImpl.Attribute implements javax.xml.stream.events.Attribute{
     
    }
     */
    
} //XMLAttributesIteratorImpl
