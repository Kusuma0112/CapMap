package com.example.capmap.fragements.contribute;

public class CompanyModel {
    private String companyName;
    private String status;
    private String offer;
    private String description;
    private String logoUrl;
    private String key; // Add a key field to store the company key
    private String contributorKey; // Add contributor's key

    public CompanyModel() {}

    public CompanyModel(String companyName, String status, String offer, String description, String logoUrl, String key, String contributorKey) {
        this.companyName = companyName;
        this.status = status;
        this.offer = offer;
        this.description = description;
        this.logoUrl = logoUrl;
        this.key = key;
        this.contributorKey = contributorKey;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOffer() {
        return offer;
    }

    public void setOffer(String offer) {
        this.offer = offer;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getContributorKey() {
        return contributorKey;
    }

    public void setContributorKey(String contributorKey) {
        this.contributorKey = contributorKey;
    }
}