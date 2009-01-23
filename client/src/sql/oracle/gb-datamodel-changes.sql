alter table GB_CATEGORY_T
add (
	IS_EXTRA_CREDIT number(1,0),
	IS_EQUAL_WEIGHT_ASSNS number(1,0),
	IS_UNWEIGHTED number(1,0)
);

alter table GB_GRADEBOOK_T
add (
	IS_EQUAL_WEIGHT_CATS number(1,0)
);

alter table GB_GRADE_RECORD_T
add (
	EXCLUDED number(1,0)
);

alter table GB_GRADABLE_OBJECT_T
add (
	IS_EXTRA_CREDIT number(1,0),
	ASSIGNMENT_WEIGHTING double precision,
	IS_UNWEIGHTED number(1,0)
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



