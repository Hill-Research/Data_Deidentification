package Algorithm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Mask {
	public class MaskLocation {
		@SuppressWarnings("rawtypes")
		static HashMap<String,HashMap> base = null;

		public MaskLocation() throws NoSuchAlgorithmException {
			setLocationsName();
		}

		@SuppressWarnings({ "rawtypes", "unchecked" })
		public static void setLocationsName() throws NoSuchAlgorithmException{
			final File file = new File("../data/location.csv");
			file.setReadable(true);
		    BufferedReader reader;
	        HashMap<String, HashMap> china = new HashMap<String, HashMap>();
			try {
				reader = new BufferedReader(new FileReader(file));
		        String record = null;
		        
		        HashMap<String, HashMap> province = null;
		        HashMap<String, HashMap> city = null;
		        String provinceName = null;
		        String cityName = null;
		        String countryName = null;
		        while ((record = reader.readLine()) != null) {
		            String[] keywords = record.split("\\t");
		            int level = Integer.parseInt(keywords[0]);
		            String name = keywords[3];
		            String mergeName = keywords[4];
		            String[] simpliedNameLinkWord = mergeName.split(",");
		            String simpliedName = simpliedNameLinkWord[simpliedNameLinkWord.length-1];
		            String linkWord = name.replace(simpliedName,"");
		            if(linkWord == null) linkWord = "";
		            if(name.equals("市辖区") || name.equals( "直辖区") || name.equals("直辖县")) continue;
		            switch(level) {
			            case 0:
			            	provinceName = simpliedName+","+linkWord;
			            	china.put(provinceName, new HashMap<String, HashMap>());
			            	province = china.get(provinceName);
			            	break;
			            case 1:
			            	cityName = simpliedName+","+linkWord;
			            	province.put(cityName, new HashMap<String, HashMap>());
			            	city = province.get(cityName);
			            	break;
			            case 2:
			            	countryName = simpliedName+","+linkWord;
			            	city.put(countryName, null);
			            	break;
			            case 3:
			            	cityName = simpliedName+","+linkWord;
			            	province.put(cityName, null);
			            	break;
		            }
		        }
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}			
			base = china;
		}
		
		@SuppressWarnings("rawtypes")
		private static String checkStandardLocation (String location, HashMap<String, HashMap> map) throws NoSuchAlgorithmException {
			Iterator<Map.Entry<String, HashMap>> iterator = map.entrySet().iterator();
			String selectedLocation = null;
			int minimumLoc = location.length()+1;
			while (iterator.hasNext()) {
				Map.Entry<String, HashMap> entry = iterator.next();
				String key = entry.getKey();
				String subLocation = key.split(",")[0];
				int firstLoc = location.indexOf(subLocation);
				if(firstLoc != -1 && minimumLoc > firstLoc) {
					minimumLoc = firstLoc;
					selectedLocation = key;
				}
			}
			if(selectedLocation == null) throw new NoSuchAlgorithmException();
			else return selectedLocation;
		}
		
		@SuppressWarnings({ "rawtypes", "unchecked" })
		private static String[] getStandardLocation (String input, HashMap<String, HashMap> fathermap) throws NoSuchAlgorithmException {
			String[] StandardLocationSeries = new String[3];
			String Location = input.replaceAll("([^\\u4e00-\\u9fa5])", "");
			Location.replaceAll("中国|天朝|中华人民共和国|大陆", "");
			HashMap<String, HashMap> map = fathermap;
			for (int k = 0; k < 3 && map != null; k++) {
				try {
					String subLocationLinkWord = checkStandardLocation(Location, map);
					String[] subLocationLinkWordList = subLocationLinkWord.split(",");
					String subLocation = subLocationLinkWordList[0];
					String subLinkWord = (subLocationLinkWordList.length == 2) ? subLocationLinkWordList[1] : "";
					Location = Location.replaceFirst(subLocation, "");
					if (k<2) map = map.get(subLocationLinkWord);
					StandardLocationSeries[k] = subLocation + subLinkWord;
				} catch (NoSuchAlgorithmException e) {break;}
			} 
			return StandardLocationSeries;
		}
		
		public static String mask(String input) throws NoSuchAlgorithmException {
			String standardQuickLocation = "";
			try {
				standardQuickLocation = mask(input, "province");
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
			return standardQuickLocation;
		}
		
		public static String mask(String input, String level) throws NoSuchAlgorithmException {
			String StandardQuickLocation = "";
			try {
				int count=0;
				switch (level) {
					case "province": count = 1; break;
					case "city": count = 2; break;
					case "country": count = 3; break;
					default: break;
				}
				if(count==0) throw new IllegalArgumentException();
				
				String[] StandardLocationSeries = getStandardLocation(input, base);
				StringBuffer StandardLocationBuffer = new StringBuffer();
				for(int k = 0; k < StandardLocationSeries.length; k++) {
					String LocationElement = StandardLocationSeries[k];
					if (LocationElement == null) continue;
					else {
						if (k<count) StandardLocationBuffer.append(LocationElement);
						else {
							StringBuffer stars = new StringBuffer();
							for (int i=0;i <LocationElement.length(); i++) {
								stars.append("*");
							}
							StandardLocationBuffer.append(stars.toString());
						}
					}
				}
				StandardQuickLocation = StandardLocationBuffer.toString();
				StandardQuickLocation.replace("高开区","高新技术产业开发区");
				StandardQuickLocation.replace("经开区", "经济开发区");
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				System.out.println("The value of level should be chosen in province/city/country.");
				e.printStackTrace();
			}
			return StandardQuickLocation;
		}
	}
}
