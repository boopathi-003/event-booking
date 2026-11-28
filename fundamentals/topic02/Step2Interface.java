// Topic 2, step 2: the RIGHT way (depend on an interface)
// Run:  java fundamentals/topic02/Step2Interface.java

public class Step2Interface {

    public static void main(String[] args) {
        // Same BookingService, two different notifiers.
        // BookingService's code did not change at all.
        BookingService withEmail = new BookingService(new EmailNotifier());
        withEmail.book("Ravi", "Coldplay Live", "A12");

        System.out.println("-----");

        BookingService withSms = new BookingService(new SmsNotifier());
        withSms.book("Ravi", "Coldplay Live", "A13");
    }
}

// The contract: "anything that can notify a user".
// It says WHAT, not HOW.
interface Notifier {
    void send(String user, String message);
}

// One way to keep the contract
class EmailNotifier implements Notifier {
    @Override
    public void send(String user, String message) {
        System.out.println("EMAIL to " + user + ": " + message);
    }
}

// Another way to keep the same contract
class SmsNotifier implements Notifier {
    @Override
    public void send(String user, String message) {
        System.out.println("SMS to " + user + ": " + message);
    }
}

class BookingService {

    // BookingService only knows the contract, not the concrete class.
    private final Notifier notifier;

    // Someone ELSE decides which Notifier to give us, and passes it in.
    BookingService(Notifier notifier) {
        this.notifier = notifier;
    }

    void book(String user, String event, String seat) {
        System.out.println("Booked seat " + seat + " for " + event);
        notifier.send(user, "Your seat " + seat + " is confirmed!");
    }
}
