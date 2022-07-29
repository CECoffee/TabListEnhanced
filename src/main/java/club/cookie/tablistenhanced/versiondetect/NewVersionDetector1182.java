package club.cookie.tablistenhanced.versiondetect;

import java.lang.reflect.Field;

import club.cookie.tablistenhanced.TabListEnhanced;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.game.PacketPlayOutPlayerListHeaderFooter;
import org.bukkit.entity.Player;

public class NewVersionDetector1182 implements TabListEnhanced.TabV {
    public TabListEnhanced plugin11;

    public NewVersionDetector1182(TabListEnhanced plugin) {
        this.plugin11 = plugin;
    }

    public NewVersionDetector1182(NewVersionDetector1182 plugin15) {
    }

    public void sendTabHF(Player player, String header, String footer) {
        IChatBaseComponent headerJSON = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + header + "\"}");
        IChatBaseComponent footerJSON = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + footer + "\"}");
        PacketPlayOutPlayerListHeaderFooter packet = new PacketPlayOutPlayerListHeaderFooter(headerJSON, footerJSON);

        try {
            Field headerField = packet.getClass().getDeclaredField("header");
            headerField.setAccessible(true);
            headerField.set(packet, headerJSON);
            headerField.setAccessible(!headerField.isAccessible());
            Field footerField = packet.getClass().getDeclaredField("footer");
            footerField.setAccessible(true);
            footerField.set(packet, footerJSON);
            footerField.setAccessible(!footerField.isAccessible());

            ProtocolLibrary.getProtocolManager().sendServerPacket(player.getPlayer(), PacketContainer.fromPacket(packet));
        } catch (Exception var11) {
            var11.printStackTrace();
        }
    }
}