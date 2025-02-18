package com.example.bankdatabase;

/**
 * The Savings class extends the Account class and defines the constants for interest rate,
 * loyal interest rate, monthly fee, minimum balance fee waived, and number of months
 */

public class Savings extends Account{
    protected boolean isLoyal; //loyal customer status
    private static final double INTEREST_RATE = 0.04; // 1.0% annual interest rate
    private static final double LOYAL_INTEREST_RATE = 0.0425;
    private static final double MONTHLY_FEE = 25.0;
    private static final double MIN_BALANCE_FEE_WAIVED = 500;
    private static final int NUM_MONTHS = 12;
    static final double zero = 0.0;

    /**
     * Constructs a new Savings object with the specified account holder and balance.
     *
     * @param holder  the account holder's profile
     * @param balance the initial account balance
     */
    public Savings(Profile holder, double balance) {
        super(holder, balance);

    }
    /**
     * Returns a formatted string representation of the Savings account, including loyal customer status.
     *
     * @return a string representation of the account in the format:
     * "Savings::FirstName LastName DateOfBirth::Balance $X.XX::is loyal"
     */
    @Override
    public String toString() {
        String loyalString = isLoyal ? "::is loyal" : "";
        return String.format("%s::%s %s %s::Balance $%,.2f%s", "Savings",
                holder.getFirstName(),
                holder.getLastName(),
                holder.getDateOfBirth().dateStr(),
                balance,
                loyalString);
    }
    /**
     * Returns a formatted string representation of the Savings account, including fees and interest.
     *
     * @return a string representation of the account in the format:
     * "Savings::FirstName LastName DateOfBirth::Balance $X.XX::is loyal::fee $X.XX::monthly interest $X.XX"
     */
    public String stringWithFees(){
        String feeString = String.format("$%.2f", monthlyFee());
        String interestString = String.format("$%.2f", monthlyInterest());
        String balanceString = String.format("$%,.2f", balance);
        String loyalty = isLoyal ? "::is loyal" : "";
        return String.format("Savings::%s %s %s::Balance %s%s::fee %s::monthly interest %s",
                holder.getFirstName(), holder.getLastName(), holder.getDateOfBirth().dateStr(),
                balanceString, loyalty, feeString, interestString);
    }
    /**
     * Compares this Savings account with another object for equality.
     *
     * @param obj the object to compare to
     * @return true if the objects are equal, false otherwise
     */

    @Override
    public boolean equals(Object obj){
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Savings savings = (Savings) obj;
        return savings.holder.equals(holder);
    }
    /**
     * Compares this Savings account with another object for equality in the context of transactions.
     *
     * @param obj the object to compare to
     * @return true if the objects are equal for transaction purposes, false otherwise
     */

    public boolean equalsForTransactions(Object obj){
        if (this == obj) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        Savings savings = (Savings) obj;
        return savings.holder.equals(holder);
    }

    /**
     * Compares this Savings account with another account for ordering based on account type, profile, and balance.
     *
     * @param o the account to compare to
     * @return a negative integer, zero, or a positive integer as this account is less than, equal to, or greater than the specified account
     */
    @Override
    public int compareTo(Account o) {
        int typeComparison = this.getClass().getSimpleName()
                .compareTo(o.getClass().getSimpleName());
        if(typeComparison > 0){
            return 1;
        }
        if(typeComparison < 0){
            return -1;
        }
        if(this.holder.compareTo(o.holder) > 0){
            return 1;
        }
        if(this.holder.compareTo(o.holder) < 0){
            return -1;
        }
        if(this.balance < o.balance) {
            return -1;
        }
        if (this.balance > o.balance) {
            return 1;
        }

        Savings savings = (Savings) o;

        return Boolean.compare(this.isLoyal, savings.isLoyal);
    }

    /**
     * Calculates and returns the monthly interest for the Savings account.
     *
     * @return the monthly interest amount
     */
    @Override
    public double monthlyInterest() {
        if(isLoyal){
            return balance*(LOYAL_INTEREST_RATE/NUM_MONTHS);
        }
        return balance*(INTEREST_RATE/NUM_MONTHS);
    }

    /**
     * Calculates and returns the monthly fee for the Savings account.
     *
     * @return the monthly fee amount
     */
    @Override
    public double monthlyFee() {
        if(balance >= MIN_BALANCE_FEE_WAIVED){
            return zero;
        }
        return MONTHLY_FEE;
    }

    /**
     * Sets the loyal customer status for the account.
     *
     * @param isLoyal true if the account holder is a loyal customer, false otherwise
     */
    public void setIsLoyal(boolean isLoyal) {
        this.isLoyal = isLoyal;
    }

}