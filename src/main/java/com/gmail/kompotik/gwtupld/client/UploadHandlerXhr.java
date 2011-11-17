package com.gmail.kompotik.gwtupld.client;

import java.util.HashMap;
import java.util.Map;

import com.gmail.kompotik.gwtupld.client.file.File;
import com.gmail.kompotik.gwtupld.client.utils.UUID;
import com.gmail.kompotik.gwtupld.client.xhr.OnUploadProgressEvent;
import com.gmail.kompotik.gwtupld.client.xhr.OnUploadProgressHandler;
import com.gmail.kompotik.gwtupld.client.xhr.XMLHttpRequestAdvanced;
import com.google.gwt.http.client.URL;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.xhr.client.ReadyStateChangeHandler;
import com.google.gwt.xhr.client.XMLHttpRequest;

/**
 * Class for uploading files using XHR
 */
public class UploadHandlerXhr extends UploadHandlerAbstract {
  private Map<String, File> files;
  private Map<String, XMLHttpRequest> xhrs;
  // current loaded size in bytes for each file
  private Map<String, Integer> loaded;

  public UploadHandlerXhr(UploadProgressHandlers handlers, Options options) {
    super(handlers, options);
    files = new HashMap<String, File>();
    xhrs = new HashMap<String, XMLHttpRequest>();
    loaded = new HashMap<String, Integer>();
  }

  @Override
  protected String add(Object o) {
    final String id = UUID.uuid();
    files.put(id, (File) o);
    return id;
  }

  @Override
  protected String getName(String id) {
    return files.get(id).getName();
  }

  @Override
  protected int getSize(String id) {
    return files.get(id).getSize();
  }

  /**
   * Sends the file identified by id and additional query params to the server
   * @param id file id
   * @param params params name-value string pairs
   */
  @Override
  protected String _upload(final String id, Map<String, String> params) {
    final File file = files.get(id);
    final String name = getName(id);
    final long size = getSize(id);
    
    loaded.put(id, 0);
    
    XMLHttpRequestAdvanced xhr = XMLHttpRequestAdvanced.create();
    xhrs.put(id, xhr);

    xhr.setOnUploadProgress(new OnUploadProgressHandler() {
      @Override
      public void onUploadProgress(OnUploadProgressEvent e) {
        if (e.isLengthComputable()) {
          loaded.put(id, e.getLoaded());
          UploadHandlerXhr.super.progressHandlers.onProgress(
              id, name, e.getLoaded(), e.getTotal()
          );
        }
      }
    });

    xhr.setOnReadyStateChange(new ReadyStateChangeHandler() {
      @Override
      public void onReadyStateChange(XMLHttpRequest xhr) {
        if (xhr.getReadyState() == XMLHttpRequest.DONE) {
          onComplete(id, xhr);
        }
      }
    });

    // TODO: build query string
    // params = params || {};
    // params['qqfile'] = name;
    // var queryString = qq.obj2url(params, this._options.action);
    String queryString = options.getAction();
    
    xhr.open("POST", queryString);
    xhr.setRequestHeader("X-Requested-With", "XMLHttpRequest");
    xhr.setRequestHeader("X-File-Name", URL.encodeQueryString(name));
    xhr.setRequestHeader("Content-Type", "application/octet-stream");
    xhr.send(file);
    log("File with id `" + id + "` was just sent to server");
    return null;
  }

  @Override
  protected void _cancel(String id) {
    progressHandlers.onCancel(id, files.get(id).getName());
    files.remove(id);
    final XMLHttpRequest xhr = xhrs.get(id);
    if (xhr != null) {
      xhr.abort();
      xhr.clearOnReadyStateChange();
      xhrs.remove(id);
    }
  }
  
  private void onComplete(String id, XMLHttpRequest xhr) {
    log("File with id `" + id + "` has been successfully uploaded");
    
    if (files.get(id) == null) {
      // the request was aborted/cancelled
      return;
    }
    
    String name = getName(id);
    int size = getSize(id);

    // TODO: what is it for within `onComplete`
    progressHandlers.onProgress(id, name, size, size);

    JSONValue response = new JSONString("");
    log("xhr - status is " + xhr.getStatus());
    log("xhr - response is " + xhr.getResponseText());
    if (xhr.getStatus() == 200) {
      response = JSONParser.parseStrict(xhr.getResponseText());
    }
    progressHandlers.onComplete(id, name, response);

    files.remove(id);
    xhr.clearOnReadyStateChange();
    xhrs.remove(id);
    _dequeue(id);
  }

  public native static boolean isSupported() /*-{
    var input = document.createElement('input');
    input.type = 'file';

    return (
        'multiple' in input &&
        typeof File != "undefined" &&
        typeof (new XMLHttpRequest()).upload != "undefined" );
  }-*/;
}
