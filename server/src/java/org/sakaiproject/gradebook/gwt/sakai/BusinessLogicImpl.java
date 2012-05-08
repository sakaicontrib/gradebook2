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

package org.sakaiproject.gradebook.gwt.sakai;

import java.math.BigDecimal;
import java.util.List;

import org.sakaiproject.gradebook.gwt.client.BusinessLogicCode;
import org.sakaiproject.gradebook.gwt.client.exceptions.BusinessRuleException;
import org.sakaiproject.gradebook.gwt.client.model.type.ActionType;
import org.sakaiproject.gradebook.gwt.client.model.type.EntityType;
import org.sakaiproject.gradebook.gwt.sakai.model.ActionRecord;
import org.sakaiproject.gradebook.gwt.server.Util;
import org.sakaiproject.service.gradebook.shared.GradebookService;
import org.sakaiproject.tool.gradebook.Assignment;
import org.sakaiproject.tool.gradebook.Category;
import org.sakaiproject.tool.gradebook.Gradebook;
import org.sakaiproject.util.ResourceLoader;

public class BusinessLogicImpl implements BusinessLogic {

	// Set via IoC
	private ResourceLoader i18n;
	
	private GradebookToolService gbService;

	public void applyRulesBeforeAddingCategory(boolean hasCategories, Long gradebookId, String name, 
			List<Category> categories, Integer dropLowest, Boolean isEqualWeight) 
	throws BusinessRuleException {
		if (hasCategories) {
			applyNoDuplicateCategoryNamesRule(gradebookId, name, null, categories);
		}
	}

