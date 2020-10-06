package com.unclepaul.uicacheprototype.materializedview;

import com.unclepaul.uicacheprototype.utils.ManualResetEvent;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;

import java.time.Duration;
import java.util.*;

import static org.slf4j.LoggerFactory.getLogger;

public class KafkaTopicEntityListener<T> {
    protected static final Logger log = getLogger(KafkaTopicEntityListener.class);

    private final String _topic;
    private final java.util.function.Consumer<Collection<T>> _batchProcessor;
    private final Class<T> _tClass;
    private final Long _fullSnapshotEstimate;
    private Consumer<Long, String> _consumer;
    private volatile Thread _pollerThread;
    protected Gson _deSerializer;
    private final ManualResetEvent _snapshotReceived = new ManualResetEvent(false);


    KafkaTopicEntityListener(String topic, java.util.function.Consumer<Collection<T>> batchProcessor, Class<T> tClass, Long fullSnapshotEstimate) {

        _topic = topic;
        _batchProcessor = batchProcessor;
        _tClass = tClass;
        _fullSnapshotEstimate = fullSnapshotEstimate;
        GsonBuilder builder = new GsonBuilder();
        _deSerializer = builder.create();

        if(fullSnapshotEstimate==0)
            _snapshotReceived.set();

    }

    private Consumer<Long, String> createConsumer() {

        Properties props = new Properties();

        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");

        props.put(ConsumerConfig.GROUP_ID_CONFIG, "materialized-view-" + this._topic);

        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class.getName());

        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 1000000); // batch size

        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");

        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        Consumer<Long, String> consumer = new KafkaConsumer<>(props);

        return consumer;

    }


    public void start() throws Exception {
        if (_pollerThread != null)
            throw new Exception("Already Running!");

        _consumer = createConsumer();
        _consumer.subscribe(Collections.singletonList(_topic));

        _pollerThread = new Thread(() -> {

            Long totalMessages = 0L;

            while (true) {
                var recordBatch = _consumer.poll(Duration.ofMillis(100));

                if (!recordBatch.isEmpty()) {

                    List<T> batch = new ArrayList();

                    for (ConsumerRecord<Long, String> rec : recordBatch) {

                        totalMessages++;

                        batch.add(_deSerializer.fromJson(rec.value(), _tClass));
                    }

                    _batchProcessor.accept(batch);

                    if (!_snapshotReceived.isSet() && totalMessages >= _fullSnapshotEstimate)
                        _snapshotReceived.set();
                }
            }
        });

        _pollerThread.start();
    }

    public ManualResetEvent getSnapshotReceivedWaiter() {
        return _snapshotReceived;
    }

}
