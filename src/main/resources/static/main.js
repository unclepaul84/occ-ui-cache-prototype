var columnDefs = [

        { headerName: 'StockLoanId', field: 'StockLoanId', type: 'number' },
        { headerName: 'StockSymbol', field: 'StockSymbol' ,   type: 'text' },
        { headerName: 'Borrower', field: 'Borrower',   type: 'text'  } ,
        { headerName: 'Lender', field: 'Lender',  type: 'text'  },
        { headerName: 'LoanQty', field: 'LoanQty' ,type: 'number' }
];

let gridOptions = {
  columnTypes: {
    dimension: {
      enableRowGroup: false,
      enablePivot: false,
    }
  },


  columnDefs: columnDefs,

  defaultColDef: {
      flex: 1,
      minWidth: 150,
      sortable: true,
      resizable: true,
    },
 columnTypes: {
    number: { filter: 'agNumberColumnFilter', filterParams: {
        buttons: ['reset'],
        debounceMs: 1000,
        suppressAndOrCondition: true,
        filterOptions: ['equals','notEqual','lessThan', 'greaterThan']
        } },
    text: { filter: 'agTextColumnFilter', filterParams: {
                buttons: ['reset'],
                debounceMs: 200,
                suppressAndOrCondition: true,
                filterOptions: ['startsWith','contains','equals']
                } }
  },



  rowModelType: 'serverSide',
  cacheBlockSize: 1000,
  suppressAggFuncInHeader: true,
  suppressMultiSort: true,
  animateRows: false,
  paginationAutoPageSize: false,
  paginationPageSize: 1000,
  pagination: true,

};

function EnterpriseDatasource() {}

EnterpriseDatasource.prototype.getRows = function (params) {
  let jsonRequest = JSON.stringify(params.request, null, 2);
  console.log(jsonRequest);

  let httpRequest = new XMLHttpRequest();
  httpRequest.open('POST', 'http://localhost:8080/getRows');
  httpRequest.setRequestHeader("Content-type", "application/json");
  httpRequest.send(jsonRequest);
  httpRequest.onreadystatechange = () => {
    if (httpRequest.readyState === 4 && httpRequest.status === 200) {
      let result = JSON.parse(httpRequest.responseText);
      params.successCallback(result.data, result.lastRow);
    }
  };
};

// setup the grid after the page has finished loading
document.addEventListener('DOMContentLoaded', function () {
  let gridDiv = document.querySelector('#myGrid');
  new agGrid.Grid(gridDiv, gridOptions);
  gridOptions.api.setServerSideDatasource(new EnterpriseDatasource());
});

function numberCellFormatter(params) {
  let formattedNumber = Math.floor(Math.abs(params.value)).toString().replace(/(\d)(?=(\d{3})+(?!\d))/g, "$1,");
  return params.value < 0 ? '(' + formattedNumber + ')' : formattedNumber;
}