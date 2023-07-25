/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.jumpstart;

import com.jme3.anim.AnimComposer;
import com.jme3.anim.AnimLayer;
import com.jme3.anim.AnimationMask;
import com.jme3.anim.tween.action.Action;

/**
 *
 * @author codex
 */
public class LayerController {
    
    private final String layer;
    private AnimComposer anim;
    private AnimationMask mask;
    private boolean resetProtection = true;
    
    public LayerController(String layer) {
        this.layer = layer;
    }
    public LayerController(String layer, AnimationMask mask) {
        this.layer = layer;
        this.mask = mask;
    }
    
    public void enter(String action) {
        if (anim == null) return;
        Action run = anim.action(action);
        if (resetProtection && run == getLayer().getCurrentAction()) return;
        getLayer().setCurrentAction(run);
    }
    public void exit() {
        if (!isActive()) return;
        getLayer().setCurrentAction(null);
    }
    
    protected void setAnimComposer(AnimComposer anim) {
        this.anim = anim;
    }
    protected void createIfAbsent() {
        if (anim == null || mask == null || anim.getLayerNames().contains(layer)) {
            return;
        }
        anim.makeLayer(layer, mask);
    }
    
    /**
     * Reset protection ensures that an action does not
     * get interrupted by entering the same action.
     * <p>Is useful if it is more convenient to constantly enter
     * an animation instead of entering it once.
     * <p>Default=true
     * @param enable 
     */
    public void enableResetProtection(boolean enable) {
        resetProtection = enable;
    }
    
    public boolean isActive() {
        return anim != null && getLayer().getCurrentAction() != null;
    }
    public AnimComposer getAnimComposer() {
        return anim;
    }
    public AnimLayer getLayer() {
        if (anim == null) return null;
        return anim.getLayer(layer);
    }
    public String getLayerName() {
        return layer;
    }
    
}
