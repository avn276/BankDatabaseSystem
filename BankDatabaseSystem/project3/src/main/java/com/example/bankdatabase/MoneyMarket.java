package com.example.bankdatabase;

/**
 * The MoneyMarket class extends the Savings class and defines the constants for interest rate,
 * loyal interest rate, minimum balance fee waived, monthly fee, withdrawals over minimum fee,
 * minimum withdrawals allowed, and number of months
 */

public class MoneyMarket extends Savings{
    private int withdrawal; //number of withdrawals
    private static final double INTEREST_RATE = 0.0450;
    private static final double LOYAL_INTEREST_RATE = 0.0475;
    public static final double MIN_BALANCE_FEE_WAIVED = 2000;
    private static final double MONTHLY_FEE = 25.0;
    private static final double WITHDRAWALS_OVER_MIN_FEE = 10.0;
    private static final int MIN_WITHDRAWALS_ALLOWED = 3;
    private static final int NUM_MONTHS = 12;


    /**
     * Constructs a new MoneyMarket object with the specified account holder, balance, and loyal customer status.
     *
     * @param holder   the account holder's profile
     * @param balance  the initial account balance
     * @param isLoyal  true if the account holder is a loyal customer, false otherwise
     */
    public MoneyMarket(Profile holder, double balance, boolean isLoyal) {
        super(holder, balance);
        isLoyal = true;
        this.isLoyal = isLoyal;
    }
    /**
     * Returns a formatted string representation of the MoneyMarket account, including the number of withdrawals.
     *
     * @return a string representation of the account in the format: "Money Market::Savings::withdrawal: X"
     */
    @Override
    public String toString() {
        return String.format("%s::Savings::withdrawal: %d", "Money market",
                super.toString(),
                withdrawal);
    }

    /**
     * Returns a formatted string representation of the MoneyMarket account, including fees, interest, and withdrawals.
     *
     * @return a string representation of the account in the format:
     * "Money Market::Savings::FirstName LastName DateOfBirth::Balance $X.XX::is loyal::withdrawal: X::fee $X.XX::monthly interest $X.XX"
     */
    public String stringWithFees(){
        String feeString = String.format("$%.2f", monthlyFee());
        String interestString = String.format("$%.2f", monthlyInterest());
        String balanceString = String.format("$%,.2f", balance);
        String loyalty = isLoyal ? "::is loyal" : "";
        return String.format("Money Market::Savings::%s %s %s::Balance %s%s::withdrawal: %d::fee %s::monthly interest %s",
                holder.getFirstName(), holder.getLastName(), holder.getDateOfBirth().dateStr(),
                balanceString, loyalty, withdrawal, feeString, interestString);
    }
    /**
     * Sets the number of withdrawals for the account.
     *
     * @param withdrawal the number of withdrawals to set
     */
    public void setWithdrawal(int withdrawal) {
        this.withdrawal = withdrawal;
    }

    /**
     * Compares this MoneyMarket account with another object for equality.
     *
     * @param obj the object to compare to
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object obj){
        if(this == obj) return true;
        if(obj == null || getClass() != obj.getClass()) return false;
        if(!super.equals(obj)) return false;
        MoneyMarket mmAccount = (MoneyMarket) obj;
        return true;
    }
    /**
     * Compares this MoneyMarket account with another object for equality in the context of transactions.
     *
     * @param obj the object to compare to
     * @return true if the objects are equal for transaction purposes, false otherwise
     */

    public boolean equalsForTransactions(Object obj){
         return super.equalsForTransactions(obj);
    }

    /**
     * Compares this MoneyMarket account with another account for ordering based on the number of withdrawals.
     *
     * @param o the account to compare to
     * @return a negative integer, zero, or a positive integer as this account is less than, equal to, or greater than the specified account
     */
    @Override
    public int compareTo(Account o) {
        int superComparison= super.compareTo(o);
        if(superComparison != 0){
            return superComparison;
        }
        MoneyMarket mmAccount = (MoneyMarket) o;
        if(this.withdrawal < mmAccount.withdrawal){
            return -1;
        }
        if(this.withdrawal > mmAccount.withdrawal){
            return 1;
        }
        return 0;
    }
    /**
     * Calculates and returns the monthly interest for the MoneyMarket account.
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
     * Calculates and returns the monthly fee for the MoneyMarket account.
     *
     * @return the monthly fee amount
     */
    @Override
    public double monthlyFee() {
        if(withdrawal > MIN_WITHDRAWALS_ALLOWED){
            if(balance >= MIN_BALANCE_FEE_WAIVED){
                return WITHDRAWALS_OVER_MIN_FEE;
            }
            return MONTHLY_FEE + WITHDRAWALS_OVER_MIN_FEE;
        }
        if(balance >= MIN_BALANCE_FEE_WAIVED){
            return 0.0;
        }

        return MONTHLY_FEE;
    }
    /**
     * Increments the number of withdrawals for the account.
     */
    public void incrementWithdrawals(){
        withdrawal++;
    }
}