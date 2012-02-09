package ru.artlebedev.gwtupld.client;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.dom.client.UListElement;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;

public class FileUploaderList extends FileUploader {
  interface Binder extends UiBinder<DivElement, FileUploaderList> {}
  private static Binder binder = GWT.create(Binder.class);

  @UiField MyCss style;
  interface MyCss extends CssResource {
    String dropzone();
    String dropzoneHover();
  }

  @UiField UListElement list;
  @UiField DivElement dropZoneAndButtonContainer;

  public FileUploaderList(Options options) {
    super(options);
    setElement(binder.createAndBindUi(this));
    initialize();
  }

  @Override
  protected Element getDropZone() {
    return dropZoneAndButtonContainer;
  }

  @Override
  public void updateView(List<FileInfo> files) {
    while (list.hasChildNodes()) {
      list.getChild(0).removeFromParent();
    }
    initAddFileInfos(files);
  }

  @Override
  protected void onDropEvent() {
    getDropZone().removeClassName(style.dropzoneHover());
  }

  @Override
  protected void onDragLeaveEvent() {
    getDropZone().removeClassName(style.dropzoneHover());
  }

  @Override
  protected void onDragEnterEvent() {
    getDropZone().addClassName(style.dropzoneHover());
  }

  @Override
  public void onCancel(String id, String filename) {
  }

  @Override
  protected void updateExactFileInfo(String justAddedId) {
    int index = -1;
    if (list.getChildCount() > 0) {
      for (int i = 0; i < list.getChildCount(); i++) {
        LIElement li = (LIElement) list.getChild(i);
        if (li.getAttribute("id").equals(justAddedId)) {
          index = i;
          break;
        }
      }
    }
    updateFileInfo(index, fileInfos.get(justAddedId));
  }

  private void updateFileInfo(int index, FileInfo fileInfo) {
    LIElement li;
    if (index != -1) {
      li = (LIElement) list.getChild(index);
    } else {
      li = Document.get().createLIElement();
      li.setAttribute("id", fileInfo.getId());
      list.appendChild(li);
    }
    li.setInnerText(
        fileInfo.getName()
        + ", " + fileInfo.getPercentageReady() + "%"
    );
  }
}
