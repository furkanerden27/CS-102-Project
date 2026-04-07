package com.mygdx.LordOfTheDices;

import java.util.ArrayList;

import com.mygdx.LordOfTheDices.Card.Rank;
import com.mygdx.LordOfTheDices.Card.Suit;
import com.mygdx.LordOfTheDices.Relic.RelicType;

public class PlayerData {

    public String saveName;
    public int currentLevel;
    public float currentHealth;
    public int currentMoney;
    public float playerX;
    public float playerY;
    public long timestamp;
    public boolean mobDefeated;
    public boolean bossDefeated;

    //Inventory data
    public ArrayList<String> cards;
    public ArrayList<String> dice;
    public ArrayList<String> relics;

    public PlayerData() {
        cards = new ArrayList<>();
        dice = new ArrayList<>();
        relics = new ArrayList<>();
    }

    public static PlayerData fromPlayScreen(String saveName, int level, float health,
                                            float x, float y, Inventory inv,
                                            boolean mobDefeated, boolean bossDefeated) {
        PlayerData data = new PlayerData();
        data.saveName = saveName;
        data.currentLevel = level;
        data.currentHealth = health;
        data.playerX = x;
        data.playerY = y;
        data.mobDefeated = mobDefeated;
        data.bossDefeated = bossDefeated;
        data.timestamp = System.currentTimeMillis();
        data.currentMoney = inv.getGold();

        for (Card c : inv.getCards()) {
            data.cards.add(c.getSuit().name() + "-" + c.getRank().getNumericValue());
        }
        for (Dice d : inv.getDice()) {
            data.dice.add(d.getName());
        }
        for (Relic r : inv.getRelics()) {
            data.relics.add(r.getRelicType().name() + ":" + r.isActive());
        }

        return data;
    }

    public static PlayerData fromPlayer(String saveName, int level, Player player) {
        PlayerData data = new PlayerData();
        data.saveName = saveName;
        data.currentLevel = level;
        data.currentHealth = player.getHealth();
        data.playerX = player.getX();
        data.playerY = player.getY();
        data.mobDefeated = player.isMobDefeated();
        data.bossDefeated = player.isBossDefeated();
        data.timestamp = System.currentTimeMillis();

        Inventory inv = player.getInventory();
        data.currentMoney = inv.getGold();

        for (Card c : inv.getCards()) {
            data.cards.add(c.getSuit().name() + "-" + c.getRank().getNumericValue());
        }
        for (Dice d : inv.getDice()) {
            data.dice.add(d.getName());
        }
        for (Relic r : inv.getRelics()) {
            data.relics.add(r.getRelicType().name() + ":" + r.isActive());
        }

        return data;
    }

    public static PlayerData newSave(String saveName, int level, int health, int money) {
        PlayerData data = new PlayerData();
        data.saveName = saveName;
        data.currentLevel = level;
        data.currentHealth = health;
        data.currentMoney = money;
        data.playerX = 0;
        data.playerY = 0;
        data.timestamp = System.currentTimeMillis();
        return data;
    }

    public Inventory toInventory() {
        ArrayList<Card> cardList = new ArrayList<>();
        ArrayList<Dice> diceList = new ArrayList<>();
        ArrayList<Relic> relicList = new ArrayList<>();

        for (String s : cards) {
            String[] split = s.split("-");
            if (split.length == 2) {
                Suit suit = Suit.valueOf(split[0]);
                int rankVal = Integer.parseInt(split[1]);
                Rank rank = intToRank(rankVal);
                if (rank != null) {
                    if (rankVal >= 11) {
                        cardList.add(new SpecialCard(suit, rank));
                    } else {
                        cardList.add(new Card(suit, rank));
                    }
                }
            }
        }
        for (String s : dice) {
            diceList.add(new Dice(s));
        }
        for (String s : relics) {
            String[] parts = s.split(":");
            RelicType type = RelicType.valueOf(parts[0]);
            if (parts.length >= 2) {
                boolean active = Boolean.parseBoolean(parts[1]);
                relicList.add(new Relic(type, active));
            } else {
                relicList.add(new Relic(type));
            }
        }

        return new Inventory(diceList, cardList, relicList, currentMoney);
    }

