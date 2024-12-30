package com.clarifin.services.services.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class UtilString {

  public static String generateMd5(final String value) {
    try {
      // Crear una instancia de MessageDigest con el algoritmo MD5
      MessageDigest md = MessageDigest.getInstance("MD5");

      // Convertir la cadena en un array de bytes
      byte[] bytes = md.digest(value.getBytes());

      // Convertir los bytes a formato hexadecimal
      StringBuilder sb = new StringBuilder();
      for (byte b : bytes) {
        sb.append(String.format("%02x", b));
      }

      // Imprimir el hash MD5
      return sb.toString();
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
    return value;
  }

}
