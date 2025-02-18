package com.example.bankdatabase;

/**
 *  The AccountDatabase class represents a database for linear data structure to hold a list
 *  of accounts with different types
 */
public class AccountDatabase {
    private static final int INCREMENT_AMOUNT = 4;
    private static final int INITIAL_CAPACITY = 4;
    private Account[] accounts; //list of various types of accounts
    private int numAcct; //number of accounts in the array
    private static final int NOT_FOUND = -1;
    private static final int STARTING_NUM_ACCT = 0;

    /**
     * Initializes an empty account database with initial capacity.
     */
    public AccountDatabase(){
        accounts = new Account[INITIAL_CAPACITY];
        numAcct = STARTING_NUM_ACCT;
    }

    /**
     * Finds the index of a given account in the database.
     * @param account The account to find.
     * @return The index of the account, or NOT_FOUND if not found.
     */
    private int find(Account account) {
        int index = NOT_FOUND;
        for (int i = 0; i < numAcct; i++) {
            if (accounts[i].equals(account)) {
                index = i;
                break;
            }
        }
        return index;
    }

    /**
     * Increases the capacity of the account database by a fixed increment.
     */
    private void grow(){
        Account[] copy = new Account[numAcct + INCREMENT_AMOUNT];
        for(int i = 0; i < numAcct; i++){
            copy[i] = accounts[i];
        }
        accounts = copy;

    }

    /**
     * Checks if an account exists in the database.
     * @param account The account to check.
     * @return true if the account exists, false otherwise.
     */
    public boolean contains(Account account){
        return find(account) != NOT_FOUND;
    }
    public boolean contains(Checking c) {
        int i = NOT_FOUND;
        for(int j = 0; j < numAcct; j++) {
            if(accounts[j].equalsForTransactions(c)) {
                return true;
            }

        }
        return false;
    }
    /**
     * Opens a new account and adds it to the database.
     * @param account The account to open.
     * @return true if the account was successfully opened, false otherwise.
     */
    public boolean open(Account account){
       if(account.getClass().equals(CollegeChecking.class)) {
           if(contains((CollegeChecking) account)) {
               return false;
           }
       } else if (account.getClass().equals(Checking.class)) {
           if(contains((Checking) account) ) {
               return false;
           }
       } else if(contains(account)) {
           return false;
       }
       if(numAcct >= accounts.length) {
           grow();
       }
       accounts[numAcct] = account;
       numAcct++;
       return true;
    }


    /**
     * Closes an existing account and removes it from the database.
     * @param account The account to close.
     * @return true if the account was successfully closed, false otherwise.
     */
    public boolean close(Account account){
        int removeIndex = find(account);
        if (removeIndex != NOT_FOUND) {
            for (int i = removeIndex; i < numAcct - 1; i++) {
                accounts[i] = accounts[i + 1];
            }
            numAcct--;
            accounts[numAcct] = null;
            return true;
        }
        return false;
    }

    /**
     * Withdraws an amount from an existing account.
     * @param account The account from which to withdraw.
     * @return true if the withdrawal was successful, false otherwise.
     */
    public boolean withdraw(Account account){
        int index = find(account);
        double wd = account.balance;
        if (index == NOT_FOUND) {
            return false;
        }
        Account acct = accounts[index];
        account.balance = accounts[index].balance;
        if (acct.balance < wd) {
            return false;
        }
        acct.balance -= wd;
        if (acct instanceof MoneyMarket) {
            MoneyMarket mmAccount = (MoneyMarket) acct;
            mmAccount.incrementWithdrawals();
            if (mmAccount.balance < MoneyMarket.MIN_BALANCE_FEE_WAIVED) {
                mmAccount.isLoyal = false;
            }
            accounts[index] = mmAccount;
        }
        return true;
    }

    /**
     * Sorts the accounts array based on the account
     * type using the Selection Sort algorithm.
     */
    private void selectionSortAccountType() {
        int n = numAcct;

        for (int i = 0; i < n-1; i++) {
            int minIdx = i;
            for (int j = i+1; j < n; j++) {
                if (accounts[j].compareTo(accounts[minIdx]) < 0) {
                    minIdx = j;
                }
            }

            Account temp = accounts[minIdx];
            accounts[minIdx] = accounts[i];
            accounts[i] = temp;
        }
    }

