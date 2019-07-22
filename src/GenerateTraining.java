/**
 * @author: Jose G. Perez <josegperez@mail.com>
 * Generates the training ARFF file for consumer/producer prediction (project 1)
 *      from the training CSV files
 */
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class GenerateTraining {
    public static final String TRAINING_PATH = "TestData/";
    
    public static void main(String[] args) throws ClassNotFoundException, IOException{
        // Load encoders
        Map<String, Integer> nameEncoder = loadEncoder("nameEncoder.ser");
        Map<String, Integer> typeEncoder = loadEncoder("typeEncoder.ser");
        
        // Encode all CSV files into one ARFF file
        File inputFolder = new File(TRAINING_PATH);
        System.out.println("===Writing to file output.arff");
        FileWriter writer = new FileWriter("output.arff");
        writer.write("@relation SPOT\n");
        writer.write("@attribute customer_name real\n");
        writer.write("@attribute power_type real\n");
        writer.write("@attribute population real\n");
        writer.write("@attribute date real\n");
        writer.write("@attribute month real\n");
        writer.write("@attribute day real\n");
        writer.write("@attribute hour real\n");
        writer.write("@attribute cloud_coverage real\n");
        writer.write("@attribute temperature real\n");
        writer.write("@attribute wind_direction real\n");
        writer.write("@attribute wind_speed real\n");
        writer.write("@attribute net_usage_production real\n");
        writer.write("@data\n");
        for (final File csvFile : inputFolder.listFiles()){
            System.out.println("\tReading file " + csvFile.getName());
            Scanner fileReader = new Scanner(csvFile);
            while (fileReader.hasNextLine()){
                String[] split = fileReader.nextLine().split(",");
                // Encode
                String customerName = nameEncoder.get(split[0]).toString();
                String powerType = typeEncoder.get(split[1]).toString();
                
                if (split[1].equals("WIND_PRODUCTION")) {
                	//System.out.println(split[1]);
                	split[0] = customerName;
                    split[1] = powerType;
                	writer.write(String.join(",", split) + '\n');
                }
            }
            fileReader.close();
        }
        writer.close();
        
        System.out.println("Finished!");
    }

    public static Map<String, Integer> loadEncoder(String filename) throws IOException, ClassNotFoundException{
        FileInputStream fis = new FileInputStream(filename);
        ObjectInputStream ois = new ObjectInputStream(fis);
        @SuppressWarnings("unchecked")
        Map<String,Integer> map = (HashMap<String, Integer>) ois.readObject();
        ois.close();
        fis.close();
        
        return map;
    }
}
