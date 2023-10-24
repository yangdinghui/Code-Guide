package com.rome.invoice.core.domain.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Configuration;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;

/**
 * @author Gu Lifeng
 * @version 1.0
 * @date 2021/12/1 19:06
 */

@Configuration
@Slf4j
public class WorkIdConfig {

    /**
     * 设置workerIdValue的值
     */
    static {

        int workId = 0;
        try {
            workId = getWorkId();
        } catch (Exception e) {
            log.error("生成workId发生异常", e);
        }
        System.setProperty("workerIdValue", String.valueOf(workId));
    }


    /**
     * 根据机器名称生成workId
     *
     * @return
     */
    private static int getWorkId() {
        String hostAddress = "";
        //hostAddress = System.getenv("HOSTNAME");
        //hostAddress = InetAddress.getLocalHost().getHostAddress();
        hostAddress = findHostIP();
        log.info("============= hostAddress: {} =============", hostAddress);
        int[] ints = StringUtils.toCodePoints(hostAddress);
        int sum = 0;

        for (int b : ints) {
            sum += b;
        }

        int workId = (sum % 32);
        log.info("============== workId：{} =============", workId);
        return workId;
    }

    private static String findHostIP() {
        try {
            Enumeration<NetworkInterface> networks = NetworkInterface.getNetworkInterfaces();
            Enumeration<InetAddress> addresses;
            while (networks.hasMoreElements()) {
                addresses = networks.nextElement().getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress inetAddress = addresses.nextElement();
                    // 由于线上一台机器可能在多个网段，此处只取以10开头的网段。
                    if (inetAddress instanceof Inet4Address
                            && inetAddress.isSiteLocalAddress()
                            && StringUtils.startsWith(inetAddress.getHostAddress(), "10.")) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (Exception e) {
            // ignore;
        }
        // 兜底
        String tempIP = "127.0.0.1";
        try {
            tempIP = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e1) {
            // ignore
        }
        return tempIP;
    }
}
