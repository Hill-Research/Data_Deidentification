package com.dupreytherapeutics.Demo;

import java.security.NoSuchAlgorithmException;
import com.dupreytherapeutics.Algorithm.Blur.BlurTime;

public class Demo_BlurTime {

	public static void main(String[] args) throws NoSuchAlgorithmException {
		String[] examples = {"000000","20070701171207","20070907161105",
				"2022年0108","2001年2月15日","20040507","2012-11-20",
				"2021,12,1","20118月5日","2013年58","721022","251230",
				"2025年2月4","1997nian910ri","19979月20","1995-12-21",
				"2023,11,2","1972年11月08日","22年02月12日","78年10月12日",
				"78-12-21","25年3月15日","2022年13月34日","2022年8月","20228",
				"199706","1997年","22年","2204","18nian","225","22年4月",
				"1996年3月","25年12月","25-08"};
		BlurTime blurTimeEngine = new BlurTime();
		for (int i=0;i<examples.length;i++) {
			String StandardTime = blurTimeEngine.blur(examples[i],"month");
			System.out.printf("index: %d, example time sequence: %s, standard time sequence: %s\n", i, examples[i], StandardTime);
		}
	}
}
