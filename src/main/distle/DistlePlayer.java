package main.distle;

import static main.distle.EditDistanceUtils.*;
import java.util.*;

/**
 * AI Distle Player! Contains all logic used to automagically play the game of
 * Distle with frightening accuracy (hopefully).
 */
public class DistlePlayer {
    private Set<String> dictionary;

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
    }

    /**
     * Requests a new guess to be made in the current game of Distle. Uses the
     * DistlePlayer's fields to arrive at this decision.
     * 
     * @return The next guess from this DistlePlayer.
     */

    public String makeGuess() {
        return this.dictionary.stream().findFirst().orElse(null);
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
        Iterator<String> it = dictionary.iterator();
        while (it.hasNext()) {
            String word = it.next();
            List<String> transformations = getTransformationList(guess, word);
            if (!transformations.equals(transforms)) {
                it.remove();
            }
        }
    }
}
