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
        Map<String, Integer> bigramCounts = new HashMap<>();

        for (String word : dictionary) {
            for (int i = 0; i < word.length() - 1; i++) {
                String bigram = word.substring(i, i + 2);
                bigramCounts.put(bigram, bigramCounts.getOrDefault(bigram, 0) + 1);
            }
        }

        List<String> sortedWords = new ArrayList<>(dictionary);
        sortedWords.sort((a, b) -> {
            double aWeight = 0;
            double bWeight = 0;

            for (int i = 0; i < a.length() - 1; i++) {
                String bigram = a.substring(i, i + 2);
                aWeight += bigramCounts.getOrDefault(bigram, 0);
            }

            for (int i = 0; i < b.length() - 1; i++) {
                String bigram = b.substring(i, i + 2);
                bWeight += bigramCounts.getOrDefault(bigram, 0);
            }

            return Double.compare(bWeight, aWeight);
        });

        for (String word : sortedWords) {
            if (!guessedWords.contains(word)) {
                guessedWords.add(word);
                return word;
            }
        }

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
        AtomicReference<Double> minEntropy = new AtomicReference<>(Double.POSITIVE_INFINITY);
        Set<String> validWords = new HashSet<>();
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
        dictionary = validWords.stream().filter(word -> getEntropy(word, editDistance) <= minEntropy.get())
                .collect(Collectors.toSet());
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
}
