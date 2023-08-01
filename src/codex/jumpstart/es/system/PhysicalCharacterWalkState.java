/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.jumpstart.es.system;

import codex.jumpstart.CharacterWalkControl;
import codex.jumpstart.es.ESAppState;
import codex.jumpstart.es.components.CharacterShape;
import codex.jumpstart.es.components.WalkSpeed;
import codex.jumpstart.es.components.ViewDirection;
import codex.jumpstart.es.components.WalkDirection;
import codex.jumpstart.es.components.Visual;
import codex.jumpstart.es.registry.Registry;
import com.jme3.app.Application;
import com.jme3.scene.Spatial;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;

/**
 *
 * @author codex
 */
public class PhysicalCharacterWalkState extends ESAppState {

    private EntitySet characters;
    private Registry<Spatial> spatials;
    
    @Override
    protected void init(Application app) {
        characters = ed.getEntities(
                Visual.class,
                CharacterShape.class,
                WalkDirection.class,
                ViewDirection.class,
                WalkSpeed.class);
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
        refresh(true);
    }
    
    private void refresh(boolean routine) {
        if (characters.applyChanges()) {
            if (!routine) spatials.refresh();
            for (Entity e : characters.getAddedEntities()) {
                var movement = new CharacterWalkControl();
                movement.setFaceWalkDirection(false); // we can use a system for that
                spatials.get(e.getId()).addControl(movement);
            }
            for (Entity e : characters.getChangedEntities()) {
                var movement = getMovementControl(e.getId());
                movement.setWalkDirection(e.get(WalkDirection.class).getDirection());
                movement.setViewDirection(e.get(ViewDirection.class).getDirection());
                movement.setWalkSpeed(e.get(WalkSpeed.class).getSpeed());
            }
            for (Entity e : characters.getRemovedEntities()) {
                var spatial = spatials.get(e.getId());
                if (spatial == null) continue;
                spatial.removeControl(CharacterWalkControl.class);
            }
        }
    }
    public void refresh() {
        refresh(false);
    }
    public CharacterWalkControl refresh(EntityId fetch) {
        refresh(false);
        return getMovementControl(fetch);
    }
    
    public CharacterWalkControl getMovementControl(EntityId id) {
        return spatials.get(id).getControl(CharacterWalkControl.class);
    }
    
}
