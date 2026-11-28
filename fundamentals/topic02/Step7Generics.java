// Topic 2, step 7: generics — one piece of code, many types, still type-safe
// Run:  java fundamentals/topic02/Step7Generics.java

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Step7Generics {

    public static void main(String[] args) {
        withoutGenerics();
        withGenerics();
        genericRepository();
    }

    // ---------------------------------------------------------------
    // 1. The OLD way: hold anything as Object, cast when reading
    // ---------------------------------------------------------------
    static void withoutGenerics() {
        System.out.println("=== WITHOUT GENERICS ===");
        ObjectBox box = new ObjectBox();
        box.put("Coldplay Live");
        String name = (String) box.get();               // cast needed
        System.out.println("Got: " + name);

        box.put(42);                                     // compiler happily allows this...
        try {
            String oops = (String) box.get();            // ...and it explodes at RUNTIME
            System.out.println(oops);
        } catch (ClassCastException e) {
            System.out.println("Crash: " + e.getMessage());
        }
    }

    // ---------------------------------------------------------------
    // 2. The generic way: the type is a parameter, checked at COMPILE time
    // ---------------------------------------------------------------
    static void withGenerics() {
        System.out.println("\n=== WITH GENERICS ===");
        Box<String> nameBox = new Box<>();
        nameBox.put("Coldplay Live");
        String name = nameBox.get();                     // no cast
        System.out.println("Got: " + name);
        // nameBox.put(42);                              // won't even compile

        Box<Integer> idBox = new Box<>();                // same class, different type
        idBox.put(42);
        System.out.println("Got: " + idBox.get());
    }

    // ---------------------------------------------------------------
    // 3. A generic repository: ONE class stores events, bookings, anything
    // ---------------------------------------------------------------
    static void genericRepository() {
        System.out.println("\n=== GENERIC REPOSITORY ===");

        Repository<Event, Integer> events = new InMemoryRepository<>();
        events.save(42, new Event(42, "Coldplay Live"));
        events.save(7, new Event(7, "Sunburn Festival"));

        Repository<Booking, String> bookings = new InMemoryRepository<>();
        bookings.save("BK-1", new Booking("BK-1", 42, "A12"));

        Event event = events.findById(42);               // returns Event, no cast
        System.out.println("Event 42:      " + event.name());
        System.out.println("All events:    " + events.findAll());
        System.out.println("Booking BK-1:  " + bookings.findById("BK-1"));
    }
}

// --- the old way ---
class ObjectBox {
    private Object value;
    void put(Object value) { this.value = value; }
    Object get() { return value; }
}

// --- the generic way: T is a placeholder for "some type, decided later" ---
class Box<T> {
    private T value;
    void put(T value) { this.value = value; }
    T get() { return value; }
}

// --- a generic contract with TWO type parameters ---
// T  = what we store,  ID = the type of its key
interface Repository<T, ID> {
    void save(ID id, T item);
    T findById(ID id);
    List<T> findAll();
}

class InMemoryRepository<T, ID> implements Repository<T, ID> {
    private final Map<ID, T> store = new HashMap<>();

    public void save(ID id, T item) { store.put(id, item); }
    public T findById(ID id)        { return store.get(id); }
    public List<T> findAll()        { return new ArrayList<>(store.values()); }
}

record Event(int id, String name) { }
record Booking(String id, int eventId, String seat) { }
