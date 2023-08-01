/*
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor,
 * Boston, MA  02110-1301, USA.
 */

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
