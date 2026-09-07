package Jalapeno.module.player;

import Jalapeno.module.Category;
import Jalapeno.module.Module;
import Jalapeno.module.ModuleInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.world.level.gamerules.GameRules;
import org.lwjgl.glfw.GLFW;

@ModuleInfo(name = "KeepInventory", category = Category.PLAYER, key = GLFW.GLFW_KEY_Y, description = "Keep inventory and XP after death")
public class KeepInventory extends Module {
    private static final int COMMAND_REFRESH_TICKS = 200;
    private Boolean previousGameRuleValue;
    private int commandRefreshTicks;

    @Override
    protected void onEnable() {
        System.out.println("[KeepInventory] enabled");
        setIntegratedServerGameRule(true);
        requestRemoteServerGameRule();
    }

    @Override
    protected void onDisable() {
        System.out.println("[KeepInventory] disabled");
        if (previousGameRuleValue != null) {
            setIntegratedServerGameRule(previousGameRuleValue);
            previousGameRuleValue = null;
        }
    }

    @Override
    protected void onTick() {
        setIntegratedServerGameRule(true);
        if (commandRefreshTicks > 0) {
            commandRefreshTicks--;
        } else {
            requestRemoteServerGameRule();
        }
    }

    private void setIntegratedServerGameRule(boolean value) {
        MinecraftServer server = Minecraft.getInstance().getSingleplayerServer();
        if (server == null) {
            return;
        }

        boolean currentValue = server.getGameRules().get(GameRules.KEEP_INVENTORY);
        if (previousGameRuleValue == null) {
            previousGameRuleValue = currentValue;
        }
        if (currentValue != value) {
            server.getGameRules().set(GameRules.KEEP_INVENTORY, value, server);
        }
    }

    private void requestRemoteServerGameRule() {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null || minecraft.getSingleplayerServer() != null || !player.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER)) {
            return;
        }

        player.connection.sendCommand("gamerule keep_inventory true");
        commandRefreshTicks = COMMAND_REFRESH_TICKS;
    }
}
