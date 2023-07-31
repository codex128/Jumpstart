/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.jumpstart.es.registry;

import codex.jumpstart.es.system.VisualState;
import codex.jumpstart.AnimLayerControl;
import com.jme3.anim.AnimComposer;
import com.jme3.anim.SkinningControl;
import com.jme3.app.Application;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import com.simsilica.es.EntityId;
import java.util.HashMap;

/**
 *
 * @author codex
 */
public class AnimationRegistry extends Registry<Spatial> {

    private HashMap<EntityId, Spatial> animations = new HashMap<>();
    
    @Override
    protected void init(Application app) {}
    @Override
    protected void cleanup(Application app) {
        animations.clear();
    }
    @Override
    protected void onEnable() {}
    @Override
    protected void onDisable() {}
    @Override
    public void update(float tpf) {}    
    @Override
    public boolean link(EntityId entity, Spatial object) {
        if (object.getControl(AnimComposer.class) == null) {
            throw new NullPointerException("Spatial must have an AnimComposer!");
        }
        if (animations.putIfAbsent(entity, object) != null) {
            return false;
        }
        VisualState.assignId(object, entity);
        return true;
    }
    @Override
    public Spatial unlink(EntityId entity) {
        var spatial = animations.remove(entity);
        if (spatial != null) {
            VisualState.assignId(spatial, null);
        }
        return spatial;
    }
    @Override
    public Spatial get(EntityId entity) {
        return animations.get(entity);
    }
    
    public Spatial getAnimatedSpatial(EntityId id) {
        return animations.get(id);
    }
    public <T extends Control> T getControl(EntityId id, Class<T> type) {
        var spatial = getAnimatedSpatial(id);
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
