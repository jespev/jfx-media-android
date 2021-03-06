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

package javafx.scene.text;

/**
Builder class for javafx.scene.text.Font
@see javafx.scene.text.Font
@deprecated This class is deprecated and will be removed in the next version
* @since JavaFX 2.0
*/
@javax.annotation.Generated("Generated by javafx.builder.processor.BuilderProcessor")
@Deprecated
public final class FontBuilder implements javafx.util.Builder<javafx.scene.text.Font> {
    protected FontBuilder() {
    }
    
    /** Creates a new instance of FontBuilder. */
    @SuppressWarnings({"deprecation", "rawtypes", "unchecked"})
    public static javafx.scene.text.FontBuilder create() {
        return new javafx.scene.text.FontBuilder();
    }
    
    private java.lang.String name;
    /**
    Set the value of the {@link javafx.scene.text.Font#getName() name} property for the instance constructed by this builder.
    */
    public javafx.scene.text.FontBuilder name(java.lang.String x) {
        this.name = x;
        return this;
    }
    
    private double size;
    /**
    Set the value of the {@link javafx.scene.text.Font#getSize() size} property for the instance constructed by this builder.
    */
    public javafx.scene.text.FontBuilder size(double x) {
        this.size = x;
        return this;
    }
    
    /**
    Make an instance of {@link javafx.scene.text.Font} based on the properties set on this builder.
    */
    public javafx.scene.text.Font build() {
        javafx.scene.text.Font x = new javafx.scene.text.Font(this.name, this.size);
        return x;
    }
}
