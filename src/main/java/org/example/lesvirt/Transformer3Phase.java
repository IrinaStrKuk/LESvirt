package org.example.lesvirt;

import java.util.Arrays;

public class Transformer3Phase extends ElectricalComponent {
    private boolean[] phases;
    private double windingRatio;
    private double outputVoltage;
    private double power;

    public Transformer3Phase() {
        super();
        this.phases = new boolean[]{true, true, true};
        this.windingRatio = 0.1;
        this.outputVoltage = 20;
        this.power = 120;
    }

    // Getters and setters
    public boolean[] getPhases() { return phases; }
    public void setPhases(boolean[] phases) { this.phases = phases; }
    public double getWindingRatio() { return windingRatio; }
    public void setWindingRatio(double windingRatio) { this.windingRatio = windingRatio; }
    public double getOutputVoltage() { return outputVoltage; }
    public void setOutputVoltage(double outputVoltage) { this.outputVoltage = outputVoltage; }
    public double getPower() { return power; }
    public void setPower(double power) { this.power = power; }

    public int getActivePhasesCount() {
        int count = 0;
        for (boolean phase : phases) {
            if (phase) count++;
        }
        return count;
    }
}
