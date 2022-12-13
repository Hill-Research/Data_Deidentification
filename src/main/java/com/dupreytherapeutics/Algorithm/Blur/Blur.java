package com.dupreytherapeutics.Algorithm.Blur;

import java.security.NoSuchAlgorithmException;

/**
 * This interface specifies the API interfaces for Blurring algorithms. We could blur items (time,
 * number, etc.) at different levels.
 */
interface Blur {

  /**
   * Blur item using default level.
   *
   * @param input item to be blurred
   * @return blurred item
   * @throws NoSuchAlgorithmException when the corresponding blur block is not found.
   */
  String blur(String input) throws NoSuchAlgorithmException;

  /**
   * Blur item with explicitly specified level.
   *
   * @param input item to be blurred
   * @param level at which the item to be blurred.
   * @return blurred item
   * @throws NoSuchAlgorithmException when the corresponding blur block is not found.
   */
  String blur(String input, String level) throws NoSuchAlgorithmException;
}
