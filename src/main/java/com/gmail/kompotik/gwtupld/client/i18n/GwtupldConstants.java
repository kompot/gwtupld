package com.gmail.kompotik.gwtupld.client.i18n;

import com.google.gwt.i18n.client.Constants;

public interface GwtupldConstants extends Constants {
  // TODO: why are DefaultStringValue required (we have
  // set-property-fallback set in Gwtupld.gwt.xml)
  // if it is not set there will be compile-time error

  @DefaultStringValue("")
  String welcomeXhr();

  @DefaultStringValue("")
  String welcomeIframe();

  @DefaultStringValue("")
  String sizeMB();

  @DefaultStringValue("")
  String sizeKB();

  @DefaultStringValue("")
  String cancel();

  @DefaultStringValue("")
  String ready();

  @DefaultStringValue("")
  String uploading();
}
