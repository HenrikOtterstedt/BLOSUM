import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public class Cluster {
    HashSet<Integer> cluster_ids = new HashSet<>();
    HashMap<Character,Integer[]> characterHashMap = new HashMap<>();

    public Cluster(HashSet<Integer> clustered)
    {
        cluster_ids.addAll(clustered);
    }

    public boolean is_clustered(int i)
    {
        return cluster_ids.contains(i);
    }

    public void addCluster(HashSet<Integer> cluster)
    {
        cluster_ids.addAll(cluster);
    }

    public HashSet<Integer> getCluster_ids() {
        return cluster_ids;
    }

    @Override
    public String toString() {
        String output = "Cluster{";
        for(int cluster_id : cluster_ids)
        {
            output = output + (cluster_id+1) + ", ";
        }
        output=output.substring(0,output.length()-2);
        return  output +
                '}';
    }

    public void computeChars(HashSet<Character> characters, ArrayList<Sequenz> sequenzs)
    {
        for(Character character : characters)
        {
            Integer[] tmp = new Integer[sequenzs.get(0).seq.length()];
            Arrays.fill(tmp, 0);
            characterHashMap.put(character,tmp);
        }

        for(int seq_id : cluster_ids)
        {
            Sequenz seq = sequenzs.get(seq_id);
            for(int j=0;j<seq.seq.length();j++)
            {
                Integer[] tmp = characterHashMap.get(seq.seq.charAt(j));
                tmp[j]++;
                characterHashMap.put(seq.seq.charAt(j),tmp);
            }
        }
    }

    public int getValAt(Character character, int position)
    {
        return characterHashMap.get(character)[position];
    }

    public int getNSeq()
    {
        return cluster_ids.size();
    }
}
