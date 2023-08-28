package com.assignment.aggregationflux.utils;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AppClient {

    private static final Logger log = LoggerFactory.getLogger(AppClient.class);

    WebClientUtil webClientUtil;

    /**
     * Method to invoke API
     */
    public void invokeApi(ConcurrentLinkedQueue<Map<String, List<String>>> incomingQueue,
                          Map<String, Mono<Map>> outgoingMap, String apiTyp) {

        log.debug("Starting API processing " + apiTyp);

        // uid - payload
        Map<String, List<String>> incomingMap = AppUtil.getQueueMap(incomingQueue);
        //get normal map
        Map<String, Map> tempOutgoingMap = new HashMap<>();

        if (incomingMap == null || incomingMap.isEmpty()) {
            log.debug("Invoke API - Incoming map is empty" );
            return;
        }

        List<String> mergedData = AppUtil.mergedData(incomingMap);

        if (mergedData == null || mergedData.isEmpty()) {
            log.debug("Invoking API - Merged data is empty" );
            return;
        }

        /**
         *     uid          payload                 aggregatedResponse                  outgoingMap
         *     abc1         1,2,3                   <1,v1:2:v2:3:v3,4:v4,5:v5>          abc1:: 1,v1:2:v2,3:v3
         */

        //aggregated response
        Mono<Map> response = webClientUtil
                .processRequest(mergedData, apiTyp)
                .map(m -> {
                    m.forEach((k, v) -> {
                        parseResponse(tempOutgoingMap, (String) k, v, incomingMap);
                    });
                    //convert it to mono map
                    incomingMap.forEach((k, v) -> {
                        outgoingMap.put(k, Mono.just(tempOutgoingMap.get(k)));
                    });
                    return m;
                });

        response.subscribe();

        //Save the response for each key in outgoingMap
        //Parse the aggregated response with payload in incomingMap field
        System.out.println("Size " + tempOutgoingMap.size());
        incomingMap.keySet().forEach(k -> {
            outgoingMap.put(k, response);
        });

        log.debug("End API processing " + apiTyp);

    }

    private void parseResponse(Map<String, Map> tempOutgoingMap, String payloadId, Object response,
                                    Map<String, List<String>> incomingMap) {

        // OutgoingMap - uid - Resposne
        // IncomingMap - uid - list of payload
        Set<Map.Entry<String, List<String>>> entry = incomingMap.entrySet();

        for (Map.Entry<String, List<String>> stringListEntry : entry) {
            if(stringListEntry.getValue().contains(payloadId)) {
                String uid = stringListEntry.getKey();
                Map tempMap = tempOutgoingMap.getOrDefault(uid, new HashMap());
                tempMap.put(payloadId, response);
                tempOutgoingMap.put(uid, tempMap);
            }
        }
    }
}
