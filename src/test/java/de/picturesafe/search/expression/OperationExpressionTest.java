/*
 * Copyright 2020 picturesafe media/data/bank GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.picturesafe.search.expression;

import de.picturesafe.search.expression.internal.EmptyExpression;
import de.picturesafe.search.expression.internal.FalseExpression;
import de.picturesafe.search.expression.internal.TrueExpression;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

public class OperationExpressionTest {

    private static final String STANDARD_DAY_FORMAT = "dd.MM.yyyy";

    @Test
    public void testEmptyExpressionOptimization() {
        OperationExpression op = OperationExpression.and(new EmptyExpression(), new EmptyExpression());
        Expression optimzedExpression = op.optimize();
        assertTrue(optimzedExpression instanceof EmptyExpression);

        op = OperationExpression.and(
                new EmptyExpression(),
                new EmptyExpression(),
                new ValueExpression("test1", "test1"));
        optimzedExpression = op.optimize();
        assertTrue(optimzedExpression instanceof ValueExpression);

        final List<Expression> expressions = new ArrayList<>();
        expressions.add(new EmptyExpression());
        expressions.add(new EmptyExpression());
        expressions.add(new ValueExpression("test1", "test1"));
        expressions.add(new ValueExpression("test2", "test2"));
        op = OperationExpression.and(expressions);
        optimzedExpression = op.optimize();
        assertTrue(optimzedExpression instanceof OperationExpression);
        assertEquals(2, ((OperationExpression) optimzedExpression).getOperands().size());
        assertTrue(((OperationExpression) optimzedExpression).getOperands().get(0) instanceof ValueExpression);

        op = OperationExpression.and(new ValueExpression("test3", "test3")).addAll(expressions);
        optimzedExpression = op.optimize();
        assertTrue(optimzedExpression instanceof OperationExpression);
        assertEquals(3, ((OperationExpression) optimzedExpression).getOperands().size());
        assertTrue(((OperationExpression) optimzedExpression).getOperands().get(0) instanceof ValueExpression);

        op = OperationExpression.and(expressions);
        optimzedExpression = op.optimize();
        assertTrue(optimzedExpression instanceof OperationExpression);
        assertEquals(2, ((OperationExpression) optimzedExpression).getOperands().size());
        assertTrue(((OperationExpression) optimzedExpression).getOperands().get(0) instanceof ValueExpression);
    }

    @Test
    public void testTrueExpressionOptimization() {
        OperationExpression op = OperationExpression.or(new TrueExpression());
        Expression optimzedExpression = op.optimize();
        assertTrue(optimzedExpression instanceof TrueExpression);

        op = OperationExpression.or(
                new TrueExpression(),
                new ValueExpression("test1", "test1"));
        optimzedExpression = op.optimize();
        assertTrue(optimzedExpression instanceof TrueExpression);

        op = OperationExpression.and(new TrueExpression());
        optimzedExpression = op.optimize();
        assertTrue(optimzedExpression instanceof TrueExpression);

        op = OperationExpression.and(
                new TrueExpression(),
                new ValueExpression("test1", "test1"));
        optimzedExpression = op.optimize();
        assertTrue(optimzedExpression instanceof ValueExpression);

        op = OperationExpression.and(
                new TrueExpression(),
                new ValueExpression("test1", "test1"),
                new ValueExpression("test2", "test2"));
        optimzedExpression = op.optimize();
        assertEquals(2, ((OperationExpression) optimzedExpression).getOperands().size());
        assertTrue(((OperationExpression) optimzedExpression).getOperands().get(0) instanceof ValueExpression);
    }

    @Test
    public void testFalseExpressionOptimization() {
        OperationExpression op = OperationExpression.and(new FalseExpression());
        Expression optimzedExpression = op.optimize();
        assertTrue(optimzedExpression instanceof FalseExpression);

        op = OperationExpression.and(
                new FalseExpression(),
                new ValueExpression("test1", "test1"));
        optimzedExpression = op.optimize();
        assertTrue(optimzedExpression instanceof FalseExpression);

        op = OperationExpression.or(new FalseExpression());
        optimzedExpression = op.optimize();
        assertTrue(optimzedExpression instanceof FalseExpression);

        op = OperationExpression.or(
                new FalseExpression(),
                new ValueExpression("test1", "test1"));
        optimzedExpression = op.optimize();
        assertTrue(optimzedExpression instanceof ValueExpression);

        op = OperationExpression.or(
                new FalseExpression(),
                new ValueExpression("test1", "test1"),
                new ValueExpression("test2", "test2"));
        optimzedExpression = op.optimize();
        assertEquals(2, ((OperationExpression) optimzedExpression).getOperands().size());
        assertTrue(((OperationExpression) optimzedExpression).getOperands().get(0) instanceof ValueExpression);
    }

    @Test
    public void testInExpressionOptimization() {
        final long[] ids1 = new long[]{1, 2, 3};
        final long[] ids2 = new long[]{3, 4, 5};

        OperationExpression op = OperationExpression.and(
                new InExpression("ids1", ids1),
                new InExpression("ids2", ids2));
        Expression optimzedExpression = op.optimize();
        assertEquals(2, ((OperationExpression) optimzedExpression).getOperands().size());
        assertTrue(((OperationExpression) optimzedExpression).getOperands().get(0) instanceof InExpression);
        assertTrue(((OperationExpression) optimzedExpression).getOperands().get(1) instanceof InExpression);

        op = OperationExpression.and(
                new InExpression("ids", ids1),
                new InExpression("ids", ids2));
        optimzedExpression = op.optimize();
        assertTrue(optimzedExpression instanceof InExpression);
        assertEquals(1, ((InExpression) optimzedExpression).getValues().length);
        assertEquals(3L, ((InExpression) optimzedExpression).getValues()[0]);

        op = OperationExpression.or(
                new InExpression("ids", ids1),
                new InExpression("ids", ids2));
        optimzedExpression = op.optimize();
        assertTrue(optimzedExpression instanceof InExpression);
        assertEquals(5, ((InExpression) optimzedExpression).getValues().length);

        op = OperationExpression.and(
                new InExpression("stringArray1", "A", "B", "C"),
                new InExpression("stringArray2", "C", "D", "E"));
        optimzedExpression = op.optimize();
        assertEquals(2, ((OperationExpression) optimzedExpression).getOperands().size());
        assertTrue(((OperationExpression) optimzedExpression).getOperands().get(0) instanceof InExpression);
        assertTrue(((OperationExpression) optimzedExpression).getOperands().get(1) instanceof InExpression);

        op = OperationExpression.and(
                new InExpression("stringArray", "A", "B", "C"),
                new InExpression("stringArray", "C", "D", "E"));
        optimzedExpression = op.optimize();
        assertTrue(optimzedExpression instanceof InExpression);
        assertEquals(1, ((InExpression) optimzedExpression).getValues().length);
        assertEquals("C", ((InExpression) optimzedExpression).getValues()[0]);

        op = OperationExpression.or(
                new InExpression("stringArray", "A", "B", "C"),
                new InExpression("stringArray", "C", "D", "E"));
        optimzedExpression = op.optimize();
        assertTrue(optimzedExpression instanceof InExpression);
        assertEquals(5, ((InExpression) optimzedExpression).getValues().length);
    }

    @Test
    public void testMustNotExpressionOptimization() {
        final long[] ids1 = new long[]{1, 2, 3};
        final long[] ids2 = new long[]{3, 4, 5};

        OperationExpression op = OperationExpression.and(
                new MustNotExpression(new InExpression("ids1", ids1)),
                new MustNotExpression(new InExpression("ids2", ids2)));
        Expression optimzedExpression = op.optimize();
        assertEquals(2, ((OperationExpression) optimzedExpression).getOperands().size());
        assertTrue(((OperationExpression) optimzedExpression).getOperands().get(0) instanceof MustNotExpression);
        assertTrue(((OperationExpression) optimzedExpression).getOperands().get(1) instanceof MustNotExpression);

        op = OperationExpression.and(
                new MustNotExpression(new InExpression("ids", ids1)),
                new MustNotExpression(new InExpression("ids", ids2)));
        optimzedExpression = op.optimize();
        assertTrue(optimzedExpression instanceof MustNotExpression);
        assertTrue(((MustNotExpression) optimzedExpression).getExpression() instanceof InExpression);
        assertEquals(5, ((InExpression) (((MustNotExpression) optimzedExpression)).getExpression()).getValues().length);

        op = OperationExpression.or(
                new MustNotExpression(new InExpression("ids", ids1)),
                new MustNotExpression(new InExpression("ids", ids2)));
        optimzedExpression = op.optimize();
        assertTrue(optimzedExpression instanceof MustNotExpression);
        assertEquals(1, ((InExpression) (((MustNotExpression) optimzedExpression)).getExpression()).getValues().length);
        assertEquals(3L, ((InExpression) (((MustNotExpression) optimzedExpression)).getExpression()).getValues()[0]);
    }

    @Test
    public void testRangeValueExpressionOptimization() throws Exception {

        final Date day1 = new SimpleDateFormat(STANDARD_DAY_FORMAT).parse("26.04.2017");
        final Date day2 = new SimpleDateFormat(STANDARD_DAY_FORMAT).parse("30.04.2017");

        OperationExpression op = OperationExpression.and(
                new ValueExpression("dayfield1", ConditionExpression.Comparison.GE, day1),
                new ValueExpression("dayfield2", ConditionExpression.Comparison.LE, day2));
        Expression optimzedExpression = op.optimize();
        assertEquals(2, ((OperationExpression) optimzedExpression).getOperands().size());
        assertTrue(((OperationExpression) optimzedExpression).getOperands().get(0) instanceof ValueExpression);
        assertTrue(((OperationExpression) optimzedExpression).getOperands().get(1) instanceof ValueExpression);

        op = OperationExpression.and(
                new ValueExpression("dayfield", ConditionExpression.Comparison.GE, day1),
                new ValueExpression("dayfield", ConditionExpression.Comparison.LE, day2));
        optimzedExpression = op.optimize();
        assertTrue(optimzedExpression instanceof RangeValueExpression);
    }

    @Test
    public void testDayRangeValueExpressionOptimization() throws Exception {
        final Date day1 = new SimpleDateFormat(STANDARD_DAY_FORMAT).parse("26.04.2017");
        final Date day2 = new SimpleDateFormat(STANDARD_DAY_FORMAT).parse("30.04.2017");

        OperationExpression op = OperationExpression.and(
                new DayExpression("dayfield1", ConditionExpression.Comparison.GE, day1),
                new DayExpression("dayfield2", ConditionExpression.Comparison.LE, day2));
        Expression optimzedExpression = op.optimize();
        assertEquals(2, ((OperationExpression) optimzedExpression).getOperands().size());
        assertTrue(((OperationExpression) optimzedExpression).getOperands().get(0) instanceof DayExpression);
        assertTrue(((OperationExpression) optimzedExpression).getOperands().get(1) instanceof DayExpression);

        op = OperationExpression.and(
                new DayExpression("dayfield", ConditionExpression.Comparison.GE, day1),
                new DayExpression("dayfield", ConditionExpression.Comparison.LE, day2));
        optimzedExpression = op.optimize();
        assertTrue(optimzedExpression instanceof DayRangeExpression);
    }
}
