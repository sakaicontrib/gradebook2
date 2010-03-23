create table GB_ACTION_RECORD_T
(
	ID bigint NOT NULL AUTO_INCREMENT,
	KEY(ID),
	VERSION int,
	GRADEBOOK_UID varchar(756),
	GRADEBOOK_ID bigint,
	ENTITY_TYPE varchar(200),
	ACTION_TYPE varchar(200),
	ENTITY_NAME varchar(756),
	ENTITY_ID varchar(200),
	PARENT_ID varchar(200),
	LEARNER_UID varchar(99),
	FIELD_NAME varchar(756),
	FIELD_VALUE varchar(756),
	FIELD_START_VALUE varchar(756),
	ACTION_STATUS varchar(100),
	DATE_PERFORMED timestamp,
	DATE_RECORDED timestamp,
	GRADER_ID varchar(99)
);


alter table GB_CATEGORY_T
add (
	IS_EXTRA_CREDIT tinyint(1),
	IS_EQUAL_WEIGHT_ASSNS tinyint(1),
	IS_UNWEIGHTED tinyint(1),
	CATEGORY_ORDER INT,
	ENFORCE_POINT_WEIGHTING tinyint(1)
); 


alter table GB_GRADEBOOK_T
add (
	IS_EQUAL_WEIGHT_CATS tinyint(1),
	IS_SCALED_EXTRA_CREDIT tinyint(1)
	DO_SHOW_MEAN tinyint(1),
	DO_SHOW_MEDIAN tinyint(1),
	DO_SHOW_MODE tinyint(1),
	DO_SHOW_RANK tinyint(1),
	DO_SHOW_ITEM_STATS tinyint(1)
);



alter table GB_GRADABLE_OBJECT_T
add (
	IS_EXTRA_CREDIT tinyint(1),
	ASSIGNMENT_WEIGHTING double precision,
	IS_UNWEIGHTED tinyint(1),
	IS_NULL_ZERO tinyint(1),
	SORT_ORDER INT
);


alter table GB_GRADE_RECORD_T
add (
	IS_EXCLUDED_FROM_GRADE tinyint(1)
);

create index GB_ACTION_RECORD_ID_IDX on GB_ACTION_RECORD_T(ID);




create index GB_ACTION_RECORD_GRADEBOOK_IDX on GB_ACTION_RECORD_T(GRADEBOOK_UID);



create table GB_ACTION_RECORD_PROPERTY_T 
(
	ACTION_RECORD_ID bigint,
	PROPERTY_NAME varchar(756),
	PROPERTY_VALUE varchar(756)
);




create index GB_ACTION_RECORD_PROP_ID_IDX on GB_ACTION_RECORD_PROPERTY_T(ACTION_RECORD_ID);




create table GB_USER_DEREFERENCE_T 
(
	ID bigint NOT NULL AUTO_INCREMENT,
	KEY(ID),
	USER_UID varchar(99),
	EID varchar(99),
	DISPLAY_ID varchar(99),
	DISPLAY_NAME varchar(756),
	LAST_NAME_FIRST varchar(756),
	SORT_NAME varchar(756),
	EMAIL varchar(756),
	CREATED_ON timestamp
);



create unique index GB_USER_DEREF_USER_IDX on GB_USER_DEREFERENCE_T(USER_UID);





create table GB_USER_DEREF_RM_UPDATE_T 
(
	ID bigint NOT NULL AUTO_INCREMENT,
	KEY(ID),
	REALM_ID varchar(99),
	LAST_UPDATE timestamp,
	REALM_COUNT bigint
);





create index GB_USER_DEREF_RM_UP_IDX on GB_USER_DEREF_RM_UPDATE_T(REALM_ID);


alter table GB_GRADE_RECORD_T
add (
	EXCLUDED tinyint(1)
);

create table GB_USER_CONFIG_T 
(
	ID bigint NOT NULL AUTO_INCREMENT,
	KEY(ID),
	USER_UID varchar(99),
	GRADEBOOK_ID bigint,
	CONFIG_FIELD varchar(99),
	CONFIG_VALUE varchar(756)
);


