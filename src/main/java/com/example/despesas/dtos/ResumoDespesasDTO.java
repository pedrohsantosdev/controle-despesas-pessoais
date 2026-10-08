package com.example.despesas.dtos;

public class ResumoDespesasDTO {

    private Double total;
    private Double totalPago;
    private Double totalPendente;

    public ResumoDespesasDTO() {
    }

    public ResumoDespesasDTO(Double total, Double totalPago, Double totalPendente) {
        this.total = total;
        this.totalPago = totalPago;
        this.totalPendente = totalPendente;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public Double getTotalPago() {
        return totalPago;
    }

    public void setTotalPago(Double totalPago) {
        this.totalPago = totalPago;
    }

    public Double getTotalPendente() {
        return totalPendente;
    }

    public void setTotalPendente(Double totalPendente) {
        this.totalPendente = totalPendente;
    }
}
