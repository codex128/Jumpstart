/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.jumpstart.es.components;

import com.simsilica.es.EntityComponent;
import com.simsilica.es.EntityId;

/**
 *
 * @author codex
 */
public class Hand implements EntityComponent {
    
    private final EntityId hold;
    
    public Hand() {
        this(null);
    }
    public Hand(EntityId hold) {
        this.hold = hold;
    }

    public EntityId getHeldEntity() {
        return hold;
    }
    
}
