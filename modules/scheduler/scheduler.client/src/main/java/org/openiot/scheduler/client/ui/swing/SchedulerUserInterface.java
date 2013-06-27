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

public class SchedulerUserInterface extends JPanel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JFrame frmSchedulerClient;
	
	private static SchedulerClient schedulerClient;
	private JTextField osdSpecpathTextField;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		
		schedulerClient = new SchedulerClient("http://localhost:8080/scheduler.core");
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					// Set System L&F
					UIManager.setLookAndFeel(
				            UIManager.getSystemLookAndFeelClassName());
					
					SchedulerUserInterface window = new SchedulerUserInterface();
					window.frmSchedulerClient.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public SchedulerUserInterface() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmSchedulerClient = new JFrame();
		frmSchedulerClient.setTitle("Scheduler Client");
		frmSchedulerClient.setBounds(100, 100, 450, 300);
		frmSchedulerClient.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmSchedulerClient.getContentPane().setLayout(null);
		
		JButton btnWelcome = new JButton("Welcome");
		btnWelcome.addActionListener(new BtnWelcomeActionListener());
		btnWelcome.setBounds(10, 11, 136, 23);
		frmSchedulerClient.getContentPane().add(btnWelcome);
		
		JButton btnDiscoverSensors = new JButton("Discover Sensors");
		btnDiscoverSensors.addActionListener(new BtnDiscoverSensorsActionListener());
		btnDiscoverSensors.setBounds(10, 45, 136, 23);
		frmSchedulerClient.getContentPane().add(btnDiscoverSensors);
		
		JButton btnRegisterService = new JButton("Register Service");
		btnRegisterService.addActionListener(new BtnRegisterServiceActionListener());
		btnRegisterService.setBounds(10, 79, 136, 23);
		frmSchedulerClient.getContentPane().add(btnRegisterService);
		
		JButton btnOpenOsdspec = new JButton("Open OSDSpec");
		btnOpenOsdspec.addActionListener(new BtnOpenOsdspecActionListener());
		btnOpenOsdspec.setBounds(10, 195, 124, 23);
		frmSchedulerClient.getContentPane().add(btnOpenOsdspec);
		
		osdSpecpathTextField = new JTextField();
		osdSpecpathTextField.setBounds(152, 196, 272, 20);
		frmSchedulerClient.getContentPane().add(osdSpecpathTextField);
		osdSpecpathTextField.setColumns(10);
		
		JButton btnRegisterOsdspec = new JButton("Register OSDSpec");
		btnRegisterOsdspec.addActionListener(new BtnRegisterOsdspecActionListener());
		btnRegisterOsdspec.setBounds(152, 227, 272, 23);
		frmSchedulerClient.getContentPane().add(btnRegisterOsdspec);
	}
	private class BtnWelcomeActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			
			schedulerClient.welcomeMessage();	
			
		}
	}
	private class BtnDiscoverSensorsActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			
			schedulerClient.discoverSensors();
			
		}
	}
	private class BtnRegisterServiceActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			
			schedulerClient.registerService();
			
		}
	}
	private class BtnOpenOsdspecActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			
			JFileChooser fc = new JFileChooser();


			// FileFilter filter = null;
			// File f = null;
			// f.
			// filter.accept(f);
			// fc.setFileFilter(filter);
			
			 int returnVal = fc.showOpenDialog(SchedulerUserInterface.this);
		        if (returnVal == JFileChooser.APPROVE_OPTION) {
		            File file = fc.getSelectedFile();
		            //This is where a real application would open the file.
		            System.out.println("Opening: " + file.getName() + "." + "\n");
		            
		            osdSpecpathTextField.setText(file.getAbsolutePath());
		            
		        } else {
		        	System.out.println("Open command cancelled by user." + "\n");
		        }

			
		}
	}
	private class BtnRegisterOsdspecActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			
			schedulerClient.registerFromFile(osdSpecpathTextField.getText());
			
		}
	}
}
