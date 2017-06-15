package io.sporing;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by nschutta on 5/31/17.
 */
@Controller
public class IndexController {
    @RequestMapping(value = "/")
    public String index() {
        return "login";
    }
}
