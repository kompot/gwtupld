package com.gmail.kompotik.gwtupld.client.i18n;

import com.google.gwt.i18n.client.Messages;

public interface GwtupldMessages extends Messages {
  String welcomeXhr();
  String welcomeIframe();
  String sizeMB();
  String sizeKB();
  String cancel();
  String ready();
  String uploading();
  String extensionNotAllowed(String allowedExtensions);
  String fileSizeIsZero();
  String fileIsTooSmall(String minSize);
  String fileIsTooLarge(String minSize);
}
