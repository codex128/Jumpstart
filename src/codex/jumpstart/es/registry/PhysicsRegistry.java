/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.jumpstart.es.registry;

import com.jme3.app.Application;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.simsilica.es.EntityId;

/**
 * Physics registry.
 * 
 * <p>This works mainly as a backend for other systems to register
 * physical bodies with. As a result, there isn't much functionality
 * beyond just being a registry.
 * 
 * @author codex
 */
public class PhysicsRegistry extends Registry<PhysicsRigidBody> {
    
    private BulletAppState bulletapp;
    
    public PhysicsRegistry() {
        
    }
    
    @Override
    protected void init(Application app) {
        bulletapp = getState(BulletAppState.class, true);
    }
    @Override
    protected void onEnable() {}
    @Override
    protected void onDisable() {}
    
    public boolean link(EntityId id, PhysicsRigidBody body, boolean addToSpace) {
        var linked = link(id, body);
        if (linked && addToSpace) getPhysicsSpace().add(body);
        return linked;
    }
    public PhysicsRigidBody unlink(EntityId id, boolean removeFromSpace) {
        var body = unlink(id);
        if (removeFromSpace) getPhysicsSpace().remove(body);
        return body;
    }
    
    public BulletAppState getBulletAppState() {
        return bulletapp;
    }
    public PhysicsSpace getPhysicsSpace() {
        return bulletapp.getPhysicsSpace();
    }
    
}
