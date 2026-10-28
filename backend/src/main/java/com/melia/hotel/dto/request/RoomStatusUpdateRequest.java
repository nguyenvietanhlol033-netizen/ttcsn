package com.melia.hotel.dto.request;

import jakarta.validation.constraints.NotBlank;

public class RoomStatusUpdateRequest {

    @NotBlank(message = "Trạng thái phòng không được để trống")
    private String status;

    public RoomStatusUpdateRequest() {}
    public RoomStatusUpdateRequest(String status) { this.status = status; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
