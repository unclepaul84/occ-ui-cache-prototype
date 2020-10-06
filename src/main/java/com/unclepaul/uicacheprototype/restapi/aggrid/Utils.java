package com.unclepaul.uicacheprototype.restapi.aggrid;

import com.unclepaul.uicacheprototype.restapi.aggrid.filter.ColumnFilter;
import com.unclepaul.uicacheprototype.restapi.aggrid.filter.NumberColumnFilter;
import com.unclepaul.uicacheprototype.restapi.aggrid.filter.TextColumnFilter;
import com.unclepaul.uicacheprototype.restapi.aggrid.request.SortModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.lang.String.join;
import static java.util.stream.Collectors.toList;

public final class Utils {


    public static String whereSql(Map<String, ColumnFilter> filterMap) throws Exception {
        if(filterMap == null || filterMap.size()==0)
            return " ";

        StringBuilder sb = new StringBuilder();

        sb.append(" WHERE (");

        List<String> conditions = new ArrayList<>();

        for (var fi: filterMap.entrySet())
        {
            if(fi.getValue() instanceof TextColumnFilter)
            {
                var textFilter = (TextColumnFilter)fi.getValue();

                if(textFilter.getType().equals("startsWith"))
                {
                    conditions.add(fi.getKey() + " LIKE '" + textFilter.getFilter() + "%'");

                }else if (textFilter.getType().equals("contains"))
                {
                    conditions.add(fi.getKey() + " LIKE '%" + textFilter.getFilter() + "%'");
                }
                else if (textFilter.getType().equals("equals"))
                {
                    conditions.add(fi.getKey() + " = '" + textFilter.getFilter() + "'");
                }
                else
                    throw new Exception("Unsupported filter type: " + textFilter.getType());
            }
            else if(fi.getValue() instanceof NumberColumnFilter)
            {
                var numFilter = (NumberColumnFilter)fi.getValue();

                if(numFilter.getType().equals("equals"))
                {
                    conditions.add(fi.getKey() + " = " + numFilter.getFilter());

                }else if (numFilter.getType().equals("notEqual"))
                {
                    conditions.add(fi.getKey() + " != " + numFilter.getFilter() );
                }
                else if (numFilter.getType().equals("lessThan"))
                {
                    conditions.add(fi.getKey() + " < " + numFilter.getFilter() );
                }
                else if (numFilter.getType().equals("greaterThan"))
                {
                    conditions.add(fi.getKey() + " > " + numFilter.getFilter() );
                }
                else
                    throw new Exception("Unsupported filter type: " + numFilter.getType());
            }
        }

        sb.append(join(" AND ", conditions));
        sb.append(")");

        return sb.toString();

    }
    public static String orderBySql(List<SortModel> sortModel) {
        Function<SortModel, String> orderByMapper = model -> model.getColId() + " " + model.getSort();

        List<String> orderByCols = sortModel.stream()
                .map(orderByMapper)
                .collect(toList());

        return orderByCols.isEmpty() ? "" : " ORDER BY " + join(",", orderByCols);
    }

   private Utils(){}
}
