package org.sakaiproject.gradebook.gwt.sakai.mock;

import java.util.Date;

import org.sakaiproject.gradebook.gwt.client.Gradebook2RPCService;
import org.sakaiproject.gradebook.gwt.client.action.UserEntityUpdateAction;
import org.sakaiproject.gradebook.gwt.client.action.Action.EntityType;
import org.sakaiproject.gradebook.gwt.client.exceptions.BusinessRuleException;
import org.sakaiproject.gradebook.gwt.client.exceptions.FatalException;
import org.sakaiproject.gradebook.gwt.client.exceptions.InvalidInputException;
import org.sakaiproject.gradebook.gwt.client.exceptions.SecurityException;
import org.sakaiproject.gradebook.gwt.client.model.ApplicationModel;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel;
import org.sakaiproject.gradebook.gwt.client.model.ItemModel;
import org.sakaiproject.gradebook.gwt.client.model.StudentModel;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel.CategoryType;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel.GradeType;
import org.sakaiproject.gradebook.gwt.client.model.ItemModel.Type;
import org.sakaiproject.gradebook.gwt.sakai.Gradebook2ResourceProducer;
import org.sakaiproject.gradebook.gwt.sakai.Gradebook2Service;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class Gradebook2ResourceProducerMock extends RemoteServiceServlet implements Gradebook2RPCService {

	private static final long serialVersionUID = 1L;
	
	private Gradebook2ResourceProducer producer;
	
	public void init() {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[]{"test.xml", "db.xml"});

		Gradebook2Service service = (Gradebook2Service)context.getBean("org.sakaiproject.gradebook.gwt.sakai.Gradebook2Service");

		producer = new Gradebook2ResourceProducer();
		producer.setService(service);
		
		try {

			ApplicationModel applicationModel = service.getApplicationModel();

			GradebookModel gbModel = applicationModel.getGradebookModels().get(0);
			
			//GradebookModel gbModel = service.getGradebook("emptyid");
			
			ItemModel gradebook = gbModel.getGradebookItemModel();
			
			gradebook.setName("Test Gradebook");
			gradebook.setCategoryType(CategoryType.WEIGHTED_CATEGORIES);
			gradebook.setGradeType(GradeType.PERCENTAGES);
			gradebook.setItemType(Type.GRADEBOOK);
			
			service.updateItemModel(gradebook);
			
			
			String gradebookUid = gbModel.getGradebookUid();
			Long gradebookId = gbModel.getGradebookId();
			
			ItemModel essaysCategory = new ItemModel();
			essaysCategory.setName("My Essays");
			essaysCategory.setPercentCourseGrade(Double.valueOf(60d));
			essaysCategory.setDropLowest(Integer.valueOf(0));
			essaysCategory.setEqualWeightAssignments(Boolean.TRUE);
			essaysCategory.setItemType(Type.CATEGORY);
			essaysCategory.setIncluded(Boolean.TRUE);
			essaysCategory = getActiveItem(service.addItemCategory(gradebookUid, gradebookId, essaysCategory));

			
			ItemModel hwCategory = new ItemModel();
			hwCategory.setName("My Homework");
			hwCategory.setPercentCourseGrade(Double.valueOf(40d));
			hwCategory.setDropLowest(Integer.valueOf(0));
			hwCategory.setEqualWeightAssignments(Boolean.TRUE);
			hwCategory.setItemType(Type.CATEGORY);
			hwCategory.setIncluded(Boolean.TRUE);
			hwCategory = getActiveItem(service.addItemCategory(gradebookUid, gradebookId, hwCategory));
			
			
			ItemModel ecCategory = new ItemModel();
			ecCategory.setName("Extra Credit");
			ecCategory.setPercentCourseGrade(Double.valueOf(10d));
			ecCategory.setDropLowest(Integer.valueOf(0));
			ecCategory.setEqualWeightAssignments(Boolean.TRUE);
			ecCategory.setItemType(Type.CATEGORY);
			ecCategory.setExtraCredit(Boolean.TRUE);
			ecCategory.setIncluded(Boolean.TRUE);
			ecCategory = getActiveItem(service.addItemCategory(gradebookUid, gradebookId, ecCategory));
			
			
			ItemModel essay1 = new ItemModel();
			essay1.setName("Essay 1");
			essay1.setPoints(Double.valueOf(20d));
			essay1.setDueDate(new Date());
			essay1.setCategoryId(essaysCategory.getCategoryId());
			essay1.setReleased(Boolean.TRUE);
			essay1.setItemType(Type.ITEM);
			essay1.setIncluded(Boolean.TRUE);
			//essay1.setItemOrder(2);
			service.createItem(gradebookUid, gradebookId, essay1, true);
			
			ItemModel essay2 = new ItemModel();
			essay2.setName("Essay 2");
			essay2.setPoints(Double.valueOf(20d));
			essay2.setDueDate(new Date());
			essay2.setCategoryId(essaysCategory.getCategoryId());
			essay2.setReleased(Boolean.TRUE);
			essay2.setItemType(Type.ITEM);
			essay2.setIncluded(Boolean.TRUE);
			//essay2.setItemOrder(1);
			service.createItem(gradebookUid, gradebookId, essay2, true);
			
			ItemModel essay3 = new ItemModel();
			essay3.setName("Essay 3");
			essay3.setPoints(Double.valueOf(20d));
			essay3.setDueDate(new Date());
			essay3.setCategoryId(essaysCategory.getCategoryId());
			essay3.setReleased(Boolean.TRUE);
			essay3.setItemType(Type.ITEM);
			essay3.setIncluded(Boolean.TRUE);
			//essay3.setItemOrder(0);
			service.createItem(gradebookUid, gradebookId, essay3, true);
			

			ItemModel hw1 = new ItemModel();
			hw1.setName("HW 1");
			hw1.setPoints(Double.valueOf(10d));
			hw1.setDueDate(new Date());
			hw1.setCategoryId(hwCategory.getCategoryId());
			hw1.setItemType(Type.ITEM);
			hw1.setIncluded(Boolean.TRUE);
			hw1.setReleased(Boolean.FALSE);
			//hw1.setItemOrder(0);
			service.createItem(gradebookUid, gradebookId, hw1, true);
			
			ItemModel hw2 = new ItemModel();
			hw2.setName("HW 2");
			hw2.setPoints(Double.valueOf(10d));
			hw2.setDueDate(new Date());
			hw2.setCategoryId(hwCategory.getCategoryId());
			hw2.setItemType(Type.ITEM);
			hw2.setIncluded(Boolean.TRUE);
			hw2.setReleased(Boolean.FALSE);
			//hw2.setItemOrder(1);
			service.createItem(gradebookUid, gradebookId, hw2, true);
			
			ItemModel hw3 = new ItemModel();
			hw3.setName("HW 3");
			hw3.setPoints(Double.valueOf(10d));
			hw3.setDueDate(new Date());
			hw3.setCategoryId(hwCategory.getCategoryId());
			hw3.setItemType(Type.ITEM);
			hw3.setIncluded(Boolean.TRUE);
			hw3.setReleased(Boolean.FALSE);
			//hw3.setItemOrder(2);
			service.createItem(gradebookUid, gradebookId, hw3, true);
			
			ItemModel hw4 = new ItemModel();
			hw4.setName("HW 4");
			hw4.setPoints(Double.valueOf(10d));
			hw4.setDueDate(new Date());
			hw4.setCategoryId(hwCategory.getCategoryId());
			hw4.setItemType(Type.ITEM);
			hw4.setIncluded(Boolean.TRUE);
			hw4.setReleased(Boolean.FALSE);
			//hw4.setItemOrder(3);
			service.createItem(gradebookUid, gradebookId, hw4, true);

			
			ItemModel ec1 = new ItemModel();
			ec1.setName("EC 1");
			ec1.setPercentCategory(Double.valueOf(100d));
			ec1.setPoints(Double.valueOf(10d));
			ec1.setDueDate(new Date());
			ec1.setCategoryId(ecCategory.getCategoryId());
			ec1.setIncluded(Boolean.TRUE);
			ec1.setExtraCredit(Boolean.TRUE);
			ec1.setReleased(Boolean.FALSE);
			service.createItem(gradebookUid, gradebookId, ec1, true);
			
			
		} catch (Exception fe) {
			GWT.log("Failed to update gradebook properties", fe);
		}
	}
	
	public <X extends BaseModel> X create(String entityUid, Long entityId, X model, EntityType type, String secureToken) throws BusinessRuleException, FatalException, SecurityException {
		return producer.create(entityUid, entityId, model, type, secureToken);
	}

	public <X extends BaseModel> X delete(String entityUid, Long entityId, X model, EntityType type, String secureToken) throws FatalException, SecurityException {
		return producer.delete(entityUid, entityId, model, type, secureToken);
	}

	public <X extends BaseModel> X get(String entityUid, Long entityId, EntityType type, String learnerUid, Boolean doShowAll, String secureToken) throws FatalException, SecurityException {
		return producer.<X>get(entityUid, entityId, type, learnerUid, doShowAll, secureToken);
	}

	public <X extends BaseModel, Y extends ListLoadResult<X>> Y getPage(String uid, Long id, EntityType type, PagingLoadConfig config, String secureToken) throws FatalException, SecurityException {
		return producer.<X,Y>getPage(uid, id, type, config, secureToken);
	}

	public <X extends BaseModel> X update(X model, EntityType type, UserEntityUpdateAction<StudentModel> action, String secureToken) throws InvalidInputException, FatalException, SecurityException {
		return producer.update(model, type, action, secureToken);
	}

	
	private ItemModel getActiveItem(ItemModel parent) {
		if (parent.isActive())
			return parent;
		
		for (ItemModel c : parent.getChildren()) {
			if (c.isActive()) {
				return c;
			}
			
			if (c.getChildCount() > 0) {
				ItemModel activeItem = getActiveItem(c);
				
				if (activeItem != null)
					return activeItem;
			}
		}
		
		return null;
	}
}
