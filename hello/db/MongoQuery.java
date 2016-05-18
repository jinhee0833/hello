package com.helloants.mm.helloants1.db;

import com.mongodb.BasicDBObject;

/**
 * Created by JJ on 2016-01-18.
 */
public enum MongoQuery {
    INSTANCE;

    public final int REPLY_PAGE_SIZE = 5;
    public final int REPLY_PAGE_LIMIT = 10;
    public final int REPLY_BEST_LIMIT = 5;
    public final int QANDA_PAGE_SIZE = 10;
    public final int REQUEST_PAGE_SIZE = 10;
    public final int DISCUSS_PAGE_SIZE = 10;

    public BasicDBObject Content() { return new BasicDBObject(); }
    public BasicDBObject NewsContent() {
        return new BasicDBObject("area","news");
    }
    public BasicDBObject PcmtContent() {
        return new BasicDBObject("area","pcmt");
    }
    public BasicDBObject TipContent() {
        return new BasicDBObject("area","tip");
    }
    public BasicDBObject ContentDetail(int id) {
        return new BasicDBObject("cNum",id);
    }
    public BasicDBObject replyList(int id) { return new BasicDBObject("contentId", id); }
    public BasicDBObject id(int id) { return new BasicDBObject("_id", id); }
    public BasicDBObject email(String email) { return new BasicDBObject("email", email); }
    public BasicDBObject numberCount() { return new BasicDBObject("_id", "userid"); }
}
