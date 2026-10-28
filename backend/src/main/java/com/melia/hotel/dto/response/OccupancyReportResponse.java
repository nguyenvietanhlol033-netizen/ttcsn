package com.melia.hotel.dto.response;

import java.time.LocalDate;

public class OccupancyReportResponse {
    private LocalDate fromDate;
    private LocalDate toDate;
    private Long totalRooms;
    private Long occupiedRooms;
    private Double occupancyRate;

    public OccupancyReportResponse() {}

    public OccupancyReportResponse(LocalDate fromDate, LocalDate toDate, Long totalRooms, Long occupiedRooms, Double occupancyRate) {
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.totalRooms = totalRooms;
        this.occupiedRooms = occupiedRooms;
        this.occupancyRate = occupancyRate;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private LocalDate fromDate;
        private LocalDate toDate;
        private Long totalRooms;
        private Long occupiedRooms;
        private Double occupancyRate;

        public Builder fromDate(LocalDate fromDate) { this.fromDate = fromDate; return this; }
        public Builder toDate(LocalDate toDate) { this.toDate = toDate; return this; }
        public Builder totalRooms(Long totalRooms) { this.totalRooms = totalRooms; return this; }
        public Builder occupiedRooms(Long occupiedRooms) { this.occupiedRooms = occupiedRooms; return this; }
        public Builder occupancyRate(Double occupancyRate) { this.occupancyRate = occupancyRate; return this; }
        public OccupancyReportResponse build() {
            return new OccupancyReportResponse(fromDate, toDate, totalRooms, occupiedRooms, occupancyRate);
        }
    }

    public LocalDate getFromDate() { return fromDate; }
    public void setFromDate(LocalDate fromDate) { this.fromDate = fromDate; }
    public LocalDate getToDate() { return toDate; }
    public void setToDate(LocalDate toDate) { this.toDate = toDate; }
    public Long getTotalRooms() { return totalRooms; }
    public void setTotalRooms(Long totalRooms) { this.totalRooms = totalRooms; }
    public Long getOccupiedRooms() { return occupiedRooms; }
    public void setOccupiedRooms(Long occupiedRooms) { this.occupiedRooms = occupiedRooms; }
    public Double getOccupancyRate() { return occupancyRate; }
    public void setOccupancyRate(Double occupancyRate) { this.occupancyRate = occupancyRate; }
}
