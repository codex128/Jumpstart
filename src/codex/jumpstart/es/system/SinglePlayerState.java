/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.jumpstart.es.system;

import codex.boost.camera.OrbitalCamera;
import codex.jumpstart.*;
import codex.jumpstart.es.ESAppState;
import codex.jumpstart.es.components.*;
import codex.jumpstart.es.registry.AnimationRegistry;
import codex.jumpstart.event.AnimEventListener;
import codex.jumpstart.event.AnimationEvent;
import com.jme3.anim.AnimComposer;
import com.jme3.anim.SkinningControl;
import com.jme3.anim.tween.Tween;
import com.jme3.anim.tween.Tweens;
import com.jme3.anim.tween.action.BlendAction;
import com.jme3.app.Application;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.simsilica.es.EntityComponent;
import com.simsilica.es.EntityId;
import com.simsilica.lemur.GuiGlobals;
import com.simsilica.lemur.input.AnalogFunctionListener;
import com.simsilica.lemur.input.FunctionId;
import com.simsilica.lemur.input.InputState;
import com.simsilica.lemur.input.StateFunctionListener;

/**
 *
 * @author codex
 */
public class SinglePlayerState extends ESAppState implements AnimEventListener,
        AnalogFunctionListener, StateFunctionListener, CharacterWalkListener {

    private final EntityId player;
    private AnimComposer anim;
    private SkinningControl skin;
    private AnimLayerControl layerControl;
    private BetterCharacterControl control;
    private OrbitalCamera orbital;
    private ShoulderCamera shoulder;
    private Vector3f inputdirection = new Vector3f();
    private float speedfactor = 0f;
    private boolean sprinting = true;
    private boolean sneaking = false;
    private boolean aimShifting = false;
    private final float impactThreshold = 6f;
    
    public SinglePlayerState(EntityId player) {
        this.player = player;
    }
    
    @Override
    protected void init(Application app) {        
        var spatial = getState(VisualState.class).get(player);
        var animated = AnimationConfig.fetchAnimatedSpatial(spatial);
        AnimationConfig.configurePlayerAnimations(player, animated, this);
        getState(AnimationRegistry.class).link(player, animated);
        anim = animated.getControl(AnimComposer.class);
        skin = animated.getControl(SkinningControl.class);
        layerControl = animated.getControl(AnimLayerControl.class);
        control = getState(PhysicalCharacterState.class).refresh(player);
        getState(PhysicalCharacterWalkState.class).refresh(player).addListener(this);
        
        // camera
        orbital = new OrbitalCamera(cam, inputMapper);
        orbital.getDistanceDomain().set(1f, 20f);
        orbital.setDistance(15f);
        orbital.setOffsets(new Vector3f(0f, 1.5f, 0f));
        spatial.addControl(orbital);
        inputMapper.activateGroup(OrbitalCamera.INPUT_GROUP);
        shoulder = new ShoulderCamera(cam);
        shoulder.setEnabled(false);
        shoulder.setOffset(new Vector3f(-.2f, 1.7f, -1.8f));
        shoulder.setTarget(new Vector3f(-.2f, 1.7f, 1f));
        spatial.addControl(shoulder);
        GuiGlobals.getInstance().setCursorEventsEnabled(false);
        
        // gun
        EntityId gun = ed.createEntity();
        ed.setComponents(gun,
                new Name("Glock17"),
                new Visual(),
                new Accuracy(.1f),
                new Capacity(17),
                new Explosive(), // this makes the bullet explode on impact! :D
                new Firerate(3),
                new Force(100f),
                new Damage(25f));
        var gunSpat = assetManager.loadModel("Models/weapons/M9Pistol.j3o");
        gunSpat.setLocalScale(50f);
        gunSpat.setLocalRotation(new Quaternion().lookAt(Vector3f.UNIT_Y.negate(), Vector3f.UNIT_X));
        getState(VisualState.class).link(gun, gunSpat);
        set(new Hand(gun));
        putGunInHolster();
        
        inputMapper.addAnalogListener(this,
                Functions.F_WALK,
                Functions.F_STRAFE,
                Functions.F_AIM_XZ,
                Functions.F_AIM_Y);
        inputMapper.addStateListener(this,
                Functions.F_JUMP,
                Functions.F_SPRINT,
                Functions.F_SHOOT,
                Functions.F_DIE_IMPACT);
        inputMapper.activateGroup(Functions.MAIN_GROUP);
        inputMapper.activateGroup(Functions.DEV_GROUP);
        
    }
    @Override
    protected void cleanup(Application app) {
        getState(AnimationRegistry.class).unlink(player);
    }
    @Override
    protected void onEnable() {}
    @Override
    protected void onDisable() {}
    @Override
    public void update(float tpf) {
        Vector3f walkDir = new Vector3f();
        if (!layerControl.isActive("gun")) {            
            if (inputdirection.z != 0 || inputdirection.x != 0) {
                Quaternion rotation = new Quaternion().lookAt(orbital.getPlanarCameraDirection(), Vector3f.UNIT_Y);
                walkDir.set(rotation.getRotationColumn(2));
                walkDir.multLocal(inputdirection.z);
                walkDir.addLocal(rotation.getRotationColumn(0).multLocal(-inputdirection.x));
                Quaternion currentDir = new Quaternion().lookAt(get(WalkDirection.class).getDirection(), Vector3f.UNIT_Y);
                Quaternion desiredDir = new Quaternion().lookAt(walkDir.normalizeLocal(), Vector3f.UNIT_Y);
                currentDir.nlerp(desiredDir, .1f);
                set(new WalkDirection(currentDir.mult(Vector3f.UNIT_Z)));
            }
            set(new WalkSpeed(Math.max(FastMath.abs(inputdirection.z), FastMath.abs(inputdirection.x))*getWalkSpeed()));
        }
        else if (aimShifting) {
            if (inputdirection.z != 0 || inputdirection.x != 0) {
                Quaternion rotation = new Quaternion().lookAt(get(ViewDirection.class).getDirection(), Vector3f.UNIT_Y);
                set(new WalkDirection(rotation.getRotationColumn(2).multLocal(inputdirection.z).addLocal(rotation.getRotationColumn(0).multLocal(-inputdirection.x)).normalizeLocal()));
            }
            set(new WalkSpeed(Math.max(FastMath.abs(inputdirection.z), FastMath.abs(inputdirection.x))*getWalkSpeed()));
        }
        if (shoulder.isEnabled()) {
            shoulder.setSubjectTranslation(control.getRigidBody().getPhysicsLocation());
            //shoulder.setSubjectRotation(control.getRigidBody().getPhysicsRotation());
        }
        if (!control.isOnGround() && !layerControl.isActive("jump")) {
            // trigger the falling loop if the jump layer is not active
            // this makes "falling" a default when not on the ground
            //layerControl.enter("jump", "falling-loop");
        }
        inputdirection.set(0f, 0f, 0f);
    }

    @Override
    public void animationEvent(AnimationEvent event) {}
    @Override
    public Tween tween(AnimationEvent event) {
        return Tweens.callMethod(this, event.getName(), event.getArguments());
    }

    @Override
    public void valueActive(FunctionId func, double value, double tpf) {
        if (!layerControl.isActive("jump") && isAlive()) {
            if (func == Functions.F_WALK) {
                inputdirection.z = FastMath.sign((float)value);
            }
            else if (func == Functions.F_STRAFE) {
                inputdirection.x = FastMath.sign((float)value);
            }
            if (!layerControl.isActive("gun")) {
                if (!sneaking && sprinting && (speedfactor += tpf) > 1f) {
                    speedfactor = 1f;
                }
                else if ((sneaking || !sprinting) && (speedfactor -= tpf) < 0f) {
                    speedfactor = 0f;
                }
            }
        }
        if (layerControl.isActive("gun")) {
            if (func == Functions.F_AIM_XZ) {
                Vector3f dir = new Quaternion().fromAngleAxis((float)value*-.05f, Vector3f.UNIT_Y).mult(ed.getComponent(player, ViewDirection.class).getDirection());
                ed.setComponent(player, new ViewDirection(dir));
                ed.setComponent(player, new WalkDirection(dir));
            }
        }
    }
    @Override
    public void valueChanged(FunctionId func, InputState value, double tpf) {
        if (!layerControl.isActive("gun") && func == Functions.F_SPRINT && isAlive() && value == InputState.Positive) {
            //sprinting = value == InputState.Positive;
            // repurposing sprint function for sneaking temporarily
            sneaking = !sneaking;
            control.setDucked(sneaking);
            if (sneaking) {
                if (get(WalkSpeed.class).getSpeed() > 0f) {
                    layerControl.enter("move", "sneaking");
                }
            }
            else {
                layerControl.enter("idle", "idle");
                if (get(WalkSpeed.class).getSpeed() > 0f) {
                    layerControl.enter("move", "walk->run");
                }
                else {
                    layerControl.exit("move");
                }
            }
        }
        else if (func == Functions.F_SHOOT && isAlive()) {
            if (!layerControl.get("gun").isActive() && value == InputState.Positive) {
                layerControl.exit("move");
                layerControl.get("gun").enter("draw-pistol-once");
                //movement.setFaceWalkDirection(false);
                set(new WalkSpeed(0f));
                speedfactor = 0f;
            }
            else if (layerControl.get("gun").isActive() && value == InputState.Off) {
                layerControl.get("gun").enter("holster-pistol-once");
                set(new LookAtWalk(true));
                switchCameraModes();
                set(new WalkSpeed(0f));
                enableAimShifting(false);
            }
        }
        else if (func == Functions.F_JUMP && isAlive() && value == InputState.Positive
                && control.isOnGround() && !layerControl.isActive("gun")) {
            control.jump();
        }
        else if (func == Functions.F_DIE_IMPACT && value != InputState.Off) {
            //startRagdollPhysics();
            if (!layerControl.isActive("death")) {
                layerControl.enter("death", "die-impact");
                if (layerControl.isActive("gun")) {
                    layerControl.exit("gun");
                    switchCameraModes();
                    speedfactor = 0f;
                    set(new LookAtWalk(false));
                }
            }
            else {
                layerControl.exit("death");
            }
        }
    }
    @Override
    public void walkDirectionChanged(Vector3f oldDir, Vector3f newDir) {}
    @Override
    public void moveSpeedChanged(float oldSpeed, float newSpeed, float delta) {
        if (!layerControl.isActive("gun")) {
            if (newSpeed == 0f) {
                layerControl.exit("move");
                speedfactor = 0f;
                return;
            }
            if (oldSpeed == 0f) {
                if (sneaking) layerControl.enter("move", "sneaking");
                else layerControl.enter("move", "walk->run");
            }
            ((BlendAction)anim.action("walk->run")).getBlendSpace().setValue(speedfactor);
        }
    }
    @Override
    public void velocityChanged(Vector3f oldVel, Vector3f newVel) {
        float hi = oldVel.y;
        float low = newVel.y;
        if (FastMath.abs(low) < .001f && hi < -impactThreshold) {
            layerControl.enter("jump", "land-once");
            speedfactor = 0f;
        }
    }
    
    private <T extends EntityComponent> T get(Class<T> type) {
        return ed.getComponent(player, type);
    }
    private void set(EntityComponent component) {
        ed.setComponent(player, component);
    }
    
    private float getWalkSpeed() {
        return FastMath.interpolateLinear(speedfactor, 2f, 10f);
    }
    private boolean isAlive() {
        return !layerControl.isActive("death");
    }
    
    public void switchCameraModes() {
        orbital.setEnabled(shoulder.isEnabled());
        shoulder.setEnabled(!orbital.isEnabled());
    }
    public void putGunInHand() {
        Node hand = (Node)((Node)anim.getSpatial()).getChild("hand");
        hand.attachChild(getState(VisualState.class).get(get(Hand.class).getHeldEntity()));
    }
    public void putGunInHolster() {
        Node holster = (Node)((Node)anim.getSpatial()).getChild("holster");
        holster.attachChild(getState(VisualState.class).get(get(Hand.class).getHeldEntity()));
    }
    public void playGunShot() {
        //gunShotSound.playInstance();
    }
    public void enableAimShifting(boolean enable) {
        aimShifting = enable;
    }
    
}
