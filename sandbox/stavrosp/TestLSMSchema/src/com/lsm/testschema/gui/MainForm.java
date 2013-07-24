package com.lsm.testschema.gui;

import javax.swing.JFrame;

import javax.swing.JTable;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.JButton;

import org.openrdf.query.BindingSet;
import org.openrdf.query.TupleQueryResult;

import com.lsm.testschema.model.Service;
import com.lsm.testschema.model.ServiceStatus;
import com.lsm.testschema.model.User;
import com.lsm.testschema.model.WidgetAvailable;
import com.lsm.testschema.model.WidgetAttributes;
import com.lsm.testschema.model.WidgetPresentation;
import com.lsm.testschema.queryhelper.SesameSPARQLClient;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import java.awt.Dimension;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;


public class MainForm extends JFrame //implements ListSelectionListener
{
	private static MainForm mainform;
	
	
	private JTable tableUser;
	private JTable tableService;
	private JTable tableServiceStatus;
	private JTable tableWidgetPre;
	private JTable tableWidgetAttr;
	private JTable tableWidgetAvailable;
	
	private MainForm() 
	{
		setTitle("Test LSM store");		
		initialize();
	}
	
	private void initialize() 
	{
		
		this.setBounds(100, 100, 1479, 649);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.getContentPane().setLayout(null);
		setLocationRelativeTo(null);
		
		JPanel panelUser = new JPanel();
		panelUser.setBorder(new TitledBorder(null, "User", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelUser.setBounds(12, 13, 474, 281);
		this.getContentPane().add(panelUser);
		
		JScrollPane scrollPaneUser = new JScrollPane();
		
		//table user
		tableUser = new JTable();
		scrollPaneUser.setViewportView(tableUser);
		tableUser.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"Id", "Name", "Description", "eMail"
			}
		) {
			Class[] columnTypes = new Class[] {
				String.class, String.class, String.class, String.class
			};
			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}
		});
		tableUser.getColumnModel().getColumn(0).setPreferredWidth(83);
		tableUser.getColumnModel().getColumn(2).setPreferredWidth(90);
		tableUser.getColumnModel().getColumn(3).setPreferredWidth(85);
		tableUser.setRowSelectionAllowed(true);
		tableUser.setColumnSelectionAllowed(false);
		tableUser.setSelectionMode( ListSelectionModel.SINGLE_SELECTION);
		//tableUser.getSelectionModel().addListSelectionListener(this);
		
		JButton btnNewUser = new JButton("NewUser");
		
		JButton btnRefreshUsr = new JButton("RefreshUsr");
		
		JButton btnClearUser = new JButton("Clear");
		btnClearUser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{				
				clearUserTable();
			}
		});
		GroupLayout gl_panelUser = new GroupLayout(panelUser);
		gl_panelUser.setHorizontalGroup(
			gl_panelUser.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelUser.createSequentialGroup()
					.addGap(5)
					.addGroup(gl_panelUser.createParallelGroup(Alignment.LEADING)
						.addComponent(scrollPaneUser)
						.addGroup(gl_panelUser.createSequentialGroup()
							.addComponent(btnNewUser)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnClearUser)
							.addPreferredGap(ComponentPlacement.RELATED, 204, Short.MAX_VALUE)
							.addComponent(btnRefreshUsr)))
					.addGap(5))
		);
		gl_panelUser.setVerticalGroup(
			gl_panelUser.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panelUser.createSequentialGroup()
					.addComponent(scrollPaneUser, GroupLayout.DEFAULT_SIZE, 215, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panelUser.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnRefreshUsr)
						.addComponent(btnNewUser)
						.addComponent(btnClearUser))
					.addGap(9))
		);
		panelUser.setLayout(gl_panelUser);
		btnRefreshUsr.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) 
			{
				clearUserTable();
				fillUserTable();
			}
		});
		btnNewUser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{				
				EnterUser enterusr = new EnterUser();				
				enterusr.setVisible(true);				
			}
		});
		
		
		JPanel panelService = new JPanel();
		panelService.setBorder(new TitledBorder(null, "Service", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelService.setBounds(498, 13, 467, 281);
		this.getContentPane().add(panelService);
		
		JScrollPane scrollPaneService = new JScrollPane();
		
		//table panel
		tableService = new JTable();
		scrollPaneService.setViewportView(tableService);
		tableService.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"ID", "Name", "Description", "Query", "ServiceOf"
			}
		) {
			Class[] columnTypes = new Class[] {
				String.class, String.class, String.class, String.class, String.class
			};
			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}
		});
		tableService.setRowSelectionAllowed(true);
		tableService.setColumnSelectionAllowed(false);
		tableService.setSelectionMode( ListSelectionModel.SINGLE_SELECTION);
		//tableService.getSelectionModel().addListSelectionListener(this);
		
		JButton btnNewService = new JButton("NewService");
		btnNewService.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{				
				com.lsm.testschema.model.User tableUser = getSelectedUser();
				List<WidgetAvailable> wa =getAllWidgetAvail();
				if(tableUser==null)
				{					
					System.out.println("SELECT USER");
				}
				else if(wa==null)
				{
					System.out.println("NO WIDGETS AVAILABLE");
				}
				else
				{
					EnterService entersrvc = new EnterService(tableUser,wa);				
					entersrvc.setVisible(true);										
				}				
			}
		});
		
		JButton btnRefreshSrvc = new JButton("RefreshSrvc");
		btnRefreshSrvc.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				clearServiceTable();
				com.lsm.testschema.model.User tableUser = getSelectedUser();
				fillServiceTable(tableUser);				
			}
		});
		
		JButton btnClearService = new JButton("Clear");
		btnClearService.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				DefaultTableModel tb = (DefaultTableModel)tableService.getModel();				
				while (tb.getRowCount() > 0){
					tb.removeRow(0);
				}
			}
		});
		GroupLayout gl_panelService = new GroupLayout(panelService);
		gl_panelService.setHorizontalGroup(
			gl_panelService.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, gl_panelService.createSequentialGroup()
					.addGroup(gl_panelService.createParallelGroup(Alignment.TRAILING)
						.addGroup(gl_panelService.createSequentialGroup()
							.addContainerGap()
							.addComponent(scrollPaneService, GroupLayout.DEFAULT_SIZE, 431, Short.MAX_VALUE))
						.addGroup(gl_panelService.createSequentialGroup()
							.addGap(12)
							.addComponent(btnNewService, GroupLayout.PREFERRED_SIZE, 111, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnClearService)
							.addPreferredGap(ComponentPlacement.RELATED, 139, Short.MAX_VALUE)
							.addComponent(btnRefreshSrvc, GroupLayout.PREFERRED_SIZE, 111, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap())
		);
		gl_panelService.setVerticalGroup(
			gl_panelService.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panelService.createSequentialGroup()
					.addComponent(scrollPaneService, GroupLayout.DEFAULT_SIZE, 216, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panelService.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnNewService)
						.addComponent(btnClearService)
						.addComponent(btnRefreshSrvc))
					.addGap(8))
		);
		panelService.setLayout(gl_panelService);
		
		
		JPanel panelServiceStatus = new JPanel();
		panelServiceStatus.setBorder(new TitledBorder(null, "ServiceStatus", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelServiceStatus.setBounds(977, 13, 472, 281);
		this.getContentPane().add(panelServiceStatus);
		
		JScrollPane scrollPaneServiceStatus = new JScrollPane();
		
		//table service status
		tableServiceStatus = new JTable();
		scrollPaneServiceStatus.setViewportView(tableServiceStatus);		
		tableServiceStatus.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"ID", "Status", "Timestamp", "Status OF"
			}
		) {
			Class[] columnTypes = new Class[] {
				String.class, String.class, String.class, String.class
			};
			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}
		});
		tableServiceStatus.setRowSelectionAllowed(true);
		tableServiceStatus.setColumnSelectionAllowed(false);
		tableServiceStatus.setSelectionMode( ListSelectionModel.SINGLE_SELECTION);
		//tableServiceStatus.getSelectionModel().addListSelectionListener(this);
		
		JButton btnNewSrvcStat = new JButton("NewSrvcStatus");
		btnNewSrvcStat.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				Service tableService = getSelectedService();
				if(tableService!=null)
				{
					EnterServiceStatus entersrvcstat = new EnterServiceStatus(tableService);				
					entersrvcstat.setVisible(true);
				}
				else
				{
					System.out.println("NO SERVICE SELECTED!!!!");					
				}			
			}
		});
		
		JButton btnRefreshSrvcStat = new JButton("RefreshSrvcStat");
		btnRefreshSrvcStat.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				clearServiceStatusTable();
				Service tableService = getSelectedService();				
				fillServiceStatusTable(tableService);				
			}
		});
		
		JButton btnClearSrvcStatus = new JButton("Clear");
		btnClearSrvcStatus.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				DefaultTableModel tb = (DefaultTableModel)tableServiceStatus.getModel();				
				while (tb.getRowCount() > 0){
					tb.removeRow(0);
				}
			}
		});
		GroupLayout gl_panelServiceStatus = new GroupLayout(panelServiceStatus);
		gl_panelServiceStatus.setHorizontalGroup(
			gl_panelServiceStatus.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelServiceStatus.createSequentialGroup()
					.addGap(2)
					.addGroup(gl_panelServiceStatus.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panelServiceStatus.createSequentialGroup()
							.addComponent(scrollPaneServiceStatus)
							.addPreferredGap(ComponentPlacement.RELATED))
						.addGroup(gl_panelServiceStatus.createSequentialGroup()
							.addComponent(btnNewSrvcStat)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnClearSrvcStatus)
							.addPreferredGap(ComponentPlacement.RELATED, 138, Short.MAX_VALUE)
							.addComponent(btnRefreshSrvcStat)))
					.addGap(6))
		);
		gl_panelServiceStatus.setVerticalGroup(
			gl_panelServiceStatus.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panelServiceStatus.createSequentialGroup()
					.addComponent(scrollPaneServiceStatus, GroupLayout.DEFAULT_SIZE, 213, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panelServiceStatus.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnNewSrvcStat)
						.addComponent(btnClearSrvcStatus)
						.addComponent(btnRefreshSrvcStat))
					.addGap(11))
		);
		panelServiceStatus.setLayout(gl_panelServiceStatus);
		
		JPanel panelWidgetPre = new JPanel();
		panelWidgetPre.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "WidgetPre", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelWidgetPre.setBounds(498, 307, 467, 281);
		getContentPane().add(panelWidgetPre);
		
		JScrollPane scrollPaneWidgetPre = new JScrollPane();
		
		tableWidgetPre = new JTable();
		tableWidgetPre.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"ID", "Widget", "Service", "Attribute"
			}
		) {
			Class[] columnTypes = new Class[] {
				String.class, String.class, String.class, String.class
			};
			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}
		});
		tableWidgetPre.getColumnModel().getColumn(0).setPreferredWidth(94);
		tableWidgetPre.getColumnModel().getColumn(1).setPreferredWidth(110);
		tableWidgetPre.getColumnModel().getColumn(2).setPreferredWidth(99);
		tableWidgetPre.getColumnModel().getColumn(3).setPreferredWidth(117);
		scrollPaneWidgetPre.setViewportView(tableWidgetPre);
		
		JButton buttonNewWidgetPre = new JButton("NewWidgetPre");
		buttonNewWidgetPre.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				Service srvc = getSelectedService();
				List<WidgetAvailable> wa =getAllWidgetAvail();
				if(srvc==null)
				{					
					System.out.println("SELECT SERVICE");
				}
				else if(wa==null)
				{
					System.out.println("NO WIDGETS AVAILABLE");
				}
				else
				{
					EnterWidgetPresentation enterwPre = new EnterWidgetPresentation(srvc,wa);				
					enterwPre.setVisible(true);										
				}	
			}
		});
		
		JButton buttonClearWidgetPreTable = new JButton("Clear");
		buttonClearWidgetPreTable.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				clearWidgetPreTable();
			}
		});
		
		JButton buttonRefreshWidgetPreTable = new JButton("RefreshWidgetPre");
		buttonRefreshWidgetPreTable.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				clearWidgetPreTable();				
				fillWidgetPreTable(getSelectedService());
			}
		});
		GroupLayout gl_panelWidgetPre = new GroupLayout(panelWidgetPre);
		gl_panelWidgetPre.setHorizontalGroup(
			gl_panelWidgetPre.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panelWidgetPre.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panelWidgetPre.createParallelGroup(Alignment.TRAILING)
						.addComponent(scrollPaneWidgetPre, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 431, Short.MAX_VALUE)
						.addGroup(Alignment.LEADING, gl_panelWidgetPre.createSequentialGroup()
							.addComponent(buttonNewWidgetPre, GroupLayout.PREFERRED_SIZE, 126, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(buttonClearWidgetPreTable, GroupLayout.PREFERRED_SIZE, 72, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED, 87, Short.MAX_VALUE)
							.addComponent(buttonRefreshWidgetPreTable, GroupLayout.PREFERRED_SIZE, 145, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap())
		);
		gl_panelWidgetPre.setVerticalGroup(
			gl_panelWidgetPre.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panelWidgetPre.createSequentialGroup()
					.addComponent(scrollPaneWidgetPre, GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panelWidgetPre.createParallelGroup(Alignment.BASELINE)
						.addComponent(buttonNewWidgetPre)
						.addComponent(buttonRefreshWidgetPreTable)
						.addComponent(buttonClearWidgetPreTable))
					.addGap(4))
		);
		panelWidgetPre.setLayout(gl_panelWidgetPre);
		
		JPanel panelWidgetAttr = new JPanel();
		panelWidgetAttr.setBorder(new TitledBorder(null, "WidgetAttr", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelWidgetAttr.setBounds(977, 307, 472, 281);
		getContentPane().add(panelWidgetAttr);
		
		JScrollPane scrollPaneWidgetAttr = new JScrollPane();
		
		tableWidgetAttr = new JTable();
		tableWidgetAttr.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"ID", "Description", "Name", "WidgetAttrOf"
			}
		) {
			Class[] columnTypes = new Class[] {
				String.class, String.class, String.class, String.class
			};
			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}
		});
		tableWidgetAttr.getColumnModel().getColumn(0).setPreferredWidth(111);
		tableWidgetAttr.getColumnModel().getColumn(1).setPreferredWidth(122);
		tableWidgetAttr.getColumnModel().getColumn(2).setPreferredWidth(114);
		tableWidgetAttr.getColumnModel().getColumn(3).setPreferredWidth(122);
		scrollPaneWidgetAttr.setViewportView(tableWidgetAttr);
		
		JButton buttonNewWAttr = new JButton("NewWidgetAttr");
		buttonNewWAttr.setEnabled(false);
		
		JButton buttonCleatWAttr = new JButton("Clear");
		buttonCleatWAttr.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				clearWidgetAttrTable();
			}
		});
		
		JButton buttonRefreshWAttr = new JButton("RefreshWidgetAttr");
		buttonRefreshWAttr.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				clearWidgetAttrTable();				
				fillWidgetAttrTable(getSelectedWidgetPre());				
			}
		});
		GroupLayout gl_panelWidgetAttr = new GroupLayout(panelWidgetAttr);
		gl_panelWidgetAttr.setHorizontalGroup(
			gl_panelWidgetAttr.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelWidgetAttr.createSequentialGroup()
					.addGap(4)
					.addGroup(gl_panelWidgetAttr.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panelWidgetAttr.createSequentialGroup()
							.addComponent(scrollPaneWidgetAttr)
							.addPreferredGap(ComponentPlacement.RELATED))
						.addGroup(gl_panelWidgetAttr.createSequentialGroup()
							.addComponent(buttonNewWAttr, GroupLayout.PREFERRED_SIZE, 128, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(buttonCleatWAttr, GroupLayout.PREFERRED_SIZE, 75, GroupLayout.PREFERRED_SIZE)
							.addGap(97)
							.addComponent(buttonRefreshWAttr, GroupLayout.PREFERRED_SIZE, 145, Short.MAX_VALUE)))
					.addGap(4))
		);
		gl_panelWidgetAttr.setVerticalGroup(
			gl_panelWidgetAttr.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panelWidgetAttr.createSequentialGroup()
					.addComponent(scrollPaneWidgetAttr, GroupLayout.DEFAULT_SIZE, 221, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panelWidgetAttr.createParallelGroup(Alignment.BASELINE)
						.addComponent(buttonCleatWAttr)
						.addComponent(buttonNewWAttr)
						.addComponent(buttonRefreshWAttr))
					.addGap(3))
		);
		panelWidgetAttr.setLayout(gl_panelWidgetAttr);
		
		JPanel panelWidgetAvailable = new JPanel();
		panelWidgetAvailable.setBorder(new TitledBorder(null, "WidgetAvailable", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelWidgetAvailable.setBounds(12, 307, 474, 281);
		getContentPane().add(panelWidgetAvailable);
		
		JScrollPane scrollPaneWidgetAvailable = new JScrollPane();
		
		tableWidgetAvailable = new JTable();
		tableWidgetAvailable.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"ID", "Name", "Description", "Location", "Type"
			}
		) {
			Class[] columnTypes = new Class[] {
				String.class, String.class, String.class, String.class, String.class
			};
			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}
		});
		scrollPaneWidgetAvailable.setViewportView(tableWidgetAvailable);
		
		JButton buttonWidgetAvailNew = new JButton("NewAvailWidget");
		buttonWidgetAvailNew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{				
				EnterWidget enterwidget = new EnterWidget();				
				enterwidget.setVisible(true);				
			}
		});
		
		JButton buttonClearWidgetAvailable = new JButton("Clear");
		buttonClearWidgetAvailable.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				clearWidgetAvailTable();
			}
		});
		
		JButton buttonRefreshWidgetAvail = new JButton("RefreshAvailW");
		buttonRefreshWidgetAvail.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				clearWidgetAvailTable();
				fillWidgetAvailTable();
			}
		});
		GroupLayout gl_panelWidgetAvailable = new GroupLayout(panelWidgetAvailable);
		gl_panelWidgetAvailable.setHorizontalGroup(
			gl_panelWidgetAvailable.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panelWidgetAvailable.createSequentialGroup()
					.addGap(5)
					.addGroup(gl_panelWidgetAvailable.createParallelGroup(Alignment.TRAILING)
						.addComponent(scrollPaneWidgetAvailable, Alignment.LEADING)
						.addGroup(gl_panelWidgetAvailable.createSequentialGroup()
							.addComponent(buttonWidgetAvailNew)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(buttonClearWidgetAvailable, GroupLayout.PREFERRED_SIZE, 79, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED, 102, Short.MAX_VALUE)
							.addComponent(buttonRefreshWidgetAvail, GroupLayout.PREFERRED_SIZE, 139, GroupLayout.PREFERRED_SIZE)))
					.addGap(5))
		);
		gl_panelWidgetAvailable.setVerticalGroup(
			gl_panelWidgetAvailable.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panelWidgetAvailable.createSequentialGroup()
					.addComponent(scrollPaneWidgetAvailable, GroupLayout.DEFAULT_SIZE, 214, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panelWidgetAvailable.createParallelGroup(Alignment.BASELINE)
						.addComponent(buttonWidgetAvailNew)
						.addComponent(buttonClearWidgetAvailable)
						.addComponent(buttonRefreshWidgetAvail))
					.addGap(10))
		);
		panelWidgetAvailable.setLayout(gl_panelWidgetAvailable);
	}
	
