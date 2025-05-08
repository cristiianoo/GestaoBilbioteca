package modelo;

/**
 * Classe que representa um membro da biblioteca.
 */
public class Membro {
    private int id;
    private String numeroSocio;
    private String primeiroNome;
    private String apelido;
    private String email;

    /**
     * Construtor completo para a classe Membro.
     * 
     * @param id Identificador único do membro
     * @param numeroSocio Número de sócio único
     * @param primeiroNome Primeiro nome do membro
     * @param apelido Apelido do membro
     * @param email Email de contato do membro
     */
    public Membro(int id, String numeroSocio, String primeiroNome, String apelido, String email) {
        this.id = id;
        this.numeroSocio = numeroSocio;
        this.primeiroNome = primeiroNome;
        this.apelido = apelido;
        this.email = email;
    }

    /**
     * Construtor sem ID, útil quando o ID é gerado automaticamente.
     * 
     * @param numeroSocio Número de sócio único
     * @param primeiroNome Primeiro nome do membro
     * @param apelido Apelido do membro
     * @param email Email de contato do membro
     */
    public Membro(String numeroSocio, String primeiroNome, String apelido, String email) {
        this.numeroSocio = numeroSocio;
        this.primeiroNome = primeiroNome;
        this.apelido = apelido;
        this.email = email;
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNumeroSocio() {
        return numeroSocio;
    }

    public void setNumeroSocio(String numeroSocio) {
        this.numeroSocio = numeroSocio;
    }

    public String getPrimeiroNome() {
        return primeiroNome;
    }

    public void setPrimeiroNome(String primeiroNome) {
        this.primeiroNome = primeiroNome;
    }

    public String getApelido() {
        return apelido;
    }

    public void setApelido(String apelido) {
        this.apelido = apelido;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Retorna o nome completo do membro.
     * 
     * @return Nome completo (primeiro nome + apelido)
     */
    public String getNomeCompleto() {
        return primeiroNome + " " + apelido;
    }

    @Override
    public String toString() {
        return getNomeCompleto() + " (Sócio: " + numeroSocio + ") - " + email;
    }
}
