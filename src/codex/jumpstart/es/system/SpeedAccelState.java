/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.jumpstart.es.system;

import codex.jumpstart.es.ESAppState;
import codex.jumpstart.es.components.Accelleration;
import codex.jumpstart.es.components.TargetSpeed;
import codex.jumpstart.es.components.WalkSpeed;
import com.jme3.app.Application;
import com.jme3.math.FastMath;
import com.simsilica.es.Entity;
import com.simsilica.es.EntitySet;

/**
 *
 * @author codex
 */
public class SpeedAccelState extends ESAppState {

    private EntitySet entities;
    
    @Override
    protected void init(Application app) {
        entities = ed.getEntities(WalkSpeed.class, TargetSpeed.class, Accelleration.class);
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
        entities.applyChanges();
        for (Entity e : entities) {
            var current = e.get(WalkSpeed.class).getSpeed();
            var target = e.get(TargetSpeed.class).getTarget();
            if (current == target) continue;
            var accelleration = (current < target ? e.get(Accelleration.class).getAccel() : -e.get(Accelleration.class).getDecel());
            if (FastMath.abs(target-current) <= accelleration) {
                e.set(new WalkSpeed(target));
            }
            else {
                e.set(new WalkSpeed(current+accelleration));
            }
        }
    }
    
}
