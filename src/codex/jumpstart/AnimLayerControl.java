/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.jumpstart;

import codex.boost.control.SubControl;
import com.jme3.anim.AnimComposer;
import com.jme3.anim.AnimationMask;
import com.jme3.anim.SkinningControl;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import java.util.HashMap;
import java.util.function.Consumer;

/**
 *
 * @author codex
 */
public class AnimLayerControl extends SubControl<AnimComposer> {
    
    HashMap<String, LayerController> controllers = new HashMap<>();
    
    public AnimLayerControl() {
        super(AnimComposer.class);
    }
    public AnimLayerControl(boolean persistant) {
        super(AnimComposer.class, persistant);
    }
    
    @Override
    protected void subControlUpdate(float tpf) {}
    @Override
    protected void subControlRender(RenderManager rm, ViewPort vp) {}
    @Override
    protected void onDependencyCaptured() {
        for (LayerController lc : controllers.values()) {
            lc.setAnimComposer(dependency);
            lc.createIfAbsent();
        }
    }
    @Override
    protected void onDependencyReleased() {
        for (LayerController lc : controllers.values()) {
            lc.setAnimComposer(null);
        }
    }
    
    public boolean add(LayerController lc) {
        if (controllers.putIfAbsent(lc.getLayerName(), lc) == null) {
            if (dependency != null) {
                lc.setAnimComposer(dependency);
                lc.createIfAbsent();
            }
            return true;
        }
        return false;
    }
    public LayerController create(String layer, AnimationMask mask) {
        var lc = new LayerController(layer, mask);
        if (add(lc)) return lc;
        else return null;
    }
    public LayerController create(AlertArmatureMask mask) {
        return create(mask.getTargetLayer(), mask);
    }
    public void createSet(AnimComposer anim, SkinningControl skin, Consumer<AlertArmatureMask> config, String... layers) {
        for (String layer : layers) {
            var mask = new AlertArmatureMask(layer, anim, skin);
            config.accept(mask);
            create(layer, mask);
        }
    }
    
    public boolean remove(LayerController lc) {
        if (controllers.remove(lc.getLayerName(), lc)) {
            lc.setAnimComposer(null);
            return true;
        }
        return false;
    }
    public LayerController remove(String layer) {
        var lc = controllers.remove(layer);
        if (lc != null) {
            lc.setAnimComposer(null);
        }
        return lc;
    }
    public LayerController get(String layer) {
        return controllers.get(layer);
    }
    
    // fast-access methods
    public void enter(String layer, String action) {
        get(layer).enter(action);
    }
    public void exit(String layer) {
        get(layer).exit();
    }
    public boolean isActive(String layer) {
        return get(layer).isActive();
    }
    
}