//	@Override
//	public void valueChanged(ListSelectionEvent e) 
//	{
//		if (e.getValueIsAdjusting()) 
//		{
//			return;
//		}
//		
//		if( e.getSource().equals(tableUser))
//		{
//			tableUser.getSelectedRow();
//			
//		}
//		else if( e.getSource().equals(tableService))
//		{
//			tableService.getSelectedRow();		
//		}
//		else if( e.getSource().equals(tableServiceStatus))
//		{
//		
//		}
//	}
	
	//////
	public User getSelectedUser()
	{
		User usr = null;
		int srow = tableUser.getSelectedRow();
		
		if(srow!=-1)
		{
			usr = new User();
			usr.setId((String)tableUser.getModel().getValueAt(srow, 0));
			usr.setName((String)tableUser.getModel().getValueAt(srow, 1));
			usr.setDescription((String)tableUser.getModel().getValueAt(srow, 2));		
			usr.setEmail((String)tableUser.getModel().getValueAt(srow, 3));
		}
		
		return usr;
	}
	public void fillUserTable()
	{
		SesameSPARQLClient sparqlCl = new SesameSPARQLClient();
		TupleQueryResult qres = sparqlCl.sparqlToQResult(User.Queries.selectAllUsers());				
		List<User> usrList = User.Queries.parseUser(qres);
		
		for (int i = 0; i < usrList.size(); i++)
		{
			((DefaultTableModel)tableUser.getModel()).
			insertRow(	((DefaultTableModel)tableUser.getModel()).getRowCount(), new Object[] {
						usrList.get(i).getId(),
						usrList.get(i).getName(),
						usrList.get(i).getEmail(),
						usrList.get(i).getDescription()
						});
		}
	}
	public void clearUserTable()
	{
		DefaultTableModel tb = (DefaultTableModel)tableUser.getModel();				
		while (tb.getRowCount() > 0){
			tb.removeRow(0);
		}
	}
	//////
	public Service getSelectedService()
	{
		Service srvc = null;
		int srow = tableService.getSelectedRow();
		
		if(srow!=-1)
		{
			srvc = new Service();
			srvc.setId((String)tableService.getModel().getValueAt(srow, 0));
			srvc.setName((String)tableService.getModel().getValueAt(srow, 1));
			srvc.setDescription((String)tableService.getModel().getValueAt(srow, 2));		
			srvc.setQueryString((String)tableService.getModel().getValueAt(srow, 3));
			User usr = new User();
			usr.setId(((String)tableService.getModel().getValueAt(srow, 4)));
			srvc.setUser(usr);
		}
		
		return srvc;
	}
	public void clearServiceTable()
	{
		DefaultTableModel tb = (DefaultTableModel)tableService.getModel();				
		while (tb.getRowCount() > 0){
			tb.removeRow(0);
		}
	}
	public void fillServiceTable(User userID)
	{
		SesameSPARQLClient sparqlCl = new SesameSPARQLClient();
		TupleQueryResult qres = sparqlCl.sparqlToQResult(Service.Queries.selectSrvcByUser(userID));				
		List<Service> serviceList = Service.Queries.parseService(qres);
		
		for (int i = 0; i < serviceList.size(); i++)
		{
			((DefaultTableModel)tableService.getModel()).
			insertRow(	((DefaultTableModel)tableService.getModel()).getRowCount(), new Object[] {
					serviceList.get(i).getId(),
					serviceList.get(i).getName(),						
					serviceList.get(i).getDescription(),
					serviceList.get(i).getQueryString(),
					serviceList.get(i).getUser().getId()
						});
		}
	}
	//////	
	public void clearServiceStatusTable()
	{
		DefaultTableModel tb = (DefaultTableModel)tableServiceStatus.getModel();				
		while (tb.getRowCount() > 0){
			tb.removeRow(0);
		}
	}	
	public void fillServiceStatusTable(Service serviceID)
	{
		SesameSPARQLClient sparqlCl = new SesameSPARQLClient();
		TupleQueryResult qres = sparqlCl.sparqlToQResult(ServiceStatus.Queries.selectSrvcStatusByService(serviceID));				
		List<ServiceStatus> serviceStatusList = ServiceStatus.Queries.parseService(qres);
		
		for (int i = 0; i < serviceStatusList.size(); i++)
		{
			((DefaultTableModel)tableServiceStatus.getModel()).
			insertRow(	((DefaultTableModel)tableServiceStatus.getModel()).getRowCount(), new Object[] {
					serviceStatusList.get(i).getId(),
					serviceStatusList.get(i).getStatus().getCode(),						
					serviceStatusList.get(i).getTime(),
					serviceStatusList.get(i).getServiceOf().getId()					
						});
		}
	}
	//////
	public void clearWidgetAvailTable()
	{
		DefaultTableModel tb = (DefaultTableModel)tableWidgetAvailable.getModel();				
		while (tb.getRowCount() > 0)
		{
			tb.removeRow(0);
		}
	}
	public void fillWidgetAvailTable()
	{
		SesameSPARQLClient sparqlCl = new SesameSPARQLClient();
		TupleQueryResult qres = sparqlCl.sparqlToQResult(WidgetAvailable.Queries.selectWidgetAvailAll());				
		List<WidgetAvailable> widgetList = WidgetAvailable.Queries.parseService(qres);
		
		for (int i = 0; i < widgetList.size(); i++)
		{
			((DefaultTableModel)tableWidgetAvailable.getModel()).
			insertRow(	((DefaultTableModel)tableWidgetAvailable.getModel()).getRowCount(), new Object[] {
					widgetList.get(i).getId(),
					widgetList.get(i).getName(),						
					widgetList.get(i).getDescription(),
					widgetList.get(i).getLocationURL(),
					widgetList.get(i).getType()
						});
		}
	}
	public List<WidgetAvailable> getAllWidgetAvail()
	{		
		List<WidgetAvailable> widgeAvailtList = null;
		int rc = ((DefaultTableModel)tableWidgetAvailable.getModel()).getRowCount();
		
		if(rc>0)
		{
			widgeAvailtList = new ArrayList<WidgetAvailable>();
			for (int i = 0; i < rc; i++)
			{
				WidgetAvailable wAvail = new WidgetAvailable();
				
				wAvail.setId((String)tableWidgetAvailable.getModel().getValueAt(i, 0));
				wAvail.setName((String)tableWidgetAvailable.getModel().getValueAt(i, 1));
				wAvail.setDescription((String)tableWidgetAvailable.getModel().getValueAt(i, 2));
				wAvail.setLocationURL((String)tableWidgetAvailable.getModel().getValueAt(i, 3));
				wAvail.setType((String)tableWidgetAvailable.getModel().getValueAt(i, 4));
				
				widgeAvailtList.add(wAvail);
			}
		}		
		
		return widgeAvailtList;
	}
	//////
	public void clearWidgetPreTable()
	{
		DefaultTableModel tb = (DefaultTableModel)tableWidgetPre.getModel();				
		while (tb.getRowCount() > 0){
			tb.removeRow(0);
		}
	}
	public WidgetPresentation getSelectedWidgetPre()
	{
		WidgetPresentation wpre = null;
		int srow = tableWidgetPre.getSelectedRow();
		
		if(srow!=-1)
		{
			wpre = new WidgetPresentation();
			
			wpre.setId((String)tableWidgetPre.getModel().getValueAt(srow, 0));
			
			WidgetAvailable wid = new WidgetAvailable();
			wid.setId(((String)tableWidgetPre.getModel().getValueAt(srow, 1)));
			wpre.setWidgetAvailable(wid);
			
			Service srvc = new Service();
			srvc.setId((String)tableWidgetPre.getModel().getValueAt(srow, 2));
			wpre.setService(srvc);
			
			WidgetAttributes wAttr = new WidgetAttributes();
			wAttr.setId((String)tableWidgetPre.getModel().getValueAt(srow, 3));
			wpre.addWidgetAttr(wAttr);
			
			//wpre.setDescription((String)tableWidgetPre.getModel().getValueAt(srow, 4));
		}
		
		return wpre;
	}
	public void fillWidgetPreTable()
	{
		SesameSPARQLClient sparqlCl = new SesameSPARQLClient();
		TupleQueryResult qres = sparqlCl.sparqlToQResult(WidgetPresentation.Queries.selectWidgetPreAll());				
		List<WidgetPresentation> widgetPreList = WidgetPresentation.Queries.parseService(qres);
		
		for (int i = 0; i < widgetPreList.size(); i++)
		{
			((DefaultTableModel)tableWidgetPre.getModel()).
			insertRow(	((DefaultTableModel)tableWidgetPre.getModel()).getRowCount(), new Object[] 
					{
					widgetPreList.get(i).getId(),
					widgetPreList.get(i).getWidgetAvailable().getId(),				
					widgetPreList.get(i).getService().getId(),					
					widgetPreList.get(i).getWidgetAttrList().get(0).getId(),
					//widgetPreList.get(i).getDescription(),
					});
		}
	}
	public void fillWidgetPreTable(Service srvc)
	{
		SesameSPARQLClient sparqlCl = new SesameSPARQLClient();
		TupleQueryResult qres = sparqlCl.sparqlToQResult(WidgetPresentation.Queries.selectWidgetPreByService(srvc));				
		List<WidgetPresentation> widgetPreList = WidgetPresentation.Queries.parseService(qres);
		
		for (int i = 0; i < widgetPreList.size(); i++)
		{
			((DefaultTableModel)tableWidgetPre.getModel()).
			insertRow(	((DefaultTableModel)tableWidgetPre.getModel()).getRowCount(), new Object[] 
					{
					widgetPreList.get(i).getId(),
					widgetPreList.get(i).getWidgetAvailable().getId(),				
					widgetPreList.get(i).getService().getId(),					
					widgetPreList.get(i).getWidgetAttrList().get(0).getId(),
					//widgetPreList.get(i).getDescription(),
					});
		}
	}
	//////
	public void clearWidgetAttrTable()
	{
		DefaultTableModel tb = (DefaultTableModel)tableWidgetAttr.getModel();				
		while (tb.getRowCount() > 0){
			tb.removeRow(0);
		}
	}
	public void fillWidgetAttrTable()
	{
		SesameSPARQLClient sparqlCl = new SesameSPARQLClient();
		TupleQueryResult qres = sparqlCl.sparqlToQResult(WidgetAttributes.Queries.selectWidgetAttrAll());				
		List<WidgetAttributes> widgetAttrList = WidgetAttributes.Queries.parseService(qres);
		
		for (int i = 0; i < widgetAttrList.size(); i++)
		{
			((DefaultTableModel)tableWidgetAttr.getModel()).
			insertRow(	((DefaultTableModel)tableWidgetAttr.getModel()).getRowCount(), new Object[] 
					{
					widgetAttrList.get(i).getId(),
					widgetAttrList.get(i).getName(),						
					widgetAttrList.get(i).getDescription(),
					widgetAttrList.get(i).getWidgetPres().getId()
					});
		}
	}
	public void fillWidgetAttrTable(WidgetPresentation wpre)
	{
		SesameSPARQLClient sparqlCl = new SesameSPARQLClient();
		TupleQueryResult qres = sparqlCl.sparqlToQResult(WidgetAttributes.Queries.selectWidgetAttrByWidgetPre(wpre));
		List<WidgetAttributes> widgetAttrList = WidgetAttributes.Queries.parseService(qres);
		
		for (int i = 0; i < widgetAttrList.size(); i++)
		{
			((DefaultTableModel)tableWidgetAttr.getModel()).
			insertRow(	((DefaultTableModel)tableWidgetAttr.getModel()).getRowCount(), new Object[] 
					{
					widgetAttrList.get(i).getId(),
					widgetAttrList.get(i).getName(),						
					widgetAttrList.get(i).getDescription(),
					widgetAttrList.get(i).getWidgetPres().getId()
					});
		}
	}
	
	public static MainForm getMainForm() 
	{
		if(mainform!=null)			
			return mainform;
		else
		{
			MainForm.mainform = new MainForm();
			return mainform;
		}
	}
}//class
