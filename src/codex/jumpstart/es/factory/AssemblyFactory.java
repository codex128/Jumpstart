/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.jumpstart.es.factory;

import com.simsilica.es.Entity;
import java.util.HashMap;

/**
 *
 * @author codex
 * @param <T>
 */
public class AssemblyFactory <T> implements Factory<T> {
    
    private final HashMap<String, Assembler<T>> lines = new HashMap<>();
    
    @Override
    public T manufacture(String request, Entity entity) {
        return get(request).assemble(entity);
    }
    
    public boolean add(String request, Assembler<T> line) {
        return lines.putIfAbsent(request, line) == null;
    }
    public Assembler<T> get(String request) {
        return lines.get(request);
    }
    
}
