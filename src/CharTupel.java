import java.util.Objects;

public class CharTupel {
    char one;
    char two;

    CharTupel(char one, char two)
    {
        this.one = one;
        this.two = two;
    }

    @Override
    public String toString() {
        return one+"|"+two;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CharTupel charTupel = (CharTupel) o;
        return one == charTupel.one &&
                two == charTupel.two;
    }

    @Override
    public int hashCode() {
        return Objects.hash(one, two);
    }
}
