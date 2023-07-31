/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.jumpstart.es.factory;

import java.util.HashMap;

/**
 *
 * @author codex
 * @param <T>
 */
public class AssemblyLineFactory <T> implements Factory<T> {
    
    private final HashMap<String, AssemblyLine<T>> lines = new HashMap<>();
    
    @Override
    public T manufacture(String request) {
        return get(request).assemble();
    }
    
    public boolean add(String request, AssemblyLine<T> line) {
        return lines.putIfAbsent(request, line) == null;
    }
    public AssemblyLine<T> get(String request) {
        return lines.get(request);
    }
    
}
