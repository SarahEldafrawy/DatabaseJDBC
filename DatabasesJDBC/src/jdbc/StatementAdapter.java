package jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import dbms.DataBaseImpl;

/**
 * check this one
 * https://docs.oracle.com/javase/8/docs/api/java/sql/Statement.html
 */

public class StatementAdapter extends StatementAbstract {

    private Connection connection;
    public static int queryTimeOut;
    private final ResultSetPool resultPool;
    private StatementPool statePool;
    public static ArrayList<String> arr;
    private String path;
    private boolean isclosed;
    private DataBaseImpl dbms;
    private boolean threadKiller;
    public static ResultSet result;

    public StatementAdapter(String path, StatementPool pool,
	    Connection connect) {
	this.path = path;
	this.threadKiller = false;
	this.resultPool = new ResultSetPool();
	this.isclosed = false;
	this.arr = new ArrayList<>();
	this.statePool = pool;
	this.connection = connect;
	this.dbms = new DataBaseImpl();
	final String[] strings = path.split("\\\\");
	System.out.println(strings[strings.length - 1]);
	dbms.createDatabase(strings[strings.length - 1], false);
	this.queryTimeOut = 10;
    }

    /**
     * Adds the given SQL command to the current list of commands for this
     * Statement object. The commands in this list can be executed as a batch by
     * calling the method executeBatch.
     * 
     * Note:This method cannot be called on a PreparedStatement or
     * CallableStatement.
     * 
     * Parameters: sql - typically this is a SQL INSERT or UPDATE statement
     * Throws: SQLException - if a database access error occurs, this method is
     * called on a closed Statement, the driver does not support batch updates,
     * the method is called on a PreparedStatement or CallableStatement
     */
    @Override
    public void addBatch(String arg0) throws SQLException {
	arr.add(arg0);
    }

    /**
     * Empties this Statement object's current list of SQL commands.
     * 
     * Throws: SQLException - if a database access error occurs, this method is
     * called on a closed Statement or the driver does not support batch updates
     */
    @Override
    public void clearBatch() throws SQLException {
	arr.clear();
    }

    /**
     * Releases this Statement object's database and JDBC resources immediately
     * instead of waiting for this to happen when it is automatically closed. It
     * is generally good practice to release resources as soon as you are
     * finished with them to avoid tying up database resources.
     * 
     * Calling the method close on a Statement object that is already closed has
     * no effect.
     * 
     * Note:When a Statement object is closed, its current ResultSet object, if
     * one exists, is also closed.
     * 
     * Specified by: close in interface AutoCloseable Throws: SQLException - if
     * a database access error occurs
     */

    @Override
    public void close() throws SQLException {
	statePool.retrieve(this);
	this.isclosed = true;
    }

    /**
     * Executes the given SQL statement, which may return multiple results. In
     * some (uncommon) situations, a single SQL statement may return multiple
     * result sets and/or update counts. Normally you can ignore this unless you
     * are (1) executing a stored procedure that you know may return multiple
     * results or (2) you are dynamically executing an unknown SQL string.
     * 
     * The execute method executes an SQL statement and indicates the form of
     * the first result. You must then use the methods getResultSet or
     * getUpdateCount to retrieve the result, and getMoreResults to move to any
     * subsequent result(s).
     * 
     * Note:This method cannot be called on a PreparedStatement or
     * CallableStatement.
     * 
     * Parameters: sql - any SQL statement Returns: true if the first result is
     * a ResultSet object; false if it is an update count or there are no
     * results Throws: SQLException - if a database access error occurs, this
     * method is called on a closed Statement, the method is called on a
     * PreparedStatement or CallableStatement SQLTimeoutException - when the
     * driver has determined that the timeout value that was specified by the
     * setQueryTimeout method has been exceeded and has at least attempted to
     * cancel the currently running Statement
     */
    @Override
    public boolean execute(String query) throws SQLException {
	final String[] operation = query.trim().split("\\W+");
	if (operation[0].equalsIgnoreCase("Select")) {
	   /* this.threadKiller = false;
	    thread(queryTimeOut);*/
	    executeQuery(query);
	   /* this.threadKiller = true;
	    logger.info("Thread ended");*/
	    return true;
	} else if (operation[0].equalsIgnoreCase("Create")
		|| operation[0].equalsIgnoreCase("drop")) {
	    this.threadKiller = false;
	    thread(queryTimeOut);
	    dbms.executeStructureQuery(query);
	    this.threadKiller = true;
	    return false;
	} else if (operation[0].equalsIgnoreCase("update")
		|| operation[0].equalsIgnoreCase("insert")
		|| operation[0].equalsIgnoreCase("delete")) {
	    /*this.threadKiller = false;
	    thread(queryTimeOut);*/
	    executeUpdate(query);
	    /*this.threadKiller = true;
	    logger.info("Thread ended");*/
	    return false;
	}

	return false;

    }

