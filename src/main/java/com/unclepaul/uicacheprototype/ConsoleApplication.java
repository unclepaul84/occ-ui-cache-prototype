package com.unclepaul.uicacheprototype;

import com.unclepaul.uicacheprototype.datagenerators.KafkaPriceDataGenerator;
import com.unclepaul.uicacheprototype.datagenerators.KafkaStockColleteralViewDataGenerator;
import com.unclepaul.uicacheprototype.datagenerators.KafkaStockLoanDataGenerator;
import com.unclepaul.uicacheprototype.entities.EquityPriceDTO;
import com.unclepaul.uicacheprototype.materializedview.EquityPriceDTOKafkaMaterializedView;
import com.unclepaul.uicacheprototype.materializedview.StockLoanDTOKafkaMaterializedView;
import com.unclepaul.uicacheprototype.springcomponents.StockLoanService;
import com.unclepaul.uicacheprototype.utils.OperationTimer;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static org.slf4j.LoggerFactory.*;

@SpringBootApplication
public class ConsoleApplication  implements ApplicationRunner {
    protected static final Logger log = getLogger(ConsoleApplication.class);
    private final StockLoanService _sl;

    public static void main(String[] args) throws Exception {
        SpringApplication app = new SpringApplication(ConsoleApplication.class);
        app.setBannerMode(Banner.Mode.OFF);
        populateKafkaTopics();
        app.run(args);

    }
    public ConsoleApplication (StockLoanService sl)
    {
        _sl = sl;
    }
    @Override
    public void run(ApplicationArguments args) throws Exception {

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));


        System.out.println("Press [Enter to Quit]");
        String name = reader.readLine();
    }

    private static KafkaStockColleteralViewDataGenerator collateralGen;

    private static void populateKafkaTopics() throws Exception {
        long startTime = System.currentTimeMillis();

        KafkaPriceDataGenerator priceGen = new KafkaPriceDataGenerator(Topics.Prices,0, 10000, 1000);

        priceGen.start(true);

        System.out.println("Prices Started!");

        KafkaStockLoanDataGenerator stockLoanGen = new KafkaStockLoanDataGenerator(Topics.StockLoans,priceGen.getEntities(),2000000);

        stockLoanGen.start(true);

        System.out.println("StockLoan Started!");

        collateralGen = new KafkaStockColleteralViewDataGenerator(Topics.CollateralViews, stockLoanGen.getEntities());

        collateralGen.start(true);

        System.out.println("Fully Started! " +  (System.currentTimeMillis() - startTime) + " milliseconds");
    }
}
