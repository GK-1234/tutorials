package com.stylus;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.*;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;

import scala.Tuple2;

public class WordCount {

    private static final Pattern SPACE = Pattern.compile(" ");
    private static final String topicName = "messages";
    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.err.println("Usage: JavaWordCount <file>");
            System.exit(1);
        }
        SparkConf sparkConf = new SparkConf().setAppName("JavaWordCount")
            .setMaster("local");
        JavaSparkContext ctx = new JavaSparkContext(sparkConf);
        JavaRDD<String> lines = ctx.textFile(args[0], 1);

        JavaRDD<String> words = lines.flatMap(s -> Arrays.asList(SPACE.split(s)).iterator());
        JavaPairRDD<String, Integer> wordAsTuple = words.mapToPair(word -> new Tuple2<>(word, 1));
        JavaPairRDD<String, Integer> wordWithCount = wordAsTuple.reduceByKey((Integer i1, Integer i2)->i1 + i2);
        List<Tuple2<String, Integer>> output = wordWithCount.collect();

        //Kafka producer config
        //Configure the Producer
        Properties configProperties = new Properties();
        configProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,"localhost:9092");
        configProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,"org.apache.kafka.common.serialization.ByteArraySerializer");
        configProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,"org.apache.kafka.common.serialization.StringSerializer");

        org.apache.kafka.clients.producer.Producer producer = new KafkaProducer<String, String>(configProperties);

        for (Tuple2<?, ?> tuple : output) {
     //       ProducerRecord<String, String> rec = new ProducerRecord<String, String>(topicName, tuple);
           // producer.send(rec);
             System.out.println(tuple._1() + ": " + tuple._2());
        }
        producer.close();
        ctx.stop();
    }
}
