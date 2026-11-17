package model;

// Customer - Bundles information about a single bank customer together.
public class Customer {

    // Encapsulation - Using private variables to hide the customer's personal profile data from outside files.
    private String customerId;
    private String firstname;
    private String surname;
    private String address;
    private String username;
    private String password;

    // Encapsulation - Sets up a new customer object with their ID, name, address, and login details.
    public Customer(String customerId, String firstname, String surname, String address, String username, String password)
    {
        this.customerId = customerId;
        this.firstname = firstname;
        this.surname = surname;
        this.address = address;
        this.username = username;
        this.password = password;
    }

    // Encapsulation - Using public getter methods to safely read the private customer variables.
    public String getCustomerId() { return customerId; }
    public String getFirstname()  { return firstname; }
    public String getSurname()    { return surname; }
    public String getAddress()    { return address; }
    public String getUsername()    { return username; }
    public String getPassword()    { return password; }

    // Encapsulation - Using a public setter method to safely change the private address variable.
    public void setAddress(String address) { this.address = address; }

    // Encapsulation - A helper method that combines the first name and surname together.
    public String getFullName() { return firstname + " " + surname; }

    // Polymorphism - Replaces Java's standard string method to print the customer's full name and ID number neatly.
    @Override
    public String toString() { return getFullName() + " (" + customerId + ")"; }
}
