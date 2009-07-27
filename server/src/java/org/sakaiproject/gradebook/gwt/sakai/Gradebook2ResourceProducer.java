package org.sakaiproject.gradebook.gwt.sakai;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gwtwidgets.server.spring.GWTSpringController;
import org.sakaiproject.gradebook.gwt.client.Gradebook2RPCService;
import org.sakaiproject.gradebook.gwt.client.action.UserEntityUpdateAction;
import org.sakaiproject.gradebook.gwt.client.action.Action.EntityType;
import org.sakaiproject.gradebook.gwt.client.exceptions.BusinessRuleException;
import org.sakaiproject.gradebook.gwt.client.exceptions.FatalException;
import org.sakaiproject.gradebook.gwt.client.exceptions.InvalidInputException;
import org.sakaiproject.gradebook.gwt.client.exceptions.SecurityException;
import org.sakaiproject.gradebook.gwt.client.model.CommentModel;
import org.sakaiproject.gradebook.gwt.client.model.ConfigurationModel;
import org.sakaiproject.gradebook.gwt.client.model.GradeScaleRecordMapModel;
import org.sakaiproject.gradebook.gwt.client.model.GradeScaleRecordModel;
import org.sakaiproject.gradebook.gwt.client.model.ItemModel;
import org.sakaiproject.gradebook.gwt.client.model.PermissionEntryModel;
import org.sakaiproject.gradebook.gwt.client.model.SpreadsheetModel;
import org.sakaiproject.gradebook.gwt.client.model.StudentModel;
import org.sakaiproject.gradebook.gwt.server.DataTypeConversionUtil;

import sun.security.provider.PolicyParser.PermissionEntry;

import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;

public class Gradebook2ResourceProducer extends GWTSpringController implements Gradebook2RPCService {

	private static final long serialVersionUID = 1L;

	private static final Log log = LogFactory.getLog(Gradebook2ResourceProducer.class);
	
	private Gradebook2Service service;
	
	
	@SuppressWarnings("unchecked")
	public <X extends BaseModel> X create(String entityUid, Long entityId, X model, EntityType type, String secureToken) 
	throws BusinessRuleException, FatalException, SecurityException {

		isSecure(secureToken);

		X entity = null;

		try {
		
			switch (type) {
			case ITEM:
			case GRADE_ITEM:
			case CATEGORY:
				entity = (X)service.createItem(entityUid, entityId, (ItemModel)model, true);
				break;
			case SPREADSHEET:
				SpreadsheetModel spreadsheetModel = (SpreadsheetModel)model;
				entity = (X)service.createOrUpdateSpreadsheet(entityUid, spreadsheetModel);
				break;
			case PERMISSION_ENTRY:
				PermissionEntryModel permissionEntryModel = (PermissionEntryModel)model;
				entity = (X)service.createPermissionEntry(entityId, permissionEntryModel);
				break;
			}
			
		} catch (BusinessRuleException bre) {
			throw bre;
		} catch (Throwable t) {
			log.warn("FatalException: ", t);
			throw new FatalException(t.getMessage(), t);
		}
		
		return entity;
	}
	
	public <X extends BaseModel> X get(String entityUid, Long entityId, EntityType type, String learnerUid, Boolean doShowAll, String secureToken) 
	throws FatalException, SecurityException {

		isSecure(secureToken);
		
		try {
			boolean showAll = DataTypeConversionUtil.checkBoolean(doShowAll);
			
			switch (type) {
			case AUTH:
				return (X)service.getAuthorization();
			case APPLICATION:
				return (X)service.getApplicationModel();
			case GRADEBOOK:
				return (X)service.getGradebook(entityUid);
			case SUBMISSION_VERIFICATION:
				return (X)service.getSubmissionVerification(entityUid, entityId);
			case PERMISSION_ENTRY:
				return (X)service.getPermissionEntryList(entityId, learnerUid);
			}
		
		} catch (Throwable t) {
			log.warn("FatalException: ", t);
			throw new FatalException(t.getMessage(), t);
		}
		
		return null;
	}
	
	public <X extends BaseModel, Y extends ListLoadResult<X>> Y getPage(String uid, Long id, EntityType type, PagingLoadConfig config, String secureToken) 
	throws FatalException, SecurityException {
		
		isSecure(secureToken);
		
		try {
			
			switch (type) {
			case ACTION:
				return (Y)service.getActionHistory(uid, config);
			case CATEGORY:
				return (Y)service.getCategories(uid, id, config);
			case SECTION:
				return (Y)service.getSections(uid, id, config, true, "All Viewable Sections");
			case LEARNER:
				return (Y)service.getStudentRows(uid, id, config, Boolean.FALSE);
			case GRADE_EVENT:
				return (Y)service.getGradeEvents(uid, id);
			case GRADE_FORMAT:
				return (Y)service.getGradeFormats(uid, id);
			case GRADE_SCALE:
				return (Y)service.getSelectedGradeMapping(uid);
			case USER:
				return (Y)service.getUsers();
			case CATEGORY_NOT_REMOVED:
				return (Y)service.getCategoriesNotRemoved(id);
			case PERMISSION_SECTIONS:
				return (Y)service.getSections(uid, id, config, true, "All Sections/Groups");
			case STATISTICS:
				return (Y)service.getStatistics(uid, id);
			}
		
		} catch (Throwable t) {
			log.warn("FatalException: ", t);
			throw new FatalException(t.getMessage(), t);
		}

		return null;
	}
	
