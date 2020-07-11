package pl.piomin.services.trip.model;

public class Passenger {

    private Long id;
    private String name;
    private String phoneNo;
    private String login;
    private int balance;
    private int homeLocationX;
    private int homeLocationY;
    private int discount;

    public Passenger() {
    }

    public Passenger(Long id, String name, String phoneNo) {
        this.id = id;
        this.name = name;
        this.phoneNo = phoneNo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public int getHomeLocationX() {
        return homeLocationX;
    }

    public void setHomeLocationX(int homeLocationX) {
        this.homeLocationX = homeLocationX;
    }

    public int getHomeLocationY() {
        return homeLocationY;
    }

    public void setHomeLocationY(int homeLocationY) {
        this.homeLocationY = homeLocationY;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }
}
