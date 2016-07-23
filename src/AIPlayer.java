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

        char[][] brd = board.getBoard();

        //horizontal
        char orientation = 'H';
        for(int i=0;i<15;i++){
            ArrayList<Character> st= new ArrayList<>();
            for(int j=0;j<15;j++){
                st.add(brd[i][j]);
            }
            //get words out of the string st, find beginning and end indexes, and the number of empty usable
            //spaces to the right and the left of the word

            //ALGORITHM
            //iterate through string
            //When it changes fom letters to hyphens, the word is complete; add it to the list
            String wrd = "";
            int indexOfLastLetter = 0;
            int beginIndex = 0;
            int endIndex = 0;
            int leftSpaces = 0;
            int rightSpaces = 0;
            int leftSpacesCount = 0;
            int rightSpacesCount = 0;
            boolean prevWasEmpty = true;
            int idx = 0;
            boolean firstWord = true;
            for(char c:st){
                //empty cell after one or more empty cells
                if( c=='-' && prevWasEmpty){
                    leftSpacesCount++;
                    rightSpacesCount++;
                }

                //first empty cell after a word
                else if( c=='-' && !prevWasEmpty ){
                    rightSpacesCount = 1;
                    leftSpacesCount = 1;
                    prevWasEmpty = true;
                    endIndex = idx;
                    firstWord = false;

                }

                //first letter after empty cell
                else if( c!='-' && prevWasEmpty ){
                    if(!wrd.equals("")) {
                        //ADD NEW WordOption OBJECT TO AR
                        int[] begIdx = {i, beginIndex};
                        int[] endIdx = {i, endIndex};
                        ar.add(new WordOption(wrd, leftSpaces, rightSpaces, 'H', begIdx, endIdx));
                        System.out.println(wrd);
                        leftSpacesCount = 0;
                        rightSpacesCount = 0;
                    }
                    prevWasEmpty = false;
                    beginIndex = idx;
                    wrd = Character.toString(c);
                    rightSpaces = rightSpacesCount-1;
                    rightSpacesCount = 0;
                    if(firstWord){
                        leftSpaces = leftSpacesCount;
                    }
                    else{
                        leftSpaces = leftSpacesCount-1;
                    }
                }

                //a letter after one or more letters
                else if( c!='-' && !prevWasEmpty ){
                    wrd += Character.toString(c);
                }
                idx++;
            }
            if(!prevWasEmpty && !wrd.equals("")){
                rightSpaces = 0;
                endIndex = 15;
                int[] begIdx = {i,beginIndex};
                int[] endIdx = {i,endIndex};
                ar.add(new WordOption(wrd,leftSpaces,rightSpaces,'H', begIdx, endIdx));
            }
            else if(prevWasEmpty && !wrd.equals("")){
                int[] begIdx = {i,beginIndex};
                int[] endIdx = {i,endIndex};
                rightSpaces = rightSpacesCount;
                ar.add(new WordOption(wrd,leftSpaces,rightSpaces,'H', begIdx, endIdx));
            }
        }
        System.out.println(ar);
        return ar;
    }

    /**
     * read the dismantled board, for each element, iterate through list of all words to find a word that can be formed
     * based on the indexes, convert that to a letterpositions object and ad it to list to be returned
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