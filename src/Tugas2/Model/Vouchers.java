package Tugas2.Model;

public class Vouchers {
    private int id;
    private String code;
    private String description;
    private double discount;
    private String startDate; // Format: "YYYY-MM-DD hh:mm:ss"
    private String endDate;   // Format: "YYYY-MM-DD hh:mm:ss"

    // Full constructor
    public Vouchers(int id, String code, String description, double discount, String startDate, String endDate) {
        this.id = id;
        this.code = code;
        this.description = description;
        this.discount = discount;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // Constructor without id (for insertions)
    public Vouchers(String code, String description, double discount, String startDate, String endDate) {
        this.code = code;
        this.description = description;
        this.discount = discount;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // Default no-args constructor
    public Vouchers() {
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public double getDiscount() {
        return discount;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}
