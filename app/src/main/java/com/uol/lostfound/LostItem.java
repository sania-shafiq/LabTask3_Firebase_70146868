package com.uol.lostfound;

/**
 * Model class - holds one Lost & Found post.
 * Firebase uses the getters to write and the setters to read,
 * and needs the public no-argument constructor.
 */
public class LostItem {

    private String id;
    private String itemName;
    private String type;      // "Lost" or "Found"
    private String location;
    private String contact;
    private String status;    // "Open" or "Returned"

    // Required by Firebase
    public LostItem() {
    }

    public LostItem(String id, String itemName, String type,
                    String location, String contact, String status) {
        this.id = id;
        this.itemName = itemName;
        this.type = type;
        this.location = location;
        this.contact = contact;
        this.status = status;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
