-- #PurchaseSummaryMember#
-- +cursor+

-- !PurchaseSummaryMemberPmb!
-- !!String memberStatusCode:cls(MemberStatus)!!

select member.MEMBER_ID
     , member.MEMBER_NAME
     , member.MEMBER_BIRTHDAY
     , member.MEMBER_FORMALIZED_DATETIME
     , (select sum(purchase.PURCHASE_COUNT)
          from PURCHASE purchase
         where purchase.MEMBER_ID = member.MEMBER_ID
       ) as PURCHASE_SUMMARY
  from MEMBER member
 where member.MEMBER_STATUS_CODE = /*pmb.memberStatusCode*/'FML'
