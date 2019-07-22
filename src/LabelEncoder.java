/**
 * @author: Jose G. Perez <josegperez@mail.com>
 */
import java.util.*;
import java.io.*;
public class LabelEncoder {
    public static final String TRAINING_PATH = "training/";
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        File inputFolder = new File(TRAINING_PATH);
        
        // Build the name and type encoders if needed
        Map<String, Integer> nameEncoder = null;
        Map<String, Integer> typeEncoder = null;
        File fileNameEncoder = new File("nameEncoder.ser");
        File fileTypeEncoder = new File("typeEncoder.ser");
        if (fileNameEncoder.exists() && fileTypeEncoder.exists()) {
            System.out.println("===Loading encoders from file");
            nameEncoder = loadEncoder("nameEncoder.ser");
            typeEncoder = loadEncoder("typeEncoder.ser");
        }
        else{
            System.out.println("===Building encoders");
            // Read all the CSV files for encoding
            Set<String> names = new HashSet<>();
            Set<String> types = new HashSet<>();
            for (final File csvFile : inputFolder.listFiles()){
                System.out.println("Reading file " + csvFile.getName());
                Scanner fileReader = new Scanner(csvFile);
                while (fileReader.hasNextLine()){
                    String line = fileReader.nextLine();
                    String[] spl = line.split(",");
                    names.add(spl[0]);
                    types.add(spl[1]);
                }
                fileReader.close();
            }
            nameEncoder = buildEncoder(names);
            typeEncoder = buildEncoder(types);
            
            // Saving maps
            saveEncoder(nameEncoder, "nameEncoder.ser");
            saveEncoder(typeEncoder, "typeEncoder.ser");
        }
            
        System.out.println(Arrays.asList(nameEncoder));
        System.out.println(Arrays.asList(typeEncoder));
        
        // Encoding and saving to one CSV file
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
            System.out.println("Reading file " + csvFile.getName());
            Scanner fileReader = new Scanner(csvFile);
            while (fileReader.hasNextLine()){
                String[] split = fileReader.nextLine().split(",");
                // Encode
                String customerName = nameEncoder.get(split[0]).toString();
                String powerType = typeEncoder.get(split[1]).toString();
                split[0] = customerName;
                split[1] = powerType;
                
                writer.write(String.join(",", split) + '\n');
            }
            fileReader.close();
        }
        writer.close();
        
        System.out.println("Finished!");
    }
    
    public static void saveEncoder(Map<String, Integer> encoder, String filename) throws IOException{
        System.out.println("Saving encoder to " + filename);
        FileOutputStream fos = new FileOutputStream(filename);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(encoder);
        oos.close();
        fos.close();
    }
    
    public static Map<String, Integer> loadEncoder(String filename) throws IOException, ClassNotFoundException{
        FileInputStream fis = new FileInputStream(filename);
        ObjectInputStream ois = new ObjectInputStream(fis);
        Map<String,Integer> map = (HashMap<String, Integer>) ois.readObject();
        ois.close();
        fis.close();
        
        return map;
    }
    
    public static Map<String, Integer> buildEncoder(Set<String> set){
        Map<String, Integer> map = new HashMap<>();
        
        int idx = 0;
        for (String value : set){
            map.put(value, idx);
            idx++;
        }
        
        return map;
    }
}
