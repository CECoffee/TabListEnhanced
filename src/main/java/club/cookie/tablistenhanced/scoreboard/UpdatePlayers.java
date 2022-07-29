package club.cookie.tablistenhanced.scoreboard;

import club.cookie.tablistenhanced.TabListEnhanced;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public class UpdatePlayers {
    private final TabListEnhanced plugin = JavaPlugin.getPlugin(TabListEnhanced.class);

    public UpdatePlayers() {
    }

    private Scoreboard statsScoreboard(Player p) {
        Scoreboard scoreboard;
        if (Bukkit.getScoreboardManager().getMainScoreboard() != null) {
            scoreboard = p.getScoreboard();
        } else {
            scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        }

        if (scoreboard.getObjective(p.getName()) == null) {
            Objective objective = scoreboard.registerNewObjective(p.getName(), "dummy");
            objective.setDisplayName("TLE Tab Sorting");
        }

        return scoreboard;
    }

    public void rechecking() {
        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this.plugin, () -> {
            this.resort();
            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {

                for (Player p : Bukkit.getOnlinePlayers()) {
                    this.checkGroupUpdate(p);
                }

            });
        }, 20L, this.plugin.getConfig().getInt("update-sorting-and-groups"));
    }

    public void resort() {

        for (Player p : Bukkit.getOnlinePlayers()) {
            Scoreboard sb = this.statsScoreboard(p);

            for (Player ppl : Bukkit.getOnlinePlayers()) {
                this.plugin.epsb.setTeam(sb, ppl);
            }

            p.setScoreboard(sb);
        }

    }

    public void checkGroupUpdate(Player ppl) {
        try {
            String uuid = ppl.getUniqueId().toString();

            for (String keys : this.plugin.epsb.groupKeys) {
                this.plugin.epsb.group.putIfAbsent(uuid, this.plugin.getConfig().getString("default-group"));
                this.plugin.epsb.getClass();
                if (ppl.hasPermission(this.plugin.getConfig().getString("groups." + keys + ".orHasPermission")) && !((String) this.plugin.epsb.group.get(uuid)).equalsIgnoreCase(keys)) {
                    this.plugin.epsb.groupTemp.put(uuid, keys);
                    this.plugin.epsb.updatePlayerPrefixSuffixPlaceholderString(ppl);
                    break;
                }

                if (!ppl.hasPermission(this.plugin.getConfig().getString("groups." + keys + ".orHasPermission")) && ((String) this.plugin.epsb.groupTemp.get(uuid)).equalsIgnoreCase(keys) && !((String) this.plugin.epsb.groupTemp.get(uuid)).equalsIgnoreCase((String) this.plugin.epsb.group.get(uuid))) {
                    this.plugin.epsb.groupTemp.put(uuid, this.plugin.epsb.group.get(uuid));
                    this.plugin.epsb.updatePlayerPrefixSuffixPlaceholderString(ppl);
                    break;
                }

                if (((String) this.plugin.epsb.group.get(uuid)).equalsIgnoreCase(keys)) {
                    this.plugin.epsb.groupTemp.put(uuid, keys);
                    this.plugin.epsb.updatePlayerPrefixSuffixPlaceholderString(ppl);
                    break;
                }
            }
        } catch (Exception ignored) {
        }

    }

    public void updatePlaceholderAPIPlaceholders() {
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {

            for (Player p : Bukkit.getOnlinePlayers()) {
                (new EPScoreboard(this.plugin)).updatePlayerPrefixSuffixPlaceholderString(p);
            }

        });
    }
}
