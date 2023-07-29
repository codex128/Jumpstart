/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.jumpstart.es;

import codex.jumpstart.CharacterMovementControl;
import com.jme3.app.Application;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.scene.control.Control;
import com.simsilica.es.EntityId;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Objects;
import java.util.function.Predicate;

/**
 *
 * @author codex
 */
public class ObjectRegistry extends ESAppState {
    
    private HashMap<EntityId, ObjectBucket> objects = new HashMap<>();
    
    @Override
    protected void init(Application app) {}
    @Override
    protected void cleanup(Application app) {}
    @Override
    protected void onEnable() {}
    @Override
    protected void onDisable() {}
    
    public void link(EntityId id, Object object) {        
        var bucket = objects.get(id);
        if (bucket == null) {
            bucket = new ObjectBucket(id);
            objects.put(id, bucket);
        }
        bucket.addLast(object);
    }
    public void unlink(EntityId id, Object object) {
        var bucket = objects.get(id);
        if (bucket != null) {
            bucket.remove(object);
        }
    }
    public void unlink(EntityId id, Class type) {
        var bucket = objects.get(id);
        if (bucket != null) {
            bucket.removeFirst(obj -> type.isAssignableFrom(obj.getClass()));
        }
    }
    
    public <T> T get(EntityId id, Class<T> type) {
        var bucket = objects.get(id);
        if (bucket != null) {
            return (T)bucket.getFirstOccurance(obj -> type.isAssignableFrom(obj.getClass()));
        }
        return null;
    }
    
    public BetterCharacterControl getControl(EntityId id) {
        return objects.get(id);
    }
    public CharacterMovementControl getMovementControl(EntityId id) {
        var bcc = getControl(id);
        if (bcc != null) {
            return bcc.getSpatial().getControl(CharacterMovementControl.class);
        }
        return null;
    }
    
    public class ObjectBucket extends LinkedList<Object> {
        
        private EntityId owner;
        
        private ObjectBucket(EntityId owner) {
            super();
            this.owner = owner;
        }
        
        public boolean removeFirst(Predicate<Object> filter) {
            Objects.requireNonNull(filter);
            final Iterator<Object> each = iterator();
            while (each.hasNext()) {
                if (filter.test(each.next())) {
                    each.remove();
                    return true;
                }
            }
            return false;
        }
        public Object getFirstOccurance(Predicate<Object> filter) {
            Objects.requireNonNull(filter);
            final Iterator<Object> each = iterator();
            while (each.hasNext()) {
                var el = each.next();
                if (filter.test(el)) {
                    return el;
                }
            }
            return null;
        }
        
    }
    public class EntityObject {
        
        private String key;
        private Object object;
        
        private EntityObject(String key, Object object) {
            this.key = key;
            this.object = object;
        }
        
        public String getKey() {
            return key;
        }
        public Object getObject() {
            return object;
        }
        
    }
    
}
