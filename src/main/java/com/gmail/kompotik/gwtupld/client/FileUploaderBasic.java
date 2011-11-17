package com.gmail.kompotik.gwtupld.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gmail.kompotik.gwtupld.client.file.File;
import com.gmail.kompotik.gwtupld.client.file.FileList;
import com.gmail.kompotik.gwtupld.client.i18n.GwtupldMessages;
import com.gmail.kompotik.gwtupld.client.utils.UUID;
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
import com.google.gwt.event.dom.client.DragEnterEvent;
import com.google.gwt.event.dom.client.DragEnterHandler;
import com.google.gwt.event.dom.client.DragLeaveEvent;
import com.google.gwt.event.dom.client.DragLeaveHandler;
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
  private final GwtupldMessages messages;

  @UiField MyCss style;
  interface MyCss extends CssResource {
    String progressBar();
    String dropzone();
    String dropzoneHover();
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
    messages = (GwtupldMessages) GWT.create(GwtupldMessages.class);
    dropZoneAndButtonContainer.setInnerText(
        options.useAdvancedUploader() ?
        messages.welcomeXhr() :
        messages.welcomeIframe()
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
    if (options.useAdvancedUploader()) {
      addDragAndDropHandlers();
    }
  }

  public void updateView(List<FileInfo> files) {
    for (FileInfo file : files) {
      final String uuid = UUID.uuid();
      fileInfos.put(uuid, file);
      updateExactFileInfo(uuid);
    }
  }

  /**
   * Used to get control state.
   *
   * @return FileInfos by id
   */
  public Map<String, FileInfo> getFileInfos() {
    return fileInfos;
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
        if (el == fileInput) {
          DataTransferAdvanced dta = (DataTransferAdvanced) event.getDataTransfer();
          uploadFileList(dta.getFiles());
          dropZoneAndButtonContainer.removeClassName(style.dropzoneHover());
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
    addDomHandler(new DragEnterHandler() {
      @Override
      public void onDragEnter(DragEnterEvent event) {
        final EventTarget eventTarget = event.getNativeEvent().getEventTarget();
        Element el = Element.as(eventTarget);
        if (el == fileInput) {
          dropZoneAndButtonContainer.addClassName(style.dropzoneHover());
        }
      }
    }, DragEnterEvent.getType());
    addDomHandler(new DragLeaveHandler() {
      @Override
      public void onDragLeave(DragLeaveEvent event) {
        final EventTarget eventTarget = event.getNativeEvent().getEventTarget();
        Element el = Element.as(eventTarget);
        if (el == fileInput) {
          dropZoneAndButtonContainer.removeClassName(style.dropzoneHover());
        }
      }
    }, DragLeaveEvent.getType());
  }

  private UploadHandlerAbstract createUploadHandler() {
    final UploadHandlerAbstract uploadHandler;
    if (options.useAdvancedUploader()) {
      uploadHandler = new UploadHandlerXhr(this, options);
    } else {
      uploadHandler = new UploadHandlerForm(this, options);
    }
    return uploadHandler;
  }

  private native static boolean preventLeaveInProgress() /*-{
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
      if (validateFile(file)) {
        uploadFile(file);
      }
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
    String name = input.getValue().replaceAll(".*(\\/|\\\\)", "");
    int size = -1;
    return validate(name, size);
  }

  private boolean validateFile(File file) {
    String name = file.getName();
    int size = file.getSize();
    return validate(name, size);
  }

  private boolean validate(String name, int size) {
    final String error = validateFile(name, size);
    if (error != null) {
      addFileInfo(UUID.uuid(), name, size, error);
    }
    return error == null;
  }

  /**
   * Checks file for being valid for upload
   * @param name file name
   * @param size file size
   * @return Error cause or null if OK
   */
  private String validateFile(String name, int size) {
    if (!isAllowedExtension(name)) {
      return messages.extensionNotAllowed(
          options.getAllowedExtensions().toString()
      );
    }
    if (size == 0) {
      return messages.fileSizeIsZero();
    }
    if (size > 0 && options.getMinSize() > 0 && size < options.getMinSize()) {
      return messages.fileIsTooSmall(formatSize(options.getMinSize()));
    }
    if (size > 0 && options.getMaxSize() > 0 && size > options.getMaxSize()) {
      return messages.fileIsTooLarge(formatSize(options.getMaxSize()));
    }
    return null;
  }
  
  private boolean isAllowedExtension(String name) {
    if (options.getAllowedExtensions() == null
        || options.getAllowedExtensions().size() == 0) {
      return true;
    }
    if (name == null || name.lastIndexOf('.') == -1) {
      return false;
    }
    String ext = name.substring(name.lastIndexOf('.') + 1).toLowerCase();
    return options.getAllowedExtensions().contains(ext);
  }

  @Override
  public void onProgress(String id, String filename, int loaded, int total) {
    fileInfos.put(id, new FileInfo(id, null, filename, total, loaded, null, null));
    updateExactFileInfo(id);
  }

  @Override
  public void onComplete(String id, String filename, JSONValue response) {
    if (response.isArray() != null) {
      JSONArray array = (JSONArray) response;
      for (int i = 0; i < array.size(); i++) {
        final JSONObject value = (JSONObject) array.get(i);
        final JSONValue size = value.get("size");
        final JSONValue url = value.get("url");
        final JSONValue type = value.get("type");
        final String url2 = String.valueOf(url);
        fileInfos.put(id, new FileInfo(
            id,
            // TODO: is there a good way to remove quotes?
            url2.substring(1, url2.length() - 1),
            filename,
            Integer.valueOf(String.valueOf(size)),
            Integer.valueOf(String.valueOf(size)),
            String.valueOf(type),
            null));
        updateExactFileInfo(id);
      }
    }
  }

  @Override
  public void onCancel(String id, String filename) {
    // TODO mark item as cancelled in the list and delete it from server
  }

  // original comment said 'return false to cancel submit'?! wtf
  @Override
  public Long onSubmit(String id, String filename) {
    filesInProgress++;
    addFileInfo(id, filename, -1, null);
    return filesInProgress;
  }

  private void addFileInfo(String id, String filename, int size, String error) {
    fileInfos.put(id, new FileInfo(id, null, filename, size, -1, null, error));
    updateExactFileInfo(id);
  }

  /**
   * Updates resulting table
   * @param justAddedId id is required in order not to update the whole table,
   *                    but just the exact row
   */
  private void updateExactFileInfo(String justAddedId) {
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

  private String formatSize(int bytes) {
    if (bytes == -1) {
      return "";
    }
    final int kbSize = 1024;
    final int mbSize = kbSize * kbSize;

    boolean mb = false;
    int divideBy = kbSize;
    if (bytes > mbSize) {
      mb = true;
      divideBy = mbSize;
    }
    final String s = String.valueOf((double) bytes / divideBy);
    int endIndex = s.indexOf('.') + 2;
    if (endIndex > s.length()) {
      endIndex = s.length() - 1;
    }
    return s.substring(0, endIndex) + " " + (mb ?
        messages.sizeMB() : messages.sizeKB());
  }
}