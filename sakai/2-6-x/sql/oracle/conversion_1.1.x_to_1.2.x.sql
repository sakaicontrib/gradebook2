-- CONVERSION FROM 1.1.x to 1.2.x
update GB_GRADE_RECORD_T
set EXCLUDED_FROM_GRADE=EXCLUDED
where EXCLUDED is not NULL;

update GB_GRADABLE_OBJECT_T
set NOT_COUNTED=IS_UNWEIGHTED
where IS_UNWEIGHTED is not NULL;

update GB_GRADABLE_OBJECT_T
set SORT_ORDER=ITEM_ORDER
where ITEM_ORDER is not NULL;
-- END CONVERSION FROM 1.1.x to 1.2.x