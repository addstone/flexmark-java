package com.vladsch.flexmark.util.sequence.edit;

import com.vladsch.flexmark.util.collection.iteration.PositionAnchor;
import com.vladsch.flexmark.util.sequence.BasedSequence;
import com.vladsch.flexmark.util.sequence.Range;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.util.List;

import static com.vladsch.flexmark.util.Utils.escapeJavaString;
import static com.vladsch.flexmark.util.sequence.edit.SegmentBuilder2.F_INCLUDE_ANCHORS;
import static com.vladsch.flexmark.util.sequence.edit.SegmentBuilder2.F_TRACK_UNIQUE;
import static org.junit.Assert.assertEquals;

public class BasedSegmentBuilder2Test {
    @Test
    public void test_basicBuildEmpty() {
        String input = "0123456789";
        BasedSequence sequence = BasedSequence.of(input);

        BasedSegmentBuilder2 segments = BasedSegmentBuilder2.emptyBuilder(sequence);

        String expected = "";

        assertEquals("BasedSegmentBuilder2{NULL, s=0:0, u=0:0, t=0:0, l=0 }", escapeJavaString(segments.toStringPrep()));
        assertEquals(segments.toString(sequence).length(), segments.length());

        assertEquals(expected, segments.toStringWithRangesVisibleWhitespace(sequence));
        assertEquals(expected, segments.toString(sequence));
    }

    @Test
    public void test_basicEmptyDefaults() {
        String input = "0123456789";
        BasedSequence sequence = BasedSequence.of(input);

        BasedSegmentBuilder2 segments = BasedSegmentBuilder2.emptyBuilder(sequence);
        segments.append(0, 0);
        segments.append(sequence.length(), sequence.length());

        assertEquals(0, segments.length());
        assertEquals("BasedSegmentBuilder2{[0, 10), s=0:0, u=0:0, t=0:0, l=0, [0), [10) }", escapeJavaString(segments.toStringPrep()));
        assertEquals(segments.toString(sequence).length(), segments.length());
    }

    @Test
    public void test_basicEmptyNoAnchors() {
        String input = "0123456789";
        BasedSequence sequence = BasedSequence.of(input);

        BasedSegmentBuilder2 segments = BasedSegmentBuilder2.emptyBuilder(sequence, ISegmentBuilder.F_TRACK_UNIQUE);
        segments.append(0, 0);
        segments.append(sequence.length(), sequence.length());

        assertEquals(0, segments.length());
        assertEquals("BasedSegmentBuilder2{[0, 10), s=0:0, u=0:0, t=0:0, l=0 }", escapeJavaString(segments.toStringPrep()));
        assertEquals(segments.toString(sequence).length(), segments.length());
    }

    @Test
    public void test_basicEmptyAnchors() {
        String input = "0123456789";
        BasedSequence sequence = BasedSequence.of(input);

        BasedSegmentBuilder2 segments = BasedSegmentBuilder2.emptyBuilder(sequence, ISegmentBuilder.F_TRACK_UNIQUE | ISegmentBuilder.F_INCLUDE_ANCHORS);
        segments.append(0, 0);
        segments.append(sequence.length(), sequence.length());

        assertEquals(0, segments.length());
        assertEquals("BasedSegmentBuilder2{[0, 10), s=0:0, u=0:0, t=0:0, l=0, [0), [10) }", escapeJavaString(segments.toStringPrep()));
        assertEquals(segments.toString(sequence).length(), segments.length());
    }

    @Test
    public void test_basicPrefix() {
        String input = "0123456789";
        BasedSequence sequence = BasedSequence.of(input);

        BasedSegmentBuilder2 segments = BasedSegmentBuilder2.emptyBuilder(sequence);

        segments.append("  ");
        segments.append(0, 4);

        assertEquals("BasedSegmentBuilder2{[0, 4), s=1:2, u=1:2, t=1:2, l=6, '  ', [0, 4) }", escapeJavaString(segments.toStringPrep()));
        assertEquals(segments.toString(sequence).length(), segments.length());

        assertEquals("  ⟦0123⟧", segments.toStringWithRangesVisibleWhitespace(sequence));
        assertEquals("  0123", segments.toString(sequence));
    }

    @Test
    public void test_appendRange1() {
        String input = "0123456789";
        BasedSequence sequence = BasedSequence.of(input);

        BasedSegmentBuilder2 segments = BasedSegmentBuilder2.emptyBuilder(sequence);

        segments.append(0, 4);
        String expected = input.substring(0, 4);

        assertEquals("BasedSegmentBuilder2{[0, 4), s=0:0, u=0:0, t=0:0, l=4, [0, 4) }", escapeJavaString(segments.toStringPrep()));
        assertEquals(segments.toString(sequence).length(), segments.length());

        assertEquals("⟦0123⟧", segments.toStringWithRangesVisibleWhitespace(sequence));
        assertEquals(expected, segments.toString(sequence));
    }

    @Test
    public void test_appendRangeNonOverlapping() {
        String input = "0123456789";
        BasedSequence sequence = BasedSequence.of(input);

        BasedSegmentBuilder2 segments = BasedSegmentBuilder2.emptyBuilder(sequence);

        segments.append(0, 4);
        segments.append(6, 7);
        String expected = input.substring(0, 4) + input.substring(6, 7);

        assertEquals("BasedSegmentBuilder2{[0, 7), s=0:0, u=0:0, t=0:0, l=5, [0, 4), [6, 7) }", escapeJavaString(segments.toStringPrep()));
        assertEquals(segments.toString(sequence).length(), segments.length());

        assertEquals("⟦0123⟧⟦6⟧", segments.toStringWithRangesVisibleWhitespace(sequence));
        assertEquals(expected, segments.toString(sequence));
    }

    @Test
    public void test_appendRangeOverlapping() {
        String input = "0123456789";
        BasedSequence sequence = BasedSequence.of(input);

        BasedSegmentBuilder2 segments = BasedSegmentBuilder2.emptyBuilder(sequence);

        segments.append(0, 5);
        segments.append(3, 7);
        String expected = input.substring(0, 5) + input.substring(3, 7);

        assertEquals("BasedSegmentBuilder2{[0, 7), s=0:0, u=1:2, t=1:2, l=9, [0, 5), '34', [5, 7) }", escapeJavaString(segments.toStringPrep()));
        assertEquals(segments.toString(sequence).length(), segments.length());

        assertEquals("⟦01234⟧34⟦56⟧", segments.toStringWithRangesVisibleWhitespace(sequence));
        assertEquals(expected, segments.toString(sequence));
    }

    @Test
    public void test_appendRangeOverlappingOverString() {
        String input = "0123456789";
        BasedSequence sequence = BasedSequence.of(input);

        BasedSegmentBuilder2 segments = BasedSegmentBuilder2.emptyBuilder(sequence);
        segments.append(0, 5);
        segments.append("abc");
        segments.append(3, 7);
        String expected = input.substring(0, 5) + "abc" + input.substring(3, 7);

        assertEquals("BasedSegmentBuilder2{[0, 7), s=0:0, u=1:5, t=1:5, l=12, [0, 5), 'abc34', [5, 7) }", escapeJavaString(segments.toStringPrep()));
        assertEquals(segments.toString(sequence).length(), segments.length());

        assertEquals("⟦01234⟧abc34⟦56⟧", segments.toStringWithRangesVisibleWhitespace(sequence));
        assertEquals(expected, segments.toString(sequence));
    }

