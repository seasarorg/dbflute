-- selectMapLikeSearch.sql

-- !MapLikeSearchPmb!
-- !!Map<String, Object> conditionMap:like!!

select member.MEMBER_ID
     , member.MEMBER_NAME
  from MEMBER member
 /*BEGIN*/where
   /*IF pmb.conditionMap.memberId != null*/member.MEMBER_ID = /*pmb.conditionMap.memberId*/3/*END*/
   /*IF pmb.conditionMap.memberName != null*/and member.MEMBER_NAME like /*pmb.conditionMap.memberName*/'S%'/*END*/
 /*END*/
 order by member.MEMBER_ID asc
