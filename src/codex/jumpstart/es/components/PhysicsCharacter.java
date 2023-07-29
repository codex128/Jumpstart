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
public class PhysicsCharacter implements EntityComponent {
    
    private final float radius, height, mass;
    
    public PhysicsCharacter(float radius, float height, float mass) {
        this.radius = radius;
        this.height = height;
        this.mass = mass;
    }
    
    public float getRadius() {
        return radius;
    }
    public float getHeight() {
        return height;
    }
    public float getMass() {
        return mass;
    }
    
}
