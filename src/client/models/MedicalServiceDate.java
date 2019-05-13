package client.models;

import java.io.Serializable;
import java.util.Date;

public class MedicalServiceDate implements Serializable {
    private int id;
    private String date;
    private boolean isReserved;

    public MedicalServiceDate() {}

    public int getId() {
        return id;
    }

    public MedicalServiceDate(int id, String date, boolean isReserved) {
        this.id = id;
        this.date = date;
        this.isReserved = isReserved;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isReserved() {
        return isReserved;
    }

    public void setReserved(boolean reserved) {
        isReserved = reserved;
    }

    @Override
    public String toString() {
        return this.date;
    }
}
