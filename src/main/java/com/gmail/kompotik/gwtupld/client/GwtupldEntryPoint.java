package com.gmail.kompotik.gwtupld.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;

public class GwtupldEntryPoint implements EntryPoint {
  public void onModuleLoad() {
    final Options options = new Options();
    options.setForceIframe(false);
    final FileUploaderBasic fileUploaderBasic = new FileUploaderBasic(options);
    RootPanel.get().add(fileUploaderBasic);

    List<FileInfo> files = new ArrayList<FileInfo>();
    files.add(new FileInfo("complex-unique-id-identifying-file-unambiguously",
        "/gwtupld/files/rospil.png", "rospil.png", 6905, 6905, "image/png"));
    fileUploaderBasic.updateView(files);
  }
}
