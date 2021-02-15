import java.util.ArrayList;

public class FirstTable {
    ArrayList<TableRow> rows;

    public FirstTable()
    {
        this.rows = new ArrayList<>();
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
        return output;
    }
}
