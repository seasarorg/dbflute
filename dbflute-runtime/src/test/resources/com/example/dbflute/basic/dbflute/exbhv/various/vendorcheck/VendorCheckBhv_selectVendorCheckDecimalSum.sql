-- #VendorCheckDecimalSum#

select sum(vendor.DECIMAL_DIGIT) as DECIMAL_DIGIT_SUM
  from VENDOR_CHECK vendor
