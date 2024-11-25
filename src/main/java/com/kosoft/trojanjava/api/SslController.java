package com.kosoft.trojanjava.api;

import cn.hutool.core.io.FileUtil;
import com.kosoft.trojanjava.bean.Ssl;
import com.kosoft.trojanjava.config.SslConfig;
import com.kosoft.trojanjava.utils.RestApiResult;
import io.netty.util.internal.StringUtil;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RequestMapping(value = "/ssl",method = RequestMethod.POST)
@RestController
public class SslController {

    @Resource
    private SslConfig sslConfig;
    /**
     * 更改SSL证书
     */
    @RequestMapping(value = "/alterSslCert",method = RequestMethod.POST)
    public RestApiResult alterSslCert(@RequestBody Ssl ssl) {

        if (StringUtil.isNullOrEmpty(ssl.getCert()) || StringUtil.isNullOrEmpty(ssl.getKey())){
            return RestApiResult.failed("证书无效！");
        }
        FileUtil.writeBytes(ssl.getCert().getBytes(), sslConfig.getSslCertPath());
        FileUtil.writeBytes(ssl.getKey().getBytes(), sslConfig.getSslKeyPath());
        try {
            sslConfig.updateSslContext(null);
        } catch (IOException e) {
            return RestApiResult.failed("证书更新失败:"+e.getMessage());
        }
        return RestApiResult.success("证书更新成功！");
    }

}
