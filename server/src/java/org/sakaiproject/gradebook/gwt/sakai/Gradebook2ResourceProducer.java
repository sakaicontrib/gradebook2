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
import org.sakaiproject.gradebook.gwt.client.model.GradeScaleRecordMapModel;
import org.sakaiproject.gradebook.gwt.client.model.GradeScaleRecordModel;
import org.sakaiproject.gradebook.gwt.client.model.ItemModel;
import org.sakaiproject.gradebook.gwt.client.model.SpreadsheetModel;
import org.sakaiproject.gradebook.gwt.client.model.StudentModel;
import org.sakaiproject.gradebook.gwt.server.DataTypeConversionUtil;
import org.sakaiproject.tool.api.Session;
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiproject.user.api.UserDirectoryService;

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
			case APPLICATION:
				return (X)service.getApplicationModel();
			case GRADEBOOK:
				return (X)service.getGradebook(entityUid);
			case SUBMISSION_VERIFICATION:
				return (X)service.getSubmissionVerification(entityUid, entityId);
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
			case SECTION:
				return (Y)service.getSections(uid, id, config);
			case LEARNER:
				return (Y)service.getStudentRows(uid, id, config, Boolean.FALSE);
			case GRADE_EVENT:
				return (Y)service.getGradeEvents(uid, id);
			case GRADE_SCALE:
				return (Y)service.getSelectedGradeMapping(uid);
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
			case GRADE_SCALE:
				GradeScaleRecordMapModel map = (GradeScaleRecordMapModel)model;
				GradeScaleRecordModel gradeScaleModel = map.getUpdatedRecord();
				List<GradeScaleRecordModel> records = service.updateGradeScaleField(map.getGradebookUid(), action.getValue(), gradeScaleModel.getLetterGrade());
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
	
	public <X extends BaseModel> X delete(X model, String secureToken) 
	throws SecurityException {
		
		isSecure(secureToken);

		return null;
	}

	/*
	 * First, we check if both the client and server session match
	 * Second, we check if current user is null
	 */
	private void isSecure(String clientSecureToken) throws SecurityException {

		String serverSecureToken = service.getCurrentSession();
		String currentUser = service.getCurrentUser();
		
		if(null == serverSecureToken || null == currentUser || "".equals(serverSecureToken) || "".equals(currentUser)) {
			log.error("Was not able to get currentUser and currentSession");
			//throw new SecurityException("Security Exception");
			return; // FIXME : need to find solution for hosted mode
		}
		
		if(!clientSecureToken.startsWith(serverSecureToken)) {
			log.warn("SECURITY: client and server secure tokens did not match");
			//throw new SecurityException("Security Exception");
		}
	}
	
	public Gradebook2Service getService() {
		return service;
	}

	public void setService(Gradebook2Service service) {
		this.service = service;
	}
}
