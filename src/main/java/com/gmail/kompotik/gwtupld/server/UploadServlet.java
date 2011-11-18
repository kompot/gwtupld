package com.gmail.kompotik.gwtupld.server;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

public class UploadServlet extends AbstractGwtupldServlet {
  private String urlPath;
  private String realPath;

  @Override
  public void init(ServletConfig config) throws ServletException {
    super.init(config);
    urlPath = "/gwtupld/files/";
    realPath = "/" + getServletContext().getRealPath("files") + "/";
  }

  @Override
  protected UploadedFile saveMultipartFile(FileItem item,
                                           HttpServletRequest req)
      throws Exception {
    String albumId = getAlbumId(req);

    final String filename = URLDecoder.decode(item.getName(), "UTF-8");
    File file = new File(realPath + albumId + filename);
    file.getParentFile().mkdirs();
    item.write(file);
    return new UploadedFile(
        filename,
        (int) item.getSize(),
        item.getContentType(),
        urlPath + albumId + filename
    );
  }

  @Override
  protected UploadedFile saveXhrFile(InputStream is, FileOutputStream fos,
                                     HttpServletRequest req)
      throws IOException {
    String albumId = getAlbumId(req);

    String filename = URLDecoder.decode(req.getHeader("X-File-Name"), "UTF-8");
    final File file = new File(realPath + albumId + filename);
    file.getParentFile().mkdirs();
    fos = new FileOutputStream(file);
    IOUtils.copy(is, fos);

    //      uploadedFile.type = item.getContentType();
    //      uploadedFile.url = file.getPath();

    return new UploadedFile(
        filename,
        (int) file.length(),
        null,
        urlPath + albumId + filename
    );
  }

  private String getAlbumId(HttpServletRequest req)
      throws UnsupportedEncodingException {
    String albumId = "";
    if (req.getParameter("albumId") != null) {
      // testing paramaters passed on submit
      albumId = URLDecoder.decode(req.getParameter("albumId"), "UTF-8") + "/";
    }
    return albumId;
  }
}
