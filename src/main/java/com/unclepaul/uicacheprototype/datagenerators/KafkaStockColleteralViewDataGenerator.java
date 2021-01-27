package com.unclepaul.uicacheprototype.datagenerators;

import com.unclepaul.uicacheprototype.entities.StockColleteralViewDTO;
import com.unclepaul.uicacheprototype.entities.StockLoanDTO;
import com.unclepaul.uicacheprototype.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import java.time.LocalDateTime;

public class KafkaStockColleteralViewDataGenerator extends KafkaEntityDataGeneratorBase<StockColleteralViewDTO> {

    private final List<StockLoanDTO> _stockLoans;
    private final List<StockColleteralViewDTO> _specialListToUpdate = new ArrayList<>();

    public KafkaStockColleteralViewDataGenerator(String topic, List<StockLoanDTO> stockLoans) throws Exception {
        super(topic, 1000);

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

                cv.StockColleteralId = ++i;

                indexMap.put(cv.Member + cv.StockSymbol, cv);
            }

            if(cv.StockSymbol == "AAPL")
                _specialListToUpdate.add(cv);

            cv.QtyBorrowed += sl.LoanQty;
            cv.Price = this._rand.nextDouble() * 100.0;
            cv.QtyPledged = (int) (100000 * this._rand.nextDouble());
            cv.TotalNAV = cv.Price * cv.QtyPledged;
            cv.Timestamp = LocalDateTime.now();
            res.add(cv);

        }

        return res.stream();
    }


    @Override
    Stream<StockColleteralViewDTO> chooseEntitiesForPublication() {
        var price = 100.0*this._rand.nextDouble();
        price =  Math.round(price * 100.0) / 100.0;
        for (var v: _specialListToUpdate) {
            v.Price= price;
            v.TotalNAV = Math.round(v.Price * v.QtyPledged * 100.0) / 100.0;
            v.Timestamp = LocalDateTime.now();
        }

        return _specialListToUpdate.stream();
    }

    @Override
    Long getKey(StockColleteralViewDTO entity) {
        return entity.StockColleteralId;
    }
}
