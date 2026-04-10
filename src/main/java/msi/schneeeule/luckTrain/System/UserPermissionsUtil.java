package msi.schneeeule.luckTrain.System;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.types.InheritanceNode;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class UserPermissionsUtil {
    private static final LuckPerms api = LuckPermsProvider.get();

    private UserPermissionsUtil() {
        // Utility class
    }


    // check if the user has permanent access to a permission, either through direct assignment or via a permanent group
    public static boolean hasLifetimePermission(Player player, String permission) {
        User user = api.getUserManager().getUser(player.getUniqueId());
        if (user == null) {
            return false;
        }

        for (Node node : user.getNodes()) {
            if (node.getKey().equalsIgnoreCase(permission)) {
                if (!node.hasExpiry()) {
                    return true;
                } else {
                    continue;
                }
            }

            if (node instanceof InheritanceNode inheritanceNode) {
                if (!inheritanceNode.hasExpiry()) {
                    Group group = api.getGroupManager().getGroup(inheritanceNode.getGroupName());

                    if (group != null) {
                        for (Node groupNode : group.getNodes()) {
                            if (groupNode.getKey().equalsIgnoreCase(permission) && !groupNode.hasExpiry()) {
                                return true;
                            }
                        }
                    }
                }
            }
        }

        return false;
    }


    // the time remaining until all accesses for this permission have expired
    public static String getFormattedPermissionTime(Player player, String permission) {
        User user = api.getUserManager().getUser(player.getUniqueId());

        if (user == null) {
            return null;
        }

        Duration finalDuration = null;

        for (Node node : user.getNodes()) {
            if (node.getKey().equalsIgnoreCase(permission)) {
                if (!node.hasExpiry()) {
                    return null;
                } else {
                    Duration duration = node.getExpiryDuration();
                    if (finalDuration == null || duration.compareTo(finalDuration) > 0) {
                        finalDuration = duration;
                    }
                }
            }

            if (node instanceof InheritanceNode inheritanceNode) {
                Group group = api.getGroupManager().getGroup(inheritanceNode.getGroupName());

                if (group != null) {
                    for (Node groupNode : group.getNodes()) {
                        if (groupNode.getKey().equalsIgnoreCase(permission)) {
                            if (!groupNode.hasExpiry()) {
                                if (!inheritanceNode.hasExpiry()) {
                                    return null;
                                } else {
                                    Duration duration = inheritanceNode.getExpiryDuration();
                                    if (finalDuration == null || duration.compareTo(finalDuration) > 0) {
                                        finalDuration = duration;
                                    }
                                }
                            } else {
                                Duration groupPermissionDuration = groupNode.getExpiryDuration();
                                if (finalDuration == null || groupPermissionDuration.compareTo(finalDuration) > 0) {
                                    finalDuration = groupPermissionDuration;
                                }
                            }
                        }
                    }
                }
            }
        }

        if (finalDuration != null) {
            long days = finalDuration.toDays();
            long hours = finalDuration.toHours() % 24;
            long minutes = finalDuration.toMinutes() % 60;
            long seconds = finalDuration.getSeconds() % 60;

            StringBuilder builder = new StringBuilder();
            if (days > 0) builder.append(days).append("d ");
            if (hours > 0 || days > 0) builder.append((hours > 9 ? hours : "0" + hours)).append("h ");
            if (minutes > 0 || builder.length() > 0) builder.append((minutes > 9 ? minutes : "0" + minutes)).append("m ");
            builder.append((seconds > 9 ? seconds : "0" + seconds)).append("s");

            return builder.toString().trim();
        }

        return null;
    }

    // the time remaining until all accesses for this permission have expired
    public static PermissionTime getPermissionTime(Player player, String permission) {
        User user = api.getUserManager().getUser(player.getUniqueId());

        if (user == null) {
            return null;
        }

        Duration finalDuration = null;

        for (Node node : user.getNodes()) {
            if (node.getKey().equalsIgnoreCase(permission)) {
                if (!node.hasExpiry()) {
                    return null;
                } else {
                    Duration duration = node.getExpiryDuration();
                    if (finalDuration == null || duration.compareTo(finalDuration) > 0) {
                        finalDuration = duration;
                    }
                }
            }

            if (node instanceof InheritanceNode inheritanceNode) {
                Group group = api.getGroupManager().getGroup(inheritanceNode.getGroupName());

                if (group != null) {
                    for (Node groupNode : group.getNodes()) {
                        if (groupNode.getKey().equalsIgnoreCase(permission)) {
                            if (!groupNode.hasExpiry()) {
                                if (!inheritanceNode.hasExpiry()) {
                                    return null;
                                } else {
                                    Duration duration = inheritanceNode.getExpiryDuration();
                                    if (finalDuration == null || duration.compareTo(finalDuration) > 0) {
                                        finalDuration = duration;
                                    }
                                }
                            } else {
                                Duration groupPermissionDuration = groupNode.getExpiryDuration();
                                if (finalDuration == null || groupPermissionDuration.compareTo(finalDuration) > 0) {
                                    finalDuration = groupPermissionDuration;
                                }
                            }
                        }
                    }
                }
            }
        }

        if (finalDuration != null) {
            long days = finalDuration.toDays();
            long hours = finalDuration.toHours() % 24;
            long minutes = finalDuration.toMinutes() % 60;
            long seconds = finalDuration.getSeconds() % 60;

            return new PermissionTime(days, hours, minutes, seconds);
        }

        return null;
    }


    // Async:

    // check if the user has permanent access to a permission, either through direct assignment or via a permanent group
    public static CompletableFuture<Boolean> hasLifetimePermission(UUID uuid, String permission) {
        return api.getUserManager().loadUser(uuid).thenApply(user -> {
            for (Node node : user.getNodes()) {
                if (node.getKey().equalsIgnoreCase(permission)) {
                    if (!node.hasExpiry()) {
                        return true;
                    }
                }

                if (node instanceof InheritanceNode inheritanceNode) {
                    if (!inheritanceNode.hasExpiry()) {
                        Group group = api.getGroupManager().getGroup(inheritanceNode.getGroupName());
                        if (group != null) {
                            for (Node groupNode : group.getNodes()) {
                                if (groupNode.getKey().equalsIgnoreCase(permission) && !groupNode.hasExpiry()) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }

            return false;
        });
    }

    // the time remaining until all accesses for this permission have expired
    public static CompletableFuture<String> getFormatedPermissionTime(UUID uuid, String permission) {
        return api.getUserManager().loadUser(uuid).thenApply(user -> {
            if (user == null) {
                return null;
            }

            Duration finalDuration = null;

            for (Node node : user.getNodes()) {
                if (node.getKey().equalsIgnoreCase(permission)) {
                    if (!node.hasExpiry()) {
                        return null;
                    } else {
                        Duration duration = node.getExpiryDuration();
                        if (finalDuration == null || duration.compareTo(finalDuration) > 0) {
                            finalDuration = duration;
                        }
                    }
                }

                if (node instanceof InheritanceNode inheritanceNode) {
                    Group group = api.getGroupManager().getGroup(inheritanceNode.getGroupName());

                    if (group != null) {
                        for (Node groupNode : group.getNodes()) {
                            if (groupNode.getKey().equalsIgnoreCase(permission)) {
                                if (!groupNode.hasExpiry()) {
                                    if (!inheritanceNode.hasExpiry()) {
                                        return null;
                                    } else {
                                        Duration duration = inheritanceNode.getExpiryDuration();
                                        if (finalDuration == null || duration.compareTo(finalDuration) > 0) {
                                            finalDuration = duration;
                                        }
                                    }
                                } else {
                                    Duration groupPermissionDuration = groupNode.getExpiryDuration();
                                    if (finalDuration == null || groupPermissionDuration.compareTo(finalDuration) > 0) {
                                        finalDuration = groupPermissionDuration;
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (finalDuration != null) {
                long days = finalDuration.toDays();
                long hours = finalDuration.toHours() % 24;
                long minutes = finalDuration.toMinutes() % 60;
                long seconds = finalDuration.getSeconds() % 60;

                StringBuilder builder = new StringBuilder();
                if (days > 0) builder.append(days).append("d ");
                if (hours > 0 || days > 0) builder.append(String.format("%02dh ", hours));
                if (minutes > 0 || builder.length() > 0) builder.append(String.format("%02dm ", minutes));
                builder.append(String.format("%02ds", seconds));

                return builder.toString().trim();
            }

            return null;
        });
    }

    // the time remaining until all accesses for this permission have expired
    public static record PermissionTime(long days, long hours, long minutes, long seconds) {}

    // the time remaining until all accesses for this permission have expired
    public static CompletableFuture<PermissionTime> getPermissionTime(UUID uuid, String permission) {
        return api.getUserManager().loadUser(uuid).thenApply(user -> {
            if (user == null) {
                 return null;
            }

            Duration finalDuration = null;

            for (Node node : user.getNodes()) {
                if (node.getKey().equalsIgnoreCase(permission)) {
                    if (!node.hasExpiry()) {
                        return null;
                    } else {
                        Duration duration = node.getExpiryDuration();
                        if (finalDuration == null || duration.compareTo(finalDuration) > 0) {
                            finalDuration = duration;
                        }
                    }
                }

                if (node instanceof InheritanceNode inheritanceNode) {
                    Group group = api.getGroupManager().getGroup(inheritanceNode.getGroupName());

                    if (group != null) {
                        for (Node groupNode : group.getNodes()) {
                            if (groupNode.getKey().equalsIgnoreCase(permission)) {
                                if (!groupNode.hasExpiry()) {
                                    if (!inheritanceNode.hasExpiry()) {
                                        return null;
                                    } else {
                                        Duration duration = inheritanceNode.getExpiryDuration();
                                        if (finalDuration == null || duration.compareTo(finalDuration) > 0) {
                                            finalDuration = duration;
                                        }
                                    }
                                } else {
                                    Duration groupPermissionDuration = groupNode.getExpiryDuration();
                                    if (finalDuration == null || groupPermissionDuration.compareTo(finalDuration) > 0) {
                                        finalDuration = groupPermissionDuration;
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (finalDuration != null) {
                long days = finalDuration.toDays();
                long hours = finalDuration.toHours() % 24;
                long minutes = finalDuration.toMinutes() % 60;
                long seconds = finalDuration.getSeconds() % 60;

                return new PermissionTime(days, hours, minutes, seconds);
            }

            return null;
        });
    }

    // the time remaining until all accesses for this permission have expired
    public static CompletableFuture<Duration> getPermissionDuration(UUID uuid, String permission) {
        return api.getUserManager().loadUser(uuid).thenApply(user -> {
            if (user == null) {
                return null;
            }

            Duration finalDuration = null;

            for (Node node : user.getNodes()) {
                if (node.getKey().equalsIgnoreCase(permission)) {
                    if (!node.hasExpiry()) {
                        return null;
                    } else {
                        Duration duration = node.getExpiryDuration();
                        if (finalDuration == null || duration.compareTo(finalDuration) > 0) {
                            finalDuration = duration;
                        }
                    }
                }

                if (node instanceof InheritanceNode inheritanceNode) {
                    Group group = api.getGroupManager().getGroup(inheritanceNode.getGroupName());

                    if (group != null) {
                        for (Node groupNode : group.getNodes()) {
                            if (groupNode.getKey().equalsIgnoreCase(permission)) {
                                if (!groupNode.hasExpiry()) {
                                    if (!inheritanceNode.hasExpiry()) {
                                        return null;
                                    } else {
                                        Duration duration = inheritanceNode.getExpiryDuration();
                                        if (finalDuration == null || duration.compareTo(finalDuration) > 0) {
                                            finalDuration = duration;
                                        }
                                    }
                                } else {
                                    Duration groupPermissionDuration = groupNode.getExpiryDuration();
                                    if (finalDuration == null || groupPermissionDuration.compareTo(finalDuration) > 0) {
                                        finalDuration = groupPermissionDuration;
                                    }
                                }
                            }
                        }
                    }
                }
            }

            return finalDuration;
        });
    }

    // check if the user has access to a permission, either through direct assignment or via a permanent group
    public static CompletableFuture<Boolean> hasPermission(UUID uuid, String permission) {
        return api.getUserManager().loadUser(uuid).thenApply(user -> {
            if (user == null) return false;
            return user.getCachedData().getPermissionData().checkPermission(permission).asBoolean();
        });
    }

    public static CompletableFuture<Boolean> hasSubPermissionOf(UUID uuid, String permission) {
        return api.getUserManager().loadUser(uuid).thenApply(user -> {
            for (Node node : user.getNodes()) {
                if (node.getKey().startsWith(permission)) {
                    return true;
                }

                if (node instanceof InheritanceNode inheritanceNode) {
                    Group group = api.getGroupManager().getGroup(inheritanceNode.getGroupName());
                    if (group != null) {
                        for (Node groupNode : group.getNodes()) {
                            if (groupNode.getKey().startsWith(permission)) {
                                return true;
                            }
                        }
                    }
                }
            }

            return false;
        });
    }


}
