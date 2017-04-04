/******************************************************************************
 * Product: Adempiere ERP & CRM Smart Business Solution                       *
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
 * Copyright (C) 2003-2007 e-Evolution,SC. All Rights Reserved.               *
 *****************************************************************************/
package org.osgroup.model;

import java.math.BigDecimal;
import java.util.List;
import java.sql.Timestamp;

import org.compiere.model.MClient;
import org.compiere.model.MDocType;
import org.compiere.model.MInvoice;
import org.compiere.model.MOrder;
import org.compiere.model.MPayment;
import org.compiere.model.MPaymentAllocate;
import org.compiere.model.MSysConfig;
import org.compiere.model.MSequence;
import org.compiere.model.ModelValidationEngine;
import org.compiere.model.ModelValidator;
import org.compiere.model.PO;
import org.compiere.model.Query;
import org.compiere.util.CLogger;
import org.compiere.util.Env;
import compiere.model.MyValidator;

/**
 *	LCO_Validator 
 *
 *  @author John Agudelo  - O.S. Group  - http://www.osgroup.co
 *  @version  $Id: LCO_Validator
 *  
 *  Localización para Colombia
 */
public class LCO_Validator implements ModelValidator
{
	/**
	 *	Constructor.
	 */
	public LCO_Validator ()
	{
		super ();
	}	//	MyValidator
	
	/**	Logger			*/
	private static CLogger log = CLogger.getCLogger(MyValidator.class);
	/** Client			*/
	private int		m_AD_Client_ID = -1;
	
	/**
	 *	Initialize Validation
	 *	@param engine validation engine 
	 *	@param client client
	 */
	
	
	public void initialize (ModelValidationEngine engine, MClient client)
	{
		if (client != null)
		{	
			m_AD_Client_ID = client.getAD_Client_ID();
		}
	   /*
	    * Cambios sobre tabla
	    */
		engine.addModelChange(MInvoice.Table_Name, this);
		engine.addModelChange(MPaymentAllocate.Table_Name, this);
		
		/*
		 * cambios sobre el documento
		 */
		engine.addDocValidate(MInvoice.Table_Name, this);
		engine.addDocValidate(MOrder.Table_Name, this);	
		engine.addDocValidate(MPayment.Table_Name, this);
		
	}	//	initialize

	public String modelChange (PO po, int type) throws Exception
	{
		String error = null;
		
		if (po.get_TableName().equals(MInvoice.Table_Name) && (type == TYPE_CHANGE || type == TYPE_BEFORE_NEW))
		{
			MInvoice invoice = (MInvoice)po;
			error = C_Invoice_BeforeSaveChange(invoice);
		}
		
		if (po.get_TableName().equals(MPaymentAllocate.Table_Name) && (type == TYPE_AFTER_CHANGE ||  type == TYPE_AFTER_NEW))
		{
			MPaymentAllocate paymentallocate = (MPaymentAllocate)po;
			error = C_PaymentAllocate_AfterSaveChange(paymentallocate);
		}
		
		return error;
	}	//	modelChange
	
	public String docValidate(PO po, int timing)
	{

		String error = null;
		log.info(po.get_TableName() + " Timing: "+timing);
		
		if (timing == TIMING_BEFORE_PREPARE ) 
		{
			if (po.get_TableName().equals(MInvoice.Table_Name))
			{
				MInvoice invoice = (MInvoice)po;
				error = MInvoice_Before_Prepare(invoice);
			}	
		}	

		
		if (timing == TIMING_AFTER_REVERSECORRECT ) 
		{
			if (po.get_TableName().equals(MInvoice.Table_Name))
			{
				MInvoice invoice = (MInvoice)po;
				error = MInvoice_After_ReverserCorrect(invoice);
			}	
		}	
		
		if (timing == TIMING_AFTER_REVERSECORRECT ) 
		{
			if (po.get_TableName().equals(MPayment.Table_Name))
			{
				MPayment payment = (MPayment)po;
				error = MPayment_After_ReverserCorrect(payment);
			}	
		}

		
		return error;			
	}	//	docValidate


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
	
//*************************************************************************************************
	private String C_Invoice_BeforeSaveChange(MInvoice invoice){
		//Guarda la nota de documento (resolución) al crear una nueva factura
		if ( invoice.get_Value("DocumentNote") == null )
		{
			invoice.set_CustomColumn( "DocumentNote", invoice.getC_DocTypeTarget().getDocumentNote() );
		}
		return "";
	}




	private String MInvoice_Before_Prepare(MInvoice invoice) {
		//Valida si los tipos de documento con tipo de documento base Factura de CxC tienen fecha y consecutivo vigente
		if ( invoice.getC_DocTypeTarget().getDocBaseType().compareTo("ARI") == 0 ){
			MDocType dt = MDocType.get(invoice.getCtx(), invoice.getC_DocTypeTarget_ID());
			
			if ( dt.get_Value("ValidTo") != null ){
				Timestamp Ahora = (Timestamp) invoice.getDateInvoiced();
				Timestamp VtoRes = (Timestamp) dt.get_Value("ValidTo");
				
				if ( VtoRes.before(Ahora) )
					return "Resolución vencida!!!" ;
			}
			
			String strsecuencia = invoice.getDocumentNo().toString();
			
			strsecuencia = strsecuencia.replaceAll("[^0-9]", "");
			
			int secuencia = Integer.parseInt( strsecuencia );
	
			if ( dt.get_Value("MaxValue") != null && dt.get_ValueAsInt("MaxValue") != 0 ) {
				int maxvalue = dt.get_ValueAsInt("maxvalue");
				if (  secuencia >= maxvalue )
					return "Consecutivo llegó a su limite!!!" ;
			}	
		}
		return "";
	}



