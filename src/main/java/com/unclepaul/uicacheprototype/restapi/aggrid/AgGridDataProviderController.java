package com.unclepaul.uicacheprototype.restapi.aggrid;


        import java.io.Serializable;
        import java.net.URLDecoder;
        import java.util.*;
        import java.util.function.Function;
        import java.util.stream.Collectors;
        import java.util.stream.Stream;
        import java.util.stream.StreamSupport;

        import com.googlecode.cqengine.resultset.ResultSet;
        import com.unclepaul.uicacheprototype.entities.StockLoanDTO;
        import com.unclepaul.uicacheprototype.restapi.aggrid.filter.ColumnFilter;
        import com.unclepaul.uicacheprototype.restapi.aggrid.filter.NumberColumnFilter;
        import com.unclepaul.uicacheprototype.restapi.aggrid.filter.TextColumnFilter;
        import com.unclepaul.uicacheprototype.restapi.aggrid.request.EnterpriseGetRowsRequest;
        import com.unclepaul.uicacheprototype.restapi.aggrid.request.SortModel;
        import com.unclepaul.uicacheprototype.restapi.aggrid.response.EnterpriseGetRowsResponse;
        import com.unclepaul.uicacheprototype.springcomponents.StockLoanService;
        import com.unclepaul.uicacheprototype.utils.OperationTimer;
        import org.slf4j.Logger;
        import org.springframework.http.HttpHeaders;
        import org.springframework.http.HttpStatus;
        import org.springframework.http.MediaType;
        import org.springframework.http.ResponseEntity;
        import org.springframework.stereotype.Controller;
        import org.springframework.web.bind.annotation.*;

        import static com.unclepaul.uicacheprototype.restapi.aggrid.Utils.orderBySql;
        import static com.unclepaul.uicacheprototype.restapi.aggrid.Utils.whereSql;
        import static com.unclepaul.uicacheprototype.utils.Utils.streamOf;
        import static java.lang.String.join;
        import static java.util.stream.Collectors.toList;
        import static org.slf4j.LoggerFactory.getLogger;
        import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
public class AgGridDataProviderController {
    protected static final Logger log = getLogger(AgGridDataProviderController.class);

    private final StockLoanService _sl;

    public AgGridDataProviderController(StockLoanService sl)
    {
        _sl = sl;
    }

    @RequestMapping(method = POST, value = "/getRows")
    @ResponseBody
    public EnterpriseGetRowsResponse getRows(@RequestBody EnterpriseGetRowsRequest request) throws Exception {

        var queryStr = "SELECT * FROM tbl " + whereSql(request.getFilterModel()) + " " + orderBySql(request.getSortModel());

            List<Map<String, Object>> rows = null;
            Stream<StockLoanDTO> str = null;
            ResultSet<StockLoanDTO> res = null;

            System.out.println(queryStr);

            try(var t = new OperationTimer("Get Rows Exec Time" + "startrow: "+ request.getStartRow() + " endrow: "+ request.getEndRow())) {

                res = _sl.GetView().executeSQL(queryStr);

                try(var t1 = new OperationTimer("Get Rows Exec - GetStream Time")) {
                    str = streamOf(res);
                }

                rows = str.skip(request.getStartRow()).limit(request.getEndRow() - request.getStartRow()).map((e) -> {

                    Map<String, Object> r = new HashMap<String, Object>();
                    r.put("StockLoanId", e.StockLoanId);
                    r.put("StockSymbol", e.StockSymbol);
                    r.put("Lender", e.Lender);
                    r.put("Borrower", e.Borrower);
                    r.put("LoanQty", e.LoanQty);
                    return r;
                }).collect(Collectors.toList());

            }

            int currentLastRow = res.size();

            int lastRow =currentLastRow;

            /*if( request.getEndRow() > currentLastRow)
                lastRow = currentLastRow;
            else
                lastRow =currentLastRow;*/

            return new EnterpriseGetRowsResponse(rows, lastRow, null);
    }

    @RequestMapping(value = "/download", method = RequestMethod.GET)
    public ResponseEntity<byte[]> getDownloadData(String query, Optional<Integer> limit) throws Exception {

     if(query==null)
         query = "SELECT * FROM tbl";

        query = URLDecoder.decode(query);

        StringBuilder builder = new StringBuilder();

        System.out.println(query);

        var res = streamOf(_sl.GetView().executeSQL(query)).limit(limit.orElse(10000)) ;

       builder.append("StockLoanId,StockSymbol,Lender,Borrower,LoanQty").append(System.getProperty("line.separator"));

         res.forEach((sl)->{

             builder.append( sl.StockLoanId + "," + sl.StockSymbol + ",'" + sl.Lender+ "','" + sl.Borrower + "'," + sl.LoanQty).append(System.getProperty("line.separator"));
         });


        byte[] output = builder.toString().getBytes();

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("charset", "utf-8");
        responseHeaders.setContentType(MediaType.valueOf("text/csv"));
        responseHeaders.setContentLength(output.length);
        responseHeaders.set("Content-disposition", "attachment; filename=stockloans.csv");

        return new ResponseEntity<byte[]>(output, responseHeaders, HttpStatus.OK);
    }


    @RequestMapping(value = "/customQuery", method = POST)
    @ResponseBody
    public Iterator<StockLoanDTO> customQuery(@RequestBody CustomQuery q) throws Exception {

        System.out.println(q.queryText);

       return streamOf(_sl.GetView().executeSQL(q.queryText)).limit(q.limit.orElse(10000)).iterator();

    }

}
 class CustomQuery implements Serializable
{
    public CustomQuery(String queryText,Optional<Integer> limit){

        this.queryText=queryText;

        this.limit=limit;
    }

    public String queryText;
    public Optional<Integer> limit;


}