    @Test
    public void test_appendRangeStrings() {
        String input = "0123456789";
        BasedSequence sequence = BasedSequence.of(input);

        BasedSegmentBuilder2 segments = BasedSegmentBuilder2.emptyBuilder(sequence);

        segments.append(0, 5);
        segments.append("abc");
        segments.append("def");
        String expected = input.substring(0, 5) + "abcdef";

        assertEquals("BasedSegmentBuilder2{[0, 5), s=0:0, u=1:6, t=1:6, l=11, [0, 5), 'abcdef' }", escapeJavaString(segments.toStringPrep()));
        assertEquals(segments.toString(sequence).length(), segments.length());

        assertEquals("⟦01234⟧abcdef", segments.toStringWithRangesVisibleWhitespace(sequence));
        assertEquals(expected, segments.toString(sequence));
    }

    @Test
    public void test_appendRangeTouching() {
        String input = "0123456789";
        BasedSequence sequence = BasedSequence.of(input);

        BasedSegmentBuilder2 segments = BasedSegmentBuilder2.emptyBuilder(sequence);

        segments.append(0, 5);
        segments.append(5, 7);
        String expected = input.substring(0, 7);

        assertEquals("BasedSegmentBuilder2{[0, 7), s=0:0, u=0:0, t=0:0, l=7, [0, 7) }", escapeJavaString(segments.toStringPrep()));
        assertEquals(segments.toString(sequence).length(), segments.length());

        assertEquals("⟦0123456⟧", segments.toStringWithRangesVisibleWhitespace(sequence));
        assertEquals(expected, segments.toString(sequence));
    }

    @Test
    public void test_handleOverlapDefaultChop1() {
        String input = "0123456789";
        BasedSequence sequence = BasedSequence.of(input);
        BasedSegmentBuilder2 segments = BasedSegmentBuilder2.emptyBuilder(sequence);

        segments.append(2, 5);
        segments.append("-");
        segments.append(4, 8);
        assertEquals("BasedSegmentBuilder2{[2, 8), s=0:0, u=1:2, t=1:2, l=8, [2, 5), '-4', [5, 8) }", escapeJavaString(segments.toStringPrep()));
        assertEquals("234-4567", segments.toString(sequence));
        assertEquals(segments.toString(sequence).length(), segments.length());
    }

    @Test
    public void test_handleOverlapDefaultChop2() {
        String input = "0123456789";
        BasedSequence sequence = BasedSequence.of(input);
        BasedSegmentBuilder2 segments = BasedSegmentBuilder2.emptyBuilder(sequence);

        segments.append(2, 5);
        segments.append("-");
        segments.append(1, 8);
        assertEquals("BasedSegmentBuilder2{[2, 8), s=0:0, u=1:5, t=1:5, l=11, [2, 5), '-1234', [5, 8) }", escapeJavaString(segments.toStringPrep()));
        assertEquals(segments.toString(sequence).length(), segments.length());
    }

    @Test
    public void test_handleOverlapDefaultChop3() {
        String input = "0123456789";
        BasedSequence sequence = BasedSequence.of(input);
        BasedSegmentBuilder2 segments = BasedSegmentBuilder2.emptyBuilder(sequence);

        segments.append(2, 5);
        segments.append("-");
        segments.append(3, 5);
        assertEquals("BasedSegmentBuilder2{[2, 5), s=0:0, u=1:3, t=1:3, l=6, [2, 5), '-34' }", escapeJavaString(segments.toStringPrep()));
        assertEquals(segments.toString(sequence).length(), segments.length());
    }

    @Test
    public void test_handleOverlapDefaultChop4() {
        String input = "0123456789";
        BasedSequence sequence = BasedSequence.of(input);
        BasedSegmentBuilder2 segments = BasedSegmentBuilder2.emptyBuilder(sequence);

        segments.append(2, 5);
        segments.append("-");
        segments.append(2, 4);
        assertEquals("BasedSegmentBuilder2{[2, 5), s=0:0, u=1:3, t=1:3, l=6, [2, 5), '-23' }", escapeJavaString(segments.toStringPrep()));
        assertEquals(segments.toString(sequence).length(), segments.length());
    }

    @Test
    public void test_handleOverlapDefaultChop5() {
        String input = "0123456789";
        BasedSequence sequence = BasedSequence.of(input);
        BasedSegmentBuilder2 segments = BasedSegmentBuilder2.emptyBuilder(sequence);

        segments.append(2, 5);
        segments.append("-");
        segments.append(2, 5);
        assertEquals("BasedSegmentBuilder2{[2, 5), s=0:0, u=1:4, t=1:4, l=7, [2, 5), '-234' }", escapeJavaString(segments.toStringPrep()));
        assertEquals(segments.toString(sequence).length(), segments.length());
    }

    @Test
    public void test_handleOverlapDefaultChop6() {
        String input = "0123456789";
        BasedSequence sequence = BasedSequence.of(input);
        BasedSegmentBuilder2 segments = BasedSegmentBuilder2.emptyBuilder(sequence);

        segments.append(2, 5);
        segments.append("-");
        segments.append(3, 4);
        assertEquals("BasedSegmentBuilder2{[2, 5), s=0:0, u=1:2, t=1:2, l=5, [2, 5), '-3' }", escapeJavaString(segments.toStringPrep()));
        assertEquals(segments.toString(sequence).length(), segments.length());
    }

    @Test
    public void test_handleOverlapDefaultFromBefore() {
        String input = "0123456789";
        BasedSequence sequence = BasedSequence.of(input);
        BasedSegmentBuilder2 segments = BasedSegmentBuilder2.emptyBuilder(sequence);

        segments.append(2, 6);
        segments.append(0, 1);
        assertEquals("BasedSegmentBuilder2{[2, 6), s=0:0, u=1:1, t=1:1, l=5, [2, 6), '0' }", escapeJavaString(segments.toStringPrep()));
        assertEquals("23450", segments.toStringChars());
        assertEquals(segments.toString(sequence).length(), segments.length());
    }

    @Test
    public void test_handleOverlapDefaultFromBefore0() {
        String input = "0123456789";
        BasedSequence sequence = BasedSequence.of(input);
        BasedSegmentBuilder2 segments = BasedSegmentBuilder2.emptyBuilder(sequence);

        segments.append(2, 6);
        segments.append(0, 2);
        assertEquals("BasedSegmentBuilder2{[2, 6), s=0:0, u=1:2, t=1:2, l=6, [2, 6), '01' }", escapeJavaString(segments.toStringPrep()));
        assertEquals("234501", segments.toStringChars());
        assertEquals(segments.toString(sequence).length(), segments.length());
    }

    @Test
    public void test_handleOverlapDefaultFromBeforeIn() {
        String input = "0123456789";
        BasedSequence sequence = BasedSequence.of(input);
        BasedSegmentBuilder2 segments = BasedSegmentBuilder2.emptyBuilder(sequence);

        segments.append(2, 6);
        segments.append(0, 3);
        assertEquals("BasedSegmentBuilder2{[2, 6), s=0:0, u=1:3, t=1:3, l=7, [2, 6), '012' }", escapeJavaString(segments.toStringPrep()));
        assertEquals("2345012", segments.toStringChars());
        assertEquals(segments.toString(sequence).length(), segments.length());
    }

