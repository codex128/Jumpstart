/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.jumpstart.event;

import codex.boost.GameAppState;
import codex.boost.Listenable;
import com.jme3.anim.tween.Tween;
import com.jme3.anim.tween.Tweens;
import com.jme3.app.Application;
import java.util.Collection;
import java.util.LinkedList;

/**
 *
 * @author codex
 */
public class AnimEventState extends GameAppState implements Listenable<AnimEventListener>{

    private final LinkedList<AnimEventListener> listeners = new LinkedList<>();
    
    @Override
    protected void init(Application app) {}
    @Override
    protected void cleanup(Application app) {}
    @Override
    protected void onEnable() {}
    @Override
    protected void onDisable() {}
    @Override
    public Collection<AnimEventListener> getListeners() {
        return listeners;
    }
    
    public void event(AnimationEvent event) {
        notifyListeners(l -> l.animationEvent(event));
    }
    public Tween tween(AnimationEvent event) {
        return Tweens.callMethod(this, "event", event);
    }
    
}
