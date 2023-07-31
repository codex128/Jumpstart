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
     * Requests only the barest so that another system
     * can easily build whatever it needs on it.
     */
    public static final String CUSTOM = "FactoryRequest:custom";
    
    public String getRequest();
    
}
