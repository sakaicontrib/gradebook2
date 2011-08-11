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


package org.sakaiproject.gradebook.gwt.sakai.mock;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Stack;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.entity.api.Entity;
import org.sakaiproject.entity.api.HttpAccess;
import org.sakaiproject.entity.api.Reference;
import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.user.api.User;
import org.sakaiproject.user.api.UserAlreadyDefinedException;
import org.sakaiproject.user.api.UserDirectoryService;
import org.sakaiproject.user.api.UserEdit;
import org.sakaiproject.user.api.UserIdInvalidException;
import org.sakaiproject.user.api.UserLockedException;
import org.sakaiproject.user.api.UserNotDefinedException;
import org.sakaiproject.user.api.UserPermissionException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class UserDirectoryServiceMock implements UserDirectoryService {

	private static final Log log = LogFactory.getLog(UserDirectoryServiceMock.class);

	private List<User> users;
	public static int DEFAULT_NUMBER_TEST_LEARNERS = 200;
	
	// GRBK-751
	// This is used to test userIDs that have special characters
	// e.g. set it to ";XXj7"
	private static final String USER_ID_POSTFIX = "";
	
	public void init() {
		int numUsers = getUserCount(); 
		boolean useOldName = useOldUserNames(); 
		if (users == null) {
			users = new ArrayList<User>(numUsers);
			for (int i=0;i<numUsers;i++) {
				users.add(createUserRecord(i + USER_ID_POSTFIX, useOldName, i + ""));
			}
		}
	}
	
	private boolean useOldUserNames() {
		boolean oldnames = false; 
		String p = System.getProperty("gb2.mockuser.oldnames"); 
		if (p != null && !"".equals(p))
		{
			 try {
				oldnames = Boolean.parseBoolean(p);
			} catch (Exception e) {
				// If its badly formatted, we'll just use the new method.  let the dev know, but likely it will be missed in log spam...
				log.error("argument gb2.mockuser.oldnames (" + p + ") is formatted incorrectly.  It must be able to be parsed as a boolean"); 
			} 
		}
		return oldnames; 
	}

	private int getUserCount() {
		String p = System.getProperty("gb2.mockuser.count"); 
		if (p != null && !"".equals(p))
		{
			try {
				int uc = Integer.parseInt(p);
				log.info("Overriding default number of mock users to count " + uc); 
				return uc; 
			} catch (NumberFormatException e) {
				log.warn("Could not read property gb2.mockuser.count - using default count of" + DEFAULT_NUMBER_TEST_LEARNERS);
				return DEFAULT_NUMBER_TEST_LEARNERS; 
			} 
		}
		else
		{
			log.info("Using default number of mock users of " + DEFAULT_NUMBER_TEST_LEARNERS); 
			return DEFAULT_NUMBER_TEST_LEARNERS;
		}
	}

	public UserEdit addUser(String arg0, String arg1)
			throws UserIdInvalidException, UserAlreadyDefinedException,
			UserPermissionException {
		// TODO Auto-generated method stub
		return null;
	}

	public User addUser(String arg0, String arg1, String arg2, String arg3,
			String arg4, String arg5, String arg6, ResourceProperties arg7)
			throws UserIdInvalidException, UserAlreadyDefinedException,
			UserPermissionException {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean allowAddUser() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean allowRemoveUser(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean allowUpdateUser(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean allowUpdateUserEmail(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean allowUpdateUserName(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean allowUpdateUserPassword(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean allowUpdateUserType(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	public User authenticate(String arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	public void cancelEdit(UserEdit arg0) {
		// TODO Auto-generated method stub

	}

	public void commitEdit(UserEdit arg0) throws UserAlreadyDefinedException {
		// TODO Auto-generated method stub

	}

	public int countSearchUsers(String arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	public int countUsers() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void destroyAuthentication() {
		// TODO Auto-generated method stub

	}

	public UserEdit editUser(String arg0) throws UserNotDefinedException,
			UserPermissionException, UserLockedException {
		// TODO Auto-generated method stub
		return null;
	}

	public Collection findUsersByEmail(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public User getAnonymousUser() {
		// TODO Auto-generated method stub
		return null;
	}

	public User getCurrentUser() {
		return createUserRecord(0,false);
	}

	public User getUser(String arg0) throws UserNotDefinedException {
		
		for(User user : users) {
			if(user.getId().equals(arg0)){
				return user;
			}
		}
		
		return null;
	}

	public User getUserByEid(String arg0) throws UserNotDefinedException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getUserEid(String arg0) throws UserNotDefinedException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getUserId(String arg0) throws UserNotDefinedException {
		// TODO Auto-generated method stub
		return null;
	}

	public List getUsers() {
		// TODO Auto-generated method stub
		return users;
	}

	
	
	public List getUsers(Collection arg0) {
		
		return users;
	}

	public List getUsers(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	public UserEdit mergeUser(Element arg0) throws UserIdInvalidException,
			UserAlreadyDefinedException, UserPermissionException {
		// TODO Auto-generated method stub
		return null;
	}

	public void removeUser(UserEdit arg0) throws UserPermissionException {
		// TODO Auto-generated method stub

	}

	public List searchUsers(String arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	public String userReference(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public String archive(String arg0, Document arg1, Stack arg2, String arg3,
			List arg4) {
		// TODO Auto-generated method stub
		return null;
	}

	public Entity getEntity(Reference arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Collection getEntityAuthzGroups(Reference arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getEntityDescription(Reference arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public ResourceProperties getEntityResourceProperties(Reference arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getEntityUrl(Reference arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public HttpAccess getHttpAccess() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getLabel() {
		// TODO Auto-generated method stub
		return null;
	}

	public String merge(String arg0, Element arg1, String arg2, String arg3,
			Map arg4, Map arg5, Set arg6) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean parseEntityReference(String arg0, Reference arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean willArchiveMerge() {
		// TODO Auto-generated method stub
		return false;
	}
	
	public List<User> getUsersByEids(Collection<String> eids){
        return null;
	}
	//
	// TEST DATA
	//
	private final String[] FIRST_NAMES = { "Joel", "John", "Kelly",
		"Freeland", "Bruce", "Rajeev", "Thomas", "Jon", "Mary", "Jane",
		"Susan", "Cindy", "Veronica", "Shana", "Shania", "Olin", "Brenda",
		"Lowell", "Doug", "Yiyun", "Xi-Ming", "Grady", "Martha", "Stewart", 
		"Kennedy", "Joseph", "Iosef", "Sean", "Timothy", "Paula", "Keith",
		"Ignatius", "Iona", "Owen", "Ian", "Ewan", "Rachel", "Wendy", 
		"Quentin", "Nancy", "Mckenna", "Kaylee", "Aaron", "Erin", "Maris", 
		"D.", "Quin", "Tara", "Moira", "Bristol" };

	private  final String[] LAST_NAMES = { "Smith", "Paterson",
		"Haterson", "Raterson", "Johnson", "Sonson", "Paulson", "Li",
		"Yang", "Redford", "Shaner", "Bradley", "Herzog", "O'Neil", "Williams",
		"Simone", "Oppenheimer", "Brown", "Colgan", "Frank", "Grant", "Klein",
		"Miller", "Taylor", "Schwimmer", "Rourer", "Depuis", "Vaugh", "Auerbach", 
		"Shannon", "Stepford", "Banks", "Ashby", "Lynne", "Barclay", "Barton",
		"Cromwell", "Dering", "Dunlevy", "Ethelstan", "Fry", "Gilly",
		"Goodrich", "Granger", "Griffith", "Herbert", "Hurst", "Keigwin", 
		"Paddock", "Pillings", "Landon", "Lawley", "Osborne", "Scarborough",
		"Whiting", "Wibert", "Worth", "Tremaine", "Barnum", "Beal", "Beers", 
		"Bellamy", "Barnwell", "Beckett", "Breck", "Cotesworth", 
		"Coventry", "Elphinstone", "Farnham", "Ely", "Dutton", "Durham",
		"Eberlee", "Eton", "Edgecomb", "Eastcote", "Gloucester", "Lewes", 
		"Leland", "Mansfield", "Lancaster", "Oakham", "Nottingham", "Norfolk",
		"Poole", "Ramsey", "Rawdon", "Rhodes", "Riddell", "Vesey", "Van Wyck",
		"Van Ness", "Twickenham", "Trowbridge", "Ames", "Agnew", "Adlam", 
		"Aston", "Askew", "Alford", "Bedeau", "Beauchamp" };
	
	private final String[] SECTIONS = { "001", "002", "003", "004" };
	
	private Random random = new Random();
	
	private int getRandomInt(int max) {
		return random.nextInt(max);
	}
	
	private User createUserRecord(String userId, boolean useOldName, String numericId) {
		
		String id = userId;
		String displayId = id; //String.valueOf(100000 + getRandomInt(899999));
		String firstName = FIRST_NAMES[getRandomInt(FIRST_NAMES.length)];
		String lastName = LAST_NAMES[getRandomInt(LAST_NAMES.length)];
		if (!useOldName)
		{
			firstName = "Student"; // FIXME - maybe i18n, but this is testdata... 
			lastName = numericId; 
		}
		String eid = "eid:" + firstName.toLowerCase() + "." + lastName.toLowerCase();
		String email = firstName.toLowerCase() + "." + lastName.toLowerCase() + "@nowhere.edu";
	
		User userRecord = new UserMock(id, displayId, eid, firstName, lastName, email);
		
		return userRecord;
		
	}
	
	private User createUserRecord(int i, boolean useOldName) {
		
		return createUserRecord(String.valueOf(i), useOldName, String.valueOf(i));
	}

	public List<User> searchExternalUsers(String arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		return null;
	}


}
