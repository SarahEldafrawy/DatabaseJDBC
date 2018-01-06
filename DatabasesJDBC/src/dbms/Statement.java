package dbms;

import java.util.ArrayList;
import java.util.List;

public class Statement {

	String[] querySplited;
	String currentDataBase;
	Object returnObject;
	String tableNameForSelect;
	List columnsNamesForSelect;
	List columnsNamesOfEntireTable;


	public Statement(String[] querySplited, String currenrDataBase, Object returnObject) {
		this.querySplited = querySplited;
		this.currentDataBase = currenrDataBase;
		this.returnObject = returnObject;
		columnsNamesForSelect = new ArrayList<>();
		columnsNamesOfEntireTable = new ArrayList<>();
	}

	public Object excute() throws Exception {

		return null ;
	}
}
