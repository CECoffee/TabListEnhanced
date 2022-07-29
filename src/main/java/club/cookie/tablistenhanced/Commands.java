package club.cookie.tablistenhanced;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

import club.cookie.tablistenhanced.config.TLUserConfigs;
import club.cookie.tablistenhanced.scoreboard.EPScoreboard;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class Commands implements CommandExecutor {
private final TabListEnhanced plugin = JavaPlugin.getPlugin(TabListEnhanced.class);
    private final EPScoreboard epsb;

    Commands(EPScoreboard epsb) {
        this.epsb = epsb;
    }

    private void commandReload() {
        this.plugin.getConfig().options().copyDefaults(false);
        this.plugin.reloadConfig();
        if (this.plugin.getConfig().getInt("header-interval") >= this.plugin.getConfig().getInt("footer-interval")) {
            HeaderFooter.fastestUpdateRateReq = this.plugin.getConfig().getInt("footer-interval");
        } else {
            HeaderFooter.fastestUpdateRateReq = this.plugin.getConfig().getInt("header-interval");
        }

        this.plugin.epsb.permOrder = new ArrayList(this.plugin.getConfig().getStringList("sortByPerms"));
        this.plugin.up.updatePlaceholderAPIPlaceholders();

        try {
            this.plugin.id.cancel();
        } catch (Exception var2) {
        }

        this.plugin.up.resort();
        this.plugin.usb.updateboard();
        HeaderFooter.headerAnimation = new ArrayList(this.plugin.getConfig().getStringList("header"));
        HeaderFooter.footerAnimation = new ArrayList(this.plugin.getConfig().getStringList("footer"));
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        String keys;
        if (sender instanceof Player) {
            Player p = (Player)sender;
            p.getUniqueId().toString();
            TabListEnhanced var10001;
            if (p.hasPermission("tablistenhanced.reload")) {
                if (label.equalsIgnoreCase("tablistenhanced") || label.equalsIgnoreCase("tle")) {
                    if (args.length == 1) {
                        if (args[0].equalsIgnoreCase("reload")) {
                            this.commandReload();
                            p.sendMessage(ChatColor.GREEN + "TabListEnhanced: Plugin successfully reloaded!");
                            return true;
                        } else {
                            p.sendMessage(ChatColor.RED + "[TabListEnhanced] Usage: /tablistenhanced <set/reload> [player] [group]");
                            return true;
                        }
                    } else if (p.hasPermission("tablistenhanced.set")) {
                        if (args.length == 3) {
                            if (args[0].equalsIgnoreCase("set")) {
                                if (Bukkit.getPlayer(args[1]) != null) {
                                    Iterator<String> var7 = this.epsb.groupKeys.iterator();

                                    do {
                                        if (!var7.hasNext()) {
                                            p.sendMessage(ChatColor.RED + "[TabListEnhanced] Group " + ChatColor.GOLD + args[2] + ChatColor.RED + " not found.");
                                            return true;
                                        }

                                        keys = (String)var7.next();
                                    } while(!keys.replaceAll("groups\\.", "").equalsIgnoreCase(args[2]));

                                    TLUserConfigs cm = new TLUserConfigs(this.plugin, Bukkit.getPlayer(args[1]));
                                    FileConfiguration f = cm.getConfig();
                                    f.set("group", args[2]);
                                    cm.reload();
                                    cm.saveConfig();
                                    this.epsb.group.put(Bukkit.getPlayer(args[1]).getUniqueId().toString(), args[2]);
                                    this.plugin.up.checkGroupUpdate(Bukkit.getPlayer(args[1]));
                                    p.sendMessage(ChatColor.GREEN + "[TabListEnhanced] Successfully set " + args[1] + "'s group to " + args[2] + "!");
                                    this.plugin.usb.updateboard();
                                } else {
                                    p.sendMessage(ChatColor.RED + "[TabListEnhanced] The player " + args[1] + " is not online!");
                                }
                            } else {
                                p.sendMessage(ChatColor.RED + "[TabListEnhanced] Usage: /tablistenhanced <set/reload> [player] [group]");
                            }
                        } else {
                            p.sendMessage(ChatColor.RED + "[TabListPro] Usage: /tablistenhanced <set/reload> [player] [group]");
                        }
                        return true;
                    } else {
                        var10001 = this.plugin;
                        p.sendMessage(TabListEnhanced.colorString(this.plugin.getConfig().getString("no-permission")));
                        return true;
                    }
                }
            } else {
                var10001 = this.plugin;
                p.sendMessage(TabListEnhanced.colorString(this.plugin.getConfig().getString("no-permission")));
            }
        } else if (sender instanceof CommandSender && (label.equalsIgnoreCase("tablistenhanced") || label.equalsIgnoreCase("tle"))) {
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("reload")) {
                    this.commandReload();
                    System.out.println("[TabListEnhanced] Plugin successfully reloaded!");
                    return true;
                }

                System.out.println("[TabListEnhanced] Console usage: /tablistenhanced <set/reload> [player] [group]");
                return true;
            }

            if (args.length == 3) {
                if (args[0].equalsIgnoreCase("set")) {
                    if (Bukkit.getPlayer(args[1]) != null) {
                        Iterator<String> var11 = this.epsb.groupKeys.iterator();

                        do {
                            if (!var11.hasNext()) {
                                System.out.println("[TabListPro] Group " + args[2] + " not found.");
                                return true;
                            }

                            keys = (String)var11.next();
                        } while(!this.plugin.getConfig().getString("groups." + keys).contains(args[2]));

                        TLUserConfigs cm = new TLUserConfigs(this.plugin, Objects.requireNonNull(Bukkit.getPlayer(args[1])));
                        FileConfiguration f = cm.getConfig();
                        if (f.getString("group") != null && Objects.requireNonNull(f.getString("group")).contains(args[2])) {
                            System.out.println("[TabListEnhanced] The player " + args[1] + " is already in the group " + args[2] + "!");
                            return true;
                        }

                        f.set("group", args[2]);
                        cm.reload();
                        cm.saveConfig();
                        this.epsb.group.put(Objects.requireNonNull(Bukkit.getPlayer(args[1])).getUniqueId().toString(), args[2]);
                        this.plugin.up.checkGroupUpdate(Bukkit.getPlayer(args[1]));
                        System.out.println("[TabListEnhanced] Set " + args[1] + "'s group to " + args[2] + "!");
                        this.plugin.usb.updateboard();
                        return true;
                    }

                    System.out.println("[TabListEnhanced] The player " + args[1] + " is not online!");
                    return true;
                }

                System.out.println("[TabListEnhanced] Console usage: /tablistenhanced <set/reload> [player] [group]");
                return true;
            }

            System.out.println("[TabListEnhanced] Console usage: /tablistenhanced <set/reload> [player] [group]");
            return true;
        }

        return true;
    }
}
