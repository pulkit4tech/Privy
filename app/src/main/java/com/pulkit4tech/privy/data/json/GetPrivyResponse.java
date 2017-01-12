package com.pulkit4tech.privy.data.json;

import java.util.List;

public class GetPrivyResponse {
    private List<String> html_attributions;
    private List<MarkerData> results;
    private String status;

    public List<String> getHtmlattributions() {
        return html_attributions;
    }

    public void setHtmlattributions(List<String> html_attributions) {
        this.html_attributions = html_attributions;
    }

    public List<MarkerData> getResults() {
        return results;
    }

    @Override
    public String toString() {
        return "GetPrivyResponse{" +
                "html_attributions=" + html_attributions +
                ", results=" + results +
                ", status='" + status + '\'' +
                '}';
    }

    public void setResults(List<MarkerData> results) {
        this.results = results;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
