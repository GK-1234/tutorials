package com.stylus.producer;

import java.util.Collections;
import java.util.Properties;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;


import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;

public class KafkaStringMessageConsumer {

	public static void main(String [] args) throws Exception {
		runConsumer();
	}
	
	
	private static Consumer<Long, String> createConsumer() {

        Properties configProperties = new Properties();
        configProperties.put(ConsumerConfig.GROUP_ID_CONFIG, "KafkaStringMessageConsumer");
        configProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,"localhost:9092,localhost:9093,localhost:9094");
        configProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class.getName());
        configProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        
        KafkaConsumer<Long, String> consumer = new KafkaConsumer<>(configProperties);
        
        consumer.subscribe(Collections.singletonList("messages"));
        return consumer;
	}

	static void runConsumer() throws InterruptedException {
		final Consumer<Long, String> consumer = createConsumer();
		final int giveUp = 100; int recordCount = 0;
		
		while (true) {
			
			final ConsumerRecords<Long, String> records = consumer.poll(1000);
			
			if (records.count() == 0) {
				recordCount++;
			
				if (recordCount > giveUp) {
					break;
				} else {
					continue;
				}			
			}
		
		records.forEach( record -> System.out.printf("Consumer record:(%s, %s, %d, %d, args)\n",
								record.key(), record.value(), record.partition(), record.offset()));
		
		consumer.commitAsync();
		}
		consumer.close();
		System.out.println("DONE");
	}
}
