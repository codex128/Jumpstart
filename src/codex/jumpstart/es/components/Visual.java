/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.jumpstart.es.components;

import codex.jumpstart.es.factory.FactoryRequest;

/**
 *
 * @author codex
 */
public class Visual implements FactoryRequest {
    
    private final String request;
    
    public Visual(String request) {
        this.request = request;
    }
    
    @Override
    public String getRequest() {
        return request;
    }
    @Override
    public String toString() {
        return "Visual[request="+request+"]";
    }
    
}
