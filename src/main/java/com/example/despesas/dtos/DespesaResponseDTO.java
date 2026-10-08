package com.example.despesas.dtos;
import com.example.despesas.entities.Despesa;

import java.time.LocalDate;

public record DespesaResponseDTO(
        Long id,
        String descricao,
        Double valor,
        LocalDate dataVencimento,
        boolean paga
) {

    public DespesaResponseDTO(Despesa despesa) {
        this(despesa.getId(), despesa.getDescricao(), despesa.getValor(),
                despesa.getDataVencimento(), despesa.isPaga());
    }
}
