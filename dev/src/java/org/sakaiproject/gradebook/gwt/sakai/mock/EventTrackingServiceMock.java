package org.sakaiproject.gradebook.gwt.sakai.mock;

import java.io.Serializable;
import java.util.Date;
import java.util.Observer;

import org.sakaiproject.event.api.Event;
import org.sakaiproject.event.api.EventDelayHandler;
import org.sakaiproject.event.api.EventTrackingService;
import org.sakaiproject.event.api.NotificationService;
import org.sakaiproject.event.api.UsageSession;
import org.sakaiproject.time.api.Time;
import org.sakaiproject.user.api.User;

public class EventTrackingServiceMock implements EventTrackingService {
	
	public void addLocalObserver(Observer observer) {
		// TODO Auto-generated method stub

	}

	public void addObserver(Observer observer) {
		// TODO Auto-generated method stub

	}

	public void addPriorityObserver(Observer observer) {
		// TODO Auto-generated method stub

	}

	public void cancelDelays(String resource) {
		// TODO Auto-generated method stub

	}

	public void cancelDelays(String resource, String event) {
		// TODO Auto-generated method stub

	}

	public void delay(Event event, Time fireTime) {
		// TODO Auto-generated method stub

	}

	public void deleteObserver(Observer observer) {
		// TODO Auto-generated method stub

	}

	public Event newEvent(String event, String resource, boolean modify)
	{
		return new BaseEvent(event, resource, modify, NotificationService.NOTI_OPTIONAL);
	}

	public Event newEvent(String event, String resource, boolean modify, int priority)
	{
		return new BaseEvent(event, resource, modify, priority);
	}

	public Event newEvent(String event, String resource, String context, boolean modify, int priority)
	{
		return new BaseEvent(event, resource, context, modify, priority);
	}

	public void post(Event event) {
		String msg = null;
		String resource = null;
		if(event!=null) {
			msg = event.getEvent();
			resource = event.getResource();
		}
		System.out.println("POSTED EVENT: " + msg + "; " + resource);

	}

	public void post(Event event, UsageSession session) {
		// TODO Auto-generated method stub

	}

	public void post(Event event, User user) {
		// TODO Auto-generated method stub

	}

	public void setEventDelayHandler(EventDelayHandler handler) {
		// TODO Auto-generated method stub

	}
	
	protected class BaseEvent implements Event, Serializable {
		/**
		 * Be a good Serializable citizen
		 */
		private static final long serialVersionUID = 3690761674282252600L;

		/** The Event's sequence number. */
		protected long m_seq = 0;

		/** The Event's id string. */
		protected String m_id = "";

		/** The Event's resource reference string. */
		protected String m_resource = "";

		/** The Event's context. May be null. */
		protected String m_context = null;
		
		/** The Event's session id string. May be null. */
		protected String m_session = null;

		/** The Event's user id string. May be null. */
		protected String m_user = null;

		/** The Event's modify flag (true if the event caused a resource modification). */
		protected boolean m_modify = false;

		/** The Event's notification priority. */
		protected int m_priority = NotificationService.NOTI_OPTIONAL;

		/** Event creation time. */
		//protected Time m_time = null;

		/**
		 * Access the event id string
		 * 
		 * @return The event id string.
		 */
		public String getEvent()
		{
			return m_id;
		}

		/**
		 * Access the resource reference.
		 * 
		 * @return The resource reference string.
		 */
		public String getResource()
		{
			return m_resource;
		}

		/**
		 * Access the resource reference.
		 * 
		 * @return The resource reference string.
		 */
		public String getContext()
		{
			return m_context;
		}

		
		/**
		 * Access the UsageSession id. If null, check for a User id.
		 * 
		 * @return The UsageSession id string.
		 */
		public String getSessionId()
		{
			return m_session;
		}

		/**
		 * Access the User id. If null, check for a session id.
		 * 
		 * @return The User id string.
		 */
		public String getUserId()
		{
			return m_user;
		}

		/**
		 * Is this event one that caused a modify to the resource, or just an access.
		 * 
		 * @return true if the event caused a modify to the resource, false if it was just an access.
		 */
		public boolean getModify()
		{
			return m_modify;
		}

		/**
		 * Access the event's notification priority.
		 * 
		 * @return The event's notification priority.
		 */
		public int getPriority()
		{
			return m_priority;
		}

		/**
		 * Construct
		 * 
		 * @param event
		 *        The Event id.
		 * @param resource
		 *        The resource id.
		 * @param modify
		 *        If the event caused a modify, true, if it was just an access, false.
		 * @param priority
		 *        The Event's notification priority.
		 */
		public BaseEvent(String event, String resource, boolean modify, int priority)
		{
			setEvent(event);
			setResource(resource);
			m_modify = modify;
			m_priority = priority;

			// for dev mode setting the context to be 'gradebook2' 
			// might want to change to to the siteid or gb uid
			// Find the context using the reference (let the service that it belongs to parse it) 
			m_context = "gradebook2";
			
		}

		/**
		 * Construct
		 * 
		 * @param event
		 *        The Event id.
		 * @param resource
		 *        The resource id.
		 * @param modify
		 *        If the event caused a modify, true, if it was just an access, false.
		 * @param context
		 *        The Event's context (may be null)
		 * @param priority
		 *        The Event's notification priority.
		 */
		public BaseEvent(String event, String resource, String context, boolean modify, int priority)
		{
			setEvent(event);
			setResource(resource);
			m_modify = modify;
			m_priority = priority;
			m_context = context;
		}
		
		/**
		 * Construct
		 * 
		 * @param seq
		 *        The event sequence number.
		 * @param event
		 *        The Event id.
		 * @param resource
		 *        The resource id.
		 * @param modify
		 *        If the event caused a modify, true, if it was just an access, false.
		 * @param priority
		 *        The Event's notification priority.
		 */
		public BaseEvent(long seq, String event, String resource, String context, boolean modify, int priority)
		{
			this(event, resource, context, modify, priority);
			m_seq = seq;
		}

		/**
		 * Set the event id.
		 * 
		 * @param id
		 *        The event id string.
		 */
		protected void setEvent(String id)
		{
			if (id != null)
			{
				m_id = id;
			}
			else
			{
				m_id = "";
			}
		}

		/**
		 * Set the resource id.
		 * 
		 * @param id
		 *        The resource id string.
		 */
		protected void setResource(String id)
		{
			if (id != null)
			{
				m_resource = id;
			}
			else
			{
				m_resource = "";
			}
		}

		/**
		 * Set the session id.
		 * 
		 * @param id
		 *        The session id string.
		 */
		protected void setSessionId(String id)
		{
			if ((id != null) && (id.length() > 0))
			{
				m_session = id;
			}
			else
			{
				m_session = null;
			}
		}

		/**
		 * Set the user id.
		 * 
		 * @param id
		 *        The user id string.
		 */
		protected void setUserId(String id)
		{
			if ((id != null) && (id.length() > 0))
			{
				m_user = id;
			}
			else
			{
				m_user = null;
			}
		}

		/**
		 * @return A representation of this event's values as a string.
		 */
		public String toString()
		{
			return m_seq + ":" + getEvent() + "@" + getResource() + "[" + (getModify() ? "m" : "a") + ", " + getPriority() + "]";
		}

		public Date getEventTime() {
			
			return new Date();
		}
	}
}
