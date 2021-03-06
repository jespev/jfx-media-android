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

package javafx.scene.control;

/**
Builder class for javafx.scene.control.ScrollBar
@see javafx.scene.control.ScrollBar
@deprecated This class is deprecated and will be removed in the next version
* @since JavaFX 2.0
*/
@javax.annotation.Generated("Generated by javafx.builder.processor.BuilderProcessor")
@Deprecated
public class ScrollBarBuilder<B extends javafx.scene.control.ScrollBarBuilder<B>> extends javafx.scene.control.ControlBuilder<B> implements javafx.util.Builder<javafx.scene.control.ScrollBar> {
    protected ScrollBarBuilder() {
    }
    
    /** Creates a new instance of ScrollBarBuilder. */
    @SuppressWarnings({"deprecation", "rawtypes", "unchecked"})
    public static javafx.scene.control.ScrollBarBuilder<?> create() {
        return new javafx.scene.control.ScrollBarBuilder();
    }
    
    private int __set;
    public void applyTo(javafx.scene.control.ScrollBar x) {
        super.applyTo(x);
        int set = __set;
        if ((set & (1 << 0)) != 0) x.setBlockIncrement(this.blockIncrement);
        if ((set & (1 << 1)) != 0) x.setMax(this.max);
        if ((set & (1 << 2)) != 0) x.setMin(this.min);
        if ((set & (1 << 3)) != 0) x.setOrientation(this.orientation);
        if ((set & (1 << 4)) != 0) x.setUnitIncrement(this.unitIncrement);
        if ((set & (1 << 5)) != 0) x.setValue(this.value);
        if ((set & (1 << 6)) != 0) x.setVisibleAmount(this.visibleAmount);
    }
    
    private double blockIncrement;
    /**
    Set the value of the {@link javafx.scene.control.ScrollBar#getBlockIncrement() blockIncrement} property for the instance constructed by this builder.
    */
    @SuppressWarnings("unchecked")
    public B blockIncrement(double x) {
        this.blockIncrement = x;
        __set |= 1 << 0;
        return (B) this;
    }
    
    private double max;
    /**
    Set the value of the {@link javafx.scene.control.ScrollBar#getMax() max} property for the instance constructed by this builder.
    */
    @SuppressWarnings("unchecked")
    public B max(double x) {
        this.max = x;
        __set |= 1 << 1;
        return (B) this;
    }
    
    private double min;
    /**
    Set the value of the {@link javafx.scene.control.ScrollBar#getMin() min} property for the instance constructed by this builder.
    */
    @SuppressWarnings("unchecked")
    public B min(double x) {
        this.min = x;
        __set |= 1 << 2;
        return (B) this;
    }
    
    private javafx.geometry.Orientation orientation;
    /**
    Set the value of the {@link javafx.scene.control.ScrollBar#getOrientation() orientation} property for the instance constructed by this builder.
    */
    @SuppressWarnings("unchecked")
    public B orientation(javafx.geometry.Orientation x) {
        this.orientation = x;
        __set |= 1 << 3;
        return (B) this;
    }
    
    private double unitIncrement;
    /**
    Set the value of the {@link javafx.scene.control.ScrollBar#getUnitIncrement() unitIncrement} property for the instance constructed by this builder.
    */
    @SuppressWarnings("unchecked")
    public B unitIncrement(double x) {
        this.unitIncrement = x;
        __set |= 1 << 4;
        return (B) this;
    }
    
    private double value;
    /**
    Set the value of the {@link javafx.scene.control.ScrollBar#getValue() value} property for the instance constructed by this builder.
    */
    @SuppressWarnings("unchecked")
    public B value(double x) {
        this.value = x;
        __set |= 1 << 5;
        return (B) this;
    }
    
    private double visibleAmount;
    /**
    Set the value of the {@link javafx.scene.control.ScrollBar#getVisibleAmount() visibleAmount} property for the instance constructed by this builder.
    */
    @SuppressWarnings("unchecked")
    public B visibleAmount(double x) {
        this.visibleAmount = x;
        __set |= 1 << 6;
        return (B) this;
    }
    
    /**
    Make an instance of {@link javafx.scene.control.ScrollBar} based on the properties set on this builder.
    */
    public javafx.scene.control.ScrollBar build() {
        javafx.scene.control.ScrollBar x = new javafx.scene.control.ScrollBar();
        applyTo(x);
        return x;
    }
}
