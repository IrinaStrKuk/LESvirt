package org.example.lesvirt;

public class ElectricalComponent {
    protected double voltage;
    protected double current;
    protected boolean isOn;

    public ElectricalComponent() {
        this.voltage = 0;
        this.current = 0;
        this.isOn = false;
    }

    // Getters and setters
    public double getVoltage() { return voltage; }
    public void setVoltage(double voltage) { this.voltage = voltage; }
    public double getCurrent() { return current; }
    public void setCurrent(double current) { this.current = current; }
    public boolean isOn() { return isOn; }
    public void setOn(boolean on) { isOn = on; }
}
