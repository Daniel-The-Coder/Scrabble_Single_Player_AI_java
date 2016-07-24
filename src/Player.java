import java.util.ArrayList;

/**
 * Created by Lord Daniel on 7/16/2016.
 */
public class Player {

    private String name;
    private int score;
    private ArrayList<Character> tiles;

    /**
     * nameless constructor for AI to inherit
     */
    public Player(){
        this.score = 0;
        this.tiles = new ArrayList<>();
        this.name = "AI Player";
    }

    public Player(String name){
        this.name = name;
        this.score = 0;
        this.tiles = new ArrayList<>();
    }

    public void addScore(int score){
        this.score += score;
    }

    public int getScore(){
        return this.score;
    }

    public int gettilesLeft(){
        return this.tiles.size();
    }

    public void addTiles(ArrayList tiles){
        this.tiles.addAll(tiles);
    }

    public String getName(){
        return this.name;
    }

    public ArrayList<Character> getTiles(){
        return this.tiles;
    }

    public void removeTile(char c){
        tiles.remove((Character)c);
    }

    public void pass(){
        System.out.println("\n* * * "+this.getName()+" passes this turn. * * *\n");
    }

    public void setName(String name){
        this.name = name;
    }
}