    // Serialize to JSON string for Firebase
    public String toJson(){
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"saveName\":\"").append(escapeJson(saveName)).append("\",");
        sb.append("\"currentLevel\":").append(currentLevel).append(",");
        sb.append("\"currentHealth\":").append((int) currentHealth).append(",");
        sb.append("\"currentMoney\":").append(currentMoney).append(",");
        sb.append("\"playerX\":").append((int) playerX).append(",");
        sb.append("\"playerY\":").append((int) playerY).append(",");
        sb.append("\"cards\":").append(toJsonArray(cards)).append(",");
        sb.append("\"dice\":").append(toJsonArray(dice)).append(",");
        sb.append("\"relics\":").append(toJsonArray(relics)).append(",");
        sb.append("\"mobDefeated\":").append(mobDefeated).append(",");
        sb.append("\"bossDefeated\":").append(bossDefeated).append(",");
        sb.append("\"timestamp\":").append(timestamp);
        sb.append("}");
        return sb.toString();
    }

    /** Parse a PlayerData from a Firebase JSON object string. */
    public static PlayerData fromJson(String json) {
        if (json == null || json.isEmpty()) return null;

        PlayerData data = new PlayerData();
        data.saveName = extractString(json, "saveName");
        data.currentLevel = extractInt(json, "currentLevel");
        data.currentHealth = extractInt(json, "currentHealth");
        data.currentMoney = extractInt(json, "currentMoney");
        data.playerX = extractFloat(json, "playerX");
        data.playerY = extractFloat(json, "playerY");
        data.timestamp = extractLong(json, "timestamp");
        data.cards = extractArray(json, "cards");
        data.dice = extractArray(json, "dice");
        data.relics = extractArray(json, "relics");
        data.mobDefeated = extractBoolean(json, "mobDefeated");
        data.bossDefeated = extractBoolean(json, "bossDefeated");

        if (data.saveName == null || data.saveName.isEmpty()) return null;
        return data;
    }

    public static ArrayList<PlayerData> fromJsonAll(String json) {
        ArrayList<PlayerData> list = new ArrayList<>();
        if (json == null || json.length() < 2) return list;

        json = json.trim();
        if (json.startsWith("{")) json = json.substring(1);
        if (json.endsWith("}")) json = json.substring(0, json.length() - 1);

        int i = 0;
        while (i < json.length()) {
            int keyStart = json.indexOf('"', i);
            if (keyStart == -1) break;
            int keyEnd = json.indexOf('"', keyStart + 1);
            if (keyEnd == -1) break;

            int objStart = json.indexOf('{', keyEnd);
            if (objStart == -1) break;

            int depth = 0;
            int objEnd = objStart;
            for (int j = objStart; j < json.length(); j++) {
                if (json.charAt(j) == '{') depth++;
                else if (json.charAt(j) == '}') {
                    depth--;
                    if (depth == 0) { objEnd = j; break; }
                }
            }

            String obj = json.substring(objStart, objEnd + 1);
            PlayerData entry = fromJson(obj);
            if (entry != null) list.add(entry);

            i = objEnd + 1;
        }

        return list;
    }
    // helpers

    private static String toJsonArray(ArrayList<String> list) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append("\"").append(escapeJson(list.get(i))).append("\"");
        }
        sb.append("]");
        return sb.toString();
    }

    private static String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private static String extractString(String json, String key) {
        String search = "\"" + key + "\":\"";
        int start = json.indexOf(search);
        if (start == -1) return "";
        start += search.length();
        int end = json.indexOf('"', start);
        return end == -1 ? "" : json.substring(start, end);
    }

    private static int extractInt(String json, String key) {
        String search = "\"" + key + "\":";
        int start = json.indexOf(search);
        if (start == -1) return 0;
        start += search.length();
        int end = start;
        while (end < json.length() && (Character.isDigit(json.charAt(end)) || json.charAt(end) == '-'))
            end++;
        try { return Integer.parseInt(json.substring(start, end)); }
        catch (NumberFormatException e) { return 0; }
    }

    private static float extractFloat(String json, String key) {
        String search = "\"" + key + "\":";
        int start = json.indexOf(search);
        if (start == -1) return 0;
        start += search.length();
        int end = start;
        while (end < json.length() && (Character.isDigit(json.charAt(end)) || json.charAt(end) == '-' || json.charAt(end) == '.'))
            end++;
        try { return Float.parseFloat(json.substring(start, end)); }
        catch (NumberFormatException e) { return 0; }
    }

    private static boolean extractBoolean(String json, String key) {
        String search = "\"" + key + "\":";
        int start = json.indexOf(search);
        if (start == -1) return false;
        start += search.length();
        return json.regionMatches(start, "true", 0, 4);
    }

    private static long extractLong(String json, String key) {
        String search = "\"" + key + "\":";
        int start = json.indexOf(search);
        if (start == -1) return 0;
        start += search.length();
        int end = start;
        while (end < json.length() && (Character.isDigit(json.charAt(end)) || json.charAt(end) == '-'))
            end++;
        try { return Long.parseLong(json.substring(start, end)); }
        catch (NumberFormatException e) { return 0; }
    }

    private static ArrayList<String> extractArray(String json, String key) {
        ArrayList<String> result = new ArrayList<>();
        String search = "\"" + key + "\":[";
        int start = json.indexOf(search);
        if (start == -1) {
            // Fallback: might be stored as a plain string (old format compatibility)
            String asString = extractString(json, key);
            if (!asString.isEmpty()) {
                String[] parts = asString.split(",");
                for (String p : parts) {
                    if (!p.trim().isEmpty()) result.add(p.trim());
                }
            }
            return result;
        }
        start += search.length();
        int end = json.indexOf(']', start);
        if (end == -1) return result;

        String arrayContent = json.substring(start, end);
        if (arrayContent.trim().isEmpty()) return result;

        // Parse quoted strings from array
        int i = 0;
        while (i < arrayContent.length()) {
            int qStart = arrayContent.indexOf('"', i);
            if (qStart == -1) break;
            int qEnd = arrayContent.indexOf('"', qStart + 1);
            if (qEnd == -1) break;
            result.add(arrayContent.substring(qStart + 1, qEnd));
            i = qEnd + 1;
        }

        return result;
    }

    private static Rank intToRank(int val) {
        for (Rank r : Rank.values()) {
            if (r.getNumericValue() == val) return r;
        }
        return null;
    }
}
