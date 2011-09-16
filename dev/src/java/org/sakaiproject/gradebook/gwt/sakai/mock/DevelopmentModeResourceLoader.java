package org.sakaiproject.gradebook.gwt.sakai.mock;
/*
 * 
 * override various methods to bypass the use of statics in superclass
 */
import java.util.Locale;


import org.sakaiproject.tool.api.SessionManager;
import org.sakaiproject.user.api.PreferencesService;
import org.sakaiproject.util.ResourceLoader;

public class DevelopmentModeResourceLoader extends ResourceLoader {
	
	/*
	 * this should be overridden by spring lookup-method handlers
	 */
	
	protected SessionManager sessionManager = null;
	
	protected PreferencesService preferencesService(){
		return (PreferencesService) null;
	}
	
/*
 * this is a direct copy of the parent method in all but the case of the first character in 'sessionManager'
 * (non-Javadoc)
 * @see org.sakaiproject.util.ResourceLoader#getLocale()
 */
	@Override
	public Locale getLocale() {
		Locale loc = null;
		 try
		 {
			 // check if locale is requested for specific user
			 if ( this.userId != null )
			 {
				 loc = getLocale( this.userId );
			 }
			 
			 else
			 {
				 loc = (Locale) sessionManager.getCurrentSession().getAttribute(LOCALE_SESSION_KEY+sessionManager.getCurrentSessionUserId());
				 
				 // The locale is not in the session. 
				 // Look for it and set in session
				 if (loc == null) 
					 loc = setContextLocale(null);
			 }
		 }
		 //FIXME NPE's should not be explicitly caught - rather check the session above fo null
		 catch(NullPointerException e) 
		 {
			if (M_log.isWarnEnabled()) {
				M_log.warn("getLocale() swallowing NPE");
				e.printStackTrace();
			}
			 // The locale is not in the session. 
			 // Look for it and set in session
			 loc = setContextLocale(null);
		 } 

		if (loc == null) {
			M_log.info("getLocale() Locale not found in preferences or session, returning default");
			loc = Locale.getDefault();
		}

		 return loc;
	}

	public void setSessionManager(SessionManager sessionManager) {
		this.sessionManager = sessionManager;
	}

	

	@Override
	public Locale getLocale(String userId) {
		return preferencesService().getLocale(userId);
	}


	/* 
	 * this is a direct copy of the parent method in all but the case of the first character in 'sessionManager'
	 * (non-Javadoc)
	 * @see org.sakaiproject.util.ResourceLoader#setContextLocale(java.util.Locale)
	 */
	@Override
	public Locale setContextLocale(Locale loc) {
		//	 First : find locale from Sakai user preferences, if available
		if (loc == null) 
		{
			try
			{
				loc = getLocale( sessionManager.getCurrentSessionUserId() );
			}
			catch (Exception e)
			{
				if (M_log.isWarnEnabled()) {
					M_log.warn("setContextLocale(Locale) swallowing Exception");
					e.printStackTrace();
				}
			} // ignore and continue
		}
			  
		// Second: find locale from user browser session, if available
		if (loc == null)
		{
			try
			{
				loc = (Locale) sessionManager.getCurrentSession().getAttribute("locale");
			}
			catch (NullPointerException e)
			{
				if (M_log.isWarnEnabled()) {
					M_log.warn("setContextLocale(Locale) swallowing NPE");
					e.printStackTrace();
				}
			} // ignore and continue
		}

		// Last: find system default locale
		if (loc == null)
		{
			// fallback to default.
			loc = Locale.getDefault();
			if (M_log.isDebugEnabled()) {
				M_log.debug("setContextLocale(Locale), default locale");
			}
		}
		else if (!Locale.getDefault().getLanguage().equals("en") && loc.getLanguage().equals("en") && !loc.toString().equals(DEBUG_LOCALE))
		{
			// Tweak for English: en is default locale. It has no suffix in filename.
			loc = new Locale("");
			if (M_log.isDebugEnabled()) {
				M_log.debug("setContextLocale(Locale), Tweak for English");
			}
		}

		//Write the sakai locale in the session	
		try 
		{
			String sessionUser = sessionManager.getCurrentSessionUserId();
			if ( sessionUser != null )
				sessionManager.getCurrentSession().setAttribute(LOCALE_SESSION_KEY+sessionUser,loc);
		}
		catch (Exception e) 
		{
			if (M_log.isWarnEnabled()) {
				M_log.warn("setContextLocale(Locale) swallowing Exception");
				e.printStackTrace();
			}
		} //Ignore and continue
		 
		return loc;		
	}


	

}
