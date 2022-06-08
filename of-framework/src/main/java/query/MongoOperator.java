package query;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MongoOperator {

    public static final String GT = "$gt";
    public static final String IN = "$in";
    public static final String LT = "$lt";
    public static final String EQ = "$eq";
}