    @Test
    public void test_handleOverlapDefaultFromBeforeInLess1() {
        String input = "0123456789";
        BasedSequence sequence = BasedSequence.of(input);
        BasedSegmentBuilder2 segments = BasedSegmentBuilder2.emptyBuilder(sequence);

        segments.append(2, 6);
        segments.append(0, 5);
        assertEquals("BasedSegmentBuilder2{[2, 6), s=0:0, u=1:5, t=1:5, l=9, [2, 6), '01234' }", escapeJavaString(segments.toStringPrep()));
        assertEquals("234501234", segments.toStringChars());
        assertEquals(segments.toString(sequence).length(), segments.length());
    }

    @Test
    public void test_handleOverlapDefaultFromBeforeInLess0() {
        String input = "0123456789";
        BasedSequence sequence = BasedSequence.of(input);
        BasedSegmentBuilder2 segments = BasedSegmentBuilder2.emptyBuilder(sequence);

        segments.append(2, 6);
        segments.append(0, 6);
        assertEquals("BasedSegmentBuilder2{[2, 6), s=0:0, u=1:6, t=1:6, l=10, [2, 6), '012345' }", escapeJavaString(segments.toStringPrep()));
        assertEquals("2345012345", segments.toStringChars());
        assertEquals(segments.toString(sequence).length(), segments.length());
    }

    @Test
    public void test_handleOverlapDefaultFromBeforeInOver1() {
        String input = "0123456789";
        BasedSequence sequence = BasedSequence.of(input);
        BasedSegmentBuilder2 segments = BasedSegmentBuilder2.emptyBuilder(sequence);

        segments.append(2, 6);
        segments.append(0, 7);
        assertEquals("BasedSegmentBuilder2{[2, 7), s=0:0, u=1:6, t=1:6, l=11, [2, 6), '012345', [6, 7) }", escapeJavaString(segments.toStringPrep()));
        assertEquals("23450123456", segments.toStringChars());
        assertEquals(segments.toString(sequence).length(), segments.length());
    }

    @Test
    public void test_handleOverlapDefaultFromBeforeInOver2() {
        String input = "0123456789";
        BasedSequence sequence = BasedSequence.of(input);
        BasedSegmentBuilder2 segments = BasedSegmentBuilder2.emptyBuilder(sequence);

        segments.append(2, 6);
        segments.append(0, 8);
        assertEquals("BasedSegmentBuilder2{[2, 8), s=0:0, u=1:6, t=1:6, l=12, [2, 6), '012345', [6, 8) }", escapeJavaString(segments.toStringPrep()));
        assertEquals("234501234567", segments.toStringChars());
        assertEquals(segments.toString(sequence).length(), segments.length());
    }

    @Test
    public void test_handleOverlapDefaultIn0By1() {
        String input = "0123456789";
        BasedSequence sequence = BasedSequence.of(input);
        BasedSegmentBuilder2 segments = BasedSegmentBuilder2.emptyBuilder(sequence);

        segments.append(2, 6);
        segments.append(2, 3);
        assertEquals("BasedSegmentBuilder2{[2, 6), s=0:0, u=1:1, t=1:1, l=5, [2, 6), '2' }", escapeJavaString(segments.toStringPrep()));
        assertEquals("23452", segments.toStringChars());
        assertEquals(segments.toString(sequence).length(), segments.length());
    }

    @Test
    public void test_handleOverlapDefaultIn0By2() {
        String input = "0123456789";
        BasedSequence sequence = BasedSequence.of(input);
        BasedSegmentBuilder2 segments = BasedSegmentBuilder2.emptyBuilder(sequence);

        segments.append(2, 6);
        segments.append(2, 4);
        assertEquals("BasedSegmentBuilder2{[2, 6), s=0:0, u=1:2, t=1:2, l=6, [2, 6), '23' }", escapeJavaString(segments.toStringPrep()));
        assertEquals("234523", segments.toStringChars());
        assertEquals(segments.toString(sequence).length(), segments.length());
    }

    @Test
    public void test_handleOverlapLoop() {
        String input = "0123456789";
        BasedSequence sequence = BasedSequence.of(input);

        for (int s = 0; s < input.length(); s++) {
            for (int e = s; e < input.length(); e++) {
                BasedSegmentBuilder2 segments = BasedSegmentBuilder2.emptyBuilder(sequence);
                segments.append(2, 6);

                segments.append(s, e);
                String expected = input.substring(2, 6) + input.substring(s, e);

                assertEquals("" + s + "," + e, expected, segments.toStringChars());
                assertEquals(expected.length(), segments.length());
            }
        }
    }

    @Test
    public void test_handleOverlapDefaultMerge1() {
        String input = "0123456789";
        BasedSequence sequence = BasedSequence.of(input);
        BasedSegmentBuilder2 segments = BasedSegmentBuilder2.emptyBuilder(sequence);

        segments.append(2, 5);
        segments.append(4, 8);
        assertEquals("BasedSegmentBuilder2{[2, 8), s=0:0, u=1:1, t=1:1, l=7, [2, 5), '4', [5, 8) }", escapeJavaString(segments.toStringPrep()));
        assertEquals(segments.toString(sequence).length(), segments.length());
    }

    @Test
    public void test_handleOverlapDefaultMerge2() {
        String input = "0123456789";
        BasedSequence sequence = BasedSequence.of(input);
        BasedSegmentBuilder2 segments = BasedSegmentBuilder2.emptyBuilder(sequence);

        segments.append(2, 5);
        segments.append(1, 8);
        assertEquals("BasedSegmentBuilder2{[2, 8), s=0:0, u=1:4, t=1:4, l=10, [2, 5), '1234', [5, 8) }", escapeJavaString(segments.toStringPrep()));
        assertEquals(segments.toString(sequence).length(), segments.length());
    }

    @Test
    public void test_handleOverlapDefaultMerge3() {
        String input = "0123456789";
        BasedSequence sequence = BasedSequence.of(input);
        BasedSegmentBuilder2 segments = BasedSegmentBuilder2.emptyBuilder(sequence);

        segments.append(2, 5);
        segments.append(3, 5);
        assertEquals("BasedSegmentBuilder2{[2, 5), s=0:0, u=1:2, t=1:2, l=5, [2, 5), '34' }", escapeJavaString(segments.toStringPrep()));
        assertEquals(segments.toString(sequence).length(), segments.length());
    }

    @Test
    public void test_handleOverlapDefaultMerge4() {
        String input = "0123456789";
        BasedSequence sequence = BasedSequence.of(input);
        BasedSegmentBuilder2 segments = BasedSegmentBuilder2.emptyBuilder(sequence);

        segments.append(2, 5);
        segments.append(2, 4);
        assertEquals("BasedSegmentBuilder2{[2, 5), s=0:0, u=1:2, t=1:2, l=5, [2, 5), '23' }", escapeJavaString(segments.toStringPrep()));
        assertEquals(segments.toString(sequence).length(), segments.length());
    }

