package pt.bmo.mortalitytable_api.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import pt.bmo.mortalitytable_api.controllers.dto.ErrorMsg;
import pt.bmo.mortalitytable_api.controllers.dto.MortalityDto;
import pt.bmo.mortalitytable_api.domain.mapper.MortalityMapper;
import pt.bmo.mortalitytable_api.services.MortalityService;
import pt.bmo.mortalitytable_api.utils.CsvHelper;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

import static pt.bmo.mortalitytable_api.controllers.MortalityController.BASE_URL;

@RestController
@RequiredArgsConstructor
@RequestMapping(BASE_URL)
public class MortalityController {

    public static final String BASE_URL = "/api/v1/mortalities";

    private final MortalityService mortalityService;

    @GetMapping
    ResponseEntity getAll(Pageable pageable) {
        return ResponseEntity.ok(mortalityService.getAll(pageable)
                .map(MortalityMapper.INSTANCE::toDto));
    }

    @GetMapping("/{year}")
    ResponseEntity getByYear(@PathVariable("year") int year, Pageable pageable) {
        return ResponseEntity.ok(mortalityService.getByYear(pageable, year)
                .map(MortalityMapper.INSTANCE::toDto));
    }

    @PostMapping
    ResponseEntity create(@Valid @RequestBody MortalityDto mortality) {
        Optional<MortalityDto> mortalityCreatedOpt = mortalityService.create(MortalityMapper.INSTANCE.toModel(mortality))
                .map(MortalityMapper.INSTANCE::toDto);

        return mortalityCreatedOpt
                .map(mortalityCreated -> ResponseEntity.created(URI.create(BASE_URL + "/" + mortalityCreated.id())).build())
                .orElseThrow(() -> new RuntimeException("Error to create a mortality entry"));
    }

    @PutMapping("/{id}")
    ResponseEntity update(@PathVariable("id") UUID id,
                          @Valid @RequestBody MortalityDto mortality) {
        Optional<MortalityDto> mortalityUpdatedOpt = mortalityService.update(id, MortalityMapper.INSTANCE.toModel(mortality))
                .map(MortalityMapper.INSTANCE::toDto);

        return mortalityUpdatedOpt.map(ResponseEntity::ok)
                .orElseThrow(() -> new RuntimeException("Error to update a mortality entry"));
    }

    @PostMapping("/batch")
    ResponseEntity processBatch(@RequestParam("file") MultipartFile file) {
        if (!CsvHelper.isCSVFormat(file)) {
            ErrorMsg errorMsg = ErrorMsg.builder()
                    .timestamp(System.currentTimeMillis())
                    .msg("File format is not a CSV")
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMsg);
        }
        mortalityService.saveRecords(file);
        return ResponseEntity.ok().build();
    }
}
