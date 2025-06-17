package Tugas2.Model;

public class Reviews {
    private int booking;
    private int star;
    private String title;
    private String content;

    // Full constructor
    public Reviews(int booking, int star, String title, String content) {
        this.booking = booking;
        this.star = star;
        this.title = title;
        this.content = content;
    }

    // Constructor without booking (optional if booking is auto-generated elsewhere)
    public Reviews(int star, String title, String content) {
        this.star = star;
        this.title = title;
        this.content = content;
    }

    // No-args constructor (needed for Jackson or JDBC)
    public Reviews() {
    }

    // Getters
    public int getBooking() {
        return booking;
    }

    public int getStar() {
        return star;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    // Setters
    public void setBooking(int booking) {
        this.booking = booking;
    }

    public void setStar(int star) {
        this.star = star;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
