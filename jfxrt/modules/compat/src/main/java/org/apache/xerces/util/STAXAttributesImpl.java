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

import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XMLAttributes;
import java.util.*;

/**
 * K.venugopal@sun.com
 */

public class STAXAttributesImpl implements XMLAttributes {
    
    int attr_index = 0;
    int MAGIC_NUMBER = 2;
    /** Namespaces. */
    protected boolean fNamespaces = true;
    protected ArrayList attrList = null;
    protected ArrayList dupList = null;
    private boolean init = false;
    //protected TreeMap attrMap = null;
    protected HashMap attrMap = null;
    //protected SymbolHash attrMap = null;
    /** Default constructor. */
    public STAXAttributesImpl() {
        attrList = new ArrayList(2);
        //attrMap = new TreeMap();
        for (int i=0;i<2;i++){
            Attribute attr = new Attribute();
            attr.name = new QName();
            attrList.add(i,attr);
        }
        //attrMap = new SymbolHash();
    }
    
    /**
     * @param tableSize initial size of table view
     */
    public STAXAttributesImpl(int tableSize) {
        attrList = new ArrayList(tableSize);
        //attrMap = new TreeMap();
        attrMap = new HashMap();
        //attrMap = new SymbolHash();
    }
    
    /**
     * Sets whether namespace processing is being performed. This state
     * is needed to return the correct value from the getLocalName method.
     *
     * @param namespaces True if namespace processing is turned on.
     *
     * @see #getLocalName
     */
    
    public void setNamespaces(boolean namespaces) {
        fNamespaces = namespaces;
    }
    
    
    /**
     * Adds an attribute. The attribute's non-normalized value of the
     * attribute will have the same value as the attribute value until
     * set using the <code>setNonNormalizedValue</code> method. Also,
     * the added attribute will be marked as specified in the XML instance
     * document unless set otherwise using the <code>setSpecified</code>
     * method.
     * <p>
     * <strong>Note:</strong> If an attribute of the same name already
     * exists, the old values for the attribute are replaced by the new
     * values.
     *
     * @param name  The attribute name.
     * @param type  The attribute type. The type name is determined by
     *                  the type specified for this attribute in the DTD.
     *                  For example: "CDATA", "ID", "NMTOKEN", etc. However,
     *                  attributes of type enumeration will have the type
     *                  value specified as the pipe ('|') separated list of
     *                  the enumeration values prefixed by an open
     *                  parenthesis and suffixed by a close parenthesis.
     *                  For example: "(true|false)".
     * @param value The attribute value.
     *
     * @return Returns the attribute index.
     *
     * @see #setNonNormalizedValue
     * @see #setSpecified
     */
    public int addAttribute(QName name, String type, String value) {
        Attribute attr =null;
        if(attr_index >= attrList.size()){
            attr =  new Attribute();
            attr.name = new QName();
            attrList.add(attr);
            attr.next = null;
        }
        else{
            attr = (Attribute)attrList.get(attr_index);
            attr.next = null;
        }
        
        attr.name.setValues(name);
        attr.type = type;
        attr.value = value;
        
        if ( attr_index < 5 ){
            Attribute tmp =  null;
            for (int i=0; i<attr_index; i++){
                tmp = (Attribute)attrList.get(i);
                if (tmp.name.rawname == name.rawname){
                    tmp.value = value;
                    return i;
                }
            }
        }else {
            Attribute tmp =  null;
            if(!init){
                if(attrMap == null)
                    attrMap = new HashMap(2,2);
                for (int i=0; i<attr_index; i++){
                    tmp = (Attribute)attrList.get(i);
                    attrMap.put(tmp.name.rawname,tmp);
                }
                init = true;
            }
            /*Attribute obj = (Attribute)attrMap.get(name.rawname);
            if( obj != null ){
                obj.value = value;
                return getLength();
             */
            if (attrMap.containsKey(name.rawname)){
                return getLength();
            }else{
                attrMap.put(name.rawname,attr);
            }
        }
        attr_index ++;
        return getLength()-1;
    }
    
    
    /**
     * Removes all of the attributes. This method will also remove all
     * entities associated to the attributes.
     */
    public void removeAllAttributes() {
        attr_index =0;
        if(attrMap != null)
            attrMap.clear();
        if(dupList != null)
            dupList.clear();
        init = false;
    }
    
