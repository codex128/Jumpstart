/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.jumpstart.es.system;

import codex.jumpstart.es.ESAppState;
import codex.jumpstart.es.components.CharacterShape;
import codex.jumpstart.es.components.Physics;
import codex.jumpstart.es.components.Visual;
import codex.jumpstart.es.registry.PhysicsRegistry;
import com.jme3.app.Application;
import com.jme3.bullet.control.BetterCharacterControl;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;

/**
 *
 * @author codex
 */
public class PhysicalCharacterState extends ESAppState {

    private EntitySet characters;
    private PhysicsRegistry physics;
    private VisualState visuals;
    
    @Override
    protected void init(Application app) {
        characters = ed.getEntities(Visual.class, Physics.class, CharacterShape.class);
        physics = getState(PhysicsRegistry.class, true);
        visuals = getState(VisualState.class, true);
    }
    @Override
    protected void cleanup(Application app) {
        super.cleanup();
        characters.release();
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
        if (characters.applyChanges()) {
            characters.getAddedEntities().stream().forEach(e -> createCharacter(e));
            characters.getRemovedEntities().stream().forEach(e -> removeCharacter(e));
        }
    }
    public BetterCharacterControl refresh(EntityId fetch) {
        refresh();
        return getCharacterControl(fetch);
    }
    public BetterCharacterControl getCharacterControl(EntityId id) {
        return visuals.getControl(id, BetterCharacterControl.class);
    }
    
    private void createCharacter(Entity e) {
        var shape = e.get(CharacterShape.class);
        var bcc = new BetterCharacterControl(shape.getRadius(), shape.getHeight(), shape.getMass());
        visuals.get(e.getId()).addControl(bcc);
        physics.link(e.getId(), bcc.getRigidBody());
        physics.getPhysicsSpace().add(bcc);
    }
    private void removeCharacter(Entity e) {
        var bcc = visuals.getControl(e.getId(), BetterCharacterControl.class);
        if (bcc == null) return;
        bcc.getSpatial().removeControl(bcc);
        physics.unlink(e.getId());
        physics.getPhysicsSpace().remove(bcc);
    }
    
}
