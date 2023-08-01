/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.jumpstart.es.registry;

import codex.jumpstart.AnimLayerControl;
import com.jme3.anim.AnimComposer;
import com.jme3.anim.SkinningControl;
import com.jme3.app.Application;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;

/**
 *
 * @author codex
 */
public class AnimationRegistry extends Registry<Spatial> {
    
    private EntitySet entities;
    
    @Override
    protected void init(Application app) {
        
    }
    @Override
    protected void cleanup(Application app) {
        super.cleanup();
        entities.release();
    }
    @Override
    protected void onEnable() {}
    @Override
    protected void onDisable() {}
    @Override
    public void update(float tpf) {
        if (entities.applyChanges()) {
            entities.getRemovedEntities().stream().forEach(e -> unlink(e.getId()));
        }
    }
    
    public <T extends Control> T getControl(EntityId id, Class<T> type) {
        var spatial = get(id);
        if (spatial == null) return null;
        return spatial.getControl(type);
    }
    public AnimComposer getAnimComposer(EntityId id) {
        return getControl(id, AnimComposer.class);
    }
    public SkinningControl getSkinningControl(EntityId id) {
        return getControl(id, SkinningControl.class);
    }
    public AnimLayerControl getLayerControl(EntityId id) {
        return getControl(id, AnimLayerControl.class);
    }
    
}
