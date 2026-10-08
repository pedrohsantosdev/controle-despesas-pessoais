package com.example.despesas.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record DespesaRequestDTO(

        @NotBlank(message = "Descrição é obrigatória")
        String descricao,

        @NotNull(message = "O valor da conta é obrigatório")
        @Positive(message = "O valor deve ser maior que zero")
        Double valor,

        @NotNull(message = "A data de vencimento é obrigatória")
        LocalDate dataVencimento,

        boolean paga,

        Long categoriaId
) {
}
