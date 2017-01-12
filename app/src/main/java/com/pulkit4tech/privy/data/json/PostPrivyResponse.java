package com.pulkit4tech.privy.data.json;

public class PostPrivyResponse {
    private String id;
    private String place_id;
    private String reference;
    private String scope;
    private String status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPlaceid() {
        return place_id;
    }

    public void setPlaceid(String place_id) {
        this.place_id = place_id;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "PostPrivyResponse{" +
                "id='" + id + '\'' +
                ", place_id='" + place_id + '\'' +
                ", reference='" + reference + '\'' +
                ", scope='" + scope + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
