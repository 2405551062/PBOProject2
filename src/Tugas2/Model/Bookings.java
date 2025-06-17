package Tugas2.Model;

public class Bookings {
    private int id;
    private int customer;
    private int roomType;
    private String checkinDate;
    private String checkoutDate;
    private int price;
    private Integer voucher; // nullable
    private int finalPrice;
    private String paymentStatus;
    private int hasCheckedIn;
    private int hasCheckedOut;

    public Bookings(int id, int customer, int roomType, String checkinDate, String checkoutDate,
                   int price, Integer voucher, int finalPrice,
                   String paymentStatus, int hasCheckedIn, int hasCheckedOut) {
        this.id = id;
        this.customer = customer;
        this.roomType = roomType;
        this.checkinDate = checkinDate;
        this.checkoutDate = checkoutDate;
        this.price = price;
        this.voucher = voucher;
        this.finalPrice = finalPrice;
        this.paymentStatus = paymentStatus;
        this.hasCheckedIn = hasCheckedIn;
        this.hasCheckedOut = hasCheckedOut;
    }

    // No-args constructor
    public Bookings() {}

    // Getters
    public int getId() {
        return id;
    }

    public int getCustomer() {
        return customer;
    }

    public int getRoomType() {
        return roomType;
    }

    public String getCheckinDate() {
        return checkinDate;
    }

    public String getCheckoutDate() {
        return checkoutDate;
    }

    public int getPrice() {
        return price;
    }

    public Integer getVoucher() {
        return voucher;
    }

    public int getFinalPrice() {
        return finalPrice;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public int getHasCheckedIn() {
        return hasCheckedIn;
    }

    public int getHasCheckedOut() {
        return hasCheckedOut;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setCustomer(int customer) {
        this.customer = customer;
    }

    public void setRoomType(int roomType) {
        this.roomType = roomType;
    }

    public void setCheckinDate(String checkinDate) {
        this.checkinDate = checkinDate;
    }

    public void setCheckoutDate(String checkoutDate) {
        this.checkoutDate = checkoutDate;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setVoucher(Integer voucher) {
        this.voucher = voucher;
    }

    public void setFinalPrice(int finalPrice) {
        this.finalPrice = finalPrice;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public void setHasCheckedIn(int hasCheckedIn) {
        this.hasCheckedIn = hasCheckedIn;
    }

    public void setHasCheckedOut(int hasCheckedOut) {
        this.hasCheckedOut = hasCheckedOut;
    }
}
