package com.example.bankdatabase;

/**
 * The Account class is an abstract class that is general type of the other account types
 */

public abstract class Account implements Comparable<Account> {
    protected Profile holder;
    protected double balance;
    public abstract double monthlyInterest();
    public abstract double monthlyFee();

    /**
     * Creates a new Account with the specified account holder and balance.
     *
     * @param holder  the profile of the account holder
     * @param balance the initial balance of the account
     */
    public Account(Profile holder, double balance) {
        this.holder = holder;
        this.balance = balance;
    }

    /**
     * Returns a formatted string representation of the account, including any fees or additional information.
     *
     * @return a string representation of the account with fees or additional details
     */
    public abstract String stringWithFees();
    /**
     * Compares this account with another object for equality in the context of transactions.
     *
     * @param obj the object to compare to
     * @return true if the objects are equal for transaction purposes, false otherwise
     */
    public boolean equalsForTransactions(Object obj) {
        if (this == obj) {
            return true;
        }
        if(obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        Account a = (Account) obj;
        return this.holder.equals(a.holder) && this.balance == a.balance;
    }
}