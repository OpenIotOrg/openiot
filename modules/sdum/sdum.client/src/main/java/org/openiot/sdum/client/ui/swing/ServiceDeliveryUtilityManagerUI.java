package org.openiot.sdum.client.ui.swing;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.JButton;

import org.openiot.sdum.client.rest.ServiceDeliveryUtilityManagerClient;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JTextField;
import javax.swing.JPanel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.border.TitledBorder;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JLabel;


/**
 * @author Stavros Petris (spet) e-mail: spet@ait.edu.gr
 * @author Nikos Kefalakis (nkef) e-mail: nkef@ait.edu.gr 
 */
public class ServiceDeliveryUtilityManagerUI {

	private JFrame frmSdumClient;
	
	private static ServiceDeliveryUtilityManagerClient serviceDeliveryUtilityManagerClient;
	private JTextField textFieldServiceID;
	

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
		frmSdumClient.setBounds(100, 100, 450, 152);
		frmSdumClient.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmSdumClient.getContentPane().setLayout(null);
		
		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "ping sdum service", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel.setBounds(12, 13, 149, 82);
		frmSdumClient.getContentPane().add(panel);
		
		JButton btnWelcome = new JButton("Welcome");
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addComponent(btnWelcome, GroupLayout.DEFAULT_SIZE, 113, Short.MAX_VALUE)
					.addContainerGap())
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, gl_panel.createSequentialGroup()
					.addContainerGap()
					.addComponent(btnWelcome, GroupLayout.DEFAULT_SIZE, 31, Short.MAX_VALUE)
					.addContainerGap())
		);
		panel.setLayout(gl_panel);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new TitledBorder(null, "poll for report", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_1.setBounds(165, 13, 255, 82);
		frmSdumClient.getContentPane().add(panel_1);
		
		textFieldServiceID = new JTextField();
		textFieldServiceID.setColumns(10);
		
		JButton btnPollForReport = new JButton("poll");
		btnPollForReport.addActionListener(new BtnPollForReportActionListener());
		
		JLabel lblServiceID = new JLabel("service id");
		GroupLayout gl_panel_1 = new GroupLayout(panel_1);
		gl_panel_1.setHorizontalGroup(
			gl_panel_1.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_1.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblServiceID)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
						.addComponent(btnPollForReport, GroupLayout.PREFERRED_SIZE, 119, GroupLayout.PREFERRED_SIZE)
						.addComponent(textFieldServiceID, GroupLayout.DEFAULT_SIZE, 168, Short.MAX_VALUE))
					.addContainerGap())
		);
		gl_panel_1.setVerticalGroup(
			gl_panel_1.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, gl_panel_1.createSequentialGroup()
					.addComponent(btnPollForReport, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED, 20, Short.MAX_VALUE)
					.addGroup(gl_panel_1.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblServiceID)
						.addComponent(textFieldServiceID, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(42))
		);
		panel_1.setLayout(gl_panel_1);
		btnWelcome.addActionListener(new BtnWelcomeActionListener());
	}
	private class BtnWelcomeActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			
			serviceDeliveryUtilityManagerClient.welcomeMessage();
			
			
		}
	}
	private class BtnPollForReportActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			
			
			serviceDeliveryUtilityManagerClient.pollForReport(textFieldServiceID.getText());
			
		}
	}
}