	public void applyCannotUnremoveItemWithRemovedCategory(boolean isRemoved, Category category) 
	throws BusinessRuleException {

		if (!isRemoved && category != null && category.isRemoved()) 
			throw new BusinessRuleException("You cannot undelete a grade item when the category that owns it has been deleted. Please undelete the category first.", 
					BusinessLogicCode.CannotUnremoveItemWithRemovedCategory);
	
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.sakai.BusinessLogic#applyNoDuplicateCategoryNamesRule(java.lang.Long, java.lang.String, java.lang.Long, java.util.List)
	 */
	public void applyNoDuplicateCategoryNamesRule(Long gradebookId, String name, Long categoryId, List<Category> categories) throws BusinessRuleException {

		if (categories != null) {
			for (Category c : categories) {
				if (!c.isRemoved() && c.getName() != null && name != null && c.getName().trim().equalsIgnoreCase(name.trim())) {
					if (categoryId != null && categoryId.equals(c.getId()))
						continue;

					StringBuilder builder = new StringBuilder();
					builder.append("There is already an existing category called \"").append(name).append("\" ").append("in this gradebook. ")
					.append("(Remember names are case-insensitive.) Please enter a different name for this category.");

					throw new BusinessRuleException(builder.toString(), BusinessLogicCode.NoDuplicateCategoryNamesRule);
				} // if
			} // for
		} // if

	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.sakai.BusinessLogic#applyNoDuplicateItemNamesRule(java.lang.Long, java.lang.String, java.lang.Long, java.util.List)
	 */
	public void applyNoDuplicateItemNamesRule(Long gradebookId, String name, Long assignmentId, List<Assignment> assignments) throws BusinessRuleException {
		if (assignments != null) {
			for (Assignment a : assignments) {
				if (!a.isRemoved() && a.getName() != null && name != null && a.getName().trim().equalsIgnoreCase(name.trim())) {
					if (assignmentId != null && assignmentId.equals(a.getId()))
						continue;

					StringBuilder builder = new StringBuilder();
					builder.append("There is already an existing item called \"").append(name).append("\" ").append("in this gradebook. ")
					.append("(Remember names are case-insensitive.) Please enter a different name for the grade item.");

					throw new BusinessRuleException(builder.toString(), BusinessLogicCode.NoDuplicateItemNamesRule);
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.sakai.BusinessLogic#applyNoImportedDuplicateItemNamesRule(java.lang.Long, java.lang.String, java.lang.Long, java.util.List)
	 */
	public void applyNoImportedDuplicateItemNamesRule(Long gradebookId, String name, Long assignmentId, List<Assignment> assignments) throws BusinessRuleException {
		if (assignments != null) {
			for (Assignment a : assignments) {
				if (!a.isRemoved() && a.getName() != null && name != null && a.getName().trim().equalsIgnoreCase(name.trim())) {
					if (assignmentId != null && assignmentId.equals(a.getId()))
						continue;

					StringBuilder builder = new StringBuilder();
					builder.append("There is already an existing item called \"").append(name).append("\" ").append("in this gradebook. ")
					.append("(Remember names are case-insensitive.) Please enter a different name for the grade item.");

					throw new BusinessRuleException(builder.toString(), BusinessLogicCode.NoImportedDuplicateItemNamesRule);
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.sakai.BusinessLogic#applyNoDuplicateItemNamesWithinCategoryRule(java.lang.Long, java.lang.String, java.lang.Long, java.util.List)
	 */
	public void applyNoDuplicateItemNamesWithinCategoryRule(Long categoryId, String name, Long assignmentId, List<Assignment> assignments) throws BusinessRuleException {
		if (assignments != null) {
			for (Assignment a : assignments) {
				if (!a.isRemoved() && a.getName().trim().equalsIgnoreCase(name.trim())) {
					if (assignmentId != null && assignmentId.equals(a.getId()))
						continue;

					StringBuilder builder = new StringBuilder();
					builder.append("There is already an existing item called \"").append(name).append("\" ").append("in this category. ")
					.append("(Remember names are case-insensitive.) Please enter a different name for the grade item.");

					throw new BusinessRuleException(builder.toString(), BusinessLogicCode.NoDuplicateItemNamesWithinCategoryRule);
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.sakai.BusinessLogic#applyNoImportedDuplicateItemNamesWithinCategoryRule(java.lang.Long, java.lang.String, java.lang.Long, java.util.List)
	 */
	public void applyNoImportedDuplicateItemNamesWithinCategoryRule(Long categoryId, String name, Long assignmentId, List<Assignment> assignments) throws BusinessRuleException {
		if (assignments != null) {
			for (Assignment a : assignments) {
				if (!a.isRemoved() && a.getName().trim().equalsIgnoreCase(name.trim())) {
					if (assignmentId != null && assignmentId.equals(a.getId()))
						continue;

					StringBuilder builder = new StringBuilder();
					builder.append("There is already an existing item called \"").append(name).append("\" ").append("in this category. ")
					.append("(Remember names are case-insensitive.) Please enter a different name for the grade item.");

					throw new BusinessRuleException(builder.toString(), BusinessLogicCode.NoImportedDuplicateItemNamesWithinCategoryRule);
				}
			}
		}
	}

	public void applyNoZeroPointItemsRule(Double itemPoints) throws BusinessRuleException {
		BigDecimal points = BigDecimal.valueOf(itemPoints.doubleValue());
		if (BigDecimal.ZERO.compareTo(points) >= 0)
			throw new BusinessRuleException(i18n.getString("applyNoZeroPointItemsRuleException"), BusinessLogicCode.NoZeroPointItemsRule);
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.sakai.BusinessLogic#applyMustIncludeCategoryRule(java.lang.Long)
	 */
	public void applyMustIncludeCategoryRule(Long categoryId) throws BusinessRuleException {
		if (categoryId == null)
			throw new BusinessRuleException("You must select a category to group this item under.", BusinessLogicCode.MustIncludeCategoryRule);
	}

	public void applyReleaseChildItemsWhenCategoryReleased(Category category, List<Assignment> assignments, boolean isReleased) throws BusinessRuleException {

		if (assignments != null) {
			for (Assignment assignment : assignments) {
				if (assignment.isReleased() != isReleased) {
					Assignment persistAssignment = gbService.getAssignment(assignment.getId());
					persistAssignment.setReleased(isReleased);
					gbService.updateAssignment(persistAssignment);
				}
			}
		}

	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.sakai.BusinessLogic#applyRemoveChildItemsWhenCategoryRemoved(org.sakaiproject.tool.gradebook.Category, java.util.List)
	 */
	public void applyRemoveChildItemsWhenCategoryRemoved(Category category, List<Assignment> assignments) throws BusinessRuleException {

		String gradebookUid = null;
		Long gradebookId = null;
		
		if (assignments.size() == 0) 
			return;
		
		// GRBK-715: create and store actionrecords for the deleted items.
		Gradebook gb = assignments.get(0).getGradebook();
		ActionRecord actionRecord = new ActionRecord(gb.getUid(), gb.getId(), null, ActionType.DELETE.name());
		
		
		if (assignments != null) {
			for (Assignment assignment : assignments) {
				
				if (assignment.isRemoved())
					continue;
				
				assignment.setRemoved(true);
				
				actionRecord.setEntityType(EntityType.ITEM.name());
				actionRecord.setEntityName(assignment.getName());
				actionRecord.setEntityId(String.valueOf(assignment.getId()));
				
				gbService.storeActionRecord(actionRecord);

				gbService.updateAssignment(assignment);
			}
		}

	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.sakai.BusinessLogic#applyCannotIncludeDeletedItemRule(boolean, boolean, boolean)
	 */
	public void applyCannotIncludeDeletedItemRule(boolean isAssignmentRemoved, boolean isCategoryRemoved, boolean isUnweighted) throws BusinessRuleException {
		if (!isUnweighted) {		
			if (isCategoryRemoved)
				throw new BusinessRuleException("You cannot include a grade item whose category has been deleted in grading. Please undelete the category first.",
						BusinessLogicCode.CannotIncludeDeletedItemRule);

			if (isAssignmentRemoved) 
				throw new BusinessRuleException("You cannot include a deleted grade item in grading. Please undelete the grade item first.",
						BusinessLogicCode.CannotIncludeDeletedItemRule);
		}
	}


	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.sakai.BusinessLogic#applyCannotIncludeItemFromUnincludedCategoryRule(boolean, boolean, boolean)
	 */
	public void applyCannotIncludeItemFromUnincludedCategoryRule(boolean isCategoryIncluded, boolean isItemIncluded, boolean wasItemIncluded)
	throws BusinessRuleException {
		if (!isCategoryIncluded) {		
			if (isItemIncluded)
				throw new BusinessRuleException("You cannot include a grade item whose category is not included in grading.",
						BusinessLogicCode.CannotIncludeItemFromUnincludedCategoryRule);
		}
	}	

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.sakai.BusinessLogic#checkRecalculateEqualWeightingRule(org.sakaiproject.tool.gradebook.Category, java.lang.Boolean, java.util.List)
	 */
	public boolean checkRecalculateEqualWeightingRule(Category category) {
		boolean hasEqualWeighting = category != null && Util.checkBoolean(category.isEqualWeightAssignments());
		return hasEqualWeighting;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.sakai.BusinessLogic#checkRecalculatePointsRule(java.lang.Long, java.lang.Double, java.lang.Double)
	 */
	public boolean checkRecalculatePointsRule(Long assignmentId, Double newPoints, Double oldPoints) {
		return (newPoints != null && oldPoints != null && newPoints.compareTo(oldPoints) != 0); 
	}
	
	public boolean checkReleased(List<Assignment> assignments) {
		boolean isReleased = false;
		if (assignments != null) {
			isReleased = assignments.size() > 0;
			for (Assignment a : assignments) {
				if (!a.isReleased()) {
					isReleased = false;
					break;
				}
			}
		}
		
		return isReleased;
	}

	public void reorderAllItems(Long gradebookId, Long assignmentId, Integer newItemOrder, Integer oldItemOrder) {
		List<Assignment> assignments = gbService.getAssignments(gradebookId);

		if (assignments != null) {
			int count = 0;
			for (Assignment assignment : assignments) {
				Integer itemOrder = Integer.valueOf(count);

				if (itemOrder.equals(newItemOrder)) {
					count++;
					itemOrder = Integer.valueOf(count);
				}

				Integer existingItemOrder = assignment.getSortOrder();

				if (!assignment.getId().equals(assignmentId)) {

					if (existingItemOrder == null || !existingItemOrder.equals(itemOrder)) {
						assignment.setSortOrder(itemOrder);
						gbService.updateAssignment(assignment);
					}

					count++;
				}
			}
		}

	}

	public void reorderAllCategories(Long gradebookId, Long categoryId, Integer newCategoryOrder, Integer oldCategoryOrder) {
		List<Category> categories = gbService.getCategories(gradebookId);

		if (categories != null) {
			int count = 0;
			for (Category category : categories) {
				if (category.isRemoved())
					continue;

				Integer categoryOrder = Integer.valueOf(count);

				if (categoryOrder.equals(newCategoryOrder)) {
					count++;
					categoryOrder = Integer.valueOf(count);
				}

				Integer existingCategoryOrder = category.getCategoryOrder();

				// Assuming we're not dealing with the category we've just updated the order on
				if (!category.getId().equals(categoryId)) {

					if (existingCategoryOrder == null || !existingCategoryOrder.equals(categoryOrder)) {
						category.setCategoryOrder(categoryOrder);
						gbService.updateCategory(category);
					}

					count++;
				}

			}
		}
	}

	public void reorderAllItemsInCategory(Long assignmentId, Category category, Category oldCategory, Integer newItemOrder, Integer oldItemOrder) {

		// We can't run this code if the category is null
		if (category == null)
			return;

		// Okay, so assume that the re-ordered item itself already has the correct 
		// order, so we don't need to touch it

		// First, we need to check to see if this is a category switch, in which case we need to reorder
		// now that we've removed the item from that old category

		boolean isSwitchOfCategories = oldCategory != null;

		if (isSwitchOfCategories) {

			List<Assignment> oldAssignments = gbService.getAssignmentsForCategory(oldCategory.getId());
			// Clean up the old assignments' order
			if (oldAssignments != null) {
				int count = 0;
				for (Assignment assignment : oldAssignments) {
					if (assignment.isRemoved())
						continue;

					Integer itemOrder = Integer.valueOf(count);
					Integer existingItemOrder = assignment.getSortOrder();

					// We need to update if the existing item order has changed or if it's null 
					// (as it would be with an older gradebook)
					if (existingItemOrder == null || !existingItemOrder.equals(itemOrder)) {
						assignment.setSortOrder(itemOrder);
						gbService.updateAssignment(assignment);
					}
					count++;
				}
			}
		} 


		// Now we can focus our attention on the current category
		List<Assignment> assignments = gbService.getAssignmentsForCategory(category.getId());

		if (assignments != null) {
			int count = 0;
			for (Assignment assignment : assignments) {

				Integer itemOrder = Integer.valueOf(count);

				// When we reach the new item that has been injected, skip it,
				// since that number has already been taken
				if (itemOrder.equals(newItemOrder)) {
					count++;
					itemOrder = Integer.valueOf(count);
				}


				Integer existingItemOrder = assignment.getSortOrder();

				// We need to update if the existing item order has changed or if it's null 
				// (as it would be with an older gradebook)

				// Assuming we're not dealing with the assignment we've just updated the order on
				if (!assignment.getId().equals(assignmentId)) {

					if (existingItemOrder == null || !existingItemOrder.equals(itemOrder)) {
						assignment.setSortOrder(itemOrder);
						gbService.updateAssignment(assignment);
					}

					count++;
				} 
			}
		}
	}

	
	public GradebookToolService getGbService() {
		return gbService;
	}

	public void setGbService(GradebookToolService gbService) {
		this.gbService = gbService;
	}	
	
	public boolean isDropLowestAllowed(Category category) {
		
		if(null == category) {
			return false;
		}
		
		boolean isCategory = category.getIsCategory();
		boolean isExtraCreditCategory = category.isExtraCredit() != null && category.isExtraCredit().booleanValue();
		boolean isWeightedCategoriesGradebook = category.getGradebook() != null && category.getGradebook().getCategory_type() == GradebookService.CATEGORY_TYPE_WEIGHTED_CATEGORY;
		boolean isWeightEquallyCategory = category.isEqualWeightAssignments() != null && category.isEqualWeightAssignments().booleanValue();
		boolean isWeightByPointsCategory = category.isEnforcePointWeighting() != null && category.isEnforcePointWeighting().booleanValue();
		
		return isDropLowestAllowed(isCategory, isExtraCreditCategory, isWeightedCategoriesGradebook, isWeightEquallyCategory, isWeightByPointsCategory);
	}
	
	public boolean isDropLowestAllowed(Boolean isCategory, Boolean isExtraCreditCategory, Boolean isWeightedCategoriesGradebook, Boolean isWeightEquallyCategory, Boolean isWeightByPointsCategory) {
		return isDropLowestAllowed(isCategory.booleanValue(), isExtraCreditCategory.booleanValue(), isWeightedCategoriesGradebook.booleanValue(), isWeightEquallyCategory.booleanValue(), isWeightByPointsCategory.booleanValue());
	}

	public boolean isDropLowestAllowed(boolean isCategory, boolean isExtraCreditCategory, boolean isWeightedCategoriesGradebook, boolean isWeightEquallyCategory, boolean isWeightByPointsCategory) {
		boolean isDropLowestAllowed = false;
		
		if (isCategory && !isExtraCreditCategory) {
			if (isWeightedCategoriesGradebook) {
				if (isWeightEquallyCategory && !isWeightByPointsCategory) {
					isDropLowestAllowed = true;
				}
			} else {
				isDropLowestAllowed = true;
			}	
		}
		return isDropLowestAllowed;
	}

	public void applyItemNameNotEmpty(String name) throws BusinessRuleException {
		if (name == null || name.length() == 0 )
		{
			throw new BusinessRuleException(i18n.getString("businessRuleNoBlankItemCategoryName"),
					BusinessLogicCode.ItemNameCannotBeNullOrEmpty);
		}
	}


	public void applyPointsNonNegative(Double points)
			throws BusinessRuleException {
		if (points != null && points.doubleValue() < 0 )
		{
			throw new BusinessRuleException(i18n.getString("businessRulePointsCannotBeNegative"), BusinessLogicCode.EntityPointsCannotBeNegative);
		}
	}

	public void applyWeightTooSmallOrTooLarge(Double weight)
			throws BusinessRuleException {
		if (weight != null) 
		{
			// Note this is imprecise, but we're only validating this in the gross sense, the fine validation occurs client side. 
			double actWeight = weight.doubleValue(); 
			
			if (actWeight < 0 || actWeight > 100)
			{
				throw new BusinessRuleException(i18n.getString("businessRuleWeightCannotBeNegative"), BusinessLogicCode.EntityWeightCannotBeNegative);
			}
		} // Null weight will be handled elsewhere. 
		
	}

	public void setI18n(ResourceLoader i18n) {
		this.i18n = i18n;
	}


}
