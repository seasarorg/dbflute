-- #UnpaidSummaryMember#

-- !UnpaidSummaryMemberPmb extends SPB!
-- !!Integer memberId!!
-- !!String memberName!!
-- !!String memberStatusCode:cls(MemberStatus)!!
-- !!boolean unpaidMemberOnly!!

/*IF pmb.isPaging()*/
select member.MEMBER_ID
     , member.MEMBER_NAME
     , (select sum(purchase.PURCHASE_PRICE)
          from PURCHASE purchase
         where purchase.MEMBER_ID = member.MEMBER_ID
           and purchase.PAYMENT_COMPLETE_FLG = 0
       ) as UNPAID_PRICE_SUMMARY
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
   /*IF pmb.memberStatusCode != null*/and member.MEMBER_STATUS_CODE = /*pmb.memberStatusCode*/'FML'/*END*/
   /*IF pmb.unpaidMemberOnly*/
   and exists (select 'yes'
                 from PURCHASE purchase
                where purchase.MEMBER_ID = member.MEMBER_ID
                  and purchase.PAYMENT_COMPLETE_FLG = 0
       )
   /*END*/
 /*END*/
 /*IF pmb.isPaging()*/
 order by UNPAID_PRICE_SUMMARY desc, member.MEMBER_ID asc
 /*END*/
