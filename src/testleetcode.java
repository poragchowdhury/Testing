import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;


public class testleetcode {
	
	public static final double Cons_TOU_MaxW = 1.1;
	public static final double Cons_TOU_AvgW = 0.9;
	public static final double Cons_TOU_MinW = 0.8;
	public static double [] usage = {11,12,13,14,15,15,15,14,13,12,11,10,11,12,13,14,15,15,15,14,13,12,11,10};
	public static double [] oldweights = new double[usage.length];
	
	public static ArrayList<Integer> newpeaks = new ArrayList<>();
	public static ArrayList<Integer> oldpeaks = new ArrayList<>();
	
	public static double learningRate = 0.3; 
	
	public static void adjustWeights(){
		System.out.println("usage length " + usage.length);
		java.util.Arrays.fill(oldweights, 1);
		double avg = 0;
		int maxindex = 0;
		int secondmaxindex = 0;
		int thirdmaxindex = 0;
		double max = Double.MIN_VALUE;
		for(int i=0; i< 24; i++){
			avg += usage[i];
			if(usage[i]>max){
				max = usage[i];
				thirdmaxindex = secondmaxindex;
				secondmaxindex = maxindex;
				maxindex = i;
			}
		}
		avg/=24;
		for(int i=0;i<24;i++){
			if(maxindex == i || secondmaxindex == i || thirdmaxindex == i)
				oldweights[i] = Cons_TOU_MaxW;
			if(usage[i] > avg)
				oldweights[i] = Cons_TOU_AvgW;
			else
				oldweights[i] = Cons_TOU_MinW;
		}
	}
	
	public static void getPeaks() {
		System.out.println("Get new peaks");
		oldpeaks = new ArrayList<Integer>(newpeaks);
		newpeaks.clear();
		Scanner in = new Scanner(System.in);
		newpeaks.add(in.nextInt());
		newpeaks.add(in.nextInt());
		newpeaks.add(in.nextInt());
	}
	
	public static void updateWeights() {
		double [] newweights = Arrays.copyOfRange(oldweights, 0, oldweights.length);
		Collections.sort(newpeaks);
		double shiftW = 0;
		ArrayList<Double> shiftableW = new ArrayList<>();
		int counter = 0;
		for(Integer peak : newpeaks) {
			shiftW = newweights[peak];
			newweights[peak] = Cons_TOU_MaxW;
			oldweights[oldpeaks.get(counter)] = shiftW;
			counter++;
		}
		
		for(int i=0; i<oldweights.length; i++) {
			
		}
	}
	
	public static void main(String [] args) {
		adjustWeights();
		Scanner in = new Scanner(System.in);
		while(in.nextInt() == 1) {
			getPeaks();
			updateWeights();
		}
		
	}
	
}
