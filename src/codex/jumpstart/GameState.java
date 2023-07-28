/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.jumpstart;

import codex.boost.GameAppState;
import codex.boost.audio.AudioModel;
import codex.boost.audio.SFXSpeaker;
import codex.boost.camera.OrbitalCamera;
import codex.boost.scene.SceneGraphIterator;
import codex.j3map.J3map;
import com.jme3.anim.AnimComposer;
import com.jme3.anim.SkinningControl;
import com.jme3.anim.tween.Tweens;
import com.jme3.anim.tween.action.BaseAction;
import com.jme3.anim.tween.action.BlendAction;
import com.jme3.anim.tween.action.ClipAction;
import com.jme3.anim.tween.action.LinearBlendSpace;
import com.jme3.app.Application;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.PhysicsTickListener;
import com.jme3.bullet.animation.DacConfiguration;
import com.jme3.bullet.animation.DynamicAnimControl;
import com.jme3.bullet.animation.RangeOfMotion;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.environment.EnvironmentCamera;
import com.jme3.environment.LightProbeFactory;
import com.jme3.environment.generation.JobProgressAdapter;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.light.LightProbe;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.util.SkyFactory;
import com.simsilica.lemur.GuiGlobals;
import com.simsilica.lemur.input.AnalogFunctionListener;
import com.simsilica.lemur.input.FunctionId;
import com.simsilica.lemur.input.InputState;
import com.simsilica.lemur.input.StateFunctionListener;
import jme3utilities.sky.SkyControl;
import jme3utilities.sky.StarsOption;
import jme3utilities.sky.Updater;

/**
 *
 * @author codex
 */
public class GameState extends GameAppState implements
        AnalogFunctionListener, StateFunctionListener, CharacterMovementListener {
    
    private Node scene;
    private AnimComposer anim;
    private SkinningControl skin;
    private AnimLayerControl layerControl;
    private CharacterMovementControl movement;
    private MyCharacterControl control;
    private OrbitalCamera orbital;
    private ShoulderCamera shoulder;
    private final Vector3f inputdirection = new Vector3f();
    private final float walkspeed = 2f;
    private final float runspeed = 10f;
    private final float sneakspeed = 1.6f;
    private float speedfactor = 0f;
    private final float impactThreshold = 6f;
    private boolean sprinting = !false;
    private boolean sneaking = false;
    private Spatial gun;
    private SFXSpeaker gunShotSound;
    private DynamicAnimControl dac;
    private boolean aimShifting = false;
    
    @Override
    protected void init(Application app) {
        
        Vector3f start = initScene();
        Spatial ybot = initPlayer(start);
        initIllumination();
        initCamera(ybot);
        initAnimations();
        initRagdoll();
        initAudio();
        initInputs();
        
        //startRagdollPhysics();
        
        gun = assetManager.loadModel("Models/weapons/M9Pistol.j3o");
        gun.setLocalScale(50f);
        gun.setLocalRotation(new Quaternion().lookAt(Vector3f.UNIT_Y.negate(), Vector3f.UNIT_X));
        putGunInHolster();
        
//        Spatial holster = ((Node)anim.getSpatial()).getChild("holster");
//        holster.addControl(new AbstractControl() {
//            @Override
//            protected void controlUpdate(float tpf) {r
//                spatial.rotate(0f, tpf, 0f);
//            }
//            @Override
//            protected void controlRender(RenderManager rm, ViewPort vp) {}
//        });
        
    }
    @Override
    protected void cleanup(Application app) {}
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
                Quaternion currentDir = new Quaternion().lookAt(movement.getWalkDirection(new Vector3f()), Vector3f.UNIT_Y);
                Quaternion desiredDir = new Quaternion().lookAt(walkDir.normalizeLocal(), Vector3f.UNIT_Y);
                currentDir.nlerp(desiredDir, .1f);
                movement.setWalkDirection(currentDir.mult(Vector3f.UNIT_Z));
            }
            movement.setWalkSpeed(Math.max(FastMath.abs(inputdirection.z), FastMath.abs(inputdirection.x))*getMoveSpeed());
        }
        else if (aimShifting) {
            if (inputdirection.z != 0 || inputdirection.x != 0) {
                Quaternion rotation = new Quaternion().lookAt(movement.getViewDirection(), Vector3f.UNIT_Y);
                movement.setWalkDirection(rotation.getRotationColumn(2).multLocal(inputdirection.z).addLocal(rotation.getRotationColumn(0).multLocal(-inputdirection.x)).normalizeLocal());
            }
            movement.setWalkSpeed(Math.max(FastMath.abs(inputdirection.z), FastMath.abs(inputdirection.x))*walkspeed);
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
                movement.setViewDirection(new Quaternion().fromAngleAxis((float)value*-.05f, Vector3f.UNIT_Y).mult(movement.getViewDirection()));
                movement.setWalkDirection(movement.getViewDirection());
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
                if (movement.getWalkSpeed() > 0f) {
                    layerControl.enter("move", "sneaking");
                }
            }
            else {
                layerControl.enter("idle", "idle");
                if (movement.getWalkSpeed() > 0f) {
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
                movement.setFaceWalkDirection(false);
                movement.setWalkSpeed(0f);
                speedfactor = 0f;
            }
            else if (layerControl.get("gun").isActive() && value == InputState.Off) {
                layerControl.get("gun").enter("holster-pistol-once");
                movement.setFaceWalkDirection(true);
                switchCameraModes();
                movement.setWalkSpeed(0f);
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
                    movement.setFaceWalkDirection(false);
                }
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
    
    private Vector3f initScene() {
        scene = (Node)assetManager.loadModel("Models/maps/playground.j3o");
        rootNode.attachChild(scene);
        var it = new SceneGraphIterator(scene);
        Vector3f startLocation = new Vector3f();
        for (Spatial spatial : it) {
            if (spatial instanceof Geometry) {
                spatial.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
                var rigidbody = new RigidBodyControl(0f);
                spatial.addControl(rigidbody);
                getPhysicsSpace().add(rigidbody);
                rigidbody.setRestitution(.5f);
                it.ignoreChildren();
            }
            if (spatial.getName().equals("start")) {
                startLocation.set(spatial.getWorldTranslation());
            }
        }
        return startLocation;
    }
    private Spatial initPlayer(Vector3f startLocation) {
        J3map character = (J3map)assetManager.loadAsset("Properties/YBot.j3map");
        Spatial ybot = assetManager.loadModel(character.getString("model"));
        ybot.setLocalScale(.01f);
        ybot.setCullHint(Spatial.CullHint.Never);
        ybot.setShadowMode(RenderQueue.ShadowMode.Cast);
        anim = fetchControl(ybot, AnimComposer.class);
        control = createCharacter(character);
        ybot.addControl(control);
        getPhysicsSpace().add(control);
        control.getRigidBody().setCcdMotionThreshold(0f);
        movement = new CharacterMovementControl(BetterCharacterControl.class);
        movement.setFaceWalkDirection(true);
        movement.addListener(this);
        ybot.addControl(movement);
        scene.attachChild(ybot);
        control.warp(startLocation);
        getPhysicsSpace().setGravity(new Vector3f(0f, -50f, 0f));
        return ybot;
    }
    private void initIllumination() {
        var gi = new AmbientLight(ColorRGBA.DarkGray);
        scene.addLight(gi);
        var sun = new DirectionalLight(new Vector3f(0f, -1f, 0f));
        sun.setColor(ColorRGBA.White);
        scene.addLight(sun);
        var dlsr = new DirectionalLightShadowRenderer(assetManager, 4096, 4);
        dlsr.setLight(sun);
        app.getViewPort().addProcessor(dlsr);
        var envCam = getState(EnvironmentCamera.class, true);
        envCam.setPosition(new Vector3f(0f, 20f, 0f));
        envCam.setBackGroundColor(ColorRGBA.White);
        LightProbeFactory.makeProbe(envCam, scene, new JobProgressAdapter<>() {
            @Override
            public void done(LightProbe result) {
                result.getArea().setRadius(100f);
                scene.addLight(result);
            }
        });
        Spatial sky = SkyFactory.createSky(assetManager, "Scenes/FullskiesSunset0068.dds", SkyFactory.EnvMapType.CubeMap);
        sky.setShadowMode(RenderQueue.ShadowMode.Off);
        rootNode.attachChild(sky);        
        SkyControl skyControl = new SkyControl(assetManager, cam, .5f, StarsOption.TopDome, true);
        rootNode.addControl(skyControl);
        skyControl.setCloudiness(0.8f);
        skyControl.setCloudsYOffset(0.4f);
        skyControl.setTopVerticalAngle(1.78f);
        skyControl.getSunAndStars().setHour(10);
        Updater updater = skyControl.getUpdater();
        updater.setAmbientLight(gi);
        updater.setMainLight(sun);
        updater.addShadowRenderer(dlsr);
        skyControl.setEnabled(true);
    }
    private void initCamera(Spatial ybot) {
        orbital = new OrbitalCamera(cam, inputMapper);
        orbital.getDistanceDomain().set(1f, 20f);
        orbital.setDistance(15f);
        orbital.setOffsets(new Vector3f(0f, 1.5f, 0f));
        ybot.addControl(orbital);
        inputMapper.activateGroup(OrbitalCamera.INPUT_GROUP);
        shoulder = new ShoulderCamera(cam);
        shoulder.setEnabled(false);
        shoulder.setOffset(new Vector3f(-.2f, 1.7f, -1.8f));
        shoulder.setTarget(new Vector3f(-.2f, 1.7f, 1f));
        ybot.addControl(shoulder);
        GuiGlobals.getInstance().setCursorEventsEnabled(false);
    }
    private void initAnimations() {
        skin = anim.getSpatial().getControl(SkinningControl.class);
        layerControl = new AnimLayerControl(AnimComposer.class);
        anim.getSpatial().addControl(layerControl);
        AlertArmatureMask allJoints = AlertArmatureMask.all("idle", anim, skin);
        layerControl.createSet(anim, skin, mask -> mask.addAll(),
                "idle", "move", "gun", "jump", "death");
        layerControl.create("idle", allJoints);
        anim.addAction("freeze", new BaseAction(anim.action("idle")));
        anim.action("freeze").setSpeed(0);
        var idle = (ClipAction)anim.action("idle");
        idle.setMaxTransitionWeight(.5);
        layerControl.enter("idle", "idle");
        var walkRun = anim.actionBlended("walk->run", new LinearBlendSpace(0f, 1f), "walk", "sprint");
        walkRun.setMaxTransitionWeight(.5);
        anim.actionSequence("land-once",
            anim.action("landing"),
            Tweens.callMethod(layerControl, "exit", "jump")
        );
        ((ClipAction)anim.action("aim-pistol")).setMaxTransitionWeight(.8);
        ((ClipAction)anim.action("shoot-pistol")).setMaxTransitionWeight(.9);
        anim.addAction("shoot-cycle", new BaseAction(Tweens.sequence(
            Tweens.loopDuration(.2f, anim.action("aim-pistol")),
            Tweens.parallel(
                anim.action("shoot-pistol"),
                Tweens.sequence(
                    Tweens.delay(.1),
                    Tweens.callMethod(this, "playGunShot")
                )
            )
        )));
        anim.action("shoot-cycle").setSpeed(2);
        ((ClipAction)anim.action("draw-pistol")).setMaxTransitionWeight(.7);
        anim.addAction("draw-pistol-once", new BaseAction(Tweens.parallel(
            Tweens.sequence(
                anim.action("draw-pistol"),
                Tweens.callMethod(this, "enableAimShifting", true),
                Tweens.callMethod(this, "switchCameraModes"),
                Tweens.callMethod(layerControl, "enter", "gun", "shoot-cycle")
            ),
            Tweens.sequence(
                Tweens.delay(.5f),
                Tweens.callMethod(this, "putGunInHand")
            )
        )));
        anim.action("draw-pistol-once").setSpeed(4);
        anim.addAction("holster-pistol-once", new BaseAction(Tweens.parallel(
            Tweens.sequence(
                Tweens.invert(anim.action("draw-pistol")),
                Tweens.callMethod(layerControl, "exit", "gun")
            ),
            Tweens.sequence(
                Tweens.delay(1.5f),
                Tweens.callMethod(this, "putGunInHolster")
            )
        )));
        anim.action("holster-pistol-once").setSpeed(4);
        anim.action("sneaking").setSpeed(.7);
        anim.addAction("die-impact", new BaseAction(Tweens.parallel(
            anim.action("killed"),
            Tweens.sequence(
                Tweens.delay(1),
                Tweens.callMethod(this, "startRagdollPhysics")
            )
        )));
        
        // theoretical animation setup
        /*anim.addAction("hi-jump-once", new BaseAction(
            Tweens.sequence(
                Tweens.parallel(
                    anim.action("jump"),
                    Tweens.sequence(
                        Tweens.delay(.1f),
                        Tweens.callMethod(control, "jump")
                    )
                ),
                Tweens.callMethod(layerControl, "enter", "jump", "jump-loop")
            )
        ));
        anim.actionSequence("long-jump-once",
            anim.action("running-jump"),
            Tweens.callMethod(layerControl, "enter", "jump", "jump-loop")
        );*/
    }
    private void initAudio() {
        var model = new AudioModel((J3map)assetManager.loadAsset("Properties/gunShot.j3map"));
        gunShotSound = new SFXSpeaker(assetManager, model);
        //playGunShot();
    }
    private void initInputs() {
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
    
    private boolean isAlive() {
        return !layerControl.isActive("death");
    }
    private float getMoveSpeed() {
        if (!sneaking) return FastMath.interpolateLinear(speedfactor, walkspeed, runspeed);
        return sneakspeed;
    }
    private PhysicsSpace getPhysicsSpace() {
        return getState(BulletAppState.class, true).getPhysicsSpace();
    }
    private MyCharacterControl createCharacter(J3map source) {
        var character = new MyCharacterControl(source.getFloat("radius"), source.getFloat("height"), source.getFloat("mass"));
        character.setJumpForce(new Vector3f(0f, source.getFloat("jumpForce", 1f), 0f));
        source.onPropertyExists("gravity", Float.class, (grav) -> {
            character.setGravity(new Vector3f(0f, grav, 0f));
            character.getRigidBody().setProtectGravity(true);
        });
        return character;
    }
    private <T extends Control> T fetchControl(Spatial spatial, Class<T> type) {
        for (Spatial s : new SceneGraphIterator(spatial)) {
            T control = s.getControl(type);
            if (control != null) return control;
        }
        return null;
    }
    private float ofGreaterMagnitude(float a, float b) {
        if (FastMath.abs(a) >= FastMath.abs(b)) return a;
        return b;
    }
    
    public void switchCameraModes() {
        orbital.setEnabled(shoulder.isEnabled());
        shoulder.setEnabled(!orbital.isEnabled());
    }
    public void putGunInHand() {
        Node hand = (Node)((Node)anim.getSpatial()).getChild("hand");
        hand.attachChild(gun);
    }
    public void putGunInHolster() {
        Node holster = (Node)((Node)anim.getSpatial()).getChild("holster");
        holster.attachChild(gun);
    }
    public void playGunShot() {
        gunShotSound.playInstance();
    }
    public void enableAimShifting(boolean enable) {
        aimShifting = enable;
    }
    
    /**
     * Starts the ragdoll physics on the next physics tick.
     * Disables character control physics.
     */
    public void startRagdollPhysics() {
        getPhysicsSpace().remove(control);
        // add the DAC to the physics space now when we actually need it
        getPhysicsSpace().add(dac);
        // enable ragdoll mode on the next physics tick
        getPhysicsSpace().addTickListener(new PhysicsTickListener() {
            @Override
            public void prePhysicsTick(PhysicsSpace space, float tpf) {}
            @Override
            public void physicsTick(PhysicsSpace space, float tpf) {
                dac.setRagdollMode();
                getPhysicsSpace().removeTickListener(this);
            }
        });
    }
    /**
     * Initializes (but does not start) the ragdoll physics.
     */
    private void initRagdoll() {
        dac = new DynamicAnimControl();
        dac.setMass(DacConfiguration.torsoName, 1f);
        var motion = motion(false, .7f, -.7f, .7f, -.7f, .7f, -.7f);
        link(dac, "Spine", 1f,          motion);
        link(dac, "Spine1", 1f,         motion);
        link(dac, "Spine2", 1f,         motion);
        link(dac, "Neck", 1f,           motion);
        link(dac, "LeftArm", 1f,        motion);
        link(dac, "LeftForeArm", 1f,    motion);
        link(dac, "RightArm", 1f,       motion);
        link(dac, "RightForeArm", 1f,   motion);
        link(dac, "LeftUpLeg", 1f,      motion);
        link(dac, "LeftLeg", 1f,        motion);
        link(dac, "LeftFoot", 1f,       motion);
        link(dac, "RightUpLeg", 1f,     motion);
        link(dac, "RightLeg", 1f,       motion);
        link(dac, "RightFoot", 1f,      motion);
        dac.setIgnoredHops(20);
        // add the DAC to the same spatial as the SkinningControl
        skin.getSpatial().addControl(dac);
        // set the margin on all collision bodies
        for (var body : dac.listRigidBodies()) {
            body.getCollisionShape().setMargin(.0001f);
        }
        // don't add the DAC to the physics space yet, because we don't really need it yet
        //getPhysicsSpace().add(dac);
    }
    /**
     * Link the given joint to the DynamicAnimControl.
     * This is for Mixamo rigs only!
     * @param dac
     * @param joint
     * @param mass
     * @param motion 
     */
    private void link(DynamicAnimControl dac, String joint, float mass, RangeOfMotion motion) {
        dac.link("mixamorig:"+joint, mass, motion);
    }
    private RangeOfMotion motion(boolean multByPi, float... values) {
        assert values.length == 6;
        if (multByPi) for (int i = 0; i < values.length; i++) {
            values[i] *= FastMath.PI;
        }
        return new RangeOfMotion(values[0], values[1], values[2], values[3], values[4], values[5]);
    }
    
}