    /**
     * Removes the attribute at the specified index.
     * <p>
     * <strong>Note:</strong> This operation changes the indexes of all
     * attributes following the attribute at the specified index.
     *
     * @param attrIndex The attribute index.
     */
    public void removeAttributeAt(int attrIndex) {
        Attribute attr = (Attribute )attrList.remove(attrIndex);
    }
    
    /**
     * Sets the name of the attribute at the specified index.
     *
     * @param attrIndex The attribute index.
     * @param attrName  The new attribute name.
     */
    public void setName(int attrIndex, QName attrName) {
        ((Attribute)attrList.get(attrIndex)).name.setValues(attrName);
    }
    
    /**
     * Sets the fields in the given QName structure with the values
     * of the attribute name at the specified index.
     *
     * @param attrIndex The attribute index.
     * @param attrName  The attribute name structure to fill in.
     */
    public void getName(int attrIndex, QName attrName) {
        attrName.setValues(((Attribute)attrList.get(attrIndex)).name);
    }
    
    /**
     * Sets the type of the attribute at the specified index.
     *
     * @param attrIndex The attribute index.
     * @param attrType  The attribute type. The type name is determined by
     *                  the type specified for this attribute in the DTD.
     *                  For example: "CDATA", "ID", "NMTOKEN", etc. However,
     *                  attributes of type enumeration will have the type
     *                  value specified as the pipe ('|') separated list of
     *                  the enumeration values prefixed by an open
     *                  parenthesis and suffixed by a close parenthesis.
     *                  For example: "(true|false)".
     */
    public void setType(int attrIndex, String attrType) {
        ((Attribute)attrList.get(attrIndex)).type = attrType;
    }
    
    /**
     * Sets the value of the attribute at the specified index. This
     * method will overwrite the non-normalized value of the attribute.
     *
     * @param attrIndex The attribute index.
     * @param attrValue The new attribute value.
     *
     * @see #setNonNormalizedValue
     */
    public void setValue(int attrIndex, String attrValue) {
        if ( attrIndex > attr_index)
            return;
        Attribute attribute = (Attribute)attrList.get(attrIndex);
        attribute.value = attrValue;
        attribute.nonNormalizedValue = attrValue;
    }
    
    /**
     * Sets the non-normalized value of the attribute at the specified
     * index.
     *
     * @param attrIndex The attribute index.
     * @param attrValue The new non-normalized attribute value.
     */
    public void setNonNormalizedValue(int attrIndex, String attrValue) {
        Attribute attribute = (Attribute)attrList.get(attrIndex);
        attribute.nonNormalizedValue = attrValue;
    }
    
    /**
     * Returns the non-normalized value of the attribute at the specified
     * index. If no non-normalized value is set, this method will return
     * the same value as the <code>getValue(int)</code> method.
     *
     * @param attrIndex The attribute index.
     */
    public String getNonNormalizedValue(int attrIndex) {
        Attribute attribute = (Attribute)attrList.get(attrIndex);
        return attribute.nonNormalizedValue;
    }
    
    /**
     * Sets whether an attribute is specified in the instance document
     * or not.
     *
     * @param attrIndex The attribute index.
     * @param specified True if the attribute is specified in the instance
     *                  document.
     */
    public void setSpecified(int attrIndex, boolean specified) {
        ((Attribute)attrList.get(attrIndex)).specified = specified;
    }
    
    /**
     * Returns true if the attribute is specified in the instance document.
     *
     * @param attrIndex The attribute index.
     */
    public boolean isSpecified(int attrIndex) {
        return ((Attribute)attrList.get(attrIndex)).specified;
    }
    
    
    /**
     * Return the number of attributes in the list.
     *
     * <p>Once you know the number of attributes, you can iterate
     * through the list.</p>
     *
     * @return The number of attributes in the list.
     */
    public int getLength() {
        return attr_index;
    }
    
