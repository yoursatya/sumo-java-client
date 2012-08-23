package com.sumologic.client;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import java.util.Map;

/**
 * A sumo server-side exception comprising the HTTP status code,
 * error identifier and code, the query uri used and a human-readable
 * message.
 *
 * @author Sebastian Mies
 * @author Daphne Hsieh
 * @version 1.0
 */
public class SumoServerException extends SumoException {

    /**
     * Constructs a server exception from a JSON error message
     *
     * @param uri  The URI that caused the exception
     * @param json The JSON error message
     * @throws SumoClientException Thrown if the JSON error message contains syntactic errors.
     */
    protected SumoServerException(String uri, String json) throws SumoClientException {
        super(json);
        this.uri = uri;

        // Parse JSON string
        ObjectMapper mapper = new ObjectMapper();
        try {
            Map<String, String> kv =
                    mapper.readValue(json, new TypeReference<Map<String, String>>() {
                    });

            // get fields
            status = Integer.parseInt(kv.get("status"));
            message = kv.get("message");
            id = kv.get("id");
            code = kv.get("code");
        } catch (Exception e) {
            throw new SumoClientException("Exception while parsing JSON: \n" + json, e);
        }
    }

    /**
     * Returns the HTTP status code.
     *
     * @return The HTTP status code.
     */
    public final int getHTTPStatus() {
        return status;
    }

    /**
     * Returns the identifier of the error., e.g.,
     * "unknown.timezone" when the timezone is unknown
     *
     * @return The identifier of the error
     */
    public final String getErrorId() {
        return id;
    }

    /**
     * Returns the error code.
     *
     * @return The error code.
     */
    public final String getErrorCode() {
        return code;
    }

    /**
     * Returns a the human-readable message associated with
     * this exception.
     *
     * @return A the human-readable message
     */
    public final String getErrorMessage() {
        return message;
    }

    /**
     * Returns the URI that caused the exception.
     *
     * @return The URI that caused the exception.
     */
    public final String getURI() {
        return uri;
    }

    /**
     * Returns true, if the error is equal to a server error, e.g.,
     * {@see SumoSearchErrors.EMPTY_FIELD_LIST}.
     *
     * @param error The error to compare
     * @return True, if the exception equals the given server error
     */
    public boolean equals(SumoServerError error) {
        return error.getId().equals(id.toLowerCase().trim());
    }

    private String uri;
    private int status;
    private String message;
    private String id;
    private String code;
}
