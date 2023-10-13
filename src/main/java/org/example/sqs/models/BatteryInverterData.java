package org.example.sqs.models;

public class BatteryInverterData {
    public String deviceID;
    public String deviceTime;
    public int batteryPowerW;
    public int meterPowerW;
    public int solarPowerW;
    public int batteryReactivePowerVar;
    public int solarReactivePowerVar;
    public int meterReactivePowerVar;
    public double gridVoltage1V;
    public double gridVoltage2V;
    public double gridVoltage3V;
    public double gridFrequencyHz;
    public double cumulativeBatteryChargeEnergyWh;
    public double cumulativeBatteryDischargeEnergyWh;
    public double cumulativePVGenerationWh;
    public double cumulativeGridImportWh;
    public double cumulativeGridExportWh;
    public double stateOfCharge;
    public double stateOfHealth;
    public int maxChargePowerW;
    public int maxDischargePowerW;
}