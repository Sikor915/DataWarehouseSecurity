package pl.polsl.sikorski.falfus.WarehouseSecurity;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.deidentifier.arx.*;
import org.deidentifier.arx.AttributeType.Hierarchy;
import org.deidentifier.arx.AttributeType.Hierarchy.DefaultHierarchy;
import org.deidentifier.arx.Data.DefaultData;
import org.deidentifier.arx.criteria.KAnonymity;

import pl.polsl.sikorski.falfus.WarehouseSecurity.KAnonymityTesting.Record;
import static pl.polsl.sikorski.falfus.WarehouseSecurity.KAnonymityTesting.isKAnonymous;
//import static pl.polsl.sikorski.falfus.WarehouseSecurity.KAnonymityTesting.loadDataset;
import static pl.polsl.sikorski.falfus.WarehouseSecurity.KAnonymityTesting.printResult;

/**
 *
 * @author Kacper Sikorski
 * @author Mateusz Falfus
 */
public class ARXTest {

    static void testArx() {
        DefaultData data = Data.create();
        data.add("age", "gender", "zipcode");
        data.add("34", "male", "81667");
        data.add("45", "female", "81675");
        data.add("66", "male", "81925");
        data.add("70", "female", "81931");
        data.add("34", "female", "81931");
        data.add("70", "male", "81931");
        data.add("45", "male", "81931");

        // Define hierarchies
        DefaultHierarchy age = Hierarchy.create();
        age.add("34", "<50", "*");
        age.add("45", "<50", "*");
        age.add("66", ">=50", "*");
        age.add("70", ">=50", "*");

        DefaultHierarchy gender = Hierarchy.create();
        gender.add("male", "*");
        gender.add("female", "*");

        // Only excerpts for readability
        DefaultHierarchy zipcode = Hierarchy.create();
        zipcode.add("81667", "8166*", "816**", "81***", "8****", "*****");
        zipcode.add("81675", "8167*", "816**", "81***", "8****", "*****");
        zipcode.add("81925", "8192*", "819**", "81***", "8****", "*****");
        zipcode.add("81931", "8193*", "819**", "81***", "8****", "*****");

        data.getDefinition().setAttributeType("age", age);
        data.getDefinition().setAttributeType("gender", gender);
        data.getDefinition().setAttributeType("zipcode", zipcode);

        // Create an instance of the anonymizer
        ARXAnonymizer anonymizer = new ARXAnonymizer();
        ARXConfiguration config = ARXConfiguration.create();
        config.addPrivacyModel(new KAnonymity(3));
        config.setSuppressionLimit(0d);
        ARXResult result = null;
        try {
            result = anonymizer.anonymize(data, config);
        } catch (IOException e) {
            System.err.println(e);
        }

        // Print info
        printResult(result, data);

        System.out.println(" - Transformed data:");
        Iterator<String[]> transformed = result.getOutput(false).iterator();
        while (transformed.hasNext()) {
            System.out.print("   ");
            System.out.println(Arrays.toString(transformed.next()));
        }
    }

    static void testKAnonymity() {
        /*List<Record> dataset = Arrays.asList(
                new Record("31-35", "8166*", "Flu"),
                new Record("31-35", "8166*", "Cold"),
                new Record("36-40", "8166*", "Cancer"),
                new Record("36-40", "8166*", "Flu"),
                new Record("36-40", "8166*", "Cold"),
                new Record("36-40", "8166*", "Cancer")
        );

        int k = 2;
        boolean result = isKAnonymous(dataset, k);
        System.out.println("Dataset is " + k + "-anonymous: " + result);
         */
    }

    public static class MedicalRecord {

        private String name;
        private String age;
        private String gender;
        private String bloodType;
        private String medicalCondition;
        private String dateOfAdmission;
        private String doctor;
        private String hospital;
        private String insuranceProvider;
        private String billingAmount;
        private String roomNumber;
        private String admissionType;
        private String dischargeDate;
        private String medication;
        private String testResults;

        // Constructor
        public MedicalRecord(String name, String age, String gender, String bloodType,
                String medicalCondition, String dateOfAdmission, String doctor,
                String hospital, String insuranceProvider, String billingAmount,
                String roomNumber, String admissionType, String dischargeDate,
                String medication, String testResults) {
            this.name = name;
            this.age = age;
            this.gender = gender;
            this.bloodType = bloodType;
            this.medicalCondition = medicalCondition;
            this.dateOfAdmission = dateOfAdmission;
            this.doctor = doctor;
            this.hospital = hospital;
            this.insuranceProvider = insuranceProvider;
            this.billingAmount = billingAmount;
            this.roomNumber = roomNumber;
            this.admissionType = admissionType;
            this.dischargeDate = dischargeDate;
            this.medication = medication;
            this.testResults = testResults;
        }

