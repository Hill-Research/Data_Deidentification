//package Demo;

import java.security.NoSuchAlgorithmException;

import Algorithm.Blur.BlurTime;

public class Demo_BlurTime {

	public static void main(String[] args) throws NoSuchAlgorithmException{
		String[] examples = {"2022年0108","2001年2月15日","20040507","2012-11-20","2021,12,1","20118月5日",
				"2013年58", "1972年11月08日"};
		for (int i=0;i<examples.length;i++) {
			String StandardTime = BlurTime.blur(examples[i],"month");
			System.out.printf("index: %d, example time sequence: %s, standard time sequence: %s\n", i, examples[i], StandardTime);
		}
	}
}
