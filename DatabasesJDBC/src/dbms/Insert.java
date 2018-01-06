package dbms;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.XMLEvent;

public class Insert extends Statement {

	private final ArrayList<String> TableColumns = new ArrayList<>();

	public Insert(String[] querySplited, String currentDataBase, Object returnObject) {
		super(querySplited, currentDataBase, returnObject);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Object excute() throws Exception {


		returnObject = 0;
		Boolean writeColumns = false;
		final StringBuilder st = new StringBuilder();
		for (int i = 2; i < querySplited.length; i++) {

			st.append(querySplited[i]);

		}
		final String tem = st.toString().trim();
		int counter = 0;
		for( int i=0; i<tem.length(); i++ ) {
			if( tem.charAt(i) == '(' ) {
				counter++;
			}
		}
		String[] strings ;
		if( counter == 2){
//			this.returnObject = 1;
//			return returnObject;
//		}
		 strings = tem.trim().split("\\(", 2);
		}else {
			strings = tem.trim().split("values" , 2);
		}
		
		final File file = CheckTable(strings[0].trim());
		
		if (file == null || file.length() == 0) {
			this.returnObject = 0;
			return returnObject;
		}
		
		final String temp = file.getPath();
		final File tempFile = new File(this.currentDataBase + System.getProperty("file.separator") + "tempo" + ".xml");
		file.renameTo(tempFile);
		String pro = strings[1].trim();
		pro = NewString(pro);
		Map<String, String> mapColumns = new LinkedHashMap<>();
		
		final XMLInputFactory inFactory = XMLInputFactory.newInstance();
		final XMLOutputFactory factory = XMLOutputFactory.newInstance();
		final XMLEventFactory eventFactory = XMLEventFactory.newInstance();
		final FileOutputStream output = new FileOutputStream(new File(temp));
		final XMLEventWriter writer = factory.createXMLEventWriter(output, "ISO-8859-1");
		final FileInputStream input = new FileInputStream(tempFile);
		final XMLEventReader eventReader = inFactory.createXMLEventReader(input);
		// final XMLStreamWriter writer =
		// factory.createXMLStreamWriter(output,"ISO-8859-1");
		// writer.writeStartDocument("ISO-8859-1","1.0");
		// final FileWriter stringWriter = new FileWriter(table);
		// final XMLOutputFactory factory = XMLOutputFactory.newInstance();
		// final XMLStreamWriter writer =
		// factory.createXMLStreamWriter(stringWriter);
		// writer.writeStartDocument();
		while (eventReader.hasNext()) {

			XMLEvent event = eventReader.nextEvent();

			switch (event.getEventType()) {
			case XMLEvent.START_DOCUMENT:
				writer.add(eventFactory.createStartDocument("ISO-8859-1", "1.0"));
				continue;
				// break;
			case XMLEvent.START_ELEMENT:
				if (event.asStartElement().getName().getLocalPart().equals("elements")) {
					final Attribute iterate = event.asStartElement().getAttributeByName(new QName("count"));


					int i = Integer.valueOf(iterate.getValue());
					i++;
					final ArrayList<Namespace> ns = new ArrayList<Namespace>();
					final ArrayList<Attribute> atts  = new ArrayList<>();
					atts.add(eventFactory.createAttribute(iterate.getName(),   String.valueOf(i) +""));
					event = eventFactory.createStartElement("", "", "elements", atts.iterator(), ns.iterator());
				}
				if (event.asStartElement().getName().toString().equalsIgnoreCase("columns")) {

					writeColumns = true;
				} else if (writeColumns) {
					TableColumns.add(event.asStartElement().getName().toString());
				}
				break;

			case XMLEvent.END_ELEMENT:
				if (event.asEndElement().getName().toString().equalsIgnoreCase("elements")) {

					writer.add(eventFactory.createStartElement("", null, "element"));
					if(counter == 2) {
					    mapColumns = GetColumns(pro.toLowerCase().split("values", 2));
					} else {
							String [] s = {NewString(strings[1])};
							mapColumns = GetColumns(s);
						}
					for (int i = 0; i < TableColumns.size(); i++) {

						final String column = TableColumns.get(i).trim();
						writer.add(eventFactory.createStartElement("", null, column));
						String value = mapColumns.get(column);
						if (value == null) {
							value = "null";
						}
						writer.add(eventFactory.createCharacters(value));
						writer.add(eventFactory.createEndElement("", null, column));
					}
				} else if (event.asEndElement().getName().toString().equalsIgnoreCase("columns")) {
					writeColumns = false;
				}

				break;

			}

			writer.add(event);

		}
		writer.close();
		output.close();
		input.close();
		tempFile.delete();
		final int rowsAdded = 1;
		this.returnObject = rowsAdded;
		return rowsAdded;
	}

	private String NewString(String pro) {
		pro = pro.replaceAll("\\(", " ");
		pro = pro.replaceAll("\\)", " ");
		pro = pro.replaceAll(";", " ");
		pro = pro.trim();

		return pro;
	}

	private Map<String, String> GetColumns(String[] split) throws Exception {
		 String[] columns = null ;
		 String[] values = null ;
		if(split.length > 1){
		 columns = split[0].trim().split(",");
		 values = split[1].trim().split(",");
		}
		else {
			columns = new String[TableColumns.size()];
			for (int i = 0; i <TableColumns.size(); i++) {
				columns[i] = TableColumns.get(i);
			}
			values = split[0].trim().split(",");
		}
		// if values and columns were with different length
//		if (columns.length != values.length) {
//			
//			throw new java.sql.SQLException();
//		}
		final Map<String, String> properties = new LinkedHashMap<>();
		for (int i = 0; i < columns.length; i++) {
			properties.put(columns[i].trim(), values[i].trim());
		}

		return properties;
	}

	private File CheckTable(String trim) throws Exception {
		final File file = new File(this.currentDataBase + System.getProperty("file.separator") + trim + ".xml");
		final File file1 = new File(System.getProperty("user.dir") + System.getProperty("file.separator") + "databases");
		if(!file1.exists()){
			file1.mkdirs();
		}
		if (!file.exists() || file1.list().length == 0) {
			this.returnObject = 0;

			throw new java.sql.SQLException();
			//return null;
		}

		return file;
	}

}
