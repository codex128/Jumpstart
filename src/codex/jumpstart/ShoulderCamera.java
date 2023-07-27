/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.jumpstart;

import codex.boost.Motion;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

/**
 *
 * @author codex
 */
public class ShoulderCamera extends AbstractControl {
    
    private final Camera camera;
    private Vector3f translation;
    private Quaternion rotation;
    private Vector3f offset = new Vector3f(1f, 0f, -2f);
    private Vector3f target = new Vector3f(1f, 0f, 2f);
    private Motion motion = Motion.LERP;
    private float speed = .5f;
    
    public ShoulderCamera(Camera camera) {
        this.camera = camera;
    }
    
    @Override
    protected void controlUpdate(float tpf) {
        camera.setLocation(nextCameraLocation());
        camera.lookAt(transpose(target), Vector3f.UNIT_Y);
    }
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {}
    
    private Vector3f getCalcTranslation() {
        return translation != null ? translation : spatial.getWorldTranslation();
    }
    private Quaternion getCalcRotation() {
        return rotation != null ? rotation : spatial.getWorldRotation();
    }
    private Vector3f transpose(Vector3f vec) {
        return getCalcTranslation().add(getCalcRotation().mult(vec));
    }
    private Vector3f nextCameraLocation() {
        switch (motion) {
            case INSTANT -> {
                return transpose(offset);
            }
            case LERP -> {
                return FastMath.interpolateLinear(speed, camera.getLocation(), transpose(offset));
            }
            case LINEAR -> {
                return linearMotion(camera.getLocation(), transpose(offset), speed);
            }
        }
        return null;
    }
    private Vector3f linearMotion(Vector3f start, Vector3f end, float speed) {
        if (start.equals(end) || start.distanceSquared(end) < speed*speed) {
            return end;
        }
        return end.subtract(start).normalizeLocal().multLocal(speed);
    }
    
    public void setSubjectTranslation(Vector3f translation) {
        this.translation = translation;
    }
    public void setSubjectRotation(Quaternion rotation) {
        this.rotation = rotation;
    }
    public void setOffset(Vector3f offset) {
        this.offset.set(offset);
    }
    public void setTarget(Vector3f target) {
        this.target.set(target);
    }
    public void setMotion(Motion motion) {
        this.motion = motion;
    }
    public void setSpeed(float speed) {
        this.speed = speed;
    }
    
}
