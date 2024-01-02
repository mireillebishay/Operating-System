import java.util.Hashtable;
import java.io.*;

public class HardDisk {
	Hashtable <Integer, Hashtable <String,String>> SSD =  new Hashtable<Integer, Hashtable<String,String>>();
	Hashtable <String, Hashtable <String,String>> SSDPCB =  new Hashtable<String, Hashtable<String,String>>();
	
	public HardDisk() throws IOException {
		File disk = new File("src/disk.txt");
//		disk.delete();
		disk.createNewFile();
		
	}
	
	 public void writeSSDToFile() {
	        try {
	            FileWriter fileWriter = new FileWriter("src/disk.txt");
	            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

	            for (Integer key : SSD.keySet()) {
	                Hashtable<String, String> innerHashtable = SSD.get(key);
//	                System.out.println("here!!!!!");
//	                System.out.println(innerHashtable.keySet());
	                for (String innerKey : innerHashtable.keySet()) {
	                    String value = innerHashtable.get(innerKey);
	                    bufferedWriter.write(key + "," + innerKey + "," + value);
	                    bufferedWriter.newLine();
	                    
	                    
	                }
	            }
	            
	            for (String key : SSDPCB.keySet()) {
	                Hashtable<String, String> innerHashtable = SSDPCB.get(key);
//	                System.out.println("here!!!!!");
//	                System.out.println(innerHashtable.keySet());
	                for (String innerKey : innerHashtable.keySet()) {
	                    String value = innerHashtable.get(innerKey);
	                    bufferedWriter.write(key + "," + innerKey + "," + value);
	                    bufferedWriter.newLine();
	                    
	                    
	                }
	            }

	            bufferedWriter.close();
	            fileWriter.close();
	            System.out.println("SSD data has been written to the file: ");
	        } catch (IOException e) {
	            System.out.println("Error occurred while writing the SSD data to file: " );
	        }
	    }

	    public void readSSDFromFile() {
	        try {
	            FileReader fileReader = new FileReader("src/disk.txt");
	            BufferedReader bufferedReader = new BufferedReader(fileReader);

	            String line;
	            while ((line = bufferedReader.readLine()) != null) {
	                String[] parts = line.split(",");
	                if (parts.length == 3 && !parts[0].startsWith("PCB")) {
	                    int key = Integer.parseInt(parts[0]);
	                    String innerKey = parts[1];
	                    String value = parts[2];

	                    if (!SSD.containsKey(key)) {
	                        SSD.put(key, new Hashtable<String, String>());
	                    }

	                    Hashtable<String, String> innerHashtable = SSD.get(key);
	                    innerHashtable.put(innerKey, value);
	                }
	            }
	            
//	             fileReader = new FileReader("src/disk.txt");
	            bufferedReader = new BufferedReader(fileReader);
//
	            while ((line = bufferedReader.readLine()) != null) {
	                String[] parts = line.split(",");
	                if (parts.length == 3 && parts[0].startsWith("PCB")) {
	                    String key = parts[0];
	                    String innerKey = parts[1];
	                    String value = parts[2];

	                    if (!SSDPCB.containsKey(key)) {
	                        SSDPCB.put(key, new Hashtable<String, String>());
	                    }

	                    Hashtable<String, String> innerHashtable = SSDPCB.get(key);
	                    innerHashtable.put(innerKey, value);
	                }
	            }

	            bufferedReader.close();
	            fileReader.close();
//	            System.out.println(SSD);
	            System.out.println("SSD data has been read from the file:");
	        } catch (IOException e) {
	            System.out.println("Error occurred while reading the SSD data from file: " + e.getMessage());
	        }
	    }
}
