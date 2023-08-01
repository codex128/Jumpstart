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
public class ProjectileType implements EntityComponent {
    
    public static enum Type {    
        /**
         * Projectile is represented and calculated using {@link Ray}.
         */
        Ray,
        
        /**
         * Projectile is represented and calculated using a physical object.
         */
        Object;
    }
    
    private final Type type;

    public ProjectileType(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    @Override
    public String toString() {
        return "ProjectileType{" + "type=" + type + '}';
    }
    
    
    
}
