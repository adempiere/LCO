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

import java.math.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;

import org.compiere.acct.Fact;
import org.compiere.acct.FactLine;
import org.compiere.acct.Doc;
import org.compiere.acct.DocLine;
import org.compiere.acct.DocTax;
import org.compiere.model.*;
import org.compiere.util.*;


/**
 *	Validator Example Implementation
 *	
 *  @author Carlos Ruiz - globalqss - Quality Systems & Solutions - http://globalqss.com 
 *	@version $Id: LCO_Validator.java,v 1.4 2007/05/13 06:53:26 cruiz Exp $
 */
public class LCO_Validator implements ModelValidator
{
	/**
	 *	Constructor.
	 *	The class is instanciated when logging in and client is selected/known
	 */
	public LCO_Validator ()
	{
		super ();
	}	//	MyValidator
	
	/**	Logger			*/
	private static CLogger log = CLogger.getCLogger(LCO_Validator.class);
	/** Client			*/
	private int		m_AD_Client_ID = -1;
	
	/**
	 *	Initialize Validation
	 *	@param engine validation engine 
	 *	@param client client
	 */
	public void initialize (ModelValidationEngine engine, MClient client)
	{
		m_AD_Client_ID = client.getAD_Client_ID();
		log.info(client.toString());

		//	Tables to be monitored
		engine.addModelChange(X_C_Invoice.Table_Name, this);
		engine.addModelChange(X_C_InvoiceLine.Table_Name, this);
		engine.addModelChange(X_LCO_InvoiceWithholding.Table_Name, this);

		//	Documents to be monitored
		engine.addDocValidate(X_C_Invoice.Table_Name, this);
		engine.addDocValidate(X_C_AllocationHdr.Table_Name, this);

	}	//	initialize

    /**
     *	Model Change of a monitored Table.
     *	Called after PO.beforeSave/PO.beforeDelete
     *	when you called addModelChange for the table
     *	@param po persistent object
     *	@param type TYPE_
     *	@return error message or null
     *	@exception Exception if the recipient wishes the change to be not accept.
     */
	public String modelChange (PO po, int type) throws Exception
	{
		log.info(po.get_TableName() + " Type: "+type);
		String msg;

		if (po.get_TableName().equals(X_C_Invoice.Table_Name) && type == ModelValidator.TYPE_BEFORE_CHANGE) {
			msg = clearInvoiceWithholdingAmtFromInvoice((MInvoice) po);
			if (msg != null)
				return msg;
		}

		if (po.get_TableName().equals(X_C_InvoiceLine.Table_Name) &&
				(type == ModelValidator.TYPE_BEFORE_NEW ||
				 type == ModelValidator.TYPE_BEFORE_CHANGE ||
				 type == ModelValidator.TYPE_BEFORE_DELETE
				)
			)
		{
			msg = clearInvoiceWithholdingAmtFromInvoiceLine((MInvoiceLine) po);
			if (msg != null)
				return msg;
		}

		if (po.get_TableName().equals(X_LCO_InvoiceWithholding.Table_Name) &&
				(type == ModelValidator.TYPE_BEFORE_NEW ||
				 type == ModelValidator.TYPE_BEFORE_CHANGE ||
				 type == ModelValidator.TYPE_BEFORE_DELETE
				)
			)
		{
			if (type == ModelValidator.TYPE_BEFORE_NEW ||
				type == ModelValidator.TYPE_BEFORE_CHANGE)
				fillFields((X_LCO_InvoiceWithholding) po);
			
			msg = recalcInvoiceWithholdingAmt((X_LCO_InvoiceWithholding) po);
			if (msg != null)
				return msg;
		}

		if (po.get_TableName().equals(X_LCO_InvoiceWithholding.Table_Name) &&
				(type == ModelValidator.TYPE_BEFORE_NEW ||
				 type == ModelValidator.TYPE_BEFORE_CHANGE ||
				 type == ModelValidator.TYPE_BEFORE_DELETE
				)
			)
		{
			if (type == ModelValidator.TYPE_BEFORE_NEW ||
				type == ModelValidator.TYPE_BEFORE_CHANGE)
				fillFields((X_LCO_InvoiceWithholding) po);
			
			msg = recalcInvoiceWithholdingAmt((X_LCO_InvoiceWithholding) po);
			if (msg != null)
				return msg;
		}

		return null;
	}	//	modelChange
	
