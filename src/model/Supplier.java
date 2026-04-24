package model;

/**
 * Represents a supplier.
 */
public class Supplier {
    /**
     * Unique ID for the supplier.
     */
    private String supplierID;

    /**
     * Supplier mailing address.
     */
    private String address;

    /**
     * Supplier email address.
     */
    private String email;

    /**
     * Supplier company name.
     */
    private String name;

    /**
     * Name of the supplier contact person.
     */
    private String contactName;

    /**
     * Creates a supplier record.
     */
    public Supplier(String supplierID, String address, String email, String name, String contactName) {
        this.supplierID = supplierID;
        this.address = address;
        this.email = email;
        this.name = name;
        this.contactName = contactName;
    }

    /**
     * Returns the supplier ID.
     */
    public String getSupplierID() {
        return supplierID;
    }

    /**
     * Returns the supplier address.
     */
    public String getAddress() {
        return address;
    }

    /**
     * Returns the supplier email.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Returns the supplier name.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the supplier contact name.
     */
    public String getContactName() {
        return contactName;
    }

    /**
     * Updates the supplier address.
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Updates the supplier email.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Updates the supplier company name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Updates the supplier contact name.
     */
    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    @Override
    public String toString() {
        return name + " (" + supplierID + ")";
    }
}
