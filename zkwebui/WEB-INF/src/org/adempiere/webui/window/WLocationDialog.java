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

/**
 * 2007, Modified by Posterita Ltd.
 */

package org.adempiere.webui.window;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;

import org.adempiere.webui.component.Button;
import org.adempiere.webui.component.Grid;
import org.adempiere.webui.component.GridFactory;
import org.adempiere.webui.component.Label;
import org.adempiere.webui.component.ListItem;
import org.adempiere.webui.component.Listbox;
import org.adempiere.webui.component.Panel;
import org.adempiere.webui.component.Row;
import org.adempiere.webui.component.Textbox;
import org.adempiere.webui.component.Window;
import org.compiere.model.MCountry;
import org.compiere.model.MLocation;
import org.compiere.model.MRegion;
import org.compiere.model.MSysConfig;
import org.compiere.model.X_C_City;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;

/**
 * @author Sendy Yagambrum
 * @date July 16, 2007
 * Location Dialog Box
 * This class is based upon VLocationDialog, written by Jorg Janke
 * 
 * 
 * @author Carlos Ruiz - globalqss
 *         Version for Localizacion Colombia.
 *           * Change order of fields, first country, then region, then according to displaysequence criteria
 *           * Mandatory Address1, Region, City
 *           * It uses city list instead of text box if sysconfig LCO_USE_CITY_LIST is enabled (and country has cities) 
 **/
public class WLocationDialog extends Window implements EventListener
{
    
    private static final String LABEL_STYLE = "white-space: nowrap;";
	private static final long serialVersionUID = 1L;
    /** Logger          */
    private static CLogger log = CLogger.getCLogger(WLocationDialog.class);
    private Label lblAddress1;
    private Label lblAddress2;
    private Label lblAddress3;
    private Label lblAddress4;
    private Label lblCity;
    private Label lblCityList;
    private Label lblZip;
    private Label lblRegion;
    private Label lblPostal;
    private Label lblPostalAdd;
    private Label lblCountry;
    
    private Textbox txtAddress1;
    private Textbox txtAddress2;
    private Textbox txtAddress3;
    private Textbox txtAddress4;
    private Textbox txtCity;
    private Textbox txtPostal;
    private Textbox txtPostalAdd;
    private Listbox lstRegion;
    private Listbox lstCountry;
    private Listbox lstCity;
    
    private Button btnOk;
    private Button btnCancel;
    private Grid mainPanel;
    
    private boolean     m_change = false;
    private MLocation   m_location;
    private int         m_origCountry_ID;
    private int         s_oldCountry_ID = 0;
	private boolean inCountryAction;
	private boolean inRegionAction;
	private boolean useCityList;
    
    public WLocationDialog(String title, MLocation location)
    {
        m_location = location;
        if (m_location == null)
            m_location = new MLocation (Env.getCtx(), 0, null);
        //  Overwrite title 
        if (m_location.getC_Location_ID() == 0)
            setTitle(Msg.getMsg(Env.getCtx(), "LocationNew"));
        else
            setTitle(Msg.getMsg(Env.getCtx(), "LocationUpdate"));    
        
        initComponents();
        init();
//      Current Country
        MCountry.setDisplayLanguage(Env.getAD_Language(Env.getCtx()));
        for (MCountry country:MCountry.getCountries(Env.getCtx()))
        {
            lstCountry.appendItem(country.getName(), country);
        }
        setCountry();
        lstCountry.addEventListener(Events.ON_SELECT,this);
        m_origCountry_ID = m_location.getC_Country_ID();
        //  Current Region
        for (MRegion region : MRegion.getRegions(Env.getCtx(), m_origCountry_ID))
        {
            lstRegion.appendItem(region.getName(),region);
        }
        lstRegion.addEventListener(Events.ON_SELECT,this);
        if (m_location.getCountry().isHasRegion() &&
        	m_location.getCountry().getRegionName() != null &&
        	m_location.getCountry().getRegionName().trim().length() > 0)
            lblRegion.setValue(m_location.getCountry().getRegionName());   //  name for region
        
        setRegion();
        
        lstCity.addEventListener(Events.ON_SELECT,this);

        initLocation();
        //               
        this.setWidth("290px");
        this.setClosable(true);
        this.setBorder("normal");
        this.setAttribute("mode","modal");
    }
    
