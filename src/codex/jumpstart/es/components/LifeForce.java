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
public class LifeForce implements EntityComponent {
    
    private final float life;
    
    public LifeForce(float life) {
        this.life = Math.max(life, 0f);
    }
    
    public float getLife() {
        return life;
    }
    public boolean isAlive() {
        return life > 0;
    }
    @Override
    public String toString() {
        return "LifeForce{life="+life+"}";
    }
    
}
