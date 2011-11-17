package com.gmail.kompotik.gwtupld.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;

public class GwtupldEntryPoint implements EntryPoint {
  public void onModuleLoad() {
    final FileUploaderBasic fileUploaderBasic = new FileUploaderBasic(
        new Options(null, null, null, null, null,
            Arrays.asList("JPG", "png"), 3000, 1024 * 1024 * 2)
    );
    RootPanel.get().add(fileUploaderBasic);

    List<FileInfo> files = new ArrayList<FileInfo>();
    files.add(new FileInfo("complex-unique-id-identifying-file-unambiguously",
        "/gwtupld/files/rospil.png", "rospil.png", 6905, 6905, "image/png",
        null));
    fileUploaderBasic.updateView(files);
  }
}