    /**
     * Look up an attribute's type by index.
     *
     * <p>The attribute type is one of the strings "CDATA", "ID",
     * "IDREF", "IDREFS", "NMTOKEN", "NMTOKENS", "ENTITY", "ENTITIES",
     * or "NOTATION" (always in upper case).</p>
     *
     * <p>If the parser has not read a declaration for the attribute,
     * or if the parser does not report attribute types, then it must
     * return the value "CDATA" as stated in the XML 1.0 Recommentation
     * (clause 3.3.3, "Attribute-Value Normalization").</p>
     *
     * <p>For an enumerated attribute that is not a notation, the
     * parser will report the type as "NMTOKEN".</p>
     *
     * @param index The attribute index (zero-based).
     * @return The attribute's type as a string, or null if the
     *         index is out of range.
     * @see #getLength
     */
    public String getType(int index) {
        if (index < 0 || index >= attrList.size()) {
            return null;
        }
        return getReportableType(((Attribute)attrList.get(index)).type);
    }
    
    /**
     * Look up an attribute's type by XML 1.0 qualified name.
     *
     * <p>See {@link #getType(int) getType(int)} for a description
     * of the possible types.</p>
     *
     * @param qname The XML 1.0 qualified name.
     * @return The attribute type as a string, or null if the
     *         attribute is not in the list or if qualified names
     *         are not available.
     */
    public String getType(String qname) {
        return null;
    }
    
    /**
     * Look up an attribute's value by index.
     *
     * <p>If the attribute value is a list of tokens (IDREFS,
     * ENTITIES, or NMTOKENS), the tokens will be concatenated
     * into a single string with each token separated by a
     * single space.</p>
     *
     * @param index The attribute index (zero-based).
     * @return The attribute's value as a string, or null if the
     *         index is out of range.
     * @see #getLength
     */
    public String getValue(int index) {
        return ((Attribute)attrList.get(index)).value;
    }
    
    /**
     * Look up an attribute's value by XML 1.0 qualified name.
     *
     * <p>See {@link #getValue(int) getValue(int)} for a description
     * of the possible values.</p>
     *
     * @param qname The XML 1.0 qualified name.
     * @return The attribute value as a string, or null if the
     *         attribute is not in the list or if qualified names
     *         are not available.
     */
    public String getValue(String qname) {
        return null;
    }
    
    
    /**
     * Return the name of an attribute in this list (by position).
     *
     * <p>The names must be unique: the SAX parser shall not include the
     * same attribute twice.  Attributes without values (those declared
     * #IMPLIED without a value specified in the start tag) will be
     * omitted from the list.</p>
     *
     * <p>If the attribute name has a namespace prefix, the prefix
     * will still be attached.</p>
     *
     * @param i The index of the attribute in the list (starting at 0).
     * @return The name of the indexed attribute, or null
     *         if the index is out of range.
     * @see #getLength
     */
    public String getName(int index) {
        if (index < 0 || index >= attrList.size()) {
            return null;
        }
        return ((Attribute)attrList.get(index)).name.rawname;
    }
    
    /**
     * Look up the index of an attribute by XML 1.0 qualified name.
     *
     * @param qName The qualified (prefixed) name.
     * @return The index of the attribute, or -1 if it does not
     *         appear in the list.
     */
    public int getIndex(String qName) {
        for (int i = 0; i < attr_index; i++) {
            Attribute attribute = (Attribute)attrList.get(i);
            if (attribute.name.rawname != null &&
            attribute.name.rawname.equals(qName)) {
                return i;
            }
        }
        return -1;
    }
    
    /**
     * Look up the index of an attribute by Namespace name.
     *
     * @param uri The Namespace URI, or null if
     *        the name has no Namespace URI.
     * @param localName The attribute's local name.
     * @return The index of the attribute, or -1 if it does not
     *         appear in the list.
     */
    public int getIndex(String uri, String localPart) {
        for (int i = 0; i < attr_index; i++) {
            Attribute attribute = (Attribute)attrList.get(i);
            if (attribute.name.localpart != null &&
            attribute.name.localpart.equals(localPart) &&
            ((uri==attribute.name.uri) ||
            (uri!=null && attribute.name.uri!=null && attribute.name.uri.equals(uri)))) {
                return i;
            }
        }
        return -1;
    }
    
