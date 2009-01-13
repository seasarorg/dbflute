-- #PurchaseMaxPriceMember#

-- !PurchaseMaxPriceMemberPmb extends SPB!
-- !!Integer memberId!!
-- !!String memberName!!

/*IF pmb.isPaging()*/
select member.MEMBER_ID
     , member.MEMBER_NAME
     , (select max(purchase.PURCHASE_PRICE)
          from PURCHASE purchase
         where purchase.MEMBER_ID = member.MEMBER_ID
       ) as PURCHASE_MAX_PRICE
     , memberStatus.MEMBER_STATUS_NAME
-- ELSE select count(*)
/*END*/
  from MEMBER member
    /*IF pmb.isPaging()*/
    left outer join MEMBER_STATUS memberStatus
      on member.MEMBER_STATUS_CODE = memberStatus.MEMBER_STATUS_CODE
    /*END*/
 /*BEGIN*/where
   /*IF pmb.memberId != null*/member.MEMBER_ID = /*pmb.memberId*/3/*END*/
   /*IF pmb.memberName != null*/and member.MEMBER_NAME like /*pmb.memberName*/'S' || '%'/*END*/
 /*END*/
 /*IF pmb.isPaging()*/
 order by PURCHASE_MAX_PRICE desc nulls last, member.MEMBER_ID asc
 /*END*/
 /*IF pmb.isPaging()*/
 limit /*$pmb.pageStartIndex*/80, /*$pmb.fetchSize*/20
 /*END*/