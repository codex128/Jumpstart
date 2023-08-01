/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.jumpstart;

import codex.jumpstart.es.ESAppState;
import codex.jumpstart.es.components.Animation;
import codex.jumpstart.es.components.Visual;
import codex.jumpstart.es.factory.FactoryRequest;
import codex.jumpstart.es.registry.AnimationRegistry;
import codex.jumpstart.es.system.VisualState;
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
                new Visual(FactoryRequest.CUSTOM),
                new Animation());
        // model
        var spatial = assetManager.loadModel("Models/characters/YBot.j3o");  
        spatial.setLocalScale(.01f);
        spatial.setCullHint(Spatial.CullHint.Never); // since this spatial will presumably always be in focus
        // animation
        var animated = AnimationConfig.fetchAnimatedSpatial(spatial);
        AnimationConfig.configurePlayerAnimations(player, animated, getState(AnimEventState.class));
        // registering
        getState(VisualState.class).link(player, spatial);
        getState(AnimationRegistry.class).link(player, animated);
        
    }
    @Override
    protected void cleanup(Application app) {}
    @Override
    protected void onEnable() {}
    @Override
    protected void onDisable() {}
    
}
