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

import java.util.List;

import org.sakaiproject.gradebook.gwt.client.exceptions.BusinessRuleException;
import org.sakaiproject.tool.gradebook.Assignment;
import org.sakaiproject.tool.gradebook.Category;

public interface BusinessLogic {

	public void applyItemNameNotEmpty(String name) throws BusinessRuleException;
	
	public void applyWeightTooSmallOrTooLarge(Double weight) throws BusinessRuleException; 
	
	public void applyPointsNonNegative(Double points) throws BusinessRuleException; 
	
	public void applyCannotUnremoveItemWithRemovedCategory(boolean isRemoved, Category category) 
	throws BusinessRuleException;

	public void applyNoDuplicateCategoryNamesRule(Long gradebookId, String name, Long categoryId, List<Category> categories) throws BusinessRuleException;

	public void applyNoDuplicateItemNamesRule(Long gradebookId, String name, Long assignmentId, List<Assignment> assignments) throws BusinessRuleException;

	public void applyNoDuplicateItemNamesWithinCategoryRule(Long categoryId, String name, Long assignmentId, List<Assignment> assignments) throws BusinessRuleException;

	public void applyNoImportedDuplicateItemNamesRule(Long gradebookId, String name, Long assignmentId, List<Assignment> assignments) throws BusinessRuleException;

	public void applyNoImportedDuplicateItemNamesWithinCategoryRule(Long categoryId, String name, Long assignmentId, List<Assignment> assignments) throws BusinessRuleException;

	public void applyNoZeroPointItemsRule(Double itemPoints) throws BusinessRuleException;
	
	public void applyMustIncludeCategoryRule(Long categoryId) throws BusinessRuleException;

	public void applyReleaseChildItemsWhenCategoryReleased(Category category, List<Assignment> assignments, boolean isReleased) throws BusinessRuleException;
	
	public void applyRemoveChildItemsWhenCategoryRemoved(Category category, List<Assignment> assignments) throws BusinessRuleException;

	public void applyCannotIncludeDeletedItemRule(boolean isAssignmentRemoved, boolean isCategoryRemoved, boolean isUnweighted) throws BusinessRuleException;

	public void applyCannotIncludeItemFromUnincludedCategoryRule(boolean isCategoryIncluded, boolean isItemIncluded, boolean wasItemIncluded) throws BusinessRuleException;
	
	public boolean checkRecalculateEqualWeightingRule(Category category);

	public boolean checkRecalculatePointsRule(Long assignmentId, Double newPoints, Double oldPoints);

	public boolean checkReleased(List<Assignment> assignments);

	public boolean isDropLowestAllowed(Category category);
	public boolean isDropLowestAllowed(Boolean isCategory, Boolean isExtraCreditCategory, Boolean isWeightedCategoriesGradebook, Boolean isWeightEquallyCategory, Boolean isWeightByPointsCategory);
	public boolean isDropLowestAllowed(boolean isCategory, boolean isExtraCreditCategory, boolean isWeightedCategoriesGradebook, boolean isWeightEquallyCategory, boolean isWeightByPointsCategory);

	public void reorderAllCategories(Long gradebookId, Long categoryId, Integer newCategoryOrder, Integer oldCategoryOrder);
	
	public void reorderAllItems(Long gradebookId, Long assignmentId, Integer newItemOrder, Integer oldItemOrder);
	
	public void reorderAllItemsInCategory(Long assignmentId, Category category, Category oldCategory, Integer newItemOrder, Integer oldItemOrder);
	
}