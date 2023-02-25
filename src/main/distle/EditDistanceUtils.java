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
        int[][] table = new int[m + 1][n + 1];
        for (int i = 0; i <= m; i++) {
            table[i][0] = i;
        }
        for (int j = 0; j <= n; j++) {
            table[0][j] = j;
        }

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (s0.charAt(i - 1) == s1.charAt(j - 1)) {
                    table[i][j] = table[i - 1][j - 1];
                } else {
                    table[i][j] = 1 + Math.min(table[i - 1][j - 1], Math.min(table[i - 1][j], table[i][j - 1]));
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
        List<String> list = new ArrayList<>();
        int i = s0.length();
        int j = s1.length();
        while (i > 0 && j > 0) {
            if (s0.charAt(i - 1) == s1.charAt(j - 1)) {
                i--;
                j--;
            } else if (table[i][j] == table[i - 1][j - 1] + 1) {
                list.add("R");
                i--;
                j--;
            } else if (table[i][j] == table[i - 2][j - 2] + 1) {
                list.add("T");
                i -= 2;
                j -= 2;
            } else if (table[i][j] == table[i][j - 1] + 1) {
                list.add("I");
                j--;
            } else if (table[i][j] == table[i - 1][j] + 1) {
                list.add("D");
                i--;
            }
        }
        while (i > 0) {
            list.add("D");
            i--;
        }
        while (j > 0) {
            list.add("I");
            j--;
        }
        Collections.reverse(list);
        return list;
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