    @Test
    public void test_handleOverlapDefaultMerge5() {
        String input = "0123456789";
        BasedSequence sequence = BasedSequence.of(input);
        BasedSegmentBuilder2 segments = BasedSegmentBuilder2.emptyBuilder(sequence);

        segments.append(2, 5);
        segments.append(2, 5);
        assertEquals("BasedSegmentBuilder2{[2, 5), s=0:0, u=1:3, t=1:3, l=6, [2, 5), '234' }", escapeJavaString(segments.toStringPrep()));
        assertEquals(segments.toString(sequence).length(), segments.length());
    }

    @Test
    public void test_handleOverlapDefaultMerge6() {
        String input = "0123456789";
        BasedSequence sequence = BasedSequence.of(input);
        BasedSegmentBuilder2 segments = BasedSegmentBuilder2.emptyBuilder(sequence);

        segments.append(2, 5);
        segments.append(3, 4);
        assertEquals("BasedSegmentBuilder2{[2, 5), s=0:0, u=1:1, t=1:1, l=4, [2, 5), '3' }", escapeJavaString(segments.toStringPrep()));
        assertEquals(segments.toString(sequence).length(), segments.length());
    }

    /*
       Optimization tests, optimizer for backward compatibility
     */

    @Test
    public void test_optimizerExtendPrev1() {
        String input = "0123456789";
        BasedSequence sequence = BasedSequence.of(input);
        CharRecoveryOptimizer2 optimizer = new CharRecoveryOptimizer2(PositionAnchor.CURRENT);
        BasedSegmentBuilder2 segments = BasedSegmentBuilder2.emptyBuilder(sequence, optimizer);

        segments.append(0, 3);
        segments.append("345");
        segments.append(6, 10);
        assertEquals("BasedSegmentBuilder2{[0, 10), s=0:0, u=0:0, t=0:0, l=10, [0, 10) }", escapeJavaString(segments.toStringPrep()));
        assertEquals(segments.toString(sequence).length(), segments.length());
    }

    @Test
    public void test_optimizerExtendPrev2() {
        String input = "0123456789";
        BasedSequence sequence = BasedSequence.of(input);
        CharRecoveryOptimizer2 optimizer = new CharRecoveryOptimizer2(PositionAnchor.CURRENT);
        BasedSegmentBuilder2 segments = BasedSegmentBuilder2.emptyBuilder(sequence, optimizer);

        segments.append(0, 3);
        segments.append("34 ");
        segments.append(6, 10);
        assertEquals("BasedSegmentBuilder2{[0, 10), s=1:1, u=1:1, t=1:1, l=10, [0, 5), ' ', [6, 10) }", escapeJavaString(segments.toStringPrep()));
        assertEquals(segments.toString(sequence).length(), segments.length());
    }

    @Test
    public void test_optimizerExtendPrevNext() {
        String input = "0123456789";
        BasedSequence sequence = BasedSequence.of(input);
        CharRecoveryOptimizer2 optimizer = new CharRecoveryOptimizer2(PositionAnchor.CURRENT);
        BasedSegmentBuilder2 segments = BasedSegmentBuilder2.emptyBuilder(sequence, optimizer);

        segments.append(0, 3);
        segments.append("34 5");
        segments.append(6, 10);
        assertEquals("BasedSegmentBuilder2{[0, 10), s=1:1, u=1:1, t=1:1, l=11, [0, 5), ' ', [5, 10) }", escapeJavaString(segments.toStringPrep()));
        assertEquals(segments.toString(sequence).length(), segments.length());
    }

    @Test
    public void test_optimizerExtendPrevNextCollapse() {
        String input = "0123456789";
        BasedSequence sequence = BasedSequence.of(input);
        CharRecoveryOptimizer2 optimizer = new CharRecoveryOptimizer2(PositionAnchor.CURRENT);
        BasedSegmentBuilder2 segments = BasedSegmentBuilder2.emptyBuilder(sequence, optimizer);

        segments.append(0, 3);
        segments.append("34 56");
        segments.append(7, 10);
        assertEquals("BasedSegmentBuilder2{[0, 10), s=1:1, u=1:1, t=1:1, l=11, [0, 5), ' ', [5, 10) }", escapeJavaString(segments.toStringPrep()));
        assertEquals(segments.toString(sequence).length(), segments.length());
    }

    @Test
    public void test_optimizerExtendNext() {
        String input = "0123456789";
        BasedSequence sequence = BasedSequence.of(input);
        CharRecoveryOptimizer2 optimizer = new CharRecoveryOptimizer2(PositionAnchor.CURRENT);
        BasedSegmentBuilder2 segments = BasedSegmentBuilder2.emptyBuilder(sequence, optimizer);

        segments.append(0, 3);
        segments.append(" 3456");
        segments.append(7, 10);
        assertEquals("BasedSegmentBuilder2{[0, 10), s=1:1, u=1:1, t=1:1, l=11, [0, 3), ' ', [3, 10) }", escapeJavaString(segments.toStringPrep()));
        assertEquals(segments.toString(sequence).length(), segments.length());
    }

    @Test
    public void test_optimizerExtendNext1() {
        String input = "0123456789";
        BasedSequence sequence = BasedSequence.of(input);
        CharRecoveryOptimizer2 optimizer = new CharRecoveryOptimizer2(PositionAnchor.CURRENT);
        BasedSegmentBuilder2 segments = BasedSegmentBuilder2.emptyBuilder(sequence, optimizer);

        segments.append(0, 3);
        segments.append(" 345");
        segments.append(6, 10);
        assertEquals("BasedSegmentBuilder2{[0, 10), s=1:1, u=1:1, t=1:1, l=11, [0, 3), ' ', [3, 10) }", escapeJavaString(segments.toStringPrep()));
        assertEquals(segments.toString(sequence).length(), segments.length());
    }

    @Test
    public void test_optimizerIndent1() {
        String input = "0123456789";
        BasedSequence sequence = BasedSequence.of(input);
        CharRecoveryOptimizer2 optimizer = new CharRecoveryOptimizer2(PositionAnchor.CURRENT);
        BasedSegmentBuilder2 segments = BasedSegmentBuilder2.emptyBuilder(sequence, optimizer);

        segments.append(0, 3);
        segments.append(" 345");
        segments.append(6, 10);
        assertEquals("BasedSegmentBuilder2{[0, 10), s=1:1, u=1:1, t=1:1, l=11, [0, 3), ' ', [3, 10) }", escapeJavaString(segments.toStringPrep()));
        assertEquals(segments.toString(sequence).length(), segments.length());
    }

    /*
     * Optimizer tests to ensure all optimizations are handled properly
     */

    @Test
    public void test_optimizersIndent1None() {
        String input = "  0123456789";
        BasedSequence sequence = BasedSequence.of(input);
        CharRecoveryOptimizer2 optimizer = new CharRecoveryOptimizer2(PositionAnchor.CURRENT);
        BasedSegmentBuilder2 segments = BasedSegmentBuilder2.emptyBuilder(sequence, optimizer);

        segments.append("    ");
        segments.append(2, 12);
        assertEquals("BasedSegmentBuilder2{[0, 12), s=1:2, u=1:2, t=1:2, l=14, '  ', [0, 12) }", escapeJavaString(segments.toStringPrep()));
        assertEquals(segments.toString(sequence).length(), segments.length());
    }

    @Test
    public void test_optimizersSpacesNone() {
        String input = "01234  56789";
        BasedSequence sequence = BasedSequence.of(input);
        CharRecoveryOptimizer2 optimizer = new CharRecoveryOptimizer2(PositionAnchor.CURRENT);
        BasedSegmentBuilder2 segments = BasedSegmentBuilder2.emptyBuilder(sequence, optimizer);

        segments.append(0, 5);
        segments.append("    ");
        segments.append(7, 12);
        assertEquals("BasedSegmentBuilder2{[0, 12), s=1:2, u=1:2, t=1:2, l=14, [0, 6), '  ', [6, 12) }", escapeJavaString(segments.toStringPrep()));
        assertEquals(segments.toString(sequence).length(), segments.length());
    }

    @Test
    public void test_optimizersSpacesLeft() {
        String input = "01234  56789";
        BasedSequence sequence = BasedSequence.of(input);
        CharRecoveryOptimizer2 optimizer = new CharRecoveryOptimizer2(PositionAnchor.PREVIOUS);
        BasedSegmentBuilder2 segments = BasedSegmentBuilder2.emptyBuilder(sequence, optimizer);

        segments.append(0, 5);
        segments.append("    ");
        segments.append(7, 12);
        assertEquals("BasedSegmentBuilder2{[0, 12), s=1:2, u=1:2, t=1:2, l=14, [0, 5), '  ', [5, 12) }", escapeJavaString(segments.toStringPrep()));
        assertEquals(segments.toString(sequence).length(), segments.length());
    }

    @Test
    public void test_optimizersSpacesRight() {
        String input = "01234  56789";
        BasedSequence sequence = BasedSequence.of(input);
        CharRecoveryOptimizer2 optimizer = new CharRecoveryOptimizer2(PositionAnchor.NEXT);
        BasedSegmentBuilder2 segments = BasedSegmentBuilder2.emptyBuilder(sequence, optimizer);

        segments.append(0, 5);
        segments.append("    ");
        segments.append(7, 12);
        assertEquals("BasedSegmentBuilder2{[0, 12), s=1:2, u=1:2, t=1:2, l=14, [0, 7), '  ', [7, 12) }", escapeJavaString(segments.toStringPrep()));
        assertEquals(segments.toString(sequence).length(), segments.length());
    }

    @Test
    public void test_optimizersIndent1Left() {
        String input = "  0123456789";
        BasedSequence sequence = BasedSequence.of(input);
        CharRecoveryOptimizer2 optimizer = new CharRecoveryOptimizer2(PositionAnchor.PREVIOUS);
        BasedSegmentBuilder2 segments = BasedSegmentBuilder2.emptyBuilder(sequence, optimizer);

        segments.append("    ");
        segments.append(2, 12);
        assertEquals("BasedSegmentBuilder2{[0, 12), s=1:2, u=1:2, t=1:2, l=14, '  ', [0, 12) }", escapeJavaString(segments.toStringPrep()));
        assertEquals(segments.toString(sequence).length(), segments.length());
    }

    @Test
    public void test_optimizersIndent1Right() {
        String input = "  0123456789";
        BasedSequence sequence = BasedSequence.of(input);
        CharRecoveryOptimizer2 optimizer = new CharRecoveryOptimizer2(PositionAnchor.NEXT);
        BasedSegmentBuilder2 segments = BasedSegmentBuilder2.emptyBuilder(sequence, optimizer);

        segments.append("    ");
        segments.append(2, 12);
        assertEquals("BasedSegmentBuilder2{[0, 12), s=1:2, u=1:2, t=1:2, l=14, '  ', [0, 12) }", escapeJavaString(segments.toStringPrep()));
        assertEquals(segments.toString(sequence).length(), segments.length());
    }

    @Test
    public void test_optimizersEOL1None() {
        String input = "01234\n  56789";
        BasedSequence sequence = BasedSequence.of(input);
        CharRecoveryOptimizer2 optimizer = new CharRecoveryOptimizer2(PositionAnchor.CURRENT);
        BasedSegmentBuilder2 segments = BasedSegmentBuilder2.emptyBuilder(sequence, optimizer);

        segments.append(0, 5);
        segments.append("\n    ");
        segments.append(8, 12);
        assertEquals("BasedSegmentBuilder2{[0, 12), s=1:2, u=1:2, t=1:2, l=14, [0, 6), '  ', [6, 12) }", escapeJavaString(segments.toStringPrep()));
        assertEquals(segments.toString(sequence).length(), segments.length());
    }

    @Test
    public void test_optimizersEOL1Left() {
        String input = "01234\n  56789";
        BasedSequence sequence = BasedSequence.of(input);
        CharRecoveryOptimizer2 optimizer = new CharRecoveryOptimizer2(PositionAnchor.PREVIOUS);
        BasedSegmentBuilder2 segments = BasedSegmentBuilder2.emptyBuilder(sequence, optimizer);

        segments.append(0, 5);
        segments.append("\n    ");
        segments.append(8, 12);
        assertEquals("BasedSegmentBuilder2{[0, 12), s=1:2, u=1:2, t=1:2, l=14, [0, 6), '  ', [6, 12) }", escapeJavaString(segments.toStringPrep()));
        assertEquals(segments.toString(sequence).length(), segments.length());
    }

    @Test
    public void test_optimizersEOL1Right() {
        String input = "01234\n  56789";
        BasedSequence sequence = BasedSequence.of(input);
        CharRecoveryOptimizer2 optimizer = new CharRecoveryOptimizer2(PositionAnchor.NEXT);
        BasedSegmentBuilder2 segments = BasedSegmentBuilder2.emptyBuilder(sequence, optimizer);

        segments.append(0, 5);
        segments.append("\n    ");
        segments.append(8, 12);
        assertEquals("BasedSegmentBuilder2{[0, 12), s=1:2, u=1:2, t=1:2, l=14, [0, 6), '  ', [6, 12) }", escapeJavaString(segments.toStringPrep()));
        assertEquals(segments.toString(sequence).length(), segments.length());
    }

    @Test
    public void test_optimizersEOL2None() {
        String input = "01234\n\n 56789";
        BasedSequence sequence = BasedSequence.of(input);
        CharRecoveryOptimizer2 optimizer = new CharRecoveryOptimizer2(PositionAnchor.CURRENT);
        BasedSegmentBuilder2 segments = BasedSegmentBuilder2.emptyBuilder(sequence, optimizer);

        segments.append(0, 5);
        segments.append("\n\n   ");
        segments.append(8, 12);
        assertEquals("BasedSegmentBuilder2{[0, 12), s=1:2, u=1:2, t=1:2, l=14, [0, 7), '  ', [7, 12) }", escapeJavaString(segments.toStringPrep()));
        assertEquals(segments.toString(sequence).length(), segments.length());
    }

    @Test
    public void test_optimizersEOL2Left() {
        String input = "01234\n\n 56789";
        BasedSequence sequence = BasedSequence.of(input);
        CharRecoveryOptimizer2 optimizer = new CharRecoveryOptimizer2(PositionAnchor.PREVIOUS);
        BasedSegmentBuilder2 segments = BasedSegmentBuilder2.emptyBuilder(sequence, optimizer);

        segments.append(0, 5);
        segments.append("\n\n   ");
        segments.append(8, 12);
        assertEquals("BasedSegmentBuilder2{[0, 12), s=1:2, u=1:2, t=1:2, l=14, [0, 7), '  ', [7, 12) }", escapeJavaString(segments.toStringPrep()));
        assertEquals(segments.toString(sequence).length(), segments.length());
    }

