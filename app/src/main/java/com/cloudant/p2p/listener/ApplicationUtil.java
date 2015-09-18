
package com.cloudant.p2p.listener;

import android.content.Context;
import android.util.Log;

import com.cloudant.sync.datastore.Datastore;
import com.cloudant.sync.datastore.DatastoreManager;
import com.cloudant.sync.datastore.DatastoreNotCreatedException;
import com.cloudant.sync.replication.Replicator;
import com.cloudant.sync.replication.ReplicatorBuilder;
import com.simpragma.samplep2p.Listener;

import org.restlet.Component;
import org.restlet.Server;
import org.restlet.data.Protocol;
import org.restlet.routing.Router;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.CountDownLatch;

/**
 * Created by snowch on 29/01/15.
 */
public class ApplicationUtil {

    public static Datastore ds;
    private static DatastoreManager manager;
    public static final String DBNAME = "mydb";

    public static void startServer(Context context) {
        Log.d("TAGGED", "Came within startServer");
        try {
            createDevelopmentDatabase(context);
            Log.d("TAGGED", "Dev database created " + manager.listAllDatastores());

            // Set up a Restlet service
            final Router router = new Router();
            router.attachDefault(HttpListener.class);

            org.restlet.Application myApp = new org.restlet.Application() {

                @Override
                public org.restlet.Restlet createInboundRoot() {
                    router.setContext(getContext());
                    return router;
                };
            };
            Component component = new Component();
            component.getDefaultHost().attach("/", myApp);

            new Server(Protocol.HTTP, 8182, component).start();
            Log.d("TAGGED", "Server started ");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void createDevelopmentDatabase(Context context) {

        // some temporary code for development purposes :)
        File path = context.getFilesDir();
        manager = new DatastoreManager(path.getAbsolutePath());

        // make sure we have a database for development
        try {
            ds = manager.openDatastore(DBNAME);
        } catch (DatastoreNotCreatedException e) {
            e.printStackTrace();
        }

        // MutableDocumentRevision rev = new MutableDocumentRevision();
        // Map<String, Object> json = new HashMap<String, Object>();
        // json.put("description", "Buy milk");
        // json.put("completed", false);
        // json.put("type", "com.cloudant.sync.example.task");
        // rev.body = DocumentBodyFactory.create(json);
        // ds.createDocumentFromRevision(rev);
        // ds.close();
    }

    public static void startSync(String url) throws URISyntaxException, InterruptedException,
            DatastoreNotCreatedException {
        // Username/password are supplied in the URL and can be Cloudant API keys
        URI uri = new URI(url);

        // Create a replicator that replicates changes from the remote
        // database to the local datastore.
        Replicator replicator = ReplicatorBuilder.pull().from(uri).to(ds).build();

        // Use a CountDownLatch to provide a lightweight way to wait for completion
        CountDownLatch latch = new CountDownLatch(1);
        Listener listener = new Listener(latch);
        replicator.getEventBus().register(listener);
        replicator.start();
        latch.await();
        replicator.getEventBus().unregister(listener);
        if (replicator.getState() != Replicator.State.COMPLETE) {
            System.out.println("Error replicating FROM remote");
            System.out.println("Error" + listener.error);
        }
    }
}
