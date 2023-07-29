/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.jumpstart.es;

import codex.jumpstart.AnimLayerControl;
import codex.jumpstart.es.AnimationFactory;
import codex.jumpstart.es.ESAppState;
import codex.jumpstart.es.SpatialRegistry;
import codex.jumpstart.es.components.Animation;
import codex.jumpstart.event.AnimEventState;
import codex.jumpstart.event.AnimationEvent;
import com.jme3.anim.AnimComposer;
import com.jme3.anim.SkinningControl;
import com.jme3.anim.tween.Tweens;
import com.jme3.anim.tween.action.BaseAction;
import com.jme3.anim.tween.action.ClipAction;
import com.jme3.anim.tween.action.LinearBlendSpace;
import com.jme3.app.Application;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import java.util.HashMap;

/**
 *
 * @author codex
 */
public class AnimationAppState extends ESAppState implements AnimationFactory {

    private EntitySet entities;
    private HashMap<EntityId, Spatial> animations = new HashMap<>();
    private AnimationFactory factory;
    private SpatialRegistry visuals;
    private AnimEventState events;
    
    @Override
    protected void init(Application app) {
        super.init(app);
        entities = ed.getEntities(Animation.class);
        if (factory == null) factory = this;
        visuals = getState(SpatialRegistry.class, true);
        events = getState(AnimEventState.class, true);
    }
    @Override
    protected void cleanup(Application app) {
        entities.release();
        animations.clear();
    }
    @Override
    protected void onEnable() {}
    @Override
    protected void onDisable() {}
    @Override
    public void update(float tpf) {
        if (entities.applyChanges()) {
            entities.getAddedEntities().stream().forEach(e -> createAnimation(e));
            entities.getRemovedEntities().stream().forEach(e -> removeAnimation(e));
        }
    }
    
    private void createAnimation(Entity e) {
        Spatial spatial = visuals.getSpatial(e.getId());
        factory.create(e.get(Animation.class).getFactoryRequest(), e);
        animations.put(e.getId(), spatial);
    }
    private void removeAnimation(Entity e) {
        animations.remove(e.getId());
    }
    
    public Spatial getAnimatedSpatial(EntityId id) {
        return animations.get(id);
    }
    public <T extends Control> T getControl(EntityId id, Class<T> type) {
        var spatial = getAnimatedSpatial(id);
        if (spatial == null) return null;
        return spatial.getControl(type);
    }
    public AnimComposer getAnimComposer(EntityId id) {
        return getControl(id, AnimComposer.class);
    }
    public SkinningControl getSkinningControl(EntityId id) {
        return getControl(id, SkinningControl.class);
    }
    public AnimLayerControl getLayerControl(EntityId id) {
        return getControl(id, AnimLayerControl.class);
    }

    @Override
    public void create(String request, Entity entity) {
        switch (request) {
            case "hero": createHeroAnimations(entity);
            case "enemy": createEnemyAnimations(entity);
        }
    }
    private void createHeroAnimations(Entity e) {
        var animated = AnimationFactory.getAnimatedSpatial(visuals.getSpatial(e.getId()));
        var anim = animated.getControl(AnimComposer.class);
        var skin = animated.getControl(SkinningControl.class);
        var layerControl = new AnimLayerControl();
        animated.addControl(layerControl);
        var prefab = AnimationEvent.createPrefab(e.getId(), anim);
        layerControl.createSet(anim, skin, mask -> mask.addAll(),
                "idle", "move", "gun", "jump", "death");
        //layerControl.create("idle", allJoints);
        anim.addAction("freeze", new BaseAction(anim.action("idle")));
        anim.action("freeze").setSpeed(0);
        var idle = (ClipAction)anim.action("idle");
        idle.setMaxTransitionWeight(.5);
        layerControl.enter("idle", "idle");
        var walkRun = anim.actionBlended("walk->run", new LinearBlendSpace(0f, 1f), "walk", "sprint");
        walkRun.setMaxTransitionWeight(.5);
        anim.actionSequence("land-once",
            anim.action("landing"),
            Tweens.callMethod(layerControl, "exit", "jump")
        );
        ((ClipAction)anim.action("aim-pistol")).setMaxTransitionWeight(.8);
        ((ClipAction)anim.action("shoot-pistol")).setMaxTransitionWeight(.9);
        anim.addAction("shoot-cycle", new BaseAction(Tweens.sequence(
            Tweens.loopDuration(.2f, anim.action("aim-pistol")),
            Tweens.parallel(
                anim.action("shoot-pistol"),
                Tweens.sequence(
                    Tweens.delay(.1),
                    events.tween(prefab.create("playGunShot"))
                )
            )
        )));
        anim.action("shoot-cycle").setSpeed(2);
        ((ClipAction)anim.action("draw-pistol")).setMaxTransitionWeight(.7);
        anim.addAction("draw-pistol-once", new BaseAction(Tweens.parallel(
            Tweens.sequence(
                anim.action("draw-pistol"),
                events.tween(prefab.create("enableAimShifting")),
                events.tween(prefab.create("switchCameraModes")),
                Tweens.callMethod(layerControl, "enter", "gun", "shoot-cycle")
            ),
            Tweens.sequence(
                Tweens.delay(.5f),
                events.tween(prefab.create("putGunInHand"))
            )
        )));
        anim.action("draw-pistol-once").setSpeed(4);
        anim.addAction("holster-pistol-once", new BaseAction(Tweens.parallel(
            Tweens.sequence(
                Tweens.invert(anim.action("draw-pistol")),
                Tweens.callMethod(layerControl, "exit", "gun")
            ),
            Tweens.sequence(
                Tweens.delay(1.5f),
                events.tween(prefab.create("putGunInHolster"))
            )
        )));
        anim.action("holster-pistol-once").setSpeed(4);
        anim.action("sneaking").setSpeed(.7);
        anim.addAction("die-impact", new BaseAction(Tweens.parallel(
            anim.action("killed"),
            Tweens.sequence(
                Tweens.delay(1),
                events.tween(prefab.create("startRagdollPhysics"))
            )
        )));
    }
    private void createEnemyAnimations(Entity e) {
        // put enemy animations here
    }
    
}
