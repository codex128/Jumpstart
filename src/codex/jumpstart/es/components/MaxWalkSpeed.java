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
public class MaxWalkSpeed implements EntityComponent {
    
    private final float speed;
    
    public MaxWalkSpeed(float speed) {
        assert speed >= 0;
        this.speed = speed;
    }

    public float getSpeed() {
        return speed;
    }
    @Override
    public String toString() {
        return "MaxMoveSpeed{" + "speed=" + speed + '}';
    }
    
}
