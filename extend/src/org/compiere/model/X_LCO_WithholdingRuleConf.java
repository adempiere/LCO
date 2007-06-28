/******************************************************************************
 * Product: Adempiere ERP & CRM Smart Business Solution                       *
 * Copyright (C) 1999-2007 ComPiere, Inc. All Rights Reserved.                *
 * This program is free software;
 you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY;
 without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program;
 if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 * For the text or an alternative of this public license, you may reach us    *
 * ComPiere, Inc., 2620 Augustine Dr. #245, Santa Clara, CA 95054, USA        *
 * or via info@compiere.org or http://www.compiere.org/license.html           *
 *****************************************************************************/
package org.compiere.model;

/** Generated Model - DO NOT CHANGE */
import java.util.*;
import java.sql.*;
import java.math.*;
import org.compiere.util.*;
/** Generated Model for LCO_WithholdingRuleConf
 *  @author Adempiere (generated) 
 *  @version Release 3.2.0 - $Id: X_LCO_WithholdingRuleConf.java,v 1.3 2007/05/09 10:43:46 cruiz Exp $ */
public class X_LCO_WithholdingRuleConf extends PO
{
/** Standard Constructor
@param ctx context
@param LCO_WithholdingRuleConf_ID id
@param trxName transaction
*/
public X_LCO_WithholdingRuleConf (Properties ctx, int LCO_WithholdingRuleConf_ID, String trxName)
{
super (ctx, LCO_WithholdingRuleConf_ID, trxName);
/** if (LCO_WithholdingRuleConf_ID == 0)
{
setLCO_WithholdingType_ID (0);
}
 */
}
/** Load Constructor 
@param ctx context
@param rs result set 
@param trxName transaction
*/
public X_LCO_WithholdingRuleConf (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=1000008 */
public static final int Table_ID=MTable.getTable_ID("LCO_WithholdingRuleConf");

/** TableName=LCO_WithholdingRuleConf */
public static final String Table_Name="LCO_WithholdingRuleConf";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"LCO_WithholdingRuleConf");

protected BigDecimal accessLevel = BigDecimal.valueOf(2);
/** AccessLevel
@return 2 - Client 
*/
protected int get_AccessLevel()
{
return accessLevel.intValue();
}
/** Load Meta Data
@param ctx context
@return PO Info
*/
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
/** Info
@return info
*/
public String toString()
{
StringBuffer sb = new StringBuffer ("X_LCO_WithholdingRuleConf[").append(get_ID()).append("]");
return sb.toString();
}
/** Set Is Use BP ISIC.
@param IsUseBPISIC Is Use BP ISIC */
public void setIsUseBPISIC (boolean IsUseBPISIC)
{
set_Value ("IsUseBPISIC", Boolean.valueOf(IsUseBPISIC));
}
/** Get Is Use BP ISIC.
@return Is Use BP ISIC */
public boolean isUseBPISIC() 
{
Object oo = get_Value("IsUseBPISIC");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Column name IsUseBPISIC */
public static final String COLUMNNAME_IsUseBPISIC = "IsUseBPISIC";
/** Set Is Use BP Tax Payer Type.
@param IsUseBPTaxPayerType Is Use BP Tax Payer Type */
public void setIsUseBPTaxPayerType (boolean IsUseBPTaxPayerType)
{
set_Value ("IsUseBPTaxPayerType", Boolean.valueOf(IsUseBPTaxPayerType));
}
/** Get Is Use BP Tax Payer Type.
@return Is Use BP Tax Payer Type */
public boolean isUseBPTaxPayerType() 
{
Object oo = get_Value("IsUseBPTaxPayerType");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Column name IsUseBPTaxPayerType */
public static final String COLUMNNAME_IsUseBPTaxPayerType = "IsUseBPTaxPayerType";
/** Set Is Use Org ISIC.
@param IsUseOrgISIC Is Use Org ISIC */
public void setIsUseOrgISIC (boolean IsUseOrgISIC)
{
set_Value ("IsUseOrgISIC", Boolean.valueOf(IsUseOrgISIC));
}
/** Get Is Use Org ISIC.
@return Is Use Org ISIC */
public boolean isUseOrgISIC() 
{
Object oo = get_Value("IsUseOrgISIC");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Column name IsUseOrgISIC */
public static final String COLUMNNAME_IsUseOrgISIC = "IsUseOrgISIC";
/** Set Is Use Org Tax Payer Type.
@param IsUseOrgTaxPayerType Is Use Org Tax Payer Type */
public void setIsUseOrgTaxPayerType (boolean IsUseOrgTaxPayerType)
{
set_Value ("IsUseOrgTaxPayerType", Boolean.valueOf(IsUseOrgTaxPayerType));
}
/** Get Is Use Org Tax Payer Type.
@return Is Use Org Tax Payer Type */
public boolean isUseOrgTaxPayerType() 
{
Object oo = get_Value("IsUseOrgTaxPayerType");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Column name IsUseOrgTaxPayerType */
public static final String COLUMNNAME_IsUseOrgTaxPayerType = "IsUseOrgTaxPayerType";
/** Set Is Use Withholding Category.
@param IsUseWithholdingCategory Is Use Withholding Category */
public void setIsUseWithholdingCategory (boolean IsUseWithholdingCategory)
{
set_Value ("IsUseWithholdingCategory", Boolean.valueOf(IsUseWithholdingCategory));
}
/** Get Is Use Withholding Category.
@return Is Use Withholding Category */
public boolean isUseWithholdingCategory() 
{
Object oo = get_Value("IsUseWithholdingCategory");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Column name IsUseWithholdingCategory */
public static final String COLUMNNAME_IsUseWithholdingCategory = "IsUseWithholdingCategory";
/** Set Withholding Type.
@param LCO_WithholdingType_ID Withholding Type */
public void setLCO_WithholdingType_ID (int LCO_WithholdingType_ID)
{
if (LCO_WithholdingType_ID < 1) throw new IllegalArgumentException ("LCO_WithholdingType_ID is mandatory.");
set_ValueNoCheck ("LCO_WithholdingType_ID", Integer.valueOf(LCO_WithholdingType_ID));
}
/** Get Withholding Type.
@return Withholding Type */
public int getLCO_WithholdingType_ID() 
{
Integer ii = (Integer)get_Value("LCO_WithholdingType_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Column name LCO_WithholdingType_ID */
public static final String COLUMNNAME_LCO_WithholdingType_ID = "LCO_WithholdingType_ID";
}
