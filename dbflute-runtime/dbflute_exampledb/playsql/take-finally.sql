
-- #df:assertCountZero#
select count (*)
  from MEMBER
 where MEMBER_STATUS_CODE = 'FML'
   and MEMBER_FORMALIZED_DATETIME is null
;

-- #df:assertListZero#
select local.MEMBER_ID, local.MEMBER_NAME
  from MEMBER local
 where local.MEMBER_STATUS_CODE = 'WDL'
   and not exists (select sub.MEMBER_ID
                     from MEMBER_WITHDRAWAL sub
                    where sub.MEMBER_ID = local.MEMBER_ID
       )
;
