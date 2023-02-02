package com.dupreytherapeutics.Algorithm.Mask;

import java.security.NoSuchAlgorithmException;

/**
 * This interface specifies the API interfaces for Masking algorithms. We could mask sensitive
 * information such as location from the input.
 */
interface Mask {

  /**
   * Mask key info from input string using default level.
   *
   * @param input input string with sensitive info
   * @return masked string
   * @throws NoSuchAlgorithmException if Mask block cannot handle the input string
   */
  public String mask(String input) throws NoSuchAlgorithmException;

  /**
   * Mask key info from input string using explicitly specified level.
   *
   * @param input input string with sensitive info
   * @param level at which the sensitive info should be masked
   * @return masked string
   * @throws NoSuchAlgorithmException if Mask block cannot handle the input string
   */
  public String mask(String input, String level) throws NoSuchAlgorithmException;
}
