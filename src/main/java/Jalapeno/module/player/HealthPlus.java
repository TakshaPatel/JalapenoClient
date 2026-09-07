package Jalapeno.module.player;

import Jalapeno.module.Category;
import Jalapeno.module.Module;
import Jalapeno.module.ModuleInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import org.lwjgl.glfw.GLFW;

@ModuleInfo(name = "HealthPlus", category = Category.PLAYER, key = GLFW.GLFW_KEY_H, description = "Regeneration, fire resistance and 20 hearts")
public class HealthPlus extends Module {
    private static final double MAX_HEALTH = 40.0;
    private static final int EFFECT_DURATION = 200;
    private static final int COMMAND_REFRESH_TICKS = 160;
    private int commandRefreshTicks;

    @Override
    protected void onEnable() {
        System.out.println("[HealthPlus] enabled");
        Player player = getTargetPlayer();
        if (player == null) {
            return;
        }
        applyToPlayer(player);
        requestRemoteServerEffects();
    }

    @Override
    protected void onDisable() {
        System.out.println("[HealthPlus] disabled");
        Player player = getTargetPlayer();
        if (player == null) {
            return;
        }
        clearOnPlayer(player);
    }

    @Override
    protected void onTick() {
        Player player = getTargetPlayer();
        if (player != null) {
            applyToPlayer(player);
        }
        if (commandRefreshTicks > 0) {
            commandRefreshTicks--;
        } else {
            requestRemoteServerEffects();
        }
    }

    private Player getTargetPlayer() {
        LocalPlayer local = Minecraft.getInstance().player;
        if (local == null) {
            return null;
        }
        ServerPlayer serverPlayer = Minecraft.getInstance().getSingleplayerServer() != null
            ? Minecraft.getInstance().getSingleplayerServer().getPlayerList().getPlayer(local.getUUID())
            : null;
        return serverPlayer != null ? serverPlayer : local;
    }

    private void applyToPlayer(Player player) {
        AttributeInstance maxHealth = player.getAttribute(Attributes.MAX_HEALTH);
        if (maxHealth != null && maxHealth.getBaseValue() != MAX_HEALTH) {
            maxHealth.setBaseValue(MAX_HEALTH);
        }
        if (player.getHealth() < MAX_HEALTH) {
            player.setHealth((float)MAX_HEALTH);
        }
        player.clearFire();
        player.setSharedFlagOnFire(false);
        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, EFFECT_DURATION, 3, false, false));
        player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, EFFECT_DURATION, 0, false, false));
    }

    private void clearOnPlayer(Player player) {
        AttributeInstance maxHealth = player.getAttribute(Attributes.MAX_HEALTH);
        if (maxHealth != null) {
            maxHealth.setBaseValue(20.0);
        }
        player.setHealth(Math.min(player.getHealth(), 20.0F));
        player.removeEffect(MobEffects.REGENERATION);
        player.removeEffect(MobEffects.FIRE_RESISTANCE);
    }

    private void requestRemoteServerEffects() {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer local = minecraft.player;
        if (local == null || minecraft.getSingleplayerServer() != null || !local.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER)) {
            return;
        }

        String playerName = local.getGameProfile().name();
        local.connection.sendCommand("effect give " + playerName + " minecraft:fire_resistance 12 0 true");
        local.connection.sendCommand("effect give " + playerName + " minecraft:regeneration 12 3 true");
        commandRefreshTicks = COMMAND_REFRESH_TICKS;
    }
}
