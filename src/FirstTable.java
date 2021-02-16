import org.apache.commons.math3.fraction.Fraction;

import java.util.ArrayList;
import java.util.HashMap;

public class FirstTable {
    ArrayList<TableRow> rows;
    HashMap<CharTupel, Fraction> sums;

    public FirstTable()
    {
        this.rows = new ArrayList<>();
        sums = new HashMap<>();
    }

    public void addRow(TableRow tr)
    {
        rows.add(tr);
    }

    public String toString()
    {
        return rows.toString();
    }

    public String finalTable()
    {
        String output = "";
        for(TableRow tr : rows)
        {
            output = output + tr.SumString() + "\n";
        }
        fill_sums();
        return output;
    }

    private void fill_sums()
    {
        for(TableRow tableRow : rows)
        {
            sums.put(tableRow.chars,new Fraction(tableRow.getSum()));
        }
    }
}
