/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.jumpstart.event;

import codex.boost.Listenable;
import codex.jumpstart.es.ESAppState;
import com.jme3.app.Application;
import java.util.Collection;
import java.util.LinkedList;

/**
 * State where {@link AnimEventListeners} can listen to all globally
 * broadcasted animation events.
 * 
 * @author codex
 */
public class AnimEventState extends ESAppState
        implements AnimEventListener, Listenable<AnimEventListener> {
    
    private LinkedList<AnimEventListener> listeners = new LinkedList<>();
    
    @Override
    protected void init(Application app) {}
    @Override
    protected void cleanup(Application app) {
        clearAllListeners();
    }
    @Override
    protected void onEnable() {}
    @Override
    protected void onDisable() {}
    @Override
    public void animationEvent(AnimationEvent event) {
        listeners.stream().forEach(l -> l.animationEvent(event));
    }
    @Override
    public Collection<AnimEventListener> getListeners() {
        return listeners;
    }
    
}
