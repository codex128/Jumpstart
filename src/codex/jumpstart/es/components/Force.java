/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.jumpstart.es.components;

import com.simsilica.es.EntityComponent;

/**
 *
 * @author codex
 */
public class Force implements EntityComponent {
    
    private final float force;

    public Force(float force) {
        this.force = force;
    }

    public float getForce() {
        return force;
    }

    @Override
    public String toString() {
        return "Force{" + "force=" + force + '}';
    }
    
    
    
}
