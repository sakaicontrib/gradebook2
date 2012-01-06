package org.sakaiproject.gradebook.gwt.client;


public enum BusinessLogicCode {

	CannotUnremoveItemWithRemovedCategory			(1),
	OnlyEqualWeightDropLowestRule					(2),
	NoDuplicateCategoryNamesRule					(3),
	NoDuplicateItemNamesRule 						(4),
	NoDuplicateItemNamesWithinCategoryRule			(5),
	NoImportedDuplicateItemNamesRule				(6),
	NoImportedDuplicateItemNamesWithinCategoryRule	(7),
	NoZeroPointItemsRule							(8),
	MustIncludeCategoryRule							(9),
	ReleaseChildItemsWhenCategoryReleased			(10),
	RemoveChildItemsWhenCategoryRemoved				(11),
	RemoveEqualWeightingWhenItemWeightChangesRules	(12),
	CannotIncludeDeletedItemRule					(13),
	CannotIncludeItemFromUnincludedCategoryRule		(14),
	ItemNameCannotBeNullOrEmpty						(15),
	EntityPointsCannotBeNegative					(16),
	EntityWeightCannotBeNegative					(17),
	ScanTronScoresMustBeNormalized					(18);

	private Integer code = null;

	private BusinessLogicCode(Integer code) {
		this.code  = code;
	}

	public Integer getCode() {
		return code;
	}

}