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
public class Visual implements EntityComponent {
    
    private final String request;
    
    public Visual(String request) {
        this.request = request;
    }
    
    public String getModelRequest() {
        return request;
    }
    @Override
    public String toString() {
        return "Visual[model="+request+"]";
    }
    
}
