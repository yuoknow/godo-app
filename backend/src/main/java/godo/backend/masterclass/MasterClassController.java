package godo.backend.masterclass;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/master-classes")
@RequiredArgsConstructor
public class MasterClassController {

    private final MasterClassRepository repository;

    @GetMapping
    public List<MasterClass> getAllMasterClasses() {
        return (List<MasterClass>) repository.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MasterClass createMasterClass(@RequestBody MasterClass masterClass) {
        masterClass.setCreatedAt(LocalDateTime.now());
        return repository.save(masterClass);
    }
}
