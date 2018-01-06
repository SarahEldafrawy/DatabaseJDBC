package mySQLvalidators;

import java.awt.Color;
import java.sql.SQLException;

import javax.swing.text.BadLocationException;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import databaseView.DatabaseConsole;


public class Controller2 {
	static boolean validIcon = false;

	public static boolean checkValidation(String statement) throws SQLException {
		Validators validate = new Validators();
		validIcon = new ValidatorsLevelOne().LevelOneValidation(statement);
		if(validIcon == true) {
		    Director d = new Director(DatabaseSingleton.db());
		    //d.functionChooser(statement.toUpperCase());
		}
		return validIcon;
	}

	public static void prepareLabelsAndIcons() {
		String newLine = DatabaseConsole.txtFieldStatement.getText() + '\n';
		Controller.lastStatement = newLine;
		StyledDocument doc = DatabaseConsole.lblBatch.getStyledDocument();

		javax.swing.text.Style style = DatabaseConsole.lblBatch.addStyle(newLine, null);
		if (validIcon == true) {
			StyleConstants.setForeground(style, Color.GREEN);
		} else {
			StyleConstants.setForeground(style, Color.RED);
		}
		try {
			doc.insertString(doc.getLength(), newLine, style);
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		DatabaseConsole.txtFieldStatement.setText("");
	}

}
