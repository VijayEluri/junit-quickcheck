/*
 The MIT License

 Copyright (c) 2010-2015 Paul R. Holser, Jr.

 Permission is hereby granted, free of charge, to any person obtaining
 a copy of this software and associated documentation files (the
 "Software"), to deal in the Software without restriction, including
 without limitation the rights to use, copy, modify, merge, publish,
 distribute, sublicense, and/or sell copies of the Software, and to
 permit persons to whom the Software is furnished to do so, subject to
 the following conditions:

 The above copyright notice and this permission notice shall be
 included in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package com.pholser.junit.quickcheck;

import com.pholser.junit.quickcheck.generator.InRange;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.experimental.results.PrintableResult.testResult;
import static org.junit.experimental.results.ResultMatchers.hasSingleFailureContaining;
import static org.junit.experimental.results.ResultMatchers.isSuccessful;

public class OffsetDateTimePropertyParameterTypesTest {
    @Test
    public void offsetDateTime() {
        assertThat(testResult(OffsetDateTimeTheory.class), isSuccessful());
    }

    @RunWith(JUnitQuickcheck.class)
    public static class OffsetDateTimeTheory {
        @Property
        public void shouldHold(OffsetDateTime d) {
        }
    }

    @Test
    public void rangedOffsetDateTime() {
        assertThat(testResult(RangedOffsetDateTimeTheory.class), isSuccessful());
    }

    @RunWith(JUnitQuickcheck.class)
    public static class RangedOffsetDateTimeTheory {
        @Property
        public void shouldHold(
            @InRange(min = "01/01/2012T00:00:00.0+01:00", max = "12/31/2012T23:59:59.999999999+01:00",
                format = "MM/dd/yyyy'T'HH:mm:ss.nxxx") OffsetDateTime d) throws Exception {

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy'T'HH:mm:ss.nxxx");

            assertThat(
                d,
                allOf(
                    greaterThanOrEqualTo(OffsetDateTime.parse("01/01/2012T00:00:00.0+01:00", formatter)),
                    lessThanOrEqualTo(OffsetDateTime.parse("12/31/2012T23:59:59.999999999+01:00", formatter))));
        }
    }

    @Test
    public void malformedMin() {
        assertThat(
            testResult(MalformedMinOffsetDateTimeTheory.class),
            hasSingleFailureContaining(IllegalArgumentException.class.getName()));
    }

    @RunWith(JUnitQuickcheck.class)
    public static class MalformedMinOffsetDateTimeTheory {
        @Property
        public void shouldHold(
            @InRange(min = "@#!@#@", max = "12/31/2012T23:59:59.999999999+01:00",
                format = "MM/dd/yyyy'T'HH:mm:ss.nxxx") OffsetDateTime d) {
        }
    }

    @Test
    public void malformedMax() {
        assertThat(
            testResult(MalformedMaxOffsetDateTimeTheory.class),
            hasSingleFailureContaining(IllegalArgumentException.class.getName()));
    }

    @RunWith(JUnitQuickcheck.class)
    public static class MalformedMaxOffsetDateTimeTheory {
        @Property
        public void shouldHold(
            @InRange(min = "06/01/2011T23:59:59.999999999+01:00", max = "*&@^#%$",
                format = "MM/dd/yyyy'T'HH:mm:ss.nxxx") OffsetDateTime d) {
        }
    }

    @Test
    public void malformedFormat() {
        assertThat(
            testResult(MalformedFormatOffsetDateTimeTheory.class),
            hasSingleFailureContaining(IllegalArgumentException.class.getName()));
    }

    @RunWith(JUnitQuickcheck.class)
    public static class MalformedFormatOffsetDateTimeTheory {
        @Property
        public void shouldHold(
            @InRange(min = "06/01/2011T23:59:59.999999999+01:00", max = "06/30/2011T23:59:59.999999999+01:00",
                format = "*@&^#$") OffsetDateTime d) {
        }
    }

    @Test
    public void missingMin() {
        assertThat(testResult(MissingMinTheory.class), isSuccessful());
    }

    @RunWith(JUnitQuickcheck.class)
    public static class MissingMinTheory {
        @Property
        public void shouldHold(
            @InRange(max = "12/31/2012T23:59:59.999999999+01:00", format = "MM/dd/yyyy'T'HH:mm:ss.nxxx") OffsetDateTime d)
            throws Exception {

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy'T'HH:mm:ss.nxxx");
            assertThat(d, lessThanOrEqualTo(OffsetDateTime.parse("12/31/2012T23:59:59.999999999+01:00", formatter)));
        }
    }

    @Test
    public void missingMax() {
        assertThat(testResult(MissingMaxTheory.class), isSuccessful());
    }

    @RunWith(JUnitQuickcheck.class)
    public static class MissingMaxTheory {
        @Property
        public void shouldHold(
            @InRange(min = "12/31/2012T23:59:59.999999999+01:00", format = "MM/dd/yyyy'T'HH:mm:ss.nxxx") OffsetDateTime d)
            throws Exception {

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy'T'HH:mm:ss.nxxx");
            assertThat(d, greaterThanOrEqualTo(OffsetDateTime.parse("12/31/2012T23:59:59.999999999+01:00", formatter)));
        }
    }

    @Test
    public void backwardsRange() {
        assertThat(
            testResult(BackwardsRangeTheory.class),
            hasSingleFailureContaining(IllegalArgumentException.class.getName()));
    }

    @RunWith(JUnitQuickcheck.class)
    public static class BackwardsRangeTheory {
        @Property
        public void shouldHold(
            @InRange(min = "12/31/2012T23:59:59.999999999+01:00", max = "12/01/2012T00:00:00.0+01:00",
                format = "MM/dd/yyyy'T'HH:mm:ss.nxxx") OffsetDateTime d) {
        }
    }
}