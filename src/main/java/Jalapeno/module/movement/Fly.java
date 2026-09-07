package Jalapeno.module.movement;

import Jalapeno.module.Category;
import Jalapeno.module.Module;
import Jalapeno.module.ModuleInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Abilities;
import org.lwjgl.glfw.GLFW;

@ModuleInfo(name = "Fly", category = Category.MOVEMENT, key = GLFW.GLFW_KEY_F, description = "Creative-like flight")
public class Fly extends Module {
    private static final float FLY_SPEED = 0.1F;
    private static boolean cancelNextFallDamage;

    @Override
    protected void onEnable() {
        System.out.println("[Fly] enabled");
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) {
            Abilities abilities = player.getAbilities();
            abilities.mayfly = true;
            abilities.flying = true;
            resetFallDistance(player);
            cancelNextFallDamage = false;
            player.onUpdateAbilities();
        }
    }

    @Override
    protected void onDisable() {
        System.out.println("[Fly] disabled");
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) {
            cancelNextFallDamage = !player.onGround() || player.fallDistance > 0.0;
            resetFallDistance(player);
            Abilities abilities = player.getAbilities();
            abilities.mayfly = false;
            abilities.flying = false;
            player.onUpdateAbilities();
        }
    }

    @Override
    protected void onTick() {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) {
            player.getAbilities().mayfly = true;
            player.getAbilities().flying = true;
            resetFallDistance(player);
        }
    }

    public static boolean consumeFallDamageCancel() {
        if (!cancelNextFallDamage) {
            return false;
        }

        cancelNextFallDamage = false;
        return true;
    }

    public static boolean shouldCancelFallDamage() {
        return cancelNextFallDamage;
    }

    public static boolean shouldSpoofOnGround(LocalPlayer player) {
        if (!cancelNextFallDamage) {
            return false;
        }
        if (player.onGround()) {
            cancelNextFallDamage = false;
            return false;
        }

        return true;
    }

    private static void resetFallDistance(LocalPlayer player) {
        player.resetFallDistance();
        ServerPlayer serverPlayer = getServerPlayer(player);
        if (serverPlayer != null) {
            serverPlayer.resetFallDistance();
        }
    }

    private static ServerPlayer getServerPlayer(LocalPlayer player) {
        Minecraft minecraft = Minecraft.getInstance();
        return minecraft.getSingleplayerServer() != null
            ? minecraft.getSingleplayerServer().getPlayerList().getPlayer(player.getUUID())
            : null;
    }
}
