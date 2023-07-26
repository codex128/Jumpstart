/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.jumpstart;

import codex.boost.GameAppState;
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
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.environment.EnvironmentCamera;
import com.jme3.environment.LightProbeFactory;
import com.jme3.environment.generation.JobProgressAdapter;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.light.LightProbe;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import com.jme3.scene.shape.Box;
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
    private Vector3f inputdirection = new Vector3f();
    private final float walkspeed = 2f;
    private final float runspeed = 10f;
    private final float sneakspeed = 1.6f;
    private float speedfactor = 0f;
    private final float impactThreshold = 6f;
    private boolean sprinting = !false;
    private boolean sneaking = false;
    private Spatial gun;
    
    @Override
    protected void init(Application app) {
        
        Vector3f start = initScene();
        Spatial ybot = initPlayer(start);
        initIllumination();
        initCamera(ybot);
        initAnimations();
        initInputs();
        
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
        if (!control.isOnGround() && !layerControl.isActive("jump")) {
            // trigger the falling loop if the jump layer is not active
            // this makes "falling" a default when not on the ground
            //layerControl.enter("jump", "falling-loop");
        }
        inputdirection.set(0f, 0f, 0f);
    }
    @Override
    public void valueActive(FunctionId func, double value, double tpf) {
        if (!layerControl.isActive("jump") && !layerControl.isActive("gun")) {
            if (func == Functions.F_WALK) {
                inputdirection.z = FastMath.sign((float)value);
            }
            else if (func == Functions.F_STRAFE) {
                inputdirection.x = FastMath.sign((float)value);
            }
            if (!sneaking && sprinting && (speedfactor += tpf) > 1f) {
                speedfactor = 1f;
            }
            else if ((sneaking || !sprinting) && (speedfactor -= tpf) < 0f) {
                speedfactor = 0f;
            }
        }
    }    
    @Override
    public void valueChanged(FunctionId func, InputState value, double tpf) {
        if (func == Functions.F_SPRINT && value == InputState.Positive) {
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
        else if (func == Functions.F_SHOOT) {
            if (!layerControl.get("gun").isActive() && value == InputState.Positive) {
                layerControl.get("gun").enter("draw-pistol-once");
                //orbital.setEnabled(false);
            }
            else if (layerControl.get("gun").isActive() && value == InputState.Off) {
                layerControl.get("gun").enter("holster-pistol-once");
                //orbital.setEnabled(true);
            }
        }
        else if (func == Functions.F_JUMP && value == InputState.Positive && control.isOnGround()) {
            control.jump();
        }
    }
    @Override
    public void walkDirectionChanged(Vector3f oldDir, Vector3f newDir) {}
    @Override
    public void moveSpeedChanged(float oldSpeed, float newSpeed, float delta) {
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
        scene = (Node)assetManager.loadModel("Models/maps/playground.gltf");
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
        ybot.setCullHint(Spatial.CullHint.Never);
        ybot.setShadowMode(RenderQueue.ShadowMode.Cast);
        anim = fetchControl(ybot, AnimComposer.class);
        control = createCharacter(character);
        ybot.addControl(control);
        getPhysicsSpace().add(control);
        control.getRigidBody().setCcdMotionThreshold(.1f);
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
        GuiGlobals.getInstance().setCursorEventsEnabled(false);
    }
    private void initAnimations() {
        skin = anim.getSpatial().getControl(SkinningControl.class);
        layerControl = new AnimLayerControl(AnimComposer.class);
        anim.getSpatial().addControl(layerControl);
        AlertArmatureMask allJoints = AlertArmatureMask.all("idle", anim, skin);
        layerControl.createSet(anim, skin, mask -> mask.addAll(), "idle", "move", "gun", "jump");
        layerControl.create("idle", allJoints);
        var idle = (ClipAction)anim.action("idle");
        idle.setMaxTransitionWeight(.5f);
        layerControl.enter("idle", "idle");
        ((ClipAction)anim.action("walk")).setMaxTransitionWeight(.6);
        ((ClipAction)anim.action("sprint")).setMaxTransitionWeight(.6);
        anim.actionBlended("walk->run", new LinearBlendSpace(0f, 1f), "walk", "sprint");
        anim.actionSequence("land-once",
            anim.action("landing"),
            Tweens.callMethod(layerControl, "exit", "jump")
        );
        ((ClipAction)anim.action("aim-pistol")).setMaxTransitionWeight(1);
        anim.addAction("shoot-cycle", new BaseAction(Tweens.sequence(
            Tweens.loopDuration(.1f, anim.action("aim-pistol")),
            Tweens.parallel(
                anim.action("shoot-pistol"),
                Tweens.sequence(
                    Tweens.delay(.1f),
                    Tweens.callMethod(this, "playGunShot")
                )
            )
        )));
        ((ClipAction)anim.action("draw-pistol")).setMaxTransitionWeight(.2);
        anim.addAction("draw-pistol-once", new BaseAction(Tweens.parallel(
            Tweens.sequence(
                anim.action("draw-pistol"),
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
    private void initInputs() {
        inputMapper.addAnalogListener(this, Functions.F_WALK, Functions.F_STRAFE, Functions.F_SHOOT);
        inputMapper.addStateListener(this, Functions.F_JUMP, Functions.F_SPRINT, Functions.F_SHOOT);
        inputMapper.activateGroup(Functions.MAIN_GROUP);
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
    
    public void putGunInHand() {
        Node hand = (Node)((Node)anim.getSpatial()).getChild("hand");
        hand.attachChild(gun);
    }
    public void putGunInHolster() {
        Node holster = (Node)((Node)anim.getSpatial()).getChild("holster");
        holster.attachChild(gun);
    }
    public void playGunShot() {
        System.out.println("bang!");
    }
    
}
