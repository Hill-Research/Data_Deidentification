package com.dupreytherapeutics.Algorithm.Blur;

import java.security.NoSuchAlgorithmException;

interface Blur {

  String blur(String input) throws NoSuchAlgorithmException;

  String blur(String input, String level) throws NoSuchAlgorithmException;
}
