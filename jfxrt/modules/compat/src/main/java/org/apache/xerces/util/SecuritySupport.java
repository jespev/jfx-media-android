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

import java.io.*;

/**
 * This class is duplicated for each JAXP subpackage so keep it in sync.
 * It is package private and therefore is not exposed as part of the JAXP
 * API.
 *
 * Base class with security related methods that work on JDK 1.1.
 */
class SecuritySupport {

    /*
     * Make this of type Object so that the verifier won't try to
     * prove its type, thus possibly trying to load the SecuritySupport12
     * class.
     */
    private static final Object securitySupport;

    static {
	SecuritySupport ss = null;
	try {
	    Class c = Class.forName("java.security.AccessController");
	    // if that worked, we're on 1.2.
	    /*
	    // don't reference the class explicitly so it doesn't
	    // get dragged in accidentally.
	    c = Class.forName("javax.mail.SecuritySupport12");
	    Constructor cons = c.getConstructor(new Class[] { });
	    ss = (SecuritySupport)cons.newInstance(new Object[] { });
	    */
	    /*
	     * Unfortunately, we can't load the class using reflection
	     * because the class is package private.  And the class has
	     * to be package private so the APIs aren't exposed to other
	     * code that could use them to circumvent security.  Thus,
	     * we accept the risk that the direct reference might fail
	     * on some JDK 1.1 JVMs, even though we would never execute
	     * this code in such a case.  Sigh...
	     */
	    ss = new SecuritySupport12();
	} catch (Exception ex) {
	    // ignore it
	} finally {
	    if (ss == null)
		ss = new SecuritySupport();
	    securitySupport = ss;
	}
    }

    /**
     * Return an appropriate instance of this class, depending on whether
     * we're on a JDK 1.1 or J2SE 1.2 (or later) system.
     */
    public static SecuritySupport getInstance() {
	return (SecuritySupport)securitySupport;
    }

    public ClassLoader getContextClassLoader() {
	return null;
    }

    public String getSystemProperty(String propName) {
        return System.getProperty(propName);
    }

    public FileInputStream getFileInputStream(File file)
        throws FileNotFoundException
    {
        return new FileInputStream(file);
    }

    public InputStream getResourceAsStream(ClassLoader cl, String name) {
        InputStream ris;
        if (cl == null) {
            ris = ClassLoader.getSystemResourceAsStream(name);
        } else {
            ris = cl.getResourceAsStream(name);
        }
        return ris;
    }
}
