package com.lsm.testschema.gui;

import java.awt.EventQueue;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JLabel;

import com.hp.hpl.jena.ontology.OntModelSpec;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Dimension;

import lsm.beans.User;
import lsm.schema.LSMSchema;
import lsm.server.LSMTripleStore;

public class EnterUser extends JDialog
{
	private JTextField textFieldUserId;
	private JTextField textFieldName;
	private JTextField textFieldEmail;
	private JTextField textFieldDescription;

	
	public EnterUser() 
	{		
		initialize();
	}

	private void initialize() 
	{
		setSize(new Dimension(270, 250));
		this.setModal(true);
		this.setTitle("Enter User");
		this.getContentPane().setLayout(null);
		setLocationRelativeTo(null);
		//this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		
		JButton btnUserInsert = new JButton("Insert");
		btnUserInsert.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				User user = new User();
				user.setUsername("spet");
				user.setPass("spetlsm");
				
				LSMTripleStore lsmStore = new LSMTripleStore();
				lsmStore.setUser(user);
				
				LSMSchema myOnt  =  new  LSMSchema("files\\savedFromProtegeCopy.owl", OntModelSpec.OWL_DL_MEM,"TURTLE");
				LSMSchema myOntInstance = new LSMSchema();
				
				com.lsm.testschema.model.User usr = new com.lsm.testschema.model.User(myOnt, myOntInstance,"http://lsm.deri.ie/OpenIoT/testSchema#",lsmStore);
				usr.setName(textFieldName.getText());
				usr.setEmail(textFieldEmail.getText());
				usr.setDescription(textFieldDescription.getText());
				
				usr.createOnt_USer();
				System.out.println(myOntInstance.exportToTriples("TURTLE"));
				boolean ok =lsmStore.pushRDF("http://lsm.deri.ie/OpenIoT/testSchema#",myOntInstance.exportToTriples("N-TRIPLE"));
				
				if(ok)
				{
					MainForm.getMainForm().clearUserTable();
					MainForm.getMainForm().fillUserTable();
				}
				else
				{
					//ERROR MESSAGE;
				}
				
				dispose();
			}
		});
		btnUserInsert.setBounds(12, 172, 97, 25);
		this.getContentPane().add(btnUserInsert);
		
		JButton btnUserCancel = new JButton("Cancel");
		btnUserCancel.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				dispose();
			}
		});
		btnUserCancel.setBounds(131, 172, 97, 25);
		this.getContentPane().add(btnUserCancel);
		
		textFieldUserId = new JTextField();
		textFieldUserId.setEnabled(false);
		textFieldUserId.setBounds(112, 10, 116, 22);
		this.getContentPane().add(textFieldUserId);
		textFieldUserId.setColumns(10);
		
		textFieldName = new JTextField();
		textFieldName.setBounds(112, 45, 116, 22);
		this.getContentPane().add(textFieldName);
		textFieldName.setColumns(10);
		
		textFieldEmail = new JTextField();
		textFieldEmail.setBounds(112, 80, 116, 22);
		this.getContentPane().add(textFieldEmail);
		textFieldEmail.setColumns(10);
		
		textFieldDescription = new JTextField();
		textFieldDescription.setBounds(112, 115, 116, 22);
		this.getContentPane().add(textFieldDescription);
		textFieldDescription.setColumns(10);
		
		JLabel lblUserID = new JLabel("ID");
		lblUserID.setEnabled(false);
		lblUserID.setBounds(63, 13, 12, 16);
		this.getContentPane().add(lblUserID);
		
		JLabel lblUserName = new JLabel("Name");
		lblUserName.setBounds(42, 48, 33, 16);
		this.getContentPane().add(lblUserName);
		
		JLabel lblEmail = new JLabel("e-Mail");
		lblEmail.setBounds(40, 83, 35, 16);
		this.getContentPane().add(lblEmail);
		
		JLabel lblDescription = new JLabel("Description");
		lblDescription.setBounds(12, 118, 63, 16);
		this.getContentPane().add(lblDescription);
	}
	
}//class
