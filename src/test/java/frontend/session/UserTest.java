package frontend.session;

import backend.data.requests.Attribute;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.util.ArrayList;

/**
 * Created by dshelygin on 25.04.2018.
 */
public class UserTest {
    final static Logger logger = Logger.getLogger(UserTest.class);

    @Test
    public void test() {
        try {
  /*          String json = "{\"classKey\":\"clKey1\", \"classCode\":\"clCode1\", \"oparationCode\":\"oparationCode1\", " +
                    "\"data\": [{ \"value\":\"value1\", \"type\":\"type1\", \"ifVersion\":\"true\" } ," +
                    "{ \"value\":\"value2\", \"type\":\"type2\", \"ifVersion\":\"true\" } ] }";

            String jattributes =   "[{ \"value\":\"value1\", \"type\":\"type1\", \"ifVersion\":\"true\" } ," +
                    "{ \"value\":\"value2\", \"type\":\"type2\", \"ifVersion\":\"true\" } ]";
            //RegisterClassRequest request = objectMapper.readValue(json,RegisterClassRequest.class);


            JsonNode jsonNode = objectMapper.readTree(json);
            logger.info(jsonNode.get("data").asText());

            //String attributes = jsonNode.get("data");
            ArrayList<Attribute> attributes1 = objectMapper.readValue(jattributes, new TypeReference<ArrayList<Attribute>>(){});
*/


            int tmp=1;

        } catch (Exception e) {
            logger.error(e);
        }


    }



}