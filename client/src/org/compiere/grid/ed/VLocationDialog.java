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
package org.compiere.grid.ed;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Level;

import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;

import org.adempiere.interfaces.PostcodeLookupInterface;
import org.adempiere.model.Postcode;
import org.compiere.apps.ADialog;
import org.compiere.apps.AEnv;
import org.compiere.apps.ConfirmPanel;
import org.compiere.model.MCountry;
import org.compiere.model.MLocation;
import org.compiere.model.MRegion;
import org.compiere.model.MSysConfig;
import org.compiere.model.X_C_City;
import org.compiere.swing.CButton;
import org.compiere.swing.CComboBox;
import org.compiere.swing.CDialog;
import org.compiere.swing.CLabel;
import org.compiere.swing.CPanel;
import org.compiere.swing.CTextField;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.KeyNamePair;
import org.compiere.util.Msg;

/**
 *	Dialog to enter Location Info (Address)
 *
 *  @author 	Jorg Janke
 *  @version 	$Id: VLocationDialog.java,v 1.2 2006/07/30 00:51:28 jjanke Exp $
 * 
 * @author Teo Sarca, SC ARHIPAC SERVICE SRL
 * 			<li>BF [ 1831060 ] Location dialog should use Address1, Address2 ... elements
 * 
 * @author Carlos Ruiz - globalqss
 *         Version for Localizacion Colombia.
 *           * Change order of fields, first country, then region, then according to displaysequence criteria
 *           * Mandatory Address1, Region, City
 *           * It uses city list instead of text box if sysconfig LCO_USE_CITY_LIST is enabled (and country has cities) 
 */
