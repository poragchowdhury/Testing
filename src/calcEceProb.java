import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class calcEceProb {
	public static double [][][] peakHourDist = new double[8][24][3];
	public static double peakCounts = 0;
	public static double [][] ecetable = new double[168][3];	
	private static final int COUNT_INDEX = 0;
	private static final int PROBABILITY_INDEX = 1; // peak hour probability
	private static final int ECE_INDEX = 1; // 0 charge; 1 for discharge; Economic control events 
	private static final int ECE_PROBABILITY_INDEX = 2;
	private static final int NO_OF_ROWS = 5;
	public static int CHARGETHRESHOLD = 50;
	public static int CHARGE_EVENT = -1;
	public static int DISCHARGE_EVENT = 1;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			File file = new File("peakHourDisttest.csv"); 
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line = "";
			System.out.println("Loading peak distribution table");
			for(int i = 0; i < NO_OF_ROWS; i++){ // how many lines? 168
				int hour = i%24;
				int day = i/24;
				day += 1;
				line = br.readLine();
				String [] arrVals = line.split(",");
				
				peakHourDist[day][hour][COUNT_INDEX] = Double.parseDouble(arrVals[2]);
				peakHourDist[day][hour][PROBABILITY_INDEX] = Double.parseDouble(arrVals[3]);
				ecetable[i][COUNT_INDEX] = peakHourDist[day][hour][COUNT_INDEX];
				if(peakHourDist[day][hour][COUNT_INDEX] > CHARGETHRESHOLD){
					// Heavy peak hour; so discharge event; default is 0 i.e. discharge event
					ecetable[i][ECE_INDEX]=DISCHARGE_EVENT;
				}
				else {
					// charge event
					ecetable[i][ECE_INDEX]=CHARGE_EVENT;
				}
				
				peakCounts += peakHourDist[day][hour][COUNT_INDEX];
				System.out.println(i+". [" + day + "," + hour +  "] count " + peakHourDist[day][hour][0] + " prob " + peakHourDist[day][hour][1]);
			}
			
			// Calculate the probability of ecetable
			int startIndex = 0;
			boolean flagArrayEnd = false;
			boolean flagSeenToggle = false;
			
			double count = 0;
			double lastSeenECE = ecetable[startIndex][ECE_INDEX];
			
			for(int i = 0; ; i++){
				int idx = i % NO_OF_ROWS;
				if(ecetable[idx][ECE_INDEX] == lastSeenECE){
					count += ecetable[idx][COUNT_INDEX];
				}
				else{
					// divide the count to all count index to calculate pr
					if(flagArrayEnd)
						flagSeenToggle = true;
					
					int endIndex = i;
					double totChargeEventProbCount = 0;
					
					for(int si = startIndex; si < endIndex; si++) {
						int index = si % NO_OF_ROWS;
						if(lastSeenECE == CHARGE_EVENT) {
							ecetable[index][ECE_PROBABILITY_INDEX] = 1-(ecetable[index][COUNT_INDEX]/count);
							if(ecetable[index][ECE_PROBABILITY_INDEX] == 0)
								ecetable[index][ECE_PROBABILITY_INDEX] = 1;
							totChargeEventProbCount += ecetable[index][ECE_PROBABILITY_INDEX];
						}
						else {
							ecetable[index][ECE_PROBABILITY_INDEX] = ecetable[index][COUNT_INDEX]/count;
						}
					}
					
					if(lastSeenECE == CHARGE_EVENT) {
						// Reverse the probabilities for charge events
						// divide the by totSum
						for(int si = startIndex; si < endIndex; si++) {
							int index = si % NO_OF_ROWS;
							ecetable[index][ECE_PROBABILITY_INDEX] /= totChargeEventProbCount;
						}
					}
					
					
					// update the last seen event to current event
					lastSeenECE = ecetable[idx][ECE_INDEX];
					
					startIndex = i;
					count = ecetable[startIndex % NO_OF_ROWS][COUNT_INDEX];
				}
				
				if((i+1) == NO_OF_ROWS) {
					flagArrayEnd = true;
				}
				
				if(flagArrayEnd && flagSeenToggle)
				{
					// break the loop; 
					// because you already looped and found a new event
					break;
				}
				
			}
			
			System.out.println("ECE Probabilities: ");
			for(int i = 0; i < NO_OF_ROWS; i++) {
				if(ecetable[i][ECE_INDEX] == DISCHARGE_EVENT) {
					double upregulation = ecetable[i][ECE_PROBABILITY_INDEX]+1.0;
					System.out.println("Discharge " + ecetable[i][ECE_PROBABILITY_INDEX] + " upregulatio " + upregulation);
				}
				else
					System.out.println("Charge " + ecetable[i][ECE_PROBABILITY_INDEX]*-1);
			}
			
			double expectedStorageMktShare = 20.0/30.0;
			System.out.println(expectedStorageMktShare);
			
			br.close();
		}
		catch(Exception ex) {
			ex.printStackTrace();
			System.out.println("Error");
		}
	}
}
