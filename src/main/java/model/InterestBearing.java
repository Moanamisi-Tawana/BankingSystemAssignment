package model;

// ABSTRACTION - An interface that forces savings or investment accounts to follow interest rules.
public interface InterestBearing {

    void payInterest();

    double getInterestRate();
}
