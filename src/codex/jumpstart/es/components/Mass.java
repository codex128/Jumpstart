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
public class Mass implements EntityComponent {
    
    private final float mass;
    
    public Mass() {
        this(0f);
    }
    public Mass(float mass) {
        this.mass = mass;
    }

    public float getMass() {
        return mass;
    }
    @Override
    public String toString() {
        return "Mass{" + "mass=" + mass + '}';
    }
    
}
