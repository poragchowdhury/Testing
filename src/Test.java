import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Test {

	public static void main (String args[]){
		
		Random r = new Random();
		
		for(int i=0; i<10; i++) {
			int coin = r.nextInt(3);
			System.out.println(coin);
		}
		//StringBuilder sb = new StringBuilder();
		//sb.append("abcd");
		//sb.insert(0, '0');
		//System.out.println(sb.toString());
	}

	public static char firstNonOccuringChar(String input){
		char C = '\0';
		int size = input.length();
		if(size == 0)
			return C;
		if(size == 1)
			return input.charAt(0);

		HashMap<Character, Integer> maps = new HashMap<Character, Integer>();
		for(int i=0; i < size; i++){
			Character tempc = input.charAt(i);
			Integer count = maps.get(tempc);
			if(count == null)
				maps.put(tempc, i);
			else
				maps.put(tempc, -1);
		}

		int lowIndex = Integer.MAX_VALUE;
		for(Map.Entry<Character, Integer> entry : maps.entrySet()){
			int i = entry.getValue();
			if(i<0)
				continue;
			else{
				if (lowIndex > i){
					lowIndex = i;
					C = entry.getKey();
				}
			}
		}
		return C;
	}


}

class Ob{
	public int val = 0;
	Ob(int i){
		val = i;
	}
}