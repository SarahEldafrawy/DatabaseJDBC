package jdbc;

import java.util.ArrayList;

public class ResultSetPool {
  ArrayList<ResultSetAdapter> available = new ArrayList<ResultSetAdapter>();
  ArrayList<ResultSetAdapter> inUse = new ArrayList<ResultSetAdapter>();
  
  public ResultSetAdapter getPool(Object[][] r, StatementAdapter state, ArrayList<String> columnNames, String tableName) {
    if(available.isEmpty()) {
      return  new ResultSetAdapter(r, new ResultSetPool(), state, columnNames, tableName);
    }
      else
      {
        ResultSetAdapter resultSet = available.remove(available.size()-1);
        inUse.add(resultSet);
        resultSet.setObject(r);
        return resultSet;
      }
    }
  
  public void retrieve (ResultSetAdapter resultSet) {
    resultSet.setWithNull(null);
    available.add(resultSet);
    inUse.remove(resultSet);
  }
  
  }

