ALTER TABLE LCO_WITHHOLDINGRULECONF
 ADD (isuseproducttaxcategory  CHAR(1)
DEFAULT 'N' NOT NULL CHECK (isuseproducttaxcategory IN ('Y',
   'N')));

ALTER TABLE LCO_WITHHOLDINGRULE ADD (c_taxcategory_id  NUMBER(10));

ALTER TABLE LCO_WITHHOLDINGRULE ADD   CONSTRAINT taxcategory_withholding
 FOREIGN KEY (c_taxcategory_id)
 REFERENCES C_TAXCATEGORY (c_taxcategory_id);
