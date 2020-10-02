package com.unclepaul.uicacheprototype;

import com.unclepaul.uicacheprototype.datagenerators.KafkaPriceDataGenerator;
import com.unclepaul.uicacheprototype.datagenerators.KafkaStockColleteralViewDataGenerator;
import com.unclepaul.uicacheprototype.datagenerators.KafkaStockLoanDataGenerator;
import com.unclepaul.uicacheprototype.entities.EquityPriceDTO;
import com.unclepaul.uicacheprototype.materilizedview.EquityPriceDTOKafkaMaterializedView;
import com.unclepaul.uicacheprototype.materilizedview.StockLoanDTOKafkaMaterializedView;
import com.unclepaul.uicacheprototype.springcomponents.StockLoanService;
import com.unclepaul.uicacheprototype.utils.OperationTimer;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

import static org.slf4j.LoggerFactory.*;

@SpringBootApplication
public class ConsoleApplication  implements ApplicationRunner {
    protected static final Logger log = getLogger(ConsoleApplication.class);
    private final StockLoanService _sl;

    public static void main(String[] args) throws Exception {
        SpringApplication app = new SpringApplication(ConsoleApplication.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.run(args);

    }
    public ConsoleApplication (StockLoanService sl)
    {
        _sl = sl;
    }
    @Override
    public void run(ApplicationArguments args) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        //populateKafkaTopics();

       // test_StockLoanMaterializedView();

       // test_Price_MaterializedView();

        String name = reader.readLine();
    }

    private static void test_StockLoanMaterializedView() throws Exception {
        StockLoanDTOKafkaMaterializedView slMv = new StockLoanDTOKafkaMaterializedView(Topics.StockLoans, 199999L);

        try(var t = new OperationTimer("StockLoanDTOKafkaMaterializedView startup time")) {

            slMv.start(true);
        }
        log.info("StockLoans Total Count: " + Long.toString(slMv.getUnderlyingView().size()));
        //warmup
        for (int i = 0; i<10; i++) {
            slMv.findByStockSymbol("AAPL");
        }
        try(var t = new OperationTimer("Lookup Stockloans by stock via index")) {

            var count =  slMv.findByStockSymbol("AAPL").count();

            log.info("StockLoans for AAPL count: " + Long.toString(count));
        }

        try(var t = new OperationTimer("Lookup Stockloans by stock via iteration")) {

            var count =  slMv.getUnderlyingView().stream().filter((sl)->{return sl.StockSymbol.equals("AAPL");}).count();

            log.info("StockLoans for AAPL count: " + Long.toString(count));
        }
    }
    private static void test_Price_MaterializedView() throws Exception {
        EquityPriceDTOKafkaMaterializedView view = new EquityPriceDTOKafkaMaterializedView(Topics.Prices, 10003L);

        try(var t = new OperationTimer("EquityPriceDTOKafkaMaterializedView startup time")) {

            view.start(true);
        }

        log.info("Prices Total Count: " + Long.toString(view.getUnderlyingView().size()));

        EquityPriceDTO price = null;

        //warmup
        for (int i = 0; i<5; i++) {
            price = view.findBySymbol("AAPL");
        }

        try(var t = new OperationTimer("Lookup Price via index")) {

            price =  view.findBySymbol("AAPL");
        }
        if(price != null)
            log.warn( Double.toString(price.Price));
        else
            log.warn("No price Found!");

        try(var t = new OperationTimer("Lookup Price via iteration")) {

            var priceRes = view.getUnderlyingView().stream().filter(p-> {
                return   p.Symbol.equals("AAPL");
            }).findFirst();

            log.warn( Double.toString(priceRes.get().Price));
        }
    }
    private static void populateKafkaTopics() throws Exception {
        long startTime = System.currentTimeMillis();

        KafkaPriceDataGenerator priceGen = new KafkaPriceDataGenerator(Topics.Prices,1000, 10000, 1000);

        priceGen.start(true);

        System.out.println("Prices Started!");

        KafkaStockLoanDataGenerator stockLoanGen = new KafkaStockLoanDataGenerator(Topics.StockLoans,priceGen.getEntities(),2000000);

        stockLoanGen.start(true);

        System.out.println("StockLoan Started!");

        KafkaStockColleteralViewDataGenerator collateralGen = new KafkaStockColleteralViewDataGenerator(Topics.CollateralViews, stockLoanGen.getEntities());

        collateralGen.start(true);

        System.out.println("Fully Started! " +  (System.currentTimeMillis() - startTime) + " milliseconds");
    }


}
