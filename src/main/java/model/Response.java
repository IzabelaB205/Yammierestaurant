package model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Response {
    private int code;
    private boolean status;
    private String message;
    private Order data;

    public Response() {
        data = null;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Order getData() { return data; }

    public void setData(Order data) {
        this.data = data;
    }
}
