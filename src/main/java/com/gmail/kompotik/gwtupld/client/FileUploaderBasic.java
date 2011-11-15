package com.gmail.kompotik.gwtupld.client;

import java.util.HashMap;
import java.util.Map;

import com.gmail.kompotik.gwtupld.client.file.File;
import com.gmail.kompotik.gwtupld.client.file.FileList;
import com.gmail.kompotik.gwtupld.client.i18n.GwtupldConstants;
import com.gmail.kompotik.gwtupld.client.xhr.DataTransferAdvanced;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.dom.client.TableElement;
import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DragOverEvent;
import com.google.gwt.event.dom.client.DragOverHandler;
import com.google.gwt.event.dom.client.DropEvent;
import com.google.gwt.event.dom.client.DropHandler;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;

public class FileUploaderBasic extends Widget implements UploadProgressHandlers {
  interface Binder extends UiBinder<DivElement, FileUploaderBasic> {}
  private static Binder binder = GWT.create(Binder.class);
  private final GwtupldConstants constants;

  @UiField MyCss style;
  interface MyCss extends CssResource {
    String progressBar();
    String dropzone();
    String data();
  }

  @UiField TableElement table;
  @UiField DivElement dropZoneAndButtonContainer;
  private UploadButton uploadButton;
  private InputElement fileInput;

  private UploadHandlerAbstract uploadHandler;
  private Options options;
  private Long filesInProgress = 0L;
  private Map<String, FileInfo> fileInfos;

  public FileUploaderBasic(Options options) {
    setElement(binder.createAndBindUi(this));
    this.options = options;
    uploadHandler = createUploadHandler();
    fileInfos = new HashMap<String, FileInfo>();
    constants = (GwtupldConstants) GWT.create(GwtupldConstants.class);
    dropZoneAndButtonContainer.setInnerText(
        options.useAdvancedUploader() ?
        constants.welcomeXhr() :
        constants.welcomeIframe()
    );
    fileInput = createUploadButton(dropZoneAndButtonContainer);
    // TODO: what is this for?
    // preventLeaveInProgress();
    addDomHandler(new ChangeHandler() {
      @Override
      public void onChange(ChangeEvent event) {
        final EventTarget et = event.getNativeEvent().getEventTarget();
        final Element el = Element.as(et);
        if (el == fileInput) {
          onInputChange((FileInputElement) fileInput);
        }
      }
    }, ChangeEvent.getType());
    addDragAndDropHandlers();
  }

  private FileInputElement createUploadButton(DivElement container) {
    if (uploadButton != null) {
      uploadButton.reset();
    }
    uploadButton = new UploadButton(
        container,
        options.isMultiple() && options.useAdvancedUploader()
    );
    return uploadButton.getInput();
  }

  private void addDragAndDropHandlers() {
    addDomHandler(new DropHandler() {
      @Override
      public void onDrop(DropEvent event) {
        final EventTarget eventTarget = event.getNativeEvent().getEventTarget();
        Element el = Element.as(eventTarget);
        if (el == dropZoneAndButtonContainer || el == fileInput) {
          DataTransferAdvanced dta = (DataTransferAdvanced) event.getDataTransfer();
          uploadFileList(dta.getFiles());
        }
        event.preventDefault();
      }
    }, DropEvent.getType());
    addDomHandler(new DragOverHandler() {
      @Override
      public void onDragOver(DragOverEvent event) {
        DataTransferAdvanced dta = (DataTransferAdvanced) event.getDataTransfer();
        String effect = dta.getEffectAllowed();
        if (effect.equals("move") || effect.equals("linkmove")) {
          // for FF (only move allowed)
          dta.setDropEffect("move");
        } else {
          // for Chrome
          dta.setDropEffect("copy");
        }
        event.stopPropagation();
        event.preventDefault();
      }
    }, DragOverEvent.getType());
  }

  private UploadHandlerAbstract createUploadHandler() {
    final UploadHandlerAbstract uploadHandler;
    if (options.useAdvancedUploader()) {
      uploadHandler = new UploadHandlerXhr(this);
    } else {
      uploadHandler = new UploadHandlerForm(this);
    }
    return uploadHandler;
  }

