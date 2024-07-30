package ec.com.eurofish.service;

import java.util.List;
import java.util.UUID;

import ec.com.eurofish.model.PaaSModel;
import io.quarkus.reactive.datasource.ReactiveDataSource;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.Tuple;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class PostgreSQLService {

    @Inject
    @ReactiveDataSource("entities")
    PgPool pg;

    public Multi<PaaSModel> retrievePaaSByWebId(String webId) {
        return pg.preparedQuery("select * from retrieve_paas_per_webid($1)")
                .execute(Tuple.of(webId))
                .onItem().transformToMulti(set -> Multi.createFrom().iterable(set))
                .onItem().transform(PaaSModel::from);
    }

    public Multi<PaaSModel> retrievePaaSList() {
        return pg.preparedQuery("select * from retrieve_paas_all()")
                .execute()
                .onItem().transformToMulti(set -> Multi.createFrom().iterable(set))
                .onItem().transform(PaaSModel::from);
    }

    public Multi<PaaSModel> retrievePaaSListByBson(List<String> bson) {
        return pg.preparedQuery("select * from retrieve_paas_list_per_bson($1)")
                .execute(Tuple.of(String.join(",", bson)))
                .onItem().transformToMulti(set -> Multi.createFrom().iterable(set))
                .onItem().transform(PaaSModel::from);
    }

    public Uni<String> updateCookie(UUID webId, String cookie) {
        return pg.preparedQuery("select update_cookie($1, $2)")
                .execute(Tuple.of(webId, cookie))
                .replaceWith(cookie);
    }

}
