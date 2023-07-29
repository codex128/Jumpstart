/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.jumpstart.es;

import com.jme3.scene.Spatial;

/**
 *
 * @author codex
 */
public interface ModelFactory {
    
    public Spatial create(String request);
    
}