    /**
     * Submits a batch of commands to the database for execution and if all
     * commands execute successfully, returns an array of update counts. The int
     * elements of the array that is returned are ordered to correspond to the
     * commands in the batch, which are ordered according to the order in which
     * they were added to the batch. The elements in the array returned by the
     * method executeBatch may be one of the following:
     * 
     * A number greater than or equal to zero -- indicates that the command was
     * processed successfully and is an update count giving the number of rows
     * in the database that were affected by the command's execution A value of
     * SUCCESS_NO_INFO -- indicates that the command was processed successfully
     * but that the number of rows affected is unknown
     * 
     * If one of the commands in a batch update fails to execute properly, this
     * method throws a BatchUpdateException, and a JDBC driver may or may not
     * continue to process the remaining commands in the batch. However, the
     * driver's behavior must be consistent with a particular DBMS, either
     * always continuing to process commands or never continuing to process
     * commands. If the driver continues processing after a failure, the array
     * returned by the method BatchUpdateException.getUpdateCounts will contain
     * as many elements as there are commands in the batch, and at least one of
     * the elements will be the following: A value of EXECUTE_FAILED --
     * indicates that the command failed to execute successfully and occurs only
     * if a driver continues to process commands after a command fails
     * 
     * The possible implementations and return values have been modified in the
     * Java 2 SDK, Standard Edition, version 1.3 to accommodate the option of
     * continuing to process commands in a batch update after a
     * BatchUpdateException object has been thrown.
     * 
     * Returns: an array of update counts containing one element for each
     * command in the batch. The elements of the array are ordered according to
     * the order in which commands were added to the batch. Throws: SQLException
     * - if a database access error occurs, this method is called on a closed
     * Statement or the driver does not support batch statements. Throws
     * BatchUpdateException (a subclass of SQLException) if one of the commands
     * sent to the database fails to execute properly or attempts to return a
     * result set. SQLTimeoutException - when the driver has determined that the
     * timeout value that was specified by the setQueryTimeout method has been
     * exceeded and has at least attempted to cancel the currently running
     * Statement
     */
    @Override
    public int[] executeBatch() throws SQLException {
	final int[] answer = new int[arr.size()];

	for (int i = 0; i < arr.size(); i++) {
	    this.threadKiller = false;
	    thread(queryTimeOut);
	    answer[i] = executeUpdate(arr.get(i));
	    this.threadKiller = true;
	}
	return answer;
    }

    /**
     * By default, only one ResultSet object per Statement object can be open at
     * the same time. Therefore, if the reading of one ResultSet object is
     * interleaved with the reading of another, each must have been generated by
     * different Statement objects. All execution methods in the Statement
     * interface implicitly close a current ResultSet object of the statement if
     * an open one exists.
     */

    /**
     * Executes the given SQL statement, which returns a single ResultSet
     * object.
     * 
     * Note:This method cannot be called on a PreparedStatement or
     * CallableStatement.
     * 
     * Parameters: sql - an SQL statement to be sent to the database, typically
     * a static SQL SELECT statement Returns: a ResultSet object that contains
     * the data produced by the given query; never null Throws: SQLException -
     * if a database access error occurs, this method is called on a closed
     * Statement, the given SQL statement produces anything other than a single
     * ResultSet object, the method is called on a PreparedStatement or
     * CallableStatement SQLTimeoutException - when the driver has determined
     * that the timeout value that was specified by the setQueryTimeout method
     * has been exceeded and has at least attempted to cancel the currently
     * running Statement
     */
    @Override
    public ResultSet executeQuery(String arg0) throws SQLException {
	this.threadKiller = false;
	thread(queryTimeOut);

	final Object[][] ans = dbms.executeQuery(arg0);
	this.threadKiller = true;
	final ResultSet result = resultPool.getPool(ans, this,
		new ArrayList<String>(dbms.getColumnsNames()),
		dbms.getTableNameForSelect());
	this.result = result;

	return result;
    }

