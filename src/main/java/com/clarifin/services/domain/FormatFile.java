package com.clarifin.services.domain;

import java.util.HashMap;
import lombok.Data;

@Data
public class FormatFile {

  private Long id;
  private String description;
  private String status;
  private Long startRow;
  private HashMap<String, Format> format;
}
