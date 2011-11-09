package com.gmail.kompotik.gwtupld.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;

public abstract class UploadHandlerAbstract {
  protected Options options;
  private List<String> queue;
  protected Map<String, Map<String, String>> params;
  protected UploadProgressHandlers progressHandlers;

  /**
   * Adds file or file input to the queue
   *
   * @param o Object to add (InputElement or File)
   * @return id
   **/
  protected abstract String add(Object o);

  /**
   * Sends the file identified by id and additional query params to the server
   * @param id
   * @param params
   */
  public void upload(String id, Map<String, String> params) {
    this.queue.add(id);
    this.params.put(id, params);
    final int size = queue.size();
    if (size <= this.options.getMaxConnections()) {
      _upload(id, params);
    }
  }

  /**
   * Cancels file upload by id
   * @param id file id
   */
  public void cancel(String id) {
    _cancel(id);
    _dequeue(id);
  }

  public void cancelAll(String id) {
    for (String s : queue) {
      _cancel(s);
    }
  }

  /**
   * Returns name of the file identified by id
   *
   * @param id file id
   * @return file name
   */
  protected abstract String getName(String id);

  /**
   * Returns size of the file identified by id
   *
   * @param id file id
   * @return file size
   */
  protected abstract int getSize(String id);

  @Deprecated
  protected List<String> getQueue() {
    return queue;
  }

  /**
   * Actual upload method
   * @param id file id
   * @param params parameters to pass with request
   */
  protected abstract String _upload(String id, Map<String, String> params);

  /**
   * Actual cancel method
   * @param id file id
   */
  protected abstract void _cancel(String id);

  /**
   * Removes element from queue, starts upload of next
   */
  protected void _dequeue(String id) {
    final int i = queue.indexOf(id);
    queue.remove(i);

    final byte max = options.getMaxConnections();
    if (queue.size() >= max && i < max) {
      String nextId = queue.get(max - 1);
      this._upload(nextId, params.get(nextId));
    }
  }

  protected UploadHandlerAbstract(UploadProgressHandlers progressHandlers) {
    this.progressHandlers = progressHandlers;
    this.options = new Options();
    this.queue = new ArrayList<String>();
    this.params = new HashMap<String, Map<String, String>>();
  }

  protected void log(String s) {
    if (options.debug) {
      GWT.log("[uploader] " + s);
    }
  }
}