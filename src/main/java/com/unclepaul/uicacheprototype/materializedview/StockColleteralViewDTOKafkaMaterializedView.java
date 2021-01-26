package com.unclepaul.uicacheprototype.materializedview;

import com.googlecode.cqengine.attribute.Attribute;
import com.googlecode.cqengine.attribute.SimpleAttribute;
import com.googlecode.cqengine.attribute.SimpleNullableAttribute;
import com.googlecode.cqengine.index.navigable.NavigableIndex;
import com.googlecode.cqengine.index.unique.UniqueIndex;
import com.googlecode.cqengine.query.option.QueryOptions;
import com.unclepaul.uicacheprototype.entities.StockColleteralViewDTO;
import com.unclepaul.uicacheprototype.entities.StockLoanDTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.stream.Stream;

import static com.googlecode.cqengine.query.QueryFactory.equal;
import static com.googlecode.cqengine.query.QueryFactory.forObjectsMissing;
import static com.unclepaul.uicacheprototype.utils.Utils.streamOf;

public class StockColleteralViewDTOKafkaMaterializedView extends KafkaMaterializedViewBase<StockColleteralViewDTO> {

    public StockColleteralViewDTOKafkaMaterializedView(String topic, Long fullSnapshotEstimate) {
        super(topic, StockColleteralViewDTO.class, fullSnapshotEstimate);
    }

    @Override
    protected void SetupIndexes() {
       //super.SetupIndexes();
       this._materializedView.addIndex(UniqueIndex.onAttribute(StockColleteralViewDTO_StockColleteralId));

        this._materializedView.addIndex(NavigableIndex.onAttribute(StockColleteralViewDTO_StockColleteralId));

        this._materializedView.addIndex(NavigableIndex.onAttribute(forObjectsMissing(StockColleteralViewDTO_Member)));
        this._materializedView.addIndex(NavigableIndex.onAttribute(StockColleteralViewDTO_Member));

        this._materializedView.addIndex(NavigableIndex.onAttribute(forObjectsMissing(StockColleteralViewDTO_StockSymbol)));
        this._materializedView.addIndex(NavigableIndex.onAttribute(StockColleteralViewDTO_StockSymbol));

        this._materializedView.addIndex(NavigableIndex.onAttribute(StockColleteralViewDTO_QtyPledged));

        this._materializedView.addIndex(NavigableIndex.onAttribute(StockColleteralViewDTO_QtyBorrowed));
        this._materializedView.addIndex(NavigableIndex.onAttribute(StockColleteralViewDTO_Price));
        this._materializedView.addIndex(NavigableIndex.onAttribute(StockColleteralViewDTO_TotalNAV));




    }



    public static final SimpleNullableAttribute<StockColleteralViewDTO, String> StockColleteralViewDTO_StockSymbol = new SimpleNullableAttribute<StockColleteralViewDTO, String>("StockSymbol") {
        public String getValue(StockColleteralViewDTO e, QueryOptions queryOptions) { return e.StockSymbol; }
    };
    public static final SimpleNullableAttribute<StockColleteralViewDTO, String> StockColleteralViewDTO_Member = new SimpleNullableAttribute<StockColleteralViewDTO, String>("Member") {
        public String getValue(StockColleteralViewDTO e, QueryOptions queryOptions) { return e.Member; }
    };

    public static final Attribute<StockColleteralViewDTO, Long> StockColleteralViewDTO_StockColleteralId= new SimpleAttribute<StockColleteralViewDTO, Long>("StockColleteralId") {
        public Long getValue(StockColleteralViewDTO e, QueryOptions queryOptions) { return e.StockColleteralId; }
    };

    public static final Attribute<StockColleteralViewDTO, Long> StockColleteralViewDTO_QtyPledged = new SimpleAttribute<StockColleteralViewDTO, Long>("QtyPledged") {
        public Long getValue(StockColleteralViewDTO e, QueryOptions queryOptions) { return e.QtyPledged; }
    };


    public static final Attribute<StockColleteralViewDTO, Long> StockColleteralViewDTO_QtyBorrowed = new SimpleAttribute<StockColleteralViewDTO, Long>("QtyBorrowed") {
        public Long getValue(StockColleteralViewDTO e, QueryOptions queryOptions) { return e.QtyBorrowed; }
    };
    public static final Attribute<StockColleteralViewDTO, Double> StockColleteralViewDTO_Price = new SimpleAttribute<StockColleteralViewDTO, Double>("Price") {
        public Double getValue(StockColleteralViewDTO e, QueryOptions queryOptions) { return e.Price; }
    };
    public static final Attribute<StockColleteralViewDTO, Double> StockColleteralViewDTO_TotalNAV = new SimpleAttribute<StockColleteralViewDTO, Double>("TotalNAV") {
        public Double getValue(StockColleteralViewDTO e, QueryOptions queryOptions) { return e.TotalNAV; }
    };


}

