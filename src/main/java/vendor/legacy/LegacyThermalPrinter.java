package vendor.legacy;

public final class LegacyThermalPrinter {
    public void legacyPrint(byte[] payload) {
        // imagine this talks to a serial port / ESC-POS device
        System.out.println("[Legacy] printing bytes: " +
                payload.length);
    }
}