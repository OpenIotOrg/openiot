package org.openiot.sdum.client.ui.swing;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.JButton;

import org.openiot.sdum.client.rest.ServiceDeliveryUtilityManagerClient;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class ServiceDeliveryUtilityManagerUI {

	private JFrame frmSdumClient;
	
	private static ServiceDeliveryUtilityManagerClient serviceDeliveryUtilityManagerClient;
	

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		
		serviceDeliveryUtilityManagerClient = new ServiceDeliveryUtilityManagerClient("http://localhost:8080/sdum.core");
		
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					
					// Set System L&F
					UIManager.setLookAndFeel(
				            UIManager.getSystemLookAndFeelClassName());
					
					ServiceDeliveryUtilityManagerUI window = new ServiceDeliveryUtilityManagerUI();
					window.frmSdumClient.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public ServiceDeliveryUtilityManagerUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmSdumClient = new JFrame();
		frmSdumClient.setTitle("SD&UM Client");
		frmSdumClient.setBounds(100, 100, 450, 300);
		frmSdumClient.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmSdumClient.getContentPane().setLayout(null);
		
		JButton btnWelcome = new JButton("Welcome");
		btnWelcome.addActionListener(new BtnWelcomeActionListener());
		btnWelcome.setBounds(22, 29, 119, 23);
		frmSdumClient.getContentPane().add(btnWelcome);
		
		JButton btnPollForReport = new JButton("Poll For Report");
		btnPollForReport.addActionListener(new BtnPollForReportActionListener());
		btnPollForReport.setBounds(22, 86, 119, 23);
		frmSdumClient.getContentPane().add(btnPollForReport);
	}
	private class BtnWelcomeActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			
			serviceDeliveryUtilityManagerClient.welcomeMessage();
			
			
		}
	}
	private class BtnPollForReportActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			
			
			serviceDeliveryUtilityManagerClient.pollForReport();
			
		}
	}
}
