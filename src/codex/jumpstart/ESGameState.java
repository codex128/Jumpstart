/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.jumpstart;

import codex.jumpstart.es.AnimationConfig;
import codex.jumpstart.es.registry.AnimationRegistry;
import codex.jumpstart.es.ESAppState;
import codex.jumpstart.es.system.VisualState;
import codex.jumpstart.es.components.Member;
import codex.jumpstart.event.AnimEventState;
import com.jme3.app.Application;
import com.jme3.scene.Spatial;
import com.simsilica.es.EntityId;

/**
 *
 * @author codex
 */
public class ESGameState extends ESAppState {

    @Override
    protected void init(Application app) {
        
        // create player entity
        EntityId player = ed.createEntity();
        ed.setComponents(player,
                new Member(VisualState.class, AnimationRegistry.class));
        Spatial ybot = assetManager.loadModel("Models/characters/YBot.j3o");
        getState(VisualState.class, true).link(player, ybot);
        Spatial animated = AnimationConfig.getAnimatedSpatial(ybot);
        AnimationConfig.configurePlayerAnimations(player, animated, getState(AnimEventState.class, true));
        getState(AnimationRegistry.class, true).link(player, animated);
        
    }
    @Override
    protected void cleanup(Application app) {}
    @Override
    protected void onEnable() {}
    @Override
    protected void onDisable() {}
    
}
