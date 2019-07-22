import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PowerTACAnalysis {
	public static double DOUBLE_MIN_VALUE = Double.MAX_VALUE*-1;
	enum POWERTAC {
		gameId,gameName,status,gameSize,gameLength,lastTick,weatherLocation,weatherDate,logUrl,AgentUDE18,Bunnie19,COLDPower19,CrocodileAgent19,EWIIS3,Mertacor2019,SPOT,VidyutVanika;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		try {
			//fullGameCSVParse();
			smallGameCSVParse(POWERTAC.SPOT.ordinal(), POWERTAC.Mertacor2019.ordinal());
			smallGameCSVParse(POWERTAC.SPOT.ordinal(), POWERTAC.AgentUDE18.ordinal());
			smallGameCSVParse(POWERTAC.SPOT.ordinal(), POWERTAC.CrocodileAgent19.ordinal());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void smallGameCSVParse(int brokerId1, int brokerId2) throws Exception {
		FileWriter fwOutputOrderbook = new FileWriter("PowerTACRankingSPOT.csv", true);
		PrintWriter pwOutputOrderbook = new PrintWriter(new BufferedWriter(fwOutputOrderbook));
		String bootName = "games.finals_2019_07_1_1.csv"; //"testGames.csv";// 
		
		Map<Integer, Map<Integer, List<Double>>> gameMapBySize = new HashMap<Integer, Map<Integer, List<Double>>>();
		
		try(BufferedReader br = new BufferedReader(new FileReader(bootName))) {
			String line = br.readLine();
			line = br.readLine();
			while (line != null) {
				//System.out.println(lineCount);
				String [] arrData = line.split(",", -1);
				
				// Get the broker data
				// Get the gameSize
				int gameSize = Integer.parseInt(arrData[POWERTAC.gameSize.ordinal()]);
				Map<Integer, List<Double>> brokerMapById = gameMapBySize.get(gameSize);
				if(brokerMapById == null) {
					brokerMapById = new HashMap<Integer, List<Double>>();
				}
				
				if(arrData[brokerId1].trim().equalsIgnoreCase("") || arrData[brokerId2].trim().equalsIgnoreCase("")) {
					// We don't care
				}
				else {
					
					List<Double> winMap = brokerMapById.get(brokerId2);
					if(winMap == null) {
						winMap = new ArrayList<Double>();
						winMap.add(0.0);
						winMap.add(0.0);
					}
					
					double brokerScore1 = Double.parseDouble(arrData[brokerId1]);
					double brokerScore2 = Double.parseDouble(arrData[brokerId2]);
					if(brokerScore1 > brokerScore2) {
						Double winCount = winMap.get(0);
						winCount++;
						winMap.set(0, winCount);
					}
					else { 
						Double lossCount = winMap.get(1);
						lossCount++;
						winMap.set(1, lossCount);
					}
					brokerMapById.put(brokerId2, winMap);
				}
				gameMapBySize.put(gameSize, brokerMapById);
				line = br.readLine();
			}
			
			// Print the ranking
			for(Map.Entry<Integer, Map<Integer, List<Double>>> entryGame : gameMapBySize.entrySet()) {
				Integer gameSize = entryGame.getKey();
				Map<Integer, List<Double>> brokerMapById = entryGame.getValue();
				
				pwOutputOrderbook.println("gameSize,Broker,Win,Lose,Total");
				for(Map.Entry<Integer, List<Double>> entryBroker : brokerMapById.entrySet()) {
					int agentIndex = entryBroker.getKey();
					pwOutputOrderbook.print(gameSize + ",");
					pwOutputOrderbook.print(POWERTAC.values()[agentIndex] +",");
					
					List<Double> winMap = entryBroker.getValue();
					Double totCount = 0.0;
					Double winCount = winMap.get(0);
					Double loseCount = winMap.get(1);
					if(winCount == null)
						winCount = 0.0;
					if(loseCount == null)
						loseCount = 0.0;
					totCount = winCount + loseCount;
					pwOutputOrderbook.println((winCount/totCount)*100 + "," + (loseCount/totCount)*100 + "," + totCount);
				}
			}  
		}
		pwOutputOrderbook.close();
	}
	
	public static void fullGameCSVParse() throws Exception {
		FileWriter fwOutputOrderbook = new FileWriter("PowerTACRanking.csv");
		PrintWriter pwOutputOrderbook = new PrintWriter(new BufferedWriter(fwOutputOrderbook));
		String bootName = "games.finals_2019_07_1_1.csv"; //"testGames.csv";// 
		
		Map<Integer, Map<Integer, Map<Integer, Integer>>> gameMapBySize = new HashMap<Integer, Map<Integer, Map<Integer, Integer>>>();
		
		ArrayList<Double> agentScore = new ArrayList<Double>();
		ArrayList<Double> sortedScore = new ArrayList<Double>();
		try(BufferedReader br = new BufferedReader(new FileReader(bootName))) {
			String line = br.readLine();
			line = br.readLine();
			while (line != null) {
				//System.out.println(lineCount);
				String [] arrData = line.split(",", -1);
				
				// Get the broker data
				for(int agentId = POWERTAC.AgentUDE18.ordinal(); agentId <= POWERTAC.VidyutVanika.ordinal(); agentId++) {
					if(!arrData[agentId].trim().equalsIgnoreCase("")) {
						agentScore.add(Double.parseDouble(arrData[agentId]));
						sortedScore.add(Double.parseDouble(arrData[agentId]));
					}
					else {
						agentScore.add(DOUBLE_MIN_VALUE);
						sortedScore.add(DOUBLE_MIN_VALUE);
					}
				}
				
				// Sort the scores
				Collections.sort(sortedScore, Collections.reverseOrder());
				
				// Get the gameSize
				int gameSize = Integer.parseInt(arrData[POWERTAC.gameSize.ordinal()]);
				Map<Integer, Map<Integer, Integer>> brokerMapById = gameMapBySize.get(gameSize);
				if(brokerMapById == null) {
					brokerMapById = new HashMap<Integer, Map<Integer, Integer>>();
				}
				
				// Update the ranking count in the hashMap
				
				for(int rank = 0; rank < sortedScore.size(); rank++) {
					
					int agentIndex = agentScore.indexOf(sortedScore.get(rank));
					
					agentScore.set(agentIndex, null);
					
					Map<Integer, Integer> rankingMap = brokerMapById.get(agentIndex);
					if(rankingMap == null) {
						rankingMap = new HashMap<Integer, Integer>();
					}
					
					Integer count = rankingMap.get(rank);
					if(count == null)
						rankingMap.put(rank, 1);
					else {
						rankingMap.put(rank, ++count);
					}
					brokerMapById.put(agentIndex, rankingMap);
						
				}
				gameMapBySize.put(gameSize, brokerMapById);
				
				agentScore.clear();
				sortedScore.clear();
				
				line = br.readLine();
			}
			
			// Print the ranking
			for(Map.Entry<Integer, Map<Integer, Map<Integer, Integer>>> entryGame : gameMapBySize.entrySet()) {
				Integer gameSize = entryGame.getKey();
				Map<Integer, Map<Integer, Integer>> brokerMapById = entryGame.getValue();
				
				pwOutputOrderbook.println("gameSize,Broker,1st,2nd,3rd,4th,5th,6th,7th,8th,totalGames");
				for(Map.Entry<Integer, Map<Integer, Integer>> entryBroker : brokerMapById.entrySet()) {
					int agentIndex = entryBroker.getKey();
					pwOutputOrderbook.print(gameSize + ",");
					System.out.print("GameSize-" + gameSize + ",");
					
					double totGames = 84; // Small games
					if(gameSize == 8) // Large games
						totGames = 40;
					else if(gameSize == 5) // Medium games
						totGames = 140;
					
					pwOutputOrderbook.print(POWERTAC.values()[agentIndex+POWERTAC.AgentUDE18.ordinal()] +",");
					
					Map<Integer, Integer> rankMap = entryBroker.getValue();
					int totCount = 0;
					for(int rank = 0; rank < gameSize; rank++) {
						Integer count = rankMap.get(rank);
						
						if(count == null)
							count = 0;
						totCount += count;
						pwOutputOrderbook.print((count/totGames)*100 + ",");
						System.out.print(POWERTAC.values()[agentIndex+POWERTAC.AgentUDE18.ordinal()]+",Rank-" + rank + ", Count," + count + ",");
					}
					pwOutputOrderbook.println(totCount);
					System.out.println();
				}
			}  
		}
		pwOutputOrderbook.close();
	}
}