    /**
     * Look up an attribute's local name by index.
     *
     * @param index The attribute index (zero-based).
     * @return The local name, or the empty string if Namespace
     *         processing is not being performed, or null
     *         if the index is out of range.
     * @see #getLength
     */
    public String getLocalName(int index) {
        if (!fNamespaces) {
            return "";
        }
        return ((Attribute)attrList.get(index)).name.localpart;
    }
    
    /**
     * Look up an attribute's XML 1.0 qualified name by index.
     *
     * @param index The attribute index (zero-based).
     * @return The XML 1.0 qualified name, or the empty string
     *         if none is available, or null if the index
     *         is out of range.
     * @see #getLength
     */
    public String getQName(int index) {
        return  ((Attribute)attrList.get(index)).name.rawname;
    }
    
    public QName getQualifiedName(int index){
        return  ((Attribute)attrList.get(index)).name;
    }
    
    /**
     * Look up an attribute's type by Namespace name.
     *
     * <p>See {@link #getType(int) getType(int)} for a description
     * of the possible types.</p>
     *
     * @param uri The Namespace URI, or null if the
     *        name has no Namespace URI.
     * @param localName The local name of the attribute.
     * @return The attribute type as a string, or null if the
     *         attribute is not in the list or if Namespace
     *         processing is not being performed.
     */
    public String getType(String uri, String localName) {
        if (!fNamespaces) {
            return null;
        }
        int index = getIndex(uri, localName);
        return index != -1 ? getReportableType(((Attribute)attrList.get(index)).type) : null;
    }
    
    /**
     * Returns the prefix of the attribute at the specified index.
     *
     * @param index The index of the attribute.
     */
    public String getPrefix(int index) {
        return  ((Attribute)attrList.get(index)).name.prefix;
    }
    
    /**
     * Look up an attribute's Namespace URI by index.
     *
     * @param index The attribute index (zero-based).
     * @return The Namespace URI
     * @see #getLength
     */
    public String getURI(int index) {
        return  ((Attribute)attrList.get(index)).name.uri;
    }
    
    /**
     * Look up an attribute's value by Namespace name.
     *
     * <p>See {@link #getValue(int) getValue(int)} for a description
     * of the possible values.</p>
     *
     * @param uri The Namespace URI, or null if the
     * @param localName The local name of the attribute.
     * @return The attribute value as a string, or null if the
     *         attribute is not in the list.
     */
    public String getValue(String uri, String localName) {
        int index = getIndex(uri, localName);
        return index != -1 ? getValue(index) : null;
    }
    
    
    /**
     * Look up an augmentations by Namespace name.
     *
     * @param uri The Namespace URI, or null if the
     * @param localName The local name of the attribute.
     * @return Augmentations
     */
    public Augmentations getAugmentations(String uri, String localName) {
        return null;
    }
    
    /**
     * Look up an augmentation by XML 1.0 qualified name.
     * <p>
     *
     * @param qName The XML 1.0 qualified name.
     *
     * @return Augmentations
     *
     */
    public Augmentations getAugmentations(String qName){
        return null;
    }
    
    /**
     * Look up an augmentations by attributes index.
     *
     * @param attributeIndex The attribute index.
     * @return Augmentations
     */
    public Augmentations getAugmentations(int attributeIndex){
        return null;
    }
    
    /**
     * Sets the augmentations of the attribute at the specified index.
     *
     * @param attrIndex The attribute index.
     * @param augs      The augmentations.
     */
    public void setAugmentations(int attrIndex, Augmentations augs) {
    }
    
    /**
     * Sets the uri of the attribute at the specified index.
     *
     * @param attrIndex The attribute index.
     * @param uri       Namespace uri
     */
    public void setURI(int attrIndex, String uri) {
        Attribute attribute = (Attribute)attrList.get(attrIndex);
        attribute.name.uri = uri;
    }
    
