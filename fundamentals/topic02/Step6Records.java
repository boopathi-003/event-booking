// Topic 2, step 6: records — data classes in one line
// Run:  java fundamentals/topic02/Step6Records.java

import java.util.HashSet;
import java.util.Set;

public class Step6Records {

    public static void main(String[] args) {

        // 1. Creating and reading
        Seat seat = new Seat("a", 12);
        System.out.println("=== BASICS ===");
        System.out.println("Row:      " + seat.row());          // not getRow()!
        System.out.println("Number:   " + seat.number());
        System.out.println("toString: " + seat);                 // free, readable

        // 2. equals + hashCode for free -> the Set works
        System.out.println("\n=== IN A SET ===");
        Set<Seat> booked = new HashSet<>();
        System.out.println("Book A12: " + booked.add(new Seat("A", 12)));
        System.out.println("Book a12: " + booked.add(new Seat("a", 12)));   // same seat!
        System.out.println("Size:     " + booked.size());

        // 3. Validation in the compact constructor
        System.out.println("\n=== VALIDATION ===");
        try {
            new Seat("A", -5);
        } catch (IllegalArgumentException e) {
            System.out.println("Rejected: " + e.getMessage());
        }
        try {
            new Seat("  ", 3);
        } catch (IllegalArgumentException e) {
            System.out.println("Rejected: " + e.getMessage());
        }

        // 4. Records are immutable: no setters. To "change", make a new one.
        System.out.println("\n=== IMMUTABLE ===");
        BookingRequest request = new BookingRequest(42, new Seat("B", 7));
        BookingRequest moved = new BookingRequest(request.eventId(), new Seat("B", 8));
        System.out.println("Original: " + request);
        System.out.println("Moved:    " + moved);
    }
}

// ---------------------------------------------------------------
// One line gives you: private final fields, constructor, accessors,
// equals(), hashCode(), toString().
// ---------------------------------------------------------------
record Seat(String row, int number) {

    // "Compact constructor": runs before the fields are set.
    // Perfect place for validation and clean-up.
    Seat {
        if (row == null || row.isBlank()) {
            throw new IllegalArgumentException("Row must not be empty");
        }
        if (number <= 0) {
            throw new IllegalArgumentException("Seat number must be positive, got " + number);
        }
        row = row.trim().toUpperCase();   // "a" and "A" become the same seat
    }
}

// Records can contain other records. This is the shape of a typical API request body.
record BookingRequest(int eventId, Seat seat) { }
