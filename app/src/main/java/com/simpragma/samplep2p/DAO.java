package com.simpragma.samplep2p;

import com.cloudant.p2p.listener.ApplicationUtil;
import com.cloudant.sync.datastore.BasicDocumentRevision;
import com.cloudant.sync.datastore.DocumentBodyFactory;
import com.cloudant.sync.datastore.DocumentException;
import com.cloudant.sync.datastore.MutableDocumentRevision;
import com.cloudant.sync.query.IndexManager;
import com.cloudant.sync.query.QueryResult;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by swagata on 15/09/15.
 */
public class DAO {
    public void save(Map<String,Object> body) throws DocumentException {
        MutableDocumentRevision revision = new MutableDocumentRevision();
        revision.body = DocumentBodyFactory.create(body);
        BasicDocumentRevision saved = ApplicationUtil.ds.createDocumentFromRevision(revision);
    }

    public List<String> getAll(){
        List<String> fields = Arrays.asList("name");
        Map<String, Object> query = new HashMap<String, Object>();
        IndexManager im = new IndexManager(ApplicationUtil.ds);
        QueryResult queryResult = im.find(query, 0, 0, fields, null);
        return queryResult.documentIds();
    }
}
