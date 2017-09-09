package hello;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@EnableAutoConfiguration
public class GreetingController {

    public GreetingController(Greeter greeter) {
        this.greeter = greeter;
    }

    @RequestMapping("/greeting")
    @ResponseBody
    public String greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
        return greeter.say(name);
    }

    @Autowired
    private final Greeter greeter;
}
