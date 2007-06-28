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
/** Generated Model for LCO_InvoiceWithholding
 *  @author Adempiere (generated) 
 *  @version Release 3.2.0 - $Id: X_LCO_InvoiceWithholding.java,v 1.3 2007/05/09 10:43:45 cruiz Exp $ */
public class X_LCO_InvoiceWithholding extends PO
{
/** Standard Constructor
@param ctx context
@param LCO_InvoiceWithholding_ID id
@param trxName transaction
*/
public X_LCO_InvoiceWithholding (Properties ctx, int LCO_InvoiceWithholding_ID, String trxName)
{
super (ctx, LCO_InvoiceWithholding_ID, trxName);
/** if (LCO_InvoiceWithholding_ID == 0)
{
setC_Invoice_ID (0);
setIsTaxIncluded (false);	// N
setLCO_WithholdingType_ID (0);
setProcessed (false);
setTaxAmt (Env.ZERO);
setTaxBaseAmt (Env.ZERO);
}
 */
}
/** Load Constructor 
@param ctx context
@param rs result set 
@param trxName transaction
*/
public X_LCO_InvoiceWithholding (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=1000009 */
public static final int Table_ID=MTable.getTable_ID("LCO_InvoiceWithholding");

/** TableName=LCO_InvoiceWithholding */
public static final String Table_Name="LCO_InvoiceWithholding";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"LCO_InvoiceWithholding");

protected BigDecimal accessLevel = BigDecimal.valueOf(1);
/** AccessLevel
@return 1 - Org 
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
StringBuffer sb = new StringBuffer ("X_LCO_InvoiceWithholding[").append(get_ID()).append("]");
return sb.toString();
}
/** Set Invoice.
@param C_Invoice_ID Invoice Identifier */
public void setC_Invoice_ID (int C_Invoice_ID)
{
if (C_Invoice_ID < 1) throw new IllegalArgumentException ("C_Invoice_ID is mandatory.");
set_ValueNoCheck ("C_Invoice_ID", Integer.valueOf(C_Invoice_ID));
}
/** Get Invoice.
@return Invoice Identifier */
public int getC_Invoice_ID() 
{
Integer ii = (Integer)get_Value("C_Invoice_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Column name C_Invoice_ID */
public static final String COLUMNNAME_C_Invoice_ID = "C_Invoice_ID";
/** Set Tax.
@param C_Tax_ID Tax identifier */
public void setC_Tax_ID (int C_Tax_ID)
{
if (C_Tax_ID <= 0) set_ValueNoCheck ("C_Tax_ID", null);
 else 
set_ValueNoCheck ("C_Tax_ID", Integer.valueOf(C_Tax_ID));
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
/** Set Account Date.
@param DateAcct Accounting Date */
public void setDateAcct (Timestamp DateAcct)
{
set_Value ("DateAcct", DateAcct);
}
/** Get Account Date.
@return Accounting Date */
public Timestamp getDateAcct() 
{
return (Timestamp)get_Value("DateAcct");
}
/** Column name DateAcct */
public static final String COLUMNNAME_DateAcct = "DateAcct";
/** Set Transaction Date.
@param DateTrx Transaction Date */
public void setDateTrx (Timestamp DateTrx)
{
set_Value ("DateTrx", DateTrx);
}
/** Get Transaction Date.
@return Transaction Date */
public Timestamp getDateTrx() 
{
return (Timestamp)get_Value("DateTrx");
}
/** Column name DateTrx */
public static final String COLUMNNAME_DateTrx = "DateTrx";
/** Set Is Calc On Payment.
@param IsCalcOnPayment Is Calc On Payment */
public void setIsCalcOnPayment (boolean IsCalcOnPayment)
{
set_ValueNoCheck ("IsCalcOnPayment", Boolean.valueOf(IsCalcOnPayment));
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
/** Set Price includes Tax.
@param IsTaxIncluded Tax is included in the price  */
public void setIsTaxIncluded (boolean IsTaxIncluded)
{
set_Value ("IsTaxIncluded", Boolean.valueOf(IsTaxIncluded));
}
/** Get Price includes Tax.
@return Tax is included in the price  */
public boolean isTaxIncluded() 
{
Object oo = get_Value("IsTaxIncluded");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Column name IsTaxIncluded */
public static final String COLUMNNAME_IsTaxIncluded = "IsTaxIncluded";
/** Set Invoice Withholding.
@param LCO_InvoiceWithholding_ID Invoice Withholding */
public void setLCO_InvoiceWithholding_ID (int LCO_InvoiceWithholding_ID)
{
if (LCO_InvoiceWithholding_ID <= 0) set_ValueNoCheck ("LCO_InvoiceWithholding_ID", null);
 else 
set_ValueNoCheck ("LCO_InvoiceWithholding_ID", Integer.valueOf(LCO_InvoiceWithholding_ID));
}
/** Get Invoice Withholding.
@return Invoice Withholding */
public int getLCO_InvoiceWithholding_ID() 
{
Integer ii = (Integer)get_Value("LCO_InvoiceWithholding_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Column name LCO_InvoiceWithholding_ID */
public static final String COLUMNNAME_LCO_InvoiceWithholding_ID = "LCO_InvoiceWithholding_ID";
/** Set Withholding Rule.
@param LCO_WithholdingRule_ID Withholding Rule */
public void setLCO_WithholdingRule_ID (int LCO_WithholdingRule_ID)
{
if (LCO_WithholdingRule_ID <= 0) set_ValueNoCheck ("LCO_WithholdingRule_ID", null);
 else 
set_ValueNoCheck ("LCO_WithholdingRule_ID", Integer.valueOf(LCO_WithholdingRule_ID));
}
/** Get Withholding Rule.
@return Withholding Rule */
public int getLCO_WithholdingRule_ID() 
{
Integer ii = (Integer)get_Value("LCO_WithholdingRule_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Column name LCO_WithholdingRule_ID */
public static final String COLUMNNAME_LCO_WithholdingRule_ID = "LCO_WithholdingRule_ID";
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
/** Set Percent.
@param Percent Percentage */
public void setPercent (BigDecimal Percent)
{
set_Value ("Percent", Percent);
}
/** Get Percent.
@return Percentage */
public BigDecimal getPercent() 
{
BigDecimal bd = (BigDecimal)get_Value("Percent");
if (bd == null) return Env.ZERO;
return bd;
}
/** Column name Percent */
public static final String COLUMNNAME_Percent = "Percent";
/** Set Processed.
@param Processed The document has been processed */
public void setProcessed (boolean Processed)
{
set_Value ("Processed", Boolean.valueOf(Processed));
}
/** Get Processed.
@return The document has been processed */
public boolean isProcessed() 
{
Object oo = get_Value("Processed");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Column name Processed */
public static final String COLUMNNAME_Processed = "Processed";
/** Set Tax Amount.
@param TaxAmt Tax Amount for a document */
public void setTaxAmt (BigDecimal TaxAmt)
{
if (TaxAmt == null) throw new IllegalArgumentException ("TaxAmt is mandatory.");
set_Value ("TaxAmt", TaxAmt);
}
/** Get Tax Amount.
@return Tax Amount for a document */
public BigDecimal getTaxAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("TaxAmt");
if (bd == null) return Env.ZERO;
return bd;
}
/** Column name TaxAmt */
public static final String COLUMNNAME_TaxAmt = "TaxAmt";
/** Set Tax base Amount.
@param TaxBaseAmt Base for calculating the tax amount */
public void setTaxBaseAmt (BigDecimal TaxBaseAmt)
{
if (TaxBaseAmt == null) throw new IllegalArgumentException ("TaxBaseAmt is mandatory.");
set_Value ("TaxBaseAmt", TaxBaseAmt);
}
/** Get Tax base Amount.
@return Base for calculating the tax amount */
public BigDecimal getTaxBaseAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("TaxBaseAmt");
if (bd == null) return Env.ZERO;
return bd;
}
/** Column name TaxBaseAmt */
public static final String COLUMNNAME_TaxBaseAmt = "TaxBaseAmt";
}
