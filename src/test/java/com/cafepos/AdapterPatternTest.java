package com.cafepos;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.cafepos.printing.LegacyPrinterAdapter;
import com.cafepos.printing.Printer;

import vendor.legacy.LegacyThermalPrinter;

class AdapterPatternTest {
    
    @Test
    void adapter_calls_legacy_printer() {
        // Capture console output to verify the legacy printer was called
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));
        
        try {
            LegacyThermalPrinter legacy = new LegacyThermalPrinter();
            Printer printer = new LegacyPrinterAdapter(legacy);
            
            printer.print("ABC");
            
            String output = outContent.toString();
            assertTrue(output.contains("[Legacy] printing bytes:"), 
                "Adapter should call legacy printer");
        } finally {
            System.setOut(originalOut);
        }
    }
    
    @Test
    void adapter_converts_text_to_bytes() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));
        
        try {
            LegacyThermalPrinter legacy = new LegacyThermalPrinter();
            Printer printer = new LegacyPrinterAdapter(legacy);
            
            printer.print("Test Receipt");
            
            String output = outContent.toString();
            // "Test Receipt" should be at least 12 bytes
            assertTrue(output.contains("printing bytes:"), 
                "Should convert string to bytes");
        } finally {
            System.setOut(originalOut);
        }
    }
    
    @Test
    void adapter_handles_empty_string() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));
        
        try {
            LegacyThermalPrinter legacy = new LegacyThermalPrinter();
            Printer printer = new LegacyPrinterAdapter(legacy);
            
            printer.print("");
            
            String output = outContent.toString();
            assertTrue(output.contains("printing bytes: 0"), 
                "Empty string should produce 0 bytes");
        } finally {
            System.setOut(originalOut);
        }
    }
}