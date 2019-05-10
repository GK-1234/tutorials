$KAFKA/bin/kafka-topics.sh --create \
 --zookeeper localhost:2181 \
 --replication-factor 1 --partitions 4 \
 --topic messages
$SPARK/bin/spark-submit \
  --class com.stylus.data.pipeline.WordCountingAppWithCheckpoint \
  --master local[2] \
  target/apache-spark-1.0-SNAPSHOT-jar-with-dependencies.jar