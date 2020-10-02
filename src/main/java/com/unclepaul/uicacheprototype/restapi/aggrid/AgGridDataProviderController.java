package com.unclepaul.uicacheprototype.restapi.aggrid;

        import java.util.ArrayList;
        import java.util.List;
        import java.util.Map;
        import java.util.concurrent.atomic.AtomicLong;

        import com.unclepaul.uicacheprototype.restapi.aggrid.request.ColumnVO;
        import com.unclepaul.uicacheprototype.restapi.aggrid.request.EnterpriseGetRowsRequest;
        import com.unclepaul.uicacheprototype.restapi.aggrid.response.EnterpriseGetRowsResponse;
        import com.unclepaul.uicacheprototype.springcomponents.StockLoanService;
        import org.springframework.stereotype.Controller;
        import org.springframework.web.bind.annotation.*;

        import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
public class AgGridDataProviderController {

    private final StockLoanService _sl;

    public AgGridDataProviderController(StockLoanService sl)
    {
        _sl = sl;
    }

    @RequestMapping(method = POST, value = "/getRows")
    @ResponseBody
    public EnterpriseGetRowsResponse getRows(@RequestBody EnterpriseGetRowsRequest request) {

            List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();




            int currentLastRow = request.getStartRow() + rows.size();

            int lastRow = currentLastRow <= request.getEndRow() ? currentLastRow : -1;

            return new EnterpriseGetRowsResponse(rows, lastRow, null);


    }
}
