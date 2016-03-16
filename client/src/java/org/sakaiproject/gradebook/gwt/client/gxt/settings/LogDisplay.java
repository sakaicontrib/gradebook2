/**********************************************************************************
*
* $Id:$
*
***********************************************************************************
*
* Copyright (c) 2008, 2009 The Regents of the University of California
*
* Licensed under the
* Educational Community License, Version 2.0 (the "License"); you may
* not use this file except in compliance with the License. You may
* obtain a copy of the License at
* 
* http://www.osedu.org/licenses/ECL-2.0
* 
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an "AS IS"
* BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
* or implied. See the License for the specific language governing
* permissions and limitations under the License.
*
**********************************************************************************/
/*
 * This file is a close adaptation of the GXT class com.extjs.gxt.ui.client.widget.Info
 * licensed under the following license. We would like to simply extend Info instead of
 * copying it here, but this was not easily achievable due to the private member vars.
 * 
 * Ext GWT - Ext for GWT
 * Copyright(c) 2007, 2008, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package org.sakaiproject.gradebook.gwt.client.gxt.settings;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.sakaiproject.gradebook.gwt.client.gxt.model.GradeEventModel;
import org.sakaiproject.gradebook.gwt.client.model.GradeEvent;

import com.extjs.gxt.ui.client.event.IconButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.util.Format;
import com.extjs.gxt.ui.client.util.Params;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.ToolButton;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.RootPanel;

public class LogDisplay extends ContentPanel {

	private static Stack<LogDisplay> infoStack = new Stack<LogDisplay>();
	private static ArrayList<LogDisplay> slots = new ArrayList<LogDisplay>();


	/**
	 * Displays a message using the specified config.
	 * 
	 * @param config
	 *            the info config
	 */
	public static void display(int x, int y, LogConfig config) {
		pop().show(x, y, config);
	}

	/**
	 * Displays a message with the given title and text.
	 * 
	 * @param title
	 *            the title
	 * @param text
	 *            the text
	 */
	public static void display(int x, int y, String title, String text) {
		display(x, y, new LogConfig(title, text));
	}

	public static void display(int x, int y, String title, List<GradeEventModel> events) {
		display(x, y, new LogConfig(title, events, null));
	}
	
	/**
	 * Displays a message with the given title and text. All {0},{1}... values
	 * in text will be replaced with values.
	 * 
	 * @param title
	 *            the message title
	 * @param text
	 *            the message
	 * @param values
	 *            the values to be substituted
	 */
	public static void display(int x, int y, String title, String text, String... values) {
		display(x, y, new LogConfig(title, text, new Params((Object[]) values)));
	}

	/**
	 * Displays a message with the given title and text. The passed parameters
	 * will be applied to both the title and text before being displayed.
	 * 
	 * @param title
	 *            the info title
	 * @param text
	 *            the info text
	 * @param params
	 *            the paramters to be applied to the title and text
	 */
	public static void display(int x, int y, String title, String text, Params params) {
		LogConfig config = new LogConfig(title, text, params);
		display(x, y, config);
	}

	private static int firstAvail() {
		int size = slots.size();
		for (int i = 0; i < size; i++) {
			if (slots.get(i) == null) {
				return i;
			}
		}
		return size;
	}

	private static LogDisplay pop() {
		LogDisplay info = infoStack.size() > 0 ? (LogDisplay) infoStack.pop() : null;
		if (info == null) {
			info = new LogDisplay();
		}
		return info;
	}

	private static void push(LogDisplay info) {
		infoStack.push(info);
	}

	private LogConfig config;
	private int level;
	private SelectionListener<IconButtonEvent> selectionListener;
	
	/**
	 * Creates a new info instance.
	 */
	public LogDisplay() {
		baseStyle = "x-info";
		frame = true;
		setShadow(true);
		setLayoutOnChange(true);
		setLayout(new RowLayout());
		initListeners();
		getHeader().addTool(new ToolButton("x-tool-close", selectionListener));
	}

	public void hide() {
		super.hide();
		afterHide();
	}

	/**
	 * Displays the info.
	 * 
	 * @param config
	 *            the info config
	 */
	public void show(int x, int y, LogConfig config) {
		this.config = config;
		onShowInfo(x, y);
	}

	private void afterHide() {
		layer.hideShadow();
		RootPanel.get("mainapp").remove(this);
		slots.set(level, null);
		push(this);
	}

	private void afterShow() {
		Timer t = new Timer() {
			public void run() {
				afterHide();
			}
		};
		t.schedule(config.display);
	}

	private void initListeners() {
		selectionListener = new SelectionListener<IconButtonEvent>() {

			@Override
			public void componentSelected(IconButtonEvent ce) {
				afterHide();
			}
			
		};
	}
	
	private void onShowInfo(int x, int y) {
		RootPanel.get("mainapp").add(this);
		el().makePositionable(true);

		setTitle();
		
		if (config.events != null) {
			FlexTable flexTable = new FlexTable();
			int i = 0;
			for (GradeEvent event : config.events) {
				flexTable.setText(i, 0, event.getDateGraded());
				flexTable.setText(i, 1, "Grade set to " + event.getGrade() + " by " + event.getGraderName());
				i++;
			}
			add(flexTable);
		} else {
			setText();
		}
		
		level = firstAvail();
		slots.add(level, this);

		el().setLeftTop(x, y);
		setSize(config.width, config.height);

		if (!config.isPermanent)
			afterShow();
	}

	private void setText() {
		if (config.text != null) {
			if (config.params != null) {
				config.text = Format.substitute(config.text, config.params);
				if (config.text != null) {
					config.height = (config.text.length() / 75 + 1) * 30 + 20;
					
					if (config.height < 50)
						config.height = 50;
				}
			}
			removeAll();
			addText(config.text);
		}
	}

	private void setTitle() {
		if (config.title != null) {
			head.setVisible(true);
			if (config.params != null) {
				config.title = Format.substitute(config.title, config.params);
			}
			setHeading(config.title);
		} else {
			head.setVisible(false);
		}
	}
}
