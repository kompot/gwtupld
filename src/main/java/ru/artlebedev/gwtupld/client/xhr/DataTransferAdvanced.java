package ru.artlebedev.gwtupld.client.xhr;

import ru.artlebedev.gwtupld.client.file.FileList;
import ru.artlebedev.gwtupld.client.file.impl.FileListImpl;

import com.google.gwt.dom.client.DataTransfer;

public class DataTransferAdvanced extends DataTransfer {
  protected DataTransferAdvanced() {
  }

  public final FileList getFiles() {
    return new FileList(getFileList());
  }

  private native FileListImpl getFileList() /*-{
    return this.files;
  }-*/;
  
  public final native String getEffectAllowed() /*-{
    return this.effectAllowed;
  }-*/;

  public final native void setDropEffect(String effect) /*-{
    this.dropEffect = effect;
  }-*/;
}
