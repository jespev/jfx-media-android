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
 * The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the "License").  You may not use this file except
 * in compliance with the License.
 *
 * You can obtain a copy of the license at
 * https://jaxp.dev.java.net/CDDLv1.0.html.
 * See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL
 * HEADER in each file and include the License file at
 * https://jaxp.dev.java.net/CDDLv1.0.html
 * If applicable add the following below this CDDL HEADER
 * with the fields enclosed by brackets "[]" replaced with
 * your own identifying information: Portions Copyright
 * [year] [name of copyright owner]
 */
package com.sun.xml.stream.events;

import javax.xml.stream.events.DTD;
import javax.xml.stream.events.XMLEvent;

/**
 *
 * @author  Neeraj Bajaj, Sun Microsystesm.
 *
 */
public class DTDEvent extends DummyEvent implements DTD{
    
    private String fDoctypeDeclaration;
    private java.util.List fNotations;
    private java.util.List fEntities;
    
    /** Creates a new instance of DTDEvent */
    public DTDEvent() {
        init();
    }
    
    public DTDEvent(String doctypeDeclaration){
        init();
        fDoctypeDeclaration = doctypeDeclaration;
    }
    
    public void setDocumentTypeDeclaration(String doctypeDeclaration){
        fDoctypeDeclaration = doctypeDeclaration;
    }
    
    public String getDocumentTypeDeclaration() {
        return fDoctypeDeclaration;
    }
    
    //xxx: we can change the signature if the implementation doesn't store the entities in List Datatype.
    //and then convert that DT to list format here. That way callee dont need to bother about conversion
    
    public void setEntities(java.util.List entites){
        fEntities = entites;
    }
    
    public java.util.List getEntities() {
        return fEntities;
    }
    
    //xxx: we can change the signature if the implementation doesn't store the entities in List Datatype.
    //and then convert that DT to list format here. That way callee dont need to bother about conversion
    
    public void setNotations(java.util.List notations){
        fNotations = notations;
    }
    
    public java.util.List getNotations() {
        return fNotations;
    }
    
    /**
     *Returns an implementation defined representation of the DTD.
     * This method may return null if no representation is available.
     *
     */
    public Object getProcessedDTD() {
        return null;
    }
    
    protected void init(){
        setEventType(XMLEvent.DTD);
    }
    
    public String toString(){
        return fDoctypeDeclaration ;
    }
    
    protected void writeAsEncodedUnicodeEx(java.io.Writer writer) 
    throws java.io.IOException
    {
        writer.write(fDoctypeDeclaration);
    }    
}
