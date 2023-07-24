package com.project.test.util.junit.mobile;

/**
 * 
 * @author Valentino Milanov
 * 
 * Enum for Mobile devices used for testing
 */
public enum Devices {

	EMULATOR_REMOTE_1("[Device name]","UDID of device"),
    ;
    
    private final String udid;
    private final String deviceName;

    private Devices(String deviceName, String udid) {
        this.deviceName = deviceName;
        this.udid = udid;
    }

    public String getUdid() {
        return udid;
    }
    
    public String getDeviceName() {
        return deviceName;
    }
}
