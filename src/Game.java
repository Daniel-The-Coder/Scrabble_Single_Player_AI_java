import java.io.FileNotFoundException;
import java.util.*;

/**
 * Created by Lord Daniel on 7/16/2016.
 */
public class Game {
    public boolean gameOver;

    public static boolean firstWord = true;

    public TilesBag tilesBag;
    public Board board;
    public Player p;
    public AIPlayer AIplayer;

    public Game() throws FileNotFoundException{
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
        Scanner in = new Scanner(System.in);
        System.out.println("Welcome to Daniel's single player Scrabble!\n");
        System.out.print("Player: Enter name: ");
        String playerName = in.next();
        if(playerName.equals("")){
            playerName = "Player1";
        }
        this.p = new Player(playerName);
        this.AIplayer = new AIPlayer();
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

            //HUMAN PLAYS
            boolean pass = false;

            //refill player's list of tiles
            p.addTiles(tilesBag.getTiles(7-p.gettilesLeft()));

            //display current player's tiles and prompt him to play
            System.out.println("\nIt's "+p.getName()+"'s turn!");
            System.out.println("\n"+this.board+"\n");
            System.out.println("Your tiles: "+p.getTiles());
            System.out.print("Enter letters and locations OR \"pass\": ");
            String line = in.nextLine();
            if(line.toUpperCase().equals("PASS")){
                p.pass();
            }
            else {
                ArrayList<LetterPosition> letterPositions = parseInput(line);
                int errorcode = Validator.validate(letterPositions, this.board.getBoard(), p.getTiles());
                if (errorcode != 0) {
                    //loop until valid indexes given
                    while (errorcode != 0) {
                        pass = false;
                        if (errorcode == 1) {
                            System.out.print("Invalid indexes. Try again: ");
                        } else if (errorcode == 2) {
                            System.out.print("You must place your tiles on only one row or one column. Try again: ");
                        } else if(errorcode == 3) {
                            System.out.print("One or more of the indexes is occupied. Try again: ");
                        }
                        else if(errorcode == 4){
                            System.out.print("You do not have enough tiles. Try again: ");
                        }
                        else{
                            System.out.print("Your word must join the existing cluster Try again: ");
                        }
                        line = in.nextLine();
                        if(line.toUpperCase().equals("PASS")){
                            p.pass();
                            break;
                        }
                        letterPositions = parseInput(line);
                        errorcode = Validator.validate(letterPositions, this.board.getBoard(), p.getTiles());
                    }
                }
                //now valid input has been obtained
                //use indexes to figure out the word and check if it's valid
                String word = computeWord(letterPositions);
                if(!Validator.isValid(word)) {
                    //no valid words found
                    //loop until valid word found
                    while (!Validator.isValid(word)) {
                        if(pass){
                            break;
                        }
                        System.out.print("Invalid word or length is 1: "+word+". Try again.");
                        line = in.nextLine();
                        if (line.toUpperCase().equals("PASS")) {
                            p.pass();
                            break;
                        } else {
                            pass = false;
                            letterPositions = parseInput(line);
                            int errorcode2 = Validator.validate(letterPositions, this.board.getBoard(), p.getTiles());
                            if (errorcode2 != 0) {
                                //loop until valid indexes given
                                while (errorcode2 != 0) {
                                    if (errorcode == 1) {
                                        System.out.print("Invalid indexes. Try again: ");
                                    } else if (errorcode == 2) {
                                        System.out.print("You must place your tiles on only one row or one column. Try again: ");
                                    } else if(errorcode == 3) {
                                        System.out.print("One or more of the indexes is occupied. Try again: ");
                                    }
                                    else if(errorcode == 4){
                                        System.out.print("You do not have enough tiles. Try again: ");
                                    }
                                    else{
                                        System.out.print("Your word must join the existing cluster Try again: ");
                                    }
                                    line = in.nextLine();
                                    if(line.toUpperCase().equals("PASS")){
                                        p.pass();
                                        pass = true;
                                        break;
                                    }
                                    letterPositions = parseInput(line);
                                    errorcode2 = Validator.validate(letterPositions, this.board.getBoard(), p.getTiles());
                                }
                            }
                            //now valid input has been obtained
                            //use indexes to figure out the word and check if it's valid
                            word = computeWord(letterPositions);
                        }
                    }
                }

                if(!pass) {
                    firstWord = false;
                    //SCORE PERPENDICULAR WORDS TOO//TODO
                    ArrayList<LetterPosition> tiles = parseInput(line);
                    p.addScore(computeScore(tiles));
                    addTiles(tiles);
                    for (LetterPosition c : tiles) {
                        p.removeTile(c.letter);
                    }
                    System.out.println("\nWord: " + word);
                    System.out.println(p.getName() + " scores " + computeScore(tiles) + " points.");
                    System.out.println(p.getName() + "'s current total score is " + p.getScore() + " points.");
                    System.out.println("Tiles left: "+tilesBag.tilesLeft());
                }
            }

            //check if tiles left in tile bag. game ends when the bag runs out of tiles
            if(this.tilesBag.tilesLeft()==0){
                gameOver = true;
                break;
            }

            //AI plays
            System.out.println("\n\nIt's AI player's turn!");
            //refill AI's's list of tiles;
            AIplayer.addTiles(tilesBag.getTiles(7-AIplayer.gettilesLeft()));
            System.out.println("\n"+this.board+"\n");
            System.out.println("AI player's tiles: "+AIplayer.getTiles());
            //all computation happens in AIPlaayer.play()
            ArrayList<LetterPosition> AITiles = AIplayer.play(this.board, AIplayer);
            if(AITiles.size()==0){
                //AI player passes
                AIplayer.pass();
            }
            else{
                AIplayer.addScore(computeScore(AITiles));
                addTiles(AITiles);
                for (LetterPosition c : AITiles) {
                    AIplayer.removeTile(c.letter);
                }
                System.out.println("\nWord: " + computeWord(AITiles));
                System.out.println("AI Player scores " + computeScore(AITiles) + " points.");
                System.out.println("AI Player's current total score is " +AIplayer.getScore() + " points.");
                System.out.println("Tiles left: "+tilesBag.tilesLeft());
            }
        }
    }

    public static void main(String[] args) throws FileNotFoundException{
        Game game = new Game();
        game.initializeGame();
        game.play();
        System.out.println("\n* * * GAME OVER * * *");
        //print winner

        System.out.println("\nScores:");
        System.out.println("AI: "+game.AIplayer.getScore());
        System.out.println(game.p.getName()+": "+game.p.getScore());

        if(game.AIplayer.getScore() > game.p.getScore()){
            System.out.println("Winner: AI; score: "+game.AIplayer.getScore());
        }
        else if(game.AIplayer.getScore() < game.p.getScore()){
            System.out.println("Winner: "+game.p.getName()+"; score: "+game.p.getScore());
        }
        else{
            System.out.println("Tie. Score: "+game.p.getScore());
        }

    }
}
