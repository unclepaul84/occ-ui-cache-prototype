package com.unclepaul.uicacheprototype.datagenerators;

import com.unclepaul.uicacheprototype.entities.StockColleteralViewDTO;
import com.unclepaul.uicacheprototype.entities.StockLoanDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

public class KafkaStockColleteralViewDataGenerator extends KafkaEntityDataGeneratorBase<StockColleteralViewDTO> {

    private final List<StockLoanDTO> _stockLoans;

    public KafkaStockColleteralViewDataGenerator(String topic, List<StockLoanDTO> stockLoans) throws Exception {
        super(topic, 0);

        if(stockLoans == null || stockLoans.size() == 0)
            throw new Exception("must pass in valid stockLoans!");

        _stockLoans = stockLoans;
    }

    @Override
    Stream<StockColleteralViewDTO> generateEntities() {

        var res = new ArrayList<StockColleteralViewDTO>();

        int id = 1;

        var indexMap = new HashMap<String, StockColleteralViewDTO>();

        for (int i = 1; i < this._stockLoans.size(); i++) {

            var sl = this._stockLoans.get(i);

            var cv = indexMap.getOrDefault(sl.Borrower + sl.StockSymbol, null);

            if (cv == null) {

                cv = new  StockColleteralViewDTO();

                cv.Member = sl.Borrower;

                cv.StockSymbol = sl.StockSymbol;

                cv.QtyPledged = (int) (100000 * this._rand.nextDouble());

                cv.StockColleteralId = ++i;

                indexMap.put(cv.Member + cv.StockSymbol, cv);
            }

            cv.QtyBorrowed += sl.LoanQty;

            res.add(cv);

        }

        return res.stream();
    }

    @Override
    Long getKey(StockColleteralViewDTO entity) {
        return entity.StockColleteralId;
    }
}
