package com.gmail.kompotik.gwtupld.server;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.Gson;

public class UploadServlet extends HttpServlet {
  private String urlPath;
  private String realPath;

  @Override
  public void init(ServletConfig config) throws ServletException {
    super.init(config);
    urlPath = "/gwtupld/files/";
    realPath = "/" + getServletContext().getRealPath("files") + "/";
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    setAllowedHeaders(resp);

    resp.getWriter().write("please do `POST` request");
  }

  private void setAllowedHeaders(HttpServletResponse resp) {
    resp.setHeader("Access-Control-Allow-Origin", "*");
    resp.setHeader("Access-Control-Allow-Headers", "X-File-Name,X-File-Type," +
        "X-File-Size,X-Requested-With,Content-Type");
  }

  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    setAllowedHeaders(resp);

    final PrintWriter writer = resp.getWriter();
    if (req.getContentType().equals("application/octet-stream")) {
      List<UploadedFile> files = new ArrayList<UploadedFile>();
      saveOctetStream(req, resp, files);
      writer.write(new Gson().toJson(files));
      writer.flush();
      writer.close();
      return;
    }

    if (!ServletFileUpload.isMultipartContent(req)) {
      throw new IllegalArgumentException("Request is not multipart," +
          "please 'multipart/form-data' enctype for your form.");
    }

    System.out.println("--- before uploading");
    ServletFileUpload sfu = new ServletFileUpload(new DiskFileItemFactory());
    resp.setContentType("text/plain");
    try {
      List<FileItem> items = sfu.parseRequest(req);
      List<UploadedFile> files = new ArrayList<UploadedFile>();
      for (FileItem item : items) {
        if (!item.isFormField()) {
          final String filename = item.getName();
          File file = new File(realPath + filename);
          item.write(file);
          final UploadedFile uploadedFile = new UploadedFile(
              filename,
              (int) item.getSize(),
              item.getContentType(),
              urlPath + filename
          );
          files.add(uploadedFile);
        }
      }
      writer.write(new Gson().toJson(files));
    } catch (FileUploadException e) {
      throw new RuntimeException(e);
    } catch (Exception e) {
      throw new RuntimeException(e);
    } finally {
      writer.flush();
      writer.close();
    }
  }

  @Override
  protected void doOptions(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    setAllowedHeaders(resp);
  }

  private void saveOctetStream(HttpServletRequest request,
                               HttpServletResponse response,
                               List<UploadedFile> files) {
    InputStream is = null;
    FileOutputStream fos = null;

    String filename = request.getHeader("X-File-Name");
    try {
      is = request.getInputStream();
      final File file = new File(realPath + filename);
      file.getParentFile().mkdirs();
      fos = new FileOutputStream(file);
      IOUtils.copy(is, fos);
      final UploadedFile uploadedFile = new UploadedFile(
          filename,
          (int) file.length(),
          null,
          urlPath + filename
      );
//      uploadedFile.type = item.getContentType();
//      uploadedFile.url = file.getPath();
      files.add(uploadedFile);
      response.setStatus(HttpServletResponse.SC_OK);
    } catch (FileNotFoundException ex) {
      response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      System.out.println(UploadServlet.class.getName() + "has thrown an exception: " + ex.getMessage());
      log(UploadServlet.class.getName() + "has thrown an exception: " + ex.getMessage());
    } catch (IOException ex) {
      response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      System.out.println(UploadServlet.class.getName() + "has thrown an exception: " + ex.getMessage());
      log(UploadServlet.class.getName() + "has thrown an exception: " + ex.getMessage());
    } finally {
      IOUtils.closeQuietly(fos);
      IOUtils.closeQuietly(is);
    }
  }

  @Override
  protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    super.doDelete(req, resp);
  }
}
