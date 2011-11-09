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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.Gson;

public class UploadServlet extends HttpServlet {
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

  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    setAllowedHeaders(response);

    final PrintWriter writer = response.getWriter();
    if (request.getContentType().equals("application/octet-stream")) {
      List<UploadedFile> files = new ArrayList<UploadedFile>();
      saveOctetStream(request, response, files);
      writer.write(new Gson().toJson(files));
      writer.flush();
      writer.close();
      return;
    }

    if (!ServletFileUpload.isMultipartContent(request)) {
      throw new IllegalArgumentException("Request is not multipart," +
          "please 'multipart/form-data' enctype for your form.");
    }

    System.out.println("--- before uploading");
    ServletFileUpload uploadHandler = new ServletFileUpload(new DiskFileItemFactory());
    response.setContentType("text/plain");
    try {
      List<FileItem> items = uploadHandler.parseRequest(request);
      List<UploadedFile> files = new ArrayList<UploadedFile>();
      for (FileItem item : items) {
        if (!item.isFormField()) {
          File file = File.createTempFile(item.getName(), "");
          item.write(file);
          final UploadedFile uploadedFile = new UploadedFile();
          uploadedFile.name = item.getName();
          uploadedFile.size = item.getSize();
          uploadedFile.type = item.getContentType();
          uploadedFile.url = file.getAbsolutePath();
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
  protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    setAllowedHeaders(resp);
  }

  private void saveOctetStream(HttpServletRequest request, HttpServletResponse response, List<UploadedFile> files) {
    InputStream is = null;
    FileOutputStream fos = null;

    String filename = request.getHeader("X-File-Name");
    try {
      is = request.getInputStream();
      final File tempFile = File.createTempFile(filename, "");
      fos = new FileOutputStream(tempFile);
      IOUtils.copy(is, fos);
      final UploadedFile uploadedFile = new UploadedFile();
      uploadedFile.name = filename;
      uploadedFile.size = tempFile.length();
//      uploadedFile.type = item.getContentType();
//      uploadedFile.url = tempFile.getPath();
      files.add(uploadedFile);
      response.setStatus(HttpServletResponse.SC_OK);
    } catch (FileNotFoundException ex) {
      response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      log(UploadServlet.class.getName() + "has thrown an exception: " + ex.getMessage());
    } catch (IOException ex) {
      response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      log(UploadServlet.class.getName() + "has thrown an exception: " + ex.getMessage());
    } finally {
      IOUtils.closeQuietly(fos);
      IOUtils.closeQuietly(is);
    }
  }
}
