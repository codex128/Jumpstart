/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.jumpstart.es.system;

import codex.jumpstart.es.ESAppState;
import codex.jumpstart.es.components.Mass;
import codex.jumpstart.es.components.Physics;
import codex.jumpstart.es.components.Visual;
import codex.jumpstart.es.registry.PhysicsRegistry;
import codex.jumpstart.es.registry.Registry;
import com.jme3.app.Application;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.scene.Spatial;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;

/**
 *
 * @author codex
 */
public class RigidBodyState extends ESAppState {
    
    private EntitySet entities;
    private PhysicsRegistry physics;
    private Registry<Spatial> visuals;
    
    @Override
    protected void init(Application app) {
        entities = ed.getEntities(Visual.class, Physics.class, Mass.class);
        physics = getState(PhysicsRegistry.class, true);
        visuals = getState(VisualState.class, true);
    }
    @Override
    protected void cleanup(Application app) {
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
    
    public void refresh() {
        if (entities.applyChanges()) {
            entities.getAddedEntities().stream().forEach(e -> createBody(e));
            entities.getRemovedEntities().stream().forEach(e -> removeBody(e));
        }
    }
    public RigidBodyControl refresh(EntityId fetch) {
        refresh();
        return get(fetch);
    }
    public RigidBodyControl get(EntityId id) {
        var spatial = visuals.get(id);
        if (spatial == null) return null;
        return spatial.getControl(RigidBodyControl.class);
    }
    
    private void createBody(Entity e) {
        var rbc = new RigidBodyControl(e.get(Mass.class).getMass());
        var spatial = visuals.get(e.getId());
        spatial.addControl(rbc);
        physics.link(e.getId(), rbc, true);
    }
    private void removeBody(Entity e) {
        var spatial = visuals.get(e.getId());
        var rbc = spatial.getControl(RigidBodyControl.class);
        spatial.removeControl(rbc);
        physics.unlink(e.getId(), true);
    }
    
}
