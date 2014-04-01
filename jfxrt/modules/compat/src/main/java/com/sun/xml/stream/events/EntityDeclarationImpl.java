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
 * $Id: EntityDeclarationImpl.java,v 1.5 2010-11-02 21:02:28 joehw Exp $
 */
package com.sun.xml.stream.events;

import javax.xml.stream.events.EntityDeclaration;
import javax.xml.stream.events.XMLEvent;
import org.apache.xerces.xni.XMLResourceIdentifier;

/**
 *
 * This class store all the information for a particular EntityDeclaration. EntityDeclaration interface
 * has various get* functiosn to retirve information about a particular EntityDeclaration.
 *
 * @author  Neeraj Bajaj, Sun Microsystems.
 */
public class EntityDeclarationImpl extends DummyEvent implements EntityDeclaration {
    
    private XMLResourceIdentifier fXMLResourceIdentifier ;
    private String fEntityName;
    private String fReplacementText;
    private String fNotationName;
    
    /** Creates a new instance of EntityDeclarationImpl */
    public EntityDeclarationImpl() {
        init();
    }
    
    public EntityDeclarationImpl(String entityName , String replacementText){
        this(entityName,replacementText,null);
        
    }
    
    public EntityDeclarationImpl(String entityName, String replacementText, XMLResourceIdentifier resourceIdentifier){
        init();
        fEntityName = entityName;
        fReplacementText = replacementText;
        fXMLResourceIdentifier = resourceIdentifier;
    }
    
    public void setEntityName(String entityName){
        fEntityName = entityName;
    }
    
    public String getEntityName(){
        return fEntityName;
    }
    
    public void setEntityReplacementText(String replacementText){
        fReplacementText = replacementText;
    }
    
    public void setXMLResourceIdentifier(XMLResourceIdentifier resourceIdentifier){
        fXMLResourceIdentifier = resourceIdentifier ;
    }
    
    public XMLResourceIdentifier getXMLResourceIdentifier(){
        return fXMLResourceIdentifier;
    }
    
    public String getSystemId(){
        if(fXMLResourceIdentifier != null)
            return fXMLResourceIdentifier.getLiteralSystemId();
        return null;
    }
    
    public String getPublicId(){
        if(fXMLResourceIdentifier != null)
            return fXMLResourceIdentifier.getPublicId();
        
        return null;
    }
    
    public String getBaseURI() {
        if(fXMLResourceIdentifier != null)
            return fXMLResourceIdentifier.getBaseSystemId();
        return null;
    }
    
    public String getName(){
        return fEntityName;
    }
    
    public String getNotationName() {
        return fNotationName;
    }
    
    public void setNotationName(String notationName){
        fNotationName = notationName;
    }
    
    public String getReplacementText() {
        return fReplacementText;
    }
    
    protected void init(){
        setEventType(XMLEvent.ENTITY_DECLARATION);
    }

    protected void writeAsEncodedUnicodeEx(java.io.Writer writer) 
    throws java.io.IOException
    {
        writer.write("<!ENTITY ");
        writer.write(fEntityName);
        if (fReplacementText != null) {
            //internal entity
            //escape quotes, lt and amps
            writer.write(" \"");
            charEncode(writer, fReplacementText);             
        } else {
            //external entity
            String pubId = getPublicId();
            if (pubId != null) {
                writer.write(" PUBLIC \"");
                writer.write(pubId);
            } else {
                writer.write(" SYSTEM \"");
                writer.write(getSystemId());
            }
        }
        writer.write("\"");
        if (fNotationName != null) {
            writer.write(" NDATA ");
            writer.write(fNotationName);
        }
        writer.write(">");
    }    
}
