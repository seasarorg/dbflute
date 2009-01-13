-- selectBindVariableNotFoundProperty.sql

select member.MEMBER_ID
     , member.MEMBER_NAME
     , (select sum(purchase.PURCHASE_PRICE)
          from PURCHASE purchase
         where purchase.MEMBER_ID = member.MEMBER_ID
           and purchase.PAYMENT_COMPLETE_FLG = 0
       ) as UNPAID_PRICE_SUMMARY
     , memberStatus.MEMBER_STATUS_NAME
  from MEMBER member
    left outer join MEMBER_STATUS memberStatus
      on member.MEMBER_STATUS_CODE = memberStatus.MEMBER_STATUS_CODE
 /*BEGIN*/where
   member.MEMBER_ID = /*pmb.wrongMemberId*/3
   /*IF pmb.memberName != null*/and member.MEMBER_NAME like /*pmb.memberName*/'ス' || '%'/*END*/
   /*IF pmb.memberStatusCode != null*/and member.MEMBER_STATUS_CODE = /*pmb.memberStatusCode*/'FML'/*END*/
   /*IF pmb.unpaidMemberOnly*/
   and exists (select 'yes'
                 from PURCHASE purchase
                where purchase.MEMBER_ID = member.MEMBER_ID
                  and purchase.PAYMENT_COMPLETE_FLG = 0
       )
   /*END*/
 /*END*/
 order by UNPAID_PRICE_SUMMARY desc, member.MEMBER_ID asc
