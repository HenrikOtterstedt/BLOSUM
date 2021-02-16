import org.apache.commons.cli.*;
import org.apache.commons.math3.fraction.Fraction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class MAIN {
    public static void main(String[] args) {
        Options options = new Options();

        Option threshold = new Option("t","threshold",true,"Threshold for the clustering required for the BLOSUM Matrix. (For a BLOSUM-55 it is 55).\n The parameter should be an Integer between 0 and 100.");
        threshold.setRequired(true);
        options.addOption(threshold);

        Option sequences = new Option("s","sequences",true,"Sequence Blocks for the BLOSUM Matrix. \nWithin a block the Sequences have to be formatted in the following way: \n <Block1.1>,<Block1.2>,<Block1.3> <Block2.1>,<Block2.2>...\n The Blocks have to be separated by <Space>.");
        sequences.setRequired(true);
        sequences.setArgs(Option.UNLIMITED_VALUES);
        options.addOption(sequences);

        //add parser and help formatter
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;

        //parse arguments
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("utility-name", options);
            System.exit(1);
        }

        //get the Sequences and calculate clusters and tables for them
        String[] blocks = cmd.getOptionValues("s");

        ArrayList<FirstTable> tables = new ArrayList<>();
        int blockcounter = 1;
        for(String block : blocks) {
            String[] seqs = block.split(",");
            System.out.println("[BLOCK "+blockcounter+"]:\tGiven Sequences:");
            System.out.println(Arrays.toString(seqs) + "\n");
            System.out.println("Given Threshold:");
            System.out.println(cmd.getOptionValue("t") + "\n");
            ArrayList<Sequenz> allSeqs = new ArrayList<>();
            for (int i = 0; i < seqs.length; i++) {
                allSeqs.add(new Sequenz(i, seqs[i]));
            }

            ArrayList<Cluster> cluster = cluster(Integer.parseInt(cmd.getOptionValue("t")), allSeqs);
            System.out.println("[BLOCK "+blockcounter+"]:\tThe following clusters were found. The number is referring to the place in the input string.");
            System.out.println(cluster + "\n");

            if (cluster.size() > 1) {
                FirstTable ft = computeFirstTable(allSeqs, cluster);
                System.out.println("[BLOCK "+blockcounter+"]:\tThe position Table:");
                System.out.println(ft.toString());
                System.out.println("\n");
                System.out.println("The sum-table for [BLOCK "+blockcounter+"]. Sums for rows with the same Character are doubled.");
                System.out.println(ft.finalTable());
                tables.add(ft);
            } else {
                System.out.println("[BLOCK "+blockcounter+"]:\tThere was only one cluster found, consisting of all Seqs.");
                System.out.println("No BLOSUM Matrix can be computed.");
            }
            blockcounter++;
            System.out.println("\n----------\n");
        }
        //make the final matrix
        System.out.println("The final BLOSUM-"+cmd.getOptionValue("t")+" Matrix. Calculated by adding the results of all Blocks.");
        for(CharTupel charTupel : tables.get(0).sums.keySet())
        {
            String output = charTupel.one +"|"+charTupel.two+"\t";
            Fraction value = new Fraction(0);
            for(FirstTable firstTable: tables)
            {
                value = value.add(firstTable.sums.get(charTupel));
            }
            System.out.println(output+value);
        }
    }


    public static ArrayList<Cluster> cluster(int threshold, ArrayList<Sequenz> seqs)
    {
        int length = seqs.get(0).seq.length();
        ArrayList<Cluster> clusters = new ArrayList<>();
        for(int i=0;i<seqs.size();i++)
        {
            for(int j=i+1;j<seqs.size();j++)
            {
                Sequenz seq1 = seqs.get(i);
                Sequenz seq2 = seqs.get(j);
                int sim = seq1.calcSim(seq2.seq);
                if(((float)(sim)/(float)(length))*100>=threshold)
                {
                    seq1.addCluster(j);
                    seq2.addCluster(i);
                }
            }
        }

        for(Sequenz sequenz : seqs)
        {
            boolean seq_clustered = false;
            for(Cluster cluster : clusters)
            {
                if(cluster.is_clustered(sequenz.id))
                {
                    cluster.addCluster(sequenz.clustered);
                    seq_clustered = true;
                }
            }
            if(!seq_clustered)
            {
                clusters.add(new Cluster(sequenz.clustered));
            }
        }

        return clusters;
    }

    public static FirstTable computeFirstTable(ArrayList<Sequenz> sequenzArrayList, ArrayList<Cluster> clusters)
    {
        int length = 0;
        HashSet<Character> Chars = new HashSet<>();
        FirstTable table = new FirstTable();
        for(Sequenz sequenz : sequenzArrayList)
        {
            String seq = sequenz.seq;
            length = seq.length();
            for(int i=0;i<seq.length();i++)
            {
                Chars.add(seq.charAt(i));
            }
        }

        //Compute the char frequencys in the clusters
        for(Cluster cluster : clusters)
        {
            cluster.computeChars(Chars,sequenzArrayList);
        }

        ArrayList<Character> allChars = new ArrayList<>(Chars);
        //find all possible char combinations
        ArrayList<CharTupel> charTupels = new ArrayList<>();
        for(int i=0;i<allChars.size();i++)
        {
            for(int j=i;j<allChars.size();j++)
            {
                charTupels.add(new CharTupel(allChars.get(i),allChars.get(j)));
            }
        }

        for(CharTupel charTupel : charTupels)
        {
            TableRow row = new TableRow(charTupel,length);
            //i is the position
            for(int i=0;i<length;i++)
            {
                double value = 0;
                for(int ii=0;ii<clusters.size();ii++)
                {
                    Cluster cluster = clusters.get(ii);
                    for(int jj=ii+1;jj<clusters.size();jj++)
                    {
                        Cluster cluster2 = clusters.get(jj);
                        //special case if the chars are the same
                        if(charTupel.one==charTupel.two)
                        {
                            double temp_val = cluster.getValAt(charTupel.one,i) * cluster2.getValAt(charTupel.one,i);
                            temp_val = temp_val / (cluster.getNSeq()*cluster2.getNSeq());
                            value = value + temp_val;
                        }
                        else{
                            double temp_val = cluster.getValAt(charTupel.one,i) * cluster2.getValAt(charTupel.two,i);
                            temp_val = temp_val + (cluster.getValAt(charTupel.two,i) * cluster2.getValAt(charTupel.one,i));
                            temp_val = temp_val / (cluster.getNSeq()*cluster2.getNSeq());
                            value = value + temp_val;
                        }
                    }
                }
                row.addValue(i,value);
            }
            table.addRow(row);
        }
        return table;
    }
}

