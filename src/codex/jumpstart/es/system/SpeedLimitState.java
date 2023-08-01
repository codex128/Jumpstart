/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.jumpstart.es.system;

import codex.jumpstart.es.ESAppState;
import codex.jumpstart.es.components.MaxWalkSpeed;
import codex.jumpstart.es.components.WalkSpeed;
import com.jme3.app.Application;
import com.simsilica.es.Entity;
import com.simsilica.es.EntitySet;

/**
 *
 * @author codex
 */
public class SpeedLimitState extends ESAppState {
    
    private EntitySet entities;
    
    @Override
    protected void init(Application app) {
        entities = ed.getEntities(WalkSpeed.class, MaxWalkSpeed.class);
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
        if (entities.applyChanges()) {
            for (Entity e : entities.getChangedEntities()) {
                float max = e.get(MaxWalkSpeed.class).getSpeed();
                if (e.get(WalkSpeed.class).getSpeed() > max) {
                    e.set(new WalkSpeed(max));
                }
            }
        }
    }
    
}