	private void fillFields(X_LCO_InvoiceWithholding iwh) {
		
		if (iwh.getLCO_WithholdingRule_ID() > 0) {

			// Fill isCalcOnPayment according to rule
			X_LCO_WithholdingRule wr = new X_LCO_WithholdingRule(iwh.getCtx(), iwh.getLCO_WithholdingRule_ID(), iwh.get_TrxName());
			X_LCO_WithholdingCalc wc = new X_LCO_WithholdingCalc(iwh.getCtx(), wr.getLCO_WithholdingCalc_ID(), iwh.get_TrxName());
			iwh.setIsCalcOnPayment( ! wc.isCalcOnInvoice() );

		} else {

			// Fill isCalcOnPayment according to isSOTrx on type
			X_LCO_WithholdingType wt = new X_LCO_WithholdingType (iwh.getCtx(), iwh.getLCO_WithholdingType_ID(), iwh.get_TrxName());
			// set on payment for sales, and on invoice for purchases
			iwh.setIsCalcOnPayment(wt.isSOTrx());

		}

		// Fill DateTrx and DateAcct for isCalcOnInvoice according to the invoice
		MInvoice inv = new MInvoice(iwh.getCtx(), iwh.getC_Invoice_ID(), iwh.get_TrxName());
		iwh.setDateAcct(inv.getDateAcct());
		iwh.setDateTrx(inv.getDateInvoiced());
		
	}

	private String clearInvoiceWithholdingAmtFromInvoice(MInvoice inv) {
		// Clear invoice withholding amount
		
		if (inv.is_ValueChanged("AD_Org_ID") || inv.is_ValueChanged("C_BPartner_ID")) {
			
			boolean thereAreCalc;
			try {
				thereAreCalc = thereAreCalc(inv);
			} catch (SQLException e) {
				log.log(Level.SEVERE, "Error looking for calc on invoice rules", e);
				return "Error looking for calc on invoice rules";
			}
			
			BigDecimal curWithholdingAmt = (BigDecimal) inv.get_Value("WithholdingAmt");
			if (thereAreCalc) {
				if (curWithholdingAmt != null) {
					inv.set_CustomColumn("WithholdingAmt", null);
				}
			} else {
				if (curWithholdingAmt == null) {
					inv.set_CustomColumn("WithholdingAmt", Env.ZERO);
				}
			}

		}
		
		return null;
	}

	private String clearInvoiceWithholdingAmtFromInvoiceLine(MInvoiceLine invline) {
		// Clear invoice withholding amount
		MInvoice inv = invline.getParent();
		
		boolean thereAreCalc;
		try {
			thereAreCalc = thereAreCalc(inv);
		} catch (SQLException e) {
			log.log(Level.SEVERE, "Error looking for calc on invoice rules", e);
			return "Error looking for calc on invoice rules";
		}
		
		BigDecimal curWithholdingAmt = (BigDecimal) inv.get_Value("WithholdingAmt");
		if (thereAreCalc) {
			if (curWithholdingAmt != null) {
				inv.set_CustomColumn("WithholdingAmt", null);
				inv.save();
			}
		} else {
			if (curWithholdingAmt == null) {
				inv.set_CustomColumn("WithholdingAmt", Env.ZERO);
				inv.save();
			}
		}
		return null;
	}

	private boolean thereAreCalc(MInvoice inv) throws SQLException {
		boolean thereAreCalc = false;
		String sqlwccoi = 
			"SELECT 1 "
			+ "  FROM LCO_WithholdingType wt, LCO_WithholdingCalc wc "
			+ " WHERE wt.LCO_WithholdingType_ID = wc.LCO_WithholdingType_ID";
		PreparedStatement pstmtwccoi = DB.prepareStatement(sqlwccoi, inv.get_TrxName());
		ResultSet rswccoi = pstmtwccoi.executeQuery();
		if (rswccoi.next())
			thereAreCalc = true;
		rswccoi.close();
		pstmtwccoi.close();
		return thereAreCalc;
	}

