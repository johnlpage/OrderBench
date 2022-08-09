package com.mongodb.jlp.orderbench;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.cli.*;
import org.bson.Document;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestOptions extends Document {
	Logger logger;

	TestOptions(String[] args) throws ParseException {
		super();

		logger = LoggerFactory.getLogger(TestOptions.class);

		CommandLineParser parser = new DefaultParser();

		Options cliopt;
		cliopt = new Options();
		cliopt.addOption("c", "config", true, "Config file with parameters");
		cliopt.addOption("u", "uri", true, "MongoDB COnnection URI (localhost:27017)");
		cliopt.addOption("l", "load", false, "Perform Initial Data Load");
		cliopt.addOption("s", "size", true, "Size of orders (4000)10");
		cliopt.addOption("n", "customers", true, "Number of customers");
		cliopt.addOption("m", "orders", true, "Number of orders per customer");
		cliopt.addOption("i", "items", true, "Number of items per order");
		cliopt.addOption("p", "products", true, "Number of products");
		cliopt.addOption("t", "threads", true, "Number of client threads (default 20)");
		cliopt.addOption("z", "iterations", true, "Number of operation to test (default 50000)");

		CommandLine cmd;
		try {
			cmd = parser.parse(cliopt, args);
		} catch (ParseException e) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("OrderBench", cliopt);
			throw e;
		}

		JSONObject configFileParams = new JSONObject();;
		if (cmd.hasOption("config")) {
			JSONParser jsonparser = new JSONParser();
			try {
				Object parsedObj = jsonparser.parse(new FileReader(cmd.getOptionValue("config")));
				configFileParams =  (JSONObject) parsedObj;
				logger.info(configFileParams.toString());
			} catch (FileNotFoundException e) {
				logger.error("Can't open config file");
				e.printStackTrace();
			} catch (IOException e) {
				logger.error("Issue in reading config file");
				e.printStackTrace();
			} catch (org.json.simple.parser.ParseException e) {
				logger.error("Issue in parsing JSON in config file");
				e.printStackTrace();
			}
		}
		
		if (cmd.hasOption("uri")) {
			this.put("uri", cmd.getOptionValue("uri"));
		} else {
			this.put("uri", "mongodb://localhost:27017");
		}

		if (cmd.hasOption("load")) {
			this.put("load", true);
		}

		String[] integerParameters = { "size", "customers", "orders", "items", "products", "threads", "iterations" };
		Document integerDefaults = Document
			.parse("{size:4096,customers:10000,orders:20,items:5,products:10000,iterations:50000}");

		for (String param : integerParameters) {
			if (cmd.hasOption(param)) {
				this.put(param, Integer.parseInt(cmd.getOptionValue(param)));
			} else if ( configFileParams.containsKey(param) ) {
				this.put(param, ((Long) configFileParams.get(param)).intValue());
			} else {
				this.put(param, integerDefaults.getInteger(param, 1));
			}
		}

	}

}
