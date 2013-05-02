/*
 * Copyright 2008 Grant Slender <gslender@iinet.com.au>
 * 
 * Ideas/concepts borrowed from Thorsten Suckow-Homberg <ts@siteartwork.de>
 * http://www.siteartwork.de/wizardcomponent
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

/*
 * Significant modifications made by @author jpgorrono@ucdavis.edu
 * including:
 * 
 *  - modifications to work with 2.x
 *   -- such as, use of buttonbar and status objects
 *  - added cancel listeners
 *  - added method to add cards directly on wizard
 * 
 */
package org.sakaiproject.gradebook.gwt.client.wizard;

import java.util.ArrayList;
import java.util.List;

import org.sakaiproject.gradebook.gwt.client.AppConstants;
import org.sakaiproject.gradebook.gwt.client.I18nConstants;
import org.sakaiproject.gradebook.gwt.client.api.Card;
import org.sakaiproject.gradebook.gwt.client.api.Wizard.Indicator;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.ResizeEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.CardPanel;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.ProgressBar;
import com.extjs.gxt.ui.client.widget.Status;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ButtonBar;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Image;

/**
 * A wizard window intended to display wizard cards.</br></br>
 * 
 * // setup an array of WizardCards</br> ArrayList<WizardCard> cards = new
 * ArrayList<WizardCard>();</br> </br> // 1st card - a welcome</br> WizardCard
 * wc = new WizardCard("Welcome");</br> wc.setHtmlText(
 * "Welcome to the example for ext.ux.WizardWindow, "</br> +
 * "a ExtGWT user extension for creating wizards.<br/><br/>"</br> +
 * "Please click the \"next\"-button and fill out all form values.");</br>
 * cards.add(wc);</br> </br> // 2nd or more cards...</br> // wc = new
 * WizardCard("More cards...");</br> // cards.add(wc);</br> // ...</br> </br>
 * WizardWindow wizwin = new WizardWindow(cards);</br>
 * wizwin.setHeading("A simple example for a wizard");</br>
 * wizwin.setHeaderTitle("Simple Wizard Example");</br> </br>
 * wizwin.show();</br>
 */
public class WizardWindow extends Window {

	I18nConstants i18n = Registry.get(AppConstants.I18N);
	
	/** The status bar text. */
	private String statusBarText = i18n.wizardDefaultStatusBarText(); // "Saving...";

	/** The previous button text. */
	private String previousButtonText = i18n.wizardDefaultPreviousButton(); // "< Previous";

	/** The next button text. */
	private String nextButtonText = i18n.wizardDefaultNextButton(); // "Next >";

	/** The cancel button text. */
	private String cancelButtonText = i18n.cancelButton(); // "Cancel";

	/** The finish button text. */
	private String finishButtonText = i18n.wizardDefaultFinishButton(); // "Finish";

	/** The indicate step text. */
	private String indicateStepText = i18n.wizardDefaultStepDescriptor(); // "Step ";

	/** The indicate of text. */
	private String indicateOfText = i18n.wizardDefaultOutOfDescriptor(); // " of ";

	/** The current step. */
	private int currentStep = 0;

	/** The wizard cards. */
	private List<Card> cards;

	/** The header title. */
	private String headerTitle;

	/** The header panel. */
	private Header headerPanel;

	/** The card panel. */
	private CardPanel cardPanel;

	/** The button bar. */
	protected Status status = new Status();

	/** The button bar. */
	protected ButtonBar buttonBar;
	
	/** The prev button. */
	private Button prevBtn;

	/** The next button. */
	private Button nextBtn;

	/** The cancel button. */
	private Button cancelBtn;

	/** The progress indicator. */
	private Indicator progressIndicator = Indicator.DOT;

	/** The wizard main img. */
	private String wizMainImg = GWT.getModuleBaseURL() + "ext-ux-wiz-default-pic.png";

	/** The hide on finish. */
	private boolean hideOnFinish = true;

	/** The showWestImageContainer. */
	private boolean showWestImageContainer = true;

	/** The panelBackgroundColor. */
	private String panelBackgroundColor ="#F6F6F6";
	
	private ArrayList<Listener<BaseEvent>> cancelListeners = new ArrayList<Listener<BaseEvent>>();
	
