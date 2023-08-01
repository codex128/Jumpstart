/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.jumpstart.es.system;

import codex.jumpstart.es.ESAppState;
import codex.jumpstart.es.components.LookAtWalk;
import codex.jumpstart.es.components.ViewDirection;
import codex.jumpstart.es.components.WalkDirection;
import com.jme3.app.Application;
import com.jme3.math.Vector3f;
import com.simsilica.es.Entity;
import com.simsilica.es.EntitySet;
import com.simsilica.es.Filters;

/**
 *
 * @author codex
 */
public class ViewMatchWalkState extends ESAppState {

    private EntitySet entities;
    
    @Override
    protected void init(Application app) {
        entities = ed.getEntities(
                Filters.fieldEquals(LookAtWalk.class, "yes", true),
                LookAtWalk.class,
                WalkDirection.class,
                ViewDirection.class);
    }
    @Override
    protected void cleanup(Application app) {}
    @Override
    protected void onEnable() {}
    @Override
    protected void onDisable() {}
    @Override
    public void update(float tpf) {
        if (entities.applyChanges()) {
            var walk = new Vector3f();
            var view = new Vector3f();
            for (Entity e : entities.getChangedEntities()) {
                walk.set(e.get(WalkDirection.class).getDirection());
                view.set(e.get(ViewDirection.class).getDirection());
                if (!walk.equals(view)) {
                    e.set(new ViewDirection(walk));
                }
            }
        }
    }
    
}
