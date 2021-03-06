/* 
 * Copyright (c) 2011, 2013, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package javafx.scene.layout;

/**
Builder class for javafx.scene.layout.Pane
@see javafx.scene.layout.Pane
@deprecated This class is deprecated and will be removed in the next version
* @since JavaFX 2.0
*/
@javax.annotation.Generated("Generated by javafx.builder.processor.BuilderProcessor")
@Deprecated
public class PaneBuilder<B extends javafx.scene.layout.PaneBuilder<B>> extends javafx.scene.layout.RegionBuilder<B> {
    protected PaneBuilder() {
    }
    
    /** Creates a new instance of PaneBuilder. */
    @SuppressWarnings({"deprecation", "rawtypes", "unchecked"})
    public static javafx.scene.layout.PaneBuilder<?> create() {
        return new javafx.scene.layout.PaneBuilder();
    }
    
    private boolean __set;
    public void applyTo(javafx.scene.layout.Pane x) {
        super.applyTo(x);
        if (__set) x.getChildren().addAll(this.children);
    }
    
    private java.util.Collection<? extends javafx.scene.Node> children;
    /**
    Add the given items to the List of items in the {@link javafx.scene.layout.Pane#getChildren() children} property for the instance constructed by this builder.
    */
    @SuppressWarnings("unchecked")
    public B children(java.util.Collection<? extends javafx.scene.Node> x) {
        this.children = x;
        __set = true;
        return (B) this;
    }
    
    /**
    Add the given items to the List of items in the {@link javafx.scene.layout.Pane#getChildren() children} property for the instance constructed by this builder.
    */
    public B children(javafx.scene.Node... x) {
        return children(java.util.Arrays.asList(x));
    }
    
    /**
    Make an instance of {@link javafx.scene.layout.Pane} based on the properties set on this builder.
    */
    public javafx.scene.layout.Pane build() {
        javafx.scene.layout.Pane x = new javafx.scene.layout.Pane();
        applyTo(x);
        return x;
    }
}