	private boolean hidePreviousButtonOnFirstCard = false;
	
	protected boolean isHeaderPanelHidden = false;

	protected HorizontalAlignment buttonAlignment = HorizontalAlignment.RIGHT;

	private boolean hideCancelButton = false;

	private boolean hideFinishButtonOnLastCard = false;
	

	/**
	 * Creates a new wizard window.
	 * 
	 * @param cards
	 *            an ArrayList of WizardCard/s
	 */
	public WizardWindow(List<Card> cards) {
		super();
		this.cards = cards;
		setSize(540, 300);
		setClosable(true);
		setResizable(false);
		setModal(true);
		
		
	}
	
	public void reset() {
		/*
		 * Note: this resetting process still has some things 
		 * to work out. Finish listeners seem to not be called
		 * after a reset and rebuild of a card.
		 */
		cards = null;
		setSize(540, 300);
		setClosable(true);
		setResizable(false);
		setModal(true);
		
		statusBarText = i18n.wizardDefaultStatusBarText(); // "Saving...";
		previousButtonText = i18n.wizardDefaultPreviousButton(); // "< Previous";
		nextButtonText = i18n.wizardDefaultNextButton(); // "Next >";
		cancelButtonText = i18n.cancelButton(); // "Cancel";
		finishButtonText = i18n.wizardDefaultFinishButton(); // "Finish";
		indicateStepText = i18n.wizardDefaultStepDescriptor(); // "Step ";
		indicateOfText = i18n.wizardDefaultOutOfDescriptor(); // " of ";
		currentStep = 0;
		headerTitle = null;
		headerPanel = null;
		cardPanel = null;
		status = new Status();
		buttonBar = null;
		prevBtn = null;
		nextBtn =  null;
		cancelBtn =  null;
		progressIndicator = Indicator.DOT;
		wizMainImg = GWT.getModuleBaseURL() + "ext-ux-wiz-default-pic.png";
		hideOnFinish = true;
		showWestImageContainer = true;
		panelBackgroundColor ="#F6F6F6";
		cancelListeners = new ArrayList<Listener<BaseEvent>>();

	}

	/**
	 * On button pressed.
	 * 
	 * @param component
	 *            the button
	 */
	private void onButtonPressed(Component component) {
		if (component == cancelBtn) {
			hide((Button)component);
			notifyCancelListeners();
			return;
		}
		if (component == prevBtn) {
			if (this.currentStep > 0) {
				currentStep--;
				updateWizard();
			}
		}
		if (component == nextBtn) {
			if (!cards.get(currentStep).isValid()) return;
			if (currentStep + 1 == cards.size()) {
				status.setText(statusBarText);
				status.show();
				buttonBar.disable();
				cards.get(currentStep).notifyFinishListeners();
				if (hideOnFinish) hide();
			}
			else {
				cards.get(currentStep).notifyCardCloseListeners();
				currentStep++;
				updateWizard();
			}
		}
	}

	/**
	 * Update wizard.
	 */
	private void updateWizard() {
		WizardCard wc = (WizardCard) cards.get(currentStep);
		headerPanel.updateIndicatorStep(wc.getCardTitle());
		this.cardPanel.setActiveItem(wc);
		wc.layout();

		if (currentStep + 1 == cards.size()) {
			nextBtn.setText(finishButtonText);
			nextBtn.setVisible(!hideFinishButtonOnLastCard);
		}
		else {
			nextBtn.setVisible(true);
			nextBtn.setText(nextButtonText);
		}

		if (currentStep == 0) {
			prevBtn.setEnabled(false);
			if (hidePreviousButtonOnFirstCard)
				prevBtn.setVisible(false);
		}
		else {
			prevBtn.setEnabled(true);
			prevBtn.setVisible(true);
		}
	}

	public boolean isHidePreviousButtonOnFirstCard() {
		return hidePreviousButtonOnFirstCard;
	}

