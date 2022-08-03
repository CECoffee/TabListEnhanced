package club.cookie.tablistenhanced.versiondetect;

import club.cookie.tablistenhanced.TabListEnhanced;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLib;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import me.neznamy.tab.api.TabPlayer;
import net.minecraft.network.chat.IChatBaseComponent;
import org.bukkit.entity.Player;

import static club.cookie.tablistenhanced.TabListEnhanced.hfm;

public class NewVersionDetector1182 implements TabListEnhanced.TabV {
    public TabListEnhanced plugin11;

    public NewVersionDetector1182(TabListEnhanced plugin) {
        this.plugin11 = plugin;
    }

    public NewVersionDetector1182(NewVersionDetector1182 plugin15) {
    }

    public void sendTabHF(Player player, String header, String footer) {
        //PacketContainer packet = new PacketContainer(PacketType.Play.Server.PLAYER_LIST_HEADER_FOOTER);
        IChatBaseComponent headerJSON = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + header + "\"}");
        IChatBaseComponent footerJSON = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + footer + "\"}");
        //PacketPlayOutPlayerListHeaderFooter pack = new PacketPlayOutPlayerListHeaderFooter(headerJSON, footerJSON);

        try {
            //TODO 无效发包（此处使用TabApi，真tm搞不定NMS和protocolLib
            hfm.setHeader((TabPlayer) player, String.valueOf(headerJSON));
            hfm.setFooter((TabPlayer) player, String.valueOf(footerJSON));

            /*Field headerField = packet.getClass().getDeclaredField("header");
            headerField.setAccessible(true);
            headerField.set(packet, headerJSON);
            headerField.setAccessible(!headerField.isAccessible());
            Field footerField = packet.getClass().getDeclaredField("footer");
            footerField.setAccessible(true);
            footerField.set(packet, footerJSON);
            footerField.setAccessible(!footerField.isAccessible());*/

            //TabListEnhanced.pm.sendServerPacket(player,packet,false);
        } catch (Exception var11) {
            var11.printStackTrace();
        }
    }
}