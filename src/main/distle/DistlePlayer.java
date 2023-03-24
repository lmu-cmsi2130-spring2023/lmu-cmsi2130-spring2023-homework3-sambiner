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
     * dictionary, int maxGuesses)} method.
     */
    public DistlePlayer() {

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

        // Create a map of character frequencies and a map of word lengths
        Map<Character, Integer> freqMap = new HashMap<>();
        Map<Integer, Set<String>> lengthMap = new HashMap<>();
        for (String word : dictionary) {
            int length = word.length();
            lengthMap.putIfAbsent(length, new HashSet<>());
            lengthMap.get(length).add(word);
        }

        // Find the most frequent word length
        int mostFrequentWordLength = -1;
        int maxCount = -1;
        for (Map.Entry<Integer, Set<String>> entry : lengthMap.entrySet()) {
            if (entry.getValue().size() > maxCount) {
                maxCount = entry.getValue().size();
                mostFrequentWordLength = entry.getKey();
            }
        }

        // Find the most frequent characters in words of the most frequent length
        Set<String> mostFrequentLengthWords = lengthMap.get(mostFrequentWordLength);
        for (String word : mostFrequentLengthWords) {
            for (char c : word.toCharArray()) {
                freqMap.put(c, freqMap.getOrDefault(c, 0) + 1);
            }
        }

        List<Map.Entry<Character, Integer>> sortedFrequencies = new ArrayList<>(freqMap.entrySet());
        sortedFrequencies.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        String bestGuess = null;
        int bestGuessScore = -1;

        // Score each word based on how many of the most frequent characters it has
        for (String word : mostFrequentLengthWords) {
            int score = 0;
            for (Map.Entry<Character, Integer> entry : sortedFrequencies) {
                char c = entry.getKey();
                if (word.indexOf(c) >= 0) {
                    score += entry.getValue();
                }
            }
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
        Set<String> validWords = new HashSet<>();
        for (String word : dictionary) {
            List<String> transformations = getTransformationList(guess, word);
            if (transformations.equals(transforms)) {
                validWords.add(word);
            }
        }

        Map<Integer, int[][]> wordLengthFreqs = precomputeFrequencies(validWords);

        PriorityQueue<String> pq = new PriorityQueue<>(
                (a, b) -> Double.compare(calculateEntropy(a, wordLengthFreqs, validWords),
                        calculateEntropy(b, wordLengthFreqs, validWords)));

        pq.addAll(validWords);

        dictionary = new LinkedHashSet<>(pq);
    }

    /**
     * Precomputes the frequencies of characters in words of the same length.
     * 
     * @param validWords: A set of words that are valid guesses.
     * @return wordLengthFreqs: A map from word length to a 2D array of character
     *         frequencies.
     */

    private Map<Integer, int[][]> precomputeFrequencies(Set<String> validWords) {
        Map<Integer, int[][]> wordLengthFreqs = new HashMap<>();

        for (String word : validWords) {
            int wordLength = word.length();
            if (!wordLengthFreqs.containsKey(wordLength)) {
                wordLengthFreqs.put(wordLength, new int[wordLength][26]);
            }

            int[][] freqs = wordLengthFreqs.get(wordLength);
            for (int i = 0; i < wordLength; i++) {
                if (word.charAt(i) != '-') {
                    freqs[i][word.charAt(i) - 'a']++;
                }
            }
        }

        return wordLengthFreqs;
    }

    /**
     * Calculates the entropy of a word given the frequencies of characters in words
     * of the same length.
     * 
     * @param word:            The word to calculate the entropy of
     * @param wordLengthFreqs: The frequencies of characters in words of the same
     *                         length
     * @param validWords:      The set of valid words
     * @return entropy: The entropy of the word.
     */

    private double calculateEntropy(String word, Map<Integer, int[][]> wordLengthFreqs, Set<String> validWords) {
        int wordLength = word.length();
        int[][] freqs = wordLengthFreqs.get(wordLength);

        double entropy = 0;
        for (int i = 0; i < wordLength; i++) {
            if (word.charAt(i) != '-') {
                int charFreq = freqs[i][word.charAt(i) - 'a'];
                if (charFreq != 0) {
                    double probability = (double) charFreq / validWords.size();
                    entropy -= probability * Math.log(probability) / Math.log(2);
                }
            }
        }

        return entropy;
    }

}
