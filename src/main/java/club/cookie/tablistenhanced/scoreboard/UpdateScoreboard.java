package club.cookie.tablistenhanced.scoreboard;

import java.util.List;
import me.clip.placeholderapi.PlaceholderAPI;
import club.cookie.tablistenhanced.TabListEnhanced;
import club.cookie.tablistenhanced.config.TLUserConfigs;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class UpdateScoreboard {
    private final TabListEnhanced plugin = JavaPlugin.getPlugin(TabListEnhanced.class);

    public UpdateScoreboard() {
    }

    public void updateboard() {
        this.plugin.loadAnimations();

        try {
            this.plugin.id.cancel();
        } catch (Exception var3) {
        }

        this.plugin.epsb.updateSpeedGlobal = this.plugin.getConfig().getInt("name-animation");
        this.plugin.epsb.biggestAnimationList = 0;
        this.plugin.epsb.groupKeys = this.plugin.getConfig().getConfigurationSection("groups").getKeys(false);

        for (Player p : Bukkit.getOnlinePlayers()) {
            this.plugin.up.checkGroupUpdate(p);
        }

        this.plugin.id = Bukkit.getServer().getScheduler().runTaskTimer(this.plugin, () -> {
            if (this.plugin.getConfig().getBoolean("use-displayname")) {
                this.plugin.id.cancel();
            } else {

                for (Player p : Bukkit.getOnlinePlayers()) {
                    String uuid = p.getUniqueId().toString();
                    if (this.plugin.epsb.group.get(uuid) == null) {
                        TLUserConfigs cm = new TLUserConfigs(this.plugin, p);
                        FileConfiguration f = cm.getConfig();
                        if (f.getString("group") == null) {
                            this.plugin.epsb.group.put(uuid, this.plugin.getConfig().getString("default-group"));
                            this.plugin.epsb.groupTemp.put(uuid, this.plugin.getConfig().getString("default-group"));
                        } else {
                            f.set("group", this.plugin.epsb.group.get(uuid));
                            f.set("groupTemp", this.plugin.epsb.groupTemp.get(uuid));
                            cm.reload();
                            cm.saveConfig();
                        }
                    }

                    this.plugin.epsb.groupTemp.putIfAbsent(uuid, this.plugin.getConfig().getString("default-group"));
                    Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                        List list = this.plugin.epsb.animations.get(this.plugin.epsb.groupTemp.get(uuid));
                        if (list == null) {
                            this.plugin.epsb.groupTemp.put(uuid, this.plugin.getConfig().getString("default-group"));
                        }

                        if (list != null) {
                            TabListEnhanced var10001;
                            String playerTabText;
                            if (this.plugin.epsb.updateFrame < list.size()) {
                                if (TabListEnhanced.placeholderapi) {
                                    playerTabText = PlaceholderAPI.setPlaceholders(p, (String) list.get(this.plugin.epsb.updateFrame));
                                    var10001 = this.plugin;
                                    p.setPlayerListName(TabListEnhanced.colorString(playerTabText.replaceAll("%player%", p.getName()).replaceAll("%player_displayname%", p.getDisplayName())));
                                } else {
                                    playerTabText = (String) list.get(this.plugin.epsb.updateFrame);
                                    var10001 = this.plugin;
                                    p.setPlayerListName(TabListEnhanced.colorString(playerTabText.replaceAll("%player%", p.getName()).replaceAll("%player_displayname%", p.getDisplayName())));
                                }
                            } else if (TabListEnhanced.placeholderapi) {
                                playerTabText = PlaceholderAPI.setPlaceholders(p, (String) list.get(list.size() - 1));
                                var10001 = this.plugin;
                                p.setPlayerListName(TabListEnhanced.colorString(playerTabText.replaceAll("%player%", p.getName()).replaceAll("%player_displayname%", p.getDisplayName())));
                            } else {
                                playerTabText = (String) list.get(list.size() - 1);
                                var10001 = this.plugin;
                                p.setPlayerListName(TabListEnhanced.colorString(playerTabText.replaceAll("%player%", p.getName()).replaceAll("%player_displayname%", p.getDisplayName())));
                            }

                            int size = this.plugin.getConfig().getStringList("groups." + this.plugin.epsb.groupTemp.get(uuid) + ".display").size();
                            if (size > 0 && size > this.plugin.epsb.biggestAnimationList) {
                                this.plugin.epsb.biggestAnimationList = size;
                            }

                        }
                    });
                }

                ++this.plugin.epsb.updateFrame;
                if (this.plugin.epsb.updateFrame >= this.plugin.epsb.biggestAnimationList) {
                    this.plugin.epsb.updateFrame = 0;
                }

            }
        }, 0L, this.plugin.epsb.updateSpeedGlobal);
    }
}