	public void setHidePreviousButtonOnFirstCard(
			boolean hidePreviousButtonOnFirstCard) {
		this.hidePreviousButtonOnFirstCard = hidePreviousButtonOnFirstCard;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.extjs.gxt.ui.client.widget.Window#onRender(com.google.gwt.user.client
	 * .Element, int)
	 */
	@Override
	protected void onRender(Element parent, int pos) {
		setLayout(new BorderLayout());
		
		SelectionListener<ButtonEvent> listener = new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				onButtonPressed(ce.getComponent());
				
			}
			
		};

		prevBtn = new Button(previousButtonText, listener);
		nextBtn = new Button(nextButtonText, listener);
		cancelBtn = new Button(cancelButtonText, listener);

		buttonBar = new ButtonBar();

		buttonBar.add(prevBtn);
		buttonBar.add(nextBtn);
		buttonBar.add(cancelBtn);
		//add(status);
		
		buttonBar.setAlignment(buttonAlignment );
		
		if(hideCancelButton)
			cancelBtn.hide();

		super.onRender(parent, pos);

		headerPanel = new Header();
		if(!isHeaderPanelHidden) {
		
			add(headerPanel, new BorderLayoutData(LayoutRegion.NORTH, 60));
		}
		cardPanel = new CardPanel();
		cardPanel.setStyleAttribute("padding", "40px 15px 5px 5px");
		cardPanel.setStyleAttribute("backgroundColor", panelBackgroundColor);

		LayoutContainer lc = new LayoutContainer();
		lc.setStyleAttribute("backgroundColor", panelBackgroundColor);
		add(lc, new BorderLayoutData(LayoutRegion.EAST, 20));
				
		if (showWestImageContainer) {
			lc = new LayoutContainer();
			lc.setStyleAttribute("backgroundColor", panelBackgroundColor);
			int width = 100; // min width allowed
			if (wizMainImg != null) {
				Image leftimage = new Image(wizMainImg);
				lc.add(leftimage);
				width = Math.max(100, leftimage.getWidth());
			}
			add(lc, new BorderLayoutData(LayoutRegion.WEST, width));
		}
		else
			add(lc, new BorderLayoutData(LayoutRegion.WEST, 20));
		LayoutContainer sc = new LayoutContainer();
		sc.add(buttonBar);
		add(sc, new BorderLayoutData(LayoutRegion.SOUTH, 35));
		
		add(cardPanel, new BorderLayoutData(LayoutRegion.CENTER));
		for (Card wizardCard : cards) {
			cardPanel.add((WizardCard)wizardCard);
		}

		if (cards.size() > 0) {
			updateWizard();
		}
	}

	/**
	 * Returns the currently set header title.
	 * 
	 * @return the header title
	 */
	public String getHeaderTitle() {
		return headerTitle;
	}

	/**
	 * Sets the title located in the top header.
	 * 
	 * @param hdrtitle
	 *            string value
	 */
	public void setHeaderTitle(String hdrtitle) {
		this.headerTitle = hdrtitle;
	}


	/**
	 * Returns true if the West image container will be shown.
	 * 
	 * @return value of showWestImageContainer
	 */
	public boolean isShowWestImageContainer() {
		return showWestImageContainer;
	}


	/**
	 * Sets if the West image container will be shown.
	 * 
	 * @param showWestImageContainer
	 *            boolean value
	 */
	public void setShowWestImageContainer(boolean showWestImageContainer) {
		this.showWestImageContainer = showWestImageContainer;
	}

	/**
	 * Returns the hex color value of the main panel background. Defaults to #F6F6F6.
	 * 
	 * @return hex color String
	 */
	public String getPanelBackgroundColor() {
		return panelBackgroundColor;
	}

	/**
	 * Sets the hex color value of the main panel background.
	 * 
	 * @param panelBackgroundColor
	 *            String value
	 */
	public void setPanelBackgroundColor(String panelBackgroundColor) {
		this.panelBackgroundColor = panelBackgroundColor;
	}

	/**
	 * Sets the progress indicator type. Defaults to DOT
	 * 
	 * @param value
	 *            the value
	 */
	public void setProgressIndicator(Indicator value) {
		progressIndicator = value;
	}

	/**
	 * Sets the wizard image picture, or set to null if you don't wish to
	 * display an image. Defaults to "ext-ux-wiz-default-pic.png"
	 * 
	 * @param url
	 *            the url
	 */
	public void setMainImg(String url) {
		wizMainImg = url;
	}

	/**
	 * The Class Header.
	 */
	protected class Header extends VerticalPanel {

		/** The indicator panel. */
		private HorizontalPanel indicatorPanel;

		/** The indicator bar. */
		private ProgressBar indicatorBar;

		/** The step html. */
		private Html stepHTML;

		/** The title html. */
		private Html titleHTML;

		/**
		 * Creates a new header.
		 */
		protected Header() {
			super();
			setTableWidth("100%");
			setTableHeight("100%");
			setStyleName("ext-ux-wiz-Header");
			setBorders(true);

			titleHTML = new Html("");
			titleHTML.setStyleName("ext-ux-wiz-Header-title");
			add(titleHTML);

			if (progressIndicator == Indicator.DOT) {

				stepHTML = new Html("");
				stepHTML.setStyleName("ext-ux-wiz-Header-step");
				add(stepHTML);

				indicatorPanel = new HorizontalPanel();
				indicatorPanel.setStyleName("ext-ux-wiz-Header-stepIndicator-container");
				for (int i = 0; i < cards.size(); i++) {
					Image img = new Image(GWT.getModuleBaseURL() + "ext-ux-wiz-stepIndicator-off.png");
					img.setStyleName("ext-ux-wiz-Header-stepIndicator");
					indicatorPanel.add(img);
				}
				TableData td = new TableData();
				td.setHorizontalAlign(HorizontalAlignment.RIGHT);
				add(indicatorPanel, td);
			}
			if (progressIndicator == Indicator.PROGRESSBAR) {
				indicatorBar = new ProgressBar();
				LayoutContainer lc = new LayoutContainer();
				lc.add(indicatorBar);
				lc.setWidth("50%");
				TableData td = new TableData();
				td.setHorizontalAlign(HorizontalAlignment.RIGHT);
				td.setPadding(5);
				add(lc, td);
			}
		}

		/**
		 * Update indicator step.
		 * 
		 * @param cardtitle
		 *            the cardtitle
		 */
		protected void updateIndicatorStep(String cardtitle) {

			final boolean titleEmpty = null == cardtitle || "".equals(cardtitle.trim());
			
			final String stepStr = indicateStepText + (1 + currentStep) + indicateOfText + cards.size()
					+ (titleEmpty ? "" : " : " + cardtitle);
			final double stepRatio = (double) (1 + currentStep) / (double) cards.size();
			titleHTML.setHtml(headerTitle);

			if (progressIndicator == Indicator.DOT) {
				stepHTML.setHtml(stepStr);
				indicatorPanel.removeAll();
				for (int i = 0; i < cards.size(); i++) {

					Image img = new Image(GWT.getModuleBaseURL() + "ext-ux-wiz-stepIndicator-off.png");
					img.setStyleName("ext-ux-wiz-Header-stepIndicator");
					if (i == currentStep) {
						img.setUrl(GWT.getModuleBaseURL() + "ext-ux-wiz-stepIndicator-on.png");
					}
					indicatorPanel.add(img);
				}
				indicatorPanel.layout();
			}
			if (progressIndicator == Indicator.PROGRESSBAR) {
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {
					public void execute() {
						indicatorBar.updateProgress(stepRatio, stepStr);
					}
				});
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.extjs.gxt.ui.client.widget.VerticalPanel#onRender(com.google.
		 * gwt.user.client.Element, int)
		 */
		@Override
		protected void onRender(Element parent, int pos) {
			super.onRender(parent, pos);
			setStyleAttribute("borderLeft", "none");
			setStyleAttribute("borderRight", "none");
			setStyleAttribute("borderTop", "none");
		}
	}

	/**
	 * Gets the previous button text.
	 * 
	 * @return the previousButtonText
	 */
	public String getPreviousButtonText() {
		return previousButtonText;
	}

	/**
	 * Sets the previous button text.
	 * 
	 * @param previousButtonText
	 *            the previousButtonText to set. Defaults to "< Previous".
	 */
	public void setPreviousButtonText(String previousButtonText) {
		this.previousButtonText = previousButtonText;
	}

	/**
	 * Gets the next button text.
	 * 
	 * @return the nextButtonText
	 */
	public String getNextButtonText() {
		return nextButtonText;
	}

	/**
	 * Sets the next button text.
	 * 
	 * @param nextButtonText
	 *            the nextButtonText to set. Defaults to "Next >".
	 */
	public void setNextButtonText(String nextButtonText) {
		this.nextButtonText = nextButtonText;
	}

	/**
	 * Gets the cancel button text.
	 * 
	 * @return the cancelButtonText
	 */
	public String getCancelButtonText() {
		return cancelButtonText;
	}

	/**
	 * Sets the cancel button text.
	 * 
	 * @param cancelButtonText
	 *            the cancelButtonText to set. Defaults to "Cancel".
	 */
	public void setCancelButtonText(String cancelButtonText) {
		this.cancelButtonText = cancelButtonText;
	}

	/**
	 * Gets the finish button text.
	 * 
	 * @return the finishButtonText
	 */
	public String getFinishButtonText() {
		return finishButtonText;
	}

	/**
	 * Sets the finish button text.
	 * 
	 * @param finishButtonText
	 *            the finishButtonText to set. Defaults to "Finish".
	 */
	public void setFinishButtonText(String finishButtonText) {
		this.finishButtonText = finishButtonText;
	}

	/**
	 * Gets the indicate step text.
	 * 
	 * @return the indicateStepText
	 */
	public String getIndicateStepText() {
		return indicateStepText;
	}

	/**
	 * Sets the indicate step text.
	 * 
	 * @param indicateStepText
	 *            the indicateStepText to set. Defaults to "Step ".
	 */
	public void setIndicateStepText(String indicateStepText) {
		this.indicateStepText = indicateStepText;
	}

	/**
	 * Gets the indicate of text.
	 * 
	 * @return the indicateOfText
	 */
	public String getIndicateOfText() {
		return indicateOfText;
	}

	/**
	 * Sets the indicate of text.
	 * 
	 * @param indicateOfText
	 *            the indicateOfText to set. Defaults to " of ".
	 */
	public void setIndicateOfText(String indicateOfText) {
		this.indicateOfText = indicateOfText;
	}

	/**
	 * Sets the status bar text.
	 * 
	 * @param statusBarText
	 *            the new status bar text
	 */
	public void setStatusBarText(String statusBarText) {
		this.statusBarText = statusBarText;
	}

	/**
	 * Gets the status bar text.
	 * 
	 * @return the status bar text
	 */
	public String getStatusBarText() {
		return statusBarText;
	}

	/**
	 * Checks if hide on finish is set.
	 * 
	 * @return true, if hide on finish is enabled
	 */
	public boolean isHideOnFinish() {
		return hideOnFinish;
	}

	/**
	 * Enables hide on finish.
	 * 
	 * @param hideOnFinish
	 *            true to hide when finish button pressed. Defaults to true.
	 */
	public void setHideOnFinish(boolean hideOnFinish) {
		this.hideOnFinish = hideOnFinish;
	}
	
	protected List<Card> getCards() {
		if (null == cards) {
			cards = new ArrayList<Card>();
		}
		return cards;
	}

	public void addCancelListener(Listener<BaseEvent> listener) {
		cancelListeners.add(listener);
	}

	public void notifyCancelListeners() {
		if (cancelListeners != null) {
			for (Listener<BaseEvent> listener : cancelListeners) {
				listener.handleEvent(new BaseEvent(this));
			}
		}
		
	}

	@Override
	protected void onEndResize(ResizeEvent re) {
		super.onEndResize(re);
		updateWizard();
	}

	public HorizontalAlignment getButtonAlignment() {
		return buttonAlignment;
	}

	public void setButtonAlignment(HorizontalAlignment buttonAlignment) {
		this.buttonAlignment = buttonAlignment;
	}
	
	public Component getPreviousButton() {
		return prevBtn;
	}
	
	public Component getNextButton() {
		return nextBtn;
	}
	
	public Component getCancelButton() {
		return cancelBtn;
	}

	public void pressPreviousButton() {
		onButtonPressed(getPreviousButton());
		
	}

	public void pressNextButton() {
		onButtonPressed(getNextButton());
		
	}

	public void pressCancelButton() {
		onButtonPressed(getCancelButton());
		
	}

	public void hideCancelButton(boolean hide) {
		hideCancelButton = hide;
		
	}

	public boolean isHideFinishButtonOnLastCard() {
		return this.hideFinishButtonOnLastCard;
	}

	public void setHideFinishButtonOnLastCard(boolean hide) {
		this.hideFinishButtonOnLastCard = hide;
	}

}
