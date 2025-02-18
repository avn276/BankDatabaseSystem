package com.example.bankdatabase;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.event.ActionEvent;
import java.util.Optional;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Scanner;

/**
 * This class is the controller for managing bank account transactions and provides
 * methods to open, close, deposit, withdraw, and display account information.
 */

public class TransactionManagerController {
    @FXML
    private TextField firstName, lastName, openDeposit, firstNameDW, lastNameDW, depositOrWithdraw;
    @FXML
    private DatePicker dob, dobDW;
    @FXML
    private RadioButton checking, collegeChecking, savings, moneyMarket,
            checkingDW, collegeCheckingDW, savingsDW, moneyMarketDW, loyal;
    @FXML
    private Button open, close, withdraw, deposit, loadAccounts;
    @FXML
    private ToggleGroup accountTypeGroup, accountTypeGroupDW;
    @FXML
    private ToggleGroup campus;
    @FXML
    private VBox campusGroupContainer, loyalContainer;
    @FXML
    private TextArea openCloseOutput, depositWithdrawOutput, databaseOutput;
    String[] fields = new String[FIELDS_FOR_OPEN_CLOSE];
    private AccountDatabase accountDatabase = new AccountDatabase();
    Date accountDob;
    double initialDeposit;
    double amount;
    private static final int FIELDS_FOR_OPEN_CLOSE = 6;
    private static final int FNAME_INPUT = 0;
    private static final int LNAME_INPUT = 1;
    private static final int DEPOSIT_INPUT = 2;
    private static final int ZERO_QUANTITY = 0;
    private static final double MIN_AGE_TO_TO_OPEN = 16;
    private static final double MAX_AGE_TO_OPEN_CC = 24;
    private static final String LOYAL = "1";
    private static final int ACCOUNT_TYPE_PART = 0;
    private static final int FNAME_PART = 1;
    private static final int LNAME_PART = 2;
    private static final int DOB_PART = 3;
    private static final int INITIAL_DEPOSIT_PART = 4;
    private static final int LOYALTY_PART = 5;
    private static final int CAMPUS_CODE_PART = 5;
    private static final int DATE_COMPONENTS_LENGTH = 3;
    private static final int YEAR_PART = 2;
    private static final int MONTH_PART = 0;
    private static final int DAY_PART = 1;

    /**
     * Handles the action of loading account data from a file.
     */
    @FXML
    protected void loadAccountsFromFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Accounts File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text Files", "*.txt"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );
        File selectedFile = fileChooser.showOpenDialog(
                loadAccounts.getScene().getWindow());

