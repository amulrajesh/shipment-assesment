package com.assignment.aggregationflux.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

public class AppUtil {

    private static final Logger log = LoggerFactory.getLogger(AppUtil.class);

    public static Map<String, List<String>> getQueueMap(ConcurrentLinkedQueue<Map<String, List<String>>> incomingQueue) {

        if (incomingQueue.isEmpty()) {
            log.debug("GetQueueMap :: Incoming queue is empty" );
            return Collections.emptyMap();
        }

        Map<String, List<String>> map = new HashMap<>();
        int count = 1;
        while (count < 5) {
            if (incomingQueue.isEmpty()) {
                //Breaking condition
                break;
            }
            Map<String, List<String>> tempMap = incomingQueue.poll();
            tempMap.keySet().forEach(k -> map.put(k, tempMap.get(k)));
            count++;
        }
        return map;

    }

    public static List<String> mergedData(Map<String, List<String>> map) {
        return map.entrySet().stream().flatMap(v -> v.getValue().stream()).collect(Collectors.toList());
    }

    public static void addToIncomingQueueMap(ConcurrentLinkedQueue<Map<String, List<String>>> incomingQueue,
                                             String key, List<String> ids) {
        Map<String, List<String>> map = new HashMap<>();
        map.put(key, ids);
        incomingQueue.add(map);
    }

    /**
     * Method to get API response data using key and remove from the shipmentQueue and outgoingMap
     *
     * @param key unique key
     * @return api response
     */
    public static Mono<Map> getResponse(String key,
                                        Map<String, List<String>> shipmentQueue,
                                        Map<String, Mono<Map>> outgoingMap) {

        Mono<Map> map = outgoingMap.get(key);

        //Remove data from map
        outgoingMap.remove(key);
        shipmentQueue.remove(key);

        return map.map(m -> m);

    }
}
