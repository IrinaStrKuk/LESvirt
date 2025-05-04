package org.example.lesvirt;

public class LampBank {
    private boolean[][] lamps;
    private double powerPerLamp;

    public LampBank() {
        this.lamps = new boolean[3][5]; // 3 groups with 5 lamps each
        this.powerPerLamp = 25;
    }

    public boolean[][] getLamps() {
        return lamps;
    }

    public void setLamps(boolean[][] lamps) {
        if (lamps != null && lamps.length == 3 && lamps[0].length == 5) {
            this.lamps = lamps;
        }
    }

    public double getPowerPerLamp() {
        return powerPerLamp;
    }

    public int getActiveLampsCount() {
        int count = 0;
        for (boolean[] group : lamps) {
            for (boolean lamp : group) {
                if (lamp) count++;
            }
        }
        return count;
    }
}
