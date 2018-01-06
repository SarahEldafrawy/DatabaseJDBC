package jdbcView;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.sql.ResultSet;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTable;

import jdbc.ResultSetAdapter;

public class ResultSetView {
    ResultSetAdapter result;
    private JFrame frame;
    private JTable table;

    /**
     * Launch the application.
     * 
     * @param result
     */
    public static void main(String[] args, ResultSet result) {
	EventQueue.invokeLater(new Runnable() {
	    @Override
	    public void run() {
		try {
		    ResultSetView window = new ResultSetView(result);
		    window.frame.setVisible(true);
		} catch (Exception e) {
		    e.printStackTrace();
		}
	    }
	});
    }

    /**
     * Create the application.
     */
    public ResultSetView(ResultSet result) {
	this.result = (ResultSetAdapter) result;
	initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
	int size = 0;
	if (result.result != null) {
	    size = result.result.length;
	}
	frame = new JFrame();
	frame.setBounds(500, 200, 109 * result.columnsName.size(),
		57 * size + 65);
	frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	frame.getContentPane().setLayout(null);

	table = new JTable(result.result, result.columnsName.toArray());
	table.setBounds(0, 50, 100 * result.columnsName.size(), 57 * size);
	table.setFont(new Font("Tahoma", Font.PLAIN, 17));
	table.setRowHeight(50);
	frame.getContentPane().add(table);
	if (result.result != null) {
	    for (int i = 0; i < result.columnsName.size(); i++) {
		JLabel label = new JLabel(result.columnsName.get(i));
		label.setBounds((0 + i * 100), 0, 100, 50);
		label.setFont(new Font("Tahoma", Font.PLAIN, 15));
		label.setForeground(Color.BLUE);
		frame.getContentPane().add(label);
	    }
	}
    }
}
