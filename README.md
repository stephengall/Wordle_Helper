# Wordle_Helper
**Wordle bot, capable of narrowing down word possibilities until the correct word is guessed**

"allowableWords.txt" contains words that wordle allow the player to input.  
"wordleWords.txt" contains words that can appear as the word to guess.

**Note: Filepath strings in lines 7, 8 and 97 will need to be edited to run correctly.**

##  Technical Details
**Wordle helper algorithm which takes the following parameters into consideration when narrowing down possible words:**
- Greens - letters that appear in the desired word and are in the correct position.
  
       'for(int x = 0; x < tempGreen.length(); x++){ //green filter
            String green = tempGreen.substring(x, x + 1);
            int pos = greens.indexOf(green);

            output.removeIf(n -> (!(n.contains(green)))); //removes if word does not contain a green letter
            output.removeIf(n -> (n.indexOf(green)) != pos); //removes if green is in incorrect position
        }'  


- Golds - letters that appear in the desired word but are in the incorrect position.


       'for(int x = 0; x < tempGold.length(); x++){ //filter golds
            String gold = tempGold.substring(x, x + 1);
            int pos = golds.indexOf(gold);

            output.removeIf(n -> (!(n.contains(gold)) || n.indexOf(gold) == pos));  //removes if word does not contain gold letter, or if char is in same position as gold guess
        }'  
 
- Greys - letters that do not appear in the word at all.


       'for(int i = 0; i < greys.length(); i++){  //filters out words that contain grey letters
            char temp = greys.charAt(i);
            output.removeIf(n -> (n.indexOf(temp) >= 0));
        }'  

The returnSuggestion method is also given the previously narrowed down list of possible words to reduce number of checks required.

