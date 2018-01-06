package jdbc;
import java.sql.SQLException;
import java.sql.Statement;

public class ConnectAdapter extends Connect implements AutoCloseable {
  
  private StatementPool statePool;
  private ConnectionPool connectPool;
  private String path;
  private boolean isclosed;
  

  public ConnectAdapter(String path, ConnectionPool pool) {
    this.path = path;
    this.isclosed = false;
    this.connectPool = pool;
    this.statePool = new StatementPool();
  }

  /**
   * Creates a Statement object for sending SQL statements to the database. SQL statements without
   * parameters are normally executed using Statement objects. If the same SQL statement is executed
   * many times, it may be more efficient to use a PreparedStatement object.
   * 
   * Result sets created using the returned Statement object will by default be type
   * TYPE_FORWARD_ONLY and have a concurrency level of CONCUR_READ_ONLY. The holdability of the
   * created result sets can be determined by calling getHoldability().
   * 
   * Returns: a new default Statement object Throws: SQLException - if a database access error
   * occurs or this method is called on a closed connection
   */

  @Override
  public Statement createStatement() throws SQLException {
    if (isclosed) {
      throw new SQLException();
    }    
    return statePool.getStatement(path, this); 
  }

  @Override
  public void close() throws SQLException {
    connectPool.retrieve(this);
    isclosed = true;
  }

  @Override
  public boolean isClosed() throws SQLException {
    return isclosed;
  }

  public void setPath(String path) {
    this.path = path;
  }

}
