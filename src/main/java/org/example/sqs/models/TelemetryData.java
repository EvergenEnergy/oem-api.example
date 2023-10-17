package org.example.sqs.models;

import java.util.ArrayList;


public class TelemetryData {
    public String siteID;
    public ArrayList<BatteryInverterData> batteryInverters;
    public ArrayList<HybridInverterData> hybridInverters;
    public ArrayList<SolarInverterData> solarInverters;
    public ArrayList<MeterData> meters;
}