        // Getters only (add setters if needed)
        public String getName() {
            return name;
        }

        public String getAge() {
            return age;
        }

        public String getGender() {
            return gender;
        }

        public String getBloodType() {
            return bloodType;
        }

        public String getMedicalCondition() {
            return medicalCondition;
        }

        public String getDateOfAdmission() {
            return dateOfAdmission;
        }

        public String getDoctor() {
            return doctor;
        }

        public String getHospital() {
            return hospital;
        }

        public String getInsuranceProvider() {
            return insuranceProvider;
        }

        public String getBillingAmount() {
            return billingAmount;
        }

        public String getRoomNumber() {
            return roomNumber;
        }

        public String getAdmissionType() {
            return admissionType;
        }

        public String getDischargeDate() {
            return dischargeDate;
        }

        public String getMedication() {
            return medication;
        }

        public String getTestResults() {
            return testResults;
        }
        
        String getQuasiIdentifier() {
            return age + "|" + gender + "|" + bloodType + "|" + dateOfAdmission + "|" + dischargeDate + "|" 
                    + hospital + "|" + insuranceProvider + "|" + roomNumber + "|" + admissionType + "|" + billingAmount + "|" 
                    + medication;
        }
        
        void PrintValues(){
            System.out.println(name + "|" + age + "|" + gender + "|" + bloodType + "|" + medicalCondition + "|" + dateOfAdmission + "|" + dischargeDate + "|" 
                    + hospital + "|" + doctor + "|" + insuranceProvider + "|" + roomNumber + "|" + admissionType + "|" + billingAmount + "|" 
                    + medication + "|" + testResults);
        }
    }

    public static List<MedicalRecord> convertToRecords(File csvFile) throws Exception {
        // Load the CSV strictly as strings
        DataSource source = DataSource.createCSVSource(csvFile, StandardCharsets.UTF_8, ',', true);
        source.addColumn("Name", DataType.STRING);
        source.addColumn("Age", DataType.STRING);
        source.addColumn("Gender", DataType.STRING);
        source.addColumn("Blood Type", DataType.STRING);
        source.addColumn("Medical Condition", DataType.STRING);
        source.addColumn("Date of Admission", DataType.STRING);
        source.addColumn("Doctor", DataType.STRING);
        source.addColumn("Hospital", DataType.STRING);
        source.addColumn("Insurance Provider", DataType.STRING);
        source.addColumn("Billing Amount", DataType.STRING);
        source.addColumn("Room Number", DataType.STRING);
        source.addColumn("Admission Type", DataType.STRING);
        source.addColumn("Discharge Date", DataType.STRING);
        source.addColumn("Medication", DataType.STRING);
        source.addColumn("Test Results", DataType.STRING);

        Data data = Data.create(source);
        DataHandle handle = data.getHandle();

        List<MedicalRecord> records = new ArrayList<>();
        int rowCount = handle.getNumRows();

        for (int i = 0; i < rowCount; i++) {
            records.add(new MedicalRecord(
                    handle.getValue(i, 0),
                    handle.getValue(i, 1),
                    handle.getValue(i, 2),
                    handle.getValue(i, 3),
                    handle.getValue(i, 4),
                    handle.getValue(i, 5),
                    handle.getValue(i, 6),
                    handle.getValue(i, 7),
                    handle.getValue(i, 8),
                    handle.getValue(i, 9),
                    handle.getValue(i, 10),
                    handle.getValue(i, 11),
                    handle.getValue(i, 12),
                    handle.getValue(i, 13),
                    handle.getValue(i, 14)
            ));
        }

        return records;
    }
    
    public static boolean isKAnonymous(List<MedicalRecord> records, int k) {
        Map<String, Integer> qiCounts = new HashMap<>();

        for (MedicalRecord record : records) {
            String qi = record.getQuasiIdentifier();
            qiCounts.put(qi, qiCounts.getOrDefault(qi, 0) + 1);
        }
        
        for (int count : qiCounts.values()) {
            if (count < k) {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        File path = new File("src/main/resources/healthcare_Novice.csv");
        int K = 7;
        System.out.println("Testing file: " + path + "\nThe K level is " + K);
        try {
            List<MedicalRecord> list = convertToRecords(path);
            if(isKAnonymous(list, K)){
                System.out.println("This set is kAnonymous");
            }
            else{
                System.out.println("This set is not kAnonymous");
            }
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        }//testArx();
        /*testKAnonymity();
        try {
            loadDataset();
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
            Logger.getLogger(ARXTest.class.getName()).log(Level.SEVERE, null, ex);
        }*/
    }
}
