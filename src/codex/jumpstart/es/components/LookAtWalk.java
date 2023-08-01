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
public class LookAtWalk implements EntityComponent {
    
    private final boolean yes;
    
    public LookAtWalk(boolean yes) {
        this.yes = yes;
    }
    
    public boolean isYes() {
        return yes;
    }

    @Override
    public String toString() {
        return "LookAtWalk{" + "yes=" + yes + '}';
    }
    
}
