/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package codex.jumpstart.es.registry;

import codex.jumpstart.es.ESAppState;
import com.jme3.app.Application;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityComponent;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import java.util.HashMap;

/**
 *
 * @author codex
 * @param <T>
 */
public abstract class Registry <T> extends ESAppState {
    
    protected final HashMap<EntityId, T> registry = new HashMap<>();
    protected Class<? extends EntityComponent> identifier;
    protected EntitySet entities;
    
    public Registry() {}
    public Registry(Class<? extends EntityComponent> identifier) {
        
    }
    
    @Override
    protected void initialize(Application app) {
        super.initialize(app);
        if (identifier != null) {
            initializeAutoIdentity(identifier);
        }
    }
    @Override
    protected void cleanup(Application app) {
        entities.release();
        registry.clear();
    }
    @Override
    public void update(float tpf) {
        refresh();
    }
    
    protected void initializeAutoIdentity(Class<? extends EntityComponent> identifier) {
        this.identifier = identifier;
        entities = ed.getEntities(this.identifier);        
    }
    public boolean autoIdentityEnabled() {
        return entities != null && identifier != null;
    }
    
    public void refresh() {
        if (autoIdentityEnabled() && entities.applyChanges()) {
            for (Entity e : entities.getRemovedEntities()) {
                unlink(e.getId());
            }
        }
    }
    public T refresh(EntityId fetch) {
        refresh();
        return get(fetch);
    }
    
    public boolean link(EntityId entity, T object) {
        return (!autoIdentityEnabled() || ed.getComponent(entity, identifier) != null)
                && registry.putIfAbsent(entity, object) == null;
    }
    public T unlink(EntityId entity) {
        return registry.remove(entity);
    }
    public T get(EntityId entity) {
        return registry.get(entity);
    }
    
}
