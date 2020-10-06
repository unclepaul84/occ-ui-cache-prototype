package com.unclepaul.uicacheprototype.materializedview;

import com.googlecode.cqengine.attribute.Attribute;
import com.googlecode.cqengine.attribute.SimpleAttribute;
import com.googlecode.cqengine.attribute.SimpleNullableAttribute;
import com.googlecode.cqengine.index.navigable.NavigableIndex;
import com.googlecode.cqengine.index.unique.UniqueIndex;
import com.googlecode.cqengine.query.Query;
import com.googlecode.cqengine.query.option.QueryOptions;
import com.unclepaul.uicacheprototype.entities.StockLoanDTO;

import java.util.stream.Stream;

import static com.googlecode.cqengine.query.QueryFactory.equal;
import static com.googlecode.cqengine.query.QueryFactory.forObjectsMissing;
import static com.unclepaul.uicacheprototype.utils.Utils.streamOf;

public class StockLoanDTOKafkaMaterializedView extends KafkaMaterializedViewBase<StockLoanDTO> {

    public StockLoanDTOKafkaMaterializedView(String topic, Long fullSnapshotEstimate) {
        super(topic, StockLoanDTO.class, fullSnapshotEstimate);
    }

    @Override
    protected void SetupIndexes() {
       //super.SetupIndexes();
        this._materializedView.addIndex(UniqueIndex.onAttribute(StockLoanDTO_StockLoanId));

        this._materializedView.addIndex(NavigableIndex.onAttribute(StockLoanDTO_StockLoanId));

        this._materializedView.addIndex(NavigableIndex.onAttribute(StockLoanDTO_Qty));

        this._materializedView.addIndex(NavigableIndex.onAttribute(forObjectsMissing(StockLoanDTO_Lender)));
        this._materializedView.addIndex(NavigableIndex.onAttribute(StockLoanDTO_Lender));

        this._materializedView.addIndex(NavigableIndex.onAttribute(forObjectsMissing(StockLoanDTO_Borrower)));
        this._materializedView.addIndex(NavigableIndex.onAttribute(StockLoanDTO_Borrower));

        this._materializedView.addIndex(NavigableIndex.onAttribute(forObjectsMissing(StockLoanDTO_StockSymbol)));
        this._materializedView.addIndex(NavigableIndex.onAttribute(StockLoanDTO_StockSymbol));

    }



    public Stream<StockLoanDTO> findByStockSymbol(String stockSymbol)
    {
        return streamOf(this._materializedView.retrieve(equal(StockLoanDTO_StockSymbol, stockSymbol)));
    }

    public static final SimpleNullableAttribute<StockLoanDTO, String> StockLoanDTO_StockSymbol = new SimpleNullableAttribute<StockLoanDTO, String>("StockSymbol") {
        public String getValue(StockLoanDTO e, QueryOptions queryOptions) { return e.StockSymbol; }
    };
    public static final SimpleNullableAttribute<StockLoanDTO, String> StockLoanDTO_Lender = new SimpleNullableAttribute<StockLoanDTO, String>("Lender") {
        public String getValue(StockLoanDTO e, QueryOptions queryOptions) { return e.Lender; }
    };
    public static final SimpleNullableAttribute<StockLoanDTO, String> StockLoanDTO_Borrower = new SimpleNullableAttribute<StockLoanDTO, String>("Borrower") {
        public String getValue(StockLoanDTO e, QueryOptions queryOptions) { return e.Borrower; }
    };
    public static final Attribute<StockLoanDTO, Long> StockLoanDTO_StockLoanId = new SimpleAttribute<StockLoanDTO, Long>("StockLoanId") {
        public Long getValue(StockLoanDTO e, QueryOptions queryOptions) { return e.StockLoanId; }
    };
    public static final Attribute<StockLoanDTO, Integer> StockLoanDTO_Qty = new SimpleAttribute<StockLoanDTO, Integer>("LoanQty") {
        public Integer getValue(StockLoanDTO e, QueryOptions queryOptions) { return e.LoanQty; }
    };
}

