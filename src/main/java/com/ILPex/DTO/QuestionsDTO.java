package com.ILPex.DTO;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuestionsDTO {
    private String question;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
}
