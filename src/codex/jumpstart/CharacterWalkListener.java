/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package codex.jumpstart;

import com.jme3.math.Vector3f;

/**
 *
 * @author codex
 */
public interface CharacterWalkListener {
    
    public void walkDirectionChanged(Vector3f oldDir, Vector3f newDir);
    public void moveSpeedChanged(float oldSpeed, float newSpeed, float delta);
    
    public default void viewDirectionChanged(Vector3f oldDir, Vector3f newDir) {}
    public default void velocityChanged(Vector3f oldVel, Vector3f newVel) {}
    
}
