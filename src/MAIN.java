import org.apache.commons.cli.*;
import org.apache.commons.math3.fraction.Fraction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class MAIN {
    public static void main(String[] args) {
        Options options = new Options();

        Option threshold = new Option("t","threshold",true,"Threshold for the clustering required for the BLOSUM Matrix. (For a BLOSUM-55 it is 55).");
        threshold.setRequired(true);
        options.addOption(threshold);

        Option sequences = new Option("s","sequences",true,"The Sequence Block for which the BLOSUM Matrix is to be calculated. The Sequences have to be entered in the following way: \n <Seq1>,<Seq2>,<Seq3> ...");
        sequences.setRequired(true);
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

        String[] seqs = cmd.getOptionValue("s").split(",");
        System.out.println("Given Sequences:");
        System.out.println(Arrays.toString(seqs) +"\n");
        System.out.println("Given Threshold:");
        System.out.println(cmd.getOptionValue("t")+"\n");
        ArrayList<Sequenz> allSeqs = new ArrayList<>();
        for(int i=0;i<seqs.length;i++)
        {
            allSeqs.add(new Sequenz(i,seqs[i]));
        }

        ArrayList<Cluster> cluster = cluster(Integer.parseInt(cmd.getOptionValue("t")),allSeqs);
        System.out.println("The following clusters were found. The number is referring to the place in the input string.");
        System.out.println(cluster+"\n");

        if(cluster.size()>1) {
            FirstTable ft = computeFirstTable(allSeqs, cluster);
            System.out.println("The position Table:");
            System.out.println(ft.toString());
            System.out.println("\n");
            System.out.println("The final Table with all the sums. Sums for rows with the same Character are doubled.");
            System.out.println(ft.finalTable());
        }
        else{
            System.out.println("There was only one cluster found, consisting of all Seqs.");
            System.out.println("No BLOSUM Matrix can be computed.");
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

