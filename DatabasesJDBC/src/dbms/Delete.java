package dbms;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.XMLEvent;

public class Delete extends Statement {
	private boolean deleteAllTable;
	private int result;
	private HandleCondition handler;
	private final Exception SQLException = null;


	public Delete(String[] querySplited, String currentDataBase, Object returnObject) {
		super(querySplited, currentDataBase, returnObject);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Object excute() throws Exception {
		result = 0;
		final StringBuilder st = new StringBuilder();
		for (int i = 1; i < querySplited.length; i++) {

			st.append(querySplited[i]);

		}
		final String query = st.toString();
		String[] strings = query.toLowerCase().split("from", 2);
		if (!strings[1].contains("where")) {
			deleteAllTable = true;
		}
		File file = null;
		if (strings[1].toLowerCase().contains("where")) {
			strings = strings[1].toLowerCase().split("where", 2);
			file = CheckTable(strings[0].trim());
			String conditionString;
			conditionString = strings[1];
			handler = new HandleCondition(conditionString);

		} else {
			handler = new HandleCondition();
			handler.setConditionsArray(null);
			file = CheckTable(strings[1].trim());
			if (file == null || file.length() == 0) {
				this.returnObject = 0;
				return returnObject;
			}

		}
		Iterate(file);
		super.returnObject = result;
		return result;

	}

	private void Iterate(File file) throws XMLStreamException, IOException {
		boolean writeColumns = false;
		boolean writeElement = false;
		final String temp = file.getAbsolutePath();
		final File tempFile = new File(this.currentDataBase + System.getProperty("file.separator") + "tempo" + ".xml");
		file.renameTo(tempFile);
		final XMLInputFactory inFactory = XMLInputFactory.newInstance();
		final XMLOutputFactory factory = XMLOutputFactory.newInstance();
		final XMLEventFactory eventFactory = XMLEventFactory.newInstance();
		final FileOutputStream output = new FileOutputStream(new File(temp));
		final XMLEventWriter writer = factory.createXMLEventWriter(output, "ISO-8859-1");
		final FileInputStream input = new FileInputStream(tempFile);
		final XMLEventReader eventReader = inFactory.createXMLEventReader(input);

		while (eventReader.hasNext()) {
			XMLEvent event = eventReader.nextEvent();
			switch (event.getEventType()) {
			case XMLEvent.START_DOCUMENT:
				writer.add(eventFactory.createStartDocument("ISO-8859-1", "1.0"));
				continue;
			case XMLEvent.START_ELEMENT:
				if (event.asStartElement().getName().toString().equals("elements")) {
					if (deleteAllTable) {
						final Attribute at = event.asStartElement().getAttributeByName(new QName("count"));
						result = Integer.parseInt(at.getValue());
						final ArrayList<Namespace> ns = new ArrayList<Namespace>();
						final ArrayList<Attribute> atts = new ArrayList<>();
						atts.add(eventFactory.createAttribute("count", String.valueOf(0)));
						writer.add(
								eventFactory.createStartElement("", null, "elements", atts.iterator(), ns.iterator()));
						writer.add(eventFactory.createEndElement("", null, "elements"));
						final StringBuilder st = new StringBuilder();
						for (int i = 1; i < querySplited.length; i++) {

							st.append(querySplited[i]);

						}
						final String query = st.toString();
						final String[] strings = query.toLowerCase().split("from", 2);
						writer.add(eventFactory.createEndElement("", null, strings[1]));
						writer.close();
						output.close();
						input.close();
						tempFile.delete();
						return;
					}
					break;
				}
				if (writeElement) {
					XMLEvent elementEvent = event;
					final Map<String, String> elementMap = new LinkedHashMap<>();
					while (elementEvent.getEventType() != XMLEvent.END_ELEMENT) {
						if (!elementEvent.isStartElement()) {
							elementEvent = eventReader.nextEvent();
							continue;
						}
						final String columnName = elementEvent.asStartElement().getName().toString();
						elementEvent = eventReader.nextEvent();
						elementMap.put(columnName, elementEvent.asCharacters().getData().trim());
						elementEvent = eventReader.nextEvent();
						elementEvent = eventReader.nextEvent();
					}
					// final boolean put = checkCondition(elementMap);

					final boolean put = handler.checkCondition(elementMap);

					if (put) {
						result++;
					} else {
						writer.add(eventFactory.createStartElement("", null, "element"));
						for (final Map.Entry<String, String> column : elementMap.entrySet()) {
							writer.add(eventFactory.createStartElement("", null, column.getKey()));
							writer.add(eventFactory.createCharacters(column.getValue()));
							writer.add(eventFactory.createEndElement("", null, column.getKey()));

						}
						writer.add(eventFactory.createEndElement("", null, "element"));
					}
					elementEvent = eventReader.nextEvent();
					event = elementEvent;
					try {
						if (!event.asStartElement().getName().toString().equals("element")) {
							writeElement = false;
						} else {
							continue;
						}
					} catch (final Exception e) {
						// TODO: handle exception
					}
					break;
				}
				writeElement = StartElementConditions(event, writeElement);
				if (writeElement) {
					continue;
				}
				// writer.add(event);
				// use or not
				// writeColumns = StartColumnsConditions(event, writeColumns);

				break;

			case XMLEvent.END_ELEMENT:
				/*
				 * if
				 * (event.asEndElement().getName().toString().equalsIgnoreCase(
				 * "element")) {
				 *
				 * writeElement = false;
				 *
				 * } else
				 */if (event.asEndElement().getName().toString().equalsIgnoreCase("columns")) {
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

	}

	private boolean StartElementConditions(XMLEvent event, boolean writeElement) {

		if (event.asStartElement().getName().toString().equalsIgnoreCase("element")) {

			return true;
		}
		return writeElement;

	}

	private File CheckTable(String trim) throws Exception {
		final File file = new File(super.currentDataBase + System.getProperty("file.separator") + trim + ".xml");
		final File file1 = new File(System.getProperty("user.dir") + System.getProperty("file.separator") + "databases");
		if (!file.exists() || file1.list().length == 0) {
			this.returnObject = 0;
			throw new java.sql.SQLException();
			//return null;
		}

		return file;
	}

}
