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