	private String recalcInvoiceWithholdingAmt(X_LCO_InvoiceWithholding invwh) {
		LCO_MInvoice inv = new LCO_MInvoice(invwh.getCtx(), invwh.getC_Invoice_ID(), invwh.get_TrxName());

		try {
			inv.recalcWithholdingAmount();
			inv.save();
		} catch (SQLException e) {
			log.log(Level.SEVERE, "Error recalculating invoice withholding amount", e);
			return "Error recalculating invoice withholding amount";
		}
		
		return null;
	}

	/**
	 *	Validate Document.
	 *	Called as first step of DocAction.prepareIt 
     *	when you called addDocValidate for the table.
     *	Note that totals, etc. may not be correct.
	 *	@param po persistent object
	 *	@param timing see TIMING_ constants
     *	@return error message or null
	 */
	public String docValidate (PO po, int timing)
	{
		log.info(po.get_TableName() + " Timing: "+timing);
		String msg;

		if (po.get_TableName().equals(X_C_Invoice.Table_Name) && timing == TIMING_BEFORE_PREPARE) {
			MInvoice inv = (MInvoice) po;
			if (inv.get_Value("WithholdingAmt") == null) {
				// 20070803 - globalqss - Carlos Ruiz
				// Withholding must be generated for generated sales invoices
				// in sales invoices the withholdings can be edited in payment receipt
				// Not generate if the auto-generation comes from a POS Order (it's supposed the money was received)
				if (!inv.isSOTrx()) {
					// purchase invoice - must generate withholding
					return "@WithholdingNotGenerated@";
				} else {
					// sales order
					if (inv.getC_Order_ID() > 0) {
						MOrder ord = new MOrder (inv.getCtx(), inv.getC_Order_ID(), inv.get_TrxName());
						MDocType dt = new MDocType (inv.getCtx(), ord.getC_DocTypeTarget_ID(), inv.get_TrxName());
						if (dt.getDocBaseType().equals(MDocType.DOCBASETYPE_SalesOrder)
								&& dt.getDocSubTypeSO().equals(MDocType.DOCSUBTYPESO_POSOrder)) {
							// is a POS Order don't generate
							inv.set_CustomColumn("WithholdingAmt", new BigDecimal(0));
						} else {
							// is not a POS Order, generate withholdings
							LCO_MInvoice lcoinv = new LCO_MInvoice(inv.getCtx(), inv.getC_Invoice_ID(), inv.get_TrxName());
							lcoinv.recalcWithholdings();
						}						
					}
				}
			}
		}

		if (po.get_TableName().equals(X_C_Invoice.Table_Name) && timing == TIMING_AFTER_PREPARE) {
			msg = translateWithholdingToTaxes((MInvoice) po);
			if (msg != null)
				return msg;
		}

		if (po.get_TableName().equals(X_C_Invoice.Table_Name) && timing == TIMING_AFTER_COMPLETE) {
			msg = completeInvoiceWithholding((MInvoice) po);
			if (msg != null)
				return msg;
		}

		if (po.get_TableName().equals(X_C_AllocationHdr.Table_Name) && timing == TIMING_BEFORE_POST) {
			msg = accountingForInvoiceWithholdingOnPayment((MAllocationHdr) po);
			if (msg != null)
				return msg;
		}

		return null;
	}	//	docValidate
	
