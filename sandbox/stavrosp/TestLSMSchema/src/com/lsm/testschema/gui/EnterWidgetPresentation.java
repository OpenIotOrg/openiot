package com.lsm.testschema.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

import lsm.beans.User;
import lsm.schema.LSMSchema;
import lsm.server.LSMTripleStore;

import com.hp.hpl.jena.ontology.OntModelSpec;
import com.lsm.testschema.model.Service;
import com.lsm.testschema.model.WidgetAttributes;
import com.lsm.testschema.model.WidgetAvailable;
import com.lsm.testschema.model.WidgetPresentation;

public class EnterWidgetPresentation extends JDialog 
{

	private JPanel contentPane;	
	private JPanel panelWidget2;
	private JTabbedPane tabbedPaneWidgetAttrs;
	private Service service;
	private JTextField textFieldService;

	public EnterWidgetPresentation(Service service,List<WidgetAvailable> wa) 
	{
		init();
		
		////addTab("test");
		
		for (int i = 0; i < wa.size(); i++)
		{
			addTab(wa.get(i).getId());
		}
		
		this.service=service;		
		this.textFieldService.setText(service.getId());
	}
	
	private void init()
	{
		setTitle("Enter Widget Presentation");
		this.setModal(true);
		setBounds(100, 100, 451, 504);
		setLocationRelativeTo(null);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		this.setContentPane(contentPane);
		contentPane.setLayout(null);
		
		
		JButton buttonServiceInsert = new JButton("Insert");
		buttonServiceInsert.addActionListener(new ActionListener() 
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

				
				Service srvc = new Service(myOnt, myOntInstance,"http://lsm.deri.ie/OpenIoT/testSchema#",lsmStore);				
				srvc.setId(service.getId());
				//
				srvc.createClassIdv();
				
				
				/////	widget stuff	/////
				
				for(int j=0; j<tabbedPaneWidgetAttrs.getTabCount(); j++)
				{					
					JPanel tabPanel = (JPanel)tabbedPaneWidgetAttrs.getComponent(j);
					JScrollPane scrolPane= ((JScrollPane)tabPanel.getComponent(0));
					JTable jt = (JTable)scrolPane.getViewport().getComponent(0);
					DefaultTableModel dModel = (DefaultTableModel)jt.getModel();
					
					int rc = dModel.getRowCount();
					if(rc==0)
					{
						continue;
					}
					
					WidgetPresentation widgetPre = new WidgetPresentation(myOnt, myOntInstance,"http://lsm.deri.ie/OpenIoT/testSchema#",lsmStore);
					//widgetPre.setDescription("desc");
					widgetPre.setService(srvc);
					//
					widgetPre.createClassIdv();
					//widgetPre.createPdescription();
					widgetPre.createPwidgetPresOf();
					//widgetP.addWidget(widget);
					//widgetP.addWidgetAttr(widgetAttr)
					
					WidgetAvailable wAvail = new WidgetAvailable(myOnt, myOntInstance,"http://lsm.deri.ie/OpenIoT/testSchema#",lsmStore);				
					wAvail.setId(tabbedPaneWidgetAttrs.getTitleAt(j));
					wAvail.setWidgetPre(widgetPre);
					///
					wAvail.createClassIdv();
					wAvail.createPWidgetOf();				

					
					//WidgetAttributes[] wAttrArr = new WidgetAttributes[rc];
					for(int i=0; i<rc; i++)
					{
						WidgetAttributes wAttr = new WidgetAttributes(myOnt, myOntInstance,"http://lsm.deri.ie/OpenIoT/testSchema#",lsmStore);
						wAttr.setDescription((String)dModel.getValueAt(i, 0));
						wAttr.setName((String)dModel.getValueAt(i, 1));
						wAttr.setWidgetPre(widgetPre);
						///
						wAttr.createClassIdv();
						wAttr.createPdesc();
						wAttr.createPname();
						wAttr.createPWidgetAttrOf();
						
						widgetPre.addWidgetAttr(wAttr);
						widgetPre.createPwidgetAttr();
						//wAttrArr[i]=wAttr;
					}				
				
					widgetPre.setWidgetAvailable(wAvail);
					widgetPre.createPwidget();
					srvc.addWidgetPresentation(widgetPre);
					srvc.createPwidgetPres();
				}
				////	widget stuff end   /////
				
				if(srvc.getWidgetPresList().size()!=0)
				{					
					lsmStore.pushRDF("http://lsm.deri.ie/OpenIoT/testSchema#",myOntInstance.exportToTriples("N-TRIPLE"));
				}
				else
				{
					System.out.println("\n widget not added to service \n");
				}
				System.out.println(myOntInstance.exportToTriples("TURTLE"));
				dispose();
			}
		});
		buttonServiceInsert.setBounds(12, 428, 97, 25);
		contentPane.add(buttonServiceInsert);
		
		JButton buttonStatusInsertCancel = new JButton("Cancel");
		buttonStatusInsertCancel.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				dispose();
			}
		});
		buttonStatusInsertCancel.setBounds(326, 428, 97, 25);
		contentPane.add(buttonStatusInsertCancel);
		
		panelWidget2 = new JPanel();
		panelWidget2.setBorder(new TitledBorder(null, "Widget", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelWidget2.setBounds(12, 45, 411, 375);
		contentPane.add(panelWidget2);
				
		
		tabbedPaneWidgetAttrs = new JTabbedPane(JTabbedPane.TOP);
		GroupLayout gl_panelWidget2 = new GroupLayout(panelWidget2);
		gl_panelWidget2.setHorizontalGroup
		(
			gl_panelWidget2.createParallelGroup(Alignment.LEADING)
				.addComponent(tabbedPaneWidgetAttrs, GroupLayout.DEFAULT_SIZE, 399, Short.MAX_VALUE)
		);
		gl_panelWidget2.setVerticalGroup
		(
			gl_panelWidget2.createParallelGroup(Alignment.LEADING)
				.addComponent(tabbedPaneWidgetAttrs, GroupLayout.DEFAULT_SIZE, 436, Short.MAX_VALUE)
		);
		panelWidget2.setLayout(gl_panelWidget2);		
		
		JLabel labelSrvcID = new JLabel("SrvcID");
		labelSrvcID.setBounds(95, 16, 56, 16);
		contentPane.add(labelSrvcID);
		
		textFieldService = new JTextField();
		textFieldService.setEditable(false);
		textFieldService.setColumns(10);
		textFieldService.setBounds(179, 13, 116, 22);
		contentPane.add(textFieldService);
	}
	
	private void addTab(String widgetNode)
	{
		JPanel panelOfTabbedPane = new JPanel();
		tabbedPaneWidgetAttrs.addTab(widgetNode, null, panelOfTabbedPane, null);
				
		JScrollPane scrollPane = new JScrollPane();
		
		final JTable tableAttributes2 = new JTable();
		tableAttributes2.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"Name", "Description"
			}
		) {
			Class[] columnTypes = new Class[] {
				String.class, String.class
			};
			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}
		});
		tableAttributes2.setBounds(0, 0, 1, 1);
		tableAttributes2.setRowSelectionAllowed(false);
		scrollPane.setViewportView(tableAttributes2);
		
		JButton btnAddAttr = new JButton("Add");
		btnAddAttr.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				((DefaultTableModel)tableAttributes2.getModel()).
				insertRow(	((DefaultTableModel)tableAttributes2.getModel()).getRowCount(), new Object[] 
						{
//							usrList.get(i).getId(),
//							usrList.get(i).getName(),
//							usrList.get(i).getEmail(),
//							usrList.get(i).getDescription()
						});
			}
		});		
		
		final JButton btnDelAttr = new JButton("Delete");
		btnDelAttr.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				DefaultTableModel tb = (DefaultTableModel)tableAttributes2.getModel();				
				if (tb.getRowCount() > 0)
				{
					tb.removeRow(tb.getRowCount()-1);
				}
			}
		});
		
		GroupLayout gl_panelOfTabbedPane = new GroupLayout(panelOfTabbedPane);
		gl_panelOfTabbedPane.setHorizontalGroup
		(
			gl_panelOfTabbedPane.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, gl_panelOfTabbedPane.createSequentialGroup()
						.addContainerGap()
						.addGroup(gl_panelOfTabbedPane.createParallelGroup(Alignment.TRAILING)
						.addComponent(scrollPane, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 363, Short.MAX_VALUE)
						.addGroup(gl_panelOfTabbedPane.createSequentialGroup()
							.addComponent(btnAddAttr, GroupLayout.PREFERRED_SIZE, 97, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED, 180, Short.MAX_VALUE)
							.addComponent(btnDelAttr, GroupLayout.PREFERRED_SIZE, 86, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap())
		);
		gl_panelOfTabbedPane.setVerticalGroup
		(
			gl_panelOfTabbedPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelOfTabbedPane.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panelOfTabbedPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnAddAttr)
						.addComponent(btnDelAttr))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 304, Short.MAX_VALUE)
					.addContainerGap())
		);
		panelOfTabbedPane.setLayout(gl_panelOfTabbedPane);	
	}
}//class
