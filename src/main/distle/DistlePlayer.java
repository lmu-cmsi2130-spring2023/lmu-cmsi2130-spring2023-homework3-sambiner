package main.distle;

import static main.distle.EditDistanceUtils.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

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
     * dictionary, int maxGuesses)} method.
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
     * Uses maps to store character frequencies and word lengths, and then finds the
     * most frequent word length. Then, it finds the most frequent characters in
     * words of that length. It then scores all remaining words of that length and
     * selects the word with the highest score that has not been guessed before.
     * 
     * @return The next guess from this DistlePlayer.
     */

    public String makeGuess() {
        Map<Character, Integer> freqMap = new HashMap<>();
        Map<Integer, Set<String>> lengthMap = new HashMap<>();
        for (String word : dictionary) {
            int length = word.length();
            lengthMap.putIfAbsent(length, new HashSet<>());
            lengthMap.get(length).add(word);
        }
        int mostFrequentWordLength = -1;
        int maxCount = -1;
        for (Map.Entry<Integer, Set<String>> entry : lengthMap.entrySet()) {
            if (entry.getValue().size() > maxCount) {
                maxCount = entry.getValue().size();
                mostFrequentWordLength = entry.getKey();
            }
        }
        Set<String> mostFrequentLengthWords = lengthMap.get(mostFrequentWordLength);
        for (String word : mostFrequentLengthWords) {
            for (char c : word.toCharArray()) {
                freqMap.put(c, freqMap.getOrDefault(c, 0) + 1);
            }
        }

        // Sort character frequencies in descending order
        List<Map.Entry<Character, Integer>> sortedFrequencies = new ArrayList<>(freqMap.entrySet());
        sortedFrequencies.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        // Score all remaining words of most frequent length
        String bestGuess = null;
        int bestGuessScore = -1;
        for (String word : mostFrequentLengthWords) {
            int score = 0;
            for (Map.Entry<Character, Integer> entry : sortedFrequencies) {
                char c = entry.getKey();
                if (word.indexOf(c) >= 0) {
                    score += entry.getValue();
                }
            }
            // Select word with highest score that has not been guessed before
            if (score > bestGuessScore && !guessedWords.contains(word)) {
                bestGuessScore = score;
                bestGuess = word;
            }
        }

        guessedWords.add(bestGuess);
        return bestGuess;
    }

    /**
     * Called by the DistleGame after the DistlePlayer has made an incorrect guess.
     * The feedback furnished is as follows:
     * <ul>
     * <li>guess, the player's incorrect guess (repeated here for convenience)</li>
     * <li>editDistance, the numerical edit distance between the guess and secret
     * word</li>
     * <li>transforms, a list of top-down transforms needed to turn the guess into
     * the secret word</li>
     * </ul>
     * [!] This method should be used by the DistlePlayer to update its fields and
     * plan for the next guess to be made.
     * 
     * @param guess        The last, incorrect, guess made by the DistlePlayer
     * @param editDistance Numerical distance between the guess and the secret word
     * @param transforms   List of top-down transforms needed to turn the guess into
     *                     the secret word
     */
    public void getFeedback(String guess, int editDistance, List<String> transforms) {

        // Update dictionary to only contain words that can be transformed into the
        // guess
        AtomicReference<Double> minEntropy = new AtomicReference<>(Double.POSITIVE_INFINITY);
        Set<String> validWords = new HashSet<>();

        // Iterate through dictionary and add words that can be transformed into the
        // guess to validWords
        for (String word : dictionary) {
            List<String> transformations = getTransformationList(guess, word);
            if (transformations.equals(transforms)) {
                validWords.add(word);
                double entropy = getEntropy(word, editDistance);
                if (entropy < minEntropy.get()) {
                    minEntropy.set(entropy);
                }
            }
        }

        // Update dictionary to only contain words with entropy less than or equal to
        // the minimum entropy
        dictionary = validWords.stream().filter(word -> getEntropy(word, editDistance) <= minEntropy.get())
                .collect(Collectors.toSet());
    }

    /**
     * Returns a list of top-down transforms needed to turn the guess into the
     * secret word.
     * 
     * @param word         The word that we need to get the entropy of
     * @param editDistance The edit distance between the guess and the secret word
     * @return The entropy of the word based on the edit distance
     */
    private double getEntropy(String word, int editDistance) {
        int wordLength = word.length();
        double logFactorial = 0;
        for (int i = 1; i <= wordLength; i++) {
            logFactorial += Math.log(i);
        }
        double entropy = logFactorial - wordLength * Math.log(26) + editDistance * Math.log(25);
        return entropy;
    }
}
