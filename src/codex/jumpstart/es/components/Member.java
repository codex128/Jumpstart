/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.jumpstart.es.components;

import codex.jumpstart.es.registry.Registry;
import com.simsilica.es.EntityComponent;
import java.util.Arrays;

/**
 * Component which defines which registries
 * this entity is linked to.
 * 
 * Is used mainly to remove data from these registries when
 * the entity is removed.
 * 
 * @author codex
 */
public class Member implements EntityComponent {
    
    private final Class<? extends Registry>[] registrations;
    
    public Member(Class<? extends Registry>... registrations) {
        this.registrations = registrations;
    }
    
    public Class<? extends Registry>[] getRegistrations() {
        return registrations;
    }
    @Override
    public String toString() {
        return "Member["+Arrays.toString(registrations)+"]";
    }
    
}
