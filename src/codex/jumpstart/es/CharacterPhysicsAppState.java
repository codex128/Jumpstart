/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.jumpstart.es;

import codex.jumpstart.CharacterMovementControl;
import codex.jumpstart.es.components.PhysicsCharacter;
import com.jme3.app.Application;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.scene.Spatial;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import java.util.HashMap;

/**
 *
 * @author codex
 */
public class CharacterPhysicsAppState extends ESAppState {
    
    private PhysicsRegistry registry;
    private EntitySet entities;
    private HashMap<EntityId, BetterCharacterControl> characters = new HashMap<>();
    
    @Override
    protected void init(Application app) {
        super.init(app);
        registry = getState(PhysicsRegistry.class, true);
        entities = ed.getEntities(PhysicsCharacter.class);
    }
    @Override
    protected void cleanup(Application app) {
        entities.release();
        entities = null;
        characters.clear();
    }
    @Override
    protected void onEnable() {}
    @Override
    protected void onDisable() {}
    @Override
    public void update(float tpf) {
        if (entities.applyChanges()) {
            entities.getAddedEntities().stream().forEach(e -> createCharacter(e));
            entities.getRemovedEntities().stream().forEach(e -> removeCharacter(e));
        }
    }
    
    private void createCharacter(Entity e) {
        var data = e.get(PhysicsCharacter.class);
        var character = new BetterCharacterControl(data.getRadius(), data.getHeight(), data.getMass());
        Spatial spatial = getState(VisualState.class, true).getSpatial(e.getId());
        spatial.addControl(character);
        spatial.addControl(new CharacterMovementControl());
        characters.put(e.getId(), character);
        registry.register(e.getId(), character.getRigidBody());
        registry.getPhysicsSpace().add(character);
    }
    private void removeCharacter(Entity e) {
        var bcc = characters.remove(e.getId());
        registry.remove(e.getId());
        if (bcc != null) {
            registry.getPhysicsSpace().remove(bcc);
            bcc.getSpatial().removeControl(CharacterMovementControl.class);
            bcc.getSpatial().removeControl(bcc);
        }
    }
    
    public BetterCharacterControl getControl(EntityId id) {
        return characters.get(id);
    }
    public CharacterMovementControl getMovementControl(EntityId id) {
        var bcc = getControl(id);
        if (bcc != null) {
            return bcc.getSpatial().getControl(CharacterMovementControl.class);
        }
        return null;
    }
    
}
