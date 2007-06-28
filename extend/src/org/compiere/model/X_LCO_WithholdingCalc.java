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
/** Generated Model for LCO_WithholdingCalc
 *  @author Adempiere (generated) 
 *  @version Release 3.2.0 - $Id: X_LCO_WithholdingCalc.java,v 1.3 2007/05/09 10:43:45 cruiz Exp $ */
public class X_LCO_WithholdingCalc extends PO
{
/** Standard Constructor
@param ctx context
@param LCO_WithholdingCalc_ID id
@param trxName transaction
*/
public X_LCO_WithholdingCalc (Properties ctx, int LCO_WithholdingCalc_ID, String trxName)
{
super (ctx, LCO_WithholdingCalc_ID, trxName);
/** if (LCO_WithholdingCalc_ID == 0)
{
setBaseType (null);
setLCO_WithholdingCalc_ID (0);
setName (null);
}
 */
}
/** Load Constructor 
@param ctx context
@param rs result set 
@param trxName transaction
*/
public X_LCO_WithholdingCalc (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=1000006 */
public static final int Table_ID=MTable.getTable_ID("LCO_WithholdingCalc");

/** TableName=LCO_WithholdingCalc */
public static final String Table_Name="LCO_WithholdingCalc";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"LCO_WithholdingCalc");

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
StringBuffer sb = new StringBuffer ("X_LCO_WithholdingCalc[").append(get_ID()).append("]");
return sb.toString();
}

/** BaseType AD_Reference_ID=1000000 */
public static final int BASETYPE_AD_Reference_ID=1000000;
/** Document = D */
public static final String BASETYPE_Document = "D";
/** Line = L */
public static final String BASETYPE_Line = "L";
/** Tax = T */
public static final String BASETYPE_Tax = "T";
/** Set Base Type.
@param BaseType Base Type */
public void setBaseType (String BaseType)
{
if (BaseType == null) throw new IllegalArgumentException ("BaseType is mandatory");
if (BaseType.equals("D") || BaseType.equals("L") || BaseType.equals("T"));
 else throw new IllegalArgumentException ("BaseType Invalid value - " + BaseType + " - Reference_ID=1000000 - D - L - T");
if (BaseType.length() > 1)
{
log.warning("Length > 1 - truncated");
BaseType = BaseType.substring(0,0);
}
set_Value ("BaseType", BaseType);
}
/** Get Base Type.
@return Base Type */
public String getBaseType() 
{
return (String)get_Value("BaseType");
}
/** Column name BaseType */
public static final String COLUMNNAME_BaseType = "BaseType";

