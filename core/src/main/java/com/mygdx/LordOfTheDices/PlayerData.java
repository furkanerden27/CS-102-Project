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

    //Inventory data is listing in these varaibles
    public ArrayList<String> cards;
    public ArrayList<String> dice;
    public ArrayList<String> relics;

    public PlayerData() {
        cards = new ArrayList<>();
        dice = new ArrayList<>();
        relics = new ArrayList<>();
    }
    //it gets the datas in game if the player click to return menu
    public static PlayerData fromPlayScreen
    (String saveName, int level, float health, float x, float y, 
        Inventory inv, boolean isMobDefeated, boolean isbossDefeated) {
        PlayerData data = new PlayerData();
        data.saveName = saveName;
        data.currentLevel = level;
        data.currentHealth = health;
        data.playerX = x;
        data.playerY = y;
        data.mobDefeated = isMobDefeated;
        data.bossDefeated = isbossDefeated;
        data.timestamp = System.currentTimeMillis();
        data.currentMoney = inv.getGold();

            //we add the datas into our object
        for(Card c : inv.getCards()) {
            data.cards.add(c.getSuit().name() + "-" + c.getRank().getNumericValue());
        }
        for (Relic r : inv.getRelics()) {
            data.relics.add(r.getRelicType().name() + ":" + r.isActive());
        }
        for (Dice d : inv.getDice()) {
            data.dice.add(d.getName());
        }
        return data;
    }
    //it works almost same wth from play screen it is
    //added for funcitonality but removing it is also okey
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
        Inventory inventoryOb = player.getInventory();
        data.currentMoney = inventoryOb.getGold();


        for (Card c : inventoryOb.getCards()) {
            data.cards.add(c.getSuit().name() + "-" + c.getRank().getNumericValue());
        }
        for (Dice d : inventoryOb.getDice()) {
            data.dice.add(d.getName());
        }
        for (Relic r : inventoryOb.getRelics()) {
            data.relics.add(r.getRelicType().name() + ":" + r.isActive());
        }
        return data;
    }
//create the save 
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
//this method help us to turn firebase data into object
    public Inventory toInventory() {
        ArrayList<Card> cardList = new ArrayList<>();
        ArrayList<Dice> diceList = new ArrayList<>();
        ArrayList<Relic> relicList = new ArrayList<>();

        for (String s : cards) {
            String[] split = s.split("-");
            
                Suit suit = Suit.valueOf(split[0]);
                int rankValue = Integer.parseInt(split[1]);
                Rank rank = intToRank(rankValue);
                if (rank != null) {
                    if (rankValue >= 11) {
                        cardList.add(new SpecialCard(suit, rank));
                    } else {
                        cardList.add(new Card(suit, rank));
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

    //Serialization method to send request to firebase
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

    //it convert a firebase json into playerdata object
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
        if (data.saveName == null || data.saveName.isEmpty()){
            return null;
        }
        return data;
    }

    public static ArrayList<PlayerData> fromJsonAll(String json) {
        ArrayList<PlayerData> list = new ArrayList<>();
        if (json == null || json.length() < 2){
            return list;
        }
        json = json.trim();
        if (json.startsWith("{")) json = json.substring(1);
        if (json.endsWith("}")) json = json.substring(0, json.length() - 1);

        int i = 0;
        while (i < json.length()) {
            int keyStart = json.indexOf('"', i);
            if (keyStart == -1){
                break;
            }
            int keyEnd = json.indexOf('"', keyStart + 1);
            if (keyEnd == -1) break;

            int objStart = json.indexOf('{', keyEnd);
            if (objStart == -1){
                break;
            }
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
            PlayerData input = fromJson(obj);
            if (input != null){
                list.add(input);
            }
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
        if (s == null){
            return "";
        }

        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
    //extracts string
    private static String extractString(String json, String stringKey) {
        String search = "\"" + stringKey + "\":\"";
        int start = json.indexOf(search);
        if (start == -1) return "";
        start += search.length();
        int end = json.indexOf('"', start);
        return end == -1 ? "" : json.substring(start, end);
    }
    //extracts int
    private static int extractInt(String json, String intKey) {
        String search = "\"" + intKey + "\":";
        int start = json.indexOf(search);
        if (start == -1){
            return 0;
        }
        start += search.length();
        int end = start;
        while (end < json.length() && (Character.isDigit(json.charAt(end)) || json.charAt(end) == '-'))
            end++;
        try{
            return Integer.parseInt(json.substring(start, end));
        }
        catch (NumberFormatException e){
            return 0;
        }
    }
     //extracts float
    private static float extractFloat(String json, String floatkey) {
        String search = "\"" + floatkey + "\":";
        int start = json.indexOf(search);
        if (start == -1){
            return 0;
        }
        start += search.length();
        int end = start;
        while (end < json.length() && (Character.isDigit(json.charAt(end)) || json.charAt(end) == '-' || json.charAt(end) == '.'))
            end++;
        try {
            return Float.parseFloat(json.substring(start, end));
        }
        catch (NumberFormatException e){ 
            return 0;
        }
    }
    //extracts boolean
    private static boolean extractBoolean(String json, String booleanKey) {
        String search = "\"" + booleanKey + "\":";
        int start = json.indexOf(search);
        if (start == -1){
            return false;
        }
        start += search.length();
        return json.regionMatches(start, "true", 0, 4);
    }
    //extracts long
    private static long extractLong(String json, String longKey) {
        String search = "\"" + longKey + "\":";
        int start = json.indexOf(search);
        if (start == -1) return 0;
        start += search.length();
        int end = start;
        while (end < json.length() && (Character.isDigit(json.charAt(end)) || json.charAt(end) == '-'))
            end++;
        try { return Long.parseLong(json.substring(start, end)); }
        catch (NumberFormatException e) { return 0; }
    }
    //it extracts array
    private static ArrayList<String> extractArray(String json, String Arrkey) {
        ArrayList<String> result = new ArrayList<>();
        String search = "\"" + Arrkey + "\":[";
        int start = json.indexOf(search);
        if (start == -1) {
            String str = extractString(json, Arrkey);
            if (!str.isEmpty()) {
                String[] parts = str.split(",");
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

        int i = 0;
        while (i < arrayContent.length()) {
            int quoteStart = arrayContent.indexOf('"', i);
            if (quoteStart == -1){
                break;
            }
            int quoteEnd = arrayContent.indexOf('"', quoteStart + 1);
            if (quoteEnd == -1){
                break;
            }
            result.add(arrayContent.substring(quoteStart + 1, quoteEnd));
            i = quoteEnd + 1;
        }

        return result;
    }
    //turns card value to enum
    private static Rank intToRank(int value) {
        for (Rank r : Rank.values()){
            if (r.getNumericValue() == value) return r;
        }
        return null;
    }
}
