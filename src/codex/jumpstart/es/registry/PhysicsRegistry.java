/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.jumpstart.es.registry;

import codex.jumpstart.es.registry.Registry;
import com.jme3.app.Application;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import java.util.HashMap;

/**
 *
 * @author codex
 */
public class PhysicsRegistry extends Registry<PhysicsRigidBody> {
    
    private BulletAppState bulletapp;
    private EntitySet physics;
    private final HashMap<EntityId, PhysicsRigidBody> registry = new HashMap<>();
    
    @Override
    protected void init(Application app) {
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
    @Override
    public void update(float tpf) {
        if (physics.applyChanges()) {
            physics.getRemovedEntities().stream().forEach(e -> unlink(e.getId()));
        }
    }
    @Override
    public boolean link(EntityId id, PhysicsRigidBody body) {
        return registry.putIfAbsent(id, body) == null;
    }
    @Override
    public PhysicsRigidBody unlink(EntityId id) {
        return registry.remove(id);
    }
    @Override
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
