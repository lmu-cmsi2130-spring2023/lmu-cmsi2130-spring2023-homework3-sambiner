package main.distle;

import static main.distle.EditDistanceUtils.*;
import java.util.*;

/**
 * AI Distle Player! Contains all logic used to automagically play the game of
 * Distle with frightening accuracy (hopefully).
 */
public class DistlePlayer {

    private Set<String> dictionary;
    private int maxGuesses;
    private int guesses;
    private String word;
    private int editDistance;
    private List<String> transforms;
    private List<String> minWords;
    private Map<String, Integer> maxWordCounts;

    /**
     * Constructs a new DistlePlayer.
     * [!] You MAY NOT change this signature, meaning it may not accept any
     * arguments.
     * Still, you can use this constructor to initialize any fields that need to be,
     * though you may prefer to do this in the {@link #startNewGame(Set<String>
     * dictionary, int maxGuesses)}
     * method.
     */
    public DistlePlayer() {
        this.dictionary = new HashSet<>();
        this.maxGuesses = 0;
        this.guesses = 0;
        this.word = null;
        this.editDistance = Integer.MAX_VALUE;
        this.transforms = new ArrayList<>();
        this.minWords = new ArrayList<>();
        this.maxWordCounts = new HashMap<>();
    }

    /**
     * Called at the start of every new game of Distle, and parameterized by the
     * dictionary composing all possible words that can be used as guesses / one of
     * which is the correct.
     * 
     * @param dictionary The dictionary from which the correct answer and guesses
     *                   can be drawn.
     * @param maxGuesses The max number of guesses available to the player.
     */
    public void startNewGame(Set<String> dictionary, int maxGuesses) {
        this.dictionary = dictionary;
        this.maxGuesses = maxGuesses;
        this.guesses = 0;
        this.word = null;
        this.editDistance = Integer.MAX_VALUE;
        this.transforms = new ArrayList<>();
        this.minWords = new ArrayList<>();
        this.maxWordCounts = new HashMap<>();
    }

    /**
     * Requests a new guess to be made in the current game of Distle. Uses the
     * DistlePlayer's fields to arrive at this decision.
     * 
     * @return The next guess from this DistlePlayer.
     */
    public String makeGuess() {
        if (this.guesses == 0) {
            this.word = this.dictionary.stream().findFirst().orElse(null);
        } else {
            List<String> possibleWords = getPossibleWords();
            int maxCount = 0;
            for (String w : possibleWords) {
                int count = getWordCount(w);
                if (count > maxCount) {
                    maxCount = count;
                    this.word = w;
                }
            }
        }
        this.guesses++;
        return this.word;
    }

    /**
     * @return The possible words that could be the secret word.
     */
    private List<String> getPossibleWords() {
        List<String> possibleWords = new ArrayList<>();

        for (String w : this.dictionary) {
            if (getEditDistance(this.word, w) <= this.editDistance) {
                possibleWords.add(w);
            }
        }

        return possibleWords;
    }

    private int getWordCount(String word) {

        if (!this.maxWordCounts.containsKey(word)) {
            int count = 0;

            for (String w : this.dictionary) {
                if (getEditDistance(word, w) <= this.editDistance) {
                    count++;
                }
            }

            this.maxWordCounts.put(word, count);
        }

        return this.maxWordCounts.get(word);
    }

    /**
     * Calculates the edit distance between two words. The edit distance is the
     * number of edits needed to turn one word into the other. An edit can be one of
     * the following:
     * <ul>
     * <li>Insertion of a character</li>
     * <li>Deletion of a character</li>
     * <li>Replacement of a character</li>
     * <li>Transposition of two adjacent characters</li>
     * </ul>
     * 
     * @param word1
     * @param word2
     * @return The edit distance between the two words.
     */
    private int getEditDistance(String word1, String word2) {
        int m = word1.length();
        int n = word2.length();
        int[][] table = new int[m + 1][n + 1];

        for (int i = 0; i <= m; i++) {
            table[i][0] = 0;
        }

        for (int j = 0; j <= n; j++) {
            table[0][j] = 0;
        }

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                int cost = word1.charAt(i - 1) == word2.charAt(j - 1) ? 0 : 1;
                table[i][j] = Math.min(Math.min(table[i - 1][j] + 1, table[i][j - 1] + 1), table[i - 1][j - 1] + cost);
            }
        }
        return table[m][n];
    }

    /**
     * Called by the DistleGame after the DistlePlayer has made an incorrect guess.
     * The
     * feedback furnished is as follows:
     * <ul>
     * <li>guess, the player's incorrect guess (repeated here for convenience)</li>
     * <li>editDistance, the numerical edit distance between the guess and secret
     * word</li>
     * <li>transforms, a list of top-down transforms needed to turn the guess into
     * the secret word</li>
     * </ul>
     * [!] This method should be used by the DistlePlayer to update its fields and
     * plan for
     * the next guess to be made.
     * 
     * @param guess        The last, incorrect, guess made by the DistlePlayer
     * @param editDistance Numerical distance between the guess and the secret word
     * @param transforms   List of top-down transforms needed to turn the guess into
     *                     the secret word
     */
    public void getFeedback(String guess, int editDistance, List<String> transforms) {
        if (editDistance < this.editDistance) {
            this.editDistance = editDistance;
            this.transforms = transforms;
            this.minWords = new ArrayList<>();
            this.minWords.add(guess);
            Set<Character> excludeChars = new HashSet<>();
            boolean excludeTransposed = false;

            for (String transform : transforms) {
                if (transform.startsWith("-")) {
                    excludeChars.add(transform.charAt(1));
                } else if (transform.equals("T")) {
                    excludeTransposed = true;
                }
            }

            Iterator<String> it = dictionary.iterator();
            while (it.hasNext()) {
                String word = it.next();

                for (char ch : excludeChars) {
                    if (word.indexOf(ch) >= 0) {
                        it.remove();
                        break;
                    }
                }

                if (excludeTransposed && getTransformationList(guess, word).contains("T")) {
                    it.remove();
                }
            }

        } else if (editDistance == this.editDistance) {
            this.minWords.add(guess);
        }
    }
}
