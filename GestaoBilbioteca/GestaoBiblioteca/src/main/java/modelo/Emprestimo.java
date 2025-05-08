package modelo;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Classe que representa um empréstimo de livro a um membro.
 */
public class Emprestimo {
    private int id;
    private int idLivro;
    private int idMembro;
    private Date dataEmprestimo;
    private Date dataDevolucaoPrevista;
    private Date dataDevolucaoEfetiva;
    
    // Para facilitar a formatação de datas
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    /**
     * Construtor completo para a classe Emprestimo.
     * 
     * @param id Identificador único do empréstimo
     * @param idLivro ID do livro emprestado
     * @param idMembro ID do membro que pegou emprestado
     * @param dataEmprestimo Data em que o empréstimo foi realizado
     * @param dataDevolucaoPrevista Data prevista para devolução
     */
    public Emprestimo(int id, int idLivro, int idMembro, Date dataEmprestimo, Date dataDevolucaoPrevista) {
        this.id = id;
        this.idLivro = idLivro;
        this.idMembro = idMembro;
        this.dataEmprestimo = dataEmprestimo;
        this.dataDevolucaoPrevista = dataDevolucaoPrevista;
        this.dataDevolucaoEfetiva = null; // Inicialmente, o livro não foi devolvido
    }

    /**
     * Construtor sem ID, útil quando o ID é gerado automaticamente.
     * 
     * @param idLivro ID do livro emprestado
     * @param idMembro ID do membro que pegou emprestado
     * @param dataEmprestimo Data em que o empréstimo foi realizado
     * @param dataDevolucaoPrevista Data prevista para devolução
     */
    public Emprestimo(int idLivro, int idMembro, Date dataEmprestimo, Date dataDevolucaoPrevista) {
        this.idLivro = idLivro;
        this.idMembro = idMembro;
        this.dataEmprestimo = dataEmprestimo;
        this.dataDevolucaoPrevista = dataDevolucaoPrevista;
        this.dataDevolucaoEfetiva = null;
    }

    // Getters e Setters
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
    
    /**
     * Verifica se o empréstimo está ativo (ainda não devolvido).
     * 
     * @return true se o empréstimo estiver ativo, false caso contrário
     */
    public boolean isAtivo() {
        return dataDevolucaoEfetiva == null;
    }
    
    /**
     * Verifica se o empréstimo está atrasado.
     * 
     * @return true se o empréstimo estiver atrasado, false caso contrário
     */
    public boolean isAtrasado() {
        if (dataDevolucaoEfetiva != null) {
            return dataDevolucaoEfetiva.after(dataDevolucaoPrevista);
        } else {
            Date hoje = new Date();
            return hoje.after(dataDevolucaoPrevista);
        }
    }
    
    /**
     * Obtém o estado do empréstimo como texto.
     * 
     * @return "Ativo", "Atrasado" ou "Devolvido"
     */
    public String getEstadoTexto() {
        if (dataDevolucaoEfetiva != null) {
            return "Devolvido";
        } else if (isAtrasado()) {
            return "Atrasado";
        } else {
            return "Ativo";
        }
    }
    
    /**
     * Formata uma data para exibição.
     * 
     * @param date Data a ser formatada
     * @return String formatada da data ou "-" se a data for nula
     */
    public static String formatarData(Date date) {
        return date != null ? dateFormat.format(date) : "-";
    }
    
    @Override
    public String toString() {
        return "Empréstimo #" + id + " | Livro ID: " + idLivro + " | Membro ID: " + idMembro + 
               " | Emprestado em: " + formatarData(dataEmprestimo) + 
               " | Devolução Prevista: " + formatarData(dataDevolucaoPrevista) + 
               " | Estado: " + getEstadoTexto();
    }
}
