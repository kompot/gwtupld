package com.gmail.kompotik.gwtupld.server;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.Gson;

public abstract class AbstractGwtupldServlet extends HttpServlet {
  protected abstract UploadedFile saveMultipartFile(FileItem item,
                                                    HttpServletRequest request)
      throws Exception;

  protected abstract UploadedFile saveXhrFile(InputStream is,
                                              OutputStream os,
                                              HttpServletRequest request)
      throws IOException;

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    setAllowedHeaders(resp);
    resp.setCharacterEncoding("UTF-8");
    resp.setHeader("Content-Type", "application/json");

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

    ServletFileUpload sfu = new ServletFileUpload(new DiskFileItemFactory());
    try {
      List<FileItem> items = sfu.parseRequest(req);
      List<UploadedFile> files = new ArrayList<UploadedFile>();
      for (FileItem item : items) {
        if (!item.isFormField()) {
          files.add(saveMultipartFile(item, req));
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

  private void saveOctetStream(HttpServletRequest request,
                               HttpServletResponse response,
                               List<UploadedFile> files) {
    InputStream is = null;
    FileOutputStream fos = null;

    try {
      is = request.getInputStream();
      files.add(saveXhrFile(is, fos, request));
      response.setStatus(HttpServletResponse.SC_OK);
    } catch (FileNotFoundException ex) {
      response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      System.out.println(AbstractGwtupldServlet.class.getName()
          + "has thrown an exception: " + ex.getMessage());
      log(AbstractGwtupldServlet.class.getName()
          + "has thrown an exception: " + ex.getMessage());
    } catch (IOException ex) {
      response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      System.out.println(AbstractGwtupldServlet.class.getName()
          + "has thrown an exception: " + ex.getMessage());
      log(AbstractGwtupldServlet.class.getName()
          + "has thrown an exception: " + ex.getMessage());
    } finally {
      IOUtils.closeQuietly(fos);
      IOUtils.closeQuietly(is);
    }
  }


  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    setAllowedHeaders(resp);

    resp.getWriter().write("`GET` is not supported");
  }

  @Override
  protected void doOptions(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    setAllowedHeaders(resp);
  }

  @Override
  protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    super.doDelete(req, resp);
  }

  private void setAllowedHeaders(HttpServletResponse resp) {
    resp.setHeader("Access-Control-Allow-Origin", "*");
    resp.setHeader("Access-Control-Allow-Headers", "X-File-Name,X-File-Type," +
        "X-File-Size,X-Requested-With,Content-Type");
  }
}
