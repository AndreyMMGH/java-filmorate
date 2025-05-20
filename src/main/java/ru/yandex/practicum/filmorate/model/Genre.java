package ru.yandex.practicum.filmorate.model;

import lombok.*;

@EqualsAndHashCode(of = "id")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Genre {
    private long id;
    private String name;
}
