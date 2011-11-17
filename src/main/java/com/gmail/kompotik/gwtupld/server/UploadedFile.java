package com.gmail.kompotik.gwtupld.server;

public class UploadedFile {
  private String name;
  private int size;
  private String type;
  private String url;

  public UploadedFile(String name, int size, String type, String url) {
    this.name = name;
    this.size = size;
    this.type = type;
    this.url = url;
  }

  public String getName() {
    return name;
  }

  public int getSize() {
    return size;
  }

  public String getType() {
    return type;
  }

  public String getUrl() {
    return url;
  }
}
