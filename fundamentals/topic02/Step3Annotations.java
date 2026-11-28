// Topic 2, step 3: annotations, and how a framework reads them
// Run:  java fundamentals/topic02/Step3Annotations.java

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

public class Step3Annotations {

    public static void main(String[] args) throws Exception {
        EventController controller = new EventController();

        // Pretend these paths came from HTTP requests (Topic 1!)
        System.out.println("GET /events      -> " + MiniFramework.handle(controller, "/events"));
        System.out.println("GET /events/42   -> " + MiniFramework.handle(controller, "/events/42"));
        System.out.println("GET /bookings    -> " + MiniFramework.handle(controller, "/bookings"));
    }
}

// ---------------------------------------------------------------
// 1. DEFINE our own annotation (a "sticky note" we can put on methods)
// ---------------------------------------------------------------
@Retention(RetentionPolicy.RUNTIME)   // keep the note while the program RUNS
@Target(ElementType.METHOD)           // the note may only go on methods
@interface GetMapping {
    String value();                   // the note carries one piece of data: a path
}

// ---------------------------------------------------------------
// 2. USE the annotation (stick notes on methods)
// ---------------------------------------------------------------
class EventController {

    @GetMapping("/events")
    public String listEvents() {
        return "[Coldplay Live, Sunburn Festival]";
    }

    @GetMapping("/events/42")
    public String getEvent() {
        return "{ id: 42, name: Coldplay Live }";
    }

    // No note on this one, so the framework will ignore it
    public String helper() {
        return "I am not an endpoint";
    }
}

// ---------------------------------------------------------------
// 3. READ the annotations (this is what Spring does for you)
// ---------------------------------------------------------------
class MiniFramework {

    static String handle(Object controller, String path) throws Exception {
        // Look at every method of the controller class...
        for (Method method : controller.getClass().getDeclaredMethods()) {

            // ...does it have a @GetMapping note?
            GetMapping mapping = method.getAnnotation(GetMapping.class);

            // ...and does the note's path match the request?
            if (mapping != null && mapping.value().equals(path)) {
                Object result = method.invoke(controller);   // call it
                return "200 OK  " + result;
            }
        }
        return "404 Not Found";
    }
}
