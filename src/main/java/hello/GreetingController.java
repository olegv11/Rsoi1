package hello;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalTime;

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

    @RequestMapping("/")
    public String index(Model model) {
        model.addAttribute("time", LocalTime.now().toString());
        return "index";
    }

    @Autowired
    private final Greeter greeter;
}
