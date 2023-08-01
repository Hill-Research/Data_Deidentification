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

package com.dupreytherapeutics.SampleGenerator;

import java.util.Random;

public class TimeGen {

  public static String gen() {
    Random rand = new Random();
    StringBuilder sb = new StringBuilder();
    sb.append(rand.nextInt(82) + 1940);
    sb.append("年");
    sb.append(rand.nextInt(13));
    sb.append("月");
    sb.append(rand.nextInt(29));
    sb.append("日");
    return sb.toString();
  }
}
