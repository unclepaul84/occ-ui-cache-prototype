package com.unclepaul.uicacheprototype.restapi.aggrid;


import com.googlecode.cqengine.resultset.ResultSet;
import com.unclepaul.uicacheprototype.entities.StockColleteralViewDTO;
import com.unclepaul.uicacheprototype.entities.StockLoanDTO;
import com.unclepaul.uicacheprototype.restapi.aggrid.request.EnterpriseGetRowsRequest;
import com.unclepaul.uicacheprototype.restapi.aggrid.response.EnterpriseGetRowsResponse;
import com.unclepaul.uicacheprototype.springcomponents.StockColleteralService;
import com.unclepaul.uicacheprototype.springcomponents.StockLoanService;
import com.unclepaul.uicacheprototype.utils.OperationTimer;
import org.slf4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.Serializable;
import java.net.URLDecoder;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.unclepaul.uicacheprototype.restapi.aggrid.Utils.orderBySql;
import static com.unclepaul.uicacheprototype.restapi.aggrid.Utils.whereSql;
import static com.unclepaul.uicacheprototype.utils.Utils.streamOf;
import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.web.bind.annotation.RequestMethod.POST;


@Controller
@RequestMapping("sc")
public class StockColleteralDataProviderController {
    protected static final Logger log = getLogger(StockColleteralDataProviderController.class);

    private final StockColleteralService _sl;

    public StockColleteralDataProviderController(StockColleteralService sl) {
        _sl = sl;
    }

    @RequestMapping(method = POST, value = "/getRows")
    @ResponseBody
    public EnterpriseGetRowsResponse getRows(@RequestBody EnterpriseGetRowsRequest request) throws Exception {

        var queryStr = "SELECT * FROM tbl " + whereSql(request.getFilterModel()) + " " + orderBySql(request.getSortModel());

        List<Map<String, Object>> rows = null;
        Stream<StockColleteralViewDTO> str = null;
        ResultSet<StockColleteralViewDTO> res = null;

        System.out.println(queryStr);

        try (var t = new OperationTimer("Get Rows Exec Time" + "startrow: " + request.getStartRow() + " endrow: " + request.getEndRow())) {

            res = _sl.GetView().executeSQL(queryStr);

            try (var t1 = new OperationTimer("Get Rows Exec - GetStream Time")) {
                str = streamOf(res);
            }

            rows = str.skip(request.getStartRow()).limit(request.getEndRow() - request.getStartRow()).map((e) -> {

                Map<String, Object> r = new HashMap<String, Object>();
                r.put("StockColleteralId", e.StockColleteralId);
                r.put("StockSymbol", e.StockSymbol);
                r.put("Member", e.Member);
                r.put("QtyBorrowed", e.QtyBorrowed);
                r.put("QtyPledged", e.QtyPledged);
                r.put("Price", e.Price);
                r.put("TotalNAV", e.TotalNAV);
                r.put("Timestamp", e.Timestamp);

                return r;
            }).collect(Collectors.toList());

        }

        int lastRow = res.size();

        return new EnterpriseGetRowsResponse(rows, lastRow, null);
    }



}

