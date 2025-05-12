package modelo;

public class Livro {
    private int id;
    private String isbn;
    private String titulo;
    private String autor;
    private boolean disponivel;

    public Livro(int id, String isbn, String titulo, String autor) {
        this.id = id;
        this.isbn = isbn;
        this.titulo = titulo;
        this.autor = autor;
        this.disponivel = true;
    }

    public Livro(String isbn, String titulo, String autor) {
        this.isbn = isbn;
        this.titulo = titulo;
        this.autor = autor;
        this.disponivel = true;
    }

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

    public String getEstadoTexto() {
        return disponivel ? "Disponível" : "Emprestado";
    }

    @Override
    public String toString() {
        return titulo + " (ISBN: " + isbn + ") - " + autor + " [" + getEstadoTexto() + "]";
    }
}
