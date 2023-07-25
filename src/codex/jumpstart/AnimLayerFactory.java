/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.jumpstart;

import com.jme3.anim.AnimComposer;
import com.jme3.anim.ArmatureMask;
import com.jme3.anim.SkinningControl;

/**
 *
 * @author codex
 */
public class AnimLayerFactory {
    
    private final AnimComposer anim;
    private final SkinningControl skin;
    private String root;
    
    public AnimLayerFactory(AnimComposer anim, SkinningControl skin) {
        this.anim = anim;
        this.skin = skin;
    }
    public AnimLayerFactory(AnimComposer anim, SkinningControl skin, String root) {
        this.anim = anim;
        this.skin = skin;
        this.root = root;
    }
    
    /**
     * Set the root joint.
     * @param jointname
     * @return 
     */
    public AnimLayerFactory setRootJoint(String jointname) {
        this.root = jointname;
        return this;
    }
    
    /**
     * Makes a layer including the root joint and all its children.
     * @param name name of the new layer
     * @return 
     */
    public AnimLayerFactory make(String name) {
        if (root == null) {
            throw new NullPointerException("Root joint cannot be null!");
        }
        return make(name, root);
    }
    
    /**
     * Makes a layer including the given joint and all its children.
     * @param name name of the new layer
     * @param fromjoint
     * @return 
     */
    public AnimLayerFactory make(String name, String fromjoint) {
        anim.makeLayer(name, ArmatureMask.createMask(skin.getArmature(), fromjoint));
        return this;
    }
    
}
