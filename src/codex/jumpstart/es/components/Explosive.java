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
public class Explosive implements EntityComponent {
    
    private final float countdown;
    private final boolean timer; // this is here for Filters.fieldEquals(...)

    public Explosive() {
        this(-1);
    }
    public Explosive(float countdown) {
        this.countdown = countdown;
        timer = this.countdown >= 0;
    }

    public float getCountdown() {
        return countdown;
    }
    public boolean isOnTimer() {
        return timer;
    }
    public boolean explode() {
        return countdown <= 0;
    }
    
    public Explosive count(float tpf) {
        if (!isOnTimer()) return new Explosive();
        return new Explosive(Math.max(0f, countdown-tpf));
    }

    @Override
    public String toString() {
        return "Explosive{" + "countdown=" + countdown + '}';
    }
    
}
