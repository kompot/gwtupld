package ru.artlebedev.gwtupld.client;

import java.util.HashMap;
import java.util.Map;

import ru.artlebedev.gwtupld.client.utils.UUID;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.FormElement;
import com.google.gwt.dom.client.FrameElement;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.RootPanel;

public class UploadHandlerForm extends UploadHandlerAbstract {
  private Map<String, InputElement> inputs;

  public UploadHandlerForm(UploadProgressHandlers progressHandlers, Options options) {
    super(progressHandlers, options);
    this.inputs = new HashMap<String, InputElement>();
  }

  @Override
  protected String add(Object o) {
    InputElement fileInput = (InputElement) o;
    // TODO: what is it for? why qqfile?
    fileInput.setAttribute("name", "qqfile");
    String id = "qq-upload-handler-iframe" + UUID.uuid();

    inputs.put(id, fileInput);

    fileInput.removeFromParent();
    return id;
  }

  @Override
  protected String getName(String id) {
    // get input value and remove path to normalize
    // replace all back slashes with forward slashes
    final String result = inputs.get(id).getValue().replaceAll("\\\\", "/");
    // get everything after last slash (it is file name)
    return result.substring(result.lastIndexOf('/') + 1);
  }

  @Override
  protected int getSize(String id) {
    return -1;
  }

  @Override
  protected String _upload(String id, Map<String, String> params) {
    InputElement inputElement = inputs.get(id);

    // probably file has just been dequeued
    if (inputElement == null) {
      return null;
    }

    final String fileName = getName(id);
    final Frame iframe = createFrame(id);
    final FormElement form = _createForm(iframe.getElement().getAttribute("name"), params);
    form.appendChild(inputElement);
    _attachLoadEvent(iframe, id, fileName);
    form.submit();
    log("submit");
    form.removeFromParent();
    return id;
  }

  private void _attachLoadEvent(final Frame iframe, final String id,
                                final String fileName) {
    iframe.addLoadHandler(new LoadHandler() {
      @Override
      public void onLoad(LoadEvent event) {
        final Element e = Element.as(event.getNativeEvent().getEventTarget());
        if (e == iframe.getElement()) {
          // when we remove iframe from dom
          // the request stops, but in IE load
          // event fires
          if (!iframe.getElement().hasParentElement()) {
            return;
          }
          // fixing Opera 10.53
          if (iframe.getElement().getOwnerDocument() != null
           && iframe.getElement().getOwnerDocument().getBody() != null
           && iframe.getElement().getOwnerDocument().getBody().getInnerHTML().equals("false")
              ) {
            // In Opera event is fired second time
            // when body.innerHTML changed from false
            // to server response approx. after 1 sec
            // when we upload file with iframe
            return;
          }
          onComplete(iframe, id, fileName);
        }
      }
    });
  }

  private void onComplete(final Frame iframe, String id, String fileName) {
    log("Iframe loaded; file with id `" + id + "` has been successfully uploaded");
    final JSONValue jsonValue = _getIframeContentJSON(iframe);
    if (jsonValue == null) {
      showError(messages.errorUploadingFile(fileName));
    } else {
      progressHandlers.onComplete(id, fileName, jsonValue);
    }
    inputs.remove(id);
    // timeout added to fix busy state in FF3.6
    Timer t = new Timer() {
      @Override
      public void run() {
        iframe.removeFromParent();
      }
    };
    t.schedule(1);
  }
  
  private JSONValue _getIframeContentJSON(Frame iframe) {
    final FrameElement frameElement = (FrameElement)iframe.getElement().cast();
    Document contentDocument = frameElement.getContentDocument();
    if (contentDocument == null) {
      // probably error on server has happened
      return null;
    }
    return JSONParser.parseStrict(
        contentDocument.getBody().getInnerText()
    );
  }

  @Override
  protected void _cancel(String id) {
    // TODO implement

    /**
     this._options.onCancel(id, this.getName(id));

     delete this._inputs[id];

     var iframe = document.getElementById(id);
     if (iframe){
         // to cancel request set src to something else
         // we use src="javascript:false;" because it doesn't
         // trigger ie6 prompt on https
         iframe.setAttribute('src', 'javascript:false;');

         qq.remove(iframe);
     }

     */
  }

  /**
   * Creates iframe with unique name that will be the form target and
   * will get server response on form post.
   *
   * @param id upload id
   * @return created frame
   */
  private Frame createFrame(String id) {
    // We can't use following code as the name attribute
    // won't be properly registered in IE6, and new window
    // on form submit will open
    // var iframe = document.createElement('iframe');
    // iframe.setAttribute('name', id);
    // TODO: test upload in IE6
    Frame frame = new Frame("javascript:false;");
    // src="javascript:false;" removes ie6 prompt on https
    // TODO: consider using NamedFrame GWT class as it already incapsulates
    // this fix
    frame.getElement().setAttribute("name", id);
    frame.getElement().setAttribute("id", id);
    frame.getElement().getStyle().setDisplay(Style.Display.NONE);
    RootPanel.get().add(frame);
    return frame;
  }

  /**
   * Creates form, that will be submitted to iframe
   */
  private FormElement _createForm(String iframeName, Map<String, String> params) {
    // We can't use the following code in IE6
    // var form = document.createElement('form');
    // form.setAttribute('method', 'post');
    // form.setAttribute('enctype', 'multipart/form-data');
    // Because in this case file won't be attached to request
    // TODO: test upload in IE6
    final FormElement form = Document.get().createFormElement();
    form.setMethod("post");
    form.setEnctype("multipart/form-data");
    form.setAction(appendParamsToAction(
        options.getAction(),
        options.getParams()
    ));
    form.setTarget(iframeName);
    form.getStyle().setDisplay(Style.Display.NONE);
    Document.get().getBody().appendChild(form);
    return form;
  }
}