  public native static boolean preventLeaveInProgress() /*-{
      var self = this;

      qq.attach(window, 'beforeunload', function(e){
          if (!self._filesInProgress){return;}

          var e = e || window.event;
          // for ie, ff
          e.returnValue = self._options.messages.onLeave;
          // for webkit
          return self._options.messages.onLeave;
      });
  }-*/;

  private void onInputChange(FileInputElement input) {
    if (uploadHandler instanceof UploadHandlerXhr) {
      uploadFileList(input.getFiles());
    } else {
      if (validateFile(input)) {
        uploadFile(input);
      }
    }
    fileInput = createUploadButton(dropZoneAndButtonContainer);
  }

  private void uploadFileList(FileList files) {
    for (File file : files) {
      if (!validateFile(file)) {
        return;
      }
    }
    for (File file : files) {
      uploadFile(file);
    }
  }

  private void uploadFile(Object file) {
    String id = uploadHandler.add(file);
    String fileName = uploadHandler.getName(id);

//    if (onSubmit(id, fileName) > 0) {
    onSubmit(id, fileName);
    uploadHandler.upload(id, options.getParams());
//    }
  }

  private boolean validateFile(FileInputElement input) {
    // TODO implement
    return true;
  }

  // TODO implement
  private boolean validateFile(File file) {
    return true;
    /**

             var name, size;

        if (file.value){
            // it is a file input
            // get input value and remove path to normalize
            name = file.value.replace(/.*(\/|\\)/, "");
        } else {
            // fix missing properties in Safari
            name = file.fileName != null ? file.fileName : file.name;
            size = file.fileSize != null ? file.fileSize : file.size;
        }

        if (! this._isAllowedExtension(name)){
            this._error('typeError', name);
            return false;

        } else if (size === 0){
            this._error('emptyError', name);
            return false;

        } else if (size && this._options.sizeLimit && size > this._options.sizeLimit){
            this._error('sizeError', name);
            return false;

        } else if (size && size < this._options.minSizeLimit){
            this._error('minSizeError', name);
            return false;
        }

        return true;

     */
  }

  @Override
  public void onProgress(String id, String filename, int loaded, int total) {
    fileInfos.put(id, new FileInfo(id, filename, total, loaded, null));
    updateView(id);
  }

  @Override
  public void onComplete(String id, String filename, JSONValue response) {
    if (response.isArray() != null) {
      JSONArray array = (JSONArray) response;
      for (int i = 0; i < array.size(); i++) {
        final JSONObject value = (JSONObject) array.get(i);
        final JSONValue size = value.get("size");
        final JSONValue type = value.get("type");
        fileInfos.put(id, new FileInfo(
            id,
            filename,
            Integer.valueOf(String.valueOf(size)),
            Integer.valueOf(String.valueOf(size)),
            String.valueOf(type)
        ));
        updateView(id);
      }
    }
  }

  @Override
  public void onCancel(String id, String filename) {
    // TODO mark item as cancelled in the list
  }

  // original comment said 'return false to cancel submit'?! wtf
  @Override
  public Long onSubmit(String id, String filename) {
    filesInProgress++;
    fileInfos.put(id, new FileInfo(id, filename, -1, -1, null));
    updateView(id);
    return filesInProgress;
  }

  /**
   * Updates resulting table
   * @param justAddedId id is required in order not to update the whole table,
   *                    but just the exact row
   */
  private void updateView(String justAddedId) {
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
    
    fileNameCell.setInnerHTML(fileInfo.getName() + formatSize(fileInfo.getTotal()));
    updateProgressBar(fileInfo, progressCell);
  }

  private void insertCancelButton(final FileInfo fileInfo, TableRowElement row) {
    final TableCellElement cc = row.insertCell(row.getCells().getLength());
    cc.setInnerHTML(constants.cancel());
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
      div.setInnerText(constants.ready());
      div.getStyle().setWidth(100, Style.Unit.PCT);
    } else {
      div.setInnerText(constants.uploading());
      div.getStyle().setWidth(100, Style.Unit.PCT);
    }
  }

  private String formatSize(int bytes) {
    if (bytes == -1) {
      return "";
    }
    final String s = String.valueOf((double) bytes / 1024 / 1024);
    int endIndex = s.indexOf('.') + 2;
    if (endIndex > s.length()) {
      endIndex = s.length() - 1;
    }
    return ", " + s.substring(0, endIndex) + " " + constants.sizeMB();
  }
}