package com.booking.event_booking;

import org.springframework.boot.SpringApplication;

public class TestEventBookingApplication {

	public static void main(String[] args) {
		SpringApplication.from(EventBookingApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