/** C_BaseTax_ID AD_Reference_ID=158 */
public static final int C_BASETAX_ID_AD_Reference_ID=158;
/** Set Base Tax.
@param C_BaseTax_ID Base Tax */
public void setC_BaseTax_ID (int C_BaseTax_ID)
{
if (C_BaseTax_ID <= 0) set_Value ("C_BaseTax_ID", null);
 else 
set_Value ("C_BaseTax_ID", Integer.valueOf(C_BaseTax_ID));
}
/** Get Base Tax.
@return Base Tax */
public int getC_BaseTax_ID() 
{
Integer ii = (Integer)get_Value("C_BaseTax_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Column name C_BaseTax_ID */
public static final String COLUMNNAME_C_BaseTax_ID = "C_BaseTax_ID";
/** Set Tax.
@param C_Tax_ID Tax identifier */
public void setC_Tax_ID (int C_Tax_ID)
{
if (C_Tax_ID <= 0) set_Value ("C_Tax_ID", null);
 else 
set_Value ("C_Tax_ID", Integer.valueOf(C_Tax_ID));
}
/** Get Tax.
@return Tax identifier */
public int getC_Tax_ID() 
{
Integer ii = (Integer)get_Value("C_Tax_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Column name C_Tax_ID */
public static final String COLUMNNAME_C_Tax_ID = "C_Tax_ID";
/** Set Description.
@param Description Optional short description of the record */
public void setDescription (String Description)
{
if (Description != null && Description.length() > 255)
{
log.warning("Length > 255 - truncated");
Description = Description.substring(0,254);
}
set_Value ("Description", Description);
}
/** Get Description.
@return Optional short description of the record */
public String getDescription() 
{
return (String)get_Value("Description");
}
/** Column name Description */
public static final String COLUMNNAME_Description = "Description";
/** Set Is Calc On Invoice.
@param IsCalcOnInvoice Is Calc On Invoice */
public void setIsCalcOnInvoice (boolean IsCalcOnInvoice)
{
set_Value ("IsCalcOnInvoice", Boolean.valueOf(IsCalcOnInvoice));
}
/** Get Is Calc On Invoice.
@return Is Calc On Invoice */
public boolean isCalcOnInvoice() 
{
Object oo = get_Value("IsCalcOnInvoice");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Column name IsCalcOnInvoice */
public static final String COLUMNNAME_IsCalcOnInvoice = "IsCalcOnInvoice";
/** Set Is Calc On Payment.
@param IsCalcOnPayment Is Calc On Payment */
public void setIsCalcOnPayment (boolean IsCalcOnPayment)
{
set_Value ("IsCalcOnPayment", Boolean.valueOf(IsCalcOnPayment));
}
/** Get Is Calc On Payment.
@return Is Calc On Payment */
public boolean isCalcOnPayment() 
{
Object oo = get_Value("IsCalcOnPayment");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Column name IsCalcOnPayment */
public static final String COLUMNNAME_IsCalcOnPayment = "IsCalcOnPayment";
/** Set Is Modifiable On Payment.
@param IsModifiableOnPayment Is Modifiable On Payment */
public void setIsModifiableOnPayment (boolean IsModifiableOnPayment)
{
set_Value ("IsModifiableOnPayment", Boolean.valueOf(IsModifiableOnPayment));
}
/** Get Is Modifiable On Payment.
@return Is Modifiable On Payment */
public boolean isModifiableOnPayment() 
{
Object oo = get_Value("IsModifiableOnPayment");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Column name IsModifiableOnPayment */
public static final String COLUMNNAME_IsModifiableOnPayment = "IsModifiableOnPayment";
/** Set Withholding Calculation.
@param LCO_WithholdingCalc_ID Withholding Calculation */
public void setLCO_WithholdingCalc_ID (int LCO_WithholdingCalc_ID)
{
if (LCO_WithholdingCalc_ID < 1) throw new IllegalArgumentException ("LCO_WithholdingCalc_ID is mandatory.");
set_ValueNoCheck ("LCO_WithholdingCalc_ID", Integer.valueOf(LCO_WithholdingCalc_ID));
}
/** Get Withholding Calculation.
@return Withholding Calculation */
public int getLCO_WithholdingCalc_ID() 
{
Integer ii = (Integer)get_Value("LCO_WithholdingCalc_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Column name LCO_WithholdingCalc_ID */
public static final String COLUMNNAME_LCO_WithholdingCalc_ID = "LCO_WithholdingCalc_ID";
/** Set Withholding Type.
@param LCO_WithholdingType_ID Withholding Type */
public void setLCO_WithholdingType_ID (int LCO_WithholdingType_ID)
{
if (LCO_WithholdingType_ID <= 0) set_Value ("LCO_WithholdingType_ID", null);
 else 
set_Value ("LCO_WithholdingType_ID", Integer.valueOf(LCO_WithholdingType_ID));
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
/** Set Name.
@param Name Alphanumeric identifier of the entity */
public void setName (String Name)
{
if (Name == null) throw new IllegalArgumentException ("Name is mandatory.");
if (Name.length() > 60)
{
log.warning("Length > 60 - truncated");
Name = Name.substring(0,59);
}
set_Value ("Name", Name);
}
/** Get Name.
@return Alphanumeric identifier of the entity */
public String getName() 
{
return (String)get_Value("Name");
}
/** Get Record ID/ColumnName
@return ID/ColumnName pair
*/public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(get_ID(), getName());
}
/** Column name Name */
public static final String COLUMNNAME_Name = "Name";
/** Set Threshold min.
@param Thresholdmin Minimum gross amount for withholding calculation */
public void setThresholdmin (BigDecimal Thresholdmin)
{
set_Value ("Thresholdmin", Thresholdmin);
}
/** Get Threshold min.
@return Minimum gross amount for withholding calculation */
public BigDecimal getThresholdmin() 
{
BigDecimal bd = (BigDecimal)get_Value("Thresholdmin");
if (bd == null) return Env.ZERO;
return bd;
}
/** Column name Thresholdmin */
public static final String COLUMNNAME_Thresholdmin = "Thresholdmin";
}