	private String accountingForInvoiceWithholdingOnPayment(MAllocationHdr ah) {
		// Accounting like Doc_Allocation
		// (Write off) vs (invoice withholding where iscalconpayment=Y)
		
		Doc doc = ah.getDoc();
		
		ArrayList<Fact> facts = doc.getFacts();
		// one fact per acctschema
		for (int i = 0; i < facts.size(); i++)
		{
			Fact fact = facts.get(i);
			MAcctSchema as = fact.getAcctSchema();
			
			MAllocationLine[] alloc_lines = ah.getLines(false);
			for (int j = 0; j < alloc_lines.length; j++) {
				BigDecimal tottax = new BigDecimal(0);
				
				MAllocationLine alloc_line = alloc_lines[j];
				doc.setC_BPartner_ID(alloc_line.getC_BPartner_ID());
				
				int inv_id = alloc_line.getC_Invoice_ID();
				if (inv_id <= 0)
					continue;
				MInvoice invoice = null;
				invoice = new MInvoice (ah.getCtx(), alloc_line.getC_Invoice_ID(), ah.get_TrxName());
				if (invoice == null)
					continue;
				String sql = 
					  "SELECT i.C_Tax_ID, NVL(SUM(i.TaxBaseAmt),0) AS TaxBaseAmt, NVL(SUM(i.TaxAmt),0) AS TaxAmt, t.Name, t.Rate, t.IsSalesTax "
					 + " FROM LCO_InvoiceWithholding i, C_Tax t "
					+ " WHERE i.C_Invoice_ID = ? AND i.IsCalcOnPayment = 'Y' AND i.IsActive = 'Y' "
					  + " AND i.C_Tax_ID = t.C_Tax_ID "
					+ "GROUP BY i.C_Tax_ID, t.NAME, t.Rate, t.IsSalesTax";
				PreparedStatement pstmt = null;
				try
				{
					pstmt = DB.prepareStatement(sql, ah.get_TrxName());
					pstmt.setInt(1, invoice.getC_Invoice_ID());
					ResultSet rs = pstmt.executeQuery();
					while (rs.next()) {
						int tax_ID = rs.getInt(1);
						BigDecimal taxBaseAmt = rs.getBigDecimal(2);
						BigDecimal amount = rs.getBigDecimal(3);
						String name = rs.getString(4);
						BigDecimal rate = rs.getBigDecimal(5);
						boolean salesTax = rs.getString(6).equals("Y") ? true : false;
						
						DocTax taxLine = new DocTax(tax_ID, name, rate, 
								taxBaseAmt, amount, salesTax);
						
						if (amount != null && amount.signum() != 0)
						{
							FactLine tl = null;
							if (invoice.isSOTrx()) {
								tl = fact.createLine(null, taxLine.getAccount(DocTax.ACCTTYPE_TaxDue, as),
										as.getC_Currency_ID(), amount, null);
							} else {
								tl = fact.createLine(null, taxLine.getAccount(taxLine.getAPTaxType(), as),
										as.getC_Currency_ID(), null, amount);
							}
							if (tl != null)
								tl.setC_Tax_ID(taxLine.getC_Tax_ID());
							tottax = tottax.add(amount);
						}
					}

					rs.close();
					pstmt.close();
					pstmt = null;
				}
				catch (Exception e)
				{
					log.log(Level.SEVERE, sql, e);
					return "Error posting C_InvoiceTax from LCO_InvoiceWithholding";
				}
				try
				{
					if (pstmt != null)
						pstmt.close();
					pstmt = null;
				}
				catch (Exception e)
				{
					pstmt = null;
				}
				//	Write off		DR
				if (Env.ZERO.compareTo(tottax) != 0)
				{
					DocLine line = new DocLine(alloc_line, doc);
					FactLine fl = null;
					if (invoice.isSOTrx()) {
						fl = fact.createLine (line, doc.getAccount(Doc.ACCTTYPE_WriteOff, as),
								as.getC_Currency_ID(), null, tottax);
					} else {
						fl = fact.createLine (line, doc.getAccount(Doc.ACCTTYPE_WriteOff, as),
								as.getC_Currency_ID(), tottax, null);
					}
					if (fl != null)
						fl.setAD_Org_ID(ah.getAD_Org_ID());
				}
				
			}

		}

		return null;
	}

