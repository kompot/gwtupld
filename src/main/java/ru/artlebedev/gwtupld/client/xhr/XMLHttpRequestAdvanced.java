package ru.artlebedev.gwtupld.client.xhr;

import ru.artlebedev.gwtupld.client.file.File;
import com.google.gwt.xhr.client.XMLHttpRequest;

public class XMLHttpRequestAdvanced extends XMLHttpRequest {
  protected XMLHttpRequestAdvanced() {
  }

  public static XMLHttpRequestAdvanced create() {
    return (XMLHttpRequestAdvanced)XMLHttpRequest.create();
  }

  public final native void send(File file) /*-{
    this.send(file);
  }-*/;

  public final native void setOnUploadProgress(OnUploadProgressHandler handler) /*-{
    var _this = this;
    this.upload.onprogress = $entry(function(e) {
      handler.@ru.artlebedev.gwtupld.client.xhr.OnUploadProgressHandler::onUploadProgress(Lru/artlebedev/gwtupld/client/xhr/OnUploadProgressEvent;)(e);
    });
  }-*/;
}
