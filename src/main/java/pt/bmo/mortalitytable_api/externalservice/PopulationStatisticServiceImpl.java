package pt.bmo.mortalitytable_api.externalservice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import pt.bmo.mortalitytable_api.externalservice.mapper.PopulationStatisticsMapper;
import pt.bmo.mortalitytable_api.externalservice.model.PopulationStatisticDto;
import pt.bmo.mortalitytable_api.externalservice.exception.ExternalSystemException;
import pt.bmo.mortalitytable_api.externalservice.model.PopulationStatistics;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Locale;

@Slf4j
@Service
@RequiredArgsConstructor
public class PopulationStatisticServiceImpl implements PopulationStatisticService {

    private final WebClient webClient;
    private static final Long DEFAULT_TIME_OUT = 50000L;

    @Value("${ext.sys.scheme}")
    private String scheme;

    @Value("${ext.sys.host}")
    private String host;

    @Value("${ext.sys.port}")
    private String port;

    @Value("${ext.sys.path}")
    private String path;

    @Override
    public PopulationStatisticDto findByCountryAndYear(String countryCode, int year) {

        Locale locale = new Locale("en", countryCode);

        List<PopulationStatistics> statistics = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .scheme(scheme)
                        .host(host)
                        .port(port)
                        .path(path)
                        .build(year, locale.getDisplayCountry(Locale.US)))
                .retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse -> {
//                    log.error("Error to find country statistics");
                    return clientResponse.bodyToMono(String.class)
                            .flatMap(s -> Mono.error(new ExternalSystemException(s)));
                })
                .toEntityList(PopulationStatistics.class)
                .timeout(Duration.ofMillis(DEFAULT_TIME_OUT))
                .map(ResponseEntity::getBody)
                .switchIfEmpty(Mono.empty())
                .block();

        return PopulationStatisticsMapper.INSTANCE.toDto(countryCode, year, statistics);
    }
}
