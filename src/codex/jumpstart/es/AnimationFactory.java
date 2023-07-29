/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package codex.jumpstart.es;

import codex.boost.scene.SceneGraphIterator;
import com.jme3.anim.AnimComposer;
import com.jme3.scene.Spatial;
import com.simsilica.es.Entity;

/**
 *
 * @author codex
 */
public interface AnimationFactory {
    
    /**
     * Configures animations for the spatial.
     * @param request animation configuration
     * @param entity entity associated with the animations
     */
    public void create(String request, Entity entity);
    
    public static Spatial getAnimatedSpatial(Spatial root) {
        for (Spatial spatial : new SceneGraphIterator(root)) {
            if (spatial.getControl(AnimComposer.class) != null) {
                return spatial;
            }
        }
        return null;
    }
    
}
