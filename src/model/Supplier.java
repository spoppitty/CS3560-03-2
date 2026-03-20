package model;

/**
 * Represents a supplier.
 */
public class Supplier {
    private String supplierID;
    private String address;
    private String email;
    private String name;
    private String contactName;

    public Supplier(String supplierID, String address, String email, String name, String contactName) {
        this.supplierID = supplierID;
        this.address = address;
        this.email = email;
        this.name = name;
        this.contactName = contactName;
    }

    public String getSupplierID() {
        return supplierID;
    }

    public String getAddress() {
        return address;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getContactName() {
        return contactName;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }
}