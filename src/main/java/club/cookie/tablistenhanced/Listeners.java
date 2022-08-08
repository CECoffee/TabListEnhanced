package club.cookie.tablistenhanced;

import club.cookie.tablistenhanced.config.TLUserConfigs;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class Listeners implements Listener {
    private final TabListEnhanced plugin = JavaPlugin.getPlugin(TabListEnhanced.class);

    public Listeners() {
    }

    @EventHandler
    public void onJoinUpdateGroup(PlayerJoinEvent event) {
        Player ppl = event.getPlayer();
        Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, () -> {
            this.plugin.up.resort();
            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> this.plugin.up.checkGroupUpdate(ppl));
        }, 20L);
    }

    @EventHandler
    public void onLeaveSaveGroupAndRemoveTeam(PlayerQuitEvent event) {
        Player p = event.getPlayer();
        String uuid = p.getUniqueId().toString();
        TLUserConfigs cm = new TLUserConfigs(this.plugin, p);
        FileConfiguration f = cm.getConfig();
        f.set("group", this.plugin.epsb.group.get(uuid));
        f.set("groupTemp", this.plugin.epsb.groupTemp.get(uuid));
        cm.reload();
        cm.saveConfig();
        String team = this.plugin.epsb.getTeam(event.getPlayer());
        if (event.getPlayer().getScoreboard().getTeam(team) != null) {
            if (Objects.requireNonNull(event.getPlayer().getScoreboard().getTeam(team)).getSize() <= 1) {
                Objects.requireNonNull(event.getPlayer().getScoreboard().getTeam(team)).unregister();
            } else {
                Objects.requireNonNull(event.getPlayer().getScoreboard().getTeam(team)).removeEntry(event.getPlayer().getName());
            }
        }

    }
}
