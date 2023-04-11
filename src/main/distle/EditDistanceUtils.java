package main.distle;

import java.util.*;

public class EditDistanceUtils {

    /**
     * Returns the completed Edit Distance memoization structure, a 2D array
     * of ints representing the number of string manipulations required to minimally
     * turn each subproblem's string into the other.
     * 
     * @param s0 String to transform into other
     * @param s1 Target of transformation
     * @return Completed Memoization structure for editDistance(s0, s1)
     */
    public static int[][] getEditDistTable(String s0, String s1) {

        // >> [AF] Poor variable names here that could be improved to indicate their contents
        // and purpose; use row and col instead of single-letter placeholders like i, j, and
        // rowLen and colLen rather than len0 and len1 so it orients the dimensions relative the table (-1)
        int len0 = s0.length();
        int len1 = s1.length();

        // >> [AF] A little overengineered here, as discussed -- and wait, it looks like you never
        // use this? Remove dead code before submission (-0.5)
        Map<String, Integer> transformPriority = new HashMap<>();
        transformPriority.put("R", 1); // Replacement
        transformPriority.put("T", 2); // Transposition
        transformPriority.put("I", 3); // Insertion
        transformPriority.put("D", 4); // Deletion

        int[][] table = new int[len0 + 1][len1 + 1];

        for (int i = 0; i <= len0; i++) {
            table[i][0] = i;
        }

        for (int j = 1; j <= len1; j++) {
            table[0][j] = j;
        }

        for (int i = 1; i <= len0; i++) {
            for (int j = 1; j <= len1; j++) {
                int cost = (s0.charAt(i - 1) == s1.charAt(j - 1)) ? 0 : 1;
                table[i][j] = Math.min(table[i - 1][j] + 1, Math.min(table[i][j - 1] + 1, table[i - 1][j - 1] + cost));
                if (i > 1 && j > 1 && s0.charAt(i - 1) == s1.charAt(j - 2) && s0.charAt(i - 2) == s1.charAt(j - 1)) {
                    table[i][j] = Math.min(table[i][j], table[i - 2][j - 2] + cost);
                }
            }
        }

        return table;
    }

    /**
     * Returns one possible sequence of transformations that turns String s0
     * into s1. The list is in top-down order (i.e., starting from the largest
     * subproblem in the memoization structure) and consists of Strings representing
     * the String manipulations of:
     * <ol>
     * <li>"R" = Replacement</li>
     * <li>"T" = Transposition</li>
     * <li>"I" = Insertion</li>
     * <li>"D" = Deletion</li>
     * </ol>
     * In case of multiple minimal edit distance sequences, returns a list with
     * ties in manipulations broken by the order listed above (i.e., replacements
     * preferred over transpositions, which in turn are preferred over insertions,
     * etc.)
     * 
     * @param s0    String transforming into other
     * @param s1    Target of transformation
     * @param table Precomputed memoization structure for edit distance between s0,
     *              s1
     * @return List that represents a top-down sequence of manipulations required to
     *         turn s0 into s1, e.g., ["R", "R", "T", "I"] would be two replacements
     *         followed
     *         by a transposition, then insertion.
     */
    // >> [AF] JavaDocs must be placed directly above the method they document (no space in between),
    // but you added one here (-0.25)

    public static List<String> getTransformationList(String s0, String s1, int[][] table) {
        List<String> transformations = new ArrayList<>();
        int len0 = s0.length();
        int len1 = s1.length();
        // >> [AF] Uhh... why not just rename len0 and len1?
        int row = len0;
        int col = len1;
        while (row > 0 || col > 0) {
            if (row > 0 && col > 0 && s0.charAt(row - 1) == s1.charAt(col - 1)) {
                row--;
                col--;
                continue;
            }
            int cost = Integer.MAX_VALUE;
            int replaceCost = Integer.MAX_VALUE;
            int insertCost = Integer.MAX_VALUE;
            int deleteCost = Integer.MAX_VALUE;
            int transposeCost = Integer.MAX_VALUE;
            if (row > 0 && col > 0) {
                replaceCost = table[row - 1][col - 1];
                cost = replaceCost;
            }
            if (row > 1 && col > 1 && s0.charAt(row - 2) == s1.charAt(col - 1)
                    && s0.charAt(row - 1) == s1.charAt(col - 2)) {
                transposeCost = table[row - 2][col - 2];
                // >> [AF] You shouldn't need to compare for minimums here at all since you KNOW
                // that the table's entry at the current row and column IS the minimum (assuming it was
                // completed correctly before being given to this method as an argument); instead, just
                // check if the current cell is the same as one of the candidate sources in the table
                // (making sure to add +1 where appropriate) (-0.5)
                if (transposeCost < cost) {
                    cost = transposeCost;
                }
            }
            if (col > 0) {
                insertCost = table[row][col - 1];
                if (insertCost < cost) {
                    cost = insertCost;
                }
            }
            if (row > 0) {
                deleteCost = table[row - 1][col];
                if (deleteCost < cost) {
                    cost = deleteCost;
                }
            }
            if (replaceCost == cost) {
                transformations.add("R");
                row--;
                col--;
            } else if (transposeCost == cost) {
                transformations.add("T");
                col -= 2;
                row -= 2;
            } else if (insertCost == cost) {
                transformations.add("I");
                col--;
            } else {
                transformations.add("D");
                row--;
            }
        }
        return transformations;
    }

    /**
     * Returns the edit distance between the two given strings: an int
     * representing the number of String manipulations (Insertions, Deletions,
     * Replacements, and Transpositions) minimally required to turn one into
     * the other.
     * 
     * @param s0 String to transform into other
     * @param s1 Target of transformation
     * @return The minimal number of manipulations required to turn s0 into s1
     */
    public static int editDistance(String s0, String s1) {
        if (s0.equals(s1)) {
            return 0;
        }
        return getEditDistTable(s0, s1)[s0.length()][s1.length()];
    }

    /**
     * See {@link #getTransformationList(String s0, String s1, int[][] table)}.
     */
    public static List<String> getTransformationList(String s0, String s1) {
        return getTransformationList(s0, s1, getEditDistTable(s0, s1));
    }
}

// ===================================================
// >>> [AF] Summary
// Excellent submission that has a ton to like and was
// obviously well-tested. Generally clean style (apart
// from a few quibbles noted above), and shows
// strong command of programming foundations alongside
// data structure and algorithmic concepts. Globally, I'd
// recommend giving yourself time to lint your code for
// more stylistically appropriate implementations and to
// step back whenever you feel yourself doing something
// complicated to refactor, but by George it's hard to
// argue with results -- nice 3rd place finish in the
// DistlePlayer "contest." Keep up the great work!
// ---------------------------------------------------
// >>> [AF] Style Checklist
// [X] = Good, [~] = Mixed bag, [ ] = Needs improvement
//
// [~] Variables and helper methods named and used well
// [~] Proper and consistent indentation and spacing
// [X] Proper JavaDocs provided for ALL methods
// [ ] Logic is adequately simplified
// [X] Code repetition is kept to a minimum
// ---------------------------------------------------
// Correctness:          100 / 100
// -> EditDistUtils:      20 / 20  (-2 / missed test)
// -> DistlePlayer:      290 / 265 (-0.5 / below threshold; max -30)
// Style Penalty:      -2.25
// Total:              97.75 / 100
// ===================================================
