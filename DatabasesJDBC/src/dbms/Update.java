package dbms;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

public class Update extends Statement {

	private ArrayList<String> columnsNeeded;
	private int results;
	private HandleCondition handler;
	ArrayList<TwoStrings> values;
	private static final Exception SQLClientInfoException = null;
	private final Exception SQLException = null;

	public Update(String[] querySplited, String currentDataBase, Object returnObject) {
		super(querySplited, currentDataBase, returnObject);
		this.results = 0;
		// TODO Auto-generated constructor stub
	}

	@Override
	public Object excute() throws Exception {
		returnObject = new Object();
		final StringBuilder st = new StringBuilder();
		for (int i = 1; i < querySplited.length; i++) {

			st.append(querySplited[i]);

		}
		final String query = st.toString();
		File file = null;
		String[] strings = query.toLowerCase().split("set", 2);
		file = CheckTable(strings[0].trim());
		if (file == null || file.length() == 0) {
			this.returnObject = 0;
			return 0;
		}
		final String temp = file.getAbsolutePath();
		final File tempFile = new File(this.currentDataBase +System.getProperty("file.separator") + "tem" + ".xml");
		System.out.println(file.renameTo(tempFile));

		if (strings[1].toLowerCase().contains("where")) {
			strings = strings[1].split("where");
			handler = new HandleCondition(strings[1].trim());
			values = ValuesNeeded(strings[0].trim());

		} else {
			handler = new HandleCondition();
			handler.setConditionsArray(null);
			values = ValuesNeeded(strings[1].trim());

		}

		Iterate(tempFile, temp);
		this.returnObject = results;
		return results;
	}

	private void Iterate(File file, String temp) throws XMLStreamException, IOException {
		boolean writeColumns = false;
		boolean writeElement = false;

		final XMLOutputFactory factory = XMLOutputFactory.newInstance();
		final XMLInputFactory inFactory = XMLInputFactory.newInstance();
		final XMLEventFactory eventFactory = XMLEventFactory.newInstance();
		final FileOutputStream output = new FileOutputStream(new File(temp));
		final XMLEventWriter writer = factory.createXMLEventWriter(output, "ISO-8859-1");
		final FileInputStream input = new FileInputStream(file);
		final XMLEventReader eventReader = inFactory.createXMLEventReader(input);

		while (eventReader.hasNext()) {
			final XMLEvent event = eventReader.nextEvent();
			if (!writeElement && event.getEventType() != XMLEvent.START_DOCUMENT) {
				writer.add(event);
			}
			switch (event.getEventType()) {

			case XMLEvent.START_DOCUMENT:
				writer.add(eventFactory.createStartDocument("ISO-8859-1", "1.0"));
				continue;
			case XMLEvent.START_ELEMENT:
				if (writeElement) {
					XMLEvent elementEvent = event;
					Map<String, String> elementMap = new LinkedHashMap<>();
					while (elementEvent.getEventType() != XMLEvent.END_ELEMENT) {
						// !elementEvent.asEndElement().getName().toString().equalsIgnoreCase("element")
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

					final boolean put = handler.checkCondition(elementMap);

					if (put) {
						results++;
						elementMap = updateMap(elementMap);
					}

					WriteElement(elementMap, writer, eventFactory);
					writer.add(elementEvent);
					writeElement = false;

				}
				writeElement = StartElementConditions(event, writeElement);
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

		}
		writer.close();
		output.close();
		input.close();
		file.delete();

	}

	private void WriteElement(Map<String, String> elementMap, XMLEventWriter writer, XMLEventFactory eventFactory)
			throws XMLStreamException {

		for (final Map.Entry<String, String> element : elementMap.entrySet()) {
			writer.add(eventFactory.createStartElement("", null, element.getKey()));
			writer.add(eventFactory.createCharacters(element.getValue()));
			writer.add(eventFactory.createEndElement("", null, element.getKey()));

		}

	}

	private Map<String, String> updateMap(Map<String, String> elementMap) {

		for (int i = 0; i < values.size(); i++) {

			elementMap.put(values.get(i).getString(), values.get(i).getValue());
		}
		return elementMap;
	}

	private ArrayList<TwoStrings> ValuesNeeded(String columnsValues) {
		final ArrayList<TwoStrings> list = new ArrayList<>();
		final String[] strings = columnsValues.split(",");
		for (int i = 0; i < strings.length; i++) {
			final String[] values = strings[i].trim().split("=", 2);
			final TwoStrings needed = new TwoStrings(values[0], values[1]);
			list.add(needed);
		}

		return list;
	}

	private boolean StartElementConditions(XMLEvent event, boolean writeElement) {

		if (event.asStartElement().getName().toString().equalsIgnoreCase("element")) {

			return true;
		}
		return writeElement;

	}

	private File CheckTable(String trim) throws Exception {
		final File file = new File(this.currentDataBase + System.getProperty("file.separator") + trim + ".xml");
		final File file1 = new File(System.getProperty("user.dir") + System.getProperty("file.separator") + "databases");
		if (!file.exists() || file1.list().length == 0 ) {
			this.returnObject = 1;
			throw new java.sql.SQLException();			//return null;
		}

		return file;
	}

}
