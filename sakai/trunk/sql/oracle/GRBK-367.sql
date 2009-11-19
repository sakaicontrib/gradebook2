alter table GB_GRADEBOOK_T
add (
	DO_SHOW_MEAN number(1,0),
	DO_SHOW_MEDIAN number(1,0),
	DO_SHOW_MODE number(1,0),
	DO_SHOW_RANK number(1,0),
	DO_SHOW_ITEM_STATS number(1,0)
);
