package modelo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Emprestimo {
    private int id;
    private int idLivro;
    private int idMembro;
    private Date dataEmprestimo;
    private Date dataDevolucaoPrevista;
    private Date dataDevolucaoEfetiva;
    
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    public Emprestimo(int id, int idLivro, int idMembro, Date dataEmprestimo, Date dataDevolucaoPrevista) {
        this.id = id;
        this.idLivro = idLivro;
        this.idMembro = idMembro;
        this.dataEmprestimo = dataEmprestimo;
        this.dataDevolucaoPrevista = dataDevolucaoPrevista;
        this.dataDevolucaoEfetiva = null;
    }
    
    public Emprestimo(int idLivro, int idMembro, Date dataEmprestimo, Date dataDevolucaoPrevista) {
        this.idLivro = idLivro;
        this.idMembro = idMembro;
        this.dataEmprestimo = dataEmprestimo;
        this.dataDevolucaoPrevista = dataDevolucaoPrevista;
        this.dataDevolucaoEfetiva = null;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdLivro() {
        return idLivro;
    }

    public void setIdLivro(int idLivro) {
        this.idLivro = idLivro;
    }

    public int getIdMembro() {
        return idMembro;
    }

    public void setIdMembro(int idMembro) {
        this.idMembro = idMembro;
    }

    public Date getDataEmprestimo() {
        return dataEmprestimo;
    }

    public void setDataEmprestimo(Date dataEmprestimo) {
        this.dataEmprestimo = dataEmprestimo;
    }

    public Date getDataDevolucaoPrevista() {
        return dataDevolucaoPrevista;
    }

    public void setDataDevolucaoPrevista(Date dataDevolucaoPrevista) {
        this.dataDevolucaoPrevista = dataDevolucaoPrevista;
    }

    public Date getDataDevolucaoEfetiva() {
        return dataDevolucaoEfetiva;
    }

    public void setDataDevolucaoEfetiva(Date dataDevolucaoEfetiva) {
        this.dataDevolucaoEfetiva = dataDevolucaoEfetiva;
    }
    
    public boolean isAtivo() {
        return dataDevolucaoEfetiva == null;
    }
    
    public boolean isAtrasado() {
        if (!isAtivo()) { 
            return false;
        }
        Date hoje = new Date();
        try {
            Date hojeNormalizada = dateFormat.parse(dateFormat.format(hoje));
            Date devolucaoPrevistaNormalizada = dateFormat.parse(dateFormat.format(this.dataDevolucaoPrevista));
            
            return hojeNormalizada.after(devolucaoPrevistaNormalizada);
        } catch (ParseException e) {
            System.err.println("Erro ao normalizar datas para verificar atraso: " + e.getMessage());
            return hoje.after(this.dataDevolucaoPrevista);
        }
    }

    public long getDiasAtraso() {
        if (dataDevolucaoEfetiva == null || !dataDevolucaoEfetiva.after(dataDevolucaoPrevista)) {
            if (dataDevolucaoEfetiva != null) {
                 try {
                    Date efetivaNormalizada = dateFormat.parse(dateFormat.format(this.dataDevolucaoEfetiva));
                    Date previstaNormalizada = dateFormat.parse(dateFormat.format(this.dataDevolucaoPrevista));
                    if (!efetivaNormalizada.after(previstaNormalizada)) {
                        return 0; 
                    }
                } catch (ParseException e) {
                    if(!dataDevolucaoEfetiva.after(dataDevolucaoPrevista)) return 0;
                }
            } else {
                 return 0;
            }
        }

        try {
            Date efetivaNormalizada = dateFormat.parse(dateFormat.format(this.dataDevolucaoEfetiva));
            Date previstaNormalizada = dateFormat.parse(dateFormat.format(this.dataDevolucaoPrevista));

            if (!efetivaNormalizada.after(previstaNormalizada)) {
                return 0;
            }

            long diffInMillies = efetivaNormalizada.getTime() - previstaNormalizada.getTime();
            return TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);

        } catch (ParseException e) {
            System.err.println("Erro ao normalizar datas para getDiasAtraso: " + e.getMessage());
            long diffInMillies = this.dataDevolucaoEfetiva.getTime() - this.dataDevolucaoPrevista.getTime();
            return Math.max(0, TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS));
        }
    }
    
    public String getEstadoTexto() {
        if (!isAtivo()) {
            return "Devolvido";
        } else if (isAtrasado()) {
            return "Atrasado";
        } else {
            return "Ativo";
        }
    }

    public static String formatarData(Date date) {
        return date != null ? dateFormat.format(date) : "-";
    }
    
    @Override
    public String toString() {
        return "Empréstimo #" + id + " | Livro ID: " + idLivro + " | Membro ID: " + idMembro + 
               " | Emprestado em: " + formatarData(dataEmprestimo) + 
               " | Devolução Prevista: " + formatarData(dataDevolucaoPrevista) + 
               (dataDevolucaoEfetiva != null ? " | Devolvido em: " + formatarData(dataDevolucaoEfetiva) : "") +
               " | Estado: " + getEstadoTexto();
    }
}