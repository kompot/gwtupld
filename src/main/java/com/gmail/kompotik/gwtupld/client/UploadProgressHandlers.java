package com.gmail.kompotik.gwtupld.client;

import com.google.gwt.json.client.JSONValue;

public interface UploadProgressHandlers {
  public void onProgress(String id, String filename, int loaded, int total);
  public void onComplete(String id, String filename, JSONValue response);
  public void onCancel(String id, String filename);
  public int onSubmit(String id, String filename);
}
