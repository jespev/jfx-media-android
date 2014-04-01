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
 * $Id: EventFilterSupport.java,v 1.5 2010-11-02 21:02:26 joehw Exp $
 */
package com.sun.xml.stream;

import java.util.NoSuchElementException;
import javax.xml.stream.EventFilter;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import javax.xml.stream.util.EventReaderDelegate;

/**
 *
 * @author  Neeraj Bajaj, Sun Microsystems
 *
 */
public class EventFilterSupport extends EventReaderDelegate {
    
    //maintain a reference to EventFilter
    EventFilter fEventFilter ;
    /** Creates a new instance of EventFilterSupport */
    public EventFilterSupport(XMLEventReader eventReader, EventFilter eventFilter) {
        setParent(eventReader);
        fEventFilter = eventFilter;
    }
    
    public Object next(){
        try{
            return nextEvent();
        }catch(XMLStreamException ex){
            throw new NoSuchElementException();
        }
    }
    
    public boolean hasNext(){
        try{
            return peek() != null ? true : false ;
        }catch(XMLStreamException ex){
            return false;
        }
    }
    
    public XMLEvent nextEvent()throws XMLStreamException{
        if(super.hasNext()){
            //get the next event by calling XMLEventReader
            XMLEvent event = super.nextEvent();
            
            //if this filter accepts this event then return this event
            if(fEventFilter.accept(event)){
                return event;
            }
            else{
                return nextEvent();
            }
        }else{
            throw new NoSuchElementException();
        }
    }//nextEvent()
    
     public XMLEvent nextTag() throws XMLStreamException{
         if(super.hasNext()){
             XMLEvent event = super.nextTag();
             //if the filter accepts this event return this event.
             if(fEventFilter.accept(event)){
                return event;
             }
             else{
                return nextTag();
             }    
         }else{
             throw new NoSuchElementException();
         }         
     }
     
     public XMLEvent peek() throws XMLStreamException{
         while (true) {
             XMLEvent event = super.peek();
             if(event == null)return null;
             //if the filter accepts this event return this event.
             if(fEventFilter.accept(event)){
                return event;
             }
             //call super.next(), and then peek again.
             super.next();
         }
     }
     
}//EventFilterSupport
