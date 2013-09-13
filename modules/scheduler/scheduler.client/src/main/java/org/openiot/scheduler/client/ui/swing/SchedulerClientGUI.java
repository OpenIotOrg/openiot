package org.openiot.scheduler.client.ui.swing;

/**
 *    Copyright (c) 2011-2014, OpenIoT
 *    
 *    This file is part of OpenIoT.
 *
 *    OpenIoT is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU Lesser General Public License as published by
 *    the Free Software Foundation, version 3 of the License.
 *
 *    OpenIoT is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU Lesser General Public License for more details.
 *
 *    You should have received a copy of the GNU Lesser General Public License
 *    along with OpenIoT.  If not, see <http://www.gnu.org/licenses/>.
 *
 *     Contact: OpenIoT mailto: info@openiot.eu
 */

import java.awt.EventQueue;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;

import org.openiot.commons.sensortypes.model.MeasurementCapability;
import org.openiot.commons.sensortypes.model.SensorType;
import org.openiot.commons.sensortypes.model.SensorTypes;
import org.openiot.commons.sensortypes.model.Unit;
import org.openiot.scheduler.client.rest.SchedulerClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.border.TitledBorder;
import javax.swing.JLabel;
import javax.swing.LayoutStyle.ComponentPlacement;

public class SchedulerClientGUI extends JPanel
{	
	final static Logger logger = LoggerFactory.getLogger(SchedulerClientGUI.class);
	
	private static final long serialVersionUID = 1L;
	
	private static SchedulerClient schedulerClient;

