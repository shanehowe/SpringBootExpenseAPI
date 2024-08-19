package org.ept.expensetracker.models;

import org.ept.expensetracker.expense.ExpenseCategory;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class ExpenseCategoryTest {

    @ParameterizedTest
    @MethodSource("provideExpenseCategoryStrings")
    void fromString(String categoryString, ExpenseCategory expectedCategory) {
        ExpenseCategory actualCategory = ExpenseCategory.fromString(categoryString);
        assertEquals(expectedCategory, actualCategory);
    }

    private static Stream<Arguments> provideExpenseCategoryStrings() {
        return Stream.of(
                Arguments.of("FOOD", ExpenseCategory.FOOD),
                Arguments.of("TRANSPORT", ExpenseCategory.TRANSPORT),
                Arguments.of("SHOPPING", ExpenseCategory.SHOPPING),
                Arguments.of("RENT", ExpenseCategory.RENT),
                Arguments.of("UTILITIES", ExpenseCategory.UTILITIES),
                Arguments.of("ENTERTAINMENT", ExpenseCategory.ENTERTAINMENT),
                Arguments.of("HEALTH", ExpenseCategory.HEALTH),
                Arguments.of("TRAVEL", ExpenseCategory.TRAVEL),
                Arguments.of("OTHER", ExpenseCategory.OTHER)
        );
    }
}