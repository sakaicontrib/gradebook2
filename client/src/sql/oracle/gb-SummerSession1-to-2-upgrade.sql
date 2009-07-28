alter table GB_CATEGORY_T
add (
	CATEGORY_ORDER number(10,0)
);

alter table GB_GRADABLE_OBJECT_T
add (
	ITEM_ORDER number(10,0)
);
