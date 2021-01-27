package com.unclepaul.uicacheprototype.springcomponents;

import com.unclepaul.uicacheprototype.Topics;
import com.unclepaul.uicacheprototype.materializedview.StockColleteralViewDTOKafkaMaterializedView;
import com.unclepaul.uicacheprototype.materializedview.StockLoanDTOKafkaMaterializedView;
import com.unclepaul.uicacheprototype.utils.OperationTimer;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import static org.slf4j.LoggerFactory.getLogger;

@Component
public class StockColleteralService {

    protected static final Logger log = getLogger(StockColleteralService.class);

    private final StockColleteralViewDTOKafkaMaterializedView _slMv;

    public StockColleteralService() throws Exception {
        _slMv = new StockColleteralViewDTOKafkaMaterializedView(Topics.CollateralViews, 100000L);

        try(var t = new OperationTimer("StockColleteralViewDTOKafkaMaterializedView startup time")) {

            _slMv.start(true);
        }
        log.info("Stock Colleteral Views Total Count: " + Long.toString(_slMv.getUnderlyingView().size()));
    }

    public StockColleteralViewDTOKafkaMaterializedView GetView()
    {
        return  _slMv;
    }
}