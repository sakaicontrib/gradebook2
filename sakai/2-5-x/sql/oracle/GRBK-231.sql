
-- Removes duplicate entries from the user config table
DELETE from

    gb_user_config_t A

WHERE

   rowid >

     (SELECT min(rowid) FROM gb_user_config_t B

      WHERE

         B.config_field = A.config_field

      and

         B.user_uid = A.user_uid

      and
        b.gradebook_id = a.gradebook_id

      );


create unique index GB_USER_CONFIG_UNIQ_IDX on GB_USER_CONFIG_T(USER_UID, GRADEBOOK_ID, CONFIG_FIELD);

commit;

