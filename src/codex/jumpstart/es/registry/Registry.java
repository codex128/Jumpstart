/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package codex.jumpstart.es.registry;

import codex.jumpstart.es.ESAppState;
import com.simsilica.es.EntityId;
import java.util.HashMap;

/**
 *
 * @author codex
 * @param <T>
 */
public abstract class Registry <T> extends ESAppState {
    
    protected final HashMap<EntityId, T> registry = new HashMap<>();
    
    public boolean link(EntityId entity, T object) {
        return registry.putIfAbsent(entity, object) == null;
    }
    public T unlink(EntityId entity) {
        return registry.remove(entity);
    }
    public T get(EntityId entity) {
        return registry.get(entity);
    }
    
}
