/*
 * Copyright (c) 2012, 2013, Oracle and/or its affiliates.
 * All rights reserved. Use is subject to license terms.
 *
 * This file is available and licensed under the following license:
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  - Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the distribution.
 *  - Neither the name of Oracle Corporation nor the names of its
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.oracle.javafx.scenebuilder.kit.editor.panel.content.driver;

import com.oracle.javafx.scenebuilder.kit.editor.drag.target.AbstractDropTarget;
import com.oracle.javafx.scenebuilder.kit.editor.drag.target.ContainerZDropTarget;
import com.oracle.javafx.scenebuilder.kit.editor.panel.content.ContentPanelController;
import com.oracle.javafx.scenebuilder.kit.editor.panel.content.driver.tring.AbstractTring;
import com.oracle.javafx.scenebuilder.kit.editor.panel.content.driver.tring.HBoxTring;
import com.oracle.javafx.scenebuilder.kit.fxom.FXOMInstance;
import com.oracle.javafx.scenebuilder.kit.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.kit.metadata.util.DesignHierarchyMask;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.layout.HBox;

/**
 *
 */
public class HBoxDriver extends AbstractNodeDriver {

    public HBoxDriver(ContentPanelController contentPanelController) {
        super(contentPanelController);
    }

    /*
     * AbstractDriver
     */
    @Override
    public AbstractDropTarget makeDropTarget(FXOMObject fxomObject, double sceneX, double sceneY) {
        
        assert fxomObject instanceof FXOMInstance;
        assert fxomObject.getSceneGraphObject() instanceof HBox;
        
        final HBox hbox = (HBox) fxomObject.getSceneGraphObject();
        assert hbox.getScene() != null;
        assert hbox.getLayoutBounds().contains(hbox.sceneToLocal(sceneX,sceneY));
        
        final double localX = hbox.sceneToLocal(sceneX, sceneY).getX();
        final int childCount = hbox.getChildrenUnmodifiable().size();
        
        final int targetIndex;
        if (childCount == 0) {
            // No children : we append
            targetIndex = -1;
            
        } else {
            assert childCount >= 1;
            
            int childIndex = 0;
            Node child = hbox.getChildrenUnmodifiable().get(childIndex++);
            Bounds childBounds = child.getBoundsInParent();
            double midX = (childBounds.getMinX() + childBounds.getMaxX()) / 2.0;
            while ((localX > midX) && (childIndex < childCount)) {
                child = hbox.getChildrenUnmodifiable().get(childIndex++);
                childBounds = child.getBoundsInParent();
                midX = (childBounds.getMinX() + childBounds.getMaxX()) / 2.0;
            }
            if (localX <= midX) {
                assert childIndex-1 < childCount;
                targetIndex = childIndex-1;
            } else {
                targetIndex = -1;
            }
        }
        
        final FXOMObject beforeChild;
        if (targetIndex == -1) {
            beforeChild = null;
        } else {
            final DesignHierarchyMask m = new DesignHierarchyMask(fxomObject);
            beforeChild = m.getSubComponentAtIndex(targetIndex);
        }
        
        return new ContainerZDropTarget((FXOMInstance)fxomObject, beforeChild);
    }
    
    
    @Override
    public AbstractTring<?> makeTring(AbstractDropTarget dropTarget) {
        assert dropTarget instanceof ContainerZDropTarget; 
        assert dropTarget.getTargetObject() instanceof FXOMInstance;
        assert dropTarget.getTargetObject().getSceneGraphObject() instanceof HBox;
        
        final ContainerZDropTarget zDropTarget = (ContainerZDropTarget) dropTarget;
        final int targetIndex;
        if (zDropTarget.getBeforeChild() == null) {
            targetIndex = -1;
        } else {
            targetIndex = zDropTarget.getBeforeChild().getIndexInParentProperty();
        }
        return new HBoxTring(contentPanelController, 
                (FXOMInstance) dropTarget.getTargetObject(),
                targetIndex);
    }
}
