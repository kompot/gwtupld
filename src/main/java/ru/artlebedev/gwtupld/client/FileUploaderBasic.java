package ru.artlebedev.gwtupld.client;

import java.util.List;

import ru.artlebedev.gwtupld.client.events.UploadingCompletedEvent;
import ru.artlebedev.gwtupld.client.events.UploadingCompletedEventHandler;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.dom.client.TableElement;
import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;

public class FileUploaderBasic extends FileUploader
    implements UploadingCompletedEventHandler {
  interface Binder extends UiBinder<DivElement, FileUploaderBasic> {}
  private static Binder binder = GWT.create(Binder.class);

  @UiField MyCss style;
  interface MyCss extends CssResource {
    String progressBar();
    String dropzone();
    String dropzoneHover();
    String data();
  }

  @UiField TableElement table;
  @UiField DivElement dropZoneAndButtonContainer;
  @UiField DivElement totalProgress;

  public FileUploaderBasic(Options options) {
    super(options);
    setElement(binder.createAndBindUi(this));
    initialize();
    addUploadingCompletedEventHandler(this);
  }

  @Override
  public void updateView(List<FileInfo> files) {
    fileInfos.clear();
    while (table.getRows().getLength() > 0) {
      table.getRows().getItem(0).removeFromParent();
    }
    initAddFileInfos(files);
  }

  @Override
  public void onCancel(String id, String filename) {
    // TODO mark item as cancelled in the list and delete it from server
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
  protected Element getDropZone() {
    return dropZoneAndButtonContainer;
  }

  @Override
  protected void updateExactFileInfo(String justAddedId) {
    if (filesDueToUpload() > 0) {
      totalProgress.setInnerHTML("Uploaded " +
          filesUploaded() + " of " + filesDueToUpload()
              + ", total progress is " + totalProgress() + "%"
      );
    }
    int index = -1;
    for (int i = 0; i < table.getRows().getLength(); i++) {
      final TableRowElement item = table.getRows().getItem(i);
      if (item.getCells() != null && item.getCells().getLength() > 0) {
        if (item.getCells().getItem(0).getInnerHTML().equals(justAddedId)) {
          index = i;
          break;
        }
      }
    }
    updateFileInfo(index, fileInfos.get(justAddedId));
  }

  private void updateFileInfo(int index, final FileInfo fileInfo) {
    int idx = table.getRows().getLength();
    final TableRowElement row;
    final TableCellElement fileNameCell;
    final TableCellElement progressCell;
    if (index != -1) {
      idx = index;
      row = table.getRows().getItem(idx);
      fileNameCell = row.getCells().getItem(1);
      progressCell = row.getCells().getItem(2);
      progressCell.setClassName(style.progressBar());
    } else {
      // create new row
      row = table.insertRow(idx);
      row.insertCell(0).getStyle().setDisplay(Style.Display.NONE);
      row.getCells().getItem(0).setInnerHTML(fileInfo.getId());
      fileNameCell = row.insertCell(1);
      // progress bar cell
      row.insertCell(2);
      // TODO: cancelling works for XHR uploads, to be tested for iframe
//      insertCancelButton(fileInfo, row);
      progressCell = row.getCells().getItem(2);
      final DivElement progressBar = Document.get().createDivElement();
      progressCell.appendChild(progressBar);
    }

    String fileinfo = fileInfo.getName();
    if (fileInfo.getUrl() != null && !fileInfo.getUrl().isEmpty()) {
      fileinfo = "<a target=\"_blank\" href=\"" + fileInfo.getUrl() + "\">"
          + fileInfo.getName()
          + "</a>";
    }
    String size = formatSize(fileInfo.getTotal());
    if (!size.isEmpty()) {
      size = ", " + size;
    }
    fileNameCell.setInnerHTML(fileinfo + size);
    updateProgressBar(fileInfo, progressCell);
  }

  private void insertCancelButton(final FileInfo fileInfo, TableRowElement row) {
    final TableCellElement cc = row.insertCell(row.getCells().getLength());
    cc.setInnerHTML(messages.cancel());
    cc.getStyle().setTextDecoration(Style.TextDecoration.UNDERLINE);
    cc.getStyle().setCursor(Style.Cursor.POINTER);
    addDomHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        final EventTarget et = event.getNativeEvent().getEventTarget();
        final Element el = Element.as(et);
        if (el == cc) {
          uploadHandler._cancel(fileInfo.getId());
        }
      }
    }, ClickEvent.getType());
  }

  private void updateProgressBar(FileInfo fileInfo, TableCellElement cell) {
    final DivElement div = (DivElement) cell.getElementsByTagName("div").getItem(0);
    if (fileInfo.uploadingWasStartedAndHasNotFinished()) {
      div.setInnerText(String.valueOf(fileInfo.getPercentageReady()) + "%");
      div.getStyle().setWidth(fileInfo.getPercentageReady(), Style.Unit.PCT);
    } else if (fileInfo.uploadingHasFinished()) {
      // this will skip inserting messages.ready()
      // when doing initial updateView
      if (!div.getInnerText().isEmpty()) {
        div.setInnerText(messages.ready());
        div.getStyle().setWidth(100, Style.Unit.PCT);
      }
    } else {
      div.setInnerText(
          fileInfo.getError() == null
              ? messages.uploading()
              : fileInfo.getError()
      );
      div.getStyle().setWidth(100, Style.Unit.PCT);
    }
  }

  @Override
  public void onUploadingCompleted(UploadingCompletedEvent event) {
    GWT.log("uploading completed");
  }
}