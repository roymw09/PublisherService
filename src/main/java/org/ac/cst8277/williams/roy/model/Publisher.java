package org.ac.cst8277.williams.roy.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Table;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("publisher")
public class Publisher {

    private String id;
    private Integer user_id;
}
