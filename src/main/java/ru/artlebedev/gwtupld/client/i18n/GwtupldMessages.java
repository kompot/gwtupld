package ru.artlebedev.gwtupld.client.i18n;

import com.google.gwt.i18n.client.Messages;

public interface GwtupldMessages extends Messages {
  // TODO: what are DefaultMessage annotations for - there is
  // set-property-fallback name="locale"
  // set in *.gwt.xml
  @DefaultMessage("")
  String welcomeXhr();
  @DefaultMessage("")
  String welcomeIframe();
  @DefaultMessage("")
  String sizeMB();
  @DefaultMessage("")
  String sizeKB();
  @DefaultMessage("")
  String cancel();
  @DefaultMessage("")
  String ready();
  @DefaultMessage("")
  String uploading();
  @DefaultMessage("{0}")
  String extensionNotAllowed(String allowedExtensions);
  @DefaultMessage("")
  String fileSizeIsZero();
  @DefaultMessage("{0}")
  String fileIsTooSmall(String minSize);
  @DefaultMessage("{0}")
  String fileIsTooLarge(String minSize);
  @DefaultMessage("{0}")
  String errorUploadingFile(String fileName);
}
