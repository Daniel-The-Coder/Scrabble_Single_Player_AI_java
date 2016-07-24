import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Lord Daniel on 7/24/2016.
 */
public class AIvsAIGame {

    public boolean gameOver;
    public TilesBag tilesBag;
    public Board board;
    public AIPlayer AIplayer1;
    public AIPlayer AIplayer2;

    public AIvsAIGame() throws FileNotFoundException{
        this.gameOver = false;
        this.tilesBag = new TilesBag();
        this.board = new Board();
    }

    public void addTiles(ArrayList<LetterPosition> tiles){
        for(LetterPosition L:tiles){
            int row = L.position[0];
            int col = L.position[1];
            this.board.add(L.letter, row, col);
        }
    }

    public char[][] copyBoard(char[][] board1){
        char[][] board2 = new char[15][15];
        for(int i=0;i<15;i++){
            for(int j=0;j<15;j++){
                board2[i][j] = new Character(board1[i][j]);
            }
        }
        return board2;
    }

    public int computeScore(ArrayList<LetterPosition> tiles){
        int score = 0;
        for(LetterPosition L:tiles){
            score += tilesBag.getLetterScore(L.letter);
        }
        if(tiles.size() == 7){
            //BONUS
            score += 50;
        }
        return score;
    }

    /**
     * strip leading and trailing hyphens
     * @param st
     * @return
     */
    public String stripHyphens(String st, int index){
        String str = "";
        int i1 = index+1;
        int i2 = index;
        while(!(st.charAt(i1)=='-')){
            str += st.substring(i1,i1+1); // add to the end
            if(i1<14) {
                i1 += 1;
            }
            else{
                break;
            }
        }
        while(!(st.charAt(i2)=='-')){
            str = st.substring(i2,i2+1) + str; // add to the beginning
            if(i2>0) {
                i2 -= 1;
            }
            else{
                break;
            }
        }
        return str;
    }

    /**
     * based on letterPositions, figure out if word is horizontal or vertical
     * get that row and column, then strip '-' from beginning and end
     * @param letterPositions
     * @return
     */
    public String computeWord(ArrayList<LetterPosition> letterPositions){
        char[][] boardCopy = copyBoard(board.getBoard());
        for (LetterPosition L:letterPositions){
            boardCopy[L.position[0]][L.position[1]] = L.letter;
        }
        if(letterPositions.size()==1){
            //here, check words both horizontally and vertically
            int row1 = letterPositions.get(0).position[0];
            int col1 = letterPositions.get(0).position[1];

            String st1 = "";
            int row = letterPositions.get(0).position[0];
            for (int i = 0; i < 15; i++) {
                st1 += Character.toString(boardCopy[row][i]);
            }
            String word1 = stripHyphens(st1, letterPositions.get(0).position[1]);

            String st2 = "";
            int col = letterPositions.get(0).position[1];
            for (int i = 0; i < 15; i++) {
                st2 += Character.toString(boardCopy[i][col]);
            }
            String word2 = stripHyphens(st2, letterPositions.get(0).position[0]);

            if(Validator.isValid(word1)){
                return word1;
            }
            else if(Validator.isValid(word2)){
                return word2;
            }
            else{
                return word1+" or "+word2;
            }
        }
        else {
            String word = "";
            boolean rowsEqual = true;
            boolean colsEqual = true;
            int row1 = letterPositions.get(0).position[0];
            int col1 = letterPositions.get(0).position[1];
            for (LetterPosition L : letterPositions) {
                if (!(L.position[0] == row1)) {
                    rowsEqual = false;
                }
                if (!(L.position[1] == col1)) {
                    colsEqual = false;
                }
            }
            if (rowsEqual) {
                String st = "";
                int row = letterPositions.get(0).position[0];
                for (int i = 0; i < 15; i++) {
                    st += Character.toString(boardCopy[row][i]);
                }
                word = stripHyphens(st, letterPositions.get(0).position[1]);
            } else if (colsEqual) {
                String st = "";
                int col = letterPositions.get(0).position[1];
                for (int i = 0; i < 15; i++) {
                    st += Character.toString(boardCopy[i][col]);
                }
                word = stripHyphens(st, letterPositions.get(0).position[0]);
            } else {
                System.out.println("\nERROR in computeWord!!!\n");
            }
            return word;
        }
    }

    public void initializeGame() throws FileNotFoundException{
        Validator.init();
        System.out.println("Welcome to Daniel's AI Scrabble!\n");
        System.out.println("* * * Commencing game with two AI players. * * *");
        this.AIplayer1 = new AIPlayer("AI Player 1");
        this.AIplayer2 = new AIPlayer("AI Player 2");
    }

