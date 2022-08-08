package club.cookie.tablistenhanced.scoreboard;

import java.util.*;

import me.clip.placeholderapi.PlaceholderAPI;
import club.cookie.tablistenhanced.TabListEnhanced;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class EPScoreboard {
    private final TabListEnhanced plugin;
    public int updateSpeedGlobal;
    public int updateFrame = 0;
    public int biggestAnimationList = 0;
    public Map<String, String> group = new HashMap<>();
    public Map<String, String> groupTemp = new HashMap<>();
    public Set<String> groupKeys;
    public HashMap<String, List<String>> animations = new HashMap<>();
    private static final HashMap<String, String> playerPrefixPlaceheld = new HashMap<>();
    private static final HashMap<String, String> playerSuffixPlaceheld = new HashMap<>();
    public final boolean EP_VERSION = false;
    private final boolean HIGHEST_PRESTIGES_FIRST = true;
    private final int MAX_PRESTIGES = 88;
    private final List<String> RANKS = Arrays.asList("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z");
    public ArrayList<String> permOrder = new ArrayList<>();

    public EPScoreboard(TabListEnhanced plugin) {
        this.plugin = plugin;
    }

    public void updatePlayerPrefixSuffixPlaceholderString(Player p) {
        String uuid = p.getUniqueId().toString();

        try {
            if (TabListEnhanced.placeholderapi) {
                playerPrefixPlaceheld.put(uuid, PlaceholderAPI.setPlaceholders(p, Objects.requireNonNull(this.plugin.getConfig().getString("prefix"))));
                playerSuffixPlaceheld.put(uuid, PlaceholderAPI.setPlaceholders(p, Objects.requireNonNull(this.plugin.getConfig().getString("suffix"))));
            } else {
                playerPrefixPlaceheld.put(uuid, this.plugin.getConfig().getString("prefix"));
                playerSuffixPlaceheld.put(uuid, this.plugin.getConfig().getString("suffix"));
            }
        } catch (Exception ignored) {
        }

    }

    public void setTeam(Scoreboard sb, Player p) {
        String team = this.getTeam(p);
        Team user = sb.getTeam(team);
        String uuid = p.getUniqueId().toString();
        if (playerPrefixPlaceheld.get(uuid) == null || playerSuffixPlaceheld.get(uuid) == null) {
            this.updatePlayerPrefixSuffixPlaceholderString(p);
        }

        if (user == null) {
            sb.registerNewTeam(team);
            user = sb.getTeam(team);
        }

        user.removeEntry(p.getName());
        if (!user.hasEntry(p.getName())) {
            user.addEntry(p.getName());
        }

        if (this.plugin.getConfig().getBoolean("use-displayname")) {
            TabListEnhanced var10001 = this.plugin;
            p.setPlayerListName(TabListEnhanced.colorString(playerPrefixPlaceheld.get(uuid) + p.displayName() + playerSuffixPlaceheld.get(uuid)));
        }

    }

    public String getTeam(Player p) {
        for(int i = 0; i < this.permOrder.size(); ++i) {
            if (p.hasPermission(this.permOrder.get(i))) {
                if (i >= 999) {
                    return i + "TLES";
                }

                if (i >= 99) {
                    return "0" + i + "TLES";
                }

                if (i >= 9) {
                    return "00" + i + "TLES";
                }

                return "000" + i + "TLES";
            }
        }

        return "zzzzzzzzzzzzzzzz";
    }
}
