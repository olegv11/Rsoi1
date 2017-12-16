package ru.oleg.rsoi.serviceAuth;

import lombok.Data;
import org.springframework.stereotype.Component;


@Data
@Component
public class ServiceCredentials {
    String appId;
    String appSecret;

    @Override
    public String toString() {
        String s = appId+":"+appSecret;
        return org.apache.commons.codec.binary.Base64.encodeBase64String(s.getBytes());
    }

}
