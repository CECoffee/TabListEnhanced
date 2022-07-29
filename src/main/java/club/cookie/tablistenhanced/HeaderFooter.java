package club.cookie.tablistenhanced;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import club.cookie.tablistenhanced.config.TLUserConfigs;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class HeaderFooter {
    private final TabListEnhanced plugin;
    static int fastestUpdateRateReq = 1;
    public static ArrayList<String> headerAnimation;
    public static ArrayList<String> footerAnimation;
    public Map<String, String> headerAndFooterTabText = new HashMap();

    public HeaderFooter(TabListEnhanced plugin) {
        this.plugin = plugin;
    }


    public void tabHeader() {
        Bukkit.getServer().getScheduler().runTaskTimer(this.plugin, new Runnable() {
            int headerIndex = 0;

            public void run() {
                if (HeaderFooter.this.plugin.getConfig().getBoolean("header-enabled")) {
                    String headerText;
                    if (this.headerIndex >= HeaderFooter.headerAnimation.size()) {
                        this.headerIndex = 0;

                    }
                    headerText = HeaderFooter.headerAnimation.get(this.headerIndex);
                    if (TabListEnhanced.placeholderapi) {
                        headerText = PlaceholderAPI.setPlaceholders(null, headerText);
                    }
                    HeaderFooter.this.headerAndFooterTabText.put("header", headerText);

                    ++this.headerIndex;
                }

            }
        }, 0L, this.plugin.getConfig().getInt("header-interval"));
    }

    public void tabFooter() {
        Bukkit.getServer().getScheduler().runTaskTimer(this.plugin, new Runnable() {
            int footerIndex = 0;

            public void run() {
                if (HeaderFooter.this.plugin.getConfig().getBoolean("footer-enabled")) {
                    String footerText;
                    if (this.footerIndex < HeaderFooter.footerAnimation.size()) {
                        footerText = HeaderFooter.footerAnimation.get(this.footerIndex);
                        if (TabListEnhanced.placeholderapi) {
                            footerText = PlaceholderAPI.setPlaceholders(null, footerText);
                        }

                        HeaderFooter.this.headerAndFooterTabText.put("footer", footerText);
                    } else {
                        this.footerIndex = 0;
                        footerText = HeaderFooter.footerAnimation.get(this.footerIndex);
                        if (TabListEnhanced.placeholderapi) {
                            footerText = PlaceholderAPI.setPlaceholders(null, footerText);
                        }

                        HeaderFooter.this.headerAndFooterTabText.put("footer", footerText);
                    }

                    ++this.footerIndex;
                }

            }
        }, 0L, this.plugin.getConfig().getInt("footer-interval"));
    }

    public void tabRefresh() {
        if (this.plugin.getConfig().getInt("header-interval") >= this.plugin.getConfig().getInt("footer-interval")) {
            fastestUpdateRateReq = this.plugin.getConfig().getInt("footer-interval") - 1;
        } else {
            fastestUpdateRateReq = this.plugin.getConfig().getInt("header-interval") - 1;
        }

        if (fastestUpdateRateReq == 0) {
            fastestUpdateRateReq = 1;
        }

        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this.plugin, () -> {
            if (this.plugin.getConfig().getBoolean("header-enabled") || this.plugin.getConfig().getBoolean("footer-enabled")) {
                Iterator<? extends Player> var1 = Bukkit.getServer().getOnlinePlayers().iterator();

                while(true) {
                    while(true) {
                        Player ppl;
                        do {
                            if (!var1.hasNext()) {
                                return;
                            }

                            ppl = (Player)var1.next();
                        } while(ppl == null);

                        TabListEnhanced var10002;
                        TabListEnhanced var10003;
                        if (this.plugin.getConfig().getBoolean("header-enabled") && this.plugin.getConfig().getBoolean("footer-enabled")) {
                            TabListEnhanced.TabV var10000 = this.plugin.tabV;
                            var10002 = this.plugin;
                            String var3 = TabListEnhanced.colorString(this.headerAndFooterTabText.get("header"));
                            var10003 = this.plugin;
                            var10000.sendTabHF(ppl, var3, TabListEnhanced.colorString(this.headerAndFooterTabText.get("footer")));
                        } else if (!this.plugin.getConfig().getBoolean("header-enabled")) {
                            if (this.headerAndFooterTabText.get("footer") != null) {
                                var10003 = this.plugin;
                                this.plugin.tabV.sendTabHF(ppl, "", TabListEnhanced.colorString(this.headerAndFooterTabText.get("footer")));
                            }
                        } else {
                            if (this.plugin.getConfig().getBoolean("footer-enabled")) {
                                return;
                            }

                            if (this.headerAndFooterTabText.get("header") != null) {
                                var10002 = this.plugin;
                                this.plugin.tabV.sendTabHF(ppl, TabListEnhanced.colorString(this.headerAndFooterTabText.get("header")), "");
                            }
                        }
                    }
                }
            }
        }, 0L, fastestUpdateRateReq);
    }
}
