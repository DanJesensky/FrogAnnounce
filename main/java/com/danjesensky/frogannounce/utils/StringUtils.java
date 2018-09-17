package com.danjesensky.frogannounce.utils;

import org.bukkit.ChatColor;

public class StringUtils {
    public static String recolorText(String text) {
        return text
                .replaceAll("&AQUA;", ChatColor.AQUA.toString())
                .replaceAll("&BLACK;", ChatColor.BLACK.toString())
                .replaceAll("&BLUE;", ChatColor.BLUE.toString())
                .replaceAll("&DARK_AQUA;", ChatColor.DARK_AQUA.toString())
                .replaceAll("&DARK_BLUE;", ChatColor.DARK_BLUE.toString())
                .replaceAll("&DARK_GRAY;", ChatColor.DARK_GRAY.toString())
                .replaceAll("&DARK_GREEN;", ChatColor.DARK_GREEN.toString())
                .replaceAll("&DARK_PURPLE;", ChatColor.DARK_PURPLE.toString())
                .replaceAll("&RED;", ChatColor.RED.toString())
                .replaceAll("&DARK_RED;", ChatColor.DARK_RED.toString())
                .replaceAll("&GOLD;", ChatColor.GOLD.toString())
                .replaceAll("&GRAY;", ChatColor.GRAY.toString())
                .replaceAll("&GREEN;", ChatColor.GREEN.toString())
                .replaceAll("&LIGHT_PURPLE;", ChatColor.LIGHT_PURPLE.toString())
                .replaceAll("&PURPLE;", ChatColor.LIGHT_PURPLE.toString())
                .replaceAll("&PINK;", ChatColor.LIGHT_PURPLE.toString())
                .replaceAll("&WHITE;", ChatColor.WHITE.toString())
                .replaceAll("&b;", ChatColor.AQUA.toString())
                .replaceAll("&0;", ChatColor.BLACK.toString())
                .replaceAll("&9;", ChatColor.BLUE.toString())
                .replaceAll("&3;", ChatColor.DARK_AQUA.toString())
                .replaceAll("&1;", ChatColor.DARK_BLUE.toString())
                .replaceAll("&8;", ChatColor.DARK_GRAY.toString())
                .replaceAll("&2;", ChatColor.DARK_GREEN.toString())
                .replaceAll("&5;", ChatColor.DARK_PURPLE.toString())
                .replaceAll("&4;", ChatColor.DARK_RED.toString())
                .replaceAll("&6;", ChatColor.GOLD.toString())
                .replaceAll("&7;", ChatColor.GRAY.toString())
                .replaceAll("&a;", ChatColor.GREEN.toString())
                .replaceAll("&d;", ChatColor.LIGHT_PURPLE.toString())
                .replaceAll("&c;", ChatColor.RED.toString())
                .replaceAll("&f;", ChatColor.WHITE.toString())
                .replaceAll("&e;", ChatColor.YELLOW.toString())
                .replaceAll("&k;", ChatColor.MAGIC.toString())
                .replaceAll("&MAGIC;", ChatColor.MAGIC.toString())
                .replaceAll("&BOLD;", ChatColor.BOLD.toString())
                .replaceAll("&ITALIC;", ChatColor.ITALIC.toString())
                .replaceAll("&STRIKE;", ChatColor.STRIKETHROUGH.toString())
                .replaceAll("&UNDERLINE;", ChatColor.UNDERLINE.toString())
                .replaceAll("&RESET;", ChatColor.RESET.toString());
    }

    public static boolean anyEqualIgnoreCase(String arg, String... any){
        if(any == null || arg == null || any.length == 0){
            return false;
        }

        for(String str: any){
            if(arg.equalsIgnoreCase(str)){
                return true;
            }
        }

        return false;
    }

    public static String join(int startingIndex, String delimiter, String... array){
        if(array.length - startingIndex == 1){
            return array[startingIndex];
        }

        StringBuilder sb = new StringBuilder();
        for(int i = startingIndex; i < array.length; i++) {
            sb.append(array[i]);
            sb.append(delimiter);
        }
        String s = sb.toString();
        return s.substring(0, s.lastIndexOf(delimiter));
    }
}
