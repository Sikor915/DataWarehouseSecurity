package pl.polsl.sikorski.falfus.WarehouseSecurity;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.deidentifier.arx.*;
import org.deidentifier.arx.AttributeType.Hierarchy;
import org.deidentifier.arx.AttributeType.Hierarchy.DefaultHierarchy;
import org.deidentifier.arx.Data.DefaultData;
import org.deidentifier.arx.criteria.KAnonymity;

import pl.polsl.sikorski.falfus.WarehouseSecurity.KAnonymityTesting.Record;
import static pl.polsl.sikorski.falfus.WarehouseSecurity.KAnonymityTesting.isKAnonymous;
import static pl.polsl.sikorski.falfus.WarehouseSecurity.KAnonymityTesting.loadDataset;
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
        List<Record> dataset = Arrays.asList(
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
    }
    
    public static void main(String[] args) {
        
        //testArx();
        testKAnonymity();
        try {
            loadDataset();
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
            Logger.getLogger(ARXTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
