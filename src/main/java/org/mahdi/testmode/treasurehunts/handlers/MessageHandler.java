package org.mahdi.testmode.treasurehunts.handlers;

import org.bukkit.ChatColor;

public class MessageHandler {
    private final static String mainText = "&a&l[Treasure]&r : %s";

    public static String setMessageColor(String msg) {
        return ChatColor.translateAlternateColorCodes('&', String.format(
                mainText, msg
        ));
    }

}
