/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package codex.jumpstart;

import codex.boost.scene.SceneGraphIterator;
import codex.jumpstart.AnimLayerControl;
import codex.jumpstart.event.AnimEventListener;
import codex.jumpstart.event.AnimationEvent;
import com.jme3.anim.AnimComposer;
import com.jme3.anim.SkinningControl;
import com.jme3.anim.tween.Tweens;
import com.jme3.anim.tween.action.BaseAction;
import com.jme3.anim.tween.action.ClipAction;
import com.jme3.anim.tween.action.LinearBlendSpace;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import com.simsilica.es.EntityId;

/**
 *
 * @author codex
 */
public class AnimationConfig {
    
    public static void configurePlayerAnimations(EntityId entity, Spatial animated, AnimEventListener events) {
        // get animation fields
        var anim = getControl(animated, AnimComposer.class);
        var skin = getControl(animated, SkinningControl.class);
        var layerControl = new AnimLayerControl();
        animated.addControl(layerControl);
        var prefab = AnimationEvent.createPrefab(entity, anim);
        // configure animation layers
        layerControl.createSet(anim, skin, mask -> mask.addAll(),
                "idle", "move", "gun", "jump", "death");
        // freeze action
        anim.addAction("freeze", new BaseAction(anim.action("idle")));
        anim.action("freeze").setSpeed(0);
        // idle action
        var idle = (ClipAction)anim.action("idle");
        idle.setMaxTransitionWeight(.5);
        layerControl.enter("idle", "idle");
        // walk/run action
        var walkRun = anim.actionBlended("walk->run", new LinearBlendSpace(0f, 1f), "walk", "sprint");
        walkRun.setMaxTransitionWeight(.5);
        // landing action
        anim.actionSequence("land-once",
            anim.action("landing"),
            Tweens.callMethod(layerControl, "exit", "jump")
        );
        // aiming and shooting actions
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
        // dying action
        anim.addAction("die-impact", new BaseAction(Tweens.parallel(
            anim.action("killed"),
            Tweens.sequence(
                Tweens.delay(1),
                events.tween(prefab.create("startRagdollPhysics"))
            )
        )));
    }
    
    public static <T extends Control> T getControl(Spatial spatial, Class<T> type) {
        var control = spatial.getControl(type);
        if (control == null) {
            throw new NullPointerException("Spatial does not have "+type.getSimpleName()+"!");
        }
        return control;
    }
    public static Spatial fetchAnimatedSpatial(Spatial root) {
        for (Spatial spatial : new SceneGraphIterator(root)) {
            if (spatial.getControl(AnimComposer.class) != null) {
                return spatial;
            }
        }
        return null;
    }
    
}
