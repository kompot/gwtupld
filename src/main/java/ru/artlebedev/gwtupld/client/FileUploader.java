package ru.artlebedev.gwtupld.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.artlebedev.gwtupld.client.events.UploadingCompletedEvent;
import ru.artlebedev.gwtupld.client.events.UploadingCompletedEventHandler;
import ru.artlebedev.gwtupld.client.file.File;
import ru.artlebedev.gwtupld.client.file.FileList;
import ru.artlebedev.gwtupld.client.i18n.GwtupldMessages;
import ru.artlebedev.gwtupld.client.utils.UUID;
import ru.artlebedev.gwtupld.client.xhr.DataTransferAdvanced;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.DragEnterEvent;
import com.google.gwt.event.dom.client.DragEnterHandler;
import com.google.gwt.event.dom.client.DragLeaveEvent;
import com.google.gwt.event.dom.client.DragLeaveHandler;
import com.google.gwt.event.dom.client.DragOverEvent;
import com.google.gwt.event.dom.client.DragOverHandler;
import com.google.gwt.event.dom.client.DropEvent;
import com.google.gwt.event.dom.client.DropHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.Widget;

public abstract class FileUploader extends Widget
    implements UploadProgressHandlers, HasHandlers {
  private HandlerManager handlerManager;
  private UploadButton uploadButton;
  private InputElement fileInput;
  private Options options;
  private int filesInProgress = 0;
  protected UploadHandlerAbstract uploadHandler;
  protected Map<String, FileInfo> fileInfos;
  protected final GwtupldMessages messages;

  protected abstract Element getDropZone();
  /**
   * Updates resulting file list when file is added to queue, finished uploading
   * or set via updateView.
   *
   * @param justAddedId id is required in order not to update the whole
   *                    container, but just the exact item
   */
  protected abstract void updateExactFileInfo(String justAddedId);
  protected abstract void onDropEvent();
  protected abstract void onDragLeaveEvent();
  protected abstract void onDragEnterEvent();
  public abstract void updateView(List<FileInfo> files);

  public FileUploader(Options options) {
    handlerManager = new HandlerManager(this);
    this.options = options;
    fileInfos = new HashMap<String, FileInfo>();
    messages = (GwtupldMessages) GWT.create(GwtupldMessages.class);

    uploadHandler = createUploadHandler();
  }

  @Override
  public void fireEvent(GwtEvent<?> event) {
    handlerManager.fireEvent(event);
    super.fireEvent(event);
  }

  public HandlerRegistration addUploadingCompletedEventHandler(
      UploadingCompletedEventHandler handler) {
    return handlerManager.addHandler(UploadingCompletedEvent.TYPE, handler);
  }

  /**
   * Must be called after Binder.createAndBindUi.
   * How to ensure that it was actually called from a subclass?
   */
  protected final void initialize() {
    getDropZone().setInnerText(
        options.useAdvancedUploader() ?
        messages.welcomeXhr() :
        messages.welcomeIframe()
    );
    fileInput = createUploadButton(getDropZone());
    // TODO: what is this for?
    // preventLeaveInProgress();
    addInputChangeHandlers();
    addDragAndDropHandlers();
  }

  public Options getOptions() {
    return options;
  }

  /**
   * Used to get control state.
   *
   * @return FileInfos by id
   */
  public Map<String, FileInfo> getFileInfos() {
    return fileInfos;
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

  private FileInputElement createUploadButton(Element container) {
    if (uploadButton != null) {
      uploadButton.reset();
    }
    uploadButton = new UploadButton(
        container,
        options.isMultiple() && options.useAdvancedUploader()
    );
    return uploadButton.getInput();
  }

  private void addInputChangeHandlers() {
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
  }

  private void onInputChange(FileInputElement input) {
    if (uploadHandler instanceof UploadHandlerXhr) {
      uploadFileList(input.getFiles());
    } else {
      if (validateFile(input)) {
        uploadFile(input);
      }
    }
    fileInput = createUploadButton(getDropZone());
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
      addFileToList(UUID.uuid(), name, size, error);
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

  private void addDragAndDropHandlers() {
    if (options.useAdvancedUploader()) {
      addDomHandler(new DropHandler() {
        @Override
        public void onDrop(DropEvent event) {
          final EventTarget eventTarget = event.getNativeEvent().getEventTarget();
          Element el = Element.as(eventTarget);
          if (el == fileInput) {
            DataTransferAdvanced dta = (DataTransferAdvanced) event.getDataTransfer();
            uploadFileList(dta.getFiles());
            onDropEvent();
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
            onDragEnterEvent();
          }
        }
      }, DragEnterEvent.getType());
      addDomHandler(new DragLeaveHandler() {
        @Override
        public void onDragLeave(DragLeaveEvent event) {
          final EventTarget eventTarget = event.getNativeEvent().getEventTarget();
          Element el = Element.as(eventTarget);
          if (el == fileInput) {
            onDragLeaveEvent();
          }
        }
      }, DragLeaveEvent.getType());
    }
  }

  protected String formatSize(int bytes) {
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

  @Override
  public void onComplete(String id, String filename, JSONValue response) {
    if (response.isArray() != null) {
      JSONArray array = (JSONArray) response;
      for (int i = 0; i < array.size(); i++) {
        filesInProgress--;
        if (filesInProgress == 0) {
          fireEvent(new UploadingCompletedEvent());
        }
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
            null,
            false, value));
        updateExactFileInfo(id);
      }
    }
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

  // original comment said 'return false to cancel submit'?! wtf
  @Override
  public int onSubmit(String id, String filename) {
    filesInProgress++;
    addFileToList(id, filename, -1, null);
    return filesInProgress;
  }

  private void addFileToList(String id, String filename, int size, String error) {
    fileInfos.put(id, new FileInfo(id, null, filename, size, -1, null, error, false, null));
    updateExactFileInfo(id);
  }

  @Override
  public void onProgress(String id, String filename, int loaded, int total) {
    fileInfos.put(id, new FileInfo(id, null, filename, total, loaded, null, null, false, null));
    updateExactFileInfo(id);
  }

  private void initAddFileInfo(FileInfo file) {
    final String uuid = UUID.uuid();
    fileInfos.put(uuid, file);
    updateExactFileInfo(uuid);
  }

  protected void initAddFileInfos(List<FileInfo> files) {
    if (files != null) {
      for (FileInfo file : files) {
        initAddFileInfo(file);
      }
    }
  }

  /**
   * Gets total number of files to be uploaded.
   * Excluding those ones having error.
   *
   * @return total number of files to be uploaded
   */
  protected int filesDueToUpload() {
    int result = 0;
    for (FileInfo fileInfo : fileInfos.values()) {
      if (fileInfo.dueToUpload()) {
        result++;
      }
    }
    return result;
  }

  /**
   * Gets total number of files already uploaded.
   *
   * @return total number of files already uploaded
   */
  protected int filesUploaded() {
    int result = 0;
    for (FileInfo fileInfo : fileInfos.values()) {
      if (fileInfo.dueToUpload() && fileInfo.uploadingHasFinished()) {
        result++;
      }
    }
    return result;
  }

  /**
   * Calculates total progress of all the files being uploaded
   * @return total progress of all the files being uploaded
   */
  protected int totalProgress() {
    int result = 0;
    for (FileInfo fileInfo : fileInfos.values()) {
      if (fileInfo.dueToUpload()) {
        result += fileInfo.getPercentageReady();
      }
    }
    return result / filesDueToUpload();
  }
}
