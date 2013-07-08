package org.openiot.scheduler.client.ui.swing;

import java.awt.EventQueue;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.UIManager;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;

import org.openiot.scheduler.client.rest.SchedulerClient;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.border.TitledBorder;
import javax.swing.JLabel;
import javax.swing.LayoutStyle.ComponentPlacement;

public class SchedulerClientGUI extends JPanel
{
	private static final long serialVersionUID = 1L;
	
	private static SchedulerClient schedulerClient;

	private JFrame frmSchedulerClient;	
	private JTextField osdSpecpathTextField;
	private JTextField textFieldLong;
	private JTextField textFieldLat;
	private JTextField textFieldRad;

	
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
		frmSchedulerClient.setBounds(100, 100, 464, 327);
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
		textFieldRad.setText("5");
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
		panel_2.setBounds(10, 132, 430, 142);
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
			gl_panel_2.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panel_2.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_2.createParallelGroup(Alignment.TRAILING)
						.addComponent(btnRegisterService, GroupLayout.PREFERRED_SIZE, 147, GroupLayout.PREFERRED_SIZE)
						.addComponent(osdSpecpathTextField, GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
						.addComponent(btnOpenOsdspec, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE))
					.addGap(19)
					.addComponent(btnRegisterOsdspec))
		);
		gl_panel_2.setVerticalGroup(
			gl_panel_2.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_2.createSequentialGroup()
					.addGroup(gl_panel_2.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel_2.createSequentialGroup()
							.addComponent(btnOpenOsdspec, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addComponent(osdSpecpathTextField, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE)
							.addGap(54))
						.addComponent(btnRegisterOsdspec, GroupLayout.PREFERRED_SIZE, 48, GroupLayout.PREFERRED_SIZE))
					.addGap(23))
				.addGroup(Alignment.TRAILING, gl_panel_2.createSequentialGroup()
					.addContainerGap(91, Short.MAX_VALUE)
					.addComponent(btnRegisterService, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
					.addContainerGap())
		);
		panel_2.setLayout(gl_panel_2);
		btnDiscoverSensors.addActionListener(new BtnDiscoverSensorsActionListener());
		btnWelcome.addActionListener(new BtnWelcomeActionListener());
	}
	private class BtnWelcomeActionListener implements ActionListener 
	{
		public void actionPerformed(ActionEvent e) 
		{			
			schedulerClient.welcomeMessage();
		}
	}
	private class BtnDiscoverSensorsActionListener implements ActionListener 
	{
		public void actionPerformed(ActionEvent e) 
		{			
			schedulerClient.discoverSensors(Double.valueOf(textFieldLong.getText()),
					Double.valueOf(textFieldLat.getText()),
					Float.valueOf(textFieldRad.getText()));			
		}
	}
	private class BtnRegisterServiceActionListener implements ActionListener 
	{
		public void actionPerformed(ActionEvent e) 
		{			
			schedulerClient.registerService();			
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
		        if (returnVal == JFileChooser.APPROVE_OPTION) {
		            File file = fc.getSelectedFile();
//		            System.out.println("Opening: " + file.getName() + "." + "\n");
//		            
		            osdSpecpathTextField.setText(file.getAbsolutePath());
		            
		        } else {
		        	System.out.println("Open command cancelled by user." + "\n");
		        }			
		}
	}
	private class BtnRegisterOsdspecActionListener implements ActionListener 
	{
		public void actionPerformed(ActionEvent e) 
		{			
			schedulerClient.registerFromFile(osdSpecpathTextField.getText());			
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
