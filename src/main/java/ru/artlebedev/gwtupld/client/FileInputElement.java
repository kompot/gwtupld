package ru.artlebedev.gwtupld.client;

import ru.artlebedev.gwtupld.client.file.FileList;
import ru.artlebedev.gwtupld.client.file.impl.FileListImpl;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.InputElement;

public class FileInputElement extends InputElement {
  protected FileInputElement() {
  }

  public final FileList getFiles() {
 		return new FileList(getFiles(this));
 	}

 	private native FileListImpl getFiles(Element element) /*-{
 	  return element.files;
 	}-*/;
}
