/**********************************************
 * Copyright (C) 2011 Lukas laag
 * This file is part of lib-gwt-file.
 * 
 * lib-gwt-file is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * lib-gwt-file is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with lib-gwt-file.  If not, see http://www.gnu.org/licenses/
 **********************************************/
package com.gmail.kompotik.gwtupld.client.file.events;

import com.google.gwt.event.dom.client.DomEvent;

/**
 * The progress event is triggered while reading (and decoding) blob, 
 * and reporting partial Blob data (progess.loaded/progress.total) 
 */
public class ProgressEvent extends ProgressEventBase<ProgressHandler> {
	private static final Type<ProgressHandler> TYPE = new Type<ProgressHandler>(
			"progress", new ProgressEvent());

	/**
	 * Protected constructor, use
	 * {@link DomEvent#fireNativeEvent(com.google.gwt.dom.client.NativeEvent, com.google.gwt.event.shared.HasHandlers)}
	 * to fire mouse out events.
	 */
	protected ProgressEvent() {
	}

	/**
	 * @return the handler type
	 */
	public Type<ProgressHandler> getAssociatedType() {
		return TYPE;
	}

	/**
	 * @return the handler type
	 */
	public static Type<ProgressHandler> getType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ProgressHandler handler) {
		handler.onProgress(this);
	}
}
