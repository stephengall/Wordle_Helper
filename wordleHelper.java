import java.util.*;
import java.io.*;

public class wordleHelper{
    public static void main(String []args){
        Dictionary possibleWords = new Dictionary("/Users/stephengallagher/Desktop/Wordle/wordleWords.txt");
        Dictionary allowableWords = new Dictionary("/Users/stephengallagher/Desktop/Wordle/allowableWords.txt");
        /*Dictionary class edited so a String input parameter can be taken,
         multiple different text files can be accessed with same class*/
        
        ArrayList<String> possibilities = new ArrayList<String>();

        for(int i = 0; i < allowableWords.getSize(); i++){ //filling arraylist with possible words
            possibilities.add(allowableWords.getWord(i));
        }

        int numofGuesses = 6;

        Scanner sc = new Scanner(System.in);
        String playerInput;
        String greys = ""; //stores chars that player guessed, but that aren't present in goal word

        int length1 = possibleWords.getSize();

        String chosenWord = possibleWords.getWord((int)(Math.random() * length1 - 1)); //randomly chooses word to guess from text file.
        System.out.println(chosenWord);
        
        boolean winner = false;
        int numofWins = 0;
        String suggestion = "";

        while(numofGuesses > 0){ //while loop runs for 6 guesses, stops to get user input
            System.out.println("\n--------------------\nPlease enter guess number " + (6 - (numofGuesses - 1)) + ":");
            
            if(numofGuesses == 6){
                playerInput = allowableWords.getWord((int)(Math.random() * (allowableWords.getSize() - 1)));
                System.out.println("Initial Guess: " + playerInput);
            }else{
                playerInput = suggestion;
                System.out.println("Guess: " + playerInput);
            }
            

            if(!checkValidity(playerInput)) //checks word from user against text file for allowed words
                System.out.println("Not a valid word, please try again.\n--------------------");
            else{
                numofGuesses--;

                if(playerInput.equalsIgnoreCase(chosenWord)){
                    winner = true;
                    break;
                }
                String greenVals = printGreens(playerInput, chosenWord); //stores string of greens
                String goldVals = printGolds(playerInput, chosenWord, greenVals); //stores string of golds

                System.out.println("\nGreens: " + greenVals + "\nGolds:  " + goldVals);
                greys += getGreys(playerInput, chosenWord, greys); //adds grey letters over time

                possibilities = returnSuggestion(greenVals, goldVals, playerInput, possibilities, greys);  //possibilities arraylist is set to list of possible answers. based on greens, golds & greys
                int randInt = (int)(Math.random()) * (possibilities.size() - 1); //a random output from the list of possibilites is drawn as a suggestion
                

                System.out.println("\nNumber of possibilties: " + possibilities.size());
                if(possibilities.size() == 1){
                    suggestion = possibilities.get(0);
                    System.out.println("Suggestion: " + suggestion);
                }else{
                    suggestion = possibilities.get(randInt);
                    possibilities.remove(randInt); //removes word from possibilies once it has been suggested
                    System.out.println("Suggestion: " + suggestion);
                }

                System.out.println("Greys:      " + greys + "\n--------------------");
            }
        }
        sc.close();

        if(winner){
            System.out.println("\nCongratulations!! You won in " + (6 - numofGuesses) + (6 - numofGuesses == 1 ? " guess\n" : " guesses\n"));
        }else{
            System.out.println(":(\nThe word was: " + chosenWord + "!");
        }

    }
    //getGreys method returns a string of chars that were in user input, but are not present anywhere in goal word
    public static String getGreys(String guess, String goal, String grey){ 
        String output = "";
        for(int i = 0; i < guess.length(); i++){
            if(goal.indexOf(guess.charAt(i)) < 0 && grey.indexOf(guess.charAt(i)) < 0 && output.indexOf(guess.charAt(i)) < 0) //checks for duplicate letters in 'grey' string & checks absence of chars
                output += guess.charAt(i);
        }
        return output;
    }
    //checkValidity checks whether user input words are valid
    public static boolean checkValidity(String guess){
        Dictionary allowableWords = new Dictionary("/Users/stephengallagher/Desktop/Wordle/allowableWords.txt");
        for(int i = 0; i < allowableWords.getSize() - 1; i++){
            if(guess.equalsIgnoreCase(allowableWords.getWord(i)))
                return true;
        }
        return false;
    }
    //printGreens returns chars that are prese t and in the correct place
    public static String printGreens(String player, String goal){
        String output = "";

        for(int i = 0; i < player.length(); i++){
            if(player.charAt(i) == goal.charAt(i)){
                output += player.charAt(i);
            }else{
                output += '_';
            }
        }
        return output;
    }
    //printGolds returns chars that are present but not in the correct place
    public static String printGolds(String player, String goal, String greens){
        String output = "";
        String prevGolds = "";

        for(int i = 0; i < player.length(); i++){
            if(goal.indexOf(player.charAt(i)) >= 0 && (greens.charAt(i) == '_')){
                output += player.charAt(i);
            }else{
                output += "_";
            }
        }
        return output;
    }
    //returnSuggestion method returns an arraylist of possible words. Based on previous grey chars and green/gold positions and chars
    public static ArrayList<String> returnSuggestion(String greens, String golds, String playerInput, ArrayList<String> possibilities, String greys){
        if(possibilities.size() == 1) //returns if only one value is present in the list
          return possibilities;

        String tempGreen = greens.replaceAll("[^a-zA-Z]", ""); //condenses green and gold strings into just letters
        String tempGold = golds.replaceAll("[^a-zA-Z]", "");

        ArrayList<String> output = new ArrayList<String>();
        output = possibilities;

        for(int i = 0; i < greys.length(); i++){  //filters out words that contain grey letters
            char temp = greys.charAt(i);
            output.removeIf(n -> (n.indexOf(temp) >= 0));
        }

        for(int x = 0; x < tempGreen.length(); x++){ //green filter
            String green = tempGreen.substring(x, x + 1);
            int pos = greens.indexOf(green);

            output.removeIf(n -> (!(n.contains(green)))); //removes if word does not contain a green letter
            output.removeIf(n -> (n.indexOf(green)) != pos); //removes if green is in incorrect position
        }
        for(int x = 0; x < tempGold.length(); x++){ //filter golds
            String gold = tempGold.substring(x, x + 1);
            int pos = golds.indexOf(gold);

            output.removeIf(n -> (!(n.contains(gold)) || n.indexOf(gold) == pos));  //removes if word does not contain gold letter, or if char is in same position as gold guess
        }
        return output;
    }

