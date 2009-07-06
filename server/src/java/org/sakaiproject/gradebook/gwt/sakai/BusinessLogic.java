package org.sakaiproject.gradebook.gwt.sakai;

import java.util.List;

import org.sakaiproject.gradebook.gwt.client.exceptions.BusinessRuleException;
import org.sakaiproject.tool.gradebook.Assignment;
import org.sakaiproject.tool.gradebook.Category;

public interface BusinessLogic {

	public void applyCannotUnremoveItemWithRemovedCategory(boolean isRemoved, Category category) 
	throws BusinessRuleException;
	
	public void applyOnlyEqualWeightDropLowestRule(int dropLowest, boolean isEqualWeight) throws BusinessRuleException;

	public void applyNoDuplicateCategoryNamesRule(Long gradebookId, String name, Long categoryId, List<Category> categories) throws BusinessRuleException;

	public void applyNoDuplicateItemNamesRule(Long gradebookId, String name, Long assignmentId, List<Assignment> assignments) throws BusinessRuleException;

	public void applyNoDuplicateItemNamesWithinCategoryRule(Long categoryId, String name, Long assignmentId, List<Assignment> assignments) throws BusinessRuleException;

	public void applyMustIncludeCategoryRule(Long categoryId) throws BusinessRuleException;

	public void applyRemoveChildItemsWhenCategoryRemoved(Category category, List<Assignment> assignments) throws BusinessRuleException;

	public void applyRemoveEqualWeightingWhenItemWeightChangesRules(Category category, Double oldAssignmentWeight, Double newAssignmentWeight, boolean isExtraCredit,
			boolean isUnweighted, boolean wasUnweighted) throws BusinessRuleException;

	public void applyCannotIncludeDeletedItemRule(boolean isAssignmentRemoved, boolean isCategoryRemoved, boolean isUnweighted) throws BusinessRuleException;

	public void applyCannotIncludeItemFromUnincludedCategoryRule(boolean isCategoryIncluded, boolean isItemIncluded, boolean wasItemIncluded) throws BusinessRuleException;
	
	public boolean checkRecalculateEqualWeightingRule(Category category);

	public boolean checkRecalculatePointsRule(Long assignmentId, Double newPoints, Double oldPoints);

	public void makeItemsNonExtraCredit(List<Assignment> assignments);
	
}