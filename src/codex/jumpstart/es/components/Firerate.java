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
public class Firerate implements EntityComponent {
    
    private final float rps;

    public Firerate(float rps) {
        this.rps = rps;
    }
    
    /**
     * Rounds per second.
     * @return 
     */
    public float getRps() {
        return rps;
    }

    @Override
    public String toString() {
        return "Firerate{" + "rps=" + rps + '}';
    }
    
}
