package ec.com.eurofish.service;

// @ApplicationScoped
public class BusinessPaaSService /* extends ReactivePanacheMongoEntity */ {
    // static final Logger log = Logger.getLogger(BusinessPaaSService.class);

    // @Inject
    // ReactivePanacheMongoRepository<BusinessOnePaaSRequest> repo;
    // public String ip;
    // public Integer port;
    // public String rootPath;
    // public Map<String, Object> loginBody;
    // public Integer timeout;
    // public String cookie;

    // public void updateCookie(String id, String cookie) {
    // // log.info(id);
    // repo.findById(new ObjectId(id))
    // .onItem()
    // // BusinessPaaSService.stream("_id = ?1", new ObjectId(id))
    // // .onItem()
    // .call(item -> {
    // log.info(item.getClass().getName());
    // item.setCookie(cookie);
    // return repo.persistOrUpdate(item);
    // });

    // }

    // public Uni<BusinessOnePaaSRequest> bySerial(String bson) {
    // return repo.findById(new ObjectId(bson));
    // // return BusinessPaaSService.findById(new ObjectId(bson))
    // // .onItem().transform(x -> (BusinessPaaSService) x).toMulti();
    // }

    // public BusinessOnePaaSRequest retrievePaaS(String bson) {
    // CompletableFuture<BusinessOnePaaSRequest> future = new CompletableFuture<>();
    // CompletableFuture.runAsync(() -> bySerial(bson)
    // .subscribe()
    // .with(item -> future.complete(item)));
    // return future.join();
    // // return BusinessOnePaaSRequest.fromMongoItem(future.join());
    // }

}
