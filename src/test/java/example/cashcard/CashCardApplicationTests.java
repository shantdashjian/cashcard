package example.cashcard;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CashCardApplicationTests {

    @Autowired
    TestRestTemplate restTemplate;
    @Test
    public void shouldReturnACashCardWhenDataIsSaved() {
        ResponseEntity<CashCard> response =
                restTemplate.getForEntity("/cashcards/99", CashCard.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        CashCard expected = new CashCard(99L, 100.00);
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

    @Test
    public void shouldCreateANewCashCard() {
        CashCard newCashCard = new CashCard(null, 250.00);
        ResponseEntity<Void> responseEntity = restTemplate.postForEntity("/cashcards", newCashCard, Void.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        URI location = responseEntity.getHeaders().getLocation();
        ResponseEntity<CashCard> responseEntityOfGet = restTemplate.getForEntity(location.getPath(), CashCard.class);
        assertThat(responseEntityOfGet.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntityOfGet.getBody().amount()).isEqualTo(newCashCard.amount());
    }

}
