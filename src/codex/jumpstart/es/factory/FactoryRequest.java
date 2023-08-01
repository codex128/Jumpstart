/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.jumpstart.es.factory;

import com.simsilica.es.EntityComponent;

/**
 *
 * @author codex
 */
public interface FactoryRequest extends EntityComponent {
    
    /**
     * Requests the default.
     */
    public static final String DEFAULT = "FactoryRequest:default";
    /**
     * Requests that no manufacturing be done.
     */
    public static final String CUSTOM = "FactoryRequest:custom";
    
    public String getRequest();
    
}
