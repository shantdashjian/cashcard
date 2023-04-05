package example.cashcard;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.security.Principal;
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
    public ResponseEntity<CashCard> findById(@PathVariable Long id, Principal principal) {
        Optional<CashCard> cashCard =
                Optional.ofNullable(cashCardRepository.findByIdAndOwner(id, principal.getName()));
        if (cashCard.isPresent()) {
            return ResponseEntity.ok(cashCard.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<Collection<CashCard>> findAll(Pageable pageable, Principal principal) {
        Page<CashCard> page = cashCardRepository.findByOwner(
                principal.getName(),
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
    public ResponseEntity<Void> createCashCard(
            @RequestBody CashCard newCashCard,
            UriComponentsBuilder uriComponentsBuilder,
            Principal principal) {
        CashCard cashCardWithOwner = new CashCard(
                newCashCard.id(),
                newCashCard.amount(),
                principal.getName()
        );
        CashCard createdCashCard = cashCardRepository.save(cashCardWithOwner);
        URI location = uriComponentsBuilder
                .path("/cashcards/{id}")
                .buildAndExpand(createdCashCard.id())
                .toUri();
        return ResponseEntity.created(location).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateCashCard(
            @PathVariable Long id,
            @RequestBody CashCard updateCashCard,
            Principal principal) {
        CashCard cashCardWithOwner = new CashCard(
                id,
                updateCashCard.amount(),
                principal.getName()
        );
        if (cashCardRepository.existsByIdAndOwner(id, principal.getName())) {
            cashCardRepository.save(cashCardWithOwner);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCashCard(
            @PathVariable Long id,
            Principal principal
    ) {
        if (cashCardRepository.existsByIdAndOwner(id, principal.getName())) {
            cashCardRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
