Directory for ReplaceSchema task

replace-schema.sql:
DDL statements for creation of your schema

take-finally.sql:
SQL statements for check data (or DDL after data loading)

"data" directory is for loaded data like this:
/- - - - - - - - - - - - - - - - - - - -
playsql
  |-data
     |-common
     |  |-xls
     |     |-10-master.xls
     |     |-defaultValueMap.dataprop
     |-ut
        |-xls
           |-20-member.xls  
           |-30-product.xls  
           |-defaultValueMap.dataprop
- - - - - - - - - -/
