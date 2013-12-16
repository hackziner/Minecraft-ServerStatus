/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hackziner;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.plugin.java.JavaPlugin;
import java.net.*;

/**
 *
 * @author hackziner
 * This plugin has been written by hackziner (hackzner@gmail.com)
 * The plugin has been made for the UFBTEAM (http://ufbteam.com)
 * This plugin is release under CeCILL FREE SOFTWARE LICENSE AGREEMENT 2.0
 * Please visit us @ http://ufbteam.com 
 */
public class serverstatus extends JavaPlugin implements Runnable {

    DatagramSocket socket;
    byte[] buffer = new byte[2048];
    String forbuffer;
    DatagramPacket packet;
    Thread thread; // To run the listenner in an other thread

    public void onDisable() {
        if (socket!=null)
            if (!socket.isClosed())
                socket.close();
         System.out.println("[ServerStats]: Disabled");
    }

    public void onEnable() {
        System.out.println("[ServerStats]: Enabled");
        try {
            socket = new DatagramSocket(getServer().getPort() + 1);
            packet = new DatagramPacket(buffer, buffer.length);
            thread = new Thread(this);
            thread.start();

        } catch (SocketException ex) {
            Logger.getLogger(serverstatus.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void run() {
        while (!socket.isClosed()) {
            try {

                packet.setData(buffer);
                socket.receive(packet);
            } catch (IOException ex) {
                Logger.getLogger(serverstatus.class.getName()).log(Level.SEVERE, null, ex);
            }
            try{
            if (packet.getData()[4] == 84) {
                String ServerName = getServer().getServerName().replace("\"", "");
                String Maps = "";
                String ShortGameName = "minecraft";
                String GameName = "http://ufbteam.com";
                int i;

                for (i = 0; i < getServer().getWorlds().size(); i++) {
                    if (i > 0) {
                        Maps = String.format("%s,", Maps);
                    }
                    Maps = String.format("%s%s", Maps, getServer().getWorlds().get(i).getName());

                }



                forbuffer = String.format("%c%c%c%cm", 255, 255, 255, 255); //Header
                forbuffer = String.format("%s%s:%s%c", forbuffer, getServer().getIp(), getServer().getPort(), 0); //Server address
                forbuffer = String.format("%s%s%c", forbuffer, ServerName, 0); // Server Name
                forbuffer = String.format("%s%s%c", forbuffer, Maps, 0); // Maps Name
                forbuffer = String.format("%s%s%c", forbuffer, ShortGameName, 0); // Game Dir
                forbuffer = String.format("%s%s%c", forbuffer, GameName, 0); // Game desc
                forbuffer = String.format("%s%c%c%c%c%c%c%c", forbuffer, getServer().getOnlinePlayers().length, getServer().getMaxPlayers(), 0, 2, 1, 0, 0);


            } else {
                forbuffer = String.format("%c%c%c%cD", 255, 255, 255, 255); //Header
                forbuffer = String.format("%s%c", forbuffer, getServer().getOnlinePlayers().length); // Game desc
                int i;
                for (i = 0; i < getServer().getOnlinePlayers().length; i++) {
                    forbuffer = String.format("%s%c", forbuffer, i); // Player Number
                    forbuffer = String.format("%s%s%c", forbuffer, getServer().getOnlinePlayers()[i].getDisplayName(), 0); // DisplayName
                    forbuffer = String.format("%s%c%c%c%c", forbuffer,  getServer().getOnlinePlayers()[i].getHealth(),0, 0, 0); // score
                    forbuffer = String.format("%s%c%c%c%c", forbuffer, 0, 0, 0, 0); // uptime
                }
            }
            }
            catch(Exception e)
            {
                Logger.getLogger(serverstatus.class.getName()).log(Level.SEVERE, null, e);
            }
            try {
                buffer = forbuffer.getBytes("ISO-8859-1");
                packet.setData(buffer);
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(serverstatus.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                socket.send(packet);
            } catch (IOException ex) {
                Logger.getLogger(serverstatus.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
