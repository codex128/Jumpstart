/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.jumpstart.es.registry;

import codex.jumpstart.es.ESAppState;
import codex.jumpstart.es.components.Member;
import com.jme3.app.Application;
import com.simsilica.es.Entity;
import com.simsilica.es.EntitySet;

/**
 * Unlinks all data for an entity when it is destroyed or
 * its {@link Member} component is removed.
 * 
 * @author codex
 */
public class RegistryEscapeState extends ESAppState {

    private EntitySet members;
    
    @Override
    protected void init(Application app) {
        members = ed.getEntities(Member.class);
    }
    @Override
    protected void cleanup(Application app) {
        members.release();
    }
    @Override
    protected void onEnable() {}
    @Override
    protected void onDisable() {}
    @Override
    public void update(float tpf) {
        if (members.applyChanges()) for (Entity e : members.getRemovedEntities()) {
            for (var type : e.get(Member.class).getRegistrations()) {
                var registry = getState(type);
                if (registry == null) continue;
                registry.unlink(e.getId());                
            }
        }
    }
    
}
