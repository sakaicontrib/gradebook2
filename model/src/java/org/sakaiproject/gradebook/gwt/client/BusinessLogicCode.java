package org.sakaiproject.gradebook.gwt.client;


public enum BusinessLogicCode {
	
	CannotUnremoveItemWithRemovedCategory 			(1),
	OnlyEqualWeightDropLowestRule					(2), 
	NoDuplicateCategoryNamesRule 					(3), 
	NoDuplicateItemNamesRule 						(4),
	NoDuplicateItemNamesWithinCategoryRule			(5),
	NoZeroPointItemsRule							(6),
	MustIncludeCategoryRule							(7),
	ReleaseChildItemsWhenCategoryReleased			(8),
	RemoveChildItemsWhenCategoryRemoved				(9),
	RemoveEqualWeightingWhenItemWeightChangesRules	(10),
	CannotIncludeDeletedItemRule					(11),
	CannotIncludeItemFromUnincludedCategoryRule		(12),
	ItemNameCannotBeNullOrEmpty						(13),
	EntityPointsCannotBeNegative					(14),
	EntityWeightCannotBeNegative					(15), 
	ScanTronScoresMustBeNormalized					(16);
	
	private Integer code = null;
	
	private BusinessLogicCode(Integer code) {
		this.code  = code;
	}
	
	public Integer getCode() {
		return code;
	}
	
}