	private String completeInvoiceWithholding(MInvoice inv) {
		
		// Fill DateAcct and DateTrx with final dates from Invoice
		String upd_dates =
			"UPDATE LCO_InvoiceWithholding "
			 + "   SET DateAcct = "
			 + "          (SELECT DateAcct "
			 + "             FROM C_Invoice "
			 + "            WHERE C_Invoice.C_Invoice_ID = LCO_InvoiceWithholding.C_Invoice_ID), "
			 + "       DateTrx = "
			 + "          (SELECT DateInvoiced "
			 + "             FROM C_Invoice "
			 + "            WHERE C_Invoice.C_Invoice_ID = LCO_InvoiceWithholding.C_Invoice_ID) "
			 + " WHERE C_Invoice_ID = ? ";
		int noupddates = DB.executeUpdate(upd_dates, inv.getC_Invoice_ID(), inv.get_TrxName());
		if (noupddates == -1)
			return "Error updating dates on invoice withholding";

		// Set processed for isCalcOnInvoice records
		String upd_proc =
			"UPDATE LCO_InvoiceWithholding "
			 + "   SET Processed = 'Y' "
			 + " WHERE C_Invoice_ID = ? AND IsCalcOnPayment = 'N'";
		int noupdproc = DB.executeUpdate(upd_proc, inv.getC_Invoice_ID(), inv.get_TrxName());
		if (noupdproc == -1)
			return "Error updating processed on invoice withholding";

		return null;
	}

	private String translateWithholdingToTaxes(MInvoice inv) {
		BigDecimal sumit = new BigDecimal(0);
		
		String sql = 
			  "SELECT C_Tax_ID, NVL(SUM(TaxBaseAmt),0) AS TaxBaseAmt, NVL(SUM(TaxAmt),0) AS TaxAmt "
			 + " FROM LCO_InvoiceWithholding "
			+ " WHERE C_Invoice_ID = ? AND IsCalcOnPayment = 'N' AND IsActive = 'Y' "
			+ "GROUP BY C_Tax_ID";
		PreparedStatement pstmt = null;
		try
		{
			pstmt = DB.prepareStatement(sql, inv.get_TrxName());
			pstmt.setInt(1, inv.getC_Invoice_ID());
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				MInvoiceTax it = new MInvoiceTax(inv.getCtx(), 0, inv.get_TrxName());
				it.setC_Invoice_ID(inv.getC_Invoice_ID());
				it.setC_Tax_ID(rs.getInt(1));
				it.setTaxBaseAmt(rs.getBigDecimal(2));
				it.setTaxAmt(rs.getBigDecimal(3).negate());
				sumit = sumit.add(rs.getBigDecimal(3));
				it.save();
			}
			inv.set_CustomColumn("WithholdingAmt", sumit);
			// Subtract to invoice grand total the value of withholdings
			BigDecimal gt = inv.getGrandTotal();
			inv.setGrandTotal(gt.subtract(sumit));

			rs.close();
			pstmt.close();
			pstmt = null;
		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, sql, e);
			return "Error creating C_InvoiceTax from LCO_InvoiceWithholding";
		}
		try
		{
			if (pstmt != null)
				pstmt.close();
			pstmt = null;
		}
		catch (Exception e)
		{
			pstmt = null;
		}

		return null;
	}

	/**
	 *	User Login.
	 *	Called when preferences are set
	 *	@param AD_Org_ID org
	 *	@param AD_Role_ID role
	 *	@param AD_User_ID user
	 *	@return error message or null
	 */
	public String login (int AD_Org_ID, int AD_Role_ID, int AD_User_ID)
	{
		log.info("AD_User_ID=" + AD_User_ID);
		return null;
	}	//	login

	
	/**
	 *	Get Client to be monitored
	 *	@return AD_Client_ID client
	 */
	public int getAD_Client_ID()
	{
		return m_AD_Client_ID;
	}	//	getAD_Client_ID

	
	/**
	 * 	String Representation
	 *	@return info
	 */
	public String toString ()
	{
		StringBuffer sb = new StringBuffer ("LCO_Validator");
		return sb.toString ();
	}	//	toString
	
}	//	LCO_Validator