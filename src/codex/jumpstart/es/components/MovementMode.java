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
public class MovementMode implements EntityComponent {
    
    private final Class mode;
    
    public MovementMode(Class mode) {
        this.mode = mode;
    }
    
    public Class getMode() {
        return mode;
    }
    @Override
    public String toString() {
        return "MovementMode{" + "mode=" + mode + '}';
    }    
    
}
