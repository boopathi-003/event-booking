// Topic 2, step 5: collections — List, Set, Map
// Run:  java fundamentals/topic02/Step5Collections.java

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class Step5Collections {

    public static void main(String[] args) {
        listDemo();
        setDemo();
        mapDemo();
        speedDemo();
        equalsHashCodeDemo();
    }

    // ---------------------------------------------------------------
    // LIST: ordered, allows duplicates, access by position
    // ---------------------------------------------------------------
    static void listDemo() {
        System.out.println("=== LIST ===");
        List<String> events = new ArrayList<>();
        events.add("Coldplay Live");
        events.add("Sunburn Festival");
        events.add("Coldplay Live");                  // duplicate: allowed

        System.out.println(events);                   // keeps insertion order
        System.out.println("First: " + events.get(0)); // by position (index)
        System.out.println("Size:  " + events.size());
    }

    // ---------------------------------------------------------------
    // SET: no duplicates
    // ---------------------------------------------------------------
    static void setDemo() {
        System.out.println("\n=== SET ===");
        Set<String> bookedSeats = new HashSet<>();

        System.out.println("Book A12: " + bookedSeats.add("A12"));   // true  = added
        System.out.println("Book B7:  " + bookedSeats.add("B7"));    // true  = added
        System.out.println("Book A12: " + bookedSeats.add("A12"));   // false = already there!
        System.out.println(bookedSeats);
    }

    // ---------------------------------------------------------------
    // MAP: key -> value, look up by key
    // ---------------------------------------------------------------
    static void mapDemo() {
        System.out.println("\n=== MAP ===");
        Map<Integer, String> eventsById = new HashMap<>();
        eventsById.put(42, "Coldplay Live");
        eventsById.put(7, "Sunburn Festival");

        System.out.println("Event 42: " + eventsById.get(42));
        System.out.println("Event 99: " + eventsById.get(99));      // missing key -> null
        eventsById.put(42, "Coldplay Live (Mumbai)");              // same key -> REPLACES
        System.out.println("Event 42: " + eventsById.get(42));
        System.out.println("Has 7?    " + eventsById.containsKey(7));
    }

    // ---------------------------------------------------------------
    // Why the choice matters: searching 100,000 seats, 1,000 times
    // ---------------------------------------------------------------
    static void speedDemo() {
        System.out.println("\n=== SPEED: is this seat booked? ===");
        List<String> seatList = new ArrayList<>();
        Set<String> seatSet = new HashSet<>();
        for (int i = 0; i < 100_000; i++) {
            seatList.add("S" + i);
            seatSet.add("S" + i);
        }

        long start = System.nanoTime();
        for (int i = 0; i < 1_000; i++) seatList.contains("S99999");
        long listMs = (System.nanoTime() - start) / 1_000_000;

        start = System.nanoTime();
        for (int i = 0; i < 1_000; i++) seatSet.contains("S99999");
        long setMs = (System.nanoTime() - start) / 1_000_000;

        System.out.println("List.contains: " + listMs + " ms");
        System.out.println("Set.contains:  " + setMs + " ms");
    }

    // ---------------------------------------------------------------
    // The classic bug: our own class in a HashSet
    // ---------------------------------------------------------------
    static void equalsHashCodeDemo() {
        System.out.println("\n=== OUR OWN CLASS IN A SET ===");

        Set<SeatWithoutEquals> broken = new HashSet<>();
        broken.add(new SeatWithoutEquals("A", 12));
        broken.add(new SeatWithoutEquals("A", 12));                 // same seat!
        System.out.println("Without equals/hashCode, size = " + broken.size());

        Set<SeatWithEquals> fixed = new HashSet<>();
        fixed.add(new SeatWithEquals("A", 12));
        fixed.add(new SeatWithEquals("A", 12));
        System.out.println("With equals/hashCode,    size = " + fixed.size());
    }
}

class SeatWithoutEquals {
    private final String row;
    private final int number;

    SeatWithoutEquals(String row, int number) {
        this.row = row;
        this.number = number;
    }
}

class SeatWithEquals {
    private final String row;
    private final int number;

    SeatWithEquals(String row, int number) {
        this.row = row;
        this.number = number;
    }

    // "Two seats are the same if row and number are the same"
    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof SeatWithEquals seat)) return false;
        return number == seat.number && row.equals(seat.row);
    }

    // Equal seats MUST give the same hash code (used to pick the bucket)
    @Override
    public int hashCode() {
        return Objects.hash(row, number);
    }
}
