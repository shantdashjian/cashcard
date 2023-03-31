package example.cashcard;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CashCardApplicationTests {

    @Autowired
    TestRestTemplate restTemplate;
    @Test
    public void shouldReturnACashCardWhenDataIsSaved() {
        ResponseEntity<CashCard> response =
                restTemplate.getForEntity("/cashcards/1", CashCard.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        CashCard expected = new CashCard(1L, 100.00);
        CashCard actual = response.getBody();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldNotReturnACashCardWhenIdNotFound() {
        ResponseEntity<CashCard> response =
                restTemplate.getForEntity("/cashcards/1000", CashCard.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
    }

}
