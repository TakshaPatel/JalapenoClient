package Jalapeno.module.combat;

import Jalapeno.module.Category;
import Jalapeno.module.Module;
import Jalapeno.module.ModuleInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import org.lwjgl.glfw.GLFW;

@ModuleInfo(name = "Criticals", category = Category.COMBAT, key = GLFW.GLFW_KEY_G, description = "No weapon cooldown + always critical hits")
public class Criticals extends Module {
    @Override
    protected void onEnable() {
        System.out.println("[Criticals] enabled");
    }

    @Override
    protected void onDisable() {
        System.out.println("[Criticals] disabled");
    }

    @Override
    protected void onTick() {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) {
            player.fillAttackStrengthTicker();
        }
    }
}