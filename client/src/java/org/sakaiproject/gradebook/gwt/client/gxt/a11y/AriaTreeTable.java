package org.sakaiproject.gradebook.gwt.client.gxt.a11y;

import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.TreeTableEvent;
import com.extjs.gxt.ui.client.widget.treetable.TreeTable;
import com.extjs.gxt.ui.client.widget.treetable.TreeTableColumnModel;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Accessibility;
import com.google.gwt.user.client.ui.KeyboardListener;

public class AriaTreeTable extends TreeTable {

	private String summary;
	
	public AriaTreeTable(TreeTableColumnModel cm, String summary) {
		super(cm);
		this.summary = summary;
	}
	
	@Override
	protected void onRender(Element target, int index) {
		super.onRender(target, index);
		Accessibility.setRole(el().dom, "treegrid");
		getElement().setAttribute("summary", summary);
	}
	
	@Override
	public boolean fireEvent(int type, ComponentEvent ce) {
		switch (type) {
		case Event.ONKEYPRESS:
			switch (ce.event.getKeyCode()) {
			case KeyboardListener.KEY_ENTER:
				
				if (ce instanceof TreeTableEvent) {
					doSelectNode((TreeTableEvent)ce);
				}
				break;
			case KeyboardListener.KEY_ESCAPE:
				
				if (ce instanceof TreeTableEvent) {
					doUnselectNode((TreeTableEvent)ce);
				}
				break;
			}
			break;
		}
		
		return super.fireEvent(type, ce);
	}

	
	public void doSelectNode(TreeTableEvent tte) {
		
	}
	
	public void doUnselectNode(TreeTableEvent tte) {
		
	}
}
