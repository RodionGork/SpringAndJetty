package eventrouter.mvc;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/")
public class MainController {

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public String doGet(HttpServletResponse resp) {
        return "<h1>Yeah, not too stupid!</h1>";
    }

}
