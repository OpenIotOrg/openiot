package org.openiot.scheduler.client.ui.swt;



import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import org.openiot.scheduler.client.rest.SchedulerClient;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class SchedulerUserInterface {

	protected Shell shell;
	private static SchedulerClient schedulerClient;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		
		schedulerClient = new SchedulerClient("http://localhost:8080/scheduler.core");
		try {
			SchedulerUserInterface window = new SchedulerUserInterface();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
		


		
	}



	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell = new Shell();
		shell.setSize(450, 300);
		shell.setText("SWT Application");
		
		Button btnWelcomeMessage = new Button(shell, SWT.NONE);
		btnWelcomeMessage.addSelectionListener(new BtnWelcomeMessageSelectionListener());
		btnWelcomeMessage.setBounds(25, 78, 143, 25);
		btnWelcomeMessage.setText("Welcome Message");
		
		Button btnDiscoverSensors = new Button(shell, SWT.NONE);
		btnDiscoverSensors.addSelectionListener(new BtnDiscoverSensorsSelectionListener());
		btnDiscoverSensors.setBounds(25, 118, 143, 25);
		btnDiscoverSensors.setText("Discover Sensors");
		
		Button btnRegisterService = new Button(shell, SWT.NONE);
		btnRegisterService.addSelectionListener(new BtnRegisterServiceSelectionListener());
		btnRegisterService.setBounds(25, 165, 143, 25);
		btnRegisterService.setText("Register Service");

	}
	private class BtnWelcomeMessageSelectionListener extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			
			
			schedulerClient.welcomeMessage();
			
		}
	}
	private class BtnDiscoverSensorsSelectionListener extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			
			
			schedulerClient.discoverSensors(6.631622,46.520131,5f);
			
			
		}
	}
	private class BtnRegisterServiceSelectionListener extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			
			
			schedulerClient.registerService();
			
			
		}
	}
}
