package toy.slick.repository.mongo;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.annotation.Id;

import java.sql.Timestamp;

public abstract class MongoData<T> {
    @Id
    protected String _id = StringUtils.EMPTY;
    protected Timestamp _timestamp;

    public abstract T toMongoData(String _id);
}
