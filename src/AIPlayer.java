import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Lord Daniel on 7/20/2016.
 */
public class AIPlayer extends Player {

    public AIPlayer(){
        super();

    }

    public ArrayList<WordOption> dismantleBoard(Board board){
        ArrayList<WordOption> ar = new ArrayList<>();



        return ar;
    }

    /**
     * read the options list, decide what word to enter an return an arraylist of LetterPosition objects
     * @return
     */
    public ArrayList<ArrayList<LetterPosition>> createOptions(ArrayList<WordOption> options, ArrayList<Character> tiles){
        ArrayList<ArrayList<LetterPosition>> listOfOptions = new ArrayList<>();
        for(WordOption w:options){
            ArrayList<LetterPosition> ar = new ArrayList<>();
            for(String word: Validator.getWords()){
                //check if word contains w
                if(word.contains(w.word.toLowerCase())){
                    //check if the board can accomodate word
                    int i1 = word.indexOf(w.word.toLowerCase());
                    int i2 = i1 + w.word.length();
                    int charsBeginning = i1;
                    int charsEnd = word.length() - i2 + 1;
                    if(w.leftSpaces >= charsBeginning && w.rightSpaces>=charsEnd) {
                        //check if the player has enough letters to form the word
                        ArrayList<Character> wChars = new ArrayList<Character>();
                        for (char c : w.word.toCharArray()) {
                            wChars.add(c);
                        }
                        ArrayList<Character> lettersneeded = new ArrayList<Character>();
                        for (char c : word.toCharArray()) {
                            lettersneeded.add(c);
                        }
                        for(char c:wChars){
                            lettersneeded.remove(c);
                        }
                        //the arrayList of letters needed has been generated
                        //now see if player has the tiles
                        ArrayList<Character> lettersNeededCopy = new ArrayList<>(lettersneeded);
                        for(char c:tiles){
                            lettersNeededCopy.remove(c);
                        }
                        if(lettersNeededCopy.isEmpty()){
                            //the player has enough tiles
                            //now check the orientation an generate List of LetterPosition objects
                            int index = 0;
                            if(w.orientation == 'H'){
                                for(int i=w.beginIndex[1] - w.leftSpaces; i<w.endIndex[1] + w.rightSpaces; i++){
                                    //if i is not on the word already on the board
                                    if(i<w.beginIndex[1] || i>=w.endIndex[1]){
                                        //beginIndex[0] because the row number is same
                                        int[] pos = {w.beginIndex[0],i};
                                        ar.add(new LetterPosition(lettersneeded.get(index), pos));
                                        index++;
                                    }
                                }
                            }
                            else{//vertical
                                for(int i=w.beginIndex[0] - w.leftSpaces; i<w.endIndex[0] + w.rightSpaces; i++){
                                    //if i is not on the word already on the board
                                    if(i<w.beginIndex[0] || i>=w.endIndex[0]){
                                        //beginIndex[1] because the column number is same
                                        int[] pos = {w.beginIndex[1],i};
                                        ar.add(new LetterPosition(lettersneeded.get(index), pos));
                                        index++;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            listOfOptions.add(ar);
        }

        return listOfOptions;
    }

    public int computeScore(ArrayList<LetterPosition> tiles){
        int score = 0;
        for(LetterPosition L:tiles){
            score += TilesBag.getLetterScore(L.letter);
        }
        if(tiles.size() == 7){
            //BONUS
            score += 50;
        }
        return score;
    }

    /**
     *Use chooseWord() to generate a list of LetterPosition objects and plug them into the board
     * @param board
     */
    public ArrayList<LetterPosition> play(Board board, Player player){
        ArrayList<LetterPosition> ar = new ArrayList<>();

        ArrayList<ArrayList<LetterPosition>> options = createOptions(dismantleBoard(board), player.getTiles());

        ArrayList<Integer> scores = new ArrayList<>();
        for(ArrayList<LetterPosition> op:options){
            scores.add(computeScore(op));
        }

        int max = scores.get(0);
        for(int i:scores){
            if(i>max){
                max = i;
            }
        }

        ar = options.get(scores.indexOf(max));

        return ar;
    }

}

/*
ALGORITHM

1. Iterate by row, then by column.
2. For each row/column, get words, and the number of empty spots on each sie of the word.
3. For each extracted word, iterate through the list of words nd check if the word is part of the word in words list.
4. If yes, use indexes to see if the word from words list will fit on the board.
5. If yes, check if you have enough letters for the word by first removing letters of the word on the board
    from the word from words list, then removing the letters in your tiles list from the word from words list.
    At the end if no letters are left in the word from words list, convert it into an wordOption object and
    ad it to arrayList.
6. From the arraylist of options, get the option with the highest score and ad it to board.


 */