package Https.http2;

import java.util.HashMap;
import java.util.Map;

public class HttpMessageMappingContainer {

    private Map<String, String> streamIdToSource = new HashMap<>();

    public void add(String streamId, String source){
        streamIdToSource.put(streamId, source);
    }

    public String checkIfExist(String streamId){
        if(streamIdToSource.containsKey(streamId)){
            return streamIdToSource.remove(streamId);
        }

        throw new IllegalArgumentException("streamId is not exist, cannot send to client");
    }
}
