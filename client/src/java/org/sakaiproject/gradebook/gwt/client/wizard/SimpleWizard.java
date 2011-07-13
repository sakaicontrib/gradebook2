package org.sakaiproject.gradebook.gwt.client.wizard;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Provider;

import org.sakaiproject.gradebook.gwt.client.AppConstants;
import org.sakaiproject.gradebook.gwt.client.api.Card;
import org.sakaiproject.gradebook.gwt.client.api.Wizard;
import org.sakaiproject.gradebook.gwt.client.gin.WidgetInjector;
import org.sakaiproject.gradebook.gwt.client.wizard.WizardWindow;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.ModalPanel;
import com.google.gwt.user.client.Element;



public class SimpleWizard extends WizardWindow implements Wizard  {


	private String modalCss;

	public SimpleWizard(List<Card> cards) {
		super(cards);
		
	}

	public SimpleWizard() {
		super((List<Card>)new ArrayList<Card>());
	}
	
	public Card newCard(String string) {
		WidgetInjector injector = Registry.get(AppConstants.WIDGET_INJECTOR);
		
		Provider<Card> cardProvider = injector.getWizardCardProvider();
		
		Card newCard = cardProvider.get();
		newCard.setTitle(string);
		
		getCards().add(newCard);
				
		return newCard;
	}

	public void setHeaderTitle(String hdrtitle) {
		super.setHeaderTitle(hdrtitle);
	}

	public void show() {
		super.show();
	}
	
	public void setClosable(boolean closable) {
		super.setClosable(closable);
	}

	public void setModal(boolean modal) {
		super.setModal(modal);
	}

	public void setSize(int width, int height) {
		super.setSize(width, height);
	}
	
	public void setResizable(boolean resizable) {
		super.setResizable(resizable);
	}

	public void onRender(Element parent, int pos) {
		super.onRender(parent, pos);
	}

	public String getHeaderTitle() {
		return super.getHeaderTitle();
	}

	public boolean isShowWestImageContainer() {
		return super.isShowWestImageContainer();
	}

	public void setShowWestImageContainer(boolean showWestImageContainer) {
		super.setShowWestImageContainer(showWestImageContainer);
	}

	public String getPanelBackgroundColor() {
		return super.getPanelBackgroundColor();
	}

	public void setPanelBackgroundColor(String panelBackgroundColor) {
		super.setPanelBackgroundColor(panelBackgroundColor);
	}

	public void setProgressIndicator(Indicator value) {
		super.setProgressIndicator(value);
	}

	public void setMainImg(String url) {
		super.setMainImg(url);
	}

	public String getPreviousButtonText() {
		return super.getPreviousButtonText();
	}

	public void setPreviousButtonText(String previousButtonText) {
		super.setPreviousButtonText(previousButtonText);
	}

	public String getNextButtonText() {
		return super.getNextButtonText();
	}

	public void setNextButtonText(String nextButtonText) {
		super.setNextButtonText(nextButtonText);
	}

	@Override
	public String getCancelButtonText() {
		return super.getCancelButtonText();
	}

	public void setCancelButtonText(String cancelButtonText) {
		super.setCancelButtonText(cancelButtonText);
	}

	@Override
	public String getFinishButtonText() {
		return super.getFinishButtonText();
	}

	public void setFinishButtonText(String finishButtonText) {
		super.setFinishButtonText(finishButtonText);
	}

	public String getIndicateStepText() {
		return super.getIndicateStepText();
	}

	public void setIndicateStepText(String indicateStepText) {
		super.setIndicateStepText(indicateStepText);
	}

	public String getIndicateOfText() {
		return super.getIndicateOfText();
	}

	public void setIndicateOfText(String indicateOfText) {
		super.setIndicateOfText(indicateOfText);
	}

	public void setStatusBarText(String statusBarText) {
		super.setStatusBarText(statusBarText);
	}

	public String getStatusBarText() {
		return super.getStatusBarText();
	}

	public boolean isHideOnFinish() {
		return super.isHideOnFinish();
	}

	public void setHideOnFinish(boolean hideOnFinish) {
		super.setHideOnFinish(hideOnFinish);
	}

	public List<Card> getCards() {
		return super.getCards();
	}

	public void setContainer(Element container) {
		super.setContainer(container);
	}
	
	public void setModalCssClassName(String css) {//TODO finish implementation
		modalCss = css;
	}

	public void addCancelListener(Listener<BaseEvent> listener) {
		super.addCancelListener(listener);
		
	}

	public void notifyCancelListeners() {
		super.notifyCancelListeners();
		
	}
	
	public void resize(int dw, int dh) {
		setSize(getWidth()+dw, getHeight()+dh);
	}

	

}
