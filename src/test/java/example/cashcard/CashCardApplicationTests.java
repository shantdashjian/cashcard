package example.cashcard;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CashCardApplicationTests {

    @Autowired
    TestRestTemplate restTemplate;
    @Test
    public void shouldReturnACashCardWhenDataIsSaved() {
        ResponseEntity<CashCard> response =
                restTemplate
                        .withBasicAuth("sarah1", "abc123")
                        .getForEntity("/cashcards/99", CashCard.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        CashCard expected = new CashCard(99L, 100.00, "sarah1");
        CashCard actual = response.getBody();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldNotReturnACashCardWhenIdNotFound() {
        ResponseEntity<CashCard> response =
                restTemplate
                        .withBasicAuth("sarah1", "abc123")
                        .getForEntity("/cashcards/1000", CashCard.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
    }

    @Test
    @DirtiesContext
    public void shouldCreateANewCashCard() {
        CashCard newCashCard = new CashCard(null, 250.00, null);
        ResponseEntity<Void> responseEntity =
                restTemplate
                        .withBasicAuth("sarah1", "abc123")
                        .postForEntity("/cashcards", newCashCard, Void.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        URI location = responseEntity.getHeaders().getLocation();
        ResponseEntity<CashCard> responseEntityOfGet =
                restTemplate
                        .withBasicAuth("sarah1", "abc123")
                        .getForEntity(location.getPath(), CashCard.class);
        assertThat(responseEntityOfGet.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntityOfGet.getBody().amount()).isEqualTo(newCashCard.amount());
    }

    @Test
    public void shouldReturnAListOfCashCardsIfDataIsPresent() {
        // arrange
        // act
        ResponseEntity<String> response =
                restTemplate
                        .withBasicAuth("sarah1", "abc123")
                        .getForEntity("/cashcards", String.class);
        DocumentContext body = JsonPath.parse(response.getBody());
        int count = body.read("$.length()");
        JSONArray ids = body.read("$..id");
        JSONArray amounts = body.read("$..amount");
        // assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(count).isEqualTo(3);
        assertThat(ids).containsExactlyInAnyOrder(99, 100, 101);
        assertThat(amounts).containsExactlyInAnyOrder(100.00, 150.00, 200.00);
    }

    @Test
    public void shouldReturnAPageOfCashCards() {
        // arrange
        // act
        ResponseEntity<String> response =
                restTemplate
                        .withBasicAuth("sarah1", "abc123")
                        .getForEntity("/cashcards?page=1&size=1", String.class);
        DocumentContext body = JsonPath.parse(response.getBody());
        JSONArray page = body.read("$[*]");
        // assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(page.size()).isEqualTo(1);
    }

    @Test
    public void shouldReturnASortedPageOfCashCards() {
        // arrange
        // act
        ResponseEntity<String> response =
                restTemplate
                        .withBasicAuth("sarah1", "abc123")
                        .getForEntity("/cashcards?page=0&size=3&sort=amount,desc",
                String.class);
        DocumentContext body = JsonPath.parse(response.getBody());
        JSONArray page = body.read("$[*]");
        // assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(page.size()).isEqualTo(3);
        double amount = body.read("$[0].amount");
        assertThat(amount).isEqualTo(200.00);
    }

    @Test
    public void shouldReturnASortedPageOfCashCardsWithDefaults() {
        // arrange
        // act
        ResponseEntity<String> response =
                restTemplate
                        .withBasicAuth("sarah1", "abc123")
                        .getForEntity("/cashcards?page=0&size=3",
                String.class);
        DocumentContext body = JsonPath.parse(response.getBody());
        JSONArray page = body.read("$[*]");
        // assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(page.size()).isEqualTo(3);
        double amount = body.read("$[0].amount");
        assertThat(amount).isEqualTo(200.00);
    }

    @Test
    public void shouldNotReturnACashCardWhenUserIsNonOwner() {
        ResponseEntity<CashCard> response =
                restTemplate
                        .withBasicAuth("hank_has_no_cards", "abc123")
                        .getForEntity("/cashcards/99", CashCard.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void shouldNotAllowAccessToCashCardsTheyDoNotOwn() {
        ResponseEntity<String> response = restTemplate
                .withBasicAuth("sarah1", "abc123")
                .getForEntity("/cashcards/102", String.class); // kumar2's data
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DirtiesContext
    public void shouldUpdateExistingCashCardWhenItExistsAndUserOwnsIt() {
        CashCard cashCardUpdate = new CashCard(null, 1000.00, null);
        HttpEntity<CashCard> httpEntity = new HttpEntity<>(cashCardUpdate);
        ResponseEntity<Void> responseEntity =
                restTemplate
                        .withBasicAuth("sarah1", "abc123")
                        .exchange("/cashcards/99", HttpMethod.PUT, httpEntity, Void.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        ResponseEntity<CashCard> responseEntityOfGet =
                restTemplate
                        .withBasicAuth("sarah1", "abc123")
                        .getForEntity("/cashcards/99", CashCard.class);
        assertThat(responseEntityOfGet.getBody().amount()).isEqualTo(cashCardUpdate.amount());
    }

    @Test
    @DirtiesContext
    public void shouldNotUpdateNonExistingCashCard() {
        CashCard cashCardUpdate = new CashCard(null, 1000.00, null);
        HttpEntity<CashCard> httpEntity = new HttpEntity<>(cashCardUpdate);
        ResponseEntity<Void> responseEntity =
                restTemplate
                        .withBasicAuth("sarah1", "abc123")
                        .exchange("/cashcards/999", HttpMethod.PUT, httpEntity, Void.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DirtiesContext
    public void shouldDeleteAnExistingCashCard() {
        HttpEntity<CashCard> httpEntity = new HttpEntity<>(null);
        ResponseEntity<Void> responseEntity =
                restTemplate
                        .withBasicAuth("sarah1", "abc123")
                        .exchange("/cashcards/99", HttpMethod.DELETE, httpEntity,  Void.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        ResponseEntity<CashCard> responseEntityOfGet =
                restTemplate
                        .withBasicAuth("sarah1", "abc123")
                        .getForEntity("/cashcards/99", CashCard.class);
        assertThat(responseEntityOfGet.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DirtiesContext
    public void shouldNotDeleteAnNonExistingCashCard() {
        HttpEntity<CashCard> httpEntity = new HttpEntity<>(null);
        ResponseEntity<Void> responseEntity =
                restTemplate
                        .withBasicAuth("sarah1", "abc123")
                        .exchange("/cashcards/999", HttpMethod.DELETE, httpEntity,  Void.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
