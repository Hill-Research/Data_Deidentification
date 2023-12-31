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

import com.dupreytherapeutics.Algorithm.Blur.BlurTime;
import java.security.NoSuchAlgorithmException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Demo_BlurTime {
  private static final Logger logger = LogManager.getLogger(Demo_BlurTime.class);

  public static void main(String[] args) throws NoSuchAlgorithmException {
    String[] examples = {
      "000000",
      "20070701171207",
      "20070907161105",
      "2022年0108",
      "2001年2月15日",
      "20040507",
      "2012-11-20",
      "2021,12,1",
      "20118月5日",
      "2013年58",
      "721022",
      "251230",
      "2025年2月4",
      "1997nian910ri",
      "19979月20",
      "1995-12-21",
      "2023,11,2",
      "1972年11月08日",
      "22年02月12日",
      "78年10月12日",
      "78-12-21",
      "25年3月15日",
      "2022年13月34日",
      "2022年8月",
      "20228",
      "199706",
      "1997年",
      "22年",
      "2204",
      "18nian",
      "225",
      "22年4月",
      "1996年3月",
      "25年12月",
      "25-08"
    };
    BlurTime blurTimeEngine = new BlurTime();
    for (int i = 0; i < examples.length; i++) {
      String StandardTime = blurTimeEngine.blur(examples[i], "month");
      logger.info(
          "index: %d, example time sequence: %s, standard time sequence: %s\n",
          i, examples[i], StandardTime);
    }
  }
}
