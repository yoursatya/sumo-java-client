package com.sumologic.client.search;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sumologic.client.Credentials;
import com.sumologic.client.UrlParameters;
import com.sumologic.client.model.LogMessage;
import com.sumologic.client.model.SearchRequest;
import com.sumologic.client.model.SearchResponse;
import com.sumologic.client.util.HttpUtils;
import com.sumologic.client.util.HttpUtils.ResponseHandler;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SearchClient {

    public static SearchResponse search(String protocol, String hostname, int port,
                                        Credentials credentials, SearchRequest request) {
        return HttpUtils.httpGet(protocol, hostname, port, credentials,
                getSearchEndpoint(), request, new SearchHandler());
    }

    private static String getSearchEndpoint() {
        return UrlParameters.LOGS_SERVICE + "/" + UrlParameters.SEARCH;
    }

    private static class SearchHandler implements ResponseHandler<SearchRequest, SearchResponse> {

        @Override
        public SearchResponse handle(InputStream httpStream,
                                     SearchRequest request) throws IOException {

            ObjectMapper mapper = new ObjectMapper();
            List<Map<String, String>> rawMessages = mapper.readValue(httpStream,
                    new TypeReference<List<Map<String, String>>>() {});

            List<LogMessage> messages = new ArrayList<LogMessage>();
            for (Map<String, String> map : rawMessages) {
                messages.add(new LogMessage(map));
            }
            return new SearchResponse(request, messages);
        }
    }
}
