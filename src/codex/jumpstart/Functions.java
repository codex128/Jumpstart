/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.jumpstart;

import com.jme3.input.KeyInput;
import com.simsilica.lemur.input.Axis;
import com.simsilica.lemur.input.Button;
import com.simsilica.lemur.input.FunctionId;
import com.simsilica.lemur.input.InputMapper;
import com.simsilica.lemur.input.InputState;

/**
 *
 * @author codex
 */
public class Functions {
    
    public static final String
            MAIN_GROUP = "main-group",
            DEV_GROUP = "dev-group";
    
    public static final FunctionId
            F_WALK = new FunctionId(Functions.MAIN_GROUP, "walk"),
            F_STRAFE = new FunctionId(Functions.MAIN_GROUP, "strafe"),
            F_SPRINT = new FunctionId(Functions.MAIN_GROUP, "sprint"),
            F_JUMP = new FunctionId(Functions.MAIN_GROUP, "jump"),
            F_SHOOT = new FunctionId(Functions.MAIN_GROUP, "shoot"),
            F_AIM_XZ = new FunctionId(Functions.MAIN_GROUP, "aim-xz"),
            F_AIM_Y = new FunctionId(Functions.MAIN_GROUP, "aim-y");
    public static final FunctionId
            F_DIE_IMPACT = new FunctionId(Functions.DEV_GROUP, "die-impact");
    
    public static void initialize(InputMapper im) {
        im.map(F_WALK, InputState.Positive, KeyInput.KEY_W);
        im.map(F_WALK, InputState.Negative, KeyInput.KEY_S);
        im.map(F_STRAFE, InputState.Positive, KeyInput.KEY_D);
        im.map(F_STRAFE, InputState.Negative, KeyInput.KEY_A);
        im.map(F_SPRINT, KeyInput.KEY_LSHIFT);
        im.map(F_JUMP, KeyInput.KEY_SPACE);
        im.map(F_SHOOT, Button.MOUSE_BUTTON1);
        im.map(F_AIM_XZ, Axis.MOUSE_X, KeyInput.KEY_SPACE);
        //im.map(F_AIM_Y, Axis.MOUSE_Y);
        im.map(F_DIE_IMPACT, KeyInput.KEY_X);
    }
    
}
