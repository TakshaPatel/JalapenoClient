package Jalapeno.module.misc;

import Jalapeno.module.Category;
import Jalapeno.module.ModuleInfo;
import Jalapeno.module.Module;
import org.lwjgl.glfw.GLFW;

@ModuleInfo(name = "Test", category = Category.MISC, key = GLFW.GLFW_KEY_R)
public class TestModule extends Module {
    @Override protected void onEnable() {
        System.out.println("[Test] enabled");
    }
    @Override protected void onDisable() {
        System.out.println("[Test] disabled");
    }
}