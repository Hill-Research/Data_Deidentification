package Algorithm;

import java.security.NoSuchAlgorithmException;

import java.util.Vector;
public class Blur{
	public class BlurTime extends Blur{
		private static int[][] splits = {{0,4,6,8},{0,4,6,7},{0,4,5,7},{0,4,6,6},
										{0,4,5,6},{0,2,4,6},{0,4,5,5},{0,2,3,5},
										{0,2,4,5},{0,4,4,4},{0,2,4,4},{0,2,3,4},
										{0,2,3,3},{0,2,2,2}};
		private static int[] daysLeapYear = {31,29,31,30,31,30,31,31,30,31,30,31};
		private static int[] daysNonLeapYear = {31,28,31,30,31,30,31,31,30,31,30,31};
		private static String[] linkWord = {"年","月","日"};
		
		private static boolean checkStandardTime(String[] ymdString) {
			boolean isStandard = true;
			
			int year = Integer.parseInt(ymdString[0]);
			if(ymdString[0].length()==2) {
				if (year>48 && year<49) isStandard=false;
			} else {
				if (year<1949 || year>2048) isStandard=false;
			}
			
			if(ymdString[1] == "") return isStandard;
			
			int month = Integer.parseInt(ymdString[1]);
			if(month == 0 || month > 12) isStandard=false;
			
			if(ymdString[2] == "") return isStandard;
			int day = Integer.parseInt(ymdString[2]);
			if (isStandard) {
				year = (year >= 1900) ? year : year + 1900;
				year = (year >= 1949) ? year : year + 100;
				int[] days;
				if((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) {
					days = daysLeapYear;
				}else {
					days = daysNonLeapYear;
				}
				if(day <= 0 || day > days[month-1]) isStandard = false;
			}
			return isStandard;
		}
		
		private static Vector<Integer> getPositionofSpace(String input) {
			int curLocation=0;
			int spaceCount = 0;
			
			Vector<Integer> spaceLocations = new Vector<Integer>();
			spaceLocations.add(0);
			while (curLocation<input.length()) {
				if(input.charAt(curLocation) == ' ') {
					spaceLocations.add(curLocation-spaceCount);
					spaceCount++;
				}
				curLocation++;
			}
			spaceLocations.add(input.length()-spaceCount);
			return spaceLocations;
		}
		
		private static boolean comparePositionofSpace(Vector<Integer> spaceLocations, int[] split) {
			boolean isAvailable = true;
			for (int k=0; k < spaceLocations.size(); k++) {
				if (spaceLocations.get(k) > split[3]) break;
				int spaceValue = spaceLocations.get(k);
				if(spaceValue!=split[0] && spaceValue!=split[1] && 
						spaceValue!=split[2] && spaceValue!=split[3]) isAvailable = false;
			}
			return isAvailable;
		}
	
		private static String[] getStandardTime(String input) throws NoSuchAlgorithmException {
			String[] StandardTimeSeries = new String[3];
			
			String Time = input.replaceAll("([^0-9])", " ");
			Time = Time.replaceAll("\\s{1,}", " ");
			Vector<Integer> spaceLocations = getPositionofSpace(Time);
			Time = Time.replaceAll("\\s","");
			
			boolean isStandard = false;
			for (int k=0; k<splits.length; k++) {
				int[] split = splits[k];
				if(split[3] > Time.length()) continue;
				if(!comparePositionofSpace(spaceLocations, split)) continue;
				String[] ymdString = {Time.substring(split[0],split[1]),
									Time.substring(split[1],split[2]),
									Time.substring(split[2],split[3])};
				if (checkStandardTime(ymdString)) {
					StandardTimeSeries = ymdString;
					isStandard = true;
					break;
				}
			}
			if (!isStandard) throw new NoSuchAlgorithmException();
			return StandardTimeSeries;
		}
	
		public static String blur(String input) throws NoSuchAlgorithmException {
			String StandardTime = "";
			try {
				StandardTime = blur(input,"year");
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
			return StandardTime;
		}
		
		public static String blur(String input,String level) throws NoSuchAlgorithmException {
			String StandardTime = "";
			try {
				int count=0;
				switch (level) {
					case "year": count=1; break;
					case "month": count=2; break;
					case "day": count=3; break;
					default: break;
				}
				if(count==0) throw new IllegalArgumentException();
				
				String[] StandardTimeSeries = getStandardTime(input);
				StringBuffer StandardTimeBuffer = new StringBuffer();
				for(int k = 0; k < count; k++) {
					String TimeElement = StandardTimeSeries[k];
					if(TimeElement == "" || TimeElement == null) continue;
					int Time = Integer.parseInt(TimeElement);
					if(k==0) {
						Time = (Time >= 1900) ? Time : Time + 1900;
						Time = (Time >= 1949) ? Time : Time + 100;
					}
					StandardTimeBuffer.append(Time);
					StandardTimeBuffer.append(linkWord[k]);
				}
				StandardTime = StandardTimeBuffer.toString();
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				System.out.println("The value of level should be chosen in year/month/day.");
				e.printStackTrace();
			}
			return StandardTime;
		}
	}
	
	public static class BlurAge extends Blur {
		private static int getStandardAge(String input) {
			String Age = input.replaceAll("[^0-9]","");
			int standardAgeValue = Integer.parseInt(Age);
			return standardAgeValue;
		}
		
		public static String blur(String input) throws NoSuchAlgorithmException{
			String StandardAge = null;
			try {
				StandardAge = blur(input,"10");
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
			return StandardAge;
		}
		
		public static String blur(String input, String level) throws NoSuchAlgorithmException {
			int count = Integer.parseInt(level);
			int standardAgeValue = getStandardAge(input);
			int period = standardAgeValue / count;
			String standardAge = String.format("%d岁-%d岁",period*count,(period+1)*count);
			return standardAge;
		}
	}
}
