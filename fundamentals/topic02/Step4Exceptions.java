// Topic 2, step 4: exceptions, and turning them into HTTP status codes
// Run:              java fundamentals/topic02/Step4Exceptions.java
// See a crash:      java fundamentals/topic02/Step4Exceptions.java crash

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Step4Exceptions {

    public static void main(String[] args) {
        BookingService service = new BookingService();

        if (args.length > 0 && args[0].equals("crash")) {
            // No try/catch: watch what an uncaught exception looks like
            service.book(99, "B1");
            return;
        }

        ErrorHandler handler = new ErrorHandler(service);
        System.out.println(handler.handle(42, "B7"));    // happy path
        System.out.println(handler.handle(99, "B1"));    // event doesn't exist
        System.out.println(handler.handle(42, "A12"));   // seat already taken
        System.out.println(handler.handle(42, null));    // a bug in our code
    }
}

// ---------------------------------------------------------------
// 1. Our OWN exception types: one for each thing that can go wrong
// ---------------------------------------------------------------
class EventNotFoundException extends RuntimeException {
    EventNotFoundException(int eventId) {
        super("Event " + eventId + " does not exist");
    }
}

class SeatAlreadyBookedException extends RuntimeException {
    SeatAlreadyBookedException(String seat) {
        super("Seat " + seat + " is already booked");
    }
}

// ---------------------------------------------------------------
// 2. The service THROWS when a rule is broken. It doesn't print,
//    doesn't return null, doesn't know about HTTP.
// ---------------------------------------------------------------
class BookingService {

    private final Map<Integer, String> events = Map.of(42, "Coldplay Live");
    private final Set<String> bookedSeats = new HashSet<>(Set.of("A12"));

    String book(int eventId, String seat) {
        String event = events.get(eventId);
        if (event == null) {
            throw new EventNotFoundException(eventId);          // stop right here
        }

        String cleanSeat = seat.trim().toUpperCase();           // crashes if seat is null!

        if (!bookedSeats.add(cleanSeat)) {                       // add() returns false if already there
            throw new SeatAlreadyBookedException(cleanSeat);
        }
        return "Booked seat " + cleanSeat + " for " + event;
    }
}

// ---------------------------------------------------------------
// 3. ONE place that CATCHES exceptions and turns them into HTTP answers.
//    (Spring's version of this is called @ExceptionHandler)
// ---------------------------------------------------------------
class ErrorHandler {

    private final BookingService service;

    ErrorHandler(BookingService service) {
        this.service = service;
    }

    String handle(int eventId, String seat) {
        try {
            return "201 Created     " + service.book(eventId, seat);

        } catch (EventNotFoundException e) {
            return "404 Not Found   " + e.getMessage();

        } catch (SeatAlreadyBookedException e) {
            return "409 Conflict    " + e.getMessage();

        } catch (Exception e) {
            // Anything we did NOT expect is OUR bug -> 500.
            // Log the details for us; tell the client only something generic.
            System.out.println("   [server log] " + e);
            return "500 Server Error Something went wrong, please try again";
        }
    }
}
