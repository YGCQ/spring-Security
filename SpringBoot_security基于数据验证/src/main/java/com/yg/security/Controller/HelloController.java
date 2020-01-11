package com.yg.security.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    @GetMapping("/admin/hello")
    public String admin() {
        System.out.println("hello admin");
        return "hello admin";
    }
    @GetMapping("/db/hello")
    public String dba() {
        System.out.println("hello dba");
        return "hello dba";
    }
    @GetMapping("/user/hello")
    public String user() {
        System.out.println("hello user");
        return "hello user";
    }
    @GetMapping("/hello")
    public String  hello(){
        System.out.println("hello");
        return "hello";
    }

}