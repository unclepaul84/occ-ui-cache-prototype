package com.unclepaul.uicacheprototype.materilizedview;

import com.googlecode.cqengine.ConcurrentIndexedCollection;
import com.googlecode.cqengine.IndexedCollection;

import java.util.function.Consumer;
import java.util.stream.Stream;

public abstract class KafkaMaterializedViewBase<T> {

    private final KafkaTopicEntityListener<T> _kafkaListener;
    private final Consumer<Iterable<T>> _consumer;
    protected final IndexedCollection<T> _materializedView = new ConcurrentIndexedCollection<T>();
    private final Class<T> _tClass;

    KafkaMaterializedViewBase(String topic, Class<T> tClass, Long fullSnapshotEstimate) {
        _tClass = tClass;

        _consumer = new Consumer<Iterable<T>>() {
            @Override
            public void accept(Iterable<T> batch) {
                onBatch(batch);
            }
        };
        _kafkaListener = new KafkaTopicEntityListener<T>(topic,_consumer,tClass,fullSnapshotEstimate);

    }


    public IndexedCollection<T> getUnderlyingView() {
        return _materializedView;
    }
    protected void SetupIndexes()
    {
        CQEngineUtils.autoPopulateIndexes(this._materializedView, _tClass );
    }

    protected  void onBatch(Iterable<T> batch){
        _materializedView.update(batch,batch);
    }

    public void start(Boolean waitForSnapshot) throws Exception {
        SetupIndexes();

        _kafkaListener.start();

        if(waitForSnapshot)
            _kafkaListener.getSnapshotReceivedWaiter().waitOne();
    }
}
