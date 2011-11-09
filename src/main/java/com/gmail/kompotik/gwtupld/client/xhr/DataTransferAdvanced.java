package com.gmail.kompotik.gwtupld.client.xhr;

import com.gmail.kompotik.gwtupld.client.file.FileList;
import com.gmail.kompotik.gwtupld.client.file.impl.FileListImpl;

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
