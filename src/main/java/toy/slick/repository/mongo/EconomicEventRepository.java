package toy.slick.repository.mongo;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.sql.Timestamp;

public interface EconomicEventRepository extends MongoRepository<EconomicEventRepository.EconomicEvent, String> {
    @Builder
    @Getter
    class EconomicEvent extends MongoData<EconomicEvent> {
        private String zonedDateTime;
        private String id;
        private String name;
        private String country;
        private String importance;
        private String actual;
        private String forecast;
        private String previous;

        @Override
        public EconomicEvent toMongoData(String _id) {
            this._id = _id;
            this._timestamp = new Timestamp(System.currentTimeMillis());

            return this;
        }
    }
}
