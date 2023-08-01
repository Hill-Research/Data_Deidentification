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

package com.dupreytherapeutics.Algorithm.Blur;

import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import java.util.Vector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/** This class implements the interface {@link Blur} for Time item. */
// TODO: make it singleton
public class BlurTime implements Blur {
  private static final Logger logger = LogManager.getLogger(BlurTime.class);

  // TODO: add explanations for the splits array
  private final int[][] splits = {
    {0, 4, 6, 8},
    {0, 4, 6, 7},
    {0, 4, 5, 7},
    {0, 4, 6, 6},
    {0, 4, 5, 6},
    {0, 2, 4, 6},
    {0, 4, 5, 5},
    {0, 2, 3, 5},
    {0, 2, 4, 5},
    {0, 4, 4, 4},
    {0, 2, 4, 4},
    {0, 2, 3, 4},
    {0, 2, 3, 3},
    {0, 2, 2, 2}
  };

  /* Predefined days per month for leap year */
  private final int[] daysLeapYear = {31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

  /* Predefined days per month for non-leap year */
  private final int[] daysNonLeapYear = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

  /* The linking keywords for blurred time */
  private final String[] linkWord = {"年", "月", "日"};

  /**
   * Check if the inptu time is standard or not. Standard time needs to meet the following
   * criterias: 1) year: [1949, 2048] 2) month: [1, 12] 3) day: [1, maximum day in that month] Need
   * to pay attention to the leap year
   *
   * @param ymdString input time string array, which is an array of three elements, representing
   *     year, month, day, respectively.
   * @return whether the input string array represents standard time or not
   */
  private boolean checkStandardTime(String[] ymdString) {
    if (ymdString == null) {
      logger.error("Input time string array is null!");
      System.exit(-1);
    }
    if (ymdString.length > 3) {
      logger.error("Input time string array has invalid length " + ymdString.length);
      System.exit(-1);
    }
    boolean isStandard = true;
    int year = Integer.parseInt(ymdString[0]);
    if (ymdString[0].length() == 2) {
      // TODO: how to check if year is < 1949 or > 2048
    } else {
      if (year < 1949 || year > 2048) {
        isStandard = false;
      }
    }

    if (Objects.equals(ymdString[1], "")) {
      return isStandard;
    }

    int month = Integer.parseInt(ymdString[1]);
    if (month == 0 || month > 12) {
      isStandard = false;
    }

    if (Objects.equals(ymdString[2], "")) {
      return isStandard;
    }
    int day = Integer.parseInt(ymdString[2]);
    if (isStandard) {
      year = (year >= 1900) ? year : year + 1900;
      year = (year >= 1949) ? year : year + 100;
      int[] days;
      if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) {
        days = daysLeapYear;
      } else {
        days = daysNonLeapYear;
      }
      if (day <= 0 || day > days[month - 1]) {
        isStandard = false;
      }
    }
    return isStandard;
  }

  /**
   * Given a input string, record all the positions in the string which are empty spaces.
   *
   * @param input input string
   * @return the positions of empty space
   */
  private Vector<Integer> getPositionofSpace(String input) {
    int curLocation = 0;
    int spaceCount = 0;

    Vector<Integer> spaceLocations = new Vector<>();
    spaceLocations.add(0);
    while (curLocation < input.length()) {
      if (input.charAt(curLocation) == ' ') {
        spaceLocations.add(curLocation - spaceCount);
        spaceCount++;
      }
      curLocation++;
    }
    spaceLocations.add(input.length() - spaceCount);
    return spaceLocations;
  }

  /**
   * Check if the white space locations of input string is valid or not.
   *
   * @param spaceLocations the positions of white space in the input string
   * @param split reference array of valid white space locations
   * @return whether the input string has valid white space positions or not
   */
  private boolean comparePositionofSpace(Vector<Integer> spaceLocations, int[] split) {
    boolean isAvailable = true;
    for (Integer spaceLocation : spaceLocations) {
      if (spaceLocation > split[3]) {
        break;
      }
      int spaceValue = spaceLocation;
      if (spaceValue != split[0]
          && spaceValue != split[1]
          && spaceValue != split[2]
          && spaceValue != split[3]) {
        isAvailable = false;
        break;
      }
    }
    return isAvailable;
  }

  /**
   * get standard time from input string
   *
   * @param input input time string
   * @return standard time array parsed from input time string
   * @throws NoSuchAlgorithmException when the input time string cannot be parsed into standard time
   *     format
   */
  private String[] getStandardTime(String input) throws NoSuchAlgorithmException {
    String[] StandardTimeSeries = new String[3];

    String Time = input.replaceAll("([^0-9])", " ");
    Time = Time.replaceAll("\\s{1,}", " ");
    Vector<Integer> spaceLocations = getPositionofSpace(Time);
    Time = Time.replaceAll("\\s", "");

    boolean isStandard = false;
    for (int[] split : splits) {
      if (split[3] > Time.length()) {
        continue;
      }
      if (!comparePositionofSpace(spaceLocations, split)) {
        continue;
      }
      String[] ymdString = {
        Time.substring(split[0], split[1]),
        Time.substring(split[1], split[2]),
        Time.substring(split[2], split[3])
      };
      if (checkStandardTime(ymdString)) {
        StandardTimeSeries = ymdString;
        isStandard = true;
        break;
      }
    }
    if (!isStandard) {
      throw new NoSuchAlgorithmException();
    }
    return StandardTimeSeries;
  }

  /**
   * Blur the input time using default level "year".
   *
   * @param input time to be blurred
   * @return blurred time
   * @throws NoSuchAlgorithmException when the input time string cannot be converted into standard
   *     time format
   */
  public String blur(String input) throws NoSuchAlgorithmException {
    String StandardTime = "";
    try {
      StandardTime = blur(input, "year");
    } catch (NoSuchAlgorithmException e) {
      logger.error("We don't have BlurTime algo block for " + input);
      e.printStackTrace();
    }
    return StandardTime;
  }

  /**
   * Blur the input time with explicitly specified level.
   *
   * @param input time to be blurred
   * @param level which could be "year", "month", "day"
   * @return blurred time
   * @throws NoSuchAlgorithmException when the input time string cannot be converted into standard
   *     time format
   */
  public String blur(String input, String level) throws NoSuchAlgorithmException {
    String StandardTime = "";
    try {
      int count = 0;
      switch (level) {
        case "year":
          count = 1;
          break;
        case "month":
          count = 2;
          break;
        case "day":
          count = 3;
          break;
        default:
          logger.error("Unknown blur level " + level);
          System.exit(-1);
      }
      String[] StandardTimeSeries = getStandardTime(input);
      StringBuilder StandardTimeBuffer = new StringBuilder();
      for (int k = 0; k < count; k++) {
        String TimeElement = StandardTimeSeries[k];
        if (Objects.equals(TimeElement, "") || TimeElement == null) {
          continue;
        }
        int Time = Integer.parseInt(TimeElement);
        if (k == 0) {
          Time = (Time >= 1900) ? Time : Time + 1900;
          Time = (Time >= 1949) ? Time : Time + 100;
        }
        StandardTimeBuffer.append(Time);
        StandardTimeBuffer.append(linkWord[k]);
      }
      StandardTime = StandardTimeBuffer.toString();
    } catch (NoSuchAlgorithmException e) {
      logger.error("We don't have BlurTime algo block for [" + input + ", " + level + "]");
      e.printStackTrace();
    } catch (IllegalArgumentException e) {
      logger.error("The value of level should be chosen in year/month/day.");
      e.printStackTrace();
    }
    return StandardTime;
  }
}
