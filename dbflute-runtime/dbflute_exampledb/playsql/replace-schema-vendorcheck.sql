CREATE TABLE VENDOR_CHECK
(
	VENDOR_CHECK_ID NUMERIC(16) NOT NULL PRIMARY KEY,
	DECIMAL_DIGIT NUMERIC(5, 3) NOT NULL,
	INTEGER_NON_DIGIT NUMERIC(5, 0) NOT NULL,
	TYPE_OF_BOOLEAN BOOLEAN NOT NULL,
	TYPE_OF_TEXT TEXT
)  ;

CREATE VIEW SUMMARY_PRODUCT AS
select product.PRODUCT_ID
     , product.PRODUCT_NAME
     , product.PRODUCT_STATUS_CODE
     , (select max(purchase.PURCHASE_DATETIME)
          from PURCHASE purchase
         where purchase.PRODUCT_ID = product.PRODUCT_ID
       ) as LATEST_PURCHASE_DATETIME
  from PRODUCT product
;

comment on table MEMBER is '会員登録時にInsertされる。
物理削除されることはない';
comment on column MEMBER.MEMBER_ID is '連番';
comment on column MEMBER.MEMBER_NAME is '会員検索の条件となる';
comment on table MEMBER_STATUS is '固定の区分値';
comment on table MEMBER_LOGIN is 'ログインの度にInsertされる';
comment on table MEMBER_WITHDRAWAL is '退会するとInsertされる';
comment on table PURCHASE is '購入の度ににInsertされる';
comment on table PRODUCT is '商品マスタ';
comment on table PRODUCT_STATUS is '商品ステータス';
