package main.distle;

import static main.distle.EditDistanceUtils.*;
import java.util.*;

/**
 * AI Distle Player! Contains all logic used to automagically play the game of
 * Distle with frightening accuracy (hopefully).
 */
public class DistlePlayer {
    private Set<String> dictionary;
    private List<String> guessedWords;

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
        this.guessedWords = new ArrayList<>();
    }

    /**
     * Requests a new guess to be made in the current game of Distle. Uses the
     * DistlePlayer's fields to arrive at this decision.
     * 
     * @return The next guess from this DistlePlayer.
     */

    public String makeGuess() {
        // Calculate letter frequencies for remaining words in dictionary
        Map<Character, Integer> freqMap = new HashMap<>();
        for (String word : dictionary) {
            for (char c : word.toCharArray()) {
                freqMap.put(c, freqMap.getOrDefault(c, 0) + 1);
            }
        }

        // Calculate weighted list of remaining words in dictionary
        List<Pair<String, Double>> weightedWords = new ArrayList<>();
        for (String word : dictionary) {
            double weight = 0;
            for (char c : word.toCharArray()) {
                weight += freqMap.getOrDefault(c, 0);
            }
            weightedWords.add(new Pair<>(word, weight));
        }

        // Sort list of remaining words by weight in descending order
        weightedWords.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

        // Select highest-weight word that has not been guessed
        for (Pair<String, Double> pair : weightedWords) {
            String word = pair.getKey();
            if (!guessedWords.contains(word)) {
                guessedWords.add(word);
                return word;
            }
        }

        // If all words have been guessed, make a random guess
        return dictionary.stream().findFirst().orElse(null);
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
        double minEntropy = Double.POSITIVE_INFINITY;
        for (String word : dictionary) {
            List<String> transformations = getTransformationList(guess, word);
            if (transformations.equals(transforms)) {
                double entropy = getEntropy(word, editDistance);
                if (entropy < minEntropy) {
                    minEntropy = entropy;
                }
            }
        }
        Iterator<String> it = dictionary.iterator();
        while (it.hasNext()) {
            String word = it.next();
            List<String> transformations = getTransformationList(guess, word);
            if (!transformations.equals(transforms)) {
                it.remove();
            } else {
                double entropy = getEntropy(word, editDistance);
                if (entropy > minEntropy) {
                    it.remove();
                }
            }
        }
    }

    private double getEntropy(String word, int editDistance) {
        int wordLength = word.length();
        double logFactorial = 0;
        for (int i = 1; i <= wordLength; i++) {
            logFactorial += Math.log(i);
        }
        double entropy = logFactorial - wordLength * Math.log(26) + editDistance * Math.log(25);
        return entropy;
    }

    private class Pair<K, V> {
        private final K key;
        private final V value;

        public Pair(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }
    }
}
