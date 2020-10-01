package com.unclepaul.uicacheprototype.datagenerators;

import com.unclepaul.uicacheprototype.entities.EquityPriceDTO;
import com.unclepaul.uicacheprototype.utils.Utils;

import java.util.ArrayList;
import java.util.stream.Stream;

public class KafkaPriceDataGenerator  extends KafkaEntityDataGeneratorBase<EquityPriceDTO> {

    final static String[] stocks = {"AAPL", "GOOG", "MSFT"};
    private final int _countPriceRecords;
    private final int _countPerUpdatePriceRecords;

    public KafkaPriceDataGenerator(String topic, long interval, int countTotalPriceRecords, int countPerUpdatePriceRecords) {
        super(topic, interval);
        this._countPriceRecords = countTotalPriceRecords;
        this._countPerUpdatePriceRecords = countPerUpdatePriceRecords;
    }


    @Override
    Stream<EquityPriceDTO> generateEntities() {
        var res = new ArrayList<EquityPriceDTO>();

        for (int i = 1; i < this._countPriceRecords; i++) {

            var dto = new EquityPriceDTO();

            dto.EquityId=i;
            dto.Symbol = getRandoomStock() + Integer.toString(i);
            dto.Price = 100.0 * this._rand.nextDouble();

            res.add(dto);
        }
        for (var stock :
                stocks) {

            var dto = new EquityPriceDTO();

            dto.EquityId=res.size()+1;
            dto.Symbol = stock;
            dto.Price = 100.0 * this._rand.nextDouble();

            res.add(dto);
        }
        return res.stream();
    }

    private String getRandoomStock()
    {
        return stocks[_rand.nextInt(stocks.length-1)];
    }

    @Override
    Stream<EquityPriceDTO> chooseEntitiesForPublication() {
        return Utils.pickRandom(this._entities, this._countPerUpdatePriceRecords).map((e)-> {
            e.Price= 100.0*this._rand.nextDouble();
            return e;
        });

    }

    @Override
    Long getKey(EquityPriceDTO entity) {
        return entity.EquityId;
    }
}
