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
public class TargetSpeed implements EntityComponent {
    
    private final float target;

    public TargetSpeed(float target) {
        this.target = target;
    }

    public float getTarget() {
        return target;
    }

    @Override
    public String toString() {
        return "TargetSpeed{" + "target=" + target + '}';
    }
    
}
