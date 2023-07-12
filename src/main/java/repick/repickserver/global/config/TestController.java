package repick.repickserver.global.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {


    @GetMapping("/cors")
    public String test() {
        System.out.println("TestController.test");
        return "test";
    }
}
