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

import com.dupreytherapeutics.Processor.Processor;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {

  static Processor processor;
  private static final Logger logger = LogManager.getLogger(Main.class);

  private static void callProcessor(final File file) throws IOException, NoSuchAlgorithmException {
    String fileName = file.getName();
    processor.process(fileName);
  }

  public static void main(String[] args)
      throws IOException, NoSuchAlgorithmException, URISyntaxException {
    logger.info("This is the data de-identification demo.\n");
    URL sampleURL = Main.class.getClassLoader().getResource("data/db");
    assert sampleURL != null;
    final File folder = new File(sampleURL.toURI());
    logger.info("db dir: " + folder.getName());
    File[] listOfFiles = folder.listFiles();
    if (listOfFiles == null) {
      logger.error("We could not find any data files in " + folder.getName());
      System.exit(-1);
    }
    processor = new Processor();
    for (File file : listOfFiles) {
      if (!file.isFile()) {
        logger.error("We found abnormal file " + file.getName());
        System.exit(-1);
      }
      logger.info("file: " + file.getName());
      callProcessor(file);
    }
  }
}
