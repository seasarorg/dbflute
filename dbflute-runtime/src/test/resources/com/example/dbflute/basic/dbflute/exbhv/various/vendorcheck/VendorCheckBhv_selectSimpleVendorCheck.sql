-- #SimpleVendorCheck#

select vendor.VENDOR_CHECK_ID
     , vendor.DECIMAL_DIGIT
     , vendor.INTEGER_NON_DIGIT
     , vendor.TYPE_OF_BOOLEAN
     , vendor.TYPE_OF_TEXT
  from VENDOR_CHECK vendor
