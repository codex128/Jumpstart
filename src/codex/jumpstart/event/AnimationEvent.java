/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.jumpstart.event;

import com.jme3.anim.AnimComposer;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import com.simsilica.es.EntityId;

/**
 *
 * @author codex
 */
public class AnimationEvent {
    
    private final String name;
    private final EntityId entity;
    private final AnimComposer anim;
    
    public AnimationEvent(String name, EntityId entity, AnimComposer anim) {
        this.name = name;
        this.entity = entity;
        this.anim = anim;
    }
    
    public String getName() {
        return name;
    }
    public EntityId getEntityId() {
        return entity;
    }
    /**
     * The spatial specifically animated by AnimComposer.
     * @return 
     */
    public Spatial getAnimatedSpatial() {
        return anim.getSpatial();
    }
    public AnimComposer getAnimComposer() {
        return anim;
    }
    /**
     * Get a control from the animated spatial.
     * @param <T>
     * @param type
     * @return 
     */
    public <T extends Control> T getControl(Class<T> type) {
        return getAnimatedSpatial().getControl(type);
    }
    
    public static EventPrefab createPrefab(EntityId id, AnimComposer anim) {
        return new EventPrefab(id, anim);
    }
    public static class EventPrefab extends AnimationEvent {
        
        private EventPrefab(EntityId entity, AnimComposer anim) {
            super("EventPrefab", entity, anim);
        }
        
        public AnimationEvent create(String name) {
            return new AnimationEvent(name, getEntityId(), getAnimComposer());
        }
        
    }
    
}
