/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.jumpstart.es;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.simsilica.es.EntityData;
import com.simsilica.es.base.DefaultEntityData;

/**
 *
 * @author codex
 */
public class EntityAppState extends BaseAppState {

    private final EntityData ed;
    
    public EntityAppState() {
        this(new DefaultEntityData());
    }
    public EntityAppState(EntityData ed) {
        this.ed = ed;
    }
    
    @Override
    protected void initialize(Application app) {}
    @Override
    protected void cleanup(Application app) {}
    @Override
    protected void onEnable() {}
    @Override
    protected void onDisable() {}
    
    public EntityData getEntityData() {
        return ed;
    }
    
}
