/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.jumpstart;

import com.jme3.bullet.control.BetterCharacterControl;

/**
 *
 * @author codex
 */
public class MyCharacterControl extends BetterCharacterControl {
    
    private final float radius, height;
    
    public MyCharacterControl(float radius, float height, float mass) {
        super(radius, height, mass);
        this.radius = radius;
        this.height = height;
    }
    
    public float getRadius() {
        return radius;
    }
    public float getHeight() {
        return height;
    }
    
}