	public <X extends BaseModel> X update(X model, EntityType type, UserEntityUpdateAction<StudentModel> action, String secureToken) 
	throws InvalidInputException, FatalException, SecurityException {
		
		isSecure(secureToken);
		
		X entity = null;
		
		try {
	
			switch (type) {
			case CONFIGURATION:
				ConfigurationModel configModel = (ConfigurationModel)model;
				for (String field : configModel.getPropertyNames()) {
					String value = configModel.get(field);
					entity = (X)service.createOrUpdateConfigurationModel(configModel.getGradebookId(), field, value);
				}
				break;
			case GRADE_SCALE:
				GradeScaleRecordMapModel map = (GradeScaleRecordMapModel)model;
				GradeScaleRecordModel gradeScaleModel = map.getUpdatedRecord();
				List<GradeScaleRecordModel> records = service.updateGradeScaleField(map.getGradebookUid(), action.getValue(), gradeScaleModel.getLetterGrade());
				
				entity = (X)new GradeScaleRecordMapModel(records);
				break;
			case LEARNER:
				StudentModel student = (StudentModel)model;
				
				if (action.getKey().endsWith(StudentModel.COMMENT_TEXT_FLAG)) {
					
					int indexOf = action.getKey().indexOf(StudentModel.COMMENT_TEXT_FLAG);
					String assignmentId = action.getKey().substring(0, indexOf);
					
					CommentModel comment = service.createOrUpdateComment(Long.valueOf(assignmentId), student.getIdentifier(), (String)action.getValue());
					
					if (comment != null) {
						student.set(action.getKey(), comment.getText());
						student.set(new StringBuilder(assignmentId).append(StudentModel.COMMENTED_FLAG).toString(), Boolean.TRUE);
					}
					
					entity = (X)student;
				} else if (action.getKey().endsWith(StudentModel.EXCUSE_FLAG)) {
					entity = (X)service.excuseNumericItem(action.getGradebookUid(), student, action.getKey(), (Boolean)action.getValue(), (Boolean)action.getStartValue());
				} else {
					switch (action.getClassType()) {
					case DOUBLE:
						entity = (X)service.scoreNumericItem(action.getGradebookUid(), student, action.getKey(), (Double)action.getValue(), (Double)action.getStartValue());
						break;
					case STRING:
						entity = (X)service.scoreTextItem(action.getGradebookUid(), student,  action.getKey(), (String)action.getValue(), (String)action.getStartValue());
						break;
					}
					break;
				}
				break;
			default:
				entity = (X)service.updateItemModel((ItemModel)model);
			};
				
				
		} catch (BusinessRuleException bre) {
			throw bre;
		} catch (InvalidInputException ie) {
			throw ie;
		} catch (Throwable t) {
			log.warn("FatalException: ", t);
			throw new FatalException(t.getMessage(), t);
		}
		
		return entity;
	}
	
	public <X extends BaseModel> X delete(String entityUid, Long entityId, X model, EntityType type, String secureToken) 
	throws FatalException, SecurityException {
		
		isSecure(secureToken);

		try {
			switch(type) {

				case PERMISSION_ENTRY:
					PermissionEntryModel permissionEntryModel = (PermissionEntryModel) model;
					return (X) service.deletePermissionEntry(entityId, permissionEntryModel);
			}

		} catch (Throwable t) {
			log.warn("FatalException: ", t);
			throw new FatalException(t.getMessage(), t);
		}

		return null;
	}

	/*
	 * This security check can be disabled by setting the java property
	 * -Dgb2.security=false
	 * This needs to be done in GWT hosted mode
	 */
	private void isSecure(String clientSecureToken) throws SecurityException {
		
		String securityProperty = System.getProperty("gb2.security");
		if ("false".equals(securityProperty)) {
			return;
		}
		
		String serverSecureToken = service.getCurrentSession();
		String currentUser = service.getCurrentUser();
		
		if(null == serverSecureToken || null == currentUser || "".equals(serverSecureToken) || "".equals(currentUser)) {
			log.error("GB2 SECURITY : Was not able to get currentUser and currentSession");
			throw new SecurityException("Security Exception");
		}
		
		if(!clientSecureToken.startsWith(serverSecureToken)) {
			log.warn("GB2 SECURITY : Client and server secure tokens did not match");
			throw new SecurityException("Security Exception");
		}
	}
	
	public Gradebook2Service getService() {
		return service;
	}

	public void setService(Gradebook2Service service) {
		this.service = service;
	}
}
