package model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement (name="order")
public class Order {
    private int id;
    private String date;
    private String time;

    public Order() {

    }

    public int getId() {
        return id;
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
