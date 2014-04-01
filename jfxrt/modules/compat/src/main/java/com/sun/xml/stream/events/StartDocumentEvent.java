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
 * $Id: StartDocumentEvent.java,v 1.6 2010-11-02 21:02:28 joehw Exp $
 */
package com.sun.xml.stream.events ;

import javax.xml.stream.events.StartDocument;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamConstants;

/** Implementation of StartDocumentEvent.
 *
 * @author Neeraj Bajaj Sun Microsystems,Inc.
 * @author K.Venugopal Sun Microsystems,Inc.
 *
 */

public class StartDocumentEvent extends DummyEvent
implements StartDocument {
    
    protected String fSystemId;
    protected String fEncodingScheam;
    protected boolean fStandalone;
    protected String fVersion;
    private boolean fEncodingSchemeSet = false;
    private boolean fStandaloneSet = false;
    private boolean nestedCall = false;
    
    public StartDocumentEvent() {
        init("UTF-8","1.0",true,null);
    }
    
    public StartDocumentEvent(String encoding){
        init(encoding,"1.0",true,null);
    }
    
    public StartDocumentEvent(String encoding, String version){
        init(encoding,version,true,null);
    }
    
    public StartDocumentEvent(String encoding, String version, boolean standalone){
        this.fStandaloneSet = true;
        init(encoding,version,standalone,null);
    }
    
    public StartDocumentEvent(String encoding, String version, boolean standalone,Location loc){
        this.fStandaloneSet = true;
        init(encoding, version, standalone, loc);
    }
    protected void init(String encoding, String version, boolean standalone,Location loc) {
        setEventType(XMLStreamConstants.START_DOCUMENT);
        this.fEncodingScheam = encoding;
        this.fVersion = version;
        this.fStandalone = standalone;
        if (encoding != null && !encoding.equals(""))
            this.fEncodingSchemeSet = true;
        else {
            this.fEncodingSchemeSet = false;
            this.fEncodingScheam = "UTF-8";
        }
        this.fLocation = loc;
    }
    
    public String getSystemId() {
        if(fLocation == null )
            return "";
        else
            return fLocation.getSystemId();
    }
    
    
    public String getCharacterEncodingScheme() {
        return fEncodingScheam;
    }
    
    public boolean isStandalone() {
        return fStandalone;
    }
    
    public String getVersion() {
        return fVersion;
    }
    
    public void setStandalone(boolean flag) {
        fStandaloneSet = true;
        fStandalone = flag;
    }
    
    public void setStandalone(String s) {
        fStandaloneSet = true;
        if(s == null) {
            fStandalone = true;
            return;
        }
        if(s.equals("yes"))
            fStandalone = true;
        else
            fStandalone = false;
    }
    
    public boolean encodingSet() {
        return fEncodingSchemeSet;
    }
    
    public boolean standaloneSet() {
        return fStandaloneSet;
    }
    
    public void setEncoding(String encoding) {
        fEncodingScheam = encoding;
    }
    
    void setDeclaredEncoding(boolean value){
        fEncodingSchemeSet = value;
    }
    
    public void setVersion(String s) {
        fVersion = s;
    }
    
    void clear() {
        fEncodingScheam = "UTF-8";
        fStandalone = true;
        fVersion = "1.0";
        fEncodingSchemeSet = false;
        fStandaloneSet = false;
    }
    
    public String toString() {
        String s = "<?xml version=\"" + fVersion + "\"";
        s = s + " encoding='" + fEncodingScheam + "'";
        if(fStandaloneSet) {
            if(fStandalone)
                s = s + " standalone='yes'?>";
            else
                s = s + " standalone='no'?>";
        } else {
            s = s + "?>";
        }
        return s;
    }
    
    public boolean isStartDocument() {
        return true;
    }

    protected void writeAsEncodedUnicodeEx(java.io.Writer writer) 
    throws java.io.IOException
    {
        writer.write(toString());
    }
    
}
