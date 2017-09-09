package hello;

import org.springframework.stereotype.Component;

@Component
public class HelloGreeter implements Greeter {

    @Override
    public String say(String name) {
        if (name == null) {
            return String.format(format, "nobody");
        }
        return String.format(format, name);
    }

    private String format = "Hello, %s!";
}
