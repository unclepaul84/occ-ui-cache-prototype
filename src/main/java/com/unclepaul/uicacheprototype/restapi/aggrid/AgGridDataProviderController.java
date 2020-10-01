package com.unclepaul.uicacheprototype.restapi.aggrid;

        import java.util.concurrent.atomic.AtomicLong;

        import com.unclepaul.uicacheprototype.restapi.aggrid.request.EnterpriseGetRowsRequest;
        import com.unclepaul.uicacheprototype.restapi.aggrid.response.EnterpriseGetRowsResponse;
        import org.springframework.stereotype.Controller;
        import org.springframework.web.bind.annotation.*;

        import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
public class AgGridDataProviderController {

    @RequestMapping(method = POST, value = "/getRows")
    @ResponseBody
    public EnterpriseGetRowsResponse getRows(@RequestBody EnterpriseGetRowsRequest request) {
        return new EnterpriseGetRowsResponse();
    }
}