    /**
     * Finds the index of an account that matches the given account for transactions.
     * @param account The account to find.
     * @return The index of the matching account, or NOT_FOUND if not found.
     */
    private int findForTransactions(Account account){
        int index = NOT_FOUND;
        for (int i = 0; i < numAcct; i++) {
            if (accounts[i].equals(account)) {
                index = i;
                break;
            }
        }
        return index;
    }

    /**
     * Deposits the balance into the account if it exists.
     * @param account The account to deposit into.
     */
    public void deposit(Account account){
        int index = find(account);
        if (index == NOT_FOUND) {
            return;
        }
        accounts[index].balance += account.balance;
        Account a = accounts[index];
        if(a instanceof MoneyMarket) {
            MoneyMarket ac = (MoneyMarket) a;
            if(ac.balance >= MoneyMarket.MIN_BALANCE_FEE_WAIVED) {
                ac.isLoyal = true;
            }
            accounts[index] = ac;
        }
    }

    /**
     * Prints the sorted list of accounts.
     */
    public void printSorted(){
        selectionSortAccountType();
        for (int i = 0; i < numAcct; i++) {
            System.out.println(accounts[i].toString());
        }
    }

    /**
     * Prints the list of accounts along with their fees.
     */
    public void printFeesAndInterests(){ //calculate interests/fees
        selectionSortAccountType();
        for(int i = 0; i< numAcct; i++){
            System.out.println(accounts[i].stringWithFees());
        }
    }

    /**
     * Applies the monthly interest and fees to the accounts and prints the updated balances.
     */
    public void printUpdatedBalances(){ //apply the interests/fees
        selectionSortAccountType();
        for(int i = 0; i < numAcct; i++){
            accounts[i].balance += accounts[i].monthlyInterest();
            accounts[i].balance -= accounts[i].monthlyFee();
            if(accounts[i] instanceof MoneyMarket) {
                MoneyMarket mm = (MoneyMarket) accounts[i];
                mm.setWithdrawal(0);
                accounts[i] = mm;
            }
            System.out.println(accounts[i].toString());
        }
    }
    /**
     * Checks if the account database contains the specified account for transaction purposes.
     *
     * @param a the account to check for in the database
     * @return true if the account exists in the database for transaction purposes, false otherwise
     */
    public boolean containsForTransactions(Account a) {
        return findForTransactions(a) != NOT_FOUND;
    }
    /**
     * Converts the current state of the account database to a string representation.
     * Prior to creating the string representation, it sorts the accounts by account type
     * using the selection sort algorithm.
     *
     * @return A string representation of the account database, with each account's details
     *         followed by a newline character.
     */
    public String accountDataBaseToString() {
        selectionSortAccountType();
        String str = "";
        for(int i = 0; i < numAcct; i++) {
            str += accounts[i].toString() + "\n";
        }
        return str;
    }
    /**
     * Generates a string representation of the entire account database.
     *
     * @return a string representation of all accounts in the database
     */
    public String accountDataBaseFeesToString() {
        selectionSortAccountType();
        String str = "";
        for(int i  = 0; i < numAcct; i++) {
            str += accounts[i].stringWithFees() + "\n";
        }
        return str;
    }
    /**
     * Generates a string representation of the account database, including fees for each account.
     *
     * @return a string representation of all accounts in the database with fees
     */
    public String accountDataBaseUBToString() {
        selectionSortAccountType();
        String str = "";
        selectionSortAccountType();
        for(int i = 0; i < numAcct; i++) {
            accounts[i].balance += accounts[i].monthlyInterest();
            accounts[i].balance -= accounts[i].monthlyFee();
            if(accounts[i] instanceof MoneyMarket) {
                MoneyMarket mm = (MoneyMarket) accounts[i];
                mm.setWithdrawal(0);
                accounts[i] = mm;
            }
            str += accounts[i].toString() + "\n";
        }
        return str;
    }

    /**
     * Checks if the accounts array is empty.
     * @return true if empty, false otherwise.
     */
    public boolean isEmpty(){
        return numAcct == 0;
    }

    /**
     * Getter method for NumAcct
     * @return the value of numAcct
     */
    public int getNumAcct() {
        return numAcct;
    }
}