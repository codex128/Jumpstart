package codex.jumpstart;

import codex.j3map.J3mapFactory;
import codex.j3map.processors.BooleanProcessor;
import codex.j3map.processors.FloatProcessor;
import codex.j3map.processors.IntegerProcessor;
import codex.j3map.processors.StringProcessor;
import codex.jumpstart.es.EntityAppState;
import codex.jumpstart.es.factory.AssemblyFactory;
import codex.jumpstart.es.factory.FactoryRequest;
import codex.jumpstart.es.system.VisualState;
import codex.jumpstart.event.AnimEventState;
import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.environment.EnvironmentCamera;
import com.jme3.material.Material;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.system.AppSettings;
import com.simsilica.es.EntityId;
import com.simsilica.lemur.GuiGlobals;

/**
 * This is the Main Class of your Game. You should only do initialization here.
 * Move your Logic into AppStates or Controls
 * @author normenhansen
 */
public class Main extends SimpleApplication {
    
    BulletAppState bulletapp;
    
    public static void main(String[] args) {
        Main app = new Main();
        var settings = new AppSettings(true);
        settings.setFrequency(120);
        settings.setVSync(true);
        settings.setWidth(1024);
        settings.setHeight(768);
        app.setSettings(settings);
        app.setShowSettings(true);
        app.start();
    }

    @Override
    public void simpleInitApp() {
        
        GuiGlobals.initialize(this);
        Functions.initialize(GuiGlobals.getInstance().getInputMapper());
        
        assetManager.registerLoader(J3mapFactory.class, "j3map");
        J3mapFactory.registerAllProcessors(
                BooleanProcessor.class,
                StringProcessor.class,
                IntegerProcessor.class,
                FloatProcessor.class);
        
        bulletapp = new BulletAppState();
        bulletapp.setDebugViewPorts(viewPort);
        //bulletapp.setDebugEnabled(true);
        stateManager.attach(bulletapp);
        
        stateManager.attach(new EntityAppState());
        
        stateManager.attach(new AnimEventState());
        
        var modelFactory = new AssemblyFactory<Spatial>();
        modelFactory.add(FactoryRequest.DEFAULT, (entity) -> {
            var cube = new Geometry("default-cube", new Box(.5f, .5f, .5f));
            var mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
            mat.setTexture("DiffuseMap", assetManager.loadTexture("Textures/testgrid.png"));
            cube.setMaterial(mat);
            return cube;
        });
        modelFactory.add(FactoryRequest.CUSTOM, (entity) -> new Node());
        var visuals = new VisualState(modelFactory);
        stateManager.attach(visuals);
        
        var animFactory = new AssemblyFactory<Spatial>();
        animFactory.add("player", (entity) -> AnimationConfig.configurePlayerAnimations(entity, guiNode, events))
        
        var envCam = new EnvironmentCamera();
        stateManager.attach(envCam);
        
        //var game = new GameState();
        //stateManager.attach(game);
        
    }
    @Override
    public void simpleUpdate(float tpf) {}
    @Override
    public void simpleRender(RenderManager rm) {}
    
}
