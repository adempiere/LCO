/******************************************************************************
 * Product: Adempiere ERP & CRM Smart Business Solution                       *
 * Copyright (C) 1999-2006 ComPiere, Inc. All Rights Reserved.                *
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
package org.adempiere.model;

import java.math.BigDecimal;
import java.sql.*;
import java.util.*;
import org.compiere.model.*;
import java.util.logging.*;

import org.compiere.util.*;

/**
 *	LCO_CalloutWithholding
 *
 *  @author Carlos Ruiz - globalqss - Quality Systems & Solutions - http://globalqss.com 
 *  @version  $Id: LCO_CalloutWithholding.java,v 1.3 2007/05/09 10:43:28 cruiz Exp $
 */
public class LCO_CalloutWithholding extends CalloutEngine
{
	/**
	 *	The string in the Callout field is: 
	 *  <code>org.compiere.model.LCO_CalloutWithholding.fillIsUse</code> 
	 *
	 *  @param ctx      Context
	 *  @param WindowNo current Window No
	 *  @param mTab     Model Tab
	 *  @param mField   Model Field
	 *  @param value    The new value
	 *  @param oldValue The old value
	 *	@return error message or "" if OK
	 */
	public String fillIsUse (Properties ctx, int WindowNo,
		GridTab mTab, GridField mField, Object value, Object oldValue)
	{
		log.info("");
		int wht_id = ((Integer) mTab.getValue("LCO_WithholdingType_ID")).intValue();
		
		String sql = "SELECT IsUseBPISIC, IsUseBPTaxPayerType, IsUseOrgISIC, IsUseOrgTaxPayerType, IsUseWithholdingCategory "
			           + "FROM LCO_WithholdingRuleConf WHERE LCO_WithholdingType_ID=?";		//	#1

		try
		{
			PreparedStatement pstmt = DB.prepareStatement(sql, null);
			pstmt.setInt(1, wht_id);
			ResultSet rs = pstmt.executeQuery();
			//
			if (rs.next()) {
				mTab.setValue("IsUseBPISIC", rs.getString("IsUseBPISIC"));
				mTab.setValue("IsUseBPTaxPayerType", rs.getString("IsUseBPTaxPayerType"));
				mTab.setValue("IsUseOrgISIC", rs.getString("IsUseOrgISIC"));
				mTab.setValue("IsUseOrgTaxPayerType", rs.getString("IsUseOrgTaxPayerType"));
				mTab.setValue("IsUseWithholdingCategory", rs.getString("IsUseWithholdingCategory"));
			} else {
				mTab.setValue("IsUseBPISIC", "N");
				mTab.setValue("IsUseBPTaxPayerType", "N");
				mTab.setValue("IsUseOrgISIC", "N");
				mTab.setValue("IsUseOrgTaxPayerType", "N");
				mTab.setValue("IsUseWithholdingCategory", "N");
				log.warning("Rule not configured for withholding type");
			}
			rs.close();
			pstmt.close();
		}
		catch (SQLException e)
		{
			log.log(Level.SEVERE, sql, e);
			return e.getLocalizedMessage();
		}

		return "";
	}	//	fillIsUse
	
	// Called from LCO_InvoiceWithholding.C_Tax_ID
	public String fillPercentFromTax (Properties ctx, int WindowNo,
			GridTab mTab, GridField mField, Object value, Object oldValue)
	{
		log.info("");
		int taxint = 0;
		BigDecimal percent = null;
		if (value != null)
			taxint = ((Integer) value).intValue();
		if (taxint != 0) {
			MTax tax = new MTax(ctx, taxint, null);
			percent = tax.getRate();
		}
		mTab.setValue(MLCOInvoiceWithholding.COLUMNNAME_Percent, percent);

		return recalc_taxamt(ctx, WindowNo, mTab, mField, value, oldValue);
	}	//	fillPercentFromTax

	// Called from LCO_InvoiceWithholding.C_Tax_ID and tax base
	public String recalc_taxamt(Properties ctx, int WindowNo,
			GridTab mTab, GridField mField, Object value, Object oldValue)
	{
		log.info("");

		BigDecimal percent = (BigDecimal) mTab.getValue(MLCOInvoiceWithholding.COLUMNNAME_Percent);
		BigDecimal taxbaseamt = (BigDecimal) mTab.getValue(MLCOInvoiceWithholding.COLUMNNAME_TaxBaseAmt);

		BigDecimal taxamt = null;
		if (percent != null && taxbaseamt != null)
			taxamt = percent.multiply(taxbaseamt).divide(Env.ONEHUNDRED);
		mTab.setValue(MLCOInvoiceWithholding.COLUMNNAME_TaxAmt, taxamt);

		return "";
	}	//	fillPercentFromTax

	// Called from C_Payment.C_Invoice_ID
	public String fillWriteOff (Properties ctx, int WindowNo,
			GridTab mTab, GridField mField, Object value, Object oldValue)
	{
		log.info("");
		if (isCalloutActive())
			return "";
		setCalloutActive(true);
		Integer invInt = (Integer) value;
		int inv_id = 0;
		if (value != null)
			inv_id = invInt.intValue();
		
		String sql = 
				"SELECT NVL(SUM(TaxAmt),0) "
				+ "  FROM LCO_InvoiceWithholding "
				+ " WHERE C_Invoice_ID = ? "
				+ "   AND IsCalcOnPayment = 'Y'"
				+ "   AND IsActive = 'Y'";

		try
		{
			PreparedStatement pstmt = DB.prepareStatement(sql, null);
			pstmt.setInt(1, inv_id);
			ResultSet rs = pstmt.executeQuery();
			//
			if (rs.next()) {
				mTab.setValue("WriteOffAmt", rs.getBigDecimal(1));
			} else {
				mTab.setValue("WriteOffAmt", Env.ZERO);
			}
			rs.close();
			pstmt.close();
		}
		catch (SQLException e)
		{
			log.log(Level.SEVERE, sql, e);
			setCalloutActive(false);
			return e.getLocalizedMessage();
		}

		setCalloutActive(false);
		return "";
	}	//	fillWriteOff

}	//	LCO_CalloutWithholding