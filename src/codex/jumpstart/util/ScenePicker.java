/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.jumpstart.util;

import codex.boost.scene.SceneGraphIterator;
import com.jme3.collision.Collidable;
import com.jme3.collision.CollisionResults;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import java.util.Arrays;
import java.util.Collection;

/**
 *
 * @author codex
 */
public class ScenePicker {
    
    Spatial root;
    Collection<Spatial> ignore;
    
    public ScenePicker(Spatial root) {
        this.root = root;
    }
    public ScenePicker(Spatial root, Collection<Spatial> ignore) {
        this.root = root;
        this.ignore = ignore;
    }
    public ScenePicker(Spatial root, Spatial... ignore) {
        this.root = root;
        this.ignore = Arrays.asList(ignore);
    }
    
    public CollisionResults collide(Collidable c) {
        CollisionResults res = new CollisionResults();
        if (ignore == null || ignore.isEmpty()) {
            root.collideWith(c, res);
            return res;
        }
        var it = new SceneGraphIterator(root);
        for (var spatial : it) {
            if (ignore.remove(spatial)) {
                it.ignoreChildren();
                continue;
            }
            if (spatial instanceof Geometry) {
                spatial.collideWith(c, res);
            }
        }
        return res;
    }
    
}
