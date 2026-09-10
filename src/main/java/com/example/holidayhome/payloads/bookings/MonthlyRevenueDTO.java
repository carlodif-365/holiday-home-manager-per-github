package com.example.holidayhome.payloads.bookings;

import java.math.BigDecimal;

public record MonthlyRevenueDTO(String month, BigDecimal revenue, Long bookingsCount) {
}
