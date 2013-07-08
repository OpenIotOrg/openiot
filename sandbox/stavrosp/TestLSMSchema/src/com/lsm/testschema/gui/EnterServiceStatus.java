package com.lsm.testschema.gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;


import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;

import com.hp.hpl.jena.ontology.OntModelSpec;
import com.lsm.testschema.model.Service;
import com.lsm.testschema.model.ServiceStatus;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.Date;

import lsm.beans.User;
import lsm.schema.LSMSchema;
import lsm.server.LSMTripleStore;
import com.lsm.testschema.model.ServiceStatus.State;

public class EnterServiceStatus extends JDialog
{

	private JPanel contentPane;
	private JTextField textFieldServiceID;
	private JTextField textFieldSrvcStatID;
	private JComboBox comboBox;

	public EnterServiceStatus(Service tableService) 
	{
		init();
		
		textFieldServiceID.setText(tableService.getId());		
	}
	
	private void init()
	{
		setTitle("Insert Service Status");
		this.setModal(true);		
		setBounds(100, 100, 296, 223);
		setLocationRelativeTo(null);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel labelServiceId = new JLabel("ServiceID");
		labelServiceId.setBounds(34, 39, 65, 16);
		contentPane.add(labelServiceId);
		
		textFieldServiceID = new JTextField();
		textFieldServiceID.setEditable(false);
		textFieldServiceID.setColumns(10);
		textFieldServiceID.setBounds(139, 36, 123, 22);
		contentPane.add(textFieldServiceID);
		
		JLabel labelServiceStatus = new JLabel("Status");
		labelServiceStatus.setBounds(56, 103, 43, 16);
		contentPane.add(labelServiceStatus);
		
		JButton buttonServiceStatusInsert = new JButton("Insert");
		buttonServiceStatusInsert.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				User user = new User();
				user.setUsername("spet");
				user.setPass("spetlsm");
				
				LSMTripleStore lsmStore = new LSMTripleStore();
				lsmStore.setUser(user);
				
				LSMSchema myOnt  =  new  LSMSchema("files\\savedFromProtegeCopy.owl", OntModelSpec.OWL_DL_MEM,"TURTLE");
				LSMSchema myOntInstance = new LSMSchema();
				
				ServiceStatus srvcstat= new ServiceStatus(myOnt, myOntInstance,"http://lsm.deri.ie/OpenIoT/testSchema#",lsmStore,(ServiceStatus.State)comboBox.getSelectedItem());				
				srvcstat.setTime(new Date().toGMTString());
//				srvcstat.setStatus((ServiceStatus.State)comboBox.getSelectedItem());
				//
				srvcstat.createClassIdv();
				srvcstat.createPsrvcStatTime();
				
				
				Service srvc = new Service(myOnt, myOntInstance,"http://lsm.deri.ie/OpenIoT/testSchema#",lsmStore);
				srvc.setId(textFieldServiceID.getText());
				srvc.addServiceStatus(srvcstat);
				//
				srvc.createClassIdv();
				srvc.createPserviceStatus();
				
				srvcstat.setServiceOf(srvc);
				srvcstat.createPsrvcStatOf();
				
				System.out.println(myOntInstance.exportToTriples("TURTLE"));
				lsmStore.pushRDF("http://lsm.deri.ie/OpenIoT/testSchema#",myOntInstance.exportToTriples("N-TRIPLE"));
				dispose();
			}
		});
		buttonServiceStatusInsert.setBounds(23, 138, 76, 25);
		contentPane.add(buttonServiceStatusInsert);
		
		JButton buttonServiceStatusInsertCancel = new JButton("Cancel");
		buttonServiceStatusInsertCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				dispose();
			}
		});
		buttonServiceStatusInsertCancel.setBounds(177, 138, 85, 25);
		contentPane.add(buttonServiceStatusInsertCancel);
		
		comboBox = new JComboBox();
		comboBox.setModel(new DefaultComboBoxModel(ServiceStatus.State.values()));
//		comboBox.setModel(new DefaultComboBoxModel(new String[] {"Initializing", "Enabled", "Error", "Inprogress", "Suspended", "Unsatisfied"}));
		comboBox.setBounds(139, 100, 123, 22);
		contentPane.add(comboBox);
		
		JLabel labelSrvcStatusID = new JLabel("SrvcStatID");
		labelSrvcStatusID.setEnabled(false);
		labelSrvcStatusID.setBounds(34, 68, 65, 16);
		contentPane.add(labelSrvcStatusID);
		
		textFieldSrvcStatID = new JTextField();
		textFieldSrvcStatID.setEnabled(false);
		textFieldSrvcStatID.setColumns(10);
		textFieldSrvcStatID.setBounds(139, 65, 123, 22);
		contentPane.add(textFieldSrvcStatID);
	}
}//class
