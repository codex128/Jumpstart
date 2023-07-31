/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.jumpstart.es;

import codex.boost.GameAppState;
import com.jme3.app.Application;
import com.simsilica.es.EntityData;

/**
 *
 * @author codex
 */
public abstract class ESAppState extends GameAppState {
    
    protected EntityData ed;
    
    @Override
    protected void initialize(Application app) {
        ed = getState(EntityAppState.class, true).getEntityData();
        super.initialize(app);
    }
    
}
