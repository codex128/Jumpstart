/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package codex.jumpstart.es.factory;

import com.simsilica.es.Entity;

/**
 *
 * @author codex
 * @param <T>
 */
public interface Factory <T> {
    
    public T manufacture(String request, Entity entity);
    
}
