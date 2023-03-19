package example.cashcard;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatStream;

@JsonTest
public class CashCardJsonTest {

    @Autowired
    JacksonTester<CashCard> json;

    @Test
    public void cashCardSerializationTest() throws IOException {
        // Arrange
        CashCard cashCard = new CashCard(1L, 100.00);

        // Act
        JsonContent<CashCard> jsonCashCard = json.write(cashCard);
        // Assert
        assertThat(jsonCashCard).isStrictlyEqualToJson("expected.json");
        assertThat(jsonCashCard).hasJsonPathNumberValue("@.id");
        assertThat(jsonCashCard).extractingJsonPathNumberValue("@.id").isEqualTo(1);
        assertThat(jsonCashCard).hasJsonPathNumberValue("@.amount");
        assertThat(jsonCashCard).extractingJsonPathNumberValue("@.amount").isEqualTo(100.00);

    }

    @Test
    public void cashCardDeserializationTest() throws IOException {
        // Arrange
        String jsonCashCard = """
                    {
                        "id": 1,
                        "amount": 100.00
                    }
                """;
        // Act
        CashCard cashCard = json.parseObject(jsonCashCard);
        // Assert
        assertThat(cashCard.id()).isEqualTo(1L);
        assertThat(cashCard.amount()).isEqualTo(100);
    }
}
