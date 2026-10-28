package com.melia.hotel.dto.response;

import java.math.BigDecimal;
import java.util.List;

public class RoomAvailabilityResponse {
    private String roomTypeId;
    private String roomTypeName;
    private BigDecimal basePrice;
    private Integer capacity;
    private String description;
    private String imageUrl;
    private Integer availableRoomsCount;
    private List<String> availableRoomCodes;

    public RoomAvailabilityResponse() {}

    public RoomAvailabilityResponse(String roomTypeId, String roomTypeName, BigDecimal basePrice, Integer capacity, String description, String imageUrl, Integer availableRoomsCount, List<String> availableRoomCodes) {
        this.roomTypeId = roomTypeId;
        this.roomTypeName = roomTypeName;
        this.basePrice = basePrice;
        this.capacity = capacity;
        this.description = description;
        this.imageUrl = imageUrl;
        this.availableRoomsCount = availableRoomsCount;
        this.availableRoomCodes = availableRoomCodes;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String roomTypeId;
        private String roomTypeName;
        private BigDecimal basePrice;
        private Integer capacity;
        private String description;
        private String imageUrl;
        private Integer availableRoomsCount;
        private List<String> availableRoomCodes;

        public Builder roomTypeId(String roomTypeId) { this.roomTypeId = roomTypeId; return this; }
        public Builder roomTypeName(String roomTypeName) { this.roomTypeName = roomTypeName; return this; }
        public Builder basePrice(BigDecimal basePrice) { this.basePrice = basePrice; return this; }
        public Builder capacity(Integer capacity) { this.capacity = capacity; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder imageUrl(String imageUrl) { this.imageUrl = imageUrl; return this; }
        public Builder availableRoomsCount(Integer availableRoomsCount) { this.availableRoomsCount = availableRoomsCount; return this; }
        public Builder availableRoomCodes(List<String> availableRoomCodes) { this.availableRoomCodes = availableRoomCodes; return this; }
        public RoomAvailabilityResponse build() {
            return new RoomAvailabilityResponse(roomTypeId, roomTypeName, basePrice, capacity, description, imageUrl, availableRoomsCount, availableRoomCodes);
        }
    }

    public String getRoomTypeId() { return roomTypeId; }
    public void setRoomTypeId(String roomTypeId) { this.roomTypeId = roomTypeId; }
    public String getRoomTypeName() { return roomTypeName; }
    public void setRoomTypeName(String roomTypeName) { this.roomTypeName = roomTypeName; }
    public BigDecimal getBasePrice() { return basePrice; }
    public void setBasePrice(BigDecimal basePrice) { this.basePrice = basePrice; }
    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public Integer getAvailableRoomsCount() { return availableRoomsCount; }
    public void setAvailableRoomsCount(Integer availableRoomsCount) { this.availableRoomsCount = availableRoomsCount; }
    public List<String> getAvailableRoomCodes() { return availableRoomCodes; }
    public void setAvailableRoomCodes(List<String> availableRoomCodes) { this.availableRoomCodes = availableRoomCodes; }
}
