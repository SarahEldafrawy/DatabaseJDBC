package jdbc;

import java.sql.Connection;
import java.util.ArrayList;

public class StatementPool {
  ArrayList<StatementAdapter> available = new ArrayList<StatementAdapter>();
  ArrayList<StatementAdapter> inUse = new ArrayList<StatementAdapter>();
  
  public StatementAdapter getStatement(String path, Connection connect) {
    if(available.isEmpty()) {
      return  new StatementAdapter(path, new StatementPool(), connect);
    }
      else
      {
        StatementAdapter statement = available.remove(available.size()-1);
        inUse.add(statement);
        statement.set(path, connect);
        return statement;
      }
    }
  
  public void retrieve (StatementAdapter statement) {
    statement.setWithNull();
    available.add(statement);
    inUse.remove(statement);
  }
  
  }