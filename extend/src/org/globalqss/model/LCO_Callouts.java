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
package org.globalqss.model;

import java.util.Properties;

import org.compiere.model.CalloutEngine;
import org.compiere.model.GridField;
import org.compiere.model.GridTab;
import org.globalqss.util.LCO_Utils;


/**
 *	User Callout for LCO Localization Colombia
 *
 *  @author Carlos Ruiz
 *  @version  $Id: LCO_Callouts.java,v 1.0 2008/05/26
 */
public class LCO_Callouts extends CalloutEngine
{

	/**
	 *	Check Digit based on TaxID.
	 */
	public String checkTaxIdDigit (Properties ctx, int WindowNo,
			GridTab mTab, GridField mField, Object value, Object oldValue)
	{
		log.info("");
		// TODO: Cambiar todos los mensajes por AD_Message
		// TODO: Verificar si se valida o se genera
		String taxid = mTab.get_ValueAsString("TaxID");
		if (taxid == null)
			return "No se ha indicado identificación";
		if (taxid.length() != taxid.trim().length())
			return "Elimine los espacios de la identificación";
		int taxIDDigit;
		try {
			taxIDDigit = Integer.parseInt(mTab.get_ValueAsString("TaxIdDigit"));
		} catch (NumberFormatException e) {
			return "Número no válido";
		}
		int correctDigit = LCO_Utils.calculateDigitDian(taxid.trim());
		if (correctDigit != taxIDDigit)
			return "Verifique el digito de chequeo";
		return "";
	}	//	checkTaxIdDigit

}	//	LCO_Callouts