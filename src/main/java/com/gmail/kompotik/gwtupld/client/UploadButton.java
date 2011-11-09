package com.gmail.kompotik.gwtupld.client;

import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Style;

public class UploadButton {
  private FileInputElement input;
  private DivElement container;
  private boolean multiple;

  public UploadButton(DivElement container, boolean multiple) {
    this.container = container;
    this.multiple = multiple;
    container.getStyle().setPosition(Style.Position.RELATIVE);
    container.getStyle().setOverflow(Style.Overflow.HIDDEN);
    container.setDir("ltr");
    this.input = createInput();
  }

  /**
   * Cleans/recreates the file input
   */
  public void reset() {
    input.removeFromParent();
    // TODO: qq.removeClass(this._element, this._options.focusClass);
    input = createInput();
  }

  private FileInputElement createInput() {
    FileInputElement input = (FileInputElement) container.getOwnerDocument()
        .createFileInputElement();
    if (multiple) {
      input.setAttribute("multiple", "multiple");
    }

//    input.setName(this._options.name);

    input.getStyle().setPosition(Style.Position.ABSOLUTE);
    // in Opera only 'browse' button
    // is clickable and it is located at
    // the right side of the input
    input.getStyle().setRight(0, Style.Unit.PX);
    input.getStyle().setTop(0, Style.Unit.PX);
    // TODO: wtf is this?
    // 4 persons reported this, the max values that worked for them
    // were 243, 236, 236, 118
    input.getStyle().setFontSize(500, Style.Unit.PX);
    input.getStyle().setMargin(0, Style.Unit.PX);
    input.getStyle().setPadding(0, Style.Unit.PX);
    input.getStyle().setCursor(Style.Cursor.POINTER);
    input.getStyle().setOpacity(0);

    /**

     // TODO

     qq.attach(input, 'change', function(){
         self._options.onChange(input);
     });

     qq.attach(input, 'mouseover', function(){
         qq.addClass(self._element, self._options.hoverClass);
     });
     qq.attach(input, 'mouseout', function(){
         qq.removeClass(self._element, self._options.hoverClass);
     });
     qq.attach(input, 'focus', function(){
         qq.addClass(self._element, self._options.focusClass);
     });
     qq.attach(input, 'blur', function(){
         qq.removeClass(self._element, self._options.focusClass);
     });

     // IE and Opera, unfortunately have 2 tab stops on file input
     // which is unacceptable in our case, disable keyboard access
     if (window.attachEvent){
         // it is IE or Opera
         input.setAttribute('tabIndex', "-1");
     }

     */

    container.appendChild(input);

    return input;
  }

  public FileInputElement getInput() {
    return input;
  }
}
