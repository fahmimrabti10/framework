package query;

import com.mongodb.BasicDBObject;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.mongodb.client.model.Aggregates.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class QueryBuilder {

    public static List<Bson> constraintQuery(Request req){

        List<Bson> pipeline = new ArrayList<>();

        if(!combineFilters(req.getQuery(), req.getSearch()).isEmpty())
        {
            Bson filter = match(new BasicDBObject(
                    combineFilters(
                            req.getQuery(),
                            req.getSearch())));
            pipeline.add(filter);
        }
        if(req.getField()!=null)
        {
            Bson project = project(new BasicDBObject(req.getField()));
            pipeline.add(project);
        }
        if(req.getSort()!=null)
        {
            Bson sort = sort(new BasicDBObject(req.getSort()));
            pipeline.add(sort);
        }

        if(req.getOffset()!=null) {
            Bson offset= skip(req.getOffset());
            pipeline.add(offset);
        }
        if(req.getLimit()!=null) {
            Bson limit = limit(req.getLimit());
            pipeline.add(limit);
        }
        return pipeline;
    }


    public static Map<String,Object> convertFilters(Map<String, BasicOperator> operatorMap){

        Map<String,Object> objectMap = new HashMap<>();
        if(operatorMap!=null){
            for (Map.Entry<String, BasicOperator> entry : operatorMap.entrySet())
            {
                objectMap.put(entry.getKey(),convertOperator(entry.getValue()));}
        }
        return objectMap;
    }

    public static Boolean validateType(BasicOperator operator){

        return ((operator.getValue() instanceof String)
                ||(operator.getValue() instanceof Number)
                ||(operator.getValue() instanceof Boolean)
                ||(operator.getValue() instanceof List<?>));
    }

    public static Map<String,Object> convertOperator( BasicOperator operator){
        Map<String,Object> objectMap = new HashMap<>();

        if(OperatorEnum.EQ.name().equals(operator.getOperator().name())
                &&Boolean.TRUE.equals((validateType(operator)))) {
            objectMap.put(MongoOperator.EQ, operator.getValue());
        }
        if(OperatorEnum.GT.name().equals(operator.getOperator().name())
                &&Boolean.TRUE.equals((validateType(operator)))) {
            objectMap.put(MongoOperator.GT, operator.getValue());
        }
        if(OperatorEnum.LT.name().equals(operator.getOperator().name())
                &&Boolean.TRUE.equals((validateType(operator)))) {
            objectMap.put(MongoOperator.LT, operator.getValue());
        }
        if(OperatorEnum.IN.name().equals(operator.getOperator().name())
                &&Boolean.TRUE.equals((validateType(operator)))) {
            objectMap.put(MongoOperator.IN, operator.getValue());
        }
        return objectMap;
    }


    public static Map<String,  Object> combineFilters(
            Map<String, BasicOperator> advancedFilters,
            Search search){


        return   Stream.of(
                        convertFilters(advancedFilters),
                        constraintSearchQuery(search))
                .flatMap(map -> map.entrySet().stream())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue));

    }

    public static Map<String,Object> constraintSearchQuery(Search search){
        Map<String,Object> objectMap = new HashMap<>();
        List<Object> queries = new ArrayList<>();
        if(search!=null){
            for(int i=0;i<search.getFields().size();i++){

                Document regexQuery = new Document();
                regexQuery.append("$regex", search.getTerm());
                BasicDBObject criteria = new BasicDBObject(search.getFields().get(i), regexQuery);
                queries.add(criteria);
            }
            objectMap.put("$or", queries);}
        return objectMap;
    }

}

