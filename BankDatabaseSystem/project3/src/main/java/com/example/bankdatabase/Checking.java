package com.example.bankdatabase;

/**
 *The Checking class extends the Account class and provides
 * specific implementations for a checking account.
 */
public class Checking extends Account {
    private static final double INTEREST_RATE = 0.01; // 1.0% annual interest rate
    private static final double MONTHLY_FEE = 12.0;
    private static final double MIN_BALANCE_FEE_WAIVED = 1000;
    private static final int NUM_MONTHS = 12;

    /**
     * Constructor to initialize the holder and balance for a Checking account.
     * @param holder  The profile of the account holder.
     * @param balance The initial balance of the account.
     */
    public Checking(Profile holder, double balance) {
        super(holder, balance);
    }


    /**
     * Calculates the monthly interest for the account.
     * @return The monthly interest amount.
     */
    @Override
    public double monthlyInterest() {
        return balance * (INTEREST_RATE / NUM_MONTHS);
    }

    /**
     * Calculates the monthly fee for the account.
     * @return The monthly fee amount.
     */
    @Override
    public double monthlyFee() {
        if (balance >= MIN_BALANCE_FEE_WAIVED) {
            return 0.0;
        }
        return MONTHLY_FEE;
    }

    /**
     * Returns a string representation of the Checking account.
     * @return A string representing the account.
     */
    @Override
    public String toString(){
        return String.format("%s::%s %s %s::Balance $%,.2f",
                getClass().getSimpleName(),
                holder.getFirstName(),
                holder.getLastName(),
                holder.getDateOfBirth().dateStr(),
                balance);
    }

    /**
     * Returns a string representation of the Checking account with fees and interest.
     * @return A string representing the account with fees and interest.
     */
    public String stringWithFees(){
        String feeString = String.format("$%.2f", monthlyFee());
        String interestString = String.format("$%.2f", monthlyInterest());
        String balanceString = String.format("$%,.2f", balance);
        return String.format("Checking::%s %s %s::Balance %s::fee %s::monthly interest %s",
                holder.getFirstName(), holder.getLastName(), holder.getDateOfBirth().dateStr(),
                balanceString, feeString, interestString);
    }


    /**
     * Checks equality based on the holder's profile.
     * @param obj The object to compare.
     * @return true if equal, false otherwise.
     */
    @Override
    public boolean equals(Object obj){
        if (this == obj) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        Checking checking = (Checking) obj;
        return checking.holder.equals(holder);
    }


    /**
     * Checks equality for transactions
     * @param obj The object to compare.
     * @return true if equal, false otherwise.
     */
    public boolean equalsForTransactions(Object obj){
        if (this == obj) return true;
        if(!(obj instanceof Checking)) return false;
        Checking checking = (Checking) obj;
        return checking.holder.equals(holder);
    }

    /**
     * Compares two Account objects.
     * @param open The Account object to compare.
     * @return An integer representing the comparison result.
     */
    @Override
    public int compareTo(Account open) {
        int typeComparison = this.getClass().getSimpleName().compareTo(open.getClass().getSimpleName());
        if(typeComparison > 0){
            return 1;
        }
        if(typeComparison < 0){
            return -1;
        }
        if(this.holder.compareTo(open.holder) > 0){
            return 1;
        }
        if(this.holder.compareTo(open.holder) < 0){
            return -1;
        }
        if (this.balance < open.balance) {
            return -1;
        }
        if (this.balance > open.balance) {
            return 1;
        }
        return 0;
    }
}