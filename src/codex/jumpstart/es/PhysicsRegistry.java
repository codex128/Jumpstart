/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.jumpstart.es;

import com.jme3.app.Application;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.simsilica.es.EntityId;
import java.util.HashMap;

/**
 *
 * @author codex
 */
public class PhysicsRegistry extends ESAppState {
    
    private BulletAppState bulletapp;
    private final HashMap<EntityId, PhysicsRigidBody> registry = new HashMap<>();
    
    @Override
    protected void init(Application app) {
        super.init(app);
        bulletapp = getState(BulletAppState.class, true);
    }
    @Override
    protected void cleanup(Application app) {
        registry.clear();
    }
    @Override
    protected void onEnable() {}
    @Override
    protected void onDisable() {}
    
    public void link(EntityId id, PhysicsRigidBody body) {
        if (body != null) registry.put(id, body);
        else registry.remove(id);
    }
    public PhysicsRigidBody unlink(EntityId id) {
        return registry.remove(id);
    }
    
    public PhysicsRigidBody get(EntityId id) {
        return registry.get(id);
    }
    
    public BulletAppState getBulletAppState() {
        return bulletapp;
    }
    public PhysicsSpace getPhysicsSpace() {
        return bulletapp.getPhysicsSpace();
    }
    
}
