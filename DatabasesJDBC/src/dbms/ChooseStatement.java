package dbms;


import java.lang.reflect.Constructor;
import java.util.LinkedList;
import java.util.List;

public class ChooseStatement {
	protected String query;
	protected String dataBase;
	public LinkedList<Class<? extends Statement>> supportedStatements = new LinkedList<Class<? extends Statement>>();
	protected Object returnObject;
	String tableNameForSelect;
	List columnsNamesForSelect;
	List columnsNamesOfEntireTable;

	public void createStatement() throws Exception {
		this.query = this.query.replaceAll("\\s+", " ");
		query = query.replaceAll(";", "");
		//query = query.replaceAll("'", "");
		final String[] querySplited = query.trim().split(" ");
		Statement instance = null;
		// try contains instead of the loop
		for (int i = 0; i < supportedStatements.size(); i++) {
			if (querySplited[0].equalsIgnoreCase(supportedStatements.get(i).getSimpleName())) {
				try {
					final Constructor<? extends Statement> constructor = supportedStatements.get(i)
							.getConstructor(String[].class, String.class, Object.class);
					instance = constructor.newInstance(querySplited, dataBase, null);
					break;
				} catch (final Exception e) {
					e.printStackTrace();
				}
			}
		}
		instance.excute();
		dataBase = instance.currentDataBase;
		columnsNamesForSelect =instance.columnsNamesForSelect;
		columnsNamesOfEntireTable = instance.columnsNamesOfEntireTable;
		tableNameForSelect = instance.tableNameForSelect;
		this.returnObject = instance.returnObject;

	}

	public ChooseStatement(String query, String dataBase, Object returnObject) {
		super();
		this.query = query;
		this.dataBase = dataBase;
		this.returnObject = returnObject;
		supportedStatements.add(Create.class);
		supportedStatements.add(Drop.class);
		supportedStatements.add(Insert.class);
		supportedStatements.add(Select.class);
		supportedStatements.add(Delete.class);
		supportedStatements.add(Update.class);
	}

}
