package jdbc;

import java.util.ArrayList;

public class ConnectionPool {
  ArrayList<ConnectAdapter> available = new ArrayList<ConnectAdapter>();
  ArrayList<ConnectAdapter> inUse = new ArrayList<ConnectAdapter>();
  
  public ConnectAdapter getConnection(String path) {
    if(available.isEmpty()) {
      return  new ConnectAdapter(path, new ConnectionPool());
    }
      else {
        ConnectAdapter connection = available.remove(available.size()-1);
        inUse.add(connection);
        connection.setPath(path);
        return connection;
      }
    }
  
  public void retrieve (ConnectAdapter connection) {
    connection.setPath(null);
    available.add(connection);
    inUse.remove(connection);
  }
  
  }