	private String C_PaymentAllocate_AfterSaveChange(MPaymentAllocate paymentallocate){
		//permite totalizar el monto a pagar cuando se agregan varias facturas a un pago
		if ( MSysConfig.getValue("SUM_PAY_AMT", "N", getAD_Client_ID() ).compareTo("N") != 0 ){
			MPayment payment = new MPayment(paymentallocate.getCtx(), paymentallocate.getC_Payment_ID(), paymentallocate.get_TrxName()) ;
			
			List<MPaymentAllocate> list = new Query(paymentallocate.getCtx(), paymentallocate.Table_Name, "C_Payment_ID=?", paymentallocate.get_TrxName())
		    .setClient_ID()
		    .setParameters( paymentallocate.getC_Payment_ID() )
		    .list();
			
			BigDecimal total = Env.ZERO;
			for (MPaymentAllocate paline : list){
				total = total.add( paline.getAmount());
			}
			
			payment.setPayAmt(total);
			payment.save();
		}
		return "";
	}



	private String MPayment_After_ReverserCorrect(MPayment payment){
		//Evita que se consuma un consecutivo al anular un documento tipo pago 

		MPayment reversal = new MPayment(payment.getCtx(), payment.getReversal_ID(), payment.get_TrxName()) ;
		if(reversal != null)
		{
		 reversal.setDocumentNo(payment.getDocumentNo() + "-");
		 payment.setDocumentNo(payment.getDocumentNo()+"+");      
		 payment.setDescription("(" + payment.getDocumentNo() + ") <- (" + reversal.getDocumentNo() + ")");
		 payment.saveEx();

		 reversal.setDescription("(" +reversal.getDocumentNo() + ") -> ("+payment.getDocumentNo()+")");
		 reversal.saveEx();
		}

		MDocType  document = MDocType.get(payment.getCtx(),payment.getC_DocType_ID());
		MSequence sequence = null;
		if ( document.get_ValueAsBoolean("IsOverwriteSeqOnComplete") ){
			sequence   = new MSequence(payment.getCtx(),document.getDefiniteSequence_ID(),payment.get_TrxName());
			sequence.setCurrentNext(sequence.getCurrentNext() -1);
			sequence.saveEx();
		}
		/*else{
			sequence   = new MSequence(payment.getCtx(),document.getDocNoSequence_ID(),payment.get_TrxName());
		}*/
		
	return "";
}

	
	private String MInvoice_After_ReverserCorrect(MInvoice invoice){
		//Evita que se consuma un consecutivo al anular un documento tipo factura 
	    MDocType  document = MDocType.get(invoice.getCtx(),invoice.getC_DocTypeTarget_ID());

	    MSequence defsequence   = new MSequence(invoice.getCtx(),document.getDefiniteSequence_ID(),invoice.get_TrxName());
	    
	    if (document.isOverwriteSeqOnComplete() == false){
	    	if (document.isDocNoControlled() == false){
	    		MInvoice reversal = new MInvoice(invoice.getCtx(), invoice.getReversal_ID(), invoice.get_TrxName()) ;
	    	    if(reversal != null)
	    	    {
	    	       reversal.setDocumentNo(invoice.getDocumentNo() + "-");
	    	       invoice.setDocumentNo(invoice.getDocumentNo() + "+");      
	    	       invoice.setDescription("(" + invoice.getDocumentNo() + ") <- (" + reversal.getDocumentNo() + ")");
	    	       invoice.saveEx();
	    	
	    	       reversal.setDescription("(" +reversal.getDocumentNo() + ") -> ("+invoice.getDocumentNo()+")");
	    	       reversal.saveEx();
	    	    }
    	   		return  "";
	    	}
	    	else{
	    		MSequence sequence   = new MSequence(invoice.getCtx(),document.getDocNoSequence_ID(),invoice.get_TrxName());
		    	sequence.setCurrentNext(sequence.getCurrentNext() -1);
			    sequence.saveEx();	    		
	    	}
	    }
	    else{
	    	defsequence.setCurrentNext(defsequence.getCurrentNext() -1);
		    defsequence.saveEx();	
	    }
	
	    MInvoice reversal = new MInvoice(invoice.getCtx(), invoice.getReversal_ID(), invoice.get_TrxName()) ;
	    if(reversal != null)
	    {
	       reversal.setDocumentNo(invoice.getDocumentNo() + "-");
	       invoice.setDocumentNo(invoice.getDocumentNo() + "+");      
	       invoice.setDescription("(" + invoice.getDocumentNo() + ") <- (" + reversal.getDocumentNo() + ")");
	       invoice.saveEx();
	
	       reversal.setDescription("(" +reversal.getDocumentNo() + ") -> ("+invoice.getDocumentNo()+")");
	       reversal.saveEx();
	    }

	return  "";
}


}	//LCO_Validator
