package ec.com.eurofish.model;

import io.vertx.mutiny.sqlclient.Row;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PGPaaSModel {
    Integer id;
    String description;
    String bsonid;

    public static PGPaaSModel from(Row row) {
        return PGPaaSModel.builder()
                .id(row.getInteger("id"))
                .description(row.getString("description"))
                .bsonid(row.getString("bsonid"))
                .build();
    }
}
