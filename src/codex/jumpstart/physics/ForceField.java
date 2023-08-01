/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package codex.jumpstart.physics;

import com.jme3.bounding.BoundingVolume;
import com.jme3.bullet.objects.PhysicsRigidBody;

/**
 *
 * @author codex
 */
public interface ForceField {
    
    /**
     * Get the volume of influence for this force.
     * <p>Bodies inside the volume will be affected by {@link applyForce}.
     * @return volume of influence, or null for omnipresent force.
     */
    public BoundingVolume getInfluenceVolume();
    
    /**
     * Applies the force represented by this force field to the body.
     * @param body 
     */
    public void applyForce(PhysicsRigidBody body);
    
    /**
     * Returns true if this force is omnipresent.
     * <p>Omnipresent in this case means that all bodies (no matter
     * their location) are influenced by {@code applyForce}.
     * @return true if omnipresent
     */
    public default boolean isOmnipresent() {
        return getInfluenceVolume() == null;
    }
    
}
