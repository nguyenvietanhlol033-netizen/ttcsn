package com.melia.hotel.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public class RevenueReportResponse {
    private LocalDate fromDate;
    private LocalDate toDate;
    private Integer totalInvoices;
    private BigDecimal roomRevenue;
    private BigDecimal serviceRevenue;
    private BigDecimal totalRevenue;
    private BigDecimal revPar;

    public RevenueReportResponse() {}

    public RevenueReportResponse(LocalDate fromDate, LocalDate toDate, Integer totalInvoices, BigDecimal roomRevenue, BigDecimal serviceRevenue, BigDecimal totalRevenue, BigDecimal revPar) {
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.totalInvoices = totalInvoices;
        this.roomRevenue = roomRevenue;
        this.serviceRevenue = serviceRevenue;
        this.totalRevenue = totalRevenue;
        this.revPar = revPar;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private LocalDate fromDate;
        private LocalDate toDate;
        private Integer totalInvoices;
        private BigDecimal roomRevenue;
        private BigDecimal serviceRevenue;
        private BigDecimal totalRevenue;
        private BigDecimal revPar;

        public Builder fromDate(LocalDate fromDate) { this.fromDate = fromDate; return this; }
        public Builder toDate(LocalDate toDate) { this.toDate = toDate; return this; }
        public Builder totalInvoices(Integer totalInvoices) { this.totalInvoices = totalInvoices; return this; }
        public Builder roomRevenue(BigDecimal roomRevenue) { this.roomRevenue = roomRevenue; return this; }
        public Builder serviceRevenue(BigDecimal serviceRevenue) { this.serviceRevenue = serviceRevenue; return this; }
        public Builder totalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; return this; }
        public Builder revPar(BigDecimal revPar) { this.revPar = revPar; return this; }
        public RevenueReportResponse build() {
            return new RevenueReportResponse(fromDate, toDate, totalInvoices, roomRevenue, serviceRevenue, totalRevenue, revPar);
        }
    }

    public LocalDate getFromDate() { return fromDate; }
    public void setFromDate(LocalDate fromDate) { this.fromDate = fromDate; }
    public LocalDate getToDate() { return toDate; }
    public void setToDate(LocalDate toDate) { this.toDate = toDate; }
    public Integer getTotalInvoices() { return totalInvoices; }
    public void setTotalInvoices(Integer totalInvoices) { this.totalInvoices = totalInvoices; }
    public BigDecimal getRoomRevenue() { return roomRevenue; }
    public void setRoomRevenue(BigDecimal roomRevenue) { this.roomRevenue = roomRevenue; }
    public BigDecimal getServiceRevenue() { return serviceRevenue; }
    public void setServiceRevenue(BigDecimal serviceRevenue) { this.serviceRevenue = serviceRevenue; }
    public BigDecimal getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; }
    public BigDecimal getRevPar() { return revPar; }
    public void setRevPar(BigDecimal revPar) { this.revPar = revPar; }
}
