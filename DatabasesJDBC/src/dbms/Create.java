package dbms;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

public class Create extends Statement {

	private boolean result;
	private final Exception SQLException = null;

	public Create(String[] querySplited, String currentDataBase, Object returnObject) {
		super(querySplited, currentDataBase, returnObject);
	}

	@Override
	public Object excute() throws Exception {
		if (super.querySplited[1].equalsIgnoreCase("database")) {
			final File file = new File(System.getProperty("user.dir") + System.getProperty("file.separator") + "databases");
			if(currentDataBase == null){
				currentDataBase = file.getAbsolutePath();
			}
			if (!file.exists()) {
				file.mkdirs();
			}
			if (!currentDataBase.contains(querySplited[2].toLowerCase().trim())){
				final File file1 = new File(System.getProperty("user.dir") + System.getProperty("file.separator") + "databases" + System.getProperty("file.separator") + querySplited[2]);

				super.currentDataBase = file1.getAbsolutePath();
			}
			final File f = new File(super.currentDataBase);
			final boolean temp = f.mkdirs();
			this.returnObject = temp;
			return temp;
		} else if (super.querySplited[1].equalsIgnoreCase("table")) {


			if(currentDataBase == null){
				this.returnObject = false;
				throw new SQLException();
			}
			if( querySplited.length <=3){
				this.returnObject = false ;
				throw new java.sql.SQLException();
				//return false;
				//throw SQLException;
			}
			final StringBuilder st = new StringBuilder();
			for (int i = 2; i < querySplited.length; i++) {
				st.append(querySplited[i] + " ");
			}
			final File file = new File(System.getProperty("user.dir") + System.getProperty("file.separator") + "databases");
			if (/*currentDataBase == null ||*/ !file.exists() || file.list().length == 0) {
				this.returnObject = false;
				//throw SQLException;
			}
			final String test = st.toString();
			return createTable(test);

		}
		return true;
	}

	private boolean createTable(String columns) throws Exception {
		columns = columns.replaceAll("\\)", "");
		final ArrayList<String> ColumnNames = new ArrayList<>();
		columns = columns.trim();
		String[] strings = columns.split("\\(");

		final String tableName = strings[0].trim();
		if(strings.length < 2 ){
			returnObject =false;
			throw new java.sql.SQLException();
			//return false;
		}
		final File table = CheckTable(tableName);
		if (result && table != null) {
			try {
				final FileWriter stringWriter = new FileWriter(table);
				final XMLOutputFactory factory = XMLOutputFactory.newInstance();
				final FileOutputStream output = new FileOutputStream(table);
				final XMLStreamWriter writer = factory.createXMLStreamWriter(output, "ISO-8859-1");
				writer.writeStartDocument("ISO-8859-1", "1.0");
				writer.writeStartElement(tableName);
				writer.writeStartElement("columns");
				strings[1].trim();
				strings = strings[1].split(",");
				writer.writeAttribute("count", String.valueOf(strings.length));

				for (int i = 0; i < strings.length; i++) {

					final String[] column = strings[i].trim().split(" ", 2);

					final String name = column[0].trim();
					if (!ColumnNames.contains(name)) {
						ColumnNames.add(name);
						writer.writeStartElement(name);
						writer.writeAttribute("type", column[1].trim());
						writer.writeEndElement();
					}

				}
				writer.writeEndElement();
				writer.writeStartElement("elements");
				writer.writeAttribute("count", "0");
				writer.writeEndElement();
				writer.writeEndDocument();
				writer.flush();
				writer.close();
				output.close();
				stringWriter.close();
			} catch (final Exception e) {
				// TODO: handle exception
			}

		}
		this.returnObject = result;
		return result;

	}

	private File CheckTable(String tableName) throws Exception {
		// check from the keywords
		final File db = new File(this.currentDataBase);
		File table = new File(this.currentDataBase + System.getProperty("file.separator") + tableName + ".xml");
		if (db.exists() && table.exists()) {
			result = false;
		} else if (db.exists()) {
			table.createNewFile();
			result = true;
		} else {
			result = false;
			table = null;
		}
		return table;

	}

}
