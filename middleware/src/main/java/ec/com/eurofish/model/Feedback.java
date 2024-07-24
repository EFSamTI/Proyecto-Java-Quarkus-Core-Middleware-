package ec.com.eurofish.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Feedback {
    String topic;
    String source;
    String destination;
    String operation;
    String verb;
    String path;

}
