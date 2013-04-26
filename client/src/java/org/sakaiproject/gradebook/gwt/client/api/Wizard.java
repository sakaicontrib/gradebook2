package org.sakaiproject.gradebook.gwt.client.api;

import java.util.List;


import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.Layout;
import com.google.gwt.user.client.Element;

public interface Wizard {
	
	/**
	 * Indicator type enumeration.
	 */
	public enum Indicator {
		/** NONE */
		NONE,
		/** DOT */
		DOT,
		/** PROGRESSBAR */
		PROGRESSBAR
	}
	

	public Card newCard(String string);

	public void setHeading(String string);

	public void setHeaderTitle(String string);

	public void show();
	
	public void hide();

	public abstract void setResizable(boolean resizable);

	public abstract void setSize(int width, int height);

	public abstract void setModal(boolean modal);

	public abstract void setClosable(boolean closable);

	public abstract List<Card> getCards();

	public abstract void setHideOnFinish(boolean hideOnFinish);

	public abstract boolean isHideOnFinish();

	public abstract String getStatusBarText();

	public abstract void setStatusBarText(String statusBarText);

	public abstract void setIndicateOfText(String indicateOfText);

	public abstract String getIndicateOfText();

	public abstract void setIndicateStepText(String indicateStepText);

	public abstract String getIndicateStepText();

	public abstract void setFinishButtonText(String finishButtonText);

	public abstract String getFinishButtonText();

	public abstract void setCancelButtonText(String cancelButtonText);

	public abstract String getCancelButtonText();

	public abstract void setNextButtonText(String nextButtonText);

	public abstract String getNextButtonText();

	public abstract void setPreviousButtonText(String previousButtonText);

	public abstract String getPreviousButtonText();

	public abstract void setMainImg(String url);

	public abstract void setProgressIndicator(Indicator value);

	public abstract void setPanelBackgroundColor(String panelBackgroundColor);

	public abstract String getPanelBackgroundColor();

	public abstract void setShowWestImageContainer(boolean showWestImageContainer);

	public abstract boolean isShowWestImageContainer();

	public abstract String getHeaderTitle();

	public abstract void onRender(Element parent, int pos);

	public abstract void setContainer(Element container);

	public abstract void setModalCssClassName(String css);
	
	public abstract void addCancelListener(Listener<BaseEvent> listener);
		
	public void notifyCancelListeners();
	
	/*
	 * note that an implementation may require that this 
	 * method be called after show()
	 */
	public void resize(int dw, int dh);

	public void reset();
	
	public boolean isHidePreviousButtonOnFirstCard();

	public void setHidePreviousButtonOnFirstCard(boolean hidePreviousButtonOnFirstCard);
	
	public void setHideHeaderPanel(boolean isHidden);

	public abstract void setButtonAlignment(HorizontalAlignment buttonAlignment);

	public abstract HorizontalAlignment getButtonAlignment();
	
	public void pressPreviousButton();
	
	public void pressNextButton();
	
	public void pressCancelButton();
	
	public void hideCancelButton(boolean hide);
	
	public void setLayout(Layout layout);
	

	
	//GRBK-817
	
	public boolean isHideFinishButtonOnLastCard();
	
	public void setHideFinishButtonOnLastCard(boolean hide);
	
}
