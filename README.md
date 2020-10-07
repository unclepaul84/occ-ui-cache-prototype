![Diagram](/docs/occ-materialized-view.png)

Framework prototype which demostrates the following:
 - ability of users to Sort, Filter, Page very large amounts of data using a DataGrid which supports server side operations.
 - allows users to run arbitrary indexed SQL queries against a collection of POJOs.
 
### Features
* Based on https://github.com/npgall/cqengine
* Stateless - allowing for horizontal scaling
* Embeddable into existing JVMs 
* Hydrates itself from Kakfa

