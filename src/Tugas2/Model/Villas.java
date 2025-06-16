package Tugas2.Model;

public class Villas {
    private int id;
    private String name;
    private String description;
    private String address;

    // Full constructor
    public Villas(int id, String name, String description, String address) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.address = address;
    }

    // Constructor without id (for insertions)
    public Villas(String name, String description, String address) {
        this.name = name;
        this.description = description;
        this.address = address;
    }

    // Default no-args constructor (needed for Jackson, DAO mapping, etc)
    public Villas() {
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getAddress() {
        return address;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
