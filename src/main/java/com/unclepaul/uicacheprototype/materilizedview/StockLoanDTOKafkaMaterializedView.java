package com.unclepaul.uicacheprototype.materilizedview;

import com.googlecode.cqengine.attribute.Attribute;
import com.googlecode.cqengine.attribute.SimpleAttribute;
import com.googlecode.cqengine.index.hash.HashIndex;
import com.googlecode.cqengine.query.Query;
import com.googlecode.cqengine.query.option.QueryOptions;
import com.unclepaul.uicacheprototype.entities.EquityPriceDTO;
import com.unclepaul.uicacheprototype.entities.StockLoanDTO;

import java.util.stream.Stream;

import static com.googlecode.cqengine.query.QueryFactory.equal;

public class StockLoanDTOKafkaMaterializedView extends KafkaMaterializedViewBase<StockLoanDTO> {

    public StockLoanDTOKafkaMaterializedView(String topic, Long fullSnapshotEstimate) {
        super(topic, StockLoanDTO.class, fullSnapshotEstimate);
    }

    @Override
    void SetupIndexes() {
        this._materializedView.addIndex(HashIndex.onAttribute(StockLoanDTO_StockSymbol));
    }


    public Stream<StockLoanDTO> findByStockSymbol(String stockSymbol)
    {
        Query<StockLoanDTO> q = equal(StockLoanDTO_StockSymbol, stockSymbol);

        var res = this._materializedView.retrieve(q);

        if(res.isEmpty())
            return  null;
        else
        {
            return res.stream();
        }
    }

    public static final Attribute<StockLoanDTO, String> StockLoanDTO_StockSymbol = new SimpleAttribute<StockLoanDTO, String>("StockSymbol") {
        public String getValue(StockLoanDTO e, QueryOptions queryOptions) { return e.StockSymbol; }
    };
}

