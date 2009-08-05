alter table GB_CATEGORY_T
add (
	CATEGORY_ORDER number(10,0)
);

alter table GB_GRADABLE_OBJECT_T
add (
	ITEM_ORDER number(10,0)
);

create table GB_USER_CONFIG_T 
(
	ID number(19),
	USER_UID varchar2(99),
	GRADEBOOK_ID number(19),
	CONFIG_FIELD varchar2(99),
	CONFIG_VALUE varchar2(756)
);

create sequence GB_USER_CONFIG_S
start with 1000
increment by 1
nocache
nocycle;
