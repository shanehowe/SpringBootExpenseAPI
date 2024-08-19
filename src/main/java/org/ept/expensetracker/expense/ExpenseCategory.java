package org.ept.expensetracker.expense;

public enum ExpenseCategory {
    FOOD,
    TRANSPORT,
    SHOPPING,
    RENT,
    UTILITIES,
    ENTERTAINMENT,
    HEALTH,
    TRAVEL,
    OTHER;

    /**
     * Returns the ExpenseCategory enum value from the given string.
     * Example: <code>ExpenseCategory.fromString("food")</code> returns <code>ExpenseCategory.FOOD</code>
     * @param category The string representation of the category
     * @return ExpenseCategory that corresponds to the given string
     *        or null if the string does not match any category
     */
    public static ExpenseCategory fromString(String category) {
        for (ExpenseCategory c : ExpenseCategory.values()) {
            if (c.toString().equalsIgnoreCase(category)) {
                return c;
            }
        }
        return null;
    }
}
