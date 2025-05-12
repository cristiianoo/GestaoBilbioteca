package modelo;

public class Membro {
    private int id;
    private String numeroSocio;
    private String primeiroNome;
    private String apelido;
    private String email;

    public Membro(int id, String numeroSocio, String primeiroNome, String apelido, String email) {
        this.id = id;
        this.numeroSocio = numeroSocio;
        this.primeiroNome = primeiroNome;
        this.apelido = apelido;
        this.email = email;
    }

    public Membro(String numeroSocio, String primeiroNome, String apelido, String email) {
        this.numeroSocio = numeroSocio;
        this.primeiroNome = primeiroNome;
        this.apelido = apelido;
        this.email = email;
    }

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

    public String getNomeCompleto() {
        return primeiroNome + " " + apelido;
    }

    @Override
    public String toString() {
        return getNomeCompleto() + " (Sócio: " + numeroSocio + ") - " + email;
    }
}
