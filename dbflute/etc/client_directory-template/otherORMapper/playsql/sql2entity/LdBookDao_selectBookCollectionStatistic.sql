
--#BookCollectionStatistic#--
--*BOOK_ID*--

select book.BOOK_ID
     , book.BOOK_NAME
     , (select count(*) from COLLECTION where BOOK_ID = book.BOOK_ID) as COLLECTION_COUNT
  from BOOK book
 /*BEGIN*/where
   /*IF bookName != null*/book.BOOK_NAME like /*bookName*/'S2Dao' || '%'/*END*/
 /*END*/
 order by COLLECTION_COUNT desc
;
