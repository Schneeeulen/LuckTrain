package msi.schneeeule.luckTrain.Events;

import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.event.EventBus;
import net.luckperms.api.event.node.NodeAddEvent;
import net.luckperms.api.event.node.NodeRemoveEvent;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Bus {

    public static void eventBus(JavaPlugin plugin) {
        EventBus eventBus = LuckPermsProvider.get().getEventBus();

        eventBus.subscribe(plugin, NodeAddEvent.class, event -> {
            if (event.getTarget() instanceof User) {
                User user = (User) event.getTarget();

                if (event.getNode() instanceof Group) {
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        Bukkit.getPluginManager().callEvent(new LuckPermsNodeChangeEvent(
                                user.getUniqueId(), LuckPermsNodeChangeEvent.Context.GROUP_ADD, event.getNode().getKey().toString()
                        ));
                    });


                } else {
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        Bukkit.getPluginManager().callEvent(new LuckPermsNodeChangeEvent(
                                user.getUniqueId(), LuckPermsNodeChangeEvent.Context.ADD, event.getNode().getKey().toString()
                        ));
                    });
                }
            }
        });

        eventBus.subscribe(plugin, NodeRemoveEvent.class, event -> {
            if (event.getTarget() instanceof User) {
                User user = (User) event.getTarget();

                if (event.getNode() instanceof Group) {
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        Bukkit.getPluginManager().callEvent(new LuckPermsNodeChangeEvent(
                                user.getUniqueId(), LuckPermsNodeChangeEvent.Context.GROUP_REMOVE, event.getNode().getKey().toString()
                        ));
                    });
                } else {
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        Bukkit.getPluginManager().callEvent(new LuckPermsNodeChangeEvent(
                                user.getUniqueId(), LuckPermsNodeChangeEvent.Context.REMOVE, event.getNode().getKey().toString()
                        ));
                    });
                }
            }
        });

    }


}
