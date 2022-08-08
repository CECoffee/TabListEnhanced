package club.cookie.tablistenhanced.versiondetect;

import club.cookie.tablistenhanced.TabListEnhanced;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import org.bukkit.entity.Player;

public class VersionDetector1182 implements TabListEnhanced.TabV {
    public TabListEnhanced plugin11;

    public VersionDetector1182(TabListEnhanced plugin) {
        this.plugin11 = plugin;
    }

    public VersionDetector1182(VersionDetector1182 plugin15) {
    }

    public void sendTabHF(Player player, String header, String footer) {
        PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.PLAYER_LIST_HEADER_FOOTER);
        packet.getChatComponents().write(0, WrappedChatComponent.fromJson("{\"text\": \"" + header + "\"}"));
        packet.getChatComponents().write(1, WrappedChatComponent.fromJson("{\"text\": \"" + footer + "\"}"));
        //IChatBaseComponent headerJSON = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + header + "\"}");
        //IChatBaseComponent footerJSON = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + footer + "\"}");

        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player,packet);
            /*Field headerField = packet.getClass().getDeclaredField("header");
            headerField.setAccessible(true);
            headerField.set(packet, headerJSON);
            headerField.setAccessible(!headerField.isAccessible());
            Field footerField = packet.getClass().getDeclaredField("footer");
            footerField.setAccessible(true);
            footerField.set(packet, footerJSON);
            footerField.setAccessible(!footerField.isAccessible());*/

        } catch (Exception var11) {
            var11.printStackTrace();
        }
    }
}