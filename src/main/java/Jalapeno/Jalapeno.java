package Jalapeno;

import Jalapeno.event.EventBus;
import Jalapeno.event.KeyPressEvent;
import Jalapeno.module.Module;
import Jalapeno.module.ModuleManager;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

public enum Jalapeno {
    INSTANCE;

    private final String clientName = "Minecraft | Jalapeno";
    private final String version = "v0.0.1";

    public void init(long windowHandle) {
        GLFW.glfwSetWindowTitle(windowHandle, clientName + " " + version);
        ModuleManager.getInstance().initialize();

        EventBus.getInstance().register(KeyPressEvent.class, e -> {
            if (e.getKey() == GLFW.GLFW_KEY_X && e.getAction() == InputConstants.PRESS) {
                Minecraft mc = Minecraft.getInstance();
                if (mc.player != null) {
                    mc.player.connection.sendChat("nerd");
                }
            }
        });

    }

    public void initTitle(long windowHandle) { GLFW.glfwSetWindowTitle(windowHandle, clientName + " " + version); }

    public void tick() {
        ModuleManager.getInstance().tick();
    }

    public boolean isModuleEnabled(String moduleName) {
        Module module = ModuleManager.getInstance().getModule(moduleName);
        return module != null && module.isEnabled();
    }

    public boolean isForcingCrits() {
        return isModuleEnabled("Criticals");
    }

    public void shutdown() {

    }
}