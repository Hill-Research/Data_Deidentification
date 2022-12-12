package com.dupreytherapeutics.Demo;

import com.dupreytherapeutics.Algorithm.Blur.BlurNumber;
import java.security.NoSuchAlgorithmException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Demo_BlurNumber {
	private static final Logger logger = LogManager.getLogger(Demo_BlurNumber.class);
	public static void main(String[] args) throws NoSuchAlgorithmException {
		String[] examples = {"22岁","5","45year old","34","36sui"};
		BlurNumber blurNumberEngine = new BlurNumber();
		for (int i=0;i<examples.length;i++) {
			String StandardTime = blurNumberEngine.blur(examples[i],"10");
			logger.info("index: %d, example time sequence: %s, standard time sequence: %s\n", i, examples[i], StandardTime);
		}
	}
}