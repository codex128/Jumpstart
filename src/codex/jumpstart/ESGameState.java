/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.jumpstart;

import codex.boost.scene.SceneGraphIterator;
import codex.jumpstart.es.ESAppState;
import codex.jumpstart.es.components.Accelleration;
import codex.jumpstart.es.components.Animation;
import codex.jumpstart.es.components.CharacterShape;
import codex.jumpstart.es.components.Hand;
import codex.jumpstart.es.components.LookAtWalk;
import codex.jumpstart.es.components.Mass;
import codex.jumpstart.es.components.Physics;
import codex.jumpstart.es.components.TargetSpeed;
import codex.jumpstart.es.components.ViewDirection;
import codex.jumpstart.es.components.Visual;
import codex.jumpstart.es.components.WalkDirection;
import codex.jumpstart.es.components.WalkSpeed;
import codex.jumpstart.es.system.RigidBodyState;
import codex.jumpstart.es.system.SinglePlayerState;
import codex.jumpstart.es.system.VisualState;
import com.jme3.app.Application;
import com.jme3.environment.EnvironmentCamera;
import com.jme3.environment.LightProbeFactory;
import com.jme3.environment.generation.JobProgressAdapter;
import com.jme3.light.AmbientLight;
import com.jme3.light.LightProbe;
import com.jme3.light.PointLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.shadow.PointLightShadowRenderer;
import com.jme3.util.SkyFactory;
import com.simsilica.es.EntityId;
import jme3utilities.sky.SkyControl;
import jme3utilities.sky.StarsOption;
import jme3utilities.sky.Updater;

/**
 *
 * @author codex
 */
public class ESGameState extends ESAppState {
    
    private Node scene;    
    private SinglePlayerState singleplayer;
    
    @Override
    protected void init(Application app) {
        
        var visuals = getState(VisualState.class, true);
        var rigidbodyState = getState(RigidBodyState.class, true);
        
        scene = (Node)assetManager.loadModel("Models/maps/playground.j3o");
        rootNode.attachChild(scene);
        var it = new SceneGraphIterator(scene);
        Vector3f startLocation = new Vector3f();
        for (Spatial spatial : it) {
            if (spatial instanceof Geometry) {
                EntityId block = ed.createEntity();
                ed.setComponents(block,
                        new Visual(),
                        new Physics(),
                        new Mass(0f));
                spatial.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
                visuals.link(block, spatial);
                rigidbodyState.refresh(block).setRestitution(.5f);
                it.ignoreChildren();
            }
            if (spatial.getName().equals("start")) {
                startLocation.set(spatial.getWorldTranslation());
            }
        }        
        
        // create player entity
        EntityId player = ed.createEntity();
        ed.setComponents(player,
                new Visual(),
                new Animation(),
                new Physics(),
                new CharacterShape(1f, 5f, 150f),
                new WalkDirection(),
                new ViewDirection(),
                new WalkSpeed(0f),
                new TargetSpeed(0f),
                new Accelleration(.1f, .1f),
                new LookAtWalk(true),
                new Hand());
        // model
        var spatial = assetManager.loadModel("Models/characters/YBot.j3o");  
        spatial.setLocalScale(.01f);
        spatial.setCullHint(Spatial.CullHint.Never); // since this spatial will presumably always be in focus
        scene.attachChild(spatial);
        // registering
        getState(VisualState.class).link(player, spatial);
        singleplayer = new SinglePlayerState(player);
        getStateManager().attach(singleplayer);
        
        var gi = new AmbientLight(ColorRGBA.DarkGray);
        scene.addLight(gi);
//        var sun = new DirectionalLight(new Vector3f(0f, -1f, 0f));
//        sun.setColor(ColorRGBA.White);
//        scene.addLight(sun);
//        var dlsr = new DirectionalLightShadowRenderer(assetManager, 4096, 1);
//        dlsr.setLight(sun);
//        app.getViewPort().addProcessor(dlsr);
        var point = new PointLight(new Vector3f(0f, 5f, 0f));
        point.setRadius(30f);
        scene.addLight(point);
        var plsr = new PointLightShadowRenderer(assetManager, 4096);
        plsr.setLight(point);
        app.getViewPort().addProcessor(plsr);
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
        //updater.setMainLight(sun);
        //updater.addShadowRenderer(dlsr);
        skyControl.setEnabled(true);
        
    }
    @Override
    protected void cleanup(Application app) {
        getStateManager().detach(singleplayer);
    }
    @Override
    protected void onEnable() {}
    @Override
    protected void onDisable() {}
    
}
