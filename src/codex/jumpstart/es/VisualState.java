/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.jumpstart.es;

import codex.jumpstart.es.components.Visual;
import com.jme3.app.Application;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import java.util.HashMap;

/**
 *
 * @author codex
 */
public class VisualState extends ESAppState {

    public static final String USERDATA = "VisualState(entityId)";
    
    private EntitySet entities;
    private HashMap<EntityId, Spatial> spatials = new HashMap<>();
    private ModelFactory factory;
    
    @Override
    protected void init(Application app) {
        super.init(app);
        entities = ed.getEntities(Visual.class);
    }
    @Override
    protected void cleanup(Application app) {}
    @Override
    protected void onEnable() {}
    @Override
    protected void onDisable() {}
    @Override
    public void update(float tpf) {
        if (entities.applyChanges()) {
            entities.getAddedEntities().stream().forEach(e -> createSpatial(e));
            entities.getRemovedEntities().stream().forEach(e -> removeSpatial(e));
        }
    }
    
    private void createSpatial(Entity e) {
        Spatial spatial = factory.create(e.get(Visual.class).getFactoryRequest());
        spatial.setUserData(USERDATA, e.getId().getId());
        rootNode.attachChild(spatial);
        spatials.put(e.getId(), spatial);
    }
    private void removeSpatial(Entity e) {
        Spatial spatial = spatials.remove(e.getId());
        if (spatial != null) {
            spatial.removeFromParent();
            spatial.setUserData(USERDATA, null);
        }
    }
    
    public Spatial getSpatial(EntityId id) {
        return spatials.get(id);
    }
    public <T extends Control> T getControl(EntityId id, Class<T> type) {
        var spatial = getSpatial(id);
        if (spatial == null) return null;
        return spatial.getControl(type);
    }
    
    public static EntityId fetchId(Spatial spatial) {
        Long id = spatial.getUserData(USERDATA);
        if (id == null) return null;
        return new EntityId(id);
    }
    public static EntityId fetchId(Spatial spatial, int searchDepth) {
        while (spatial != null && searchDepth-- >= 0) {
            EntityId id = fetchId(spatial);
            if (id != null) return id;
            spatial = spatial.getParent();
        }
        return null;
    }
    
}
