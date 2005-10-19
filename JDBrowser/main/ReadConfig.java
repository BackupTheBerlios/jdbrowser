package main;
import java.beans.IntrospectionException;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;

import org.apache.commons.betwixt.io.BeanReader;
import org.apache.commons.betwixt.io.BeanWriter;
import org.xml.sax.SAXException;

import config.DataSourceConfig;

public class ReadConfig {

	public ReadConfig() {
		StringWriter outputWriter = new StringWriter();
		try {
			test();
			// First construct the xml which will be read in
			// For this example, read in from a hard coded string
			File file = new File("config.xml");
			// Now convert this to a bean using betwixt
			// Create BeanReader
			BeanReader beanReader = new BeanReader();

			// Configure the reader
			// If you're round-tripping, make sure that the configurations are
			// compatible!
			beanReader.getXMLIntrospector().getConfiguration().setAttributesForPrimitives(false);
			beanReader.getBindingConfiguration().setMapIDs(false);

			// Register beans so that betwixt knows what the xml is to be
			// converted to
			// Since the element mapped to a PersonBean isn't called the same,
			// need to register the path as well
			beanReader.registerBeanClass("database", DataSourceConfig.class);

			DataSourceConfig config = (DataSourceConfig)  beanReader.parse(file);

			// send bean to system out
			System.out.println(config);
			System.out.println(outputWriter.toString());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IntrospectionException e) {
			e.printStackTrace();
		}

	}

public void test() {
	 StringWriter outputWriter = new StringWriter(); 
     
     // Betwixt just writes out the bean as a fragment
     // So if we want well-formed xml, we need to add the prolog
     outputWriter.write("<?xml version='1.0' ?>");
     
     // Create a BeanWriter which writes to our prepared stream
     BeanWriter beanWriter = new BeanWriter(outputWriter);
     
     // Configure betwixt
     // For more details see java docs or later in the main documentation
     beanWriter.getXMLIntrospector().getConfiguration().setAttributesForPrimitives(false);
     beanWriter.getBindingConfiguration().setMapIDs(false);
     beanWriter.enablePrettyPrint();
     
     // Write example bean as base element 'person'
     try {
		beanWriter.write("database", new DataSourceConfig("alias","driver","password", "userid", "url"));
	} catch (IOException e) {
		e.printStackTrace();
	} catch (SAXException e) {
		e.printStackTrace();
	} catch (IntrospectionException e) {
		e.printStackTrace();
	}
     
     // Write to System.out
     // (We could have used the empty constructor for BeanWriter
     // but this way is more instructive)
     System.out.println(outputWriter.toString());
}}
