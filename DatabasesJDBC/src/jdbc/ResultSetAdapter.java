package jdbc;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * 
 * check this one https://docs.oracle.com/javase/8/docs/api/java/sql/ResultSet.html
 *
 */

public class ResultSetAdapter extends Result {
  /**
  private String path ;
   */
  private ResultSetPool resultSetPool;
  public Object[][] result;
  private int pointer;
  private Statement statement;
  private boolean isClosed;
  private final String tableName ;
  public static ArrayList<String> columnsName ;

  public ResultSetAdapter(Object[][] r, ResultSetPool resultSetPool, Statement state , ArrayList<String> columns , String table) {
    this.result = r;
    this.isClosed = false;
    this.pointer = 0;
    this.resultSetPool = resultSetPool;
    this.statement = state;
    this.columnsName = columns ;
    this.tableName = table ;
  }

  @Override
  public boolean absolute(int arg0) throws SQLException {
    checkIfClosed();
    if (arg0 > result.length - 1 || arg0 < 0) {
      return false;
    } else {

      pointer = arg0;
      return true;
    }
  }

  @Override
  public void afterLast() throws SQLException {
    checkIfClosed();
    pointer = result.length;
  }

  @Override
  public void beforeFirst() throws SQLException {
    checkIfClosed();
    pointer = -1;

  }

  @Override
  public void close() throws SQLException {
    isClosed = true;
    resultSetPool.retrieve(this);
  }
  @Override
  public int findColumn(String arg0) throws SQLException {
    checkIfClosed();
    for (int i = 0; i < columnsName.size(); i++) {
      if (columnsName.get(i).equalsIgnoreCase(arg0)) {
        return i;
      }

    }
    throw new SQLException();
  }

  @Override
  public boolean first() throws SQLException {
    checkIfClosed();
    if (result.length > 0) {
      pointer = 0;
      return true;
    } else {
      return false;
    }
  }

  @Override
  public int getInt(int arg0) throws SQLException {
    checkIfClosed();
    return (int) result[pointer][arg0];
  }

  @Override
  public int getInt(String arg0) throws SQLException {
    checkIfClosed();
    int col = findColumn(arg0);
    return (int) (result[pointer][col]);
  }

  /**
   * Retrieves the number, types and properties of this ResultSet object's columns.
   * 
   * Returns: the description of this ResultSet object's columns Throws: SQLException - if a
   * database access error occurs or this method is called on a closed result set
   */
  @Override
  public ResultSetMetaData getMetaData() throws SQLException {
    checkIfClosed();
    return new ResultSetMetaDataAdapter(this);
    }

  @Override
  public Object getObject(int arg0) throws SQLException {
    checkIfClosed();
    return result[pointer][arg0];
  }

  @Override
  public Statement getStatement() throws SQLException {
    checkIfClosed();
    return this.statement;
  }

  @Override
  public String getString(int arg0) throws SQLException {
    checkIfClosed();
    return result[pointer][arg0].toString();
  }

  @Override
  public String getString(String arg0) throws SQLException {
    checkIfClosed();
    int col = findColumn(arg0);

    return result[pointer][col].toString();
  }

  @Override
  public boolean isAfterLast() throws SQLException {
    checkIfClosed();
    return pointer == result.length;
  }

  @Override
  public boolean isBeforeFirst() throws SQLException {
    checkIfClosed();
    return pointer == -1;
  }

  @Override
  public boolean isClosed() throws SQLException {
    return isClosed;
  }

  @Override
  public boolean isFirst() throws SQLException {
    checkIfClosed();
    return pointer == 0;
  }

  @Override
  public boolean isLast() throws SQLException {
    checkIfClosed();
    return pointer == result.length - 1;
  }

  @Override
  public boolean last() throws SQLException {
    checkIfClosed();
    if(result.length > 0){
      pointer = result.length - 1 ;
      return true ;
    }else {
      return false ;
    }
  }

  @Override
  public boolean next() throws SQLException {
    checkIfClosed();
    if(pointer + 1 < result.length -1 ){
      pointer++;
      return true ;
    }else {
      return false ;
    }
  }

  @Override
  public boolean previous() throws SQLException {
    checkIfClosed();
    if(pointer - 1 > -1){
      pointer--;
      return true ;
    }else {
      return true ;
    }
    
  }

  private void checkIfClosed() throws SQLException {
    if (isClosed) {
      throw new SQLException();
    }
  }
  
  public void setWithNull(Object object) {
    this.pointer = -1;
    this.result = null;
  }

  public void setObject(Object[][] r) {
    this.result = r;

  }

  public String getTableName() {

    return tableName;
  }
  
  public ArrayList<String> getColumnsName() {
    return columnsName;
  }
  
}