	private JFrame frmSchedulerClient;	
	private JTextField osdSpecpathTextField;
	private JTextField textFieldLong;
	private JTextField textFieldLat;
	private JTextField textFieldRad;
	private JTextField textFieldOAMOID;
	private JTextField textFieldOSMOId;
	private JTextField textFieldOAMOuserID;
	private JTextField txtOAMOid;
	private JTextField textFieldGetSpecUserID;

	
	public SchedulerClientGUI() 
	{
		schedulerClient = new SchedulerClient("http://localhost:8080/scheduler.core");
		
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmSchedulerClient = new JFrame();
		frmSchedulerClient.setTitle("Scheduler Client");
		frmSchedulerClient.setBounds(100, 100, 470, 529);
		frmSchedulerClient.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmSchedulerClient.getContentPane().setLayout(null);
		
		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(null, "ping service", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel.setBounds(10, 14, 131, 110);
		frmSchedulerClient.getContentPane().add(panel);
		
		JButton btnWelcome = new JButton("Welcome");
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, gl_panel.createSequentialGroup()
					.addContainerGap()
					.addComponent(btnWelcome, GroupLayout.DEFAULT_SIZE, 95, Short.MAX_VALUE)
					.addContainerGap())
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addGap(29)
					.addComponent(btnWelcome)
					.addContainerGap(31, Short.MAX_VALUE))
		);
		panel.setLayout(gl_panel);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new TitledBorder(null, "sensor discovery", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_1.setBounds(156, 14, 284, 110);
		frmSchedulerClient.getContentPane().add(panel_1);
		
		JButton btnDiscoverSensors = new JButton("discover");
		
		JLabel lblLong = new JLabel("long");
		
		JLabel lblLat = new JLabel("lat");
		
		JLabel lblRadius = new JLabel("radius");
		
		textFieldLong = new JTextField();
		textFieldLong.setText("6.631622");
		textFieldLong.setColumns(10);
		
		textFieldLat = new JTextField();
		textFieldLat.setText("46.520131");
		textFieldLat.setColumns(10);
		
		textFieldRad = new JTextField();
		textFieldRad.setText("15");
		textFieldRad.setColumns(10);
		GroupLayout gl_panel_1 = new GroupLayout(panel_1);
		gl_panel_1.setHorizontalGroup(
			gl_panel_1.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_1.createSequentialGroup()
					.addGroup(gl_panel_1.createParallelGroup(Alignment.TRAILING)
						.addGroup(Alignment.LEADING, gl_panel_1.createSequentialGroup()
							.addContainerGap()
							.addComponent(btnDiscoverSensors)
							.addGap(38)
							.addComponent(lblLat)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(textFieldLat, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_panel_1.createSequentialGroup()
							.addContainerGap()
							.addComponent(lblRadius)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(textFieldRad, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_panel_1.createSequentialGroup()
							.addContainerGap()
							.addComponent(lblLong)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(textFieldLong, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap(57, Short.MAX_VALUE))
		);
		gl_panel_1.setVerticalGroup(
			gl_panel_1.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_1.createSequentialGroup()
					.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel_1.createParallelGroup(Alignment.BASELINE)
							.addComponent(textFieldLong, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(lblLong))
						.addGroup(gl_panel_1.createSequentialGroup()
							.addGap(29)
							.addGroup(gl_panel_1.createParallelGroup(Alignment.BASELINE)
								.addComponent(textFieldLat, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(btnDiscoverSensors)
								.addComponent(lblLat))))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel_1.createParallelGroup(Alignment.BASELINE)
						.addComponent(textFieldRad, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblRadius))
					.addGap(2))
		);
		panel_1.setLayout(gl_panel_1);
		
		JPanel panel_2 = new JPanel();
		panel_2.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "register osdspec", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_2.setBounds(10, 327, 268, 142);
		frmSchedulerClient.getContentPane().add(panel_2);
		
		osdSpecpathTextField = new JTextField();
		osdSpecpathTextField.setColumns(10);
		
		JButton btnOpenOsdspec = new JButton("Open OSDSpec");
		btnOpenOsdspec.addActionListener(new BtnOpenOsdspecActionListener());
		
		JButton btnRegisterOsdspec = new JButton("Register OSDSpec");
		btnRegisterOsdspec.addActionListener(new BtnRegisterOsdspecActionListener());
		
		JButton btnRegisterService = new JButton("Register demo srvc");
		btnRegisterService.addActionListener(new BtnRegisterServiceActionListener());
		GroupLayout gl_panel_2 = new GroupLayout(panel_2);
		gl_panel_2.setHorizontalGroup(
			gl_panel_2.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_2.createSequentialGroup()
					.addGroup(gl_panel_2.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel_2.createSequentialGroup()
							.addContainerGap()
							.addGroup(gl_panel_2.createParallelGroup(Alignment.LEADING, false)
								.addGroup(gl_panel_2.createSequentialGroup()
									.addComponent(btnOpenOsdspec, GroupLayout.PREFERRED_SIZE, 119, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(btnRegisterOsdspec, 0, 0, Short.MAX_VALUE))
								.addComponent(osdSpecpathTextField, GroupLayout.PREFERRED_SIZE, 241, GroupLayout.PREFERRED_SIZE)))
						.addGroup(gl_panel_2.createSequentialGroup()
							.addGap(57)
							.addComponent(btnRegisterService, GroupLayout.PREFERRED_SIZE, 147, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		gl_panel_2.setVerticalGroup(
			gl_panel_2.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_2.createSequentialGroup()
					.addGroup(gl_panel_2.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnOpenOsdspec, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnRegisterOsdspec))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(osdSpecpathTextField, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED, 40, Short.MAX_VALUE)
					.addComponent(btnRegisterService))
		);
		panel_2.setLayout(gl_panel_2);
		
		JPanel panel_3 = new JPanel();
		panel_3.setBorder(new TitledBorder(null, "oamo", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_3.setBounds(10, 126, 167, 166);
		frmSchedulerClient.getContentPane().add(panel_3);
		
		JButton btnGetOAMO = new JButton("get oamo");
		btnGetOAMO.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				schedulerClient.getApplication(textFieldOAMOID.getText());
			}
		});
		
		JLabel lblId = new JLabel("id");
		
		textFieldOAMOID = new JTextField();
		textFieldOAMOID.setText("nodeID://b47205");
		textFieldOAMOID.setColumns(10);
		
		JLabel lblOamoUid = new JLabel("uid");
		
		textFieldOAMOuserID = new JTextField();
		textFieldOAMOuserID.setText("nodeID://b47204");
		textFieldOAMOuserID.setColumns(10);
		
		JButton btnGetOamoIds = new JButton("get oamo ids");
		btnGetOamoIds.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				schedulerClient.getAvailableAppIDs(textFieldOAMOuserID.getText());
			}
		});
		GroupLayout gl_panel_3 = new GroupLayout(panel_3);
		gl_panel_3.setHorizontalGroup(
			gl_panel_3.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_3.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_3.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel_3.createParallelGroup(Alignment.LEADING, false)
							.addComponent(btnGetOAMO, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addGroup(gl_panel_3.createSequentialGroup()
								.addComponent(lblId, GroupLayout.PREFERRED_SIZE, 17, GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(ComponentPlacement.RELATED)
								.addComponent(textFieldOAMOID, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
						.addGroup(gl_panel_3.createSequentialGroup()
							.addComponent(lblOamoUid, GroupLayout.PREFERRED_SIZE, 17, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(textFieldOAMOuserID, GroupLayout.PREFERRED_SIZE, 116, GroupLayout.PREFERRED_SIZE))
						.addComponent(btnGetOamoIds, GroupLayout.PREFERRED_SIZE, 138, GroupLayout.PREFERRED_SIZE))
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		gl_panel_3.setVerticalGroup(
			gl_panel_3.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panel_3.createSequentialGroup()
					.addGroup(gl_panel_3.createParallelGroup(Alignment.BASELINE)
						.addComponent(textFieldOAMOID, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblId))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnGetOAMO)
					.addGap(18)
					.addGroup(gl_panel_3.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblOamoUid)
						.addComponent(textFieldOAMOuserID, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(btnGetOamoIds)
					.addGap(37))
		);
		panel_3.setLayout(gl_panel_3);
		
		JPanel panel_4 = new JPanel();
		panel_4.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "osmo", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_4.setBounds(189, 126, 194, 166);
		frmSchedulerClient.getContentPane().add(panel_4);
		
		JButton btnGetOsmo = new JButton("get osmo");
		btnGetOsmo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				schedulerClient.getService(textFieldOSMOId.getText());
			}
		});
		
		JLabel label = new JLabel("id");
		
		textFieldOSMOId = new JTextField();
		textFieldOSMOId.setText("nodeID://b47207");
		textFieldOSMOId.setColumns(10);
		
		JLabel lblOamoid = new JLabel("oamoid");
		
		txtOAMOid = new JTextField();
		txtOAMOid.setText("nodeID://b47205");
		txtOAMOid.setColumns(10);
		
		JButton btnNewButton = new JButton("get osmo ids");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				schedulerClient.getAvailableServiceIDs(txtOAMOid.getText());
			}
		});
		GroupLayout gl_panel_4 = new GroupLayout(panel_4);
		gl_panel_4.setHorizontalGroup(
			gl_panel_4.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, gl_panel_4.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_4.createParallelGroup(Alignment.TRAILING)
						.addComponent(btnNewButton, GroupLayout.DEFAULT_SIZE, 166, Short.MAX_VALUE)
						.addGroup(Alignment.LEADING, gl_panel_4.createParallelGroup(Alignment.LEADING, false)
							.addComponent(btnGetOsmo, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addGroup(gl_panel_4.createSequentialGroup()
								.addComponent(label, GroupLayout.PREFERRED_SIZE, 17, GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(ComponentPlacement.RELATED)
								.addComponent(textFieldOSMOId, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
						.addGroup(Alignment.LEADING, gl_panel_4.createSequentialGroup()
							.addComponent(lblOamoid, GroupLayout.PREFERRED_SIZE, 55, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addComponent(txtOAMOid, GroupLayout.PREFERRED_SIZE, 106, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap())
		);
		gl_panel_4.setVerticalGroup(
			gl_panel_4.createParallelGroup(Alignment.TRAILING)
				.addGroup(Alignment.LEADING, gl_panel_4.createSequentialGroup()
					.addGroup(gl_panel_4.createParallelGroup(Alignment.BASELINE)
						.addComponent(textFieldOSMOId, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(label))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnGetOsmo)
					.addGap(18)
					.addGroup(gl_panel_4.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblOamoid)
						.addComponent(txtOAMOid, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(btnNewButton)
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		panel_4.setLayout(gl_panel_4);
		
		JPanel panel_5 = new JPanel();
		panel_5.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "get osdspec", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_5.setBounds(279, 327, 161, 142);
		frmSchedulerClient.getContentPane().add(panel_5);
		
		JButton btnGetOSDSpec = new JButton("get");
		btnGetOSDSpec.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				schedulerClient.getAvailableApps(textFieldGetSpecUserID.getText());
			}
		});
		
		JLabel lblUserid = new JLabel("userid");
		
		textFieldGetSpecUserID = new JTextField();
		textFieldGetSpecUserID.setText("nodeID://b47204");
		textFieldGetSpecUserID.setColumns(10);
		GroupLayout gl_panel_5 = new GroupLayout(panel_5);
		gl_panel_5.setHorizontalGroup(
			gl_panel_5.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_5.createSequentialGroup()
					.addGroup(gl_panel_5.createParallelGroup(Alignment.LEADING)
						.addGroup(Alignment.TRAILING, gl_panel_5.createSequentialGroup()
							.addGap(4)
							.addGroup(gl_panel_5.createParallelGroup(Alignment.TRAILING)
								.addComponent(btnGetOSDSpec, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 112, Short.MAX_VALUE)
								.addComponent(textFieldGetSpecUserID, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 77, Short.MAX_VALUE)))
						.addGroup(gl_panel_5.createSequentialGroup()
							.addGap(36)
							.addComponent(lblUserid)))
					.addContainerGap())
		);
		gl_panel_5.setVerticalGroup(
			gl_panel_5.createParallelGroup(Alignment.TRAILING)
				.addGroup(Alignment.LEADING, gl_panel_5.createSequentialGroup()
					.addGap(7)
					.addComponent(lblUserid)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(textFieldGetSpecUserID, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED, 27, Short.MAX_VALUE)
					.addComponent(btnGetOSDSpec)
					.addContainerGap())
		);
		panel_5.setLayout(gl_panel_5);
		btnDiscoverSensors.addActionListener(new BtnDiscoverSensorsActionListener());
		btnWelcome.addActionListener(new BtnWelcomeActionListener());
	}
	private class BtnWelcomeActionListener implements ActionListener 
	{
		public void actionPerformed(ActionEvent e) 
		{			
			String welcomeMsg = schedulerClient.welcomeMessage();			
		}
	}
	private class BtnDiscoverSensorsActionListener implements ActionListener 
	{
		public void actionPerformed(ActionEvent e) 
		{			
			SensorTypes stypes= schedulerClient.discoverSensors(
					Double.valueOf(textFieldLong.getText()),
					Double.valueOf(textFieldLat.getText()),
					Float.valueOf(textFieldRad.getText()));	

		}
	}
	private class BtnRegisterServiceActionListener implements ActionListener 
	{
		public void actionPerformed(ActionEvent e) 
		{			
			String registerMsg = schedulerClient.registerDemoService();			
		}
	}
	private class BtnOpenOsdspecActionListener implements ActionListener 
	{
		public void actionPerformed(ActionEvent e) 
		{
			JFileChooser fc = new JFileChooser();

			// FileFilter filter = null;
			// File f = null;
			// f.
			// filter.accept(f);
			// fc.setFileFilter(filter);
			
			int returnVal = fc.showOpenDialog(SchedulerClientGUI.this);
	        if (returnVal == JFileChooser.APPROVE_OPTION){
	            File file = fc.getSelectedFile();	        
	            osdSpecpathTextField.setText(file.getAbsolutePath());	            
	        } else {
	        	logger.debug("Open command cancelled by user." + "\n");
	        }			
		}
	}
	private class BtnRegisterOsdspecActionListener implements ActionListener 
	{
		public void actionPerformed(ActionEvent e) 
		{			
			try {
				String responseMsg = schedulerClient.registerFromFile(osdSpecpathTextField.getText());
				if (responseMsg==null){
					JOptionPane.showMessageDialog(SchedulerClientGUI.this, 
							"Error registering service. Check log.", 
							"Register service error.", 
							JOptionPane.ERROR_MESSAGE);
				}				
			} catch (FileNotFoundException ex) {
				JOptionPane.showMessageDialog(SchedulerClientGUI.this, 
						"File not found. Please check the path of the file.", 
						"File not found.", 
						JOptionPane.ERROR_MESSAGE);
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(SchedulerClientGUI.this, 
						"Error opening file.", 
						"Error opening file.", 
						JOptionPane.ERROR_MESSAGE);
			}			
		}
	}
	
	
	//launch swing client
	public static void main(String[] args) 
	{	
		EventQueue.invokeLater(new Runnable() 
		{
			public void run() 
			{
				try {
					// Set System L&F
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());					
					
					SchedulerClientGUI window = new SchedulerClientGUI();
					window.frmSchedulerClient.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
