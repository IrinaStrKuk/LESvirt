package org.example.lesvirt;

public class PowerSupply extends ElectricalComponent {
    private double frequency;
    private double dcVoltage15;
    private double dcVoltage10;

    public PowerSupply() {
        super();
        this.voltage = 220;
        this.frequency = 50;
        this.dcVoltage15 = 0;
        this.dcVoltage10 = 0;
    }

    // Getters and setters
    public double getFrequency() { return frequency; }
    public void setFrequency(double frequency) { this.frequency = frequency; }
    public double getDcVoltage15() { return dcVoltage15; }
    public void setDcVoltage15(double dcVoltage15) { this.dcVoltage15 = dcVoltage15; }
    public double getDcVoltage10() { return dcVoltage10; }
    public void setDcVoltage10(double dcVoltage10) { this.dcVoltage10 = dcVoltage10; }
}
