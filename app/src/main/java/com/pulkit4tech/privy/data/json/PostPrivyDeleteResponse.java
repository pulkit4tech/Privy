package com.pulkit4tech.privy.data.json;

public class PostPrivyDeleteResponse {
    private String status;
    private String error_message;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getErrormessage() {
        return error_message;
    }

    public void setErrormessage(String error_message) {
        this.error_message = error_message;
    }

    @Override
    public String toString() {
        return "PostPrivyDeleteResponse{" +
                "status='" + status + '\'' +
                ", error_message='" + error_message + '\'' +
                '}';
    }
}