    /**
     * Executes the given SQL statement, which may be an INSERT, UPDATE, or
     * DELETE statement or an SQL statement that returns nothing, such as an SQL
     * DDL statement.
     * 
     * Note:This method cannot be called on a PreparedStatement or
     * CallableStatement.
     * 
     * Parameters: sql - an SQL Data Manipulation Language (DML) statement, such
     * as INSERT, UPDATE or DELETE; or an SQL statement that returns nothing,
     * such as a DDL statement. Returns: either (1) the row count for SQL Data
     * Manipulation Language (DML) statements or (2) 0 for SQL statements that
     * return nothing Throws: SQLException - if a database access error occurs,
     * this method is called on a closed Statement, the given SQL statement
     * produces a ResultSet object, the method is called on a PreparedStatement
     * or CallableStatement SQLTimeoutException - when the driver has determined
     * that the timeout value that was specified by the setQueryTimeout method
     * has been exceeded and has at least attempted to cancel the currently
     * running Statement
     */
    @Override
    public int executeUpdate(String arg0) throws SQLException {
	this.threadKiller = false;
	thread(queryTimeOut);
	int ans = dbms.executeUpdateQuery(arg0);
	this.threadKiller = true;
	return ans;
    }

    /**
     * Retrieves the Connection object that produced this Statement object.
     * 
     * Returns: the connection that produced this statement Throws: SQLException
     * - if a database access error occurs or this method is called on a closed
     * Statement
     */

    @Override
    public Connection getConnection() throws SQLException {
	return connection;
    }

    /**
     * 
     * Retrieves the number of seconds the driver will wait for a Statement
     * object to execute. If the limit is exceeded, a SQLException is thrown.
     * 
     * Returns: the current query timeout limit in seconds; zero means there is
     * no limit Throws: SQLException - if a database access error occurs or this
     * method is called on a closed Statement
     * 
     * threads
     */
    @Override
    public int getQueryTimeout() throws SQLException {
	return queryTimeOut;
    }

    /**
     * Sets the number of seconds the driver will wait for a Statement object to
     * execute to the given number of seconds. By default there is no limit on
     * the amount of time allowed for a running statement to complete. If the
     * limit is exceeded, an SQLTimeoutException is thrown. A JDBC driver must
     * apply this limit to the execute, executeQuery and executeUpdate methods.
     * 
     * Note: JDBC driver implementations may also apply this limit to ResultSet
     * methods (consult your driver vendor documentation for details).
     * 
     * Note: In the case of Statement batching, it is implementation defined as
     * to whether the time-out is applied to individual SQL commands added via
     * the addBatch method or to the entire batch of SQL commands invoked by the
     * executeBatch method (consult your driver vendor documentation for
     * details).
     * 
     * Parameters: seconds - the new query timeout limit in seconds; zero means
     * there is no limit Throws: SQLException - if a database access error
     * occurs, this method is called on a closed Statement or the condition
     * seconds >= 0 is not satisfied
     */

    @Override
    public void setQueryTimeout(int arg0) throws SQLException {
	queryTimeOut = arg0;
    }

    public void set(String path, Connection connect) {
	this.path = path;
	this.connection = connect;
    }

    public void setWithNull() {
	this.arr = new ArrayList<>();
	this.threadKiller = false;
	this.isclosed = false;
	this.path = null;
	this.statePool = null;
	this.connection = null;
    }

    public void thread(int timeOutInt) throws SQLException {
	long timeOut = (long) timeOutInt * 1000;
	long startTime = System.currentTimeMillis();
	Thread t = new Thread() {
	    @Override
	    public void run() {
		while (true) {
		    System.out.println("Thread Running");
		    long currentTime = System.currentTimeMillis();
		    if ((currentTime - startTime) >= timeOut) {
			JOptionPane.showMessageDialog(null,
			              "TimeOut!!!");
			throw new RuntimeException("TimeOut!!!");
		    }
		    if (threadKiller == true) {
			return;
		    }
		}
	    }
	};
	t.start();
    }
}