    //used to access both allowed words and possible wordle words
    public static class Dictionary{
     
        private String input[]; 
    
        public Dictionary(String filePath){ //small edit to class, that allows string input parameter for filepath
            input = load(filePath);
        }
        public int getSize(){
            return input.length;
        }
        public String getWord(int n){
            return input[n];
        }
        
        private String[] load(String file) {
            File aFile = new File(file);     
            StringBuffer contents = new StringBuffer();
            BufferedReader input = null;
            try {
                input = new BufferedReader( new FileReader(aFile) );
                String line = null; 
                int i = 0;
                while (( line = input.readLine()) != null){
                    contents.append(line);
                    i++;
                    contents.append(System.getProperty("line.separator"));
                }
            }catch (FileNotFoundException ex){
                System.out.println("Can't find the file - are you sure the file is in this location: "+file);
                ex.printStackTrace();
            }catch (IOException ex){
                System.out.println("Input output exception while processing file");
                ex.printStackTrace();
            }finally{
                try {
                    if (input!= null) {
                        input.close();
                    }
                }catch (IOException ex){
                    System.out.println("Input output exception while processing file");
                    ex.printStackTrace();
                }
            }
            String[] array = contents.toString().split("\n");
            for(String s: array){
                s.trim();
            }
            return array;
        }
    }
}

