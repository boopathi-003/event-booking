// Topic 2, step 1: the WRONG way (tight coupling)
// Run:  java fundamentals/topic02/Step1TightCoupling.java

public class Step1TightCoupling {

    public static void main(String[] args) {
        BookingService bookingService = new BookingService();
        bookingService.book("Ravi", "Coldplay Live", "A12");
    }
}

class EmailSender {
    void sendEmail(String to, String message) {
        System.out.println("EMAIL to " + to + ": " + message);
    }
}

class BookingService {

    // BookingService creates its own EmailSender.
    // It is now glued to email forever.
    private EmailSender emailSender = new EmailSender();

    void book(String user, String event, String seat) {
        System.out.println("Booked seat " + seat + " for " + event);
        emailSender.sendEmail(user, "Your seat " + seat + " is confirmed!");
    }
}
