package com.gmail.kompotik.gwtupld.client;

public class FileInfo {
  private String id;
  private String name;
  private int loaded;
  private int total;
  private String type;
  private String url;
  private String error;

  public FileInfo(String id, String url, String filename, int total, int loaded,
                  String type, String error) {
    this.id = id;
    this.name = filename;
    this.total = total;
    this.loaded = loaded;
    this.type = type;
    this.url = url;
    this.error = error;
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

  public String getUrl() {
    return url;
  }

  public String getError() {
    return error;
  }

  public byte getPercentageReady() {
    if (loaded == -1 || total == -1) return -1;
    final Double v = (double) loaded / (double) total * 100;
    return v.byteValue();
  }

  public boolean uploadingWasStartedAndHasNotFinished() {
    return getPercentageReady() > 0 && getPercentageReady() < 100;
  }

  public boolean uploadingHasFinished() {
    return getPercentageReady() == 100;
  }
}
