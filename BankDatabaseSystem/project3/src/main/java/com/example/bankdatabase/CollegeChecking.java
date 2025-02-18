package com.example.bankdatabase;

/**
 * The CollegeChecking extends the Checking class and defines the constants for interest
 */

public class CollegeChecking extends Checking{
    private Campus campus; //campus code
    private static final double INTEREST_RATE = 0.01; // 1.0% annual interest rate

    /**
     * Constructs a new CollegeChecking object with the specified account holder, balance, and campus code.
     *
     * @param holder  the account holder's profile
     * @param balance the initial account balance
     * @param campus  the campus code associated with the account
     */
    public CollegeChecking(Profile holder, double balance, Campus campus) {
        super(holder, balance);
        this.campus = campus;
    }

    /**
     * Calculates and returns the monthly fee for the College Checking account, which is always zero.
     *
     * @return the monthly fee (always zero for College Checking)
     */
    @Override
    public double monthlyFee() {
        return Savings.zero; // No monthly fee for College Checking
    }

    /**
     * Returns a formatted string representation of the College Checking account.
     *
     * @return a string representation of the account in the format: "College Checking::FirstName LastName DateOfBirth::Balance $X.XX::Campus Code"
     */
    @Override
    public String toString() {
        String balancestring = String.format("$%,.2f", balance);
        return String.format("College Checking::%s %s %s::Balance %s::%s", holder.getFirstName(), holder.getLastName(), holder.getDateOfBirth().dateStr(), balancestring, campus.toString());
    }

    /**
     * Returns a formatted string representation of the College Checking account, including fees and monthly interest.
     *
     * @return a string representation of the account with fees and interest in the format: "College Checking::FirstName LastName DateOfBirth::Balance $X.XX::Campus Code::Fee $X.XX::Monthly Interest $X.XX"
     */

    public String stringWithFees(){
        String feeString = String.format("$%.2f", monthlyFee());
        String interestString = String.format("$%.2f", monthlyInterest());
        String balanceString = String.format("$%,.2f", balance);
        return String.format("College Checking::%s %s %s::Balance %s::%s::fee %s::monthly interest %s",
                holder.getFirstName(), holder.getLastName(), holder.getDateOfBirth().dateStr(),
                balanceString, campus, feeString, interestString);
    }

    /**
     * Compares this CollegeChecking account with another object for equality.
     *
     * @param obj the object to compare to
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        return super.equals(obj);
    }

    /**
     * Compares this CollegeChecking account with another object for equality in the context of transactions.
     *
     * @param obj the object to compare to
     * @return true if the objects are equal for transaction purposes, false otherwise
     */

    public boolean equalsForTransactions(Object obj){
        if (this == obj) return true;
        if (obj == null || !(obj instanceof Checking) ) return false;
        return super.equalsForTransactions(obj);
    }

    /**
     * Compares this CollegeChecking account with another account for ordering based on the campus code.
     *
     * @param account the account to compare to
     * @return a negative integer, zero, or a positive integer as this account is less than, equal to, or greater than the specified account
     */
    @Override
    public int compareTo(Account account){
        int superComparison = super.compareTo(account);
        if(superComparison != 0){
            return superComparison;
        }
        return this.campus.compareTo(((CollegeChecking) account).campus);
    }

}