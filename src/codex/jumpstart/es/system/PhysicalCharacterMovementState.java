/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.jumpstart.es.system;

import codex.jumpstart.CharacterMovementControl;
import codex.jumpstart.es.ESAppState;
import codex.jumpstart.es.components.MoveSpeed;
import codex.jumpstart.es.components.ViewDirection;
import codex.jumpstart.es.components.MoveDirection;
import codex.jumpstart.es.components.Visual;
import codex.jumpstart.es.registry.Registry;
import com.jme3.app.Application;
import com.jme3.bullet.objects.PhysicsCharacter;
import com.jme3.scene.Spatial;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;

/**
 *
 * @author codex
 */
public class PhysicalCharacterMovementState extends ESAppState {

    private EntitySet characters;
    private Registry<Spatial> spatials;
    
    @Override
    protected void init(Application app) {
        characters = ed.getEntities(
                Visual.class,
                PhysicsCharacter.class,
                MoveDirection.class,
                ViewDirection.class,
                MoveSpeed.class);
        spatials = getState(VisualState.class, true);
    }
    @Override
    protected void cleanup(Application app) {}
    @Override
    protected void onEnable() {}
    @Override
    protected void onDisable() {}
    @Override
    public void update(float tpf) {
        if (characters.applyChanges()) {
            for (Entity e : characters.getAddedEntities()) {
                var movement = new CharacterMovementControl();
                movement.setFaceWalkDirection(false); // we can use a system for that
                spatials.get(e.getId()).addControl(movement);
            }
            for (Entity e : characters.getChangedEntities()) {
                var movement = getMovementControl(e.getId());
                movement.setWalkDirection(e.get(MoveDirection.class).getDirection());
                movement.setViewDirection(e.get(ViewDirection.class).getDirection());
                movement.setWalkSpeed(e.get(MoveSpeed.class).getSpeed());
            }
            for (Entity e : characters.getRemovedEntities()) {
                var spatial = spatials.get(e.getId());
                if (spatial == null) continue;
                spatial.removeControl(CharacterMovementControl.class);
            }
        }
    }
    
    public CharacterMovementControl getMovementControl(EntityId id) {
        return spatials.get(id).getControl(CharacterMovementControl.class);
    }
    
}
