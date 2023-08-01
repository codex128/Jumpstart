/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.jumpstart.es.components;

import com.simsilica.es.EntityComponent;

/**
 * Defines the accuracy of the entity.
 * 
 * <p>It is better to think about this as inaccuracy, instead.
 * Because high numbers are less accurate than low numbers.
 * 
 * @author codex
 */
public class Accuracy implements EntityComponent {
    
    private final float accuracy;
    
    public Accuracy(float accuracy) {
        this.accuracy = accuracy;
    }

    public float getAccuracy() {
        return accuracy;
    }

    @Override
    public String toString() {
        return "Accuracy{" + "accuracy=" + accuracy + '}';
    }
    
    
    
}
