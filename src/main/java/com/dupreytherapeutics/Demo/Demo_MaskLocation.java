package com.dupreytherapeutics.Demo;

import com.dupreytherapeutics.Algorithm.Mask.MaskLocation;
import java.security.NoSuchAlgorithmException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Demo_MaskLocation {
  private static final Logger logger = LogManager.getLogger(Demo_MaskLocation.class);

  public static void main(String[] args) throws NoSuchAlgorithmException {
    String[] examples = {
      "中国河北省邢台市威县",
      "河北邢台临西",
      "河北省邢台威县",
      "河北省邢台",
      "河北邢台市",
      "北京市",
      "河北邯郸永年",
      "河北邯郸临西县",
      "中国浙江省杭州市西湖区黄龙溪路",
      "上海市闵行区东川路",
      "北京市海淀区",
      "山东省聊城市临清市",
      "山东省威海市环翠区",
      "山东聊城临清",
      "河北省邢台临西",
      "四川省成都市",
      "重庆市沙坪坝区",
      "江苏省南京市雨花台区"
    };
    MaskLocation maskLocationEngine = new MaskLocation();
    maskLocationEngine.initLocationHierarchy();
    for (int i = 0; i < examples.length; i++) {
      String StandardTime = maskLocationEngine.mask(examples[i], "province");
      logger.info(
          "index: %d, example location sequence: %s, standard location sequence: %s\n",
          i, examples[i], StandardTime);
    }
  }
}
