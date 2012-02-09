package ru.artlebedev.gwtupld.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.Window;

public class Options {
  public final boolean debug;
  private final String action;
  private final Map<String, String> params;
  private final boolean multiple;
  private final int maxConnections;
  private final List<String> allowedExtensions;
  private final int maxSize;
  private final int minSize;
  private final boolean forceIframe;

  /**
   * @param action url to post form to; if null or empty debug mode is turned on
   */
  public Options(String action) {
    this(action, null, null, null, 0, null, null, null);
  }
  
  /**
   * @param action url to post form to; if null or empty debug mode is turned on
   * @param params parameters to pass along with request
   * @param forceIframe use iframe explicitly even if XHR is available
   * @param multiple should allow multiple file selection (ignored if
   *                 forceIframe is true)
   * @param maxConnections maxumum number of simultaneous file uploads
   * @param allowedExtensions extensions allowed to be uploaded
*                          case insensitive
   * @param minSize minimum file size allowed
   * @param maxSize maximum file size allowed
   */
  public Options(String action, Map<String, String> params, Boolean forceIframe,
                 Boolean multiple, int maxConnections,
                 List<String> allowedExtensions, Integer minSize,
                 Integer maxSize) {
    debug = action == null || action.isEmpty();
    this.action = !debug ? action :
        // this URL is for testing purposes only
        // see README file for reasons of introducing 8082 port
        "http://" + Window.Location.getHostName() + ":"
            + (Window.Location.getPort().equals("8081") ? "8080" : "8082")
            + "/gwtupld/upload-servlet/";
    this.params = params != null ? params : new HashMap<String, String>();
    this.multiple = multiple != null ? multiple : true;
    this.maxConnections = maxConnections > 0 ? maxConnections : 3;
    this.minSize = minSize != null ? minSize : -1;
    this.maxSize = maxSize != null ? maxSize : -1;
    this.forceIframe = forceIframe != null ? forceIframe : false;

    this.allowedExtensions = new ArrayList<String>();
    if (allowedExtensions != null) {
      for (String extension : allowedExtensions) {
        if (extension != null && !extension.isEmpty()) {
          this.allowedExtensions.add(extension.toLowerCase());
        }
      }
    }
  }

  public boolean useAdvancedUploader() {
    return UploadHandlerXhr.isSupported() && !isForceIframe();
  }

  public String getAction() {
    return action;
  }

  public Map<String, String> getParams() {
    return params;
  }

  public boolean isForceIframe() {
    return forceIframe;
  }

  public boolean isMultiple() {
    return multiple;
  }

  public int getMaxConnections() {
    return maxConnections;
  }

  public int getMaxSize() {
    return maxSize;
  }

  public int getMinSize() {
    return minSize;
  }

  public List<String> getAllowedExtensions() {
    return allowedExtensions;
  }
}
