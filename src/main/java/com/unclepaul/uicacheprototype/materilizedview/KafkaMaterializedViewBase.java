package com.unclepaul.uicacheprototype.materilizedview;

import com.googlecode.cqengine.ConcurrentIndexedCollection;
import com.googlecode.cqengine.IndexedCollection;
import com.googlecode.cqengine.query.Query;
import com.googlecode.cqengine.query.option.QueryLog;
import com.googlecode.cqengine.query.option.QueryOptions;
import com.googlecode.cqengine.query.parser.sql.SQLParser;
import com.googlecode.cqengine.resultset.ResultSet;
import com.unclepaul.uicacheprototype.entities.StockLoanDTO;
import com.unclepaul.uicacheprototype.utils.OperationTimer;

import java.io.StringWriter;
import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static com.googlecode.cqengine.codegen.AttributeBytecodeGenerator.createAttributes;
import static com.googlecode.cqengine.query.QueryFactory.*;
import static com.googlecode.cqengine.query.option.EngineThresholds.INDEX_ORDERING_SELECTIVITY;

public abstract class KafkaMaterializedViewBase<T> {

    private final KafkaTopicEntityListener<T> _kafkaListener;
    private final Consumer<Collection<T>> _consumer;
    protected final IndexedCollection<T> _materializedView = new ConcurrentIndexedCollection<T>();
    private final Class<T> _tClass;
    protected final SQLParser<T>  _sqlParser;

KafkaMaterializedViewBase(String topic, Class<T> tClass, Long fullSnapshotEstimate) {
        _tClass = tClass;

        _consumer = new Consumer<Collection<T>>() {
            @Override
            public void accept(Collection<T> batch) {
                onBatch(batch);
            }
        };

        _kafkaListener = new KafkaTopicEntityListener<T>(topic,_consumer,tClass,fullSnapshotEstimate);

        _sqlParser = SQLParser.forPojoWithAttributes(tClass, createAttributes(tClass));

    }

    public ResultSet<T> executeSQL(String sql) throws Exception {
        var query = _sqlParser.query(sql);

        var options = _sqlParser.queryOptions(sql);

        var  queryOptions = queryOptions(applyThresholds(threshold(INDEX_ORDERING_SELECTIVITY, 1.0)));

        for (Map.Entry<Object, Object> e : options.getOptions().entrySet()) {

            queryOptions.put(e.getKey(), e.getValue());
        }

        StringWriter sr = new StringWriter();

        var queryLog = new QueryLog(sr);

        queryOptions.put(QueryLog.class, queryLog);

        ResultSet<T>  res = null;
        try(var t = new OperationTimer("_materializedView.retrieve")) {

            res = this._materializedView.retrieve(query, queryOptions);

        }
        System.out.println("Query Log: " + sr.toString());

        return res;
    }

    public IndexedCollection<T> getUnderlyingView() {
        return _materializedView;
    }

    protected void SetupIndexes()
    {
        CQEngineUtils.autoPopulateIndexes(this._materializedView, _tClass );
    }

    protected  void onBatch(Collection<T> batch){
        _materializedView.update(batch,batch);
    }

    public void start(Boolean waitForSnapshot) throws Exception {
        SetupIndexes();

        _kafkaListener.start();

        if(waitForSnapshot)
            _kafkaListener.getSnapshotReceivedWaiter().waitOne();
    }
}
