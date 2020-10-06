package com.unclepaul.uicacheprototype.restapi.aggrid.filter;

public class TextColumnFilter extends ColumnFilter {
    private String type;
    private String filter;

    public TextColumnFilter() {}

    public TextColumnFilter(String type, String filter) {
        this.type = type;
        this.filter = filter;

    }

    public String getFilterType() {
        return filterType;
    }

    public String getType() {
        return type;
    }

    public String getFilter() {
        return filter;
    }

}