    private void initComponents()
    {
        lblAddress1     = new Label(Msg.getElement(Env.getCtx(), "Address1"));
        lblAddress1.setStyle(LABEL_STYLE);
        lblAddress2     = new Label(Msg.getElement(Env.getCtx(), "Address2"));
        lblAddress2.setStyle(LABEL_STYLE);
        lblAddress3     = new Label(Msg.getElement(Env.getCtx(), "Address3"));
        lblAddress3.setStyle(LABEL_STYLE);
        lblAddress4     = new Label(Msg.getElement(Env.getCtx(), "Address4"));
        lblAddress4.setStyle(LABEL_STYLE);
        lblCity         = new Label(Msg.getMsg(Env.getCtx(), "City"));
        lblCity.setStyle(LABEL_STYLE);
        lblCityList         = new Label(Msg.getMsg(Env.getCtx(), "City"));
        lblCityList.setStyle(LABEL_STYLE);
        lblZip          = new Label(Msg.getMsg(Env.getCtx(), "Postal"));
        lblZip.setStyle(LABEL_STYLE);
        lblRegion       = new Label(Msg.getMsg(Env.getCtx(), "Region"));
        lblRegion.setStyle(LABEL_STYLE);
        lblPostal       = new Label(Msg.getMsg(Env.getCtx(), "Postal"));
        lblPostal.setStyle(LABEL_STYLE);
        lblPostalAdd    = new Label(Msg.getMsg(Env.getCtx(), "PostalAdd"));
        lblPostalAdd.setStyle(LABEL_STYLE);
        lblCountry      = new Label(Msg.getMsg(Env.getCtx(), "Country"));
        lblCountry.setStyle(LABEL_STYLE);
        
        txtAddress1 = new Textbox();
        txtAddress1.setCols(20);
        txtAddress2 = new Textbox();
        txtAddress2.setCols(20);
        txtAddress3 = new Textbox();
        txtAddress3.setCols(20);
        txtAddress4 = new Textbox();
        txtAddress4.setCols(20);
        txtCity     = new Textbox();
        txtCity.setCols(20);
        txtPostal = new Textbox();
        txtPostal.setCols(20);
        txtPostalAdd = new Textbox();
        txtPostalAdd.setCols(20);
        
        lstRegion    = new Listbox();
        lstRegion.setMold("select");
        lstRegion.setWidth("154px");
        lstRegion.setRows(0);
        
        lstCity      = new Listbox();
        lstCity.setMold("select");
        lstCity.setWidth("154px");
        lstCity.setRows(0);
        
        lstCountry  = new Listbox();
        lstCountry.setMold("select");
        lstCountry.setWidth("154px");
        lstCountry.setRows(0);
        
        btnOk = new Button();
        btnOk.setImage("/images/Ok16.png");
        btnOk.addEventListener(Events.ON_CLICK,this);
        btnCancel = new Button();
        btnCancel.setImage("/images/Cancel16.png");
        btnCancel.addEventListener(Events.ON_CLICK,this);
        
        mainPanel = GridFactory.newGridLayout();
        mainPanel.setStyle("padding:5px");
    }
    
    private void init()
    {
        Row pnlAddress1 = new Row();
        pnlAddress1.appendChild(lblAddress1.rightAlign());
        pnlAddress1.appendChild(txtAddress1);        
        
        Row pnlAddress2 = new Row();
        pnlAddress2.appendChild(lblAddress2.rightAlign());
        pnlAddress2.appendChild(txtAddress2);
        
        Row pnlAddress3 = new Row();
        pnlAddress3.appendChild(lblAddress3.rightAlign());
        pnlAddress3.appendChild(txtAddress3);
        
        Row pnlAddress4 = new Row();
        pnlAddress4.appendChild(lblAddress4.rightAlign());
        pnlAddress4.appendChild(txtAddress4);
        
        Row pnlCity     = new Row();
        pnlCity.appendChild(lblCity.rightAlign());
        pnlCity.appendChild(txtCity);
        
        Row pnlCityList     = new Row();
        pnlCityList.appendChild(lblCityList.rightAlign());
        pnlCityList.appendChild(lstCity);
        
        Row pnlPostal   = new Row();
        pnlPostal.appendChild(lblPostal.rightAlign());
        pnlPostal.appendChild(txtPostal);
        
        Row pnlPostalAdd = new Row();
        pnlPostalAdd.appendChild(lblPostalAdd.rightAlign());
        pnlPostalAdd.appendChild(txtPostalAdd);
        
        Row pnlRegion    = new Row();
        pnlRegion.appendChild(lblRegion.rightAlign());
        pnlRegion.appendChild(lstRegion);
        
        Row pnlCountry  = new Row();
        pnlCountry.appendChild(lblCountry.rightAlign());
        pnlCountry.appendChild(lstCountry);
        
        Panel pnlButton   = new Panel();
        pnlButton.appendChild(btnOk);
        pnlButton.appendChild(btnCancel);
        pnlButton.setWidth("100%");
        pnlButton.setStyle("text-align:right");
               
        this.appendChild(mainPanel);
        this.appendChild(pnlButton);
    }
    /**
     * Dynamically add fields to the Location dialog box
     * @param panel panel to add
    *
     */
    private void addComponents(Row row)
    {
    	if (mainPanel.getRows() != null)
    		mainPanel.getRows().appendChild(row);
    	else
    		mainPanel.newRows().appendChild(row);
    }
    
    private void initLocation()
    {
    	if (mainPanel.getRows() != null)
    		mainPanel.getRows().getChildren().clear();
    	
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
				lblRegion.setValue(country.getRegionName());
			}
			s_oldCountry_ID = m_location.getC_Country_ID();
		}
		
        //  Country First
        addComponents((Row)lstCountry.getParent());
        
//      sequence of City Postal Region - @P@ @C@ - @C@, @R@ @P@

		// to be changed by String ds = country.getEditSequence(); when integrated in core
		// String ds = country.get_ValueAsString("EditSequence");
		// if (ds == null || ds.length() == 0)
        String ds = country.getDisplaySequence();
        if (ds == null || ds.length() == 0)
        {
            log.log(Level.SEVERE, "DisplaySequence empty - " + country);
            ds = "";    //  @C@,  @P@
        }

		if (ds.contains("@R@")) {
			// next to country region
			if (m_location.getCountry().isHasRegion())
                addComponents((Row)lstRegion.getParent());
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
	                addComponents((Row)lstCity.getParent());
				else
	                addComponents((Row)txtCity.getParent());
			}
			else if (s.startsWith("P")) {
                addComponents((Row)txtPostal.getParent());
			}
			else if (s.startsWith("A"))
                addComponents((Row)txtPostalAdd.getParent());
        }
        
        addComponents((Row)txtAddress1.getParent());
        addComponents((Row)txtAddress2.getParent());
        addComponents((Row)txtAddress3.getParent());
        addComponents((Row)txtAddress4.getParent());

		//      Fill it
        if (m_location.getC_Location_ID() != 0)
        {
            txtAddress1.setText(m_location.getAddress1());
            txtAddress2.setText(m_location.getAddress2());
            txtAddress3.setText(m_location.getAddress3());
            txtAddress4.setText(m_location.getAddress4());
            txtCity.setText(m_location.getCity());
            txtPostal.setText(m_location.getPostal());
            txtPostalAdd.setText(m_location.getPostal_Add());
			if (m_location.getCountry().isHasRegion())
			{
				fillRegion(country.getC_Country_ID());
				lblRegion.setValue(m_location.getCountry().getRegionName());
				setRegion();
				if (m_location.getRegion() != null)
					fillCityListFromRegion(m_location.getRegion().getC_Region_ID());
				else
			    	lstCity.getChildren().clear();

			} else {
				fillCityListFromCountry(m_location.getCountry().getC_Country_ID());
			}
			if (useCityList) {
				X_C_City city = new X_C_City(Env.getCtx(), m_location.getC_City_ID(), null);
				setCity(city);
			}
            if (m_location.getCountry().isHasRegion())
            {
            	if (m_location.getCountry().getRegionName() != null
            		&& m_location.getCountry().getRegionName().trim().length() > 0)
            		lblRegion.setValue(m_location.getCountry().getRegionName());
            	else
            		lblRegion.setValue(Msg.getMsg(Env.getCtx(), "Region"));
            	
            }
            setCountry();
		} else {
			// fill defaults if any
			if (country.isHasRegion()) {
				fillRegion(country.getC_Country_ID());
				if (m_location != null && m_location.getRegion() != null)
					setRegion();
				if (lstRegion.getSelectedItem() != null)
					fillCityListFromRegion(((MRegion)lstRegion.getSelectedItem().getValue()).getC_Region_ID());
				else
					lstCity.getChildren().clear();
			} else {
				fillCityListFromCountry(country.getC_Country_ID());
			}

		}
    }

	private void fillRegion(int country_ID) {
    	lstRegion.getChildren().clear();
		MRegion[] regs = MRegion.getRegions(Env.getCtx(), country_ID);
		for (int i = 0; i < regs.length; i++)
            lstRegion.appendItem(regs[i].getName(),regs[i]);
	}

    private void setCountry()
    {
        List listCountry = lstCountry.getChildren();
        Iterator iter = listCountry.iterator();
        while (iter.hasNext())
        {
            ListItem listitem = (ListItem)iter.next();
            if (m_location.getCountry().equals(listitem.getValue()))
            {
                lstCountry.setSelectedItem(listitem);
            }
        }
    }
    private void setRegion()
    {
    	if (m_location.getRegion() != null) 
    	{
	        List listState = lstRegion.getChildren();
	        Iterator iter = listState.iterator();
	        while (iter.hasNext())
	        {
	            ListItem listitem = (ListItem)iter.next();
	            if (m_location.getRegion().equals(listitem.getValue()))
	            {
	                lstRegion.setSelectedItem(listitem);
	            }
	        }
    	}
    	else
    	{
    		lstRegion.setSelectedItem(null);
    	}        
    }
    private void setCity(X_C_City city)
    {
    	if (city != null) 
    	{
	        List listState = lstCity.getChildren();
	        Iterator iter = listState.iterator();
	        while (iter.hasNext())
	        {
	            ListItem listitem = (ListItem)iter.next();
	            if (city.equals(listitem.getValue()))
	            {
	            	lstCity.setSelectedItem(listitem);
	            }
	        }
    	}
    	else
    	{
    		lstCity.setSelectedItem(null);
    	}        
    }
    /**
     *  Get result
     *  @return true, if changed
     */
    public boolean isChanged()
    {
        return m_change;
    }   //  getChange
    /**
     *  Get edited Value (MLocation)
     *  @return location
     */
    public MLocation getValue()
    {
        return m_location;
    }   

    public void onEvent(Event event) throws Exception
    {
        if (btnOk.equals(event.getTarget()))
        {
			// LCO
			String msg = validate_OK();
			if (msg != null) {
				FDialog.error(0, this, msg);
				return;
			}

            action_OK();
            m_change = true;
            this.detach();
        }
        else if (btnCancel.equals(event.getTarget()))
        {
            m_change = false;
            this.detach();
        }

        //  Country Changed - display in new Format
        else if (lstCountry.equals(event.getTarget()))
        {
			inCountryAction = true;
            MCountry c = (MCountry)lstCountry.getSelectedItem().getValue();
            m_location.setCountry(c);
            //  refrseh
            initLocation();
			inCountryAction = false;
		} else if (lstRegion.equals(event.getTarget()))
		{
			if (inCountryAction)
				return;
			inRegionAction = true;
			// Region changed - fill city list
			if (lstRegion.getSelectedItem() != null)
				fillCityListFromRegion(((MRegion)lstRegion.getSelectedItem().getValue()).getC_Region_ID());
			else
				lstCity.getChildren().clear();
			inRegionAction = false;
		}
		else if (lstCity.equals(event.getTarget()))
		{
			if (inCountryAction || inRegionAction)
				return;
			fillCityName();
		}
    }
    
	// LCO - address 1, region and city required
	private String validate_OK() {
		MCountry country = (MCountry)lstCountry.getSelectedItem().getValue();
		String ds = country.getDisplaySequence();
		if (ds.contains("@R@") && m_location.getCountry().isHasRegion())
		{
			if (lstRegion.getSelectedItem() == null)
				return "LCO_SelectRegion";
		}
		if (ds.contains("@C@")) {
			if (useCityList) {
				if (lstCity.getSelectedItem() == null)
					return "LCO_SelectCity";
			} else {
				if (txtCity.getValue().trim().length() == 0)
					return "LCO_FillCity";
			}
		}
		if (txtAddress1.getValue().trim().length() == 0)
			return "LCO_FillAddress1";
		return null;
	}

	private void fillCityName() {
		if (lstCity.getSelectedItem() != null) {
			X_C_City city = (X_C_City) lstCity.getSelectedItem().getValue();
			String cityName = city.getName();
			if (cityName != null && cityName.trim().length() > 0)
				txtCity.setText(cityName);
		}
	}

	/**
	 *	Fill City List from Region
	 *  @params region_id
	 */
	private void fillCityListFromRegion(int region_ID) {
		if (! useCityList)
			return;
		lstCity.getChildren().clear();
		try
		{
			PreparedStatement pstmt = DB.prepareStatement("SELECT * From C_City Where IsActive='Y' AND C_Region_ID=? ORDER BY "
					+ (m_location.getCountry().getC_Country_ID() == 156 /*Colombia*/ ? "Postal, " : "")
					+ "Name", null);
			pstmt.setInt(1, region_ID);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next())
			{
				X_C_City city = new X_C_City(Env.getCtx(), rs, null);
	            lstCity.appendItem(city.getName(),city);
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
		lstCity.getChildren().clear();
		try
		{
			PreparedStatement pstmt = DB.prepareStatement("SELECT C_City_ID, Name From C_City Where IsActive='Y' AND C_Country_ID=? ORDER BY Name", null);
			pstmt.setInt(1, country_ID);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next())
			{
				X_C_City city = new X_C_City(Env.getCtx(), rs, null);
	            lstCity.appendItem(city.getName(),city);
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
     *  OK - check for changes (save them) & Exit
     */
    private void action_OK()
    {
        m_location.setAddress1(txtAddress1.getValue());
        m_location.setAddress2(txtAddress2.getValue());
        m_location.setAddress3(txtAddress3.getValue());
        m_location.setAddress4(txtAddress4.getValue());
        m_location.setCity(txtCity.getValue());
		if (useCityList) {
			X_C_City city = (X_C_City) lstCity.getSelectedItem().getValue();
			if (city != null)
				m_location.setC_City_ID(city.getC_City_ID());
		}
        m_location.setPostal(txtPostal.getValue());
        //  Country/Region
        MCountry c = (MCountry)lstCountry.getSelectedItem().getValue();
        m_location.setCountry(c);
        if (m_location.getCountry().isHasRegion())
        {
			if (lstRegion.getSelectedItem() != null)
				m_location.setRegion((MRegion)lstRegion.getSelectedItem().getValue());
        }
        else
            m_location.setC_Region_ID(0);
        //  Save chnages
        m_location.save();
        
    }   //  actionOK
}
