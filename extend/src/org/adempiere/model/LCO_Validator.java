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

		//	Documents to be monitored
		engine.addDocValidate(X_C_Invoice.Table_Name, this);
		engine.addDocValidate(X_C_Payment.Table_Name, this);
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

		// when invoiceline is changed clear the withholding amount on invoice
		// in order to force a regeneration
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

		return null;
	}	//	modelChange
	
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

		// before preparing invoice validate if withholdings has been generated
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
					boolean calc = false;
					if (inv.getC_Order_ID() > 0) {
						MOrder ord = new MOrder (inv.getCtx(), inv.getC_Order_ID(), inv.get_TrxName());
						MDocType dt = new MDocType (inv.getCtx(), ord.getC_DocTypeTarget_ID(), inv.get_TrxName());
						if (dt.getDocBaseType().equals(MDocType.DOCBASETYPE_SalesOrder)
								&& dt.getDocSubTypeSO().equals(MDocType.DOCSUBTYPESO_POSOrder)) {
							// is a POS Order don't generate
							inv.set_CustomColumn("WithholdingAmt", new BigDecimal(0));
							calc = false;
						} else {
							calc = true;
						}
					} else {
						calc = true;
					}
					if (calc) {
						// is not a POS Order, generate withholdings
						LCO_MInvoice lcoinv = new LCO_MInvoice(inv.getCtx(), inv.getC_Invoice_ID(), inv.get_TrxName());
						lcoinv.recalcWithholdings();
					}
				}
			}
		}

		// after preparing invoice move invoice withholdings to taxes and recalc grandtotal of invoice
		if (po.get_TableName().equals(X_C_Invoice.Table_Name) && timing == TIMING_AFTER_PREPARE) {
			msg = translateWithholdingToTaxes((MInvoice) po);
			if (msg != null)
				return msg;
		}

		// after completing the invoice fix the dates on withholdings and mark the invoice withholdings as processed
		if (po.get_TableName().equals(X_C_Invoice.Table_Name) && timing == TIMING_AFTER_COMPLETE) {
			msg = completeInvoiceWithholding((MInvoice) po);
			if (msg != null)
				return msg;
		}

		// before completing the payment - validate that writeoff amount must be greater than sum of payment withholdings  
		if (po.get_TableName().equals(X_C_Payment.Table_Name) && timing == TIMING_BEFORE_COMPLETE) {
			msg = validateWriteOffVsPaymentWithholdings((MPayment) po);
			if (msg != null)
				return msg;
		}

		// after completing the allocation - complete the payment withholdings  
		if (po.get_TableName().equals(X_C_AllocationHdr.Table_Name) && timing == TIMING_AFTER_COMPLETE) {
			msg = completePaymentWithholdings((MAllocationHdr) po);
			if (msg != null)
				return msg;
		}

		// before posting the allocation - post the payment withholdings vs writeoff amount  
		if (po.get_TableName().equals(X_C_AllocationHdr.Table_Name) && timing == TIMING_BEFORE_POST) {
			msg = accountingForInvoiceWithholdingOnPayment((MAllocationHdr) po);
			if (msg != null)
				return msg;
		}

		return null;
	}	//	docValidate

	private String validateWriteOffVsPaymentWithholdings(MPayment pay) {
		if (pay.getC_Invoice_ID() > 0) {
			// validate vs invoice of payment
			BigDecimal wo = pay.getWriteOffAmt();
			BigDecimal sumwhamt = Env.ZERO;
			sumwhamt = DB.getSQLValueBD(
					pay.get_TrxName(),
					"SELECT COALESCE (SUM (TaxAmt), 0) " +
					"FROM LCO_InvoiceWithholding " +
					"WHERE C_Invoice_ID = ? AND " +
					"IsActive = 'Y' AND " +
					"IsCalcOnPayment = 'Y' AND " +
					"Processed = 'N' AND " +
					"C_AllocationLine_ID IS NULL",
					pay.getC_Invoice_ID());
			if (sumwhamt == null)
				sumwhamt = Env.ZERO;
			if (wo.compareTo(sumwhamt) < 0)
				return "Write-Off Amount must be equal or greater than Withholdings";
		} else {
			// validate every C_PaymentAllocate
			String sql = 
				"SELECT C_PaymentAllocate_ID " +
				"FROM C_PaymentAllocate " +
				"WHERE C_Payment_ID = ?";
			PreparedStatement pstmt = DB.prepareStatement(sql, pay.get_TrxName());
			try {
				pstmt.setInt(1, pay.getC_Payment_ID());
				ResultSet rs = pstmt.executeQuery();
				while (rs.next()) {
					int palid = rs.getInt(1);
					MPaymentAllocate pal = new MPaymentAllocate(pay.getCtx(), palid, pay.get_TrxName());
					BigDecimal wo = pal.getWriteOffAmt();
					BigDecimal sumwhamt = Env.ZERO;
					sumwhamt = DB.getSQLValueBD(
							pay.get_TrxName(),
							"SELECT COALESCE (SUM (TaxAmt), 0) " +
							"FROM LCO_InvoiceWithholding " +
							"WHERE C_Invoice_ID = ? AND " +
							"IsActive = 'Y' AND " +
							"IsCalcOnPayment = 'Y' AND " +
							"Processed = 'N' AND " +
							"C_AllocationLine_ID IS NULL",
							pal.getC_Invoice_ID());
					if (sumwhamt == null)
						sumwhamt = Env.ZERO;
					if (wo.compareTo(sumwhamt) < 0)
						return "Write-Off Amount must be equal or greater than Withholdings";
				}
				rs.close();
				pstmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
				return e.getLocalizedMessage();
			}
		}

		return null;
	}

	private String completePaymentWithholdings(MAllocationHdr ah) {
		MAllocationLine[] als = ah.getLines(true);
		for (int i = 0; i < als.length; i++) {
			MAllocationLine al = als[i];
			if (al.getC_Invoice_ID() > 0) {
				String sql = 
					"SELECT LCO_InvoiceWithholding_ID " +
					"FROM LCO_InvoiceWithholding " +
					"WHERE C_Invoice_ID = ? AND " +
					"IsActive = 'Y' AND " +
					"IsCalcOnPayment = 'Y' AND " +
					"Processed = 'N' AND " +
					"C_AllocationLine_ID IS NULL";
				PreparedStatement pstmt = DB.prepareStatement(sql, ah.get_TrxName());
				try {
					pstmt.setInt(1, al.getC_Invoice_ID());
					ResultSet rs = pstmt.executeQuery();
					while (rs.next()) {
						int iwhid = rs.getInt(1);
						MLCOInvoiceWithholding iwh = new MLCOInvoiceWithholding(
								ah.getCtx(), iwhid, ah.get_TrxName());
						iwh.setC_AllocationLine_ID(al.getC_AllocationLine_ID());
						iwh.setDateAcct(ah.getDateAcct());
						iwh.setDateTrx(ah.getDateTrx());
						iwh.setProcessed(true);
						iwh.save();
					}
					rs.close();
					pstmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
					return e.getLocalizedMessage();
				}
			}
		}
		return null;
	}

	private String accountingForInvoiceWithholdingOnPayment(MAllocationHdr ah) {
		// Accounting like Doc_Allocation
		// (Write off) vs (invoice withholding where iscalconpayment=Y)
		// 20070807 - globalqss - instead of adding a new WriteOff post, find the
		//  current WriteOff and subtract from the posting
		
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
					+ " WHERE i.C_Invoice_ID = ? AND " +
							 "i.IsCalcOnPayment = 'Y' AND " +
							 "i.IsActive = 'Y' AND " +
							 "i.Processed = 'Y' AND " +
							 "i.C_AllocationLine_ID = ? AND " +
							 "i.C_Tax_ID = t.C_Tax_ID "
					+ "GROUP BY i.C_Tax_ID, t.Name, t.Rate, t.IsSalesTax";
				PreparedStatement pstmt = null;
				try
				{
					pstmt = DB.prepareStatement(sql, ah.get_TrxName());
					pstmt.setInt(1, invoice.getC_Invoice_ID());
					pstmt.setInt(2, alloc_line.getC_AllocationLine_ID());
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
					// First try to find the WriteOff posting record
					FactLine[] factlines = fact.getLines();
					boolean foundflwriteoff = false;
					for (int ifl = 0; ifl < factlines.length; ifl++) {
						FactLine fl = factlines[ifl];
						if (fl.getAccount().equals(doc.getAccount(Doc.ACCTTYPE_WriteOff, as))) {
							foundflwriteoff = true;
							// old balance = DB - CR
							BigDecimal balamt = fl.getAmtSourceDr().subtract(fl.getAmtSourceCr());
							// new balance = old balance +/- tottax
							BigDecimal newbalamt = Env.ZERO;
							if (invoice.isSOTrx())
								newbalamt = balamt.subtract(tottax);
							else
								newbalamt = balamt.add(tottax);
							if (Env.ZERO.compareTo(newbalamt) == 0) {
								// both zeros, remove the line
								fact.remove(fl);
							} else if (Env.ZERO.compareTo(newbalamt) > 0) {
								fl.setAmtAcct(fl.getC_Currency_ID(), Env.ZERO, newbalamt);
								fl.setAmtSource(fl.getC_Currency_ID(), Env.ZERO, newbalamt);
							} else {
								fl.setAmtAcct(fl.getC_Currency_ID(), newbalamt, Env.ZERO);
								fl.setAmtSource(fl.getC_Currency_ID(), newbalamt, Env.ZERO);
							}
							break;
						}
					}

					if (! foundflwriteoff) {
						// Create a new line
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