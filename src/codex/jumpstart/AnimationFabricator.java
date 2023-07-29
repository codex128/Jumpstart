/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.jumpstart;

import com.jme3.anim.AnimComposer;
import com.jme3.anim.SkinningControl;
import com.jme3.anim.tween.Tweens;
import com.jme3.anim.tween.action.BaseAction;
import com.jme3.anim.tween.action.ClipAction;
import com.jme3.anim.tween.action.LinearBlendSpace;
import com.jme3.scene.Spatial;

/**
 *
 * @author codex
 */
public class AnimationFabricator {
    
    public static void fabricate(Object user, Spatial spatial) {
        var anim = spatial.getControl(AnimComposer.class);
        var skin = spatial.getControl(SkinningControl.class);
        var layerControl = spatial.getControl(AnimLayerControl.class);
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
                    Tweens.callMethod(user, "playGunShot")
                )
            )
        )));
        anim.action("shoot-cycle").setSpeed(2);
        ((ClipAction)anim.action("draw-pistol")).setMaxTransitionWeight(.7);
        anim.addAction("draw-pistol-once", new BaseAction(Tweens.parallel(
            Tweens.sequence(
                anim.action("draw-pistol"),
                Tweens.callMethod(user, "enableAimShifting", true),
                Tweens.callMethod(user, "switchCameraModes"),
                Tweens.callMethod(layerControl, "enter", "gun", "shoot-cycle")
            ),
            Tweens.sequence(
                Tweens.delay(.5f),
                Tweens.callMethod(user, "putGunInHand")
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
                Tweens.callMethod(user, "putGunInHolster")
            )
        )));
        anim.action("holster-pistol-once").setSpeed(4);
        anim.action("sneaking").setSpeed(.7);
        anim.addAction("die-impact", new BaseAction(Tweens.parallel(
            anim.action("killed"),
            Tweens.sequence(
                Tweens.delay(1),
                Tweens.callMethod(user, "startRagdollPhysics")
            )
        )));
    }
    public static void fabricateIdle(Object user, Spatial spatial) {
        
    }
    
}
