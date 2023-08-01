/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.jumpstart.es.registry;

import codex.jumpstart.es.components.Animation;
import static codex.jumpstart.es.system.VisualState.assignId;
import com.jme3.app.Application;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import com.simsilica.es.EntityId;

/**
 *
 * @author codex
 */
public class AnimationRegistry extends Registry<Spatial> {

    public AnimationRegistry() {
        super(Animation.class);
    }
    
    @Override
    protected void init(Application app) {}
    @Override
    protected void onEnable() {}
    @Override
    protected void onDisable() {}
    @Override
    public boolean link(EntityId id, Spatial spatial) {
        if (super.link(id, spatial)) {
            assignId(spatial, id);
            return true;
        }
        return false;
    }
    @Override
    public Spatial unlink(EntityId id) {
        var spatial = super.unlink(id);
        if (spatial != null) {
            assignId(spatial, null);
        }
        return spatial;
    }
    
    public <T extends Control> T getControl(EntityId id, Class<T> type) {
        var spatial = get(id);
        if (spatial == null) return null;
        return spatial.getControl(type);
    }
    
}
