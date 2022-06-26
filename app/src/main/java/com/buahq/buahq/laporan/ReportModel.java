package com.buahq.buahq.laporan;

public class ReportModel {
    private String reportId;
    private String date;
    private int price;
    private int priceDiff;
    private int total;
    private String terlaris;

    public ReportModel(){}

    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getPriceDiff() {
        return priceDiff;
    }

    public void setPriceDiff(int priceDiff) {
        this.priceDiff = priceDiff;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getTerlaris() {
        return terlaris;
    }

    public void setTerlaris(String terlaris) {
        this.terlaris = terlaris;
    }
}
