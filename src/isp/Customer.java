package isp;

import java.io.Serializable;

public class Customer implements Serializable {
    private static final long serialVersionUID = 1L;
    int id;
    String name, phone, email, address;

    public Customer(int id, String name, String phone, String email, String address) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
    }

    @Override
    public String toString() {
        return "ID: " + id + " | Name: " + name + " | Phone: " + phone
                + " | Email: " + email + " | Address: " + address;
    }
}
