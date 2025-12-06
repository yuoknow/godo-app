package godo.backend.masterclass;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("master_classes")
@NoArgsConstructor
@Getter
@Setter
public class MasterClass {
    @Id
    private Long id;

    private String title;

    private String description;

    private String instructor;

    private Integer durationMinutes;

    private BigDecimal price;

    private LocalDateTime createdAt;

    public MasterClass(String title, String description, String instructor, Integer durationMinutes, BigDecimal price) {
        this.title = title;
        this.description = description;
        this.instructor = instructor;
        this.durationMinutes = durationMinutes;
        this.price = price;
    }
}
