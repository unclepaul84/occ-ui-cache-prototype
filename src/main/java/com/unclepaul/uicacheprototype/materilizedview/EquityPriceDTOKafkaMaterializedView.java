package com.unclepaul.uicacheprototype.materilizedview;

import com.googlecode.cqengine.attribute.Attribute;
import com.googlecode.cqengine.attribute.SimpleAttribute;
import com.googlecode.cqengine.index.hash.HashIndex;
import com.googlecode.cqengine.query.Query;
import com.googlecode.cqengine.query.option.QueryOptions;
import com.unclepaul.uicacheprototype.entities.EquityPriceDTO;

import static com.googlecode.cqengine.query.QueryFactory.*;

public class EquityPriceDTOKafkaMaterializedView extends KafkaMaterializedViewBase<EquityPriceDTO> {

    public EquityPriceDTOKafkaMaterializedView(String topic, Long fullSnapshotEstimate) {
        super(topic,EquityPriceDTO.class,fullSnapshotEstimate);
    }

    @Override
    protected void SetupIndexes() {

        this._materializedView.addIndex(HashIndex.onAttribute(EquityPriceDTO_Symbol));
    }

    public EquityPriceDTO findBySymbol(String symbol)
    {
        Query<EquityPriceDTO> q = equal(EquityPriceDTO_Symbol, symbol);

        var res = this._materializedView.retrieve(q);

        if(res.isEmpty())
            return  null;
        else
        {
           return res.uniqueResult();
        }

    }
    public static final Attribute<EquityPriceDTO, String> EquityPriceDTO_Symbol = new SimpleAttribute<EquityPriceDTO, String>("Symbol") {
        public String getValue(EquityPriceDTO e, QueryOptions queryOptions) { return e.Symbol; }
    };

}