    public void setSchemaId(int attrIndex, boolean schemaId) {
        //noop
    }
    public boolean getSchemaId(int index) {
        return false;
    }
    public boolean getSchemaId(String qname) {
        return false;
    }
    public boolean getSchemaId(String uri, String localName) {
        return false;
    }
    
    public void addAttributeNS(QName name, String type, String value) {
        Attribute attr = null;
        if(attr_index >= attrList.size()){
            attr =  new Attribute();
            attr.name = new QName();
            attrList.add(attr);
            attr.next = null;
        }
        else{
            attr = (Attribute)attrList.get(attr_index);
            attr.next = null;
        }
        attr.name.setValues(name);
        attr.type = type;
        attr.value = value;
    /*	System.out.println("Add local"+name.localpart);
        System.out.println("Add uri"+name.uri);
        System.out.println("Add rawname"+name.rawname);
     */
        if(attr_index > MAGIC_NUMBER){
            if(!init){
                if(attrMap == null)
                    attrMap = new HashMap(2,2);
                for (int i=0; i<attr_index; i++){
                    Attribute tmp = (Attribute)attrList.get(i);
                    attrMap.put(tmp.name.localpart,tmp);
                }
                init = true;
            }
            if(attrMap.containsKey(name.localpart)){
                Attribute obj = (Attribute)attrMap.get(name.localpart);
                attr.next = obj.next ;
                obj.next= attr;
                attr_index ++;
                if(!obj.dup){
                    if(dupList == null )
                        dupList = new ArrayList();
                    dupList.add(attr);
                    obj.dup = true;
                }
            }
            else{
                attrMap.put(name.localpart,attr);
                attr_index ++;
            }
        }
        else attr_index++;
    }
    
    /**
     * Checks for duplicate expanded names (local part and namespace name
     * pairs) in the attribute specification. If a duplicate is found its
     * name is returned.
     * <p>
     * This should be called once all the in-scope namespaces for the element
     * enclosing these attributes is known, and after all the attributes
     * have gone through namespace binding.
     *
     * @return the name of a duplicate attribute found in the search,
     * otherwise null.
     */
    public QName checkDuplicatesNS() {
        if (attr_index <=MAGIC_NUMBER ){
            for (int i = 0; i < attr_index - 1; ++i) {
                Attribute att1 =(Attribute) attrList.get(i);
                for (int j = i + 1; j < attr_index; ++j) {
                    Attribute att2 = (Attribute) attrList.get(j);
                    if (att1.name.localpart == att2.name.localpart &&
                    att1.name.uri == att2.name.uri) {
                        return att2.name;
                    }
                }
            }
        }else{
            if (dupList == null )
                return null;
            for (int i = 0; i < dupList.size(); ++i) {
                Attribute att1 = (Attribute)dupList.get(i);
                Attribute att2 = att1.next;
                while (att2!=null) {
                    if (att1.name.localpart == att2.name.localpart &&
                    att1.name.uri == att2.name.uri) {
                        return att2.name;
                    }
                    att2 = att1.next;
                }
            }
        }
        return null;
    }
    
    protected String getReportableType(String type) {
        if (type.indexOf('(') == 0 && type.lastIndexOf(')') == type.length()-1) {
            return "NMTOKEN";
        }
        return type;
    }
    
    protected Attribute getDuplicate(Attribute attr1,QName qname){
        Attribute att1 = attr1;
        if (att1.name.prefix == qname.prefix && attr1.next == null) return att1;
        while (att1!=null) {
            if (att1.name.rawname == qname.rawname )
                return att1;
            att1=att1.next;
        }
        return null;
    }
    
    class Attribute {
        
        /** Name. */
        public QName name = new QName();
        
        /** Type. */
        public String type;
        
        /** Value. */
        public String value;
        
        /** Non-normalized value. */
        public String nonNormalizedValue;
        
        /** Specified. */
        public boolean specified;
        
        /** Schema ID type. */
        public boolean schemaId;
        public boolean dup = false;
        Attribute next;
    }
    
}
