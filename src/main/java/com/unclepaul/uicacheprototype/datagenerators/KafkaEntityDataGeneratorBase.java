package com.unclepaul.uicacheprototype.datagenerators;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.unclepaul.uicacheprototype.utils.ManualResetEvent;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class KafkaEntityDataGeneratorBase<T> {
    protected static final Logger log = LoggerFactory.getLogger(KafkaEntityDataGeneratorBase.class);

    protected final String _topic;
    protected final long _interval;
    protected volatile List<T> _entities;
    protected final Timer _timer = new Timer();
    protected Producer<Long, String> _kafkaProducer;
    protected Gson _serializer;

    protected volatile boolean _firstRunCompleted;
    protected final Random _rand = new Random();

    private final ManualResetEvent _firstRunCompletedHandle = new ManualResetEvent(false);

    public KafkaEntityDataGeneratorBase(String topic, long interval) {
        _topic = topic;
        _interval = interval;
    }

    public void populateKafkaProducerProperties(Properties props) {
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, 1000);
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.RETRIES_CONFIG, 0);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.LongSerializer");

        props.put(ProducerConfig.LINGER_MS_CONFIG, 20);

        //Batch up to 64K buffer sizes.
        props.put(ProducerConfig.BATCH_SIZE_CONFIG,  16_384 * 4);

    }

    abstract Stream<T> generateEntities();

    abstract Long getKey(T entity);

    public List<T> getEntities()
    {
        return this._entities;
    }

    Stream<T> chooseEntitiesForPublication() {
        return this._entities.stream();
    }

    void onTimer() {

        var initialRun = false;

        List<T> listToPublish = null;

        if (_firstRunCompleted == false) {

            try {

                onInit();

                _firstRunCompleted = true;

                initialRun = true;

            } catch (Exception ex) {
                log.warn("error in onInit():" + ex.toString());
            }

            listToPublish = this._entities;
        } else {
            listToPublish = chooseEntitiesForPublication().collect(Collectors.toList());
            log.info("Publishing " + listToPublish.stream().count() +  " Items on " + _topic );
        }

        for (T entity : listToPublish) {

            var key = getKey(entity);

            var msg = this._serializer.toJson(entity);

            try {
                this._kafkaProducer.send(new ProducerRecord<Long, String>(this._topic, key, msg));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (initialRun)
            _firstRunCompletedHandle.set();



    }


    void onInit() {
        if (this._entities == null) {
            this._entities = generateEntities().collect(Collectors.toList());
        }

        if (_serializer == null) {
            GsonBuilder builder = new GsonBuilder();
            _serializer = builder.create();
        }

        if (this._kafkaProducer == null) {
            Properties props = new Properties();
            populateKafkaProducerProperties(props);
            this._kafkaProducer = new KafkaProducer<Long, String>(props);
        }
    }

    public void start(boolean blocking) throws Exception {

        if(_firstRunCompleted)
            throw  new Exception("Already Started!");

        scheduleTimer();

        if (blocking)
            this._firstRunCompletedHandle.waitOne();
    }

    private void scheduleTimer() {
        var isOneTimeTimer = _interval == 0;
        _timer.schedule(new TimerTask() {
            @Override
            public void run() {
                onTimer();

                if (!isOneTimeTimer)
                    scheduleTimer();
            }
        }, _interval);
    }

}
