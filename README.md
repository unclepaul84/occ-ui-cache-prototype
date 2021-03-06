![Diagram](/docs/occ-materialized-view.png)

Framework prototype which demostrates the following:
 - ability of users to Sort, Filter, Page very large amounts of data using any HTML DataGrid which supports server side operations.
 - ability of users to run arbitrary, index accelerated SQL queries against a collection of POJOs.
 
### Features
* Based on https://github.com/npgall/cqengine 
* In-Memory Processing - does not touch disk
* Stateless - allowing for horizontal scaling
* Embeddable into existing JVMs 
* Hydrates itself from Kafka (or any other persistent store)


### DEMO
[Screen Cast](https://github.com/unclepaul84/occ-ui-cache-prototype/raw/main/docs/grid.gif)


### Code Example
```java
@Controller
public class StockLoanDataProviderController {
      
      //----SNIP----
      
    @RequestMapping(value = "/customQuery", method = POST)
    @ResponseBody
    public Iterator<StockLoanDTO> customQuery(@RequestBody CustomQuery q) throws Exception {

        System.out.println(q.queryText); // SELECT * FROM t WHERE Lender LIKE 'Gold%' ORDER BY LoanQty DESC

        return streamOf(_sl.GetView().executeSQL(q.queryText)).limit(q.limit.orElse(10000)).iterator();

    } 
}
```
   
