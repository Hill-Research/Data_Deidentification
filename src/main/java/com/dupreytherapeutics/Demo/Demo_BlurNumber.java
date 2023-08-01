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

import com.dupreytherapeutics.Algorithm.Blur.BlurNumber;
import java.security.NoSuchAlgorithmException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Demo_BlurNumber {
  private static final Logger logger = LogManager.getLogger(Demo_BlurNumber.class);

  public static void main(String[] args) throws NoSuchAlgorithmException {
    String[] examples = {"22岁", "5", "45year old", "34", "36sui"};
    BlurNumber blurNumberEngine = new BlurNumber();
    for (int i = 0; i < examples.length; i++) {
      String StandardTime = blurNumberEngine.blur(examples[i], "10");
      logger.info(
          "index: %d, example time sequence: %s, standard time sequence: %s\n",
          i, examples[i], StandardTime);
    }
  }
}
