/**
 * Created by Lord Daniel on 7/21/2016.
 */
public class WordOption {
    public String word;
    public int leftSpaces;
    public int rightSpaces;
    public char orientation;
    public int[] beginIndex;
    public int[] endIndex;

    public WordOption(String word, int leftSpaces, int rightSpaces, char orientation, int[] beginIndex, int[] endIndex){
        this.word = word;
        this.leftSpaces = leftSpaces;
        this.rightSpaces = rightSpaces;
        this.orientation = orientation;
        this.beginIndex = beginIndex;
        this.endIndex = endIndex;
    }
}
