package cs3337.MedReminderbackend.Controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.util.Date;

import cs3337.MedReminderbackend.Util.MyLogger;


@RestController
@RequestMapping("/api")
public class RestApiController
{
    
    public RestApiController()
    {
        MyLogger.info("Constructing RestApiController");
    }
    
    
    // api
    
    /** <p>GET /api/hello</p>
     * 
     * This is a testing endpoint.
     * 
     * @return
     *  This endpoint will return a single string: "Hello: " + current time
     */
    @GetMapping("/hello")
    public String hello()
    {
        Date today = new Date();
        return "Hello: " + today.toString();
    }
    
}
