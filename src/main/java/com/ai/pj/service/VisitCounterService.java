package com.ai.pj.service;

import com.ai.pj.domain.VisitCount;
import com.ai.pj.security.repository.VisitCountRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.search.MeterNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VisitCounterService {

    private final VisitCountRepository visitCountRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final MeterRegistry meterRegistry;
    private final ObjectMapper objectMapper;


    // 방문자수 조회
    public Long getVisitCount(String id) {
        Optional<VisitCount> visitCount = visitCountRepository.findById(id);
        return visitCount.map(VisitCount::getCount).orElse(0L);
    }

    public void incrementVisitCount(String id, long sum) {
        String redisKey = "visitCount:" + id;
        String hour = id.substring(id.length() - 2, id.length());
        System.out.println("REDISKEY " + redisKey);
        VisitCount visitCount = visitCountRepository.findById(redisKey)
                .orElse(new VisitCount(id, hour,0L));


        visitCount.setCount(sum);
        try {
            String json = objectMapper.writeValueAsString(visitCount);
            redisTemplate.opsForValue().set(redisKey, json);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public void resetVisitCount(String id) {
        visitCountRepository.deleteById(id);
    }

    public List<VisitCount> getPastVisited(String date) {
        String dateProcessed = date.substring(0, date.length() - 3);
        System.out.println("getPastVisited");
        return visitCountRepository.findBid(dateProcessed);
    }


    private static final List<String> viewEndpoints = List.of(
            "/board/"
    );

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH");


    public List<VisitCount> visitCount() {
        long sum;

        try {
            sum = meterRegistry.get("http.server.requests")
                    .timers()
                    .stream()
                    .filter(timer -> viewEndpoints.contains(timer.getId().getTag("uri")))
                    .mapToLong(Timer::count)
                    .sum();
        } catch (MeterNotFoundException e) {
            sum = 0L ;
        }

        String currentHour = LocalDateTime.now().format(TIME_FORMATTER);

        incrementVisitCount(currentHour, sum);
        List<VisitCount> visitCountslist = getPastVisited(currentHour);

        System.out.println(visitCountslist.toString());

        return visitCountslist;
    }

}
