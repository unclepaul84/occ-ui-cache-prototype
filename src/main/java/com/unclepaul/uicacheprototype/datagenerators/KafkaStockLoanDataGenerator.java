package com.unclepaul.uicacheprototype.datagenerators;


import com.unclepaul.uicacheprototype.entities.EquityPriceDTO;
import com.unclepaul.uicacheprototype.entities.StockLoanDTO;
import com.unclepaul.uicacheprototype.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class KafkaStockLoanDataGenerator  extends KafkaEntityDataGeneratorBase<StockLoanDTO > {

    public final  String[] members =  {"ADM Investor Services, Inc.","Goldman Sachs & Co. LLC","X-Change Financial Access, LLC","ABN AMRO Clearing Chicago LLC","Sanford C. Bernstein & Co., LLC","Morgan Stanley Smith Barney LLC","Interactive Brokers LLC","Jefferies LLC","Canaccord Genuity Corp.","Natixis Securities Americas LLC","Jump Trading, LLC","RBC Capital Markets, LLC","Mirae Asset Securities (USA), Inc.","BMO Capital Markets Corp.","Morgan Stanley & Co. LLC","Axos Clearing LLC","BNP Paribas Securities Corp.","Vanguard Marketing Corporation","Instinet, LLC","Electronic Transaction Clearing, Inc. ","LPL Financial LLC"," MUFG Securities Americas Inc. ","Advantage Futures LLC","National Bank Financial Inc.","R.J. O'Brien & Associates, LLC","Questrade Inc.","Mizuho Securities USA LLC","Deutsche Bank Securities, Inc.","Virtu Americas, LLC","Wedbush Securities Inc.","Apex Clearing Corporation","Ingalls & Snyder LLC","Clear Street LLC","Wells Fargo Clearing Services, LLC","ICAP Corporates LLC","Apex Clearing Corporation ","INTL FCStone Financial Inc.","BofA Securities, Inc.","Charles Schwab & Co., Inc.","Phillip Capital Inc.","Nomura Securities International, In","Nomura Securities International, Inc.","Scotia Capital Inc.","RBC Dominion Securities Inc.","BMO Nesbitt Burns, Inc.","Scotia Capital (USA) Inc.","UBS Financial Services Inc.","National Financial Services LLC","BBS Securities Inc.","Lek Securities Corporation","Volant Execution, LLC","Wells Fargo Securities, LLC","Barclays Capital Inc.","BofA Securiites, Inc.","ING Financial Markets LLC ","TradeStation Securities, Inc.","Futu Clearing Inc.","Hilltop Securities Inc.","TD Prime Services LLC","SG Americas Securities, LLC","Credit Suisse Securities (USA) LLC","Virtu Americas LLC","HSBC Securities (USA) Inc.","Dash Financial Technologies LLC","INTL FCStone Financial Inc. ","J.P. Morgan Securities LLC","National Bank of Canada Financial Inc.","Curvature Securities LLC","E D & F Man Capital Markets Inc.","Wolverine Execution Services, LLC","Merrill Lynch, Pierce, Fenner & Smith Inc.","Tradition Securities and Derivatives Inc.","Janney Montgomery Scott LLC","E*TRADE Securities LLC","Industrial and Commercial Bank of China Financial Services LLC","Citadel Clearing LLC ","Straits Financial LLC","Citigroup Global Markets, Inc.","Amherst Pierpont Securities LLC","Citigroup Global Markets Inc.","Velocity Capital LLC","NatWest Markets Securities Inc. ","Citadel Securities LLC","Archipelago Securities, L.L.C.","CIBC World Markets Corp. ","Pershing LLC ","Muriel Siebert & Co., Inc.","Safra Securities LLC","Timber Hill LLC","CIBC World Markets Inc.","Ziv Investment Company ","Merrill Lynch Professional Clearing Corp.","Nasdaq Execution Services, LLC ","Broadridge Business Process Outsourcing, LLC","Robert W. Baird & Co. Incorporated","Oppenheimer & Co. Inc.","Deutsche Bank Securities Inc.","Cowen and Company, LLC","Vision Financial Markets LLC","SG Americas Securities, LLC ","TD Waterhouse Canada Inc.","UBS Securities LLC","Interactive Brokers LLC ","Daiwa Capital Markets America Inc. ","Lakeshore Securities, L.P. ","CF Secured, LLC ","Cantor Fitzgerald & Co.","BB&T Securities, LLC","Davenport & Company LLC","Raymond James & Associates, Inc.","American Enterprise Investment Services, Inc. ","Robinhood Securities, LLC","TD Ameritrade Clearing, Inc.","Stifel, Nicolaus & Company, Incorporated","B. Riley FBR Inc.","HSBC Securities (USA) Inc. "};
    private final List<EquityPriceDTO> _prices;
    private final int _numLoans;

    public KafkaStockLoanDataGenerator(String topic,  List<EquityPriceDTO> prices, int numLoans) throws Exception {
        super(topic, 0);

        if(prices == null || prices.size() == 0)
            throw new Exception("must pass in valid prices!");

        this._prices = prices;

        _numLoans = numLoans;
    }

    @Override
    Stream<StockLoanDTO> generateEntities() {

        var res = new ArrayList<StockLoanDTO>();

        for (int i = 1; i < this._numLoans; i++) {

            var sl = new StockLoanDTO();

            sl.StockLoanId = i;
            sl.Borrower= pickRandomMember();
            sl.Lender = pickRandomMember();

            while (sl.Borrower == sl.Lender)
                sl.Lender = pickRandomMember();

            sl.StockSymbol = pickRandomPrice().Symbol;
            sl.LoanQty = (int)(500 * 1000 * this._rand.nextDouble());

            res.add(sl);

        }

        return res.stream();
    }

    private EquityPriceDTO pickRandomPrice()
    {
        return Utils.pickRandom(this._prices,1).findFirst().get();

    }

    private String pickRandomMember()
    {
       return members[ this._rand.nextInt(members.length-1)];
    }


    @Override
    Long getKey(StockLoanDTO entity) {
        return entity.StockLoanId;
    }
}
