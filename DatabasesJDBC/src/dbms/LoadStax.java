package dbms;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

public class LoadStax {
	public static void main(String[] args) throws XMLStreamException, IOException {
		/*boolean bFirstName = false;
		boolean bLastName = false;
		boolean bNickName = false;
		boolean bMarks = false;

		try {
			final XMLInputFactory factory = XMLInputFactory.newInstance();
			final XMLEventReader eventReader =
					factory.createXMLEventReader(new FileReader("input.txt"));

			while(eventReader.hasNext()) {
				final XMLEvent event = eventReader.nextEvent();

				switch(event.getEventType()) {

				case XMLStreamConstants.START_ELEMENT:
					final StartElement startElement = event.asStartElement();
					final String qName = startElement.getName().getLocalPart();

					if (qName.equalsIgnoreCase("student")) {
						System.out.println("Start Element : student");
						//final Iterator<Attribute> attributes = startElement.getAttributes();
						//final String rollNo = attributes.next().getValue();
						//System.out.println("Roll No : " + rollNo);
					} else if (qName.equalsIgnoreCase("firstname")) {
						bFirstName = true;
					} else if (qName.equalsIgnoreCase("lastname")) {
						bLastName = true;
					} else if (qName.equalsIgnoreCase("nickname")) {
						bNickName = true;
					}
					else if (qName.equalsIgnoreCase("marks")) {
						bMarks = true;
					}
					break;

				case XMLStreamConstants.CHARACTERS:
					final Characters characters = event.asCharacters();
					if(bFirstName) {
						System.out.println("First Name: " + characters.getData());
						bFirstName = false;
					}
					if(bLastName) {
						System.out.println("Last Name: " + characters.getData());
						bLastName = false;
					}
					if(bNickName) {
						System.out.println("Nick Name: " + characters.getData());
						bNickName = false;
					}
					if(bMarks) {
						System.out.println("Marks: " + characters.getData());
						bMarks = false;
					}
					break;

				case XMLStreamConstants.END_ELEMENT:
					final EndElement endElement = event.asEndElement();

					if(endElement.getName().getLocalPart().equalsIgnoreCase("student")) {
						System.out.println("End Element : student");
						System.out.println();
					}
					break;
				}
			}
		} catch (final FileNotFoundException e) {
			e.printStackTrace();
		} catch (final XMLStreamException e) {
			e.printStackTrace();
		}*/

		final XMLInputFactory inFactory = XMLInputFactory.newInstance();

		final File f1 = new File("input.txt");
		final String temp = f1.getAbsolutePath();
		final File tempo = new File("temp.txt");
		f1.renameTo(tempo);

		final XMLEventReader eventReader = inFactory.createXMLEventReader(new FileInputStream(tempo));
		final XMLOutputFactory factory = XMLOutputFactory.newInstance();
		final XMLEventWriter writer = factory.createXMLEventWriter(new FileWriter(new File (temp)));
		final XMLEventFactory eventFactory = XMLEventFactory.newInstance();
		while (eventReader.hasNext()) {
			final XMLEvent event = eventReader.nextEvent();
			if (event.getEventType() == XMLEvent.END_ELEMENT) {
				if (event.asEndElement().getName().toString().equalsIgnoreCase("class")) {
					writer.add(eventFactory.createStartElement("", null, "student"));
					writer.add(eventFactory.createEndElement("", null, "student"));
				}
			}
			writer.add(event);

		}
		writer.close();
		//System.out.println(f.exists());
		//tempo.delete();


	}
}