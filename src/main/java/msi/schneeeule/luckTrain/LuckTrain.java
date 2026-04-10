package msi.schneeeule.luckTrain;

import msi.schneeeule.luckTrain.Events.Bus;
import org.bukkit.plugin.java.JavaPlugin;

public final class LuckTrain extends JavaPlugin {

    private static LuckTrain instance;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
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

}
