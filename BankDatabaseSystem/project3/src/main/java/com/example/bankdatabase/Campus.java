package com.example.bankdatabase;

/**
 * The campus is an enum class that represents the different campuses and their codes
 */

public enum Campus {
    NEW_BRUNSWICK("0"),
    NEWARK("1"),
    CAMDEN("2");

    private final String code; //campus code

    /**
     * Creates a new Campus with the specified code.
     *
     * @param code the unique code associated with the campus
     */
    private Campus(String code) {
        this.code = code;
    }

    /**
     * Returns the code associated with the campus.
     *
     * @return the code of the campus
     */
    public String getCode() {
        return code;
    }

    /**
     * Retrieves the Campus enum value based on the given code.
     *
     * @param code the code to look up
     * @return the corresponding Campus enum value, or null if the code is not found
     */
    public static Campus fromCode(String code) {
        for (Campus campus : Campus.values()) {
            if (campus.getCode().equals(code)) {
                return campus;
            }
        }
        return null;

    }

}