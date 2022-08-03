package club.cookie.tablistenhanced;

import club.cookie.tablistenhanced.config.TLUserConfigs;
import club.cookie.tablistenhanced.scoreboard.EPScoreboard;
import club.cookie.tablistenhanced.scoreboard.UpdatePlayers;
import club.cookie.tablistenhanced.scoreboard.UpdateScoreboard;
import club.cookie.tablistenhanced.versiondetect.NewVersionDetector1182;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.google.common.io.ByteStreams;
import me.clip.placeholderapi.PlaceholderAPI;
import me.neznamy.tab.api.HeaderFooterManager;
import me.neznamy.tab.api.TabAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TabListEnhanced extends JavaPlugin implements Listener , CommandExecutor {
    //public static ProtocolManager pm;
    public static HeaderFooterManager hfm;

    public static boolean placeholderapi = false;
    public EPScoreboard epsb;
    public UpdateScoreboard usb;
    public UpdatePlayers up;
    public TabV tabV;
    public BukkitTask id = null;
    private static final char COLOR_CHAR = '§';
    private static final String startTag = "#";
    private static final String endTag = "";
    public static TabListEnhanced apiInstance;
    private static final Map<String, List<String>> groupAnimationsStripped = new HashMap();
    private static final String ESSENTIALS_CHAT_FORMAT = "{prestige} {DISPLAYNAME}&r{EP_CHATTAG}&8&l »&r&7 {MESSAGE}";
    private static final String EXAMPLE_MESSAGE = "Hello!";
    private static Map<String, Map<Player, List<String>>> groupAnimations = new HashMap();
    private NewVersionDetector1182 this14;

    public TabListEnhanced(){}
    @Override

    public void onEnable() {
        //pm = ProtocolLibrary.getProtocolManager();
        hfm = TabAPI.getInstance().getHeaderFooterManager();
        apiInstance=this;
        this.usb = new UpdateScoreboard();
        this.up = new UpdatePlayers();
        this.epsb = new EPScoreboard(this);
        this.loadResource("config.yml");
        this.getConfig().options().copyDefaults(false);
        this.reloadConfig();
        HeaderFooter.headerAnimation = new ArrayList(this.getConfig().getStringList("header"));
        HeaderFooter.footerAnimation = new ArrayList(this.getConfig().getStringList("footer"));
        HeaderFooter hf = new HeaderFooter(this);
        this.epsb.permOrder = new ArrayList(this.getConfig().getStringList("sortByPerms"));
        this.epsb.groupKeys = this.getConfig().getConfigurationSection("groups").getKeys(false);
        this.up.updatePlaceholderAPIPlaceholders();
        this.usb.updateboard();
        this.up.rechecking();

        String implVersion;
        try {
            implVersion = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
        } catch (ArrayIndexOutOfBoundsException var8) {
            implVersion = "unknown";
        }

        if ("v1_18_R2".equals(implVersion)) {
            this.tabV = new NewVersionDetector1182(this.this14);
            Bukkit.getServer().getPluginManager().registerEvents(this, this);
        } else {
            this.setEnabled(false);
            System.out.println("[TabListEnhanced] CRITICAL ERROR: TabListEnhanced failed to startup! Cause: " + implVersion + " is not supported!");
            return;
        }


        Bukkit.getServer().getPluginManager().registerEvents(new Listeners(), this);
        Objects.requireNonNull(this.getCommand("tablistenhanced")).setExecutor(new Commands(this.epsb));
        Objects.requireNonNull(this.getCommand("tle")).setExecutor(new Commands(this.epsb));

        for (Player ppl : Bukkit.getServer().getOnlinePlayers()) {
            String ppluuid = ppl.getUniqueId().toString();
            TLUserConfigs cm = new TLUserConfigs(this, ppl);
            FileConfiguration f = cm.getConfig();
            if (!ppl.hasPlayedBefore()) {
                this.epsb.group.put(ppluuid, this.getConfig().getString("default-group"));
                this.epsb.groupTemp.put(ppluuid, this.getConfig().getString("default-group"));
            } else {
                this.epsb.group.put(ppluuid, f.getString("group"));
                this.epsb.groupTemp.put(ppluuid, f.getString("groupTemp"));
            }

            cm.reload();
            cm.saveConfig();
        }

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) {
            System.out.println("[TabListEnhanced]: PlaceholderAPI this not found! PlaceholderAPI placeholders will not work!");
            placeholderapi = false;
        } else {
            System.out.println("[TabListEnhanced]: PlaceholderAPI successfully detected. Feel free to use any PlaceholderAPI placeholders!");
            placeholderapi = true;
        }

        hf.tabHeader();
        hf.tabFooter();
        hf.tabRefresh();

    }


    private void loadResource(String s) {
        File folder = this.getDataFolder();
        if (!folder.exists()){folder.mkdir();}
        File resFile = new File(folder, "config.yml");

        try{
            if(!resFile.exists() && resFile.createNewFile()){
                InputStream in = this.getResource("config.yml");
                Throwable var1 = null;

                try{
                    OutputStream out = Files.newOutputStream(resFile.toPath());
                    Throwable var4 = null;

                    try {ByteStreams.copy(in,out);}catch (Throwable var5){
                        var4 = var5;
                        throw var5;
                    }finally {
                        if(out!=null){
                            if(var4!=null){try{out.close();}catch (Throwable var6){var4.addSuppressed(var6);}}else{out.close();}
                        }
                    }

                }catch (Exception var2){
                    var1=var2;
                    throw var2;
                }finally {
                    if(in!=null){
                        if(var1!=null){
                            try{in.close();}catch (Throwable var3){var1.addSuppressed(var3);}
                        }else {in.close();}
                    }
                }
            }
        }catch (Exception e){e.printStackTrace();}
    }

    @Override
    public void onDisable() {

        for (Player ppl : Bukkit.getServer().getOnlinePlayers()) {
            String pplUuid = ppl.getUniqueId().toString();
            TLUserConfigs cm = new TLUserConfigs(this, ppl);
            FileConfiguration f = cm.getConfig();
            f.set("group", this.epsb.group.get(pplUuid));
            f.set("groupTemp", this.epsb.groupTemp.get(pplUuid));
            cm.reload();
            cm.saveConfig();
        }

    }

    public void loadAnimations() {

        for (String keys : this.getConfig().getConfigurationSection("groups").getKeys(false)) {
            if (this.getConfig().getStringList("groups." + keys + ".display").size() > 0) {
                this.epsb.animations.put(keys, this.getConfig().getStringList("groups." + keys + ".display"));
            }
        }

    }

    public static String colorString(String message) {
        Pattern hexPattern = Pattern.compile("#([A-Fa-f0-9]{6})");
        Matcher matcher = hexPattern.matcher(message);
        StringBuffer buffer = new StringBuffer(message.length() + 32);

        String hexTranslated;
        while(matcher.find()) {
            hexTranslated = matcher.group(1);
            matcher.appendReplacement(buffer, "§x§" + hexTranslated.charAt(0) + '§' + hexTranslated.charAt(1) + '§' + hexTranslated.charAt(2) + '§' + hexTranslated.charAt(3) + '§' + hexTranslated.charAt(4) + '§' + hexTranslated.charAt(5));
        }

        hexTranslated = matcher.appendTail(buffer).toString();
        return ChatColor.translateAlternateColorCodes('&', hexTranslated);
    }

    public static TabListEnhanced getInstance() {
        return apiInstance;
    }

    public String setPlayerTabGroup(Player p, String groupID) {
        if (p != null) {
            Iterator var3 = this.epsb.groupKeys.iterator();

            String keys;
            do {
                if (!var3.hasNext()) {
                    return "[TabListPro] Group " + groupID + " not found in TabListPro's config.";
                }

                keys = (String)var3.next();
            } while(!keys.replaceAll("groups\\.", "").equalsIgnoreCase(groupID));

            TLUserConfigs cm = new TLUserConfigs(JavaPlugin.getPlugin(TabListEnhanced.class), p);
            FileConfiguration f = cm.getConfig();
            f.set("group", groupID);
            cm.reload();
            cm.saveConfig();
            this.epsb.group.put(p.getUniqueId().toString(), groupID);
            JavaPlugin.getPlugin(TabListEnhanced.class).up.checkGroupUpdate(p);
            JavaPlugin.getPlugin(TabListEnhanced.class).usb.updateboard();
            return "[TabListEnhanced] Successfully set " + p.getName() + "'s group to " + groupID + "!";
        } else {
            return "[TabListEnhanced] The player is null.";
        }
    }

    public List<String> getGroupAnimationStripped(String groupID) {
        if (!groupAnimationsStripped.containsKey(groupID.toLowerCase())) {
            List<String> groupAnimation = new ArrayList();

            for (String keys : this.epsb.groupKeys) {
                if (keys.replaceAll("groups\\.", "").equalsIgnoreCase(groupID.toLowerCase())) {
                    groupAnimation = this.getConfig().getStringList("groups." + keys + ".display");
                }
            }

            for(int i = 0; i < ((List)groupAnimation).size(); ++i) {
                try {
                    ((List)groupAnimation).set(i, ((String)((List<?>)groupAnimation).get(i)).substring(((String)((List<?>)groupAnimation).get(i)).lastIndexOf("%") + 2));
                } catch (Exception var5) {
                    ((List)groupAnimation).set(i, "");
                }
            }

            groupAnimationsStripped.put(groupID.toLowerCase(), groupAnimation);
        }

        return groupAnimationsStripped.get(groupID.toLowerCase());
    }

    public static String chatStr(Player player, String tagSuffix) {
        String chatFormat;
        if (!tagSuffix.equals("")) {
            chatFormat = colorString("{prestige} {DISPLAYNAME}&r{EP_CHATTAG}&8&l »&r&7 {MESSAGE}".replaceAll("\\{prestige}", PlaceholderAPI.setPlaceholders(player, "%ezprestige_prestigetag%")).replaceAll("\\{DISPLAYNAME}", player.getDisplayName()).replaceAll("\\{EP_CHATTAG}", tagSuffix).replaceAll("\\{MESSAGE}", "Hello!"));
        } else {
            chatFormat = colorString("{prestige} {DISPLAYNAME}&r{EP_CHATTAG}&8&l »&r&7 {MESSAGE}".replaceAll("\\{prestige}", PlaceholderAPI.setPlaceholders(player, "%ezprestige_prestigetag%")).replaceAll("\\{DISPLAYNAME}", player.getDisplayName()).replaceAll(" \\{EP_CHATTAG}", tagSuffix).replaceAll("\\{MESSAGE}", "Hello!"));
        }

        return chatFormat;
    }

    public List<String> getGroupAnimation(String groupID, Player player) {
        return new ArrayList();
    }

    public int getGroupAnimationSpeed() {
        return this.epsb.updateSpeedGlobal;
    }

    public interface TabV {
        void sendTabHF(Player var1, String var2, String var3);
    }
}
