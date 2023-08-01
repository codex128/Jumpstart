/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.jumpstart.es.components;

import com.jme3.math.Vector3f;
import com.simsilica.es.EntityComponent;

/**
 *
 * @author codex
 */
public class WalkDirection implements EntityComponent {
    
    private final Vector3f direction = new Vector3f(0f, 0f, 1f);
    
    public WalkDirection() {}
    public WalkDirection(Vector3f direction) {
        this.direction.set(direction);
    }
    
    public Vector3f getDirection() {
        return direction;
    }
    
}
