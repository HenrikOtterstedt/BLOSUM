import java.util.HashSet;

public class Sequenz {
    int id;
    String seq;
    HashSet<Integer> clustered = new HashSet<>();

    public Sequenz(int id, String seq) {
        this.id = id;
        this.seq = seq;
        clustered.add(id);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSeq() {
        return seq;
    }

    public void setSeq(String seq) {
        this.seq = seq;
    }

    public void addCluster(int id)
    {
        clustered.add(id);
    }

    public HashSet<Integer> getClustered()
    {
        return this.clustered;
    }

    public int calcSim(String seq2)
    {
        int counter = 0;
        for(int i=0;i<seq.length();i++)
        {
            if(seq.charAt(i)==seq2.charAt(i))
            {
                counter++;
            }
        }
        return counter;
    }
}