    /**
     * takes in a character and returns a number corresponding to it
     * @return
     */
    private static int letterToNum(char c){
        return (int)Character.toUpperCase(c) - 64; //A is 65; output should be 1
    }

    /**
     * Convert String to Hashmap of letters and indexes
     * For example: X 2F Y 2G Z 2H -> { X:[2,6] ; Y:[2,7] ; Z:[2,8] }
     * @param line
     * @return
     */
    public static ArrayList<LetterPosition> parseInput(String line){
        ArrayList<LetterPosition> ar = new ArrayList<>();
        List<String> lineList = Arrays.asList(line.split(" "));
        for(int i=0;i<lineList.size();i+=2){
            char letter = Character.toUpperCase(lineList.get(i).charAt(0));
            String position = lineList.get(i+1);
            int row = Integer.parseInt(position.substring(0, position.length()-1)) - 1;
            int col = letterToNum(Character.toUpperCase(position.charAt(position.length()-1))) - 1;
            int[] pos = {row,col};
            ar.add(new LetterPosition(letter, pos));
        }
        return ar;
    }

    public void play(){
        Scanner in = new Scanner(System.in);
        while(!gameOver){

            //AI 1 plays
            System.out.println("\n\nIt's AI player 1's turn!");
            //refill AI's's list of tiles;
            AIplayer1.addTiles(tilesBag.getTiles(7-AIplayer1.gettilesLeft()));
            System.out.println("\n"+this.board+"\n");
            System.out.println("AI player 1's tiles: "+AIplayer1.getTiles());
            //all computation happens in AIPlaayer.play()
            ArrayList<LetterPosition> AITiles1 = AIplayer1.play(this.board, AIplayer1);
            if(AITiles1.size()==0){
                //AI player passes
                AIplayer1.pass();
            }
            else{
                AIplayer1.addScore(computeScore(AITiles1));
                addTiles(AITiles1);
                for (LetterPosition c : AITiles1) {
                    AIplayer1.removeTile(c.letter);
                }
                System.out.println("\nWord: " + computeWord(AITiles1));
                System.out.println("AI Player 1 scores " + computeScore(AITiles1) + " points.");
                System.out.println("AI Player 1's current total score is " +AIplayer1.getScore() + " points.");
                System.out.println("Tiles left: "+tilesBag.tilesLeft());
            }

            //check if tiles left in tile bag. game ends when the bag runs out of tiles
            if(this.tilesBag.tilesLeft()==0){
                gameOver = true;
                break;
            }

            //AI 2 plays
            System.out.println("\n\nIt's AI player 2's turn!");
            //refill AI's's list of tiles;
            AIplayer2.addTiles(tilesBag.getTiles(7-AIplayer2.gettilesLeft()));
            System.out.println("\n"+this.board+"\n");
            System.out.println("AI player 2's tiles: "+AIplayer2.getTiles());
            //all computation happens in AIPlaayer.play()
            ArrayList<LetterPosition> AITiles2 = AIplayer2.play(this.board, AIplayer2);
            if(AITiles2.size()==0){
                //AI player passes
                AIplayer2.pass();
            }
            else{
                AIplayer2.addScore(computeScore(AITiles2));
                addTiles(AITiles2);
                for (LetterPosition c : AITiles2) {
                    AIplayer2.removeTile(c.letter);
                }
                System.out.println("\nWord: " + computeWord(AITiles2));
                System.out.println("AI Player 2 scores " + computeScore(AITiles2) + " points.");
                System.out.println("AI Player 2's current total score is " +AIplayer2.getScore() + " points.");
                System.out.println("Tiles left: "+tilesBag.tilesLeft());
            }
        }
        System.out.println("\n"+this.board+"\n");
    }

    public static void main(String[] args) throws FileNotFoundException{
        AIvsAIGame game = new AIvsAIGame();
        game.initializeGame();
        game.play();
        System.out.println("\n* * * GAME OVER * * *");
        //print winner

        System.out.println("\nScores:");
        System.out.println(game.AIplayer1.getName()+": "+game.AIplayer1.getScore());
        System.out.println(game.AIplayer2.getName()+": "+game.AIplayer2.getScore());

        if(game.AIplayer1.getScore() > game.AIplayer2.getScore()){
            System.out.println("Winner: AI; score: "+game.AIplayer1.getScore());
        }
        else if(game.AIplayer1.getScore() < game.AIplayer2.getScore()){
            System.out.println("Winner: "+game.AIplayer2.getName()+"; score: "+game.AIplayer2.getScore());
        }
        else{
            System.out.println("Tie. Score: "+game.AIplayer1.getScore());
        }

    }

}
