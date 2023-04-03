package example.cashcard;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Collection;
import java.util.Optional;

@RestController
@RequestMapping("/cashcards")
public class CashCardController {

    private final CashCardRepository cashCardRepository;

    public CashCardController(CashCardRepository cashCardRepository) {
        this.cashCardRepository = cashCardRepository;
    }

    @GetMapping("/{id}")
    public ResponseEntity<CashCard> findById(@PathVariable Long id) {
        Optional<CashCard> cashCard = cashCardRepository.findById(id);
        if (cashCard.isPresent()) {
            return ResponseEntity.ok(cashCard.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<Collection<CashCard>> findAll(Pageable pageable) {
        Page<CashCard> page = cashCardRepository.findAll(
                PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        pageable.getSortOr(
                                Sort.by(Sort.Direction.DESC, "amount")
                        )
                )
        );

        return ResponseEntity.ok(page.toList());
    }

    @PostMapping
    public ResponseEntity<Void> createCashCard(@RequestBody CashCard newCashCard, UriComponentsBuilder uriComponentsBuilder) {
        CashCard createdCashCard = cashCardRepository.save(newCashCard);
        URI location =uriComponentsBuilder
                .path("/cashcards/{id}")
                .buildAndExpand(createdCashCard.id())
                .toUri();
        return ResponseEntity.created(location).build();
    }
}
