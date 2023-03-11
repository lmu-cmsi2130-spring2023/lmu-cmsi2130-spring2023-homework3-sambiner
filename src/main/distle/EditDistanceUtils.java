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

        int m = s0.length();
        int n = s1.length();

        Map<String, Integer> transformPriority = new HashMap<>();
        transformPriority.put("R", 1); // Replacement
        transformPriority.put("T", 2); // Transposition
        transformPriority.put("I", 3); // Insertion
        transformPriority.put("D", 4); // Deletion

        int[][] table = new int[m + 1][n + 1];

        for (int i = 0; i <= m; i++) {
            table[i][0] = i;
        }

        for (int j = 1; j <= n; j++) {
            table[0][j] = j;
        }

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
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

    public static List<String> getTransformationList(String s0, String s1, int[][] table) {
        List<String> transformations = new ArrayList<>();
        int m = s0.length();
        int n = s1.length();
        int i = m;
        int j = n;
        while (i > 0 || j > 0) {
            if (i > 0 && j > 0 && s0.charAt(i - 1) == s1.charAt(j - 1)) {
                i--;
                j--;
                continue;
            }
            int cost = Integer.MAX_VALUE;
            int replaceCost = Integer.MAX_VALUE;
            int insertCost = Integer.MAX_VALUE;
            int deleteCost = Integer.MAX_VALUE;
            int transposeCost = Integer.MAX_VALUE;
            if (i > 0 && j > 0) {
                replaceCost = table[i - 1][j - 1];
                cost = replaceCost;
            }
            if (i > 1 && j > 1 && s0.charAt(i - 2) == s1.charAt(j - 1) && s0.charAt(i - 1) == s1.charAt(j - 2)) {
                transposeCost = table[i - 2][j - 2];
                if (transposeCost < cost) {
                    cost = transposeCost;
                }
            }
            if (j > 0) {
                insertCost = table[i][j - 1];
                if (insertCost < cost) {
                    cost = insertCost;
                }
            }
            if (i > 0) {
                deleteCost = table[i - 1][j];
                if (deleteCost < cost) {
                    cost = deleteCost;
                }
            }
            if (replaceCost == cost) {
                transformations.add("R");
                i--;
                j--;
            } else if (transposeCost == cost) {
                transformations.add("T");
                j -= 2;
                i -= 2;
            } else if (insertCost == cost) {
                transformations.add("I");
                j--;
            } else {
                transformations.add("D");
                i--;
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
