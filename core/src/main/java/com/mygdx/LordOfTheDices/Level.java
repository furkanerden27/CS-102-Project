package com.mygdx.LordOfTheDices;

public enum Level {

    LEVEL_1(1, Assets.MAP_1, "Sloth",    3),
    LEVEL_2(2, Assets.MAP_2, "Gluttony", 4),
    LEVEL_3(3, Assets.MAP_1, "Lust",     5),
    LEVEL_4(4, Assets.MAP_2, "Wrath",    6),
    LEVEL_5(5, Assets.MAP_1, "Envy",     7),
    LEVEL_6(6, Assets.MAP_2, "Pride",    8);

    private final int number;
    private final String mapFile;
    private final String bossName;
    private final int mobCount;

    Level(int number, String mapFile, String bossName, int mobCount) {
        this.number = number;
        this.mapFile = mapFile;
        this.bossName = bossName;
        this.mobCount = mobCount;
    }

    public static Level fromNumber(int num) {
        for (Level l : values()) {
            if (l.number == num) return l;
        }
        return LEVEL_1;
    }

    public Level next() {
        int nextNum = number + 1;
        if (nextNum > 6) return null;
        return fromNumber(nextNum);
    }
    
    public int getNumber(){
        return number;
    }
    public String getMapFile(){ 
        return mapFile;
    }
    public String getBossName(){
        return bossName;
    }
    public int getMobCount(){
        return mobCount;
    }
}
