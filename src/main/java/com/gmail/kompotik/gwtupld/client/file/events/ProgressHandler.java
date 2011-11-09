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

import org.vectomatic.client.file.events.ProgressEvent;

import com.google.gwt.event.shared.EventHandler;

/**
 * Handler interface for {@link org.vectomatic.client.file.events.ProgressEvent} events.
 */
public interface ProgressHandler extends EventHandler {
	/**
	 * Called when 'progress' is fired.
	 * @param event the {@link org.vectomatic.client.file.events.ProgressEvent} that was fired
	 */
	public void onProgress(ProgressEvent event);
}
