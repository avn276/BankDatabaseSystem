package com.example.bankdatabase;

/**
 * Represents a Profile with first name, last name, and date of birth.
 */
public class Profile implements Comparable<Profile>{
    private String fName;
    private String lname;
    private Date dob;
    private static final int EQUAL_CONDITION = 0;

    /**
     * Constructs a new Profile.
     * @param firstName First name of the profile.
     * @param lastName Last name of the profile.
     * @param dob Date of birth of the profile.
     */
    public Profile(String firstName, String lastName, Date dob) {
        this.fName = firstName.toLowerCase();
        this.lname = lastName.toLowerCase();
        this.dob = dob;
    }

    /**
     * Gets the capitalized first name.
     * @return Capitalized first name.
     */
    public String getFirstName(){
        return capitalize(fName);
    }

    /**
     * Gets the capitalized last name.
     * @return Capitalized last name.
     */
    public String getLastName() {
        return capitalize(lname);
    }

    /**
     * Gets the date of birth.
     * @return Date of birth.
     */
    public Date getDateOfBirth() {
        return dob;
    }

    /**
     * Gets the full name in lowercase.
     * @return Full name.
     */
    public String getFullName(){
        return fName + lname;
    }

    /**
     * Capitalizes the first letter of a string.
     * @param str The string to capitalize.
     * @return Capitalized string.
     */
    private String capitalize(String str) {
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }

    /**
     * Checks if this profile is equal to another object.
     * @param obj The object to compare.
     * @return true if equal, false otherwise.
     */
    @Override
    public boolean equals(Object obj){
        if(obj instanceof Profile){
            Profile o = (Profile) obj;
            return fName.equals(o.fName) && lname.equals(o.lname)
                    && dob.compareTo(o.dob) == EQUAL_CONDITION;
        }
        return false;

    }

    /**
     * Returns a string representation of the profile.
     * @return String representation.
     */
    @Override
    public String toString(){
        return fName +" " + lname + " " + dob.dateStr();
    }

    /**
     * Compares this profile to another profile.
     * @param o The profile to compare.
     * @return -1, 0, or 1 as this profile is less than, equal to, or greater than the specified profile.
     */
    @Override
    public int compareTo(Profile o) {
        if(this.lname.compareTo(o.lname) < 0){
            return -1;
        }
        if(this.lname.compareTo(o.lname) > 0){
            return 1;
        }

        if(this.fName.compareTo(o.fName) < 0){
            return -1;
        }
        if(this.fName.compareTo(o.fName) > 0){
            return 1;
        }
        if(this.dob.compareTo(o.dob) < 0){
            return -1;
        }
        if (this.dob.compareTo(o.dob) > 0) {
            return 1;
        }

        return 0;
    }
}