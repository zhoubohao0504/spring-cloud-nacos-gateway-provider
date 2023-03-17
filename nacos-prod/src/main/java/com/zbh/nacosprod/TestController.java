package com.zbh.nacosprod;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping
public class TestController {

    @RequestMapping ("/getTest")
    @ResponseBody
    public String getTest(){
        return "this is prod";
    }
}
