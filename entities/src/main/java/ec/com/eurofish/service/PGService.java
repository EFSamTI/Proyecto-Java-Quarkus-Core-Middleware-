package ec.com.eurofish.service;

import java.util.List;

import ec.com.eurofish.model.BusinessOnePaaSRequest;
// import ec.com.eurofish.model.GenericPaaSRequest;
import ec.com.eurofish.model.PGPaaSModel;
import io.quarkus.reactive.datasource.ReactiveDataSource;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.Tuple;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class PGService {

    @Inject
    @ReactiveDataSource("entities")
    PgPool pg;

    public Multi<PGPaaSModel> retrievePaaSByBson(String bson) {
        return pg.preparedQuery("select * from retrieve_paas_per_bson($1)")
                .execute(Tuple.of(bson))
                .onItem().transformToMulti(set -> Multi.createFrom().iterable(set))
                .onItem().transform(PGPaaSModel::from);
    }

    public Multi<PGPaaSModel> retrievePaaSList() {
        return pg.preparedQuery("select * from retrieve_paas_all()")
                .execute()
                .onItem().transformToMulti(set -> Multi.createFrom().iterable(set))
                .onItem().transform(PGPaaSModel::from);
    }

    public Multi<PGPaaSModel> retrievePaaSListByBson(List<String> bson) {
        return pg.preparedQuery("select * from retrieve_paas_list_per_bson($1)")
                .execute(Tuple.of(String.join(",", bson)))
                .onItem().transformToMulti(set -> Multi.createFrom().iterable(set))
                .onItem().transform(PGPaaSModel::from);
    }

    // public Uni<Void> saveGenericPaaS(GenericPaaSRequest request) {
    // return pg.preparedQuery("select save_paas_record($1)")
    // .execute(request.getPGJsonBody())
    // .replaceWithVoid();
    // }

    public Uni<Void> saveBusinessOnePaaS(BusinessOnePaaSRequest request) {
        return pg.preparedQuery("select save_paas_record($1)")
                .execute(request.getPGJsonBody())
                .replaceWithVoid();
    }

}
