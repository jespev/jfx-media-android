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
 * $Id: ProcessingInstructionEvent.java,v 1.5 2010-11-02 21:02:28 joehw Exp $
 */
package com.sun.xml.stream.events ;

import java.io.Writer;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.ProcessingInstruction;

/** Implements Processing Instruction Event
 *
 *@author Neeraj Bajaj, Sun Microsystems.
 *
 */


public class ProcessingInstructionEvent extends DummyEvent
implements ProcessingInstruction {
    
    /** Processing Instruction Name */
    private String fName;
    /** Processsing instruction content */
    private String fContent;
    
    public ProcessingInstructionEvent() {
        init();
    }
    
    public ProcessingInstructionEvent(String targetName, String data) {
        this(targetName,data,null);
    }
    
    public ProcessingInstructionEvent(String targetName, String data,Location loc) {
        init();
        this.fName = targetName;
        fContent = data;
        setLocation(loc);
    }
    
    protected void init() {
        setEventType(XMLStreamConstants.PROCESSING_INSTRUCTION);
    }
    
    public String getTarget() {
        return fName;
    }
    
    public void setTarget(String targetName) {
        fName = targetName;
    }
    
    public void setData(String data) {
        fContent = data;
    }
    
    public String getData() {
        return fContent;
    }
    
    public String toString() {
        if(fContent != null && fName != null)
            return "<?" + fName + " " + fContent + "?>";
        if(fName != null)
            return "<?" + fName + "?>";
        if(fContent != null)
            return "<?" + fContent + "?>";
        else
            return "<??>";
    }
    
    protected void writeAsEncodedUnicodeEx(java.io.Writer writer) 
    throws java.io.IOException
    {
        writer.write(toString());
    }
    
}
