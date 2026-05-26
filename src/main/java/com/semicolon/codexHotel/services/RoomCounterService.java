package com.semicolon.codexHotel.services;

import com.semicolon.codexHotel.data.models.RoomCounter;
import com.semicolon.codexHotel.data.models.enums.RoomType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;



@Service
@RequiredArgsConstructor
public class RoomCounterService {
    private final MongoTemplate mongoTemplate;

    public String generateRoomNumber(RoomType roomType){
        Query query = new Query(Criteria.where("_id").is(roomType.name()));
        Update update = new Update().inc("counter", 1);

        FindAndModifyOptions options = FindAndModifyOptions.options()
                .returnNew(true)
                .upsert(true);

        RoomCounter counter = mongoTemplate.findAndModify(
                query, update, options, RoomCounter.class
        );

        if (counter == null) {
            throw new RuntimeException("Failed to generate room number");
        }

        int count = counter.getCount();

        return switch(roomType) {
            case SINGLE -> "SN-" + count;
            case DOUBLE -> "DB-" + count;
            case SUITE ->  "ST-" + count;

        };
    }
}
