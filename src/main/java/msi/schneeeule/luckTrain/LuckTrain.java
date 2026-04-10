package msi.schneeeule.luckTrain;

import msi.schneeeule.luckTrain.Events.Bus;
import msi.schneeeule.luckTrain.System.UserPermissions;
import org.bukkit.plugin.java.JavaPlugin;

public final class LuckTrain extends JavaPlugin {

    private static LuckTrain instance;
    private static UserPermissions userPermissions;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        userPermissions = new UserPermissions();
        Bus.eventBus(instance);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static LuckTrain getInstance() {
        return instance;
    }
    public static LuckTrain train() {
        return instance;
    }

    public static UserPermissions getUserPermissions() {
        return userPermissions;
    }
}
