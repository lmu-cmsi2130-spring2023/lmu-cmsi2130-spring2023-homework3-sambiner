package test.distle;

import static main.distle.EditDistanceUtils.*;
import static org.junit.Assert.*;
import java.util.*;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestWatcher;
import org.junit.rules.Timeout;
import org.junit.runner.Description;

public class EditDistanceUtilsTests {

    // =================================================
    // Test Configuration
    // =================================================

    // Global timeout to prevent infinite loops from
    // crashing the test suite
    // [!] You might want to comment these lines out while
    // developing, just so you know whether or not you're
    // inefficient or bugged!
    @Rule
    public Timeout globalTimeout = Timeout.seconds(2);

    // Grade record-keeping
    static int possible = 0, passed = 0;

    // the @Before method is run before every @Test
    @Before
    public void init() {
        possible++;
    }

    // Each time you pass a test, you get a point! Yay!
    // [!] Requires JUnit 4+ to run
    @Rule
    public TestWatcher watchman = new TestWatcher() {
        @Override
        protected void succeeded(Description description) {
            passed++;
        }
    };

    // Used for grading, reports the total number of tests
    // passed over the total possible
    @AfterClass
    public static void gradeReport() {
        System.out.println("============================");
        System.out.println("Tests Complete");
        System.out.println(passed + " / " + possible + " passed!");
        if ((1.0 * passed / possible) >= 0.9) {
            System.out.println("[!] Nice job!"); // Automated acclaim!
        }
        System.out.println("============================");
    }

    // =================================================
    // Unit Tests
    // =================================================

    // Edit Distance Tests
    // -------------------------------------------------

    @Test
    public void editDist_t0() {
        assertEquals(0, editDistance("", ""));
        assertEquals(0, editDistance("a", "a"));
        assertEquals(0, editDistance("abc", "abc"));
    }

    @Test
    public void editDist_t1() {
        assertEquals(1, editDistance("a", ""));
        assertEquals(1, editDistance("", "a"));
        assertEquals(2, editDistance("aa", ""));
        assertEquals(2, editDistance("", "aa"));
        assertEquals(2, editDistance("ab", "abcd"));
    }

    @Test
    public void editDist_t2() {
        assertEquals(1, editDistance("a", "b"));
        assertEquals(1, editDistance("b", "a"));
        assertEquals(2, editDistance("ab", "cd"));
        assertEquals(3, editDistance("cat", "dog"));
    }

    @Test
    public void editDist_t3() {
        System.out.print("ab and ba have an edit distance of: ");
        System.out.println(editDistance("ab", "ba"));
        assertEquals(1, editDistance("ab", "ba"));
        System.out.println(editDistance("bar", "bra"));
        assertEquals(1, editDistance("bar", "bra"));
    }

    @Test
    public void editDist_t4() {
        System.out.println(editDistance("parisss", "parsimony"));
        assertEquals(5, editDistance("parisss", "parsimony"));
    }

    @Test
    public void editDist_t5() {
        System.out.println(editDistance("wxyyxw", "wyxxyx"));
        assertEquals(3, editDistance("wxyyxw", "wyxxyx"));
    }

    @Test
    public void editDist_t6() {
        System.out.println(editDistance("abcde", "edbca"));
        assertEquals(4, editDistance("abcde", "edbca"));
    }

    @Test
    public void editDist_t7() {
        System.out.println(editDistance("aaaabcde", "aaaedbca"));
        assertEquals(4, editDistance("aaaabcde", "aaaedbca"));
    }

    // Transform List Tests
    // -------------------------------------------------

    @Test
    public void transformList_t0() {
        String s0 = "",
                s1 = "";
        System.out.print("s0 -> s1 = ");
        System.out.println(getTransformationList(s0, s1));
        assertEquals(Arrays.asList(), getTransformationList(s0, s1));
    }

    @Test
    public void transformList_t1() {
        String s0 = "a",
                s1 = "";
        System.out.print("s0 -> s1 = ");
        System.out.println(getTransformationList(s0, s1));
        System.out.print("s1 -> s0 = ");
        System.out.println(getTransformationList(s1, s0));
        assertEquals(Arrays.asList("D"), getTransformationList(s0, s1));
        assertEquals(Arrays.asList("I"), getTransformationList(s1, s0));
    }

    @Test
    public void transformList_t2() {
        String s0 = "abc",
                s1 = "";
        System.out.print("s0 -> s1 = ");
        System.out.println(getTransformationList(s0, s1));
        System.out.print("s1 -> s0 = ");
        System.out.println(getTransformationList(s1, s0));
        assertEquals(Arrays.asList("D", "D", "D"), getTransformationList(s0, s1));
        assertEquals(Arrays.asList("I", "I", "I"), getTransformationList(s1, s0));
    }

