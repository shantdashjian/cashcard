package example.cashcard;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;

import org.assertj.core.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class CashCardJsonTest {

    @Autowired
    JacksonTester<CashCard> json;

    @Autowired
    JacksonTester<CashCard[]> jsonList;

    CashCard[] cashCards;

    @BeforeEach
    void setUp() {
        cashCards = Arrays.array(
                new CashCard(99L, 100.00, "sarah1"),
                new CashCard(100L, 150.00, "sarah1"),
                new CashCard(101L, 200.00, "sarah1")
        );
    }
    @Test
    public void cashCardSerializationTest() throws IOException {
        // Arrange
        CashCard cashCard = new CashCard(99L, 100.00, "sarah1");

        // Act
        JsonContent<CashCard> jsonCashCard = json.write(cashCard);
        // Assert
        assertThat(jsonCashCard).isStrictlyEqualToJson("single.json");
        assertThat(jsonCashCard).hasJsonPathNumberValue("@.id");
        assertThat(jsonCashCard).extractingJsonPathNumberValue("@.id").isEqualTo(99);
        assertThat(jsonCashCard).hasJsonPathNumberValue("@.amount");
        assertThat(jsonCashCard).extractingJsonPathNumberValue("@.amount").isEqualTo(100.00);

    }

    @Test
    public void cashCardDeserializationTest() throws IOException {
        // Arrange
        String jsonCashCard = """
                    {
                        "id": 99,
                        "amount": 100.00,
                        "owner": "sarah1"
                    }
                """;
        // Act
        CashCard cashCard = json.parseObject(jsonCashCard);
        // Assert
        assertThat(cashCard.id()).isEqualTo(99L);
        assertThat(cashCard.amount()).isEqualTo(100);
    }

    @Test
    public void cashCardListSerializationTest() throws IOException {
        // Arrange

        // Act
        JsonContent<CashCard[]> jsonCashCards = jsonList.write(cashCards);
        // Assert
        assertThat(jsonCashCards).isStrictlyEqualToJson("list.json");
    }

    @Test
    public void cashCardListDeserializationTest() throws IOException {
        // Arrange
        String jsonCashCards = """
            [
                  {
                    "id": 99,
                    "amount": 100.00,
                    "owner": "sarah1"
                  },
                  {
                    "id": 100,
                    "amount": 150.00,
                    "owner": "sarah1"
                  },
                  {
                    "id": 101,
                    "amount": 200.00,
                    "owner": "sarah1"
                  }
              ]
        """;
        // Act
        CashCard[] actualCashCards = jsonList.parseObject(jsonCashCards);
        // Assert
        assertThat(actualCashCards).isEqualTo(cashCards);
    }

}
