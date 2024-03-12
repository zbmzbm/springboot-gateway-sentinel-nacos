package com.example.springbootdemo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zbm
 * @date 2024/3/1117:21
 */
@RestController
@RequestMapping("/demo")
public class TestController {

    @GetMapping("/test")
    public String test() throws InterruptedException {
        //int n=10/0;
        Thread.sleep(2000);
        return "hello world";
    }

}
