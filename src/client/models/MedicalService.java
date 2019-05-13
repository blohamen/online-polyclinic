package client.models;

import java.io.Serializable;

public class MedicalService implements Serializable {
    private int id;
    private String title;
    private float price;

    public MedicalService() {

    }

    public MedicalService(int id, String title, float price) {
        this.id = id;
        this.title = title;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return this.title;
    }
}
