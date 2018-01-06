package dbms;

import java.io.File;
import java.sql.SQLException;
import java.util.List;

import databaseInterfaces.Database;

public class DataBaseImpl implements Database {
	public String currentDataBase = null;
	private String tableNameForSelect;
	private List<String> columnsNames;
	public String getTableNameForSelect() {
		return tableNameForSelect;
	}

	public List<String> getColumnsNames() {
		return columnsNames;
	}


	@Override
	public String createDatabase(String databaseName, boolean dropIfExists) {

		databaseName = databaseName.toLowerCase();
		this.currentDataBase = System.getProperty("user.dir") + System.getProperty("file.separator") + "databases"
				+ System.getProperty("file.separator") + databaseName;
		final File f = new File(this.currentDataBase);
		if (f.exists()) {
			if (dropIfExists) {
				try {
					executeStructureQuery("DROP DATABASE " + databaseName);
				} catch (final Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					executeStructureQuery("CREATE DATABASE " + databaseName);
				} catch (final Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} else {
			try {
				executeStructureQuery("CREATE DATABASE " + databaseName);
			} catch (final Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		this.currentDataBase = f.getAbsolutePath();
		// throw new RuntimeException( "Creat db: " + databaseName);

		return currentDataBase;
	}

	@Override
	public boolean executeStructureQuery(String query) throws SQLException {
		query = query.toLowerCase();
		//		if (!query.contains("create") && !query.contains("drop")) {
		//			throw new java.sql.SQLException();
		//		}
		// if( query.contains("table") && !query.contains(",") ){
		// return false;
		// }
		if (currentDataBase == null && !query.contains("database") ) {
			throw new java.sql.SQLException();
		}

		final ChooseStatement statment = new ChooseStatement(query, currentDataBase, null);
		try {
			statment.createStatement();
			System.out.println(statment.returnObject);
			this.currentDataBase = statment.dataBase;
			return (boolean) statment.returnObject;
		} catch (final Exception e) {
			throw new java.sql.SQLException();
			// try {
			// //throw SQLException;
			// } catch (final Exception e1) {
			// // TODO Auto-generated catch block
			// e1.printStackTrace();
			// }
			// return false;
		}
	}

	public void setCurrentDataBase(String currentDataBase) {
		this.currentDataBase = currentDataBase;
	}

	@Override
	public Object[][] executeQuery(String query) throws SQLException {
		query = query.toLowerCase();

		final ChooseStatement statment = new ChooseStatement(query, currentDataBase, null);
		try {
			statment.createStatement();
		} catch (final Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// throw new RuntimeException( "ex qu: " + query);
		if (query.contains("*")){
			this.columnsNames = statment.columnsNamesOfEntireTable;
		}
		else{
			this.columnsNames = statment.columnsNamesForSelect;
		}
		this.tableNameForSelect = statment.tableNameForSelect;
		return (Object[][]) statment.returnObject;
	}

	@Override
	public int executeUpdateQuery(String query) throws SQLException {
		query = query.toLowerCase();
		//		if (!query.contains("insert") && !query.contains("delete") && !query.contains("update")) {
		//			throw new java.sql.SQLException();
		//		}
		final ChooseStatement statment = new ChooseStatement(query, currentDataBase, null);
		try {
			statment.createStatement();
		} catch (final Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int temp = 0;

		try {
			//throw new RuntimeException(query);
			temp = (int) statment.returnObject;
		} catch (final Exception e) {
			// temp = 1;
			// return temp;
			throw new java.sql.SQLException();


		}

		return temp;
	}

}
