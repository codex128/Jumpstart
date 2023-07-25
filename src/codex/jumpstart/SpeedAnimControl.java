/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.jumpstart;

import codex.jmeutil.control.SubControl;
import com.jme3.anim.AnimComposer;
import com.jme3.anim.tween.action.Action;
import com.jme3.anim.tween.action.BlendAction;
import com.jme3.anim.tween.action.BlendableAction;
import com.jme3.math.Vector2f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;

/**
 *
 * @author codex
 */
public class SpeedAnimControl extends SubControl<AnimComposer> {
    
    private BlendAction action;
    private String layer;
    private final Vector2f domain = new Vector2f(0f, 1f);
    private float speed = 0f;
    private boolean useSpeedDirectly = false;
    
    public SpeedAnimControl(Class<AnimComposer> type) {
        super(type);
    }
    public SpeedAnimControl(Class<AnimComposer> type, boolean persistant) {
        super(type, persistant);
    }    

    @Override
    protected void subControlUpdate(float tpf) {
        checkInterrupted();
    }
    @Override
    protected void subControlRender(RenderManager rm, ViewPort vp) {}
    @Override
    protected void onDependencyCaptured() {}
    @Override
    protected void onDependencyReleased() {
        stopCurrentAction();
    }
    
    public void playAction(String name) {
        playAction(name, AnimComposer.DEFAULT_LAYER);
    }
    public void playAction(String name, String layer) {
        if (dependency == null) return;
        action = fetchAction(name);
        this.layer = layer;
        resetBlendValue();
        dependency.setCurrentAction(name, layer);
    }
    public void playAction(String name, String layer, float speed, float lower, float upper) {
        setSpeeds(speed, lower, upper);
        playAction(name, layer);
    }
    public void stopCurrentAction() {
        if (!isPlaying()) return;
        dependency.getLayer(layer).setCurrentAction(null);
        action = null;
        layer = null;
    }
    
    public void setSpeeds(float speed, float lower, float upper) {
        setSpeedDomain(lower, upper);
        setSpeed(speed);
    }
    public void setSpeedDomain(float lower, float upper) {
        assert lower < upper;
        if (lower != domain.x || upper != domain.y) {
            domain.set(lower, upper);
            speed = Math.min(Math.max(speed, lower), upper);
            resetBlendValue();
        }
    }
    public void setSpeed(float speed) {
        speed = Math.min(Math.max(speed, domain.x), domain.y);
        if (this.speed != speed) {
            this.speed = speed;
            resetBlendValue();
        }
    }
    public void setUseSpeedDirectly(boolean usd) {
        if (usd) {
            setSpeedDomain(0f, 1f);
            resetBlendValue();
        }
        useSpeedDirectly = usd;
    }
    
    public BlendAction getAction() {
        return action;
    }
    public String getTargetLayer() {
        return layer;
    }
    public Vector2f getSpeedDomain() {
        return domain;
    }
    public float getSpeed() {
        return speed;
    }
    public boolean isUseSpeedDirectly() {
        return useSpeedDirectly;
    }
    public boolean isPlaying() {
        return action != null && layer != null;
    }
    public boolean checkInterrupted() {
        if (isPlaying() && dependency.getLayer(layer).getCurrentAction() != action) {
            action = null;
            layer = null;
            return true;
        }
        return false;
    }

    private void resetBlendValue() {
        if (action == null) return;
        if (!useSpeedDirectly) {
            action.getBlendSpace().setValue(reverseLinearInterpolate(speed, domain.x, domain.y));
        }
        else {
            action.getBlendSpace().setValue(speed);
        }
    }
    private BlendAction fetchAction(String name) {
        if (dependency == null) return null;
        Action act = dependency.action(name);
        if (act == null) {
            throw new NullPointerException("No action \""+name+"\"!");
        }
        if (!(act instanceof BlendAction)) {
            throw new IllegalArgumentException("Action \""+name+"\" is not a BlendAction!");
        }
        return (BlendAction)act;
    }
    
    private static float reverseLinearInterpolate(float value, float a, float b) {
        float min = Math.min(a, b);
        return (value-min)/(Math.min(b, a)-min);
    }
    
}