public class VLocationDialog extends CDialog 
	implements ActionListener
{
	
	/** Lookup result */
	//private Object[][] data = null;

	/** Lookup result header */
	private Object[] header = null;

	//private int m_WindowNo = 0;

	/**
	 *	Constructor
	 *
	 * @param frame parent
	 * @param title title (field name)
	 * @param location Model Location
	 */
	public VLocationDialog (Frame frame, String title, MLocation location)
	{
		super(frame, title, true);
		//m_WindowNo = WindowNo;
		try
		{
			jbInit();
		}
		catch(Exception ex)
		{
			log.log(Level.SEVERE, ex.getMessage());
		}
		m_location = location;
		if (m_location == null)
			m_location = new MLocation (Env.getCtx(), 0, null);
		//	Overwrite title	
		if (m_location.getC_Location_ID() == 0)
			setTitle(Msg.getMsg(Env.getCtx(), "LocationNew"));
		else
			setTitle(Msg.getMsg(Env.getCtx(), "LocationUpdate"));

		//	Current Country
		MCountry.setDisplayLanguage(Env.getAD_Language(Env.getCtx()));
		fCountry = new CComboBox(MCountry.getCountries(Env.getCtx()));
		fCountry.setSelectedItem(m_location.getCountry());
		m_origCountry_ID = m_location.getC_Country_ID();
		//	Current Region
		fRegion = new CComboBox(MRegion.getRegions(Env.getCtx(), m_origCountry_ID));
		if (m_location.getCountry().isHasRegion())
			lRegion.setText(m_location.getCountry().getRegionName());	//	name for region
		fRegion.setSelectedItem(m_location.getRegion());
		//
		fCityList = new CComboBox();
		initLocation();
		fCountry.addActionListener(this);
		fOnline.addActionListener(this);
		fRegion.addActionListener(this);
		fCityList.addActionListener(this);
		AEnv.positionCenterWindow(frame, this);
		
		
	}	//	VLocationDialog

	private boolean 	m_change = false;
	private MLocation	m_location;
	private int			m_origCountry_ID;
	private int			s_oldCountry_ID = 0;
	/**	Logger			*/
	private static CLogger log = CLogger.getCLogger(VLocationDialog.class);

	private CPanel panel = new CPanel();
	private CPanel mainPanel = new CPanel();
	private CPanel southPanel = new CPanel();
	private BorderLayout panelLayout = new BorderLayout();
	private GridBagLayout gridBagLayout = new GridBagLayout();
	private ConfirmPanel confirmPanel = new ConfirmPanel(true);
	private BorderLayout southLayout = new BorderLayout();
	//
	private CLabel		lAddress1   = new CLabel(Msg.getElement(Env.getCtx(), "Address1"));
	private CLabel		lAddress2   = new CLabel(Msg.getElement(Env.getCtx(), "Address2"));
	private CLabel		lAddress3   = new CLabel(Msg.getElement(Env.getCtx(), "Address3"));
	private CLabel		lAddress4   = new CLabel(Msg.getElement(Env.getCtx(), "Address4"));
	private CLabel		lCity       = new CLabel(Msg.getMsg(Env.getCtx(), "City"));
	private CLabel		lCountry    = new CLabel(Msg.getMsg(Env.getCtx(), "Country"));
	private CLabel		lRegion     = new CLabel(Msg.getMsg(Env.getCtx(), "Region"));
	private CLabel		lPostal     = new CLabel(Msg.getMsg(Env.getCtx(), "Postal"));
	private CLabel		lPostalAdd  = new CLabel(Msg.getMsg(Env.getCtx(), "PostalAdd"));
	private CLabel		lOnline		= new CLabel("");		// dummy to use addLine without error....
	private CTextField	fAddress1 = new CTextField(20);		//	length=60
	private CTextField	fAddress2 = new CTextField(20);		//	length=60
	private CTextField	fAddress3 = new CTextField(20);		//	length=60
	private CTextField	fAddress4 = new CTextField(20);		//	length=60
	private CTextField	fCity  = new CTextField(15);		//	length=60
	private CComboBox	fCityList;
	private CComboBox	fCountry;
	private CComboBox	fRegion;
	private CTextField	fPostal = new CTextField(5);		//	length=10
	private CTextField	fPostalAdd = new CTextField(5);		//	length=10
	private CButton 	fOnline = new CButton();			
	//
	private GridBagConstraints gbc = new GridBagConstraints();
	private Insets labelInsets = new Insets(2,15,2,0);		// 	top,left,bottom,right
	private Insets fieldInsets = new Insets(2,5,2,10);
	private boolean inCountryAction;
	private boolean inRegionAction;
	private boolean useCityList;

	/**
	 *	Static component init
	 *  @throws Exception
	 */
	void jbInit() throws Exception
	{
		panel.setLayout(panelLayout);
		southPanel.setLayout(southLayout);
		mainPanel.setLayout(gridBagLayout);
		panelLayout.setHgap(5);
		panelLayout.setVgap(10);
		getContentPane().add(panel);
		panel.add(mainPanel, BorderLayout.CENTER);
		panel.add(southPanel, BorderLayout.SOUTH);
		southPanel.add(confirmPanel, BorderLayout.NORTH);
		//
		confirmPanel.addActionListener(this);
	}	//	jbInit

	/**
	 *	Dynamic Init & fill fields - Called when Country changes!
	 */
	private void initLocation()
	{
		if (s_oldCountry_ID == 0) // first_time
			s_oldCountry_ID = m_location.getC_Country_ID();
		MCountry country = m_location.getCountry();
		log.fine(country.getName() + ", Region=" + country.isHasRegion() + " " + country.getDisplaySequence()
			+ ", C_Location_ID=" + m_location.getC_Location_ID());
		//	Changed country
		if (m_location.getC_Country_ID() != s_oldCountry_ID) {
			// clear dependent fields
			m_location.setRegion(null);
			m_location.setC_City_ID(0);
			m_location.setCity(null);
			m_location.setPostal(null);
			if (country.isHasRegion()) {
				lRegion.setText(country.getRegionName());
			}
			s_oldCountry_ID = m_location.getC_Country_ID();
		}

		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.gridy = 0;			//	line
		gbc.gridx = 0;
		gbc.gridwidth = 1;
		gbc.insets = fieldInsets;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 0;
		gbc.weighty = 0;

		mainPanel.add(Box.createVerticalStrut(5), gbc);    	//	top gap

		int line = 1;

		// addLine(line++, lOnline, fOnline);

		//  Country First
		addLine(line++, lCountry, fCountry);

		//  sequence of City Postal Region - @P@ @C@ - @C@, @R@ @P@

		// to be changed by String ds = country.getEditSequence(); when integrated in core
		// String ds = country.get_ValueAsString("EditSequence");
		// if (ds == null || ds.length() == 0)
		String ds = country.getDisplaySequence();
		if (ds == null || ds.length() == 0)
		{
			log.log(Level.SEVERE, "DisplaySequence empty - " + country);
			ds = "";	//	@C@,  @P@
		}

		if (ds.contains("@R@")) {
			// next to country region
			if (m_location.getCountry().isHasRegion())
				addLine(line++, lRegion, fRegion);
		}

		StringTokenizer st = new StringTokenizer(ds, "@", false);
		useCityList = false;
		while (st.hasMoreTokens())
		{
			String s = st.nextToken();
			if (s.startsWith("C")) {
				// if there are cities in the country, then use list, otherwise use a textbox
				if (MSysConfig.getBooleanValue("LCO_USE_CITY_LIST", false, Env.getAD_Client_ID(Env.getCtx()))) {
					int cnt = DB.getSQLValue(null, "SELECT COUNT(*) FROM C_City WHERE IsActive='Y' AND C_Country_ID=?", m_location.getCountry().getC_Country_ID());
					useCityList = (cnt > 0);
				}
				if (useCityList)
					addLine(line++, lCity, fCityList);
				else
				addLine(line++, lCity, fCity);
			}
			else if (s.startsWith("P")) {
				addLine(line++, lPostal, fPostal);
			}
			else if (s.startsWith("A"))
				addLine(line++, lPostalAdd, fPostalAdd);
		}
		addLine(line++, lAddress1, fAddress1);
		addLine(line++, lAddress2, fAddress2);
		addLine(line++, lAddress3, fAddress3);
		addLine(line++, lAddress4, fAddress4);
		
		if (useCityList)
			fCity.setEnabled(false);
		else
			fCity.setEnabled(true);

		//	Fill it
		if (m_location.getC_Location_ID() != 0)
		{
			fAddress1.setText(m_location.getAddress1());
			fAddress2.setText(m_location.getAddress2());
			fAddress3.setText(m_location.getAddress3());
			fAddress4.setText(m_location.getAddress4());
			fCity.setText(m_location.getCity());
			fPostal.setText(m_location.getPostal());
			fPostalAdd.setText(m_location.getPostal_Add());
			fOnline.setText(Msg.getMsg(Env.getCtx(), "Online"));
			if (m_location.getCountry().isHasRegion())
			{
				fillRegion(country.getC_Country_ID());
				lRegion.setText(m_location.getCountry().getRegionName());
				fRegion.setSelectedItem(m_location.getRegion());
				if (m_location.getRegion() != null)
					fillCityListFromRegion(m_location.getRegion().getC_Region_ID());
				else
					fCityList.removeAllItems();
			} else {
				fillCityListFromCountry(m_location.getCountry().getC_Country_ID());
			}
			if (useCityList) {
				X_C_City city = new X_C_City(Env.getCtx(), m_location.getC_City_ID(), null);
				KeyNamePair pcit = new KeyNamePair(city.getC_City_ID(), city.getName());
				fCityList.setSelectedItem(pcit);
			}
			
			// disable online if this country doesn't have post code lookup
			if (m_location.getCountry().isPostcodeLookup()) {
				fOnline.setEnabled(true);
				fOnline.setVisible(true);
			}
			else {
				fOnline.setEnabled(false);
				fOnline.setVisible(false);
			}
			
			fCountry.setSelectedItem(country);
		} else {
			// fill defaults if any
			if (country.isHasRegion()) {
				fillRegion(country.getC_Country_ID());
				if (m_location != null && m_location.getRegion() != null)
					fRegion.setSelectedItem(m_location.getRegion());
				MRegion r = (MRegion)fRegion.getSelectedItem();
				if (r != null)
					fillCityListFromRegion(r.getC_Region_ID());
				else
					fCityList.removeAllItems();
			} else {
				fillCityListFromCountry(country.getC_Country_ID());
			}

		}
		//	Update UI
		pack();
	}	//	initLocation

	private void fillRegion(int country_ID) {
		fRegion.removeAllItems();
		MRegion[] regs = MRegion.getRegions(Env.getCtx(), country_ID);
		for (int i = 0; i < regs.length; i++)
		    fRegion.addItem(regs[i]);
	}

	/**
	 *	Add Line to screen
	 *
	 *  @param line line number (zero based)
	 *  @param label label
	 *  @param field field
	 */
	private void addLine(int line, JLabel label, JComponent field)
	{
		gbc.gridy = line;
		//	label
		gbc.insets = labelInsets;
		gbc.gridx = 0;
		gbc.weightx = 0.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		label.setHorizontalAlignment(SwingConstants.RIGHT);
		mainPanel.add(label, gbc);
		//	Field
		gbc.insets = fieldInsets;
		gbc.gridx = 1;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = fieldInsets;
		mainPanel.add(field, gbc);
		
	}	//	addLine


	/**
	 *	ActionListener
	 *  @param e ActionEvent
	 */
	public void actionPerformed(ActionEvent e)
	{
		if (e.getActionCommand().equals(ConfirmPanel.A_OK))
		{
			// LCO
			String msg = validate_OK();
			if (msg != null) {
				ADialog.error(0, this, msg);
				return;
			}

			action_OK();
			m_change = true;
			dispose();
		}
		else if (e.getActionCommand().equals(ConfirmPanel.A_CANCEL))
		{
			m_change = false;
			dispose();
		}

		//	Country Changed - display in new Format
		else if (e.getSource() == fCountry)
		{
			inCountryAction = true;
			//	Modifier for Mouse selection is 16  - for any key selection 0
			MCountry c = (MCountry)fCountry.getSelectedItem();
			m_location.setCountry(c);
			
			// refresh online button for new country
			if (c.isPostcodeLookup()) {
				fOnline.setEnabled(true);
				fOnline.setVisible(true);
			}
			else {
				fOnline.setEnabled(false);
				fOnline.setVisible(false);
			}
			
			// update the region name if regions are enabled for this country
			if (c.isHasRegion())
			{
				lRegion.setText(c.getRegionName());
				fRegion.setSelectedItem(m_location.getRegion());
				
				// TODO: fix bug that occurs when the new region name is shorter than the old region name
			}
			
			//			refresh
			mainPanel.removeAll();
			
			initLocation();
			fCountry.requestFocus();	//	allows to use Keyboard selection
			inCountryAction = false;
		}
		else if (e.getSource() == fOnline)
		{
			
			// check to see if we have a postcode lookup plugin for this country
			MCountry c = (MCountry)fCountry.getSelectedItem();
			if (c.isPostcodeLookup())
			{
				lookupPostcode(c, fPostal.getText());
			}
		} else if (e.getSource() == fRegion)
		{
			if (inCountryAction)
				return;
			inRegionAction = true;
			// Region changed - fill city list
			MRegion r = (MRegion)fRegion.getSelectedItem();
			if (r != null)
				fillCityListFromRegion(r.getC_Region_ID());
			else
				fCityList.removeAllItems();
			inRegionAction = false;
		}
		else if (e.getSource() == fCityList)
		{
			if (inCountryAction || inRegionAction)
				return;
			fillCityName();
		}
	}	//	actionPerformed

	// LCO - address 1, region and city required
	private String validate_OK() {
		MCountry country = (MCountry)fCountry.getSelectedItem();
		String ds = country.getDisplaySequence();
		if (ds.contains("@R@") && m_location.getCountry().isHasRegion())
		{
			if (fRegion.getSelectedItem() == null)
				return "LCO_SelectRegion";
		}
		if (ds.contains("@C@")) {
			if (useCityList) {
				if (fCityList.getSelectedItem() == null)
					return "LCO_SelectCity";
			} else {
				if (fCity.getText().trim().length() == 0)
					return "LCO_FillCity";
			}
		}
		if (fAddress1.getText().trim().length() == 0)
			return "LCO_FillAddress1";
		return null;
	}

	private void fillCityName() {
		if (fCityList.getSelectedItem() != null) {
			KeyNamePair city = (KeyNamePair) fCityList.getSelectedItem();
			String cityName = DB.getSQLValueString(null, "SELECT Name From C_City Where C_City_ID=?", city.getKey());
			if (cityName != null && cityName.trim().length() > 0)
				fCity.setText(cityName);
		}
	}

	/**
	 *	Fill City List from Region
	 *  @params region_id
	 */
	private void fillCityListFromRegion(int region_ID) {
		if (! useCityList)
			return;
		fCityList.removeAllItems();
		try
		{
			PreparedStatement pstmt = DB.prepareStatement("SELECT C_City_ID, Name From C_City Where IsActive='Y' AND C_Region_ID=? ORDER BY Name", null);
			pstmt.setInt(1, region_ID);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next())
			{
				KeyNamePair kn = new KeyNamePair (rs.getInt(1), rs.getString(2));
				fCityList.addItem(kn);
			}
			rs.close();
			pstmt.close();

		}
		catch(SQLException et)
		{
			log.log(Level.SEVERE, "SQL Error - ", et);
		}
		fillCityName();
	}

	/**
	 *	Fill City List from Country
	 *  @params region_id
	 */
	private void fillCityListFromCountry(int country_ID) {
		if (! useCityList)
			return;
		fCityList.removeAllItems();
		try
		{
			PreparedStatement pstmt = DB.prepareStatement("SELECT C_City_ID, Name From C_City Where IsActive='Y' AND C_Country_ID=? ORDER BY Name", null);
			pstmt.setInt(1, country_ID);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next())
			{
				KeyNamePair kn = new KeyNamePair (rs.getInt(1), rs.getString(2));
				fCityList.addItem(kn);
			}
			rs.close();
			pstmt.close();

		}
		catch(SQLException et)
		{
			log.log(Level.SEVERE, "SQL Error - ", et);
		}
		fillCityName();
	}

	/**
	 * 	OK - check for changes (save them) & Exit
	 */
	private void action_OK()
	{
		m_location.setAddress1(fAddress1.getText());
		m_location.setAddress2(fAddress2.getText());
		m_location.setAddress3(fAddress3.getText());
		m_location.setAddress4(fAddress4.getText());
		m_location.setCity(fCity.getText());
		if (useCityList) {
			KeyNamePair city = (KeyNamePair) fCityList.getSelectedItem();
			if (city != null)
				m_location.setC_City_ID(city.getKey());
		}
		m_location.setPostal(fPostal.getText());
		m_location.setPostal_Add(fPostalAdd.getText());
		//  Country/Region
		MCountry c = (MCountry)fCountry.getSelectedItem();
		m_location.setCountry(c);
		if (m_location.getCountry().isHasRegion())
		{
			MRegion r = (MRegion)fRegion.getSelectedItem();
			if (r != null)
			m_location.setRegion(r);
		}
		else
			m_location.setC_Region_ID(0);
		//	Save changes
		m_location.save();
	}	//	actionOK

	/**
	 *	Get result
	 *  @return true, if changed
	 */
	public boolean isChanged()
	{
		return m_change;
	}	//	getChange

	/**
	 * 	Get edited Value (MLocation)
	 *	@return location
	 */
	public MLocation getValue()
	{
		return m_location;
	}	//	getValue
	/**
	 * lookupPostcode
	 * 
	 * 
	 * @param country
	 * @param postcode
	 * @return
	 */
	private String lookupPostcode(MCountry country, String postcode)
	{
		// Initialise the lookup class.
		PostcodeLookupInterface pcLookup = null;
		try {
			PostcodeLookupInterface pcLookupTmp = (PostcodeLookupInterface) Class
					.forName(country.getLookupClassName()).newInstance();
			pcLookup = pcLookupTmp.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			return "lookupAddress(): " + e.getMessage();
		}
		
		// remove any spaces from the postcode and convert to upper case
		postcode = postcode.replaceAll(" ", "").toUpperCase();
		log.fine("Looking up postcode: " + postcode);
		
		// Lookup postcode on server.
		pcLookup.setServerUrl(country.getLookupUrl());
		pcLookup.setClientID(country.getLookupClientID());
		pcLookup.setPassword(country.getLookupPassword());
		if (pcLookup.lookupPostcode(postcode)==1){
			// Success
			fillLocation(pcLookup.getPostCodeData(), country);
			fAddress1.requestFocusInWindow();
		} else
			return "Postcode Lookup Error";
		
		return "";
	}
		/**
		 * Fills the location field using the information retrieved from postcode
		 * servers.
		 * 
		 * @param ctx
		 *            Context
		 * @param pkeyData
		 *            Lookup results
		 * @param windowNo
		 *            Window No.
		 * @param tab
		 *            Tab
		 * @param field
		 *            Field
		 */
		private void fillLocation(HashMap<String, Object> postcodeData, MCountry country) {

			// If it's not empty warn the user.
			if (fAddress1 != null || fAddress2 != null
					|| fAddress3 != null
					|| fAddress4 != null || fCity != null) {
				String warningMsg = "Existing address information will be overwritten. Proceed?";
				String warningTitle = "Warning";
				int response = JOptionPane.showConfirmDialog(null, warningMsg,
						warningTitle, JOptionPane.YES_NO_OPTION);
				if (response == JOptionPane.NO_OPTION)
					return;
			}
			
			
			Set<String> pcodeKeys = postcodeData.keySet();
			Iterator<String> iterator = pcodeKeys.iterator();
			header = null;

			// Allocate the header array
			header = new Object[pcodeKeys.size()];

			String headerStr = null;
			
			// need to check how many records returned
			// TODO - check number of records returns - size() method is incorrect
			if (pcodeKeys.size() > 2)
			{
				// TODO: Implement ResultData Grid and get return (for premises level data)
				System.out.println("Too many postcodes returned from Postcode Lookup - need to Implement ResultData");
			} else
			{
				for (int i = 0; (headerStr = (iterator.hasNext() ? iterator.next() : null)) != null
						|| iterator.hasNext(); i++) {
					header[i] = headerStr;
					Postcode values =  (Postcode) postcodeData.get(headerStr);
				
					// Overwrite the values in location field.
					fAddress1.setText(values.getStreet1());
					fCity.setText(values.getCity());
					fPostal.setText(values.getPostcode());
					
					// Do region lookup
					if (country.isHasRegion())
					{
						// get all regions for this country
						MRegion[] regions = MRegion.getRegions(country.getCtx(), country.getC_Country_ID());
						
						// If regions were loaded
						if ( regions.length > 0)
						{
							// loop through regions array to attempt a region match - don't finish loop if region found
							boolean found = false;
							for (i = 0; i < regions.length && !found; i++)
							{
								
								if (regions[i].getName().equals(values.getRegion()) )
								{
									// found county
									fRegion.setSelectedItem(regions[i]);	
									log.fine("Found region: " + regions[i].getName());
									found = true;
								}
							}
							if (!found)
							{
								// add new region
								MRegion region = new MRegion(country, values.getRegion());
								if (region.save())
								{
									log.fine("Added new region from web service: " + values.getRegion());
									fRegion.setSelectedItem(values);
								} else
									log.severe("Error saving new region: " + region.getName());
								
							}
						} else
							log.severe("Region lookup failed for Country: " + country.getName());
						
					}		
				}
			}
			
		}
}	//	VLocationDialog
