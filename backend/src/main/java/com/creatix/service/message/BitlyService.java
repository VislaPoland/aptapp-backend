package com.creatix.service.message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.creatix.configuration.BitlyProperties;
import com.creatix.message.MessageDeliveryException;

import net.swisstech.bitly.BitlyClient;
import net.swisstech.bitly.model.Response;
import net.swisstech.bitly.model.v3.ShortenResponse;

/**
 * @author <a href="mailto:martin@thinkcreatix.com.com">martin dupal</a>
 */
@Component
public class BitlyService {

    private static final Logger logger = LoggerFactory.getLogger(BitlyService.class);

    @Autowired
    private BitlyProperties bitlyProperties;

    public String getShortUrl(String longUrl) throws MessageDeliveryException {
        // TODO Bitly doesn't support localhost URL so we will change 'localhost' to 'localhost.eu' for DEV ENV purpose
        BitlyClient client = new BitlyClient(bitlyProperties.getAccessToken());
        Response<ShortenResponse> respShort = client.shorten()
                .setLongUrl(longUrl.replace("localhost", "localhost.eu") )
                .call();

        if (respShort.status_code != 200) {
            logger.error("BitlyClient return code " + respShort.status_code + " with msg " + respShort.status_txt);
            throw new MessageDeliveryException("BitlyClient return code " + respShort.status_code);
        }

        return respShort.data.url;
    }
}
