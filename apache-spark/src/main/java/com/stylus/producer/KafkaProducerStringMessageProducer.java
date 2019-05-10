package com.stylus.producer;

import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

//import com.esotericsoftware.kryo.serializers.DefaultSerializers.LongSerializer;
//import com.esotericsoftware.kryo.serializers.DefaultSerializers.StringSerializer;

import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;

public class KafkaProducerStringMessageProducer {

	public static void main(String [] args) throws Exception {
		runSyncProducer(20);
		runSyncProducer(5);
	}
	
	
	private static Producer<Long, String> createProducer() {
        //Kafka producer config
        //Configure the Producer
        Properties configProperties = new Properties();
        configProperties.put(ProducerConfig.CLIENT_ID_CONFIG, "KafkaProducerStringMessageProducer");
        configProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,"localhost:9092");
        configProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class.getName());
        configProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        
        return new KafkaProducer<>(configProperties);
	}
	
	//Send Sync
	static void runSyncProducer(final int sendMessageCount) throws Exception {
		final Producer<Long, String> producer = createProducer();
		long time = System.currentTimeMillis();
		
		try {
			for (long index = time; index < time + sendMessageCount; index++) {
				final ProducerRecord<Long, String> record = new ProducerRecord<>("messages", index, "Hello Vamsi" + index);
				
				RecordMetadata metadata = (RecordMetadata) producer.send(record).get();
				
				long elapsedTime = System.currentTimeMillis() - time;
				System.out.printf("Sent record(key=%s value=%s) meta(partition=%d, offset=%d) time=%d\n",
						             record.key(), record.value(), metadata.partition(), metadata.offset(), elapsedTime);
			}
		} finally {
			producer.flush();
			producer.close();
		}
	}
	
	//send Asynchronously with Callback lambda
	static void runAsyncProducer(final int sendMessageCount) throws Exception {
		final Producer<Long, String> producer = createProducer();
		long time = System.currentTimeMillis();
		final CountDownLatch countDownLatch =  new CountDownLatch(sendMessageCount);
		
		try {
			for (long index = time; index < time + sendMessageCount; index++) {
				final ProducerRecord<Long, String> record = new ProducerRecord<>("messages", index, "Hello Vamsi 2 " + index);
				producer.send(record, (metadata, exception) -> {
					long elapsedTime = System.currentTimeMillis() - time;
					if (metadata != null) {
						System.out.printf("sent record(key=%s value=%s) meta(partition=%d offset=%d) time=%d\n",
								record.key(), record.value(), metadata.partition(), metadata.offset(), elapsedTime);
					} else {
						exception.printStackTrace();
					}
					countDownLatch.countDown();
				});
			}
			countDownLatch.await(25, TimeUnit.SECONDS);
		} finally {
			producer.flush();
			producer.close();
		}
	}
	

}