    @Test
    public void test_optimizersEOL2Right() {
        String input = "01234\n\n 56789";
        BasedSequence sequence = BasedSequence.of(input);
        CharRecoveryOptimizer2 optimizer = new CharRecoveryOptimizer2(PositionAnchor.NEXT);
        BasedSegmentBuilder2 segments = BasedSegmentBuilder2.emptyBuilder(sequence, optimizer);

        segments.append(0, 5);
        segments.append("\n\n   ");
        segments.append(8, 12);
        assertEquals("BasedSegmentBuilder2{[0, 12), s=1:2, u=1:2, t=1:2, l=14, [0, 7), '  ', [7, 12) }", escapeJavaString(segments.toStringPrep()));
        assertEquals(segments.toString(sequence).length(), segments.length());
    }

    @Test
    public void test_optimizersEOL3None() {
        String input = "01234\n  56789";
        BasedSequence sequence = BasedSequence.of(input);
        CharRecoveryOptimizer2 optimizer = new CharRecoveryOptimizer2(PositionAnchor.CURRENT);
        BasedSegmentBuilder2 segments = BasedSegmentBuilder2.emptyBuilder(sequence, optimizer);

        segments.append(0, 3);
        segments.append("34\n    ");
        segments.append(8, 12);
        assertEquals("BasedSegmentBuilder2{[0, 12), s=1:2, u=1:2, t=1:2, l=14, [0, 6), '  ', [6, 12) }", escapeJavaString(segments.toStringPrep()));
        assertEquals(segments.toString(sequence).length(), segments.length());
    }

    @Test
    public void test_optimizersEOL3Left() {
        String input = "01234\n  56789";
        BasedSequence sequence = BasedSequence.of(input);
        CharRecoveryOptimizer2 optimizer = new CharRecoveryOptimizer2(PositionAnchor.PREVIOUS);
        BasedSegmentBuilder2 segments = BasedSegmentBuilder2.emptyBuilder(sequence, optimizer);

        segments.append(0, 3);
        segments.append("34\n    ");
        segments.append(8, 12);
        assertEquals("BasedSegmentBuilder2{[0, 12), s=1:2, u=1:2, t=1:2, l=14, [0, 6), '  ', [6, 12) }", escapeJavaString(segments.toStringPrep()));
        assertEquals(segments.toString(sequence).length(), segments.length());
    }

    @Test
    public void test_optimizersEOL3Right() {
        String input = "01234\n  56789";
        BasedSequence sequence = BasedSequence.of(input);
        CharRecoveryOptimizer2 optimizer = new CharRecoveryOptimizer2(PositionAnchor.NEXT);
        BasedSegmentBuilder2 segments = BasedSegmentBuilder2.emptyBuilder(sequence, optimizer);

        segments.append(0, 3);
        segments.append("34\n    ");
        segments.append(8, 12);
        assertEquals("BasedSegmentBuilder2{[0, 12), s=1:2, u=1:2, t=1:2, l=14, [0, 6), '  ', [6, 12) }", escapeJavaString(segments.toStringPrep()));
        assertEquals(segments.toString(sequence).length(), segments.length());
    }

    @Test
    public void test_optimizers1() {
        String input = "01234 \n56789";
        BasedSequence sequence = BasedSequence.of(input);
        CharRecoveryOptimizer2 optimizer = new CharRecoveryOptimizer2(PositionAnchor.NEXT);
        BasedSegmentBuilder2 segments = BasedSegmentBuilder2.emptyBuilder(sequence, optimizer);

        segments.append(0, 5);
        segments.append("\n  ");
        segments.append(7, 12);
        assertEquals("BasedSegmentBuilder2{[0, 12), s=1:2, u=1:2, t=1:2, l=13, [0, 5), [6, 7), '  ', [7, 12) }", escapeJavaString(segments.toStringPrep()));
        assertEquals(segments.toString(sequence).length(), segments.length());
    }

    @Test
    public void test_optimizers2() {
        String input = "01234 \n";
        BasedSequence sequence = BasedSequence.of(input);
        CharRecoveryOptimizer2 optimizer = new CharRecoveryOptimizer2(PositionAnchor.NEXT);
        BasedSegmentBuilder2 segments = BasedSegmentBuilder2.emptyBuilder(sequence, optimizer);

        segments.append(0, 5);
        segments.append("\n");
        assertEquals("BasedSegmentBuilder2{[0, 7), s=0:0, u=0:0, t=0:0, l=6, [0, 5), [6, 7) }", escapeJavaString(segments.toStringPrep()));
        assertEquals(segments.toString(sequence).length(), segments.length());
    }

    @Test
    public void test_optimizers2a() {
        // this one causes text to be replaced with recovered EOL in the code
        String input = "01234  \n";
        BasedSequence sequence = BasedSequence.of(input);
        CharRecoveryOptimizer2 optimizer = new CharRecoveryOptimizer2(PositionAnchor.NEXT);
        BasedSegmentBuilder2 segments = BasedSegmentBuilder2.emptyBuilder(sequence, optimizer);

        segments.append(0, 5);
        segments.append(" \n");
        assertEquals("BasedSegmentBuilder2{[0, 8), s=0:0, u=0:0, t=0:0, l=7, [0, 6), [7, 8) }", escapeJavaString(segments.toStringPrep()));
        assertEquals(segments.toString(sequence).length(), segments.length());
    }

    @Test
    public void test_optimizers3() {
        String input = "012340123401234";
        BasedSequence sequence = BasedSequence.of(input);
        CharRecoveryOptimizer2 optimizer = new CharRecoveryOptimizer2(PositionAnchor.NEXT);
        BasedSegmentBuilder2 segments = BasedSegmentBuilder2.emptyBuilder(sequence, optimizer);

        segments.append(0, 5);
        segments.append("01234");
        assertEquals(escapeJavaString("BasedSegmentBuilder2{[0, 10), s=0:0, u=0:0, t=0:0, l=10, [0, 10) }"), escapeJavaString(segments.toStringPrep()));
        assertEquals(segments.toString(sequence).length(), segments.length());
    }

    @Test
    public void test_optimizers4() {
        String input = "0123  \n  5678";
        BasedSequence sequence = BasedSequence.of(input);
        CharRecoveryOptimizer2 optimizer = new CharRecoveryOptimizer2(PositionAnchor.NEXT);
        BasedSegmentBuilder2 segments = BasedSegmentBuilder2.emptyBuilder(sequence, optimizer);

        segments.append(0, 5);
        segments.append("\n");
        segments.append(8, 13);
        assertEquals("BasedSegmentBuilder2{[0, 13), s=0:0, u=0:0, t=0:0, l=11, [0, 5), [6, 7), [8, 13) }", escapeJavaString(segments.toStringPrep()));
        assertEquals(segments.toString(sequence).length(), segments.length());
    }

