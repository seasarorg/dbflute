-- #VendorCheckIntegerSum#

select sum(vendor.INTEGER_NON_DIGIT) as INTEGER_NON_DIGIT_SUM
  from VENDOR_CHECK vendor
