/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.jumpstart.es.system;

import codex.jumpstart.es.components.Visual;
import codex.jumpstart.es.factory.Factory;
import codex.jumpstart.es.factory.FactoryState;
import com.jme3.app.Application;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityId;

/**
 *
 * @author codex
 */
public class VisualState extends FactoryState<Spatial, Visual> {

    public static final String USERDATA = "SpatialRegistry(entityId)";

    public VisualState(Factory<Spatial> factory) {
        super(Visual.class, factory);
    }
    
    @Override
    protected void init(Application app) {}
    @Override
    protected void onEnable() {}
    @Override
    protected void onDisable() {}
    @Override
    public void update(float tpf) {}
    @Override
    protected Spatial create(Entity e) {
        var spatial = super.create(e);
        if (spatial != null) {
            assignId(spatial, e.getId());
            rootNode.attachChild(spatial);
        }
        return spatial;
    }
    @Override
    protected Spatial destroy(Entity e) {
        var spatial = super.destroy(e);
        if (spatial != null) {
            assignId(spatial, null);
            spatial.removeFromParent();
        }
        return spatial;
    }
    
    public <T extends Control> T getControl(EntityId id, Class<T> type) {
        var spatial = get(id);
        if (spatial == null) return null;
        return spatial.getControl(type);
    }
    
    public static void assignId(Spatial spatial, EntityId id) {
        spatial.setUserData(USERDATA, (id != null ? id.getId() : null));
    }
    public static EntityId fetchId(Spatial spatial) {
        Long id = spatial.getUserData(USERDATA);
        if (id == null) return null;
        return new EntityId(id);
    }
    public static EntityId fetchId(Spatial spatial, int searchDepth) {
        assert spatial != null;
        do {
            var id = fetchId(spatial);
            if (id != null) return id;
        } while ((spatial = spatial.getParent()) != null && searchDepth-- != 0);
        return null;
    }
    
}
