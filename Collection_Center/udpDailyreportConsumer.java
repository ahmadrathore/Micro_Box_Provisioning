//Grid based UDP throughput data for Sites using active monitoring
package smartx.multiview.collectors.topology;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.bson.Document;

import smartx.multiview.DataLake.MongoDB_Connector;

public class udpDailyreportConsumer implements Runnable {
	private Thread thread;
	//private String topologyMongoCollection_today = "daily-report-udp-data-raw_now"; //Change collection name
	private String topologyMongoCollection = "daily-report-udp-data-raw"; //Change collection name
	
	private String ThreadName = "udp Daily report Thread";
	private String bootstrapServer;
	private MongoDB_Connector mongoConnector;
	private String topic = "daily_report_throughput_udp"; 
	private Document document;

	@Override
	public void run() {

		System.out.println("Running "+ThreadName);
		System.out.println("*******Printing the udp data at udpDailyreportConsumer****");
		try {
			this.Consume();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public udpDailyreportConsumer(String bootstrapserver, MongoDB_Connector MongoConn) 
	{
		bootstrapServer        = bootstrapserver;
		mongoConnector         = MongoConn;
	}

	public void Consume() throws IOException{
		//Kafka & Zookeeper Properties
		Properties props = new Properties();
		props.put("bootstrap.servers", bootstrapServer);
		props.put("group.id", "test");
		props.put("enable.auto.commit", "true");
		props.put("auto.commit.interval.ms", "1000");
		props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(props);
		consumer.subscribe(Arrays.asList(topic));
		Map<Integer, String> hmap = new HashMap<Integer,String>();


		/*MongoCollection<Document> collection_process = mongoConnector.getDbConnection().getCollection(topologyMongoCollection);
		for (ConsumerRecord<String, String> record : records)
		{
		document.containsValue(value)
		collection_process.deleteMany(document);	
		}*/

		//mongoConnector.getDbConnection().getCollection(topologyMongoCollection).find({ $where: function () { return Date.now() - this._id.getTimestamp() < (24 * 60 * 60 * 1000)  }  });


		while (true) 
		{
			ConsumerRecords<String, String> records = consumer.poll(0);
			for (ConsumerRecord<String, String> record : records)
			{

				StoreToDB_raw(record.value());
				System.out.println("Printing the udp data at udpDailyreportConsumer "+record.value());
			}

		}
	}


	//Printing the udp throughput data
	//private int BoxID_GIST1=1, BoxID_ID=2, BoxID_PH=3, BoxID_PKS=4, BoxID_HUST=5, BoxID_GIST2=6, BoxID_CHULA=7, BoxID_NCKU=8, BoxID_MY=9, BoxID_MYREN=10, BoxID_GIST3=11, BoxID_NUC=12;
	private void StoreToDB_raw(String record) {
		// TODO Auto-generated method stub
		document = new Document();
		String[] record_values = record.split(" ");
		System.out.println(Arrays.toString(record_values));
		System.out.printf("Length:%d",record_values.length);
		//System.out.println(record_values[0]);
		//System.out.println(record_values[1]);
		if (record_values.length>2)
		{
			System.out.println(record_values[2]);
			document.put("timestamp",   (record_values[0]+" "+record_values[1]));
			document.put("SmartX-Box-SOURCE",   record_values[2]);
			if (record_values.length>5)
			{
				document.put("SmartX-Box-GIST1",   record_values[3]);
				//				document.put("SmartX-Box-ID",   record_values[4]);
				document.put("SmartX-Box-PH",   record_values[5]);
				//				document.put("SmartX-Box-PKS",   record_values[6]);
				document.put("SmartX-Box-HUST",   record_values[7]);
				//				document.put("SmartX-Box-GIST2",   record_values[8]);
				document.put("SmartX-Box-CHULA",   record_values[9]);
				//document.put("SmartX-Box-NCKU",   record_values[10]);
				//				document.put("SmartX-Box-MY",   record_values[11]);
				document.put("SmartX-Box-MYREN",   record_values[12]);
				document.put("SmartX-Box-GIST3",   record_values[13]);
				if (record_values.length>14 )
				{
					document.put("SmartX-Box-GIST_NUC",   record_values[14].isEmpty() ? " " : record_values[14]);	
				}

			}	
		}

		/*DBCollection collection = (DBCollection) mongoConnector.getDbConnection().getCollection(topologyMongoCollection);
		//document.put("BoxID_NUC",   record_values[13]);
		mongoConnector.getDbConnection().getCollection(topologyMongoCollection).insertOne(document);
		DBCursor cursor = collection.find();
		while (cursor.hasNext()) {
		    collection.remove(cursor.next());
		}
		mongoConnector.getDbConnection().getCollection(topologyMongoCollection_today).insertOne(document);*/
		mongoConnector.getDbConnection().getCollection(topologyMongoCollection).insertOne(document);
		
		document.clear();

		 
		String date_time = "Daily-report-udp-data"+"_"+record_values[0];
		String csvFile = date_time.replace("/", "")+".csv";
		String csvFile1 = date_time.replace("/", "")+"1.csv";
		
	    /* try {
	    	System.out.println("***Writing to file***");
	    	BufferedWriter out = new BufferedWriter(new FileWriter("/active_monitoring/Daily-report-udp-data/"+csvFile));
	    	out.write(Arrays.toString(record_values));
	    	//PrintWriter out = new PrintWriter(csvFile);
			//FileWriter writer = new FileWriter("abc.csv");
			//out.println(Arrays.toString(record_values));
			//writer.write(Arrays.toString(record_values));
			out.close();
	        //writer.close();
	        
	        File file = new File("/home/netcs/active_monitoring/Daily-report-udp-data/"+csvFile);
	        if(file.exists())
	        {
	        System.out.println("***file already exist***");
	     
	        }
	        FileWriter fileWriter = new FileWriter(file);
			fileWriter.write(Arrays.toString(record_values));
			fileWriter.flush();
			fileWriter.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	     
		
		try(FileWriter fw = new FileWriter("/home/netcs/active_monitoring/Daily-report-udp-data/"+csvFile, true);

				BufferedWriter bw = new BufferedWriter(fw);

				PrintWriter out = new PrintWriter(bw))
		{

			//out.println(Arrays.toString(record_values));
			File file = new File("/home/netcs/active_monitoring/Daily-report-udp-data/"+csvFile);
	        if (file.length() == 0)
	        {
	        	out.println("Date"+" "+"TIME"+" "+"SOURCE-SITE"+" "+"SmartX-Box-GIST1"+" "+"SmartX-Box-PH"+" "+"SmartX-Box-HUST"+" "+"SmartX-Box-CHULA"+" "+"SmartX-Box-MYREN"+" "+"SmartX-Box-GIST3");	
	        }
			out.println((record_values[0]+" "+record_values[1]+" "+record_values[2]+" "+record_values[3]+" "+record_values[5]+" "+record_values[7]+" "+record_values[9]+" "+record_values[12]+" "+record_values[13]));
			//more code

			//more code
		} catch (IOException e) {
			//exception handling left as an exercise for the reader
		}
		




	}



	public void start() {
		if (thread == null) {
			thread = new Thread(this, ThreadName);
			thread.start();
		}
	}

}
