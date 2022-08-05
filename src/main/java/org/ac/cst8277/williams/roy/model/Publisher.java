package org.ac.cst8277.williams.roy.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.relational.core.mapping.Table;

import javax.persistence.Id;

@Data
@AllArgsConstructor
@Table("publisher")
public class Publisher {

    @Id
    private Integer id;
    private Integer user_id;
}