        if (selectedFile != null) {
            processFile(selectedFile);
        }
    }

    /**
     * Processes the contents of the file to create and open accounts.
     * @param file The file containing account data to be processed.
     */

    private void processFile(File file) {
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line == null || line.trim().isEmpty()) {
                    break;
                }
                String[] parts = line.split(",");
                createAccountFromParts(parts);
            }
            databaseOutput.setText("Accounts loaded successfully!");
        } catch (IOException e) {
            displayAlert("File Error", "Error while loading the accounts file: "
                    + e.getMessage());
        }
    }

    /**
     * Create and open an account based on the input parts.
     * @param parts An array of parts representing an account.
     */

    private void createAccountFromParts(String[] parts) {
        String accountType = parts[ACCOUNT_TYPE_PART];
        String fName = parts[FNAME_PART];
        String lName = parts[LNAME_PART];
        Date dob = dateParse(parts[DOB_PART]);
        double initialDeposit = Double.parseDouble(parts[INITIAL_DEPOSIT_PART]);

        switch (accountType) {
            case "S" -> {
                String loyal = parts[LOYALTY_PART];
                openSL(fName, lName, dob, initialDeposit, loyal);
            }
            case "CC" -> {
                String campusCode = parts[CAMPUS_CODE_PART];
                openCCL(fName, lName, dob,
                        initialDeposit, campusCode);
            }
            case "C" -> openChecking(fName, lName, dob, initialDeposit);
            case "MM" -> openMM(fName, lName, dob, initialDeposit);
            default -> System.out.println("Unknown account type: "
                    + accountType);
        }
    }

    /**
     * Handle opening an account based on user input.
     * @param event The event that triggered this action.
     */

    @FXML
    protected void handleOpen(ActionEvent event) {
        RadioButton selectedRadioButton = (RadioButton)
                accountTypeGroup.getSelectedToggle();
        String accountType = selectedRadioButton.getText();
        if(checkOpenField() && validInitialDeposit() &&
                checkAge(accountDob, accountType)){
            switch (accountType) {
                case "Checking" -> openChecking(fields[FNAME_INPUT],
                        fields[LNAME_INPUT], accountDob, initialDeposit);
                case "College Checking" -> openCC(fields[FNAME_INPUT],
                        fields[LNAME_INPUT], accountDob, initialDeposit);
                case "Savings" -> openSavings(fields[FNAME_INPUT],
                        fields[LNAME_INPUT], accountDob, initialDeposit);
                case "Money Market" -> openMM(fields[FNAME_INPUT],
                        fields[LNAME_INPUT], accountDob, initialDeposit);
            }
        }
    }

    /**
     * Handle closing an account based on user input.
     * @param event The event that triggered this action.
     */
    @FXML
    protected void closeHandle(ActionEvent event){
        RadioButton selectedRadioButton = (RadioButton)
                accountTypeGroup.getSelectedToggle();
        String accountType = selectedRadioButton.getText();
        if(checkFieldsClose() &&
                checkAge(accountDob, accountType)){
            switch (accountType) {
                case "Checking" -> closeChecking(fields[FNAME_INPUT],
                        fields[LNAME_INPUT], accountDob);
                case "College Checking" -> closeCC(fields[FNAME_INPUT],
                        fields[LNAME_INPUT], accountDob);
                case "Savings" -> closeSavings(fields[FNAME_INPUT],
                        fields[LNAME_INPUT], accountDob);
                case "Money Market" -> closeMM(fields[FNAME_INPUT],
                        fields[LNAME_INPUT], accountDob);
            }
        }
    }
    /**
     * Handle depositing funds into an account based on user input.
     *
     * @param event The event that triggered this action.
     */

    @FXML
    protected void handleDep(ActionEvent event) {
        RadioButton selectedRadioButton =
                (RadioButton)accountTypeGroupDW.getSelectedToggle();
        String accountType = selectedRadioButton.getText();
        if (checkFieldsDepositWithdraw() && validDeposit()) {
            switch (accountType) {
                case "Checking" -> depositChecking(fields[FNAME_INPUT],
                        fields[LNAME_INPUT], accountDob, amount);
                case "College Checking" -> depositCC(fields[FNAME_INPUT],
                        fields[LNAME_INPUT], accountDob, amount);
                case "Savings" -> depositSavings(fields[FNAME_INPUT],
                        fields[LNAME_INPUT], accountDob, amount);
                case "Money Market" -> depositMM(fields[FNAME_INPUT],
                        fields[LNAME_INPUT], accountDob, amount);
            }
        }
    }

    /**
     * Handle withdrawing funds from an account based on user input.
     *
     * @param event The event that triggered this action.
     */
    @FXML
    protected void withdrawHandle(ActionEvent event) {
        RadioButton selectedRadioButton =
                (RadioButton)accountTypeGroupDW.getSelectedToggle();
        String accountType = selectedRadioButton.getText();
        if (checkFieldsDepositWithdraw() && validWithdraw()) {
            switch (accountType) {
                case "Checking" -> withdrawChecking(fields[FNAME_INPUT],
                        fields[LNAME_INPUT], accountDob, amount);
                case "College Checking" -> withdrawCC(fields[FNAME_INPUT],
                        fields[LNAME_INPUT], accountDob, amount);
                case "Savings" -> withdrawSavings(fields[FNAME_INPUT],
                        fields[LNAME_INPUT], accountDob, amount);
                case "Money Market" -> withdrawMM(fields[FNAME_INPUT],
                        fields[LNAME_INPUT], accountDob, amount);
            }
        }
    }
    /**
     * Print account information to the database output area.
     *
     * @param event The event that triggered this action.
     */

    @FXML
    protected void printHandle(ActionEvent event){
        String textToDisplay = "";
        if (!(accountDatabase.isEmpty())) {
            textToDisplay += "*Accounts sorted by account type and profile.\n";
            textToDisplay += accountDatabase.accountDataBaseToString();
            textToDisplay += ("*end of list.\n");
        } else textToDisplay += "Account Database is empty!";

        databaseOutput.setText(textToDisplay);
    }

    /**
     * Print account information with additional fees to the database output area.
     *
     * @param event The event that triggered this action.
     */
    @FXML
    protected void printWithAdditionalFeesHandle(ActionEvent event){
        String textToDisplay = "";
        if (!(accountDatabase.isEmpty())) {
            textToDisplay += "*Accounts sorted by account type and profile.\n";
            textToDisplay += accountDatabase.accountDataBaseFeesToString();
            textToDisplay += ("*end of list.\n");
        } else textToDisplay += "Account Database is empty!";

        databaseOutput.setText(textToDisplay);
    }

    /**
     * Update the current balances of accounts and display the results in the database output area.
     *
     * @param event The event that triggered this action.
     */

    @FXML
    protected void updateCurrentBalancesHandle(ActionEvent event){
        String textToDisplay = "";
        if (!(accountDatabase.isEmpty())) {
            textToDisplay += "*Accounts sorted by account type and profile.\n";
            textToDisplay += accountDatabase.accountDataBaseUBToString();
            textToDisplay += ("*end of list.\n");
        } else textToDisplay += "Account Database is empty!";

        databaseOutput.setText(textToDisplay);
    }

    /**
     * private method to open a checking account.
     *
     * @param fName           First name of the account holder.
     * @param lName           Last name of the account holder.
     * @param dob             Date of birth of the account holder.
     * @param initialDeposit  Initial deposit amount.
     */

    private void openChecking(String fName, String lName, Date dob,
                              double initialDeposit) {
        Checking newChecking = new Checking(new Profile(fName, lName, dob),
                initialDeposit);
        openAcc(fName, lName, dob, newChecking, "C");
    }

    /**
     * Opens a College Checking account with the given information and initial deposit.
     *
     * @param fName The first name of the account holder.
     * @param lName The last name of the account holder.
     * @param dob The date of birth of the account holder.
     * @param initialDeposit The initial deposit for the account.
     */

    private void openCC(String fName, String lName, Date dob,
                        double initialDeposit) {
        Campus campusEnum = null;
        try {
            RadioButton selectedCampus = (RadioButton) campus.getSelectedToggle();
            String campusString = selectedCampus.getText();
            String campusCode = convertToCode(campusString);
            campusEnum = Campus.fromCode(campusCode);
            CollegeChecking newCollegeChecking = new CollegeChecking(new
                    Profile(fName, lName, dob), initialDeposit, campusEnum);
            if(!checkAge(dob,"CC")){
                return;
            }
            openAcc(fName, lName, dob, newCollegeChecking, "CC");
        } catch (NullPointerException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Missing Data");
            alert.setHeaderText("Please select campus");
            alert.showAndWait();
        }
    }

    /**
     * Opens a College Checking account with the given information and initial deposit from loaded data.
     *
     * @param fName The first name of the account holder.
     * @param lName The last name of the account holder.
     * @param dob The date of birth of the account holder.
     * @param initialDeposit The initial deposit for the account.
     * @param campusCode The code representing the campus associated with the account.
     */

    private void openCCL(String fName, String lName,
                         Date dob, double initialDeposit,
                         String campusCode) {
        Campus campusEnum = null;
        try {
            campusEnum = Campus.fromCode(campusCode);
            CollegeChecking newCollegeChecking = new CollegeChecking(new
                    Profile(fName, lName, dob), initialDeposit, campusEnum);
            openAcc(fName, lName, dob, newCollegeChecking, "CC");
        } catch (NullPointerException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Missing Data");
            alert.setHeaderText("Please enter campus code");
            alert.showAndWait();
        }
    }

    /**
     * Opens a Savings account with the given information and initial deposit.
     *
     * @param fName The first name of the account holder.
     * @param lName The last name of the account holder.
     * @param dob The date of birth of the account holder.
     * @param initialDeposit The initial deposit for the account.
     */
    private void openSavings(String fName, String lName, Date dob,
                             double initialDeposit) {
        Savings newSavings = new Savings(new Profile(fName, lName, dob),
                initialDeposit);
        if(loyal.isSelected()){
            newSavings.isLoyal = true;
        }
        openAcc(fName, lName, dob, newSavings, "S");
    }

    /**
     * Opens a Savings account with the given information and initial deposit from loaded data.
     *
     * @param fName The first name of the account holder.
     * @param lName The last name of the account holder.
     * @param dob The date of birth of the account holder.
     * @param initialDeposit The initial deposit for the account.
     * @param loyalty A string representing whether the account is loyal.
     */

    public void openSL(String fName, String lName, Date dob,
                       double initialDeposit, String loyalty){
        Savings newSavings = new Savings(new Profile(fName, lName, dob),
                initialDeposit);
        if(loyalty.equals(LOYAL)){
            newSavings.isLoyal = true;
        }
        openAcc(fName, lName, dob, newSavings, "S");
    }

    /**
     * Opens a Money Market account with the given information and initial deposit.
     *
     * @param fName The first name of the account holder.
     * @param lName The last name of the account holder.
     * @param dob The date of birth of the account holder.
     * @param initialDeposit The initial deposit for the account.
     */

    private void openMM(String fName, String lName, Date dob,
                        double initialDeposit) {
        if (initialDeposit < MoneyMarket.MIN_BALANCE_FEE_WAIVED) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Please enter valid amount");
            alert.setHeaderText("Minimum of $2000 to open a Money " +
                    "Market account.");
            alert.showAndWait();
            return;
        }
        MoneyMarket newMoneyMarket = new MoneyMarket(new
                Profile(fName, lName, dob), initialDeposit, true);
        openAcc(fName, lName, dob, newMoneyMarket, "MM");
    }

    /**
     * Opens an account with the provided information.
     *
     * @param fName The first name of the account holder.
     * @param lName The last name of the account holder.
     * @param dob The date of birth of the account holder.
     * @param account The account to be opened.
     * @param accountType The type of the account.
     */

    @FXML
    private void openAcc(String fName, String lName, Date dob,
                         Account account, String accountType) {
        if (accountDatabase.open(account)) {
            openCloseOutput.setText(fName + " " + lName + " " +
                    dob.dateStr() + "(" + accountType + ") opened.");

        } else {
            openCloseOutput.setText(fName + " " + lName + " " + dob.dateStr()
                    + "(" + accountType + ") is already in the database.");
        }
    }

    /**
     * private method to close a checking account.
     *
     * @param fName First name of the account holder.
     * @param lName Last name of the account holder.
     * @param dob   Date of birth of the account holder.
     */

    private void closeChecking(String fName, String lName, Date dob){
        Profile profileToClose = new Profile(fName, lName, dob);
        Checking accountToClose = new Checking(profileToClose, ZERO_QUANTITY);
        closeAcc(fName, lName, dob, accountToClose, "C");
    }

    /**
     * Closes a College Checking account associated with the given account holder's information.
     *
     * @param fName The first name of the account holder.
     * @param lName The last name of the account holder.
     * @param dob The date of birth of the account holder.
     */

    private void closeCC(String fName, String lName, Date dob){
        Profile profileToClose = new Profile(fName, lName, dob);
        CollegeChecking accountToClose = new CollegeChecking(profileToClose, ZERO_QUANTITY, null);
        closeAcc(fName, lName, dob, accountToClose, "CC");
    }

    /**
     * Closes a Savings account associated with the given account holder's information.
     *
     * @param fName The first name of the account holder.
     * @param lName The last name of the account holder.
     * @param dob The date of birth of the account holder.
     */
    private void closeSavings(String fName, String lName, Date dob){
        Profile profileToClose = new Profile(fName, lName, dob);
        Savings accountToClose = new Savings(profileToClose, ZERO_QUANTITY);
        closeAcc(fName, lName, dob, accountToClose, "S");
    }

    /**
     * Closes a Money Market account associated with the given account holder's information.
     *
     * @param fName The first name of the account holder.
     * @param lName The last name of the account holder.
     * @param dob The date of birth of the account holder.
     */

    private void closeMM(String fName, String lName, Date dob){
        Profile profileToClose = new Profile(fName, lName, dob);
        MoneyMarket accountToClose = new MoneyMarket(profileToClose, ZERO_QUANTITY, true);
        closeAcc(fName, lName, dob, accountToClose, "MM");
    }

    /**
     * Closes the specified account associated with the given account holder's information.
     *
     * @param fName The first name of the account holder.
     * @param lName The last name of the account holder.
     * @param dob The date of birth of the account holder.
     * @param account The account to be closed.
     * @param accountType The type of the account.
     */

    private void closeAcc(String fName, String lName, Date dob,
                          Account account, String accountType) {
        if (accountDatabase.close(account)) {
            openCloseOutput.setText(fName + " " + lName + " " + dob.dateStr()
                    + "(" + accountType + ") has been closed.");
        } else {
            openCloseOutput.setText(fName + " " + lName + " " + dob.dateStr()
                    + "(" + accountType + ") is not in the database.");
        }
    }

    /**
     * Private method to deposit funds into a checking account.
     *
     * @param fName    First name of the account holder.
     * @param lName    Last name of the account holder.
     * @param dob      Date of birth of the account holder.
     * @param deposit  Amount to deposit.
     */

    private void depositChecking(String fName, String lName, Date dob, double deposit) {
        Profile profileToDeposit = new Profile(fName, lName, dob);
        Checking accountToDeposit = new Checking(profileToDeposit, deposit);
        depositAcc(fName, lName, dob, accountToDeposit, "C");
    }

    /**
     * Deposits funds into a College Checking account associated with the given account holder's information.
     *
     * @param fName The first name of the account holder.
     * @param lName The last name of the account holder.
     * @param dob The date of birth of the account holder.
     * @param deposit The amount to be deposited into the account.
     */

    private void depositCC(String fName, String lName, Date dob, double deposit) {
        Profile profileToDeposit = new Profile(fName, lName, dob);
        CollegeChecking accountToDeposit = new CollegeChecking(profileToDeposit, deposit, null);
        depositAcc(fName, lName, dob , accountToDeposit,"CC");
    }

    /**
     * Deposits funds into a Savings account associated with the given account holder's information.
     *
     * @param fName The first name of the account holder.
     * @param lName The last name of the account holder.
     * @param dob The date of birth of the account holder.
     * @param deposit The amount to be deposited into the account.
     */

    private void depositSavings(String fName, String lName, Date dob, double deposit) {
        Profile profileToDeposit = new Profile(fName, lName, dob);
        Savings accountToDeposit = new Savings(profileToDeposit, deposit);
        depositAcc(fName, lName, dob, accountToDeposit, "S");
    }

    /**
     * Deposits funds into a Money Market account associated with the given account holder's information.
     *
     * @param fName The first name of the account holder.
     * @param lName The last name of the account holder.
     * @param dob The date of birth of the account holder.
     * @param deposit The amount to be deposited into the account.
     */

    private void depositMM(String fName, String lName, Date dob, double deposit) {
        Profile profileToDeposit = new Profile(fName, lName, dob);
        MoneyMarket accountToDeposit = new MoneyMarket(profileToDeposit, deposit, true);
        depositAcc(fName, lName, dob, accountToDeposit, "MM");
    }

    /**
     * Deposits funds into the specified account associated with the given account holder's information.
     *
     * @param fName The first name of the account holder.
     * @param lName The last name of the account holder.
     * @param dob The date of birth of the account holder.
     * @param account The account to which the deposit is made.
     * @param accountType The type of the account.
     */
    private void depositAcc(String fName, String lName, Date dob, Account
            account, String accountType) {
        if(accountDatabase.containsForTransactions(account)) {
            accountDatabase.deposit(account);
            depositWithdrawOutput.setText(fName + " " + lName + " " +
                    dob.dateStr() + "(" + accountType + ") Deposit - " +
                    "balance updated.");
        } else
            depositWithdrawOutput.setText(fName + " " + lName + " " +
                    dob.dateStr() + "(" + accountType + ") " +
                    "is not in the database.");

    }

    /**
     * Withdraw funds from a checking account.
     *
     * @param fName    First name of the account holder.
     * @param lName    Last name of the account holder.
     * @param dob      Date of birth of the account holder.
     * @param withdraw Amount to withdraw.
     */
    private void withdrawChecking(String fName, String lName, Date dob, double withdraw) {
        Profile profileToWithdraw = new Profile(fName, lName, dob);
        Checking accountToWithdraw = new Checking(profileToWithdraw, withdraw);
        withdrawAcc(fName, lName, dob, accountToWithdraw, withdraw, "C");
    }

    /**
     * Withdraws funds from a College Checking account associated with the given account holder's information.
     *
     * @param fName The first name of the account holder.
     * @param lName The last name of the account holder.
     * @param dob The date of birth of the account holder.
     * @param withdraw The amount to be withdrawn from the account.
     */
    private void withdrawCC(String fName, String lName, Date dob, double withdraw) {
        Profile profileToWithdraw = new Profile(fName, lName, dob);
        CollegeChecking accountToWithdraw = new CollegeChecking(profileToWithdraw, withdraw, null);
        withdrawAcc(fName, lName, dob, accountToWithdraw, withdraw, "CC");
    }

    /**
     * Withdraws funds from a Savings account associated with the given account holder's information.
     *
     * @param fName The first name of the account holder.
     * @param lName The last name of the account holder.
     * @param dob The date of birth of the account holder.
     * @param withdraw The amount to be withdrawn from the account.
     */

    private void withdrawSavings(String fName, String lName, Date dob, double withdraw) {
        Profile profileToWithdraw = new Profile(fName, lName, dob);
        Savings accountToWithdraw = new Savings(profileToWithdraw, withdraw);
        withdrawAcc(fName, lName, dob, accountToWithdraw, withdraw, "S");
    }

    /**
     * Withdraws funds from a Money Market account associated with the given account holder's information.
     *
     * @param fName The first name of the account holder.
     * @param lName The last name of the account holder.
     * @param dob The date of birth of the account holder.
     * @param withdraw The amount to be withdrawn from the account.
     */

    private void withdrawMM(String fName, String lName, Date dob, double withdraw) {
        Profile profileToWithdraw = new Profile(fName, lName, dob);
        MoneyMarket accountToWithdraw = new MoneyMarket(profileToWithdraw, withdraw, true);
        withdrawAcc(fName, lName, dob, accountToWithdraw, withdraw, "MM");
    }

    /**
     * Withdraws funds from the specified account associated with the given account holder's information.
     *
     * @param fName The first name of the account holder.
     * @param lName The last name of the account holder.
     * @param dob The date of birth of the account holder.
     * @param account The account from which the withdrawal is made.
     * @param withdraw The amount to be withdrawn from the account.
     * @param accountType The type of the account.
     */

    private void withdrawAcc(String fName, String lName, Date dob,
                             Account account, double withdraw, String accountType) {
        if (!accountDatabase.withdraw(account)) {
            if (withdraw > account.balance) {
                depositWithdrawOutput.setText(fName + " " + lName + " " +
                        dob.dateStr() + "(" + accountType + ") " +
                        "Withdraw - insufficient fund.");
            }
            else {
                depositWithdrawOutput.setText(fName + " " + lName +
                        " " + dob.dateStr() + "(" + accountType + ") " +
                        "is not in the database.");
            }
            return;
        }
        depositWithdrawOutput.setText(fName + " " + lName + " " +
                dob.dateStr() + "(" + accountType + ") Withdraw " +
                "- balance updated.");
    }

    /**
     * Show an alert dialog with the specified title and header.
     *
     * @param title   The title of the alert.
     * @param header  The header text of the alert.
     * @return True if the alert was shown.
     */

    private boolean displayAlert(String title, String header) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.showAndWait();
        return false;
    }

    /**
     * Check if a TextField is empty.
     *
     * @param textField The TextField to check.
     * @return True if the TextField is empty.
     */
    private boolean checkTF(TextField textField) {
        return textField == null || textField.getText().isEmpty();
    }

    /**
     * Checks if a given DatePicker field is null or has no selected date.
     *
     * @param datePicker The DatePicker field to be checked.
     * @return {@code true} if the DatePicker is null or has no selected date; {@code false} otherwise.
     */
    private boolean checkDF(DatePicker datePicker) {
        return datePicker == null || datePicker.getValue() == null;
    }

    /**
     * Populates the input fields for account information and initializes related variables.
     *
     * @param fNameField The TextField for the first name.
     * @param lNameField The TextField for the last name.
     * @param depositField The TextField for the initial deposit amount.
     * @param dobPicker The DatePicker for the date of birth.
     */
    private void showField(TextField fNameField, TextField lNameField,
                           TextField depositField, DatePicker dobPicker) {
        fields[FNAME_INPUT] = fNameField.getText();
        fields[LNAME_INPUT] = lNameField.getText();
        fields[DEPOSIT_INPUT] = depositField.getText();
        if(!(fields[DEPOSIT_INPUT].isEmpty())) {
            initialDeposit = Double.parseDouble(fields[DEPOSIT_INPUT]);
        }
        amount = initialDeposit;
        accountDob = new Date(dobPicker.getValue().getYear(),
                dobPicker.getValue().getMonthValue(),
                dobPicker.getValue().getDayOfMonth());
    }

    /**
     * Checks the input fields for opening an account and validates the data.
     *
     * @return {@code true} if the input fields are valid; {@code false} otherwise.
     */
    @FXML
    protected boolean checkOpenField(){
        if (checkTF(firstName) || checkTF(lastName) ||
                checkTF(openDeposit) || checkDF(dob)) {
            return displayAlert("Missing Data", "Missing data for opening " +
                    "an account.");
        }
        try {
            showField(firstName, lastName, openDeposit, dob);
        } catch(NullPointerException e) {
            return displayAlert("Missing Data", "Missing data for opening " +
                    "an account.");
        } catch (NumberFormatException e) {
            return displayAlert("Invalid Amount", "Not a valid amount.");
        }
        return true;
    }

    /**
     * Checks the input fields for closing an account and validates the data.
     *
     * @return {@code true} if the input fields are valid; {@code false} otherwise.
     */
    @FXML
    protected boolean checkFieldsClose(){
        if (checkTF(firstName) ||
                checkTF(lastName) || checkDF(dob)) {
            return displayAlert("Missing Data", "Missing data for closing an " +
                    "account.");
        }
        if (!openDeposit.getText().isEmpty()) {
            return displayAlert("Invalid Field", "Initial Deposit " +
                    "field must be empty when closing an account.");
        }
        try {
            showField(firstName, lastName, openDeposit, dob);
        } catch(NullPointerException e) {
            return displayAlert("Missing Data", "Missing data for closing an " +
                    "account.");
        }
        return true;
    }

    /**
     * Checks the input fields for depositing or withdrawing funds from an account and validates the data.
     *
     * @return {@code true} if the input fields are valid; {@code false} otherwise.
     */
    @FXML
    protected boolean checkFieldsDepositWithdraw(){
        if (checkTF(firstNameDW) || checkTF(lastNameDW) ||
                checkTF(depositOrWithdraw) || checkDF(dobDW)) {
            return displayAlert("Missing Data", "Please fill all values.");
        }
        try {
            showField(firstNameDW, lastNameDW, depositOrWithdraw, dobDW);
        } catch(NullPointerException e) {
            return displayAlert("Missing Data", "Please fill all values.");
        } catch (NumberFormatException e) {
            return displayAlert("Invalid Amount", "Not a valid amount.");
        }
        return true;
    }

    /**
     * Handle the selection of loyal or campus radio buttons.
     */

    @FXML
    protected void loyalCampusSwitch(ActionEvent event) {
        RadioButton source = (RadioButton) event.getSource();
        System.out.println("Selected RadioButton: " + source.getText());
        if(collegeChecking.isSelected()){
            campusGroupContainer.setDisable(false);
        }else {
            campusGroupContainer.setDisable(true);
        }
        if(savings.isSelected()){
            loyalContainer.setDisable(false);
        } else {
            loyalContainer.setDisable(true);
        }
        if(moneyMarket.isSelected()){
            loyal.setSelected(true);
        }
        else {
            loyal.setSelected(false);
        }
    }

    /**
     * Checks if the initial deposit amount is valid (greater than zero).
     *
     * @return {@code true} if the initial deposit is valid; {@code false} otherwise.
     */
    private boolean validInitialDeposit(){
        if (initialDeposit <= ZERO_QUANTITY) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid Amount");
            alert.setHeaderText("Initial Deposit cannot be 0 or negative.");
            alert.showAndWait();
            return false;
        }
        return true;
    }

    /**
     * Checks if the withdrawal amount is valid (greater than zero).
     *
     * @return {@code true} if the withdrawal amount is valid; {@code false} otherwise.
     */
    private boolean validWithdraw() {
        if (amount <= ZERO_QUANTITY) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid Amount");
            alert.setHeaderText("Withdraw - amount cannot be 0 or negative.");
            alert.showAndWait();
            return false;
        }
        return true;
    }

    /**
     * Checks if the deposit amount is valid (greater than zero).
     *
     * @return {@code true} if the deposit amount is valid; {@code false} otherwise.
     */
    private boolean validDeposit() {
        if (amount <= ZERO_QUANTITY) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid Amount");
            alert.setHeaderText("Deposit - amount cannot be 0 or negative.");
            alert.showAndWait();
            return false;
        }
        return true;
    }

    /**
     * Handles the action to clear the database. Shows a confirmation dialog to confirm the operation.
     *
     * @param event The action event triggering the database clearing.
     */
    @FXML
    private void clearDatabaseHandle(ActionEvent event) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText(null);
        alert.setContentText("Warning: All data will be cleared. Are you sure?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Your code to clear the database goes here
            System.out.println("Database cleared!"); // Placeholder for actual database clear operation
        }
    }

    /**
     * Checks the age of a person based on the provided date of birth and the account type.
     *
     * @param date        The date of birth to be checked.
     * @param accountType The type of account to be opened ("CC" for College Checking, etc.).
     * @return {@code true} if the date of birth is valid for opening the account; {@code false} otherwise.
     */
    private boolean checkAge(Date date, String accountType){
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH);
        currentMonth++;
        int currentDay = calendar.get(Calendar.DATE);
        Date currentDate = new Date(currentYear, currentMonth, currentDay);
        int age = calcAge(currentDate, date);
        if (age < MIN_AGE_TO_TO_OPEN) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Date of Birth invalid");
            alert.setHeaderText("DOB invalid: " + date.dateStr()
                    + " under 16.");
            alert.showAndWait();
            return false;
        }
        if ("CC".equals(accountType) && age >= MAX_AGE_TO_OPEN_CC) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Date of Birth invalid");
            alert.setHeaderText("DOB invalid for College Checking: " +
                    date.dateStr() + " over 24.");
            alert.showAndWait();
            return false;
        }
        return true;
    }

    /**
     * Calculate the age based on the current date and the provided date of birth.
     *
     * @param currentDate The current date.
     * @param ageDate     The date of birth.
     * @return The calculated age.
     */

    private int calcAge(Date currentDate, Date ageDate) {
        int age = currentDate.getYear() - ageDate.getYear();

        if (currentDate.getMonth() < ageDate.getMonth() ||
                (currentDate.getMonth() == ageDate.getMonth()
                        && currentDate.getDay() < ageDate.getDay())) {
            age--;
        }
        return age;
    }

    /**
     * Convert a campus string to its corresponding campus code.
     *
     * @param campusStr The campus string.
     * @return The campus code.
     */
    private String convertToCode(String campusStr) {
        switch (campusStr) {
            case "New Brunswick" -> {
                return "0";
            }
            case "Newark" -> {
                return "1";
            }
            case "Camden" -> {
                return "2";
            }
            default -> {
                return null;
            }
        }
    }

    /**
     * Parse a date string in the format "MM/DD/YYYY" into a Date object.
     *
     * @param dateStr The date string to parse.
     * @return The parsed Date object.
     */

    private Date dateParse(String dateStr) {
        String[] dateComponents = dateStr.split("/");
        if (dateComponents.length == DATE_COMPONENTS_LENGTH) {
            int year = Integer.parseInt(dateComponents[YEAR_PART]);
            int month = Integer.parseInt(dateComponents[MONTH_PART]);
            int day = Integer.parseInt(dateComponents[DAY_PART]);
            return new Date(year, month, day);
        }
        return null;
    }

}