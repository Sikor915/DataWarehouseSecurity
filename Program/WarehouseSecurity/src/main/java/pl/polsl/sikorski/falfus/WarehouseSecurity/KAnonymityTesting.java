/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pl.polsl.sikorski.falfus.WarehouseSecurity;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.*;
import org.deidentifier.arx.ARXAnonymizer;
import org.deidentifier.arx.ARXConfiguration;
import org.deidentifier.arx.ARXLattice;
import org.deidentifier.arx.ARXResult;
import org.deidentifier.arx.AttributeType;
import org.deidentifier.arx.AttributeType.Hierarchy;
import org.deidentifier.arx.Data;
import org.deidentifier.arx.DataSource;
import org.deidentifier.arx.DataType;
import org.deidentifier.arx.criteria.KAnonymity;

/**
 *
 * @author sikor
 */
public class KAnonymityTesting {
    
    static Data data;
    
    // Sample record class
    static class Record {
        String age;
        String zipCode;
        String disease;

        Record(String age, String zipCode, String disease) {
            this.age = age;
            this.zipCode = zipCode;
            this.disease = disease;
        }

        // Returns a string representing the quasi-identifier combination
        String getQuasiIdentifier() {
            return age + "|" + zipCode;
        }
    }
    
    // Method to check k-anonymity
    public static boolean isKAnonymous(List<Record> records, int k) {
        Map<String, Integer> qiCounts = new HashMap<>();

        // Count occurrences of each quasi-identifier combination
        for (Record record : records) {
            String qi = record.getQuasiIdentifier();
            qiCounts.put(qi, qiCounts.getOrDefault(qi, 0) + 1);
        }

        // Verify that each combination appears at least k times
        for (int count : qiCounts.values()) {
            if (count < k) {
                return false;
            }
        }
        return true;
    }
    
    public static void loadDataset() throws IOException {
        File path = new File("src/main/resources/healthcare_dataset.csv");
        DataSource source = DataSource.createCSVSource(path, StandardCharsets.UTF_8, ',', true);
        source.addColumn("Age", DataType.INTEGER);
        source.addColumn("Gender", DataType.STRING);
        source.addColumn("Blood Type", DataType.STRING);
        source.addColumn("Doctor", DataType.STRING);
        source.addColumn("Date of Admission", DataType.STRING);
        source.addColumn("Room Number", DataType.INTEGER);
        source.addColumn("Discharge Date", DataType.STRING);
        data = Data.create(source);
        path = new File("src/main/resources/age_hierarchy.csv");
        data.getDefinition().setAttributeType("Age", Hierarchy.create(path, StandardCharsets.UTF_8, ','));
        path = new File("src/main/resources/date_hierarchy.csv");
        data.getDefinition().setAttributeType("Date of Admission", Hierarchy.create(path, StandardCharsets.UTF_8, ','));
        data.getDefinition().setAttributeType("Discharge Date", Hierarchy.create(path, StandardCharsets.UTF_8, ','));
        path = new File("src/main/resources/room_hierarchy.csv");
        data.getDefinition().setAttributeType("Room Number", Hierarchy.create(path, StandardCharsets.UTF_8, ','));
        path = new File("src/main/resources/gender_hierarchy.csv");
        data.getDefinition().setAttributeType("Gender", Hierarchy.create(path, StandardCharsets.UTF_8, ','));
        
        data.getDefinition().setAttributeType("Blood Type", AttributeType.INSENSITIVE_ATTRIBUTE);
        data.getDefinition().setAttributeType("Doctor", AttributeType.INSENSITIVE_ATTRIBUTE);
        
        //TODO: anonymize billing and remove firstname from doctor. Also do something with blood type
        
        //data.removeColumn();
        
        ARXAnonymizer anonymizer = new ARXAnonymizer();
        
        ARXConfiguration config = ARXConfiguration.create();
        config.addPrivacyModel(new KAnonymity(2));
        config.setSuppressionLimit(0.5d);
        ARXResult result = anonymizer.anonymize(data, config);
        
        printResult(result, data);
        
        System.out.print(" - Writing data...");
        result.getOutput(false).save("src/main/resources/test_anonymized.csv", ',');
        System.out.println("Done!");
    }
    
    static void printResult(final ARXResult result, final Data data) {

        // Print time
        final DecimalFormat df1 = new DecimalFormat("#####0.00");
        final String sTotal = df1.format(result.getTime() / 1000d) + "s";
        System.out.println(" - Time needed: " + sTotal);

        // Extract
        final ARXLattice.ARXNode optimum = result.getGlobalOptimum();
        final List<String> qis = new ArrayList<>(data.getDefinition().getQuasiIdentifyingAttributes());

        if (optimum == null) {
            System.out.println(" - No solution found!");
            return;
        }

        // Initialize
        final StringBuffer[] identifiers = new StringBuffer[qis.size()];
        final StringBuffer[] generalizations = new StringBuffer[qis.size()];
        int lengthI = 0;
        int lengthG = 0;
        for (int i = 0; i < qis.size(); i++) {
            identifiers[i] = new StringBuffer();
            generalizations[i] = new StringBuffer();
            identifiers[i].append(qis.get(i));
            generalizations[i].append(optimum.getGeneralization(qis.get(i)));
            if (data.getDefinition().isHierarchyAvailable(qis.get(i))) {
                generalizations[i].append("/").append(data.getDefinition().getHierarchy(qis.get(i))[0].length - 1);
            }
            lengthI = Math.max(lengthI, identifiers[i].length());
            lengthG = Math.max(lengthG, generalizations[i].length());
        }

        // Padding
        for (int i = 0; i < qis.size(); i++) {
            while (identifiers[i].length() < lengthI) {
                identifiers[i].append(" ");
            }
            while (generalizations[i].length() < lengthG) {
                generalizations[i].insert(0, " ");
            }
        }

        // Print
        System.out.println(" - Information loss: " + result.getGlobalOptimum().getLowestScore() + " / " + result.getGlobalOptimum().getHighestScore());
        System.out.println(" - Optimal generalization");
        for (int i = 0; i < qis.size(); i++) {
            System.out.println("   * " + identifiers[i] + ": " + generalizations[i]);
        }
        System.out.println(" - Statistics");
        System.out.println(result.getOutput(result.getGlobalOptimum(), false).getStatistics().getEquivalenceClassStatistics());
    }

}
