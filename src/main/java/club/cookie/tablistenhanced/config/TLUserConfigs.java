package club.cookie.tablistenhanced.config;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import club.cookie.tablistenhanced.TabListEnhanced;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class TLUserConfigs {
    private final UUID u;
    private FileConfiguration fc;
    private File file;
    private final JavaPlugin plugin = JavaPlugin.getProvidingPlugin(this.getClass());

    public TLUserConfigs(TabListEnhanced plugin2, Player p) {
        this.u = p.getUniqueId();
    }

    private TLUserConfigs(UUID u) {
        this.u = u;
    }

    public Player getOwner() {
        if (this.u == null) {
            try {
                throw new Exception();
            } catch (Exception ignored) {
            }
        }

        return Bukkit.getPlayer(this.u);
    }

    public UUID getOwnerUUID() {
        if (this.u == null) {
            try {
                throw new Exception();
            } catch (Exception ignored) {
            }
        }

        return this.u;
    }

    public JavaPlugin getInstance() {
        return this.plugin;
    }

    public boolean delete() {
        return this.getFile().delete();
    }

    public boolean exists() {
        if (this.fc == null || this.file == null) {
            File temp = new File(this.getDataFolder() + "/userdata/", this.getOwnerUUID() + ".yml");
            if (!temp.exists()) {
                return false;
            }

            this.file = temp;
        }

        return true;
    }

    public File getDataFolder() {
        File dir = new File(TLUserConfigs.class.getProtectionDomain().getCodeSource().getLocation().getPath().replaceAll("%20", " "));
        File d = new File(dir.getParentFile().getPath(), this.getInstance().getName());
        if (!d.exists()) {
            d.mkdirs();
        }

        return d;
    }

    public File getFile() {
        if (this.file == null) {
            this.file = new File(this.getDataFolder() + "/userdata/", this.getOwnerUUID() + ".yml");
            if (!this.file.exists()) {
                try {
                    this.file.createNewFile();
                } catch (IOException ignored) {
                }
            }
        }

        return this.file;
    }

    public FileConfiguration getConfig() {
        if (this.fc == null) {
            this.fc = YamlConfiguration.loadConfiguration(this.getFile());
        }

        return this.fc;
    }

    public void reload() {
        if (this.file == null) {
            this.file = new File(this.getDataFolder(), "data.yml");
            if (!this.file.exists()) {
                try {
                    this.file.createNewFile();
                } catch (IOException ignored) {
                }
            }

            this.fc = YamlConfiguration.loadConfiguration(this.file);
        }

    }

    public void saveConfig() {
        try {
            this.getConfig().save(this.getFile());
        } catch (IOException ignored) {
        }

    }
}
