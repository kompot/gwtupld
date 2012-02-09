package ru.artlebedev.gwtupld.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RootPanel;

public class GwtupldEntryPoint implements EntryPoint {
  public void onModuleLoad() {
    Map<String, String> params = new HashMap<String, String>();
    params.put("albumId", "666");
    Options options = new Options(null, params, null, null, -1,
        Arrays.asList("JPG", "png", "wav", "mp3", "zip"), 3000,
        1024 * 1024 * 2000);
    final FileUploaderBasic fileUploaderBasic = new FileUploaderBasic(options);
    final FileUploaderList fileUploaderList = new FileUploaderList(options);

    HorizontalPanel hp = new HorizontalPanel();
    hp.setSpacing(100);
    hp.add(fileUploaderBasic);
    hp.add(fileUploaderList);
    RootPanel.get().add(hp);

    List<FileInfo> files = new ArrayList<FileInfo>();
    files.add(new FileInfo("complex-unique-id-identifying-file-unambiguously",
        "/gwtupld/files/rospil.png", "rospil.png", 6905, 6905, "image/png",
        null, true, null));
    fileUploaderBasic.updateView(files);
  }
}
