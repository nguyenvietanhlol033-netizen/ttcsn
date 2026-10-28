package com.melia.hotel.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class InvoiceResponse {
    private String invoiceId;
    private String bookingId;
    private LocalDateTime issueDate;
    private BigDecimal roomCharge;
    private BigDecimal serviceCharge;
    private BigDecimal surcharge;
    private BigDecimal discount;
    private BigDecimal finalAmount;
    private BigDecimal daThanhToan;
    private BigDecimal conPhaiTra;
    private String paymentMethod;
    private String paymentStatus;

    public InvoiceResponse() {}

    public InvoiceResponse(String invoiceId, String bookingId, LocalDateTime issueDate,
                           BigDecimal roomCharge, BigDecimal serviceCharge, BigDecimal surcharge,
                           BigDecimal discount, BigDecimal finalAmount, BigDecimal daThanhToan,
                           BigDecimal conPhaiTra, String paymentMethod, String paymentStatus) {
        this.invoiceId = invoiceId;
        this.bookingId = bookingId;
        this.issueDate = issueDate;
        this.roomCharge = roomCharge;
        this.serviceCharge = serviceCharge;
        this.surcharge = surcharge;
        this.discount = discount;
        this.finalAmount = finalAmount;
        this.daThanhToan = daThanhToan;
        this.conPhaiTra = conPhaiTra;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String invoiceId;
        private String bookingId;
        private LocalDateTime issueDate;
        private BigDecimal roomCharge;
        private BigDecimal serviceCharge;
        private BigDecimal surcharge;
        private BigDecimal discount;
        private BigDecimal finalAmount;
        private BigDecimal daThanhToan;
        private BigDecimal conPhaiTra;
        private String paymentMethod;
        private String paymentStatus;

        public Builder invoiceId(String invoiceId) { this.invoiceId = invoiceId; return this; }
        public Builder bookingId(String bookingId) { this.bookingId = bookingId; return this; }
        public Builder issueDate(LocalDateTime issueDate) { this.issueDate = issueDate; return this; }
        public Builder roomCharge(BigDecimal roomCharge) { this.roomCharge = roomCharge; return this; }
        public Builder serviceCharge(BigDecimal serviceCharge) { this.serviceCharge = serviceCharge; return this; }
        public Builder surcharge(BigDecimal surcharge) { this.surcharge = surcharge; return this; }
        public Builder discount(BigDecimal discount) { this.discount = discount; return this; }
        public Builder finalAmount(BigDecimal finalAmount) { this.finalAmount = finalAmount; return this; }
        public Builder daThanhToan(BigDecimal daThanhToan) { this.daThanhToan = daThanhToan; return this; }
        public Builder conPhaiTra(BigDecimal conPhaiTra) { this.conPhaiTra = conPhaiTra; return this; }
        public Builder paymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; return this; }
        public Builder paymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; return this; }
        public InvoiceResponse build() {
            return new InvoiceResponse(invoiceId, bookingId, issueDate, roomCharge, serviceCharge, surcharge, discount, finalAmount, daThanhToan, conPhaiTra, paymentMethod, paymentStatus);
        }
    }

    public String getInvoiceId() { return invoiceId; }
    public void setInvoiceId(String invoiceId) { this.invoiceId = invoiceId; }
    public String getBookingId() { return bookingId; }
    public void setBookingId(String bookingId) { this.bookingId = bookingId; }
    public LocalDateTime getIssueDate() { return issueDate; }
    public void setIssueDate(LocalDateTime issueDate) { this.issueDate = issueDate; }
    public BigDecimal getRoomCharge() { return roomCharge; }
    public void setRoomCharge(BigDecimal roomCharge) { this.roomCharge = roomCharge; }
    public BigDecimal getServiceCharge() { return serviceCharge; }
    public void setServiceCharge(BigDecimal serviceCharge) { this.serviceCharge = serviceCharge; }
    public BigDecimal getSurcharge() { return surcharge; }
    public void setSurcharge(BigDecimal surcharge) { this.surcharge = surcharge; }
    public BigDecimal getDiscount() { return discount; }
    public void setDiscount(BigDecimal discount) { this.discount = discount; }
    public BigDecimal getFinalAmount() { return finalAmount; }
    public void setFinalAmount(BigDecimal finalAmount) { this.finalAmount = finalAmount; }
    public BigDecimal getDaThanhToan() { return daThanhToan; }
    public void setDaThanhToan(BigDecimal daThanhToan) { this.daThanhToan = daThanhToan; }
    public BigDecimal getConPhaiTra() { return conPhaiTra; }
    public void setConPhaiTra(BigDecimal conPhaiTra) { this.conPhaiTra = conPhaiTra; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
}
