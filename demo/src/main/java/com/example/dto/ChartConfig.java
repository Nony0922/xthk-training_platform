package com.example.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChartConfig {
    private String type;
    private String title;
    @JsonProperty("xAxis")
    @JsonAlias({"xaxis", "x_axis", "labels"})
    private List<String> xAxis;
    private List<ChartSeries> series;
}