    @Test
    public void transformList_t3() {
        String s0 = "abc",
                s1 = "bac";
        System.out.print("s0 -> s1 = ");
        System.out.println(getTransformationList(s0, s1));
        System.out.print("s1 -> s0 = ");
        System.out.println(getTransformationList(s1, s0));
        assertEquals(Arrays.asList("T"), getTransformationList(s0, s1));
        assertEquals(Arrays.asList("T"), getTransformationList(s1, s0));
    }

    @Test
    public void transformList_t4() {
        String s0 = "aaa",
                s1 = "bbb";
        System.out.print("s0 -> s1 = ");
        System.out.println(getTransformationList(s0, s1));
        System.out.print("s1 -> s0 = ");
        System.out.println(getTransformationList(s1, s0));
        assertEquals(Arrays.asList("R", "R", "R"), getTransformationList(s0, s1));
        assertEquals(Arrays.asList("R", "R", "R"), getTransformationList(s1, s0));
    }

    @Test
    public void transformList_t5() {
        String s0 = "eagle",
                s1 = "bagle";
        System.out.print("s0 -> s1 = ");
        System.out.println(getTransformationList(s0, s1));
        System.out.print("s1 -> s0 = ");
        System.out.println(getTransformationList(s1, s0));
        assertEquals(Arrays.asList("R"), getTransformationList(s0, s1));
        assertEquals(Arrays.asList("R"), getTransformationList(s1, s0));
    }

    @Test
    public void transformList_t6() {
        String s0 = "hack",
                s1 = "fkc";
        System.out.print("s0 -> s1 = ");
        System.out.println(getTransformationList(s0, s1));
        System.out.print("s1 -> s0 = ");
        System.out.println(getTransformationList(s1, s0));
        assertEquals(Arrays.asList("T", "R", "D"), getTransformationList(s0, s1));
        assertEquals(Arrays.asList("T", "R", "I"), getTransformationList(s1, s0));
    }

    @Test
    public void transformList_t7() {
        String s0 = "intuition",
                s1 = "inception";
        System.out.print("s0 -> s1 = ");
        System.out.println(getTransformationList(s0, s1));
        System.out.print("s1 -> s0 = ");
        System.out.println(getTransformationList(s1, s0));
        assertEquals(Arrays.asList("R", "R", "R"), getTransformationList(s0, s1));
        assertEquals(Arrays.asList("R", "R", "R"), getTransformationList(s1, s0));
    }

    @Test
    public void transformList_t8() {
        String s0 = "astound",
                s1 = "distant";
        System.out.print("s0 -> s1 = ");
        System.out.println(getTransformationList(s0, s1));
        System.out.print("s1 -> s0 = ");
        System.out.println(getTransformationList(s1, s0));
        assertEquals(Arrays.asList("R", "R", "D", "R", "I"), getTransformationList(s0, s1));
        assertEquals(Arrays.asList("R", "R", "I", "R", "D"), getTransformationList(s1, s0));
    }

    @Test
    public void transformList_t9() {
        String s0 = "housemaid",
                s1 = "heartsick";
        System.out.print("s0 -> s1 = ");
        System.out.println(getTransformationList(s0, s1));
        System.out.print("s1 -> s0 = ");
        System.out.println(getTransformationList(s1, s0));
        assertEquals(Arrays.asList("R", "R", "R", "R", "R", "R", "R", "R"), getTransformationList(s0, s1));
        assertEquals(Arrays.asList("R", "R", "R", "R", "R", "R", "R", "R"), getTransformationList(s1, s0));
    }

    @Test
    public void transformList_t10() {
        String s0 = "fullness",
                s1 = "fineness";
        System.out.print("s0 -> s1 = ");
        System.out.println(getTransformationList(s0, s1));
        System.out.print("s1 -> s0 = ");
        System.out.println(getTransformationList(s1, s0));
        assertEquals(Arrays.asList("R", "R", "R"), getTransformationList(s0, s1));
        assertEquals(Arrays.asList("R", "R", "R"), getTransformationList(s1, s0));
    }

    @Test
    public void transformList_t11() {
        String s0 = "axbczy",
                s1 = "abxyzc";
        System.out.print("s0 -> s1 = ");
        System.out.println(getTransformationList(s0, s1));
        System.out.print("s1 -> s0 = ");
        System.out.println(getTransformationList(s1, s0));
        assertEquals(Arrays.asList("R", "R", "T"), getTransformationList(s0, s1));
        assertEquals(Arrays.asList("R", "R", "T"), getTransformationList(s1, s0));
    }

}