    @Test
    public void test_optimizersCompoundNoAnchors1() {
        String input = "" +
                "  line 1 \n" +
                "  line 2 \n" +
                "\n" +
                "  line 3\n" +
                "";
        BasedSequence sequence = BasedSequence.of(input);
        BasedSegmentBuilder2 segments = BasedSegmentBuilder2.emptyBuilder(sequence, F_TRACK_UNIQUE);

        @NotNull List<BasedSequence> lines = sequence.splitListEOL(false);
        for (BasedSequence line : lines) {
            BasedSequence trim = line.trim();
            if (!trim.isEmpty()) segments.append("    ");
            segments.append(trim.getSourceRange());
            segments.append("\n");
        }
        assertEquals("BasedSegmentBuilder2{[0, 30), s=3:6, u=3:6, t=3:6, l=34, '  ', [0, 8), [9, 10), '  ', [10, 18), [19, 21), '  ', [21, 30) }", escapeJavaString(segments.toStringPrep()));
        assertEquals(segments.toString(sequence).length(), segments.length());

        assertEquals("  ⟦  line 1⟧⟦\\n⟧  ⟦  line 2⟧⟦\\n\\n⟧  ⟦  line 3\\n⟧", segments.toStringWithRangesVisibleWhitespace(input));

        assertEquals("" +
                "    line 1\n" +
                "    line 2\n" +
                "\n" +
                "    line 3\n" +
                "", segments.toString(sequence));
    }

    @Test
    public void test_optimizersCompoundNoAnchors2() {
        String input = "" +
                "  line 1 \n" +
                "  line 2 \n" +
                "\n" +
                "  line 3\n" +
                "";
        BasedSequence sequence = BasedSequence.of(input);
        BasedSegmentBuilder2 segments = BasedSegmentBuilder2.emptyBuilder(sequence, F_TRACK_UNIQUE);

        @NotNull List<BasedSequence> lines = sequence.splitListEOL(false);
        for (BasedSequence line : lines) {
            BasedSequence trim = line.trim();
            if (!trim.isEmpty()) segments.append("  ");
            segments.append(trim.getSourceRange());
            segments.append("\n");
        }
        assertEquals("BasedSegmentBuilder2{[0, 30), s=0:0, u=0:0, t=0:0, l=28, [0, 8), [9, 18), [19, 30) }", escapeJavaString(segments.toStringPrep()));
        assertEquals(segments.toString(sequence).length(), segments.length());

        assertEquals("⟦  line 1⟧⟦\\n  line 2⟧⟦\\n\\n  line 3\\n⟧", segments.toStringWithRangesVisibleWhitespace(input));

        assertEquals("" +
                "  line 1\n" +
                "  line 2\n" +
                "\n" +
                "  line 3\n" +
                "", segments.toString(sequence));
    }

    @Test
    public void test_optimizersCompoundNoAnchors3() {
        String input = "" +
                "line 1\n" +
                "line 2 \n" +
                "\n" +
                "line 3\n" +
                "";
        BasedSequence sequence = BasedSequence.of(input);
        BasedSegmentBuilder2 segments = BasedSegmentBuilder2.emptyBuilder(sequence, F_TRACK_UNIQUE);

        @NotNull List<BasedSequence> lines = sequence.splitListEOL(false);
        for (BasedSequence line : lines) {
            BasedSequence trim = line.trim();
//            if (!trim.isEmpty()) segments.append("  ");
            segments.append(trim.getSourceRange());
            segments.append("\n");
        }
        assertEquals("BasedSegmentBuilder2{[0, 23), s=0:0, u=0:0, t=0:0, l=22, [0, 13), [14, 23) }", escapeJavaString(segments.toStringPrep()));
        assertEquals(segments.toString(sequence).length(), segments.length());

        assertEquals("⟦line 1\\nline 2⟧⟦\\n\\nline 3\\n⟧", segments.toStringWithRangesVisibleWhitespace(input));

        assertEquals("" +
                "line 1\n" +
                "line 2\n" +
                "\n" +
                "line 3\n" +
                "", segments.toString(sequence));
    }

    @Test
    public void test_optimizersCompoundAnchors1() {
        String input = "" +
                "  line 1 \n" +
                "  line 2 \n" +
                "\n" +
                "  line 3\n" +
                "";
        BasedSequence sequence = BasedSequence.of(input);
        BasedSegmentBuilder2 segments = BasedSegmentBuilder2.emptyBuilder(sequence, F_TRACK_UNIQUE | F_INCLUDE_ANCHORS);

        @NotNull List<BasedSequence> lines = sequence.splitListEOL(false);
        for (BasedSequence line : lines) {
            BasedSequence trim = line.trim();
            if (!trim.isEmpty()) segments.append("    ");
            segments.append(trim.getSourceRange());
            segments.append("\n");
        }

        assertEquals("BasedSegmentBuilder2{[0, 30), s=3:6, u=3:6, t=3:6, l=34, '  ', [0, 8), [9, 10), '  ', [10, 18), [19, 21), '  ', [21, 30) }", escapeJavaString(segments.toStringPrep()));
        assertEquals(segments.toString(sequence).length(), segments.length());
        assertEquals("  ⟦  line 1⟧⟦\\n⟧  ⟦  line 2⟧⟦\\n\\n⟧  ⟦  line 3\\n⟧", segments.toStringWithRangesVisibleWhitespace(input));

        assertEquals("" +
                "    line 1\n" +
                "    line 2\n" +
                "\n" +
                "    line 3\n" +
                "", segments.toString(sequence));
    }

    @Test
    public void test_optimizersCompoundAnchors2() {
        String input = "" +
                "  line 1 \n" +
                "  line 2 \n" +
                "\n" +
                "  line 3\n" +
                "";
        BasedSequence sequence = BasedSequence.of(input);
        BasedSegmentBuilder2 segments = BasedSegmentBuilder2.emptyBuilder(sequence, F_TRACK_UNIQUE | F_INCLUDE_ANCHORS);

        @NotNull List<BasedSequence> lines = sequence.splitListEOL(false);
        for (BasedSequence line : lines) {
            BasedSequence trim = line.trim();
            if (!trim.isEmpty()) segments.append("  ");
            segments.append(trim.getSourceRange());
            segments.append("\n");
        }
        assertEquals("BasedSegmentBuilder2{[0, 30), s=0:0, u=0:0, t=0:0, l=28, [0, 8), [9, 18), [19, 30) }", escapeJavaString(segments.toStringPrep()));
        assertEquals(segments.toString(sequence).length(), segments.length());

        assertEquals("⟦  line 1⟧⟦\\n  line 2⟧⟦\\n\\n  line 3\\n⟧", segments.toStringWithRangesVisibleWhitespace(input));

        assertEquals("" +
                "  line 1\n" +
                "  line 2\n" +
                "\n" +
                "  line 3\n" +
                "", segments.toString(sequence));
    }

