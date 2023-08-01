/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.jumpstart.es.system;

import codex.jumpstart.es.ESAppState;
import codex.jumpstart.es.components.Mass;
import com.jme3.app.Application;
import com.jme3.math.Ray;
import com.simsilica.es.EntityComponent;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;

/**
 *
 * @author codex
 */
public class GunAppState extends ESAppState {
    
    private EntitySet bullets;
    
    @Override
    protected void init(Application app) {
        //bullets = ed.getEntities(types);
    }
    @Override
    protected void cleanup(Application app) {}
    @Override
    protected void onEnable() {}
    @Override
    protected void onDisable() {}
    
    public void shoot(EntityId gun, Ray trajectory) {
        if (!isInitialized()) return;
        Mass mass = get(gun, Mass.class);
        if (mass == null) {
            
        }
    }
    
    private <T extends EntityComponent> T get(EntityId id, Class<T> type) {
        return ed.getComponent(id, type);
    }
    
}
