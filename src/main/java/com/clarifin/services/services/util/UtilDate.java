package com.clarifin.services.services.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UtilDate {

  public static Date convertDate(String dateImport) {
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    try {
      Date date = dateFormat.parse(dateImport);
      System.out.println("Fecha convertida: " + date);
      return date;
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return null;
  }

}
