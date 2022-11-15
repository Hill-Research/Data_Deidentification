package Demo;

import java.security.NoSuchAlgorithmException;

import Algorithm.Blur.BlurNumber;

public class Demo_BlurNumber {
	public static void main(String[] args) throws NoSuchAlgorithmException {
		String[] examples = {"22岁","5","45year old","34","36sui"};
		for (int i=0;i<examples.length;i++) {
			String StandardTime = BlurNumber.blur(examples[i],"10");
			System.out.printf("index: %d, example time sequence: %s, standard time sequence: %s\n", i, examples[i], StandardTime);
		}
	}
}