package com.example.ledger.application.query;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PageQuery {
    @Min(0)
    Integer skip = 0;
    @Min(0) @Max(50)
    Integer limit = 10;
}
