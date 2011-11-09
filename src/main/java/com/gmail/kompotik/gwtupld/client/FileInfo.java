package com.gmail.kompotik.gwtupld.client;

public class FileInfo {
  private String id;
  private String name;
  private int loaded;
  private int total;
  private String type;

  public FileInfo(String id, String filename, int total, int loaded,
                  String contentType) {
    this.id = id;
    this.name = filename;
    this.total = total;
    this.loaded = loaded;
    this.type = contentType;
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public int getLoaded() {
    return loaded;
  }

  public int getTotal() {
    return total;
  }

  public String getType() {
    return type;
  }

  public byte getPercentageReady() {
    if (loaded == -1 || total == -1) return -1;
    final Double v = (double) loaded / (double) total * 100;
    return v.byteValue();
  }
}
