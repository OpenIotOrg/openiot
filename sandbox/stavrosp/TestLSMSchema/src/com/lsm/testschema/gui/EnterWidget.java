package com.lsm.testschema.gui;

import java.awt.EventQueue;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;

import com.hp.hpl.jena.ontology.OntModelSpec;
import com.lsm.testschema.model.WidgetAvailable;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import lsm.beans.User;
import lsm.schema.LSMSchema;
import lsm.server.LSMTripleStore;

public class EnterWidget extends JDialog 
{
	private JTextField textFieldWidgetID;
	private JTextField textFieldDesc;
	private JTextField textFieldName;
	private JTextField textFieldURL;
	private JTextField textFieldType;
	
	public EnterWidget()
	{
		init();
	}
	
	public void init() 
	{
		setModal(true);
		setTitle("Enter Widget");
		setBounds(100, 100, 316, 280);
		getContentPane().setLayout(null);
		setLocationRelativeTo(null);
		
		JLabel WidgetID = new JLabel("ID");
		WidgetID.setEnabled(false);
		WidgetID.setBounds(73, 16, 12, 16);
		getContentPane().add(WidgetID);
		
		textFieldWidgetID = new JTextField();
		textFieldWidgetID.setEnabled(false);
		textFieldWidgetID.setBounds(158, 13, 116, 22);
		getContentPane().add(textFieldWidgetID);
		textFieldWidgetID.setColumns(10);
		
		JLabel labelDesc = new JLabel("Description");
		labelDesc.setBounds(26, 81, 72, 16);
		getContentPane().add(labelDesc);
		
		textFieldDesc = new JTextField();
		textFieldDesc.setColumns(10);
		textFieldDesc.setBounds(158, 78, 116, 22);
		getContentPane().add(textFieldDesc);
		
		JLabel labelName = new JLabel("Name");
		labelName.setBounds(52, 110, 33, 16);
		getContentPane().add(labelName);
		
		textFieldName = new JTextField();
		textFieldName.setColumns(10);
		textFieldName.setBounds(158, 110, 116, 22);
		getContentPane().add(textFieldName);
		
		textFieldURL = new JTextField();
		textFieldURL.setColumns(10);
		textFieldURL.setBounds(158, 139, 116, 22);
		getContentPane().add(textFieldURL);
		
		JLabel labelURL = new JLabel("URL");
		labelURL.setBounds(52, 139, 33, 16);
		getContentPane().add(labelURL);
		
		JButton buttonInsert = new JButton("Insert");
		buttonInsert.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				User user = new User();
				user.setUsername("spet");
				user.setPass("spetlsm");
				
				LSMTripleStore lsmStore = new LSMTripleStore();
				lsmStore.setUser(user);
				
				LSMSchema myOnt  =  new  LSMSchema("files\\savedFromProtegeCopy.owl", OntModelSpec.OWL_DL_MEM,"TURTLE");
				LSMSchema myOntInstance = new LSMSchema();
				
				WidgetAvailable widget = new WidgetAvailable(myOnt, myOntInstance,"http://lsm.deri.ie/OpenIoT/testSchema#",lsmStore);//,textFieldType.getText());
				widget.setType(textFieldType.getText());
				widget.setName(textFieldName.getText());
				widget.setDescription(textFieldDesc.getText());
				widget.setLocationURL(textFieldURL.getText());
				//				
				widget.createClassIdv();
				widget.createPtype();
				widget.createPdescription();
				widget.createPname();
				widget.createPlocationURL();
				
											
				System.out.println(myOntInstance.exportToTriples("TURTLE"));
				boolean ok =lsmStore.pushRDF("http://lsm.deri.ie/OpenIoT/testSchema#",myOntInstance.exportToTriples("N-TRIPLE"));
				
				if(ok)
				{
					MainForm.getMainForm().clearWidgetAvailTable();
					MainForm.getMainForm().fillWidgetAvailTable();
				}
				else
				{
					System.out.println("ERROR INSERTING WIDGET AVAILABLE");
				}
				
				dispose();
			}
		});
		buttonInsert.setBounds(26, 195, 97, 25);
		getContentPane().add(buttonInsert);
		
		JButton buttonCancel = new JButton("Cancel");
		buttonCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				dispose();
			}
		});
		buttonCancel.setBounds(177, 195, 97, 25);
		getContentPane().add(buttonCancel);
		
		JLabel lblType = new JLabel("Type");
		lblType.setBounds(52, 52, 33, 16);
		getContentPane().add(lblType);
		
		textFieldType = new JTextField();
		textFieldType.setColumns(10);
		textFieldType.setBounds(158, 46, 116, 22);
		getContentPane().add(textFieldType);

	}
}//class
