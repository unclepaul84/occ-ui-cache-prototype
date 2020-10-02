var columnDefs = [

        { headerName: 'StockLoanId', field: 'StockLoanId' },
        { headerName: 'StockSymbol', field: 'StockSymbol' },
        { headerName: 'Lender', field: 'Borrower' },
        { headerName: 'LoanQty', field: 'LoanQty' }
];

let gridOptions = {
  columnTypes: {
    dimension: {
      enableRowGroup: false,
      enablePivot: false,
    }
  },

  enableSorting: true,
  enableFilter: true,
  columnDefs: columnDefs,
  enableColResize: true,
  rowModelType: 'enterprise',
  cacheBlockSize: 1000,
  suppressAggFuncInHeader: true,
  animateRows: false
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
  gridOptions.api.setEnterpriseDatasource(new EnterpriseDatasource());
});

function numberCellFormatter(params) {
  let formattedNumber = Math.floor(Math.abs(params.value)).toString().replace(/(\d)(?=(\d{3})+(?!\d))/g, "$1,");
  return params.value < 0 ? '(' + formattedNumber + ')' : formattedNumber;
}