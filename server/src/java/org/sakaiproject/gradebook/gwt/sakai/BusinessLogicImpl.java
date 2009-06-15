package org.sakaiproject.gradebook.gwt.sakai;

import java.util.List;

import org.sakaiproject.gradebook.gwt.client.exceptions.BusinessRuleException;
import org.sakaiproject.gradebook.gwt.server.DataTypeConversionUtil;
import org.sakaiproject.tool.gradebook.Assignment;
import org.sakaiproject.tool.gradebook.Category;

public class BusinessLogicImpl implements BusinessLogic {
	
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
			throw new BusinessRuleException("You cannot undelete a grade item when the category that owns it has been deleted. Please undelete the category first.");
		
	}
	
	
	
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.sakai.BusinessLogic#applyOnlyEqualWeightDropLowestRule(int, boolean)
	 */
	public void applyOnlyEqualWeightDropLowestRule(int dropLowest, boolean isEqualWeight) throws BusinessRuleException {

		if (!isEqualWeight && dropLowest > 0) {
			StringBuilder builder = new StringBuilder();
			builder.append("Drop lowest is only valid for categories with equally weighted items. ")
				   .append("Please select equally weighted before setting a drop lowest value.");
					
			throw new BusinessRuleException(builder.toString());
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
					
					throw new BusinessRuleException(builder.toString());
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
						
					throw new BusinessRuleException(builder.toString());
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
						
					throw new BusinessRuleException(builder.toString());
				}
			}
		}
	}
	

	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.sakai.BusinessLogic#applyMustIncludeCategoryRule(java.lang.Long)
	 */
	public void applyMustIncludeCategoryRule(Long categoryId) throws BusinessRuleException {
		if (categoryId == null)
			throw new BusinessRuleException("You must select a category to group this item under.");
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
				throw new BusinessRuleException("You cannot include a grade item whose category has been deleted in grading. Please undelete the category first.");
					
			if (isAssignmentRemoved) 
				throw new BusinessRuleException("You cannot include a deleted grade item in grading. Please undelete the grade item first.");
		}
	}
	
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.sakai.BusinessLogic#applyCannotIncludeItemFromUnincludedCategoryRule(boolean, boolean, boolean)
	 */
	public void applyCannotIncludeItemFromUnincludedCategoryRule(boolean isCategoryIncluded, boolean isItemIncluded, boolean wasItemIncluded)
	throws BusinessRuleException {
		if (!isCategoryIncluded) {		
			if (isItemIncluded)
				throw new BusinessRuleException("You cannot include a grade item whose category is not included in grading. Please include the category first.");
		}
	}	
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.sakai.BusinessLogic#checkRecalculateEqualWeightingRule(org.sakaiproject.tool.gradebook.Category, java.lang.Boolean, java.util.List)
	 */
	public boolean checkRecalculateEqualWeightingRule(Category category) {
		boolean hasEqualWeighting = category != null && DataTypeConversionUtil.checkBoolean(category.isEqualWeightAssignments());
		//if (hasEqualWeighting)
		//	recalculateAssignmentWeights(category, enforeEqualWeighting, assignments);
		return hasEqualWeighting;
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.sakai.BusinessLogic#checkRecalculatePointsRule(java.lang.Long, java.lang.Double, java.lang.Double)
	 */
	public boolean checkRecalculatePointsRule(Long assignmentId, Double newPoints, Double oldPoints) {
		return (newPoints != null && oldPoints != null && newPoints.compareTo(oldPoints) != 0); 
		//	recalculateAssignmentGradeRecords(assignmentId, newPoints, oldPoints);
	}
	
	

	public GradebookToolService getGbService() {
		return gbService;
	}

	public void setGbService(GradebookToolService gbService) {
		this.gbService = gbService;
	}
	
	
}
