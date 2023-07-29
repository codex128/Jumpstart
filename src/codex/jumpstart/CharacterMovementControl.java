/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.jumpstart;

import codex.boost.Listenable;
import codex.boost.control.SubControl;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import java.util.Collection;
import java.util.LinkedList;

/**
 *
 * @author codex
 */
public class CharacterMovementControl extends SubControl<BetterCharacterControl>
        implements Listenable<CharacterMovementListener> {
    
    private final Vector3f walk = new Vector3f(0f, 0f, 1f);
    private final Vector3f view = new Vector3f(0f, 0f, 1f);
    private final Vector3f velocity = new Vector3f();
    private float speed = 0f;
    private Vector2f friction = new Vector2f(1, 0);
    private boolean faceWalkDir = false;
    private boolean autoFriction = true;
    private final LinkedList<CharacterMovementListener> listeners = new LinkedList<>();
    
    public CharacterMovementControl() {
        super(BetterCharacterControl.class);
    }
    public CharacterMovementControl(boolean persistant) {
        super(BetterCharacterControl.class, persistant);
    }    

    @Override
    protected void subControlUpdate(float tpf) {
        if (autoFriction) {
            if (dependency.isOnGround()) {
                dependency.getRigidBody().setFriction(friction.x);
            }
            else {
                dependency.getRigidBody().setFriction(friction.y);
            }
        }
        var vel = dependency.getRigidBody().getLinearVelocity();
        if (!velocity.equals(vel)) {
            notifyListeners(l -> l.velocityChanged(velocity, vel));
            velocity.set(vel);
        }
    }
    @Override
    protected void subControlRender(RenderManager rm, ViewPort vp) {}
    @Override
    protected void onDependencyCaptured() {
        updateWalkDirection();
        updateViewDirection();
        dependency.getRigidBody().getLinearVelocity(velocity);
        if (friction.x < 0) {
            friction.x = dependency.getRigidBody().getFriction();
        }
    }
    @Override
    protected void onDependencyReleased() {}
    @Override
    public Collection<CharacterMovementListener> getListeners() {
        return listeners;
    }
    
    public boolean setWalkDirection(Vector3f direction) {
        if (walk.equals(direction)) return false;
        notifyListeners(l -> l.walkDirectionChanged(walk, direction));
        walk.set(direction);
        updateWalkDirection();
        if (faceWalkDir && !view.equals(direction)) {
            view.set(direction);
            updateViewDirection();
        }
        return true;
    }
    public void setViewDirection(Vector3f direction) {
        if (faceWalkDir || view.equals(direction)) return;
        notifyListeners(l -> l.viewDirectionChanged(walk, direction));
        view.set(direction);
        updateViewDirection();
    }
    public boolean setWalkSpeed(float speed) {
        assert speed >= 0f;
        if (this.speed != speed) {
            notifyListeners(l -> l.moveSpeedChanged(this.speed, speed, speed-this.speed));
            this.speed = speed;
            updateWalkDirection();
            return true;
        }
        return false;
    }
    public void setFaceWalkDirection(boolean faceWalkDir) {
        this.faceWalkDir = faceWalkDir;
    }
    public void setAutoFrictionEnabled(boolean enable) {
        autoFriction = enable;
    }
    public void setNormalFriction(float friction) {
        this.friction.x = friction;
    }
    public void setInAirFriction(float friction) {
        this.friction.y = friction;
    }
    
    public Vector3f getWalkDirection(Vector3f store) {
        return store.set(walk);
    }
    public Vector3f getWalkDirection() {
        return walk.clone();
    }
    public Vector3f getViewDirection(Vector3f store) {
        return store.set(view);
    }
    public Vector3f getViewDirection() {
        return view.clone();
    }
    public float getWalkSpeed() {
        return speed;
    }
    public float getAbsoluteWalkSpeed() {
        return FastMath.abs(speed);
    }
    public boolean isFaceWalkDirection() {
        return faceWalkDir;
    }
    
    private void updateWalkDirection() {
        if (dependency == null) return;
        dependency.setWalkDirection(walk.mult(speed));
    }
    private void updateViewDirection() {
        if (dependency == null) return;
        dependency.setViewDirection(view);
    }
    
}
