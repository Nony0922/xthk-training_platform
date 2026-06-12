package com.example.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "spark")
public class SparkProperties {
    /** HTTP 接口 APIPassword，控制台 http 服务接口认证信息中获取 */
    private String apiPassword = "";
    /** 接口地址 */
    private String apiUrl = "https://spark-api-open.xf-yun.com/v1/chat/completions";
    /** 模型版本：4.0Ultra / generalv3.5 / lite 等 */
    private String model = "4.0Ultra";
    /** 是否启用大模型建议（未配置 APIPassword 时自动关闭） */
    private boolean enabled = true;
    /** 大模型调用超时（秒） */
    private int timeoutSeconds = 45;
}
