package modelo;

/**
 * Classe que representa um livro na biblioteca.
 */
public class Livro {
    private int id;
    private String isbn;
    private String titulo;
    private String autor;
    private boolean disponivel;

    /**
     * Construtor completo para a classe Livro.
     * 
     * @param id Identificador único do livro
     * @param isbn ISBN único do livro
     * @param titulo Título do livro
     * @param autor Autor(es) do livro
     */
    public Livro(int id, String isbn, String titulo, String autor) {
        this.id = id;
        this.isbn = isbn;
        this.titulo = titulo;
        this.autor = autor;
        this.disponivel = true; // Por padrão, um livro novo está disponível
    }

    /**
     * Construtor sem ID, útil quando o ID é gerado automaticamente.
     * 
     * @param isbn ISBN único do livro
     * @param titulo Título do livro
     * @param autor Autor(es) do livro
     */
    public Livro(String isbn, String titulo, String autor) {
        this.isbn = isbn;
        this.titulo = titulo;
        this.autor = autor;
        this.disponivel = true;
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public boolean isDisponivel() {
        return disponivel;
    }

    public void setDisponivel(boolean disponivel) {
        this.disponivel = disponivel;
    }

    /**
     * Retorna o estado do livro em formato de texto.
     * 
     * @return "Disponível" ou "Emprestado"
     */
    public String getEstadoTexto() {
        return disponivel ? "Disponível" : "Emprestado";
    }

    @Override
    public String toString() {
        return titulo + " (ISBN: " + isbn + ") - " + autor + " [" + getEstadoTexto() + "]";
    }
}
