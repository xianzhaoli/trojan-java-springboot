package com.kosoft.trojanjava.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/boot")
public class BootController {
    /**
     * 停机
     */
    @RequestMapping(value = "/shutdown")
    public void shutdown()
    {
        System.exit(0);
    }

    /**
     * 重启
     */
    @RequestMapping(value = "/restart")
    public void restart()
    {
        System.exit(1);
    }


}