    @Test
    public void test_optimizersCompound3Anchors() {
        String input = "" +
                "line 1\n" +
                "line 2 \n" +
                "\n" +
                "line 3\n" +
                "";
        BasedSequence sequence = BasedSequence.of(input);
        CharRecoveryOptimizer2 optimizer = new CharRecoveryOptimizer2(PositionAnchor.CURRENT);
        BasedSegmentBuilder2 segments = BasedSegmentBuilder2.emptyBuilder(sequence, optimizer, F_TRACK_UNIQUE | F_INCLUDE_ANCHORS);

        @NotNull List<BasedSequence> lines = sequence.splitListEOL(false);
        for (BasedSequence line : lines) {
            BasedSequence trim = line.trim();
//            if (!trim.isEmpty()) segments.append("  ");
            segments.append(trim.getSourceRange());
            segments.append("\n");
        }
        assertEquals("BasedSegmentBuilder2{[0, 23), s=0:0, u=0:0, t=0:0, l=22, [0, 13), [14, 23) }", escapeJavaString(segments.toStringPrep()));
        assertEquals(segments.toString(sequence).length(), segments.length());

        assertEquals("⟦line 1\\nline 2⟧⟦\\n\\nline 3\\n⟧", segments.toStringWithRangesVisibleWhitespace(input));

        assertEquals("" +
                "line 1\n" +
                "line 2\n" +
                "\n" +
                "line 3\n" +
                "", segments.toString(sequence));
    }

    // ************************************************************************
    // CAUTION: BasedSegmentBuilder2 Unique Test, Not in Segment Builder Tests
    //   Do NOT blow away if synchronizing the two test files
    // ************************************************************************

    @Test
    public void test_extractRangesDefault() {
        String input = "0123456789";

        BasedSequence sequence = BasedSequence.of(input);
        BasedSegmentBuilder2 segments = BasedSegmentBuilder2.emptyBuilder(sequence);

        // NOTE: test from BasedSequenceImpl which is fragile and depends on segment builder working 100%
        // BasedSequence replaced = sequence.extractRanges(Range.of(0, 0), Range.of(0, 1), Range.of(3, 6), Range.of(8, 12));
        segments.append(Range.of(0, 0));
        segments.append(Range.of(0, 1));
        segments.append(Range.of(3, 6));
        segments.append(Range.of(8, 10));
        assertEquals(escapeJavaString("BasedSegmentBuilder2{[0, 10), s=0:0, u=0:0, t=0:0, l=6, [0, 1), [3, 6), [8, 10) }"), escapeJavaString(segments.toStringPrep()));
        assertEquals(segments.toString(sequence).length(), segments.length());
        assertEquals("034589", segments.toString(sequence));
    }

    @Test
    public void test_extractRangesAnchors() {
        String input = "0123456789";

        BasedSequence sequence = BasedSequence.of(input);
        BasedSegmentBuilder2 segments = BasedSegmentBuilder2.emptyBuilder(sequence, ISegmentBuilder.F_TRACK_UNIQUE | ISegmentBuilder.F_INCLUDE_ANCHORS);

        // NOTE: test from BasedSequenceImpl which is fragile and depends on segment builder working 100%
        // BasedSequence replaced = sequence.extractRanges(Range.of(0, 0), Range.of(0, 1), Range.of(3, 6), Range.of(8, 12));
        segments.append(Range.of(0, 0));
        segments.append(Range.of(0, 1));
        segments.append(Range.of(3, 6));
        segments.append(Range.of(8, 10));
        assertEquals(escapeJavaString("BasedSegmentBuilder2{[0, 10), s=0:0, u=0:0, t=0:0, l=6, [0, 1), [3, 6), [8, 10) }"), escapeJavaString(segments.toStringPrep()));
        assertEquals(segments.toString(sequence).length(), segments.length());
        assertEquals("034589", segments.toString(sequence));
    }

    @Test
    public void test_extractRangesNoAnchors() {
        String input = "0123456789";

        BasedSequence sequence = BasedSequence.of(input);
        BasedSegmentBuilder2 segments = BasedSegmentBuilder2.emptyBuilder(sequence, ISegmentBuilder.F_TRACK_UNIQUE);

        // NOTE: test from BasedSequenceImpl which is fragile and depends on segment builder working 100%
        // BasedSequence replaced = sequence.extractRanges(Range.of(0, 0), Range.of(0, 1), Range.of(3, 6), Range.of(8, 12));
        segments.append(Range.of(0, 0));
        segments.append(Range.of(0, 1));
        segments.append(Range.of(3, 6));
        segments.append(Range.of(8, 10));
        assertEquals(escapeJavaString("BasedSegmentBuilder2{[0, 10), s=0:0, u=0:0, t=0:0, l=6, [0, 1), [3, 6), [8, 10) }"), escapeJavaString(segments.toStringPrep()));
        assertEquals(segments.toString(sequence).length(), segments.length());
        assertEquals("034589", segments.toString(sequence));
    }

    @Test
    public void test_replacePrefixDefault() {
        String input = "0123456789";

        BasedSequence sequence = BasedSequence.of(input);
        BasedSegmentBuilder2 segments = BasedSegmentBuilder2.emptyBuilder(sequence);

        // NOTE: test from BasedSequenceImpl which is fragile and depends on segment builder working 100%
        // BasedSequence replaced = sequence.replace(0, 1, "^");
        // assertEquals("^123456789", replaced.toString());

        segments.append(Range.of(0, 0));
        segments.append("^");
        segments.append(Range.of(1, 10));
        assertEquals(escapeJavaString("BasedSegmentBuilder2{[0, 10), s=0:0, u=1:1, t=1:1, l=10, [0), '^', [1, 10) }"), escapeJavaString(segments.toStringPrep()));
        assertEquals(segments.toString(sequence).length(), segments.length());
        assertEquals("^123456789", segments.toString(sequence));
    }

    @Test
    public void test_replacePrefixAnchors() {
        String input = "0123456789";

        BasedSequence sequence = BasedSequence.of(input);
        BasedSegmentBuilder2 segments = BasedSegmentBuilder2.emptyBuilder(sequence, ISegmentBuilder.F_TRACK_UNIQUE | ISegmentBuilder.F_INCLUDE_ANCHORS);

        // NOTE: test from BasedSequenceImpl which is fragile and depends on segment builder working 100%
        // BasedSequence replaced = sequence.replace(0, 1, "^");
        // assertEquals("^123456789", replaced.toString());

        segments.append(Range.of(0, 0));
        segments.append("^");
        segments.append(Range.of(1, 10));
        assertEquals(escapeJavaString("BasedSegmentBuilder2{[0, 10), s=0:0, u=1:1, t=1:1, l=10, [0), '^', [1, 10) }"), escapeJavaString(segments.toStringPrep()));
        assertEquals(segments.toString(sequence).length(), segments.length());
        assertEquals("^123456789", segments.toString(sequence));
    }

    @Test
    public void test_replacePrefixNoAnchors() {
        String input = "0123456789";

        BasedSequence sequence = BasedSequence.of(input);
        BasedSegmentBuilder2 segments = BasedSegmentBuilder2.emptyBuilder(sequence, ISegmentBuilder.F_TRACK_UNIQUE);

        // NOTE: test from BasedSequenceImpl which is fragile and depends on segment builder working 100%
        // BasedSequence replaced = sequence.replace(0, 1, "^");
        // assertEquals("^123456789", replaced.toString());

        segments.append(Range.of(0, 0));
        segments.append("^");
        segments.append(Range.of(1, 10));
        assertEquals(escapeJavaString("BasedSegmentBuilder2{[0, 10), s=0:0, u=1:1, t=1:1, l=10, '^', [1, 10) }"), escapeJavaString(segments.toStringPrep()));
        assertEquals(segments.toString(sequence).length(), segments.length());
        assertEquals("^123456789", segments.toString(sequence));
    }
}
