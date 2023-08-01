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
