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
import java.util.ResourceBundle;

import org.sakaiproject.gradebook.gwt.client.BusinessLogicCode;
import org.sakaiproject.gradebook.gwt.client.exceptions.BusinessRuleException;
import org.sakaiproject.gradebook.gwt.client.exceptions.InvalidInputException;
import org.sakaiproject.gradebook.gwt.server.Util;
import org.sakaiproject.tool.gradebook.Assignment;
import org.sakaiproject.tool.gradebook.Category;

public class BusinessLogicImpl implements BusinessLogic {

	private static ResourceBundle i18n = ResourceBundle.getBundle("org.sakaiproject.gradebook.gwt.client.I18nConstants");
	
	private GradebookToolService gbService;

	public void applyRulesBeforeAddingCategory(boolean hasCategories, Long gradebookId, String name, 
			List<Category> categories, Integer dropLowest, Boolean isEqualWeight) 
	throws BusinessRuleException {
		if (hasCategories) {
			applyNoDuplicateCategoryNamesRule(gradebookId, name, null, categories);		
			applyOnlyEqualWeightDropLowestRule(dropLowest, isEqualWeight);
		}
	}

	public void applyCannotUnremoveItemWithRemovedCategory(boolean isRemoved, Category category) 
	throws BusinessRuleException {

		if (!isRemoved && category != null && category.isRemoved()) 
			throw new BusinessRuleException("You cannot undelete a grade item when the category that owns it has been deleted. Please undelete the category first.", 
					BusinessLogicCode.CannotUnremoveItemWithRemovedCategory);
	
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.sakai.BusinessLogic#applyOnlyEqualWeightDropLowestRule(int, boolean)
	 */
	public void applyOnlyEqualWeightDropLowestRule(int dropLowest, boolean isEqualWeight) throws BusinessRuleException {

		if (!isEqualWeight && dropLowest > 0) {
			StringBuilder builder = new StringBuilder();
			builder.append("Drop lowest is only valid for categories with equally weighted items. ")
			.append("Please select equally weighted before setting a drop lowest value.");

			throw new BusinessRuleException(builder.toString(), BusinessLogicCode.OnlyEqualWeightDropLowestRule);
		}

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
					.append("Please enter a different name for this category.");

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
					.append("Please enter a different name for the grade item.");

					throw new BusinessRuleException(builder.toString(), BusinessLogicCode.NoDuplicateItemNamesRule);
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
					.append("Please enter a different name for the grade item.");

					throw new BusinessRuleException(builder.toString(), BusinessLogicCode.NoDuplicateItemNamesWithinCategoryRule);
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

		if (assignments != null) {
			for (Assignment assignment : assignments) {
				assignment.setRemoved(true);
				gbService.updateAssignment(assignment);
			}
		}

	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.sakai.BusinessLogic#applyRemoveEqualWeightingWhenItemWeightChangesRules(org.sakaiproject.tool.gradebook.Category, java.lang.Double, java.lang.Double, boolean, boolean, boolean)
	 */
	public void applyRemoveEqualWeightingWhenItemWeightChangesRules(Category category, Double oldAssignmentWeight, Double newAssignmentWeight, 
			boolean isExtraCredit, boolean isUnweighted, boolean wasUnweighted) throws BusinessRuleException {
		if (!isUnweighted && !wasUnweighted) {
			if (oldAssignmentWeight == null || !oldAssignmentWeight.equals(newAssignmentWeight)) {
				if (!isExtraCredit && category != null && category.isEqualWeightAssignments() != null && category.isEqualWeightAssignments().booleanValue()) {
					// FIXME: Do we really need to go back to the db and get this category again? 
					Category editCategory = gbService.getCategory(category.getId());
					editCategory.setEqualWeightAssignments(Boolean.FALSE);
					gbService.updateCategory(editCategory);
				}
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

	public void makeItemsNonExtraCredit(List<Assignment> assignments) {

		if (assignments != null) {
			for (Assignment assignment : assignments) {

				if (Util.checkBoolean(assignment.isExtraCredit())) {
					assignment.setExtraCredit(Boolean.FALSE);
					gbService.updateAssignment(assignment);
				}
			}
		}
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

	public void reorderRemainingItemsInCategory(Category category, Category oldCategory, Integer newItemOrder, Integer oldItemOrder) {

		boolean isSwitchOfCategories = oldCategory != null;

		List<Assignment> assignments = gbService.getAssignmentsForCategory(category.getId());

		if (isSwitchOfCategories) {
			List<Assignment> oldAssignments = gbService.getAssignmentsForCategory(oldCategory.getId());
			// Clean up the old assignments' order
			if (oldAssignments != null) {
				int count = 0;
				for (Assignment assignment : oldAssignments) {

					Integer itemOrder = assignment.getSortOrder();

					if (itemOrder == null)
						itemOrder = Integer.valueOf(count);

					count++;

					// Anything below (greater than) the old item needs to be advanced
					if (oldItemOrder.compareTo(itemOrder) < 0) {
						int idx = itemOrder.intValue() - 1;
						assignment.setSortOrder(Integer.valueOf(idx));
						gbService.updateAssignment(assignment);
					}
				}
			}
		}



		if (oldItemOrder != null && (isSwitchOfCategories || newItemOrder.compareTo(oldItemOrder) < 0)) {
			// Either we have moved something up, and we need to increase everything that follows its new place
			// until we reach its old place

			boolean isModifying = false;
			if (assignments != null) {
				int count = 0;
				for (Assignment assignment : assignments) {

					Integer itemOrder = assignment.getSortOrder();
					if (itemOrder == null)
						itemOrder = Integer.valueOf(count);

					count++;

					// We want to stop modifying immediately before pass this one
					if (!isSwitchOfCategories && itemOrder.equals(oldItemOrder)) {
						isModifying = false;
						break;
					}

					// We want to start modifying at the newItemOrder, which is not the item we've just updated
					// but the one it's replacing
					if (itemOrder.equals(newItemOrder))
						isModifying = true;

					if (isModifying) {
						int idx = itemOrder.intValue() + 1;
						assignment.setSortOrder(Integer.valueOf(idx));
						gbService.updateAssignment(assignment);
					}
				}
			}
		} else {
			// Or we have moved something down, in which case we need to take everything that follows 
			// its old place and reduce by 1 until we get to its new place

			boolean isModifying = false;
			if (assignments != null) {
				int count = 0;
				for (Assignment assignment : assignments) {

					Integer itemOrder = assignment.getSortOrder();
					if (itemOrder == null)
						itemOrder = Integer.valueOf(count);

					count++;

					if (isModifying) {
						int idx = itemOrder.intValue() - 1;
						assignment.setSortOrder(Integer.valueOf(idx));
						gbService.updateAssignment(assignment);
					}

					// We don't want to start modifying until we pass this one
					if (oldItemOrder == null || itemOrder.equals(oldItemOrder)) 
						isModifying = true;

					if (oldItemOrder != null && itemOrder.equals(newItemOrder)) {
						isModifying = false;
						break;
					}
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


}
