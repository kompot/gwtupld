package com.gmail.kompotik.gwtupld.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.Window;

public class Options {
  public boolean debug = true;
  // this URL is for testing purposes only
  // see README file for reasons of introducing 8082 port
  private String action =
      "http://" + Window.Location.getHostName() + ":" +
      (Window.Location.getPort().equals("8081") ? "8080" : "8082")
      + "/gwtupld/";
  private Map<String, String> params = new HashMap<String, String>();
  private boolean multiple = true;
  private byte maxConnections = 3;
  // TODO: to be implemented
  private List<String> allowedExtensions = new ArrayList<String>();
  // TODO: to be implemented
  private long sizeLimit = 0;
  // TODO: to be implemented
  private long minSizeLimit = 0;
  // TODO: internationalization to be implemented
  private Map<String, String> messages = new HashMap<String, String>();
  private boolean forceIframe = false;

  public Options() {
//    messages.put("typeError", "{file} has invalid extension. Only {extensions} are allowed.");
//    messages.put("sizeError", "{file} is too large, maximum file size is {sizeLimit}.");
//    messages.put("minSizeError", "{file} is too small, minimum file size is {minSizeLimit}.");
//    messages.put("emptyError", "{file} is empty, please select files again without it.");
//    messages.put("onLeave", "The files are being uploaded, if you leave now the upload will be cancelled.");
  }

  public boolean useAdvancedUploader() {
    return UploadHandlerXhr.isSupported() && !isForceIframe();
  }

  public String getAction() {
    return action;
  }

  public void setAction(String action) {
    this.action = action;
  }

  public Map<String, String> getParams() {
    return params;
  }

  public void setParams(Map<String, String> params) {
    this.params = params;
  }

  public boolean isForceIframe() {
    return forceIframe;
  }

  /**
   * Forces using iframe even if XHR is available.
   *
   * @param forceIframe use iframe explicitly
   */
  public void setForceIframe(boolean forceIframe) {
    this.forceIframe = forceIframe;
  }

  public boolean isMultiple() {
    return multiple;
  }

  public void setMultiple(boolean multiple) {
    this.multiple = multiple;
  }

  public byte getMaxConnections() {
    return maxConnections;
  }

  public void setMaxConnections(byte maxConnections) {
    this.maxConnections = maxConnections;
  }
}
