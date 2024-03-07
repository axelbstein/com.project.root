package com.project.plugin;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.swing.JOptionPane;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.equinox.p2.engine.spi.ProvisioningAction;

public class MyCustomAction extends ProvisioningAction{

	@Override
	public IStatus execute(Map<String, Object> parameters) {
		String fileName = "initial.txt"; // Change this to "changed.txt" for part 2 of the test
		try {
			File myFile = new File(System.getProperty("user.home") + System.getProperty("file.separator") + fileName);
			JOptionPane.showMessageDialog(null, "File: " + myFile.getName(), myFile.getName(), JOptionPane.PLAIN_MESSAGE);
			if (myFile.createNewFile()) {
				System.out.println("File created: " + myFile.getName());
			} else {
				System.out.println("File already exists.");
			}
		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
		return Status.OK_STATUS;
	}

	@Override
	public IStatus undo(Map<String, Object> parameters) {
		return Status.OK_STATUS;
	}
}