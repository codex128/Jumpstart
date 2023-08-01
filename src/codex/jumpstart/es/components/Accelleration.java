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
public class Accelleration implements EntityComponent {
    
    private final float accel, decel;

    public Accelleration(float accel) {
        this(accel, accel);
    }
    public Accelleration(float accel, float decel) {
        this.accel = accel;
        this.decel = decel;
    }

    public float getAccel() {
        return accel;
    }
    public float getDecel() {
        return decel;
    }

    @Override
    public String toString() {
        return "Accelleration{" + "accel=" + accel + ", decel=" + decel + '}';
    }
    
}
