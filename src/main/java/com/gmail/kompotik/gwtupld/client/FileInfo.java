package com.gmail.kompotik.gwtupld.client;

import com.google.gwt.json.client.JSONObject;

public class FileInfo {
  private String id;
  private String name;
  private int loaded;
  private int total;
  private String type;
  private String url;
  private String error;
  private boolean setOnLoad;
  private JSONObject value;

  /**
   *
   * @param id
   * @param url
   * @param filename
   * @param total
   * @param loaded
   * @param type
   * @param error
   * @param setOnLoad Used to indicate whether this instance should be used
*                  when calculating upload progress. Should be used when
   * @param value json value of a server response; by using this parameter it's
   *              easy to add extra properties to file infos (e. g. add width
   *              and height for images)
   */
  public FileInfo(String id, String url, String filename, int total, int loaded,
                  String type, String error, boolean setOnLoad, JSONObject value) {
    this.id = id;
    this.name = filename;
    this.total = total;
    this.loaded = loaded;
    this.type = type;
    this.url = url;
    this.error = error;
    this.setOnLoad = setOnLoad;
    this.value = value;
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

  public JSONObject getValue() {
    return value;
  }

  public boolean dueToUpload() {
    return !setOnLoad && (getError() == null || getError().isEmpty());
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
