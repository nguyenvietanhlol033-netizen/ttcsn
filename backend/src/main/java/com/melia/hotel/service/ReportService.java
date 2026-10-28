package com.melia.hotel.service;

import com.melia.hotel.dto.response.OccupancyReportResponse;
import com.melia.hotel.dto.response.RevenueReportResponse;

import java.time.LocalDate;

public interface ReportService {
    OccupancyReportResponse calculateOccupancyRate(LocalDate fromDate, LocalDate toDate);
    RevenueReportResponse calculateRevenueReport(LocalDate fromDate, LocalDate toDate);
}