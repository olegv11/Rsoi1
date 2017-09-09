import hello.HelloGreeter;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
public class HelloGreeterTest {
    HelloGreeter helloGreeter = new HelloGreeter();

    @Test
    public void HelloGreeterSaysHelloName() {
        Assert.assertEquals(helloGreeter.say("RSOI"), "Hello, RSOI!");
        Assert.assertEquals(helloGreeter.say("World"), "Hello, World!");
    }

    @Test
    public void HelloGreeterSaysHelloNobodyOnNull() {
        Assert.assertEquals(helloGreeter.say(null), "Hello, nobody!");
    }
}
