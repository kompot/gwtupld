package com.gmail.kompotik.gwtupld.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;

public class GwtupldEntryPoint implements EntryPoint {
  public void onModuleLoad() {
    final Options options = new Options();
    options.setForceIframe(false);
    final FileUploaderBasic fileUploaderBasic = new FileUploaderBasic(options);
    RootPanel.get().add(fileUploaderBasic);
  }
}
