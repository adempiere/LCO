/******************************************************************************
 * Product: Adempiere ERP & CRM Smart Business Solution                       *
 * Copyright (C) 1999-2007 ComPiere, Inc. All Rights Reserved.                *
 * This program is free software; you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program; if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 * For the text or an alternative of this public license, you may reach us    *
 * ComPiere, Inc., 2620 Augustine Dr. #245, Santa Clara, CA 95054, USA        *
 * or via info@compiere.org or http://www.compiere.org/license.html           *
 *****************************************************************************/
/** Generated Model - DO NOT CHANGE */
package org.compiere.model;

import java.lang.reflect.Constructor;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Properties;
import java.util.logging.Level;
import org.compiere.util.KeyNamePair;

/** Generated Model for LCO_WithholdingRule
 *  @author Adempiere (generated) 
 *  @version Release 3.4.0s - $Id$ */
public class X_LCO_WithholdingRule extends PO implements I_LCO_WithholdingRule, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

    /** Standard Constructor */
    public X_LCO_WithholdingRule (Properties ctx, int LCO_WithholdingRule_ID, String trxName)
    {
      super (ctx, LCO_WithholdingRule_ID, trxName);
      /** if (LCO_WithholdingRule_ID == 0)
        {
			setLCO_WithholdingRule_ID (0);
			setName (null);
			setValidFrom (new Timestamp(System.currentTimeMillis()));
        } */
    }

    /** Load Constructor */
    public X_LCO_WithholdingRule (Properties ctx, ResultSet rs, String trxName)
    {
      super (ctx, rs, trxName);
    }

    /** AccessLevel
      * @return 2 - Client 
      */
    protected int get_AccessLevel()
    {
      return accessLevel.intValue();
    }

    /** Load Meta Data */
    protected POInfo initPO (Properties ctx)
    {
      POInfo poi = POInfo.getPOInfo (ctx, Table_ID, get_TrxName());
      return poi;
    }

    public String toString()
    {
      StringBuffer sb = new StringBuffer ("X_LCO_WithholdingRule[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	public I_C_TaxCategory getC_TaxCategory() throws Exception 
    {
        Class<?> clazz = MTable.getClass(I_C_TaxCategory.Table_Name);
        I_C_TaxCategory result = null;
        try	{
	        Constructor<?> constructor = null;
	    	constructor = clazz.getDeclaredConstructor(new Class[]{Properties.class, int.class, String.class});
    	    result = (I_C_TaxCategory)constructor.newInstance(new Object[] {getCtx(), new Integer(getC_TaxCategory_ID()), get_TrxName()});
        } catch (Exception e) {
	        log.log(Level.SEVERE, "(id) - Table=" + Table_Name + ",Class=" + clazz, e);
	        log.saveError("Error", "Table=" + Table_Name + ",Class=" + clazz);
           throw e;
        }
        return result;
    }

	/** Set Tax Category.
		@param C_TaxCategory_ID 
		Tax Category
	  */
	public void setC_TaxCategory_ID (int C_TaxCategory_ID)
	{
		if (C_TaxCategory_ID < 1) 
			set_Value (COLUMNNAME_C_TaxCategory_ID, null);
		else 
			set_Value (COLUMNNAME_C_TaxCategory_ID, Integer.valueOf(C_TaxCategory_ID));
	}

	/** Get Tax Category.
		@return Tax Category
	  */
	public int getC_TaxCategory_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_TaxCategory_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Description.
		@param Description 
		Optional short description of the record
	  */
	public void setDescription (String Description)
	{

		if (Description != null && Description.length() > 255)
		{
			log.warning("Length > 255 - truncated");
			Description = Description.substring(0, 255);
		}
		set_Value (COLUMNNAME_Description, Description);
	}

	/** Get Description.
		@return Optional short description of the record
	  */
	public String getDescription () 
	{
		return (String)get_Value(COLUMNNAME_Description);
	}

	/** Set Default.
		@param IsDefault 
		Default value
	  */
	public void setIsDefault (boolean IsDefault)
	{
		set_Value (COLUMNNAME_IsDefault, Boolean.valueOf(IsDefault));
	}

	/** Get Default.
		@return Default value
	  */
	public boolean isDefault () 
	{
		Object oo = get_Value(COLUMNNAME_IsDefault);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Is Use BP ISIC.
		@param IsUseBPISIC Is Use BP ISIC	  */
	public void setIsUseBPISIC (boolean IsUseBPISIC)
	{
		throw new IllegalArgumentException ("IsUseBPISIC is virtual column");	}

	/** Get Is Use BP ISIC.
		@return Is Use BP ISIC	  */
	public boolean isUseBPISIC () 
	{
		Object oo = get_Value(COLUMNNAME_IsUseBPISIC);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Is Use BP Tax Payer Type.
		@param IsUseBPTaxPayerType Is Use BP Tax Payer Type	  */
	public void setIsUseBPTaxPayerType (boolean IsUseBPTaxPayerType)
	{
		throw new IllegalArgumentException ("IsUseBPTaxPayerType is virtual column");	}

	/** Get Is Use BP Tax Payer Type.
		@return Is Use BP Tax Payer Type	  */
	public boolean isUseBPTaxPayerType () 
	{
		Object oo = get_Value(COLUMNNAME_IsUseBPTaxPayerType);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Is Use Org ISIC.
		@param IsUseOrgISIC Is Use Org ISIC	  */
	public void setIsUseOrgISIC (boolean IsUseOrgISIC)
	{
		throw new IllegalArgumentException ("IsUseOrgISIC is virtual column");	}

	/** Get Is Use Org ISIC.
		@return Is Use Org ISIC	  */
	public boolean isUseOrgISIC () 
	{
		Object oo = get_Value(COLUMNNAME_IsUseOrgISIC);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Is Use Org Tax Payer Type.
		@param IsUseOrgTaxPayerType Is Use Org Tax Payer Type	  */
	public void setIsUseOrgTaxPayerType (boolean IsUseOrgTaxPayerType)
	{
		throw new IllegalArgumentException ("IsUseOrgTaxPayerType is virtual column");	}

	/** Get Is Use Org Tax Payer Type.
		@return Is Use Org Tax Payer Type	  */
	public boolean isUseOrgTaxPayerType () 
	{
		Object oo = get_Value(COLUMNNAME_IsUseOrgTaxPayerType);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Is Use Product Tax Category.
		@param IsUseProductTaxCategory Is Use Product Tax Category	  */
	public void setIsUseProductTaxCategory (boolean IsUseProductTaxCategory)
	{
		throw new IllegalArgumentException ("IsUseProductTaxCategory is virtual column");	}

	/** Get Is Use Product Tax Category.
		@return Is Use Product Tax Category	  */
	public boolean isUseProductTaxCategory () 
	{
		Object oo = get_Value(COLUMNNAME_IsUseProductTaxCategory);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Is Use Withholding Category.
		@param IsUseWithholdingCategory Is Use Withholding Category	  */
	public void setIsUseWithholdingCategory (boolean IsUseWithholdingCategory)
	{
		throw new IllegalArgumentException ("IsUseWithholdingCategory is virtual column");	}

	/** Get Is Use Withholding Category.
		@return Is Use Withholding Category	  */
	public boolean isUseWithholdingCategory () 
	{
		Object oo = get_Value(COLUMNNAME_IsUseWithholdingCategory);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** LCO_BP_ISIC_ID AD_Reference_ID=1000001 */
	public static final int LCO_BP_ISIC_ID_AD_Reference_ID=1000001;
	/** Set ISIC Business Partner.
		@param LCO_BP_ISIC_ID ISIC Business Partner	  */
	public void setLCO_BP_ISIC_ID (int LCO_BP_ISIC_ID)
	{
		if (LCO_BP_ISIC_ID < 1) 
			set_Value (COLUMNNAME_LCO_BP_ISIC_ID, null);
		else 
			set_Value (COLUMNNAME_LCO_BP_ISIC_ID, Integer.valueOf(LCO_BP_ISIC_ID));
	}

	/** Get ISIC Business Partner.
		@return ISIC Business Partner	  */
	public int getLCO_BP_ISIC_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_LCO_BP_ISIC_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** LCO_BP_TaxPayerType_ID AD_Reference_ID=1000002 */
	public static final int LCO_BP_TAXPAYERTYPE_ID_AD_Reference_ID=1000002;
	/** Set Tax Payer Type Business Partner.
		@param LCO_BP_TaxPayerType_ID Tax Payer Type Business Partner	  */
	public void setLCO_BP_TaxPayerType_ID (int LCO_BP_TaxPayerType_ID)
	{
		if (LCO_BP_TaxPayerType_ID < 1) 
			set_Value (COLUMNNAME_LCO_BP_TaxPayerType_ID, null);
		else 
			set_Value (COLUMNNAME_LCO_BP_TaxPayerType_ID, Integer.valueOf(LCO_BP_TaxPayerType_ID));
	}

	/** Get Tax Payer Type Business Partner.
		@return Tax Payer Type Business Partner	  */
	public int getLCO_BP_TaxPayerType_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_LCO_BP_TaxPayerType_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** LCO_Org_ISIC_ID AD_Reference_ID=1000001 */
	public static final int LCO_ORG_ISIC_ID_AD_Reference_ID=1000001;
	/** Set ISIC Organization.
		@param LCO_Org_ISIC_ID ISIC Organization	  */
	public void setLCO_Org_ISIC_ID (int LCO_Org_ISIC_ID)
	{
		if (LCO_Org_ISIC_ID < 1) 
			set_Value (COLUMNNAME_LCO_Org_ISIC_ID, null);
		else 
			set_Value (COLUMNNAME_LCO_Org_ISIC_ID, Integer.valueOf(LCO_Org_ISIC_ID));
	}

	/** Get ISIC Organization.
		@return ISIC Organization	  */
	public int getLCO_Org_ISIC_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_LCO_Org_ISIC_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** LCO_Org_TaxPayerType_ID AD_Reference_ID=1000002 */
	public static final int LCO_ORG_TAXPAYERTYPE_ID_AD_Reference_ID=1000002;
	/** Set Tax Payer Type Organization.
		@param LCO_Org_TaxPayerType_ID Tax Payer Type Organization	  */
	public void setLCO_Org_TaxPayerType_ID (int LCO_Org_TaxPayerType_ID)
	{
		if (LCO_Org_TaxPayerType_ID < 1) 
			set_Value (COLUMNNAME_LCO_Org_TaxPayerType_ID, null);
		else 
			set_Value (COLUMNNAME_LCO_Org_TaxPayerType_ID, Integer.valueOf(LCO_Org_TaxPayerType_ID));
	}

	/** Get Tax Payer Type Organization.
		@return Tax Payer Type Organization	  */
	public int getLCO_Org_TaxPayerType_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_LCO_Org_TaxPayerType_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_LCO_WithholdingCalc getLCO_WithholdingCalc() throws Exception 
    {
        Class<?> clazz = MTable.getClass(I_LCO_WithholdingCalc.Table_Name);
        I_LCO_WithholdingCalc result = null;
        try	{
	        Constructor<?> constructor = null;
	    	constructor = clazz.getDeclaredConstructor(new Class[]{Properties.class, int.class, String.class});
    	    result = (I_LCO_WithholdingCalc)constructor.newInstance(new Object[] {getCtx(), new Integer(getLCO_WithholdingCalc_ID()), get_TrxName()});
        } catch (Exception e) {
	        log.log(Level.SEVERE, "(id) - Table=" + Table_Name + ",Class=" + clazz, e);
	        log.saveError("Error", "Table=" + Table_Name + ",Class=" + clazz);
           throw e;
        }
        return result;
    }

	/** Set Withholding Calculation.
		@param LCO_WithholdingCalc_ID Withholding Calculation	  */
	public void setLCO_WithholdingCalc_ID (int LCO_WithholdingCalc_ID)
	{
		if (LCO_WithholdingCalc_ID < 1) 
			set_Value (COLUMNNAME_LCO_WithholdingCalc_ID, null);
		else 
			set_Value (COLUMNNAME_LCO_WithholdingCalc_ID, Integer.valueOf(LCO_WithholdingCalc_ID));
	}

	/** Get Withholding Calculation.
		@return Withholding Calculation	  */
	public int getLCO_WithholdingCalc_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_LCO_WithholdingCalc_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_LCO_WithholdingCategory getLCO_WithholdingCategory() throws Exception 
    {
        Class<?> clazz = MTable.getClass(I_LCO_WithholdingCategory.Table_Name);
        I_LCO_WithholdingCategory result = null;
        try	{
	        Constructor<?> constructor = null;
	    	constructor = clazz.getDeclaredConstructor(new Class[]{Properties.class, int.class, String.class});
    	    result = (I_LCO_WithholdingCategory)constructor.newInstance(new Object[] {getCtx(), new Integer(getLCO_WithholdingCategory_ID()), get_TrxName()});
        } catch (Exception e) {
	        log.log(Level.SEVERE, "(id) - Table=" + Table_Name + ",Class=" + clazz, e);
	        log.saveError("Error", "Table=" + Table_Name + ",Class=" + clazz);
           throw e;
        }
        return result;
    }

	/** Set Withholding Category.
		@param LCO_WithholdingCategory_ID Withholding Category	  */
	public void setLCO_WithholdingCategory_ID (int LCO_WithholdingCategory_ID)
	{
		if (LCO_WithholdingCategory_ID < 1) 
			set_Value (COLUMNNAME_LCO_WithholdingCategory_ID, null);
		else 
			set_Value (COLUMNNAME_LCO_WithholdingCategory_ID, Integer.valueOf(LCO_WithholdingCategory_ID));
	}

	/** Get Withholding Category.
		@return Withholding Category	  */
	public int getLCO_WithholdingCategory_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_LCO_WithholdingCategory_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Withholding Rule.
		@param LCO_WithholdingRule_ID Withholding Rule	  */
	public void setLCO_WithholdingRule_ID (int LCO_WithholdingRule_ID)
	{
		if (LCO_WithholdingRule_ID < 1)
			 throw new IllegalArgumentException ("LCO_WithholdingRule_ID is mandatory.");
		set_ValueNoCheck (COLUMNNAME_LCO_WithholdingRule_ID, Integer.valueOf(LCO_WithholdingRule_ID));
	}

	/** Get Withholding Rule.
		@return Withholding Rule	  */
	public int getLCO_WithholdingRule_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_LCO_WithholdingRule_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_LCO_WithholdingType getLCO_WithholdingType() throws Exception 
    {
        Class<?> clazz = MTable.getClass(I_LCO_WithholdingType.Table_Name);
        I_LCO_WithholdingType result = null;
        try	{
	        Constructor<?> constructor = null;
	    	constructor = clazz.getDeclaredConstructor(new Class[]{Properties.class, int.class, String.class});
    	    result = (I_LCO_WithholdingType)constructor.newInstance(new Object[] {getCtx(), new Integer(getLCO_WithholdingType_ID()), get_TrxName()});
        } catch (Exception e) {
	        log.log(Level.SEVERE, "(id) - Table=" + Table_Name + ",Class=" + clazz, e);
	        log.saveError("Error", "Table=" + Table_Name + ",Class=" + clazz);
           throw e;
        }
        return result;
    }

	/** Set Withholding Type.
		@param LCO_WithholdingType_ID Withholding Type	  */
	public void setLCO_WithholdingType_ID (int LCO_WithholdingType_ID)
	{
		if (LCO_WithholdingType_ID < 1) 
			set_Value (COLUMNNAME_LCO_WithholdingType_ID, null);
		else 
			set_Value (COLUMNNAME_LCO_WithholdingType_ID, Integer.valueOf(LCO_WithholdingType_ID));
	}

	/** Get Withholding Type.
		@return Withholding Type	  */
	public int getLCO_WithholdingType_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_LCO_WithholdingType_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Name.
		@param Name 
		Alphanumeric identifier of the entity
	  */
	public void setName (String Name)
	{
		if (Name == null)
			throw new IllegalArgumentException ("Name is mandatory.");

		if (Name.length() > 60)
		{
			log.warning("Length > 60 - truncated");
			Name = Name.substring(0, 60);
		}
		set_Value (COLUMNNAME_Name, Name);
	}

	/** Get Name.
		@return Alphanumeric identifier of the entity
	  */
	public String getName () 
	{
		return (String)get_Value(COLUMNNAME_Name);
	}

    /** Get Record ID/ColumnName
        @return ID/ColumnName pair
      */
    public KeyNamePair getKeyNamePair() 
    {
        return new KeyNamePair(get_ID(), getName());
    }

	/** Set Valid from.
		@param ValidFrom 
		Valid from including this date (first day)
	  */
	public void setValidFrom (Timestamp ValidFrom)
	{
		if (ValidFrom == null)
			throw new IllegalArgumentException ("ValidFrom is mandatory.");
		set_Value (COLUMNNAME_ValidFrom, ValidFrom);
	}

	/** Get Valid from.
		@return Valid from including this date (first day)
	  */
	public Timestamp getValidFrom () 
	{
		return (Timestamp)get_Value(COLUMNNAME_ValidFrom);
	}
}