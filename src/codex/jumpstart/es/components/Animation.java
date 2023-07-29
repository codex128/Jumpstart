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
public class Animation implements EntityComponent {
    
    private final String request;
    
    public Animation(String request) {
        this.request = request;
    }
    
    public String getFactoryRequest() {
        return request;
    }
    @Override
    public String toString() {
        return "Animation[request="+request+"]";
    }
    
}
