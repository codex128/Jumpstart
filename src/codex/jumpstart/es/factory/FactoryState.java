/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.jumpstart.es.factory;

import codex.jumpstart.es.EntityAppState;
import codex.jumpstart.es.registry.Registry;
import com.jme3.app.Application;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;

/**
 *
 * @author codex
 * @param <T> registry type
 * @param <R> request type
 */
public abstract class FactoryState <T, R extends FactoryRequest> extends Registry<T> {
    
    protected final Class<R> requestType;
    protected EntitySet entities;
    protected Factory<T> factory;
    
    public FactoryState(Class<R> requestType, Factory<T> factory) {
        this.requestType = requestType;
        this.factory = factory;
    }
    
    @Override
    protected void initialize(Application app) {
        entities = getState(EntityAppState.class, true).getEntityData().getEntities(requestType);
        super.initialize(app);
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
        refresh();
    }
    
    protected T create(Entity e) {
        var object = factory.manufacture(getRequestComponent(e).getRequest(), e);
        return link(e.getId(), object) ? object : null;
    }
    protected T destroy(Entity e) {
        return unlink(e.getId());
    }
    @Override
    public T get(EntityId id) {
        refresh();
        return super.get(id);
    }
    
    public void refresh() {
        if (entities.applyChanges()) {
            entities.getAddedEntities().stream().forEach(e -> create(e));
            entities.getRemovedEntities().stream().forEach(e -> destroy(e));
        }
    }
    
    protected R getRequestComponent(Entity e) {
        return e.get(requestType);
    }
    protected R getRequestComponent(EntityId e) {
        return ed.getComponent(e, requestType);
    }
    
}
