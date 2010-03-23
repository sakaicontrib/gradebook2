

alter table GB_CATEGORY_T
add (
	IS_EXTRA_CREDIT number(1,0),
	IS_EQUAL_WEIGHT_ASSNS number(1,0),
	IS_UNWEIGHTED number(1,0),
	CATEGORY_ORDER number(10,0),
	ENFORCE_POINT_WEIGHTING number(1,0)
);

alter table GB_GRADEBOOK_T
add (
	IS_EQUAL_WEIGHT_CATS number(1,0),
	IS_SCALED_EXTRA_CREDIT number(1,0),
	DO_SHOW_MEAN number(1,0),
	DO_SHOW_MEDIAN number(1,0),
	DO_SHOW_MODE number(1,0),
	DO_SHOW_RANK number(1,0),
	DO_SHOW_ITEM_STATS number(1,0)
);

alter table GB_GRADE_RECORD_T
add (
	IS_EXCLUDED_FROM_GRADE number(1,0)
);

alter table GB_GRADABLE_OBJECT_T
add (
	IS_EXTRA_CREDIT number(1,0),
	ASSIGNMENT_WEIGHTING double precision,
	IS_NULL_ZERO number(1,0),
	SORT_ORDER number(10,0)
);

create sequence GB_ACTION_RECORD_S
start with 1000
increment by 1
nocache
nocycle;

create table GB_ACTION_RECORD_T
(
	ID number(19),
	VERSION number(10),
	GRADEBOOK_UID varchar2(756),
	GRADEBOOK_ID number(19),
	ENTITY_TYPE varchar2(200),
	ACTION_TYPE varchar2(200),
	ENTITY_NAME varchar2(756),
	ENTITY_ID varchar2(200),
	PARENT_ID varchar2(200),
	LEARNER_UID varchar2(99),
	FIELD_NAME varchar2(756),
	FIELD_VALUE varchar2(756),
	FIELD_START_VALUE varchar2(756),
	ACTION_STATUS varchar2(100),
	DATE_PERFORMED timestamp,
	DATE_RECORDED timestamp,
	GRADER_ID varchar2(99)
);

create index GB_ACTION_RECORD_ID_IDX on GB_ACTION_RECORD_T(ID);

create index GB_ACTION_RECORD_GRADEBOOK_IDX on GB_ACTION_RECORD_T(GRADEBOOK_UID);

create table GB_ACTION_RECORD_PROPERTY_T 
(
	ACTION_RECORD_ID number(19),
	PROPERTY_NAME varchar(756),
	PROPERTY_VALUE varchar(756)
);

create index GB_ACTION_RECORD_PROP_ID_IDX on GB_ACTION_RECORD_PROPERTY_T(ACTION_RECORD_ID);

create table GB_USER_DEREFERENCE_T 
(
	ID number(19),
	USER_UID varchar2(99),
	EID varchar2(99),
	DISPLAY_ID varchar2(99),
	DISPLAY_NAME varchar2(756),
	LAST_NAME_FIRST varchar2(756),
	SORT_NAME varchar2(756),
	EMAIL varchar2(756),
	CREATED_ON timestamp
);

create unique index GB_USER_DEREF_USER_IDX on GB_USER_DEREFERENCE_T(USER_UID);

create sequence GB_USER_DEREFERENCE_S
start with 1000
increment by 1
nocache
nocycle;

create table GB_USER_DEREF_RM_UPDATE_T 
(
	ID number(19),
	REALM_ID varchar2(99),
	LAST_UPDATE timestamp,
	REALM_COUNT number(19)
);

create sequence GB_USER_DEREF_RM_UPDATE_S
start with 1000
increment by 1
nocache
nocycle;

create index GB_USER_DEREF_RM_UP_IDX on GB_USER_DEREF_RM_UPDATE_T(REALM_ID);


-- The following should be run when moving from QA-8 to QA-9

create index GB_USER_DEREF_SORT_NM_IDX on GB_USER_DEREFERENCE_T(SORT_NAME);
create index GB_USER_DEREF_EMAIL_IDX on GB_USER_DEREFERENCE_T(EMAIL);

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

create unique index GB_USER_CONFIG_UNIQ_IDX on GB_USER_CONFIG_T(USER_UID, GRADEBOOK_ID, CONFIG_FIELD);




