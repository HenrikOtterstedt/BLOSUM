import org.apache.commons.math3.fraction.Fraction;

import java.util.Arrays;
import java.util.stream.DoubleStream;

public class TableRow {
    CharTupel chars;
    double[] values;

    public TableRow(CharTupel chars, int length)
    {
        this.values = new double[length];
        this.chars = chars;
    }

    public String toString()
    {
        String output = chars.one + "|" + chars.two +"\t";
        for(double d : values)
        {
            output = output + new Fraction(d) + "\t|\t";
        }
        output = output.substring(0,output.length()-2);
        output = output + "\n";
        return output;
    }

    public double getSum()
    {
        return DoubleStream.of(values).sum();
    }

    public String SumString()
    {
        String output = chars.one + "|" + chars.two + "\t";
        if(chars.one==chars.two)
        {
            output = output + new Fraction(getSum()*2);
        }
        else
        {
            output = output + new Fraction(getSum());
        }
        return output;
    }

    public void addValue(int index, double value)
    {
        values[index] = value;
    }


}
