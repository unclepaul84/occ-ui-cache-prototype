package com.unclepaul.uicacheprototype.springcomponents;

import com.unclepaul.uicacheprototype.Topics;
import com.unclepaul.uicacheprototype.materializedview.StockLoanDTOKafkaMaterializedView;
import com.unclepaul.uicacheprototype.utils.OperationTimer;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import static org.slf4j.LoggerFactory.getLogger;

@Component
public class StockLoanService {

    protected static final Logger log = getLogger(StockLoanService.class);

    private final StockLoanDTOKafkaMaterializedView _slMv;

    public StockLoanService() throws Exception {
        _slMv = new StockLoanDTOKafkaMaterializedView(Topics.StockLoans, 100000L);

        try(var t = new OperationTimer("StockLoanDTOKafkaMaterializedView startup time")) {

            _slMv.start(true);
        }
        log.info("StockLoans Total Count: " + Long.toString(_slMv.getUnderlyingView().size()));
    }

    public StockLoanDTOKafkaMaterializedView GetView()
    {
        return  _slMv;
    }
}