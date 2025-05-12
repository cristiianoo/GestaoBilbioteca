package gui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.swing.JOptionPane;
import modelo.Emprestimo; 
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PaginaInicial extends javax.swing.JFrame {

    private static final String ARQUIVO_LIVROS = "livros.csv";
    private static final String ARQUIVO_MEMBROS = "membros.csv";
    private static final String ARQUIVO_EMPRESTIMOS = "emprestimos.csv";
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    private Map<Integer, String> livrosMap = new HashMap<>();
    private Map<Integer, String> membrosMap = new HashMap<>();
    private List<Emprestimo> todosEmprestimos = new ArrayList<>();

    public PaginaInicial() {
        initComponents();
        setLocationRelativeTo(null);
        setTitle("Biblioteca - Página Inicial");

        atualizarInformacaoVisivel(); 
        configurarListeners();
    }

    private void configurarListeners() {
        if (btnAtualizar != null) {
             btnAtualizar.addActionListener(e -> atualizarInformacaoVisivel());
        } else {
             System.err.println("PaginaInicial ERRO: btnAtualizar não foi inicializado!");
        }

        jMenuItem1.addActionListener(this::abrirGestaoMembros);
        jMenuItem2.addActionListener(this::abrirGestaoEmprestimos);
        jMenuItem3.addActionListener(this::abrirGestaoLivros);
    }

    private void atualizarInformacaoVisivel() {
         if (!verificarArquivosCSVExistem()) {
            jTextArea1.setText("Erro crítico: Arquivos de dados não encontrados.\nVerifique a localização dos arquivos CSV.");
            jTextArea2.setText("Funcionalidades limitadas.");
            livrosMap.clear();
            membrosMap.clear();
            todosEmprestimos.clear();
        } else {
            carregarDadosIniciais();
            popularEmprestimosRecentes();
            popularDevolucoesProximas();
        }
    }

    private boolean verificarArquivo(String nomeArquivo) {
        File f = new File(nomeArquivo);
        boolean existe = f.exists() && !f.isDirectory();
        if (!existe) {
            System.err.println("PaginaInicial ERRO: Arquivo '" + nomeArquivo + "' não encontrado em: " + f.getAbsolutePath());
        }
        return existe;
    }

    private boolean verificarArquivosCSVExistem() {
        boolean arquivosOk = verificarArquivo(ARQUIVO_LIVROS) &&
                             verificarArquivo(ARQUIVO_MEMBROS) &&
                             verificarArquivo(ARQUIVO_EMPRESTIMOS);
        if (!arquivosOk) {
             JOptionPane.showMessageDialog(this,
                "Um ou mais arquivos CSV não foram encontrados na raiz do projeto.\nA aplicação pode não funcionar corretamente.",
                "Erro de Arquivo(s)", JOptionPane.ERROR_MESSAGE);
        }
        return arquivosOk;
    }

    private void carregarDadosIniciais() {
        livrosMap.clear();
        membrosMap.clear();
        todosEmprestimos.clear();
        carregarLivros();
        carregarMembros();
        carregarEmprestimos();
    }

    private void carregarLivros() {
        File arquivo = new File(ARQUIVO_LIVROS);
        if (!arquivo.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(arquivo))) {
            String linha = br.readLine();
            if (linha == null) { System.err.println("PaginaInicial AVISO: Arquivo de livros vazio."); return; }

            while ((linha = br.readLine()) != null) {
                if (linha.trim().isEmpty()) continue;
                String[] partes = linha.split(",");
                if (partes.length >= 2) {
                    try {
                        int id = Integer.parseInt(partes[0].trim());
                        String titulo = partes[1].trim();
                        livrosMap.put(id, titulo);
                    } catch (NumberFormatException e) {
                        System.err.println("PaginaInicial ERRO Livros: Não foi possível converter ID '" + partes[0] + "' para número. Linha: " + linha);
                    }
                } else {
                     System.err.println("PaginaInicial AVISO Livros: Linha ignorada (colunas < 2): '" + linha + "'");
                }
            }
        } catch (IOException e) {
             mostrarErroFatal("Erro de IO ao ler " + ARQUIVO_LIVROS + ": " + e.getMessage(), "Erro de Leitura");
        }
    }
    
     private void carregarMembros() {
        File arquivo = new File(ARQUIVO_MEMBROS);
        if (!arquivo.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(arquivo))) {
            String linha = br.readLine();
             if (linha == null) { System.err.println("PaginaInicial AVISO: Arquivo de membros vazio."); return; }

            while ((linha = br.readLine()) != null) {
                 if (linha.trim().isEmpty()) continue;
                 String[] partes = linha.split(",");
                 if (partes.length >= 2) {
                     try {
                        int id = Integer.parseInt(partes[0].trim());
                        String nome = partes[1].trim();
                        membrosMap.put(id, nome);
                    } catch (NumberFormatException e) {
                         System.err.println("PaginaInicial ERRO Membros: Não foi possível converter ID '" + partes[0] + "' para número. Linha: " + linha);
                    }
                 } else {
                     System.err.println("PaginaInicial AVISO Membros: Linha ignorada (colunas < 2): '" + linha + "'");
                 }
            }
        } catch (IOException e) {
             mostrarErroFatal("Erro de IO ao ler " + ARQUIVO_MEMBROS + ": " + e.getMessage(), "Erro de Leitura");
        }
    }

    private void carregarEmprestimos() {
        File arquivo = new File(ARQUIVO_EMPRESTIMOS);
        if (!arquivo.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(arquivo))) {
            String linha = br.readLine(); // Cabeçalho
            if (linha == null) { System.err.println("PaginaInicial AVISO: Arquivo de empréstimos vazio."); return; }

            int linhaNum = 1;
            while ((linha = br.readLine()) != null) {
                linhaNum++;
                 if (linha.trim().isEmpty()) continue;
                 String[] partes = linha.split(",");
                 if (partes.length >= 5) {
                    try {
                        int id = Integer.parseInt(partes[0].trim());
                        int idLivro = Integer.parseInt(partes[1].trim());
                        int idMembro = Integer.parseInt(partes[2].trim());
                        Date dataEmprestimo = dateFormat.parse(partes[3].trim());
                        Date dataDevolucaoPrevista = dateFormat.parse(partes[4].trim());

                        Emprestimo emp = new Emprestimo(id, idLivro, idMembro, dataEmprestimo, dataDevolucaoPrevista);

                        if (partes.length >= 6 && partes[5] != null && !partes[5].trim().isEmpty() && !partes[5].trim().equals("-")) {
                            try {
                                Date dataDevolucaoEfetiva = dateFormat.parse(partes[5].trim());
                                emp.setDataDevolucaoEfetiva(dataDevolucaoEfetiva);
                            } catch (ParseException pe) {
                                 System.err.println("PaginaInicial AVISO Empréstimos: Linha " + linhaNum + ": Data de devolução efetiva inválida ('" + partes[5] + "').");
                            }
                        }
                        todosEmprestimos.add(emp);

                    } catch (NumberFormatException e) {
                        System.err.println("PaginaInicial ERRO Empréstimos: Linha " + linhaNum + ": Erro ao converter ID (Empréstimo/Livro/Membro).");
                    } catch (ParseException e) {
                         System.err.println("PaginaInicial ERRO Empréstimos: Linha " + linhaNum + ": Erro ao converter Data (Empréstimo/Prevista).");
                    }
                 } else {
                     System.err.println("PaginaInicial AVISO Empréstimos: Linha " + linhaNum + " ignorada (colunas < 5).");
                 }
            }
        } catch (IOException e) {
             mostrarErroFatal("Erro de IO ao ler " + ARQUIVO_EMPRESTIMOS + ": " + e.getMessage(), "Erro de Leitura");
        }
    }

     private void popularEmprestimosRecentes() {
        jTextArea1.setText("");
        if (todosEmprestimos.isEmpty()) {
            jTextArea1.setText("Nenhum empréstimo registrado.");
            return;
        }

        List<Emprestimo> emprestimosOrdenados = new ArrayList<>(todosEmprestimos);
        emprestimosOrdenados.sort(Comparator.comparing(Emprestimo::getDataEmprestimo).reversed());

        StringBuilder sb = new StringBuilder();
        int count = 0;
        for (Emprestimo emp : emprestimosOrdenados) {
            if (count >= 5) break;

            // Usa getOrDefault para buscar o nome/título, com fallback para ID
            String nomeLivro = livrosMap.getOrDefault(emp.getIdLivro(), "Livro ID " + emp.getIdLivro());
            String nomeMembro = membrosMap.getOrDefault(emp.getIdMembro(), "Membro ID " + emp.getIdMembro());
            sb.append(Emprestimo.formatarData(emp.getDataEmprestimo()))
              .append(" - ")
              .append(nomeLivro)
              .append(" para ")
              .append(nomeMembro)
              .append("\n");
            count++;
        }
        jTextArea1.setText(sb.length() > 0 ? sb.toString() : "Nenhum empréstimo recente encontrado.");
    }

    private void popularDevolucoesProximas() {
        jTextArea2.setText("");
        if (todosEmprestimos.isEmpty()) {
            jTextArea2.setText("Nenhum empréstimo ativo.");
            return;
        }

        Date hoje = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(hoje);
        try { 
            hoje = dateFormat.parse(dateFormat.format(hoje));
        } catch (ParseException e) {  }

        cal.setTime(hoje);
        cal.add(Calendar.DAY_OF_MONTH, 7);
        Date dataLimite = cal.getTime();
        final Date hojeFinal = hoje;

        List<Emprestimo> devolucoesProximas = todosEmprestimos.stream()
                .filter(Emprestimo::isAtivo)
                .filter(emp -> {
                    try {
                        Date devPrevistaNormalizada = dateFormat.parse(dateFormat.format(emp.getDataDevolucaoPrevista()));
                        return !devPrevistaNormalizada.before(hojeFinal) && !devPrevistaNormalizada.after(dataLimite);
                    } catch (ParseException e) { return false; }
                })
                .sorted(Comparator.comparing(Emprestimo::getDataDevolucaoPrevista))
                .collect(Collectors.toList());

        StringBuilder sb = new StringBuilder();
        int count = 0;
        for (Emprestimo emp : devolucoesProximas) {
            if (count >= 5) break;

            String nomeLivro = livrosMap.getOrDefault(emp.getIdLivro(), "Livro ID " + emp.getIdLivro());
            String nomeMembro = membrosMap.getOrDefault(emp.getIdMembro(), "Membro ID " + emp.getIdMembro());
            sb.append(Emprestimo.formatarData(emp.getDataDevolucaoPrevista()))
              .append(" - ")
              .append(nomeLivro)
              .append(" por ")
              .append(nomeMembro)
              .append(emp.isAtrasado() ? " (ATRASADO!)" : "")
              .append("\n");
            count++;
        }
        jTextArea2.setText(sb.length() > 0 ? sb.toString() : "Nenhuma devolução nos próximos 7 dias.");
    }

    private void abrirGestaoMembros(ActionEvent evt) {
         try {
            GestaoMembros gestaoMembros = new GestaoMembros(); // Assume que existe
            gestaoMembros.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
            gestaoMembros.setVisible(true);
         } catch (NoClassDefFoundError e) {
             mostrarErroClasseNaoEncontrada("GestaoMembros");
         } catch (Exception e) {
             mostrarErroGenerico("abrir Gestão de Membros", e);
         }
    }

    private void abrirGestaoLivros(ActionEvent evt) {
         try {
            GestaoLivros gestaoLivros = new GestaoLivros(); // Assume que existe
            gestaoLivros.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
            gestaoLivros.setVisible(true);
         } catch (NoClassDefFoundError e) {
             mostrarErroClasseNaoEncontrada("GestaoLivros");
         } catch (Exception e) {
             mostrarErroGenerico("abrir Gestão de Livros", e);
         }
    }

    private void abrirGestaoEmprestimos(ActionEvent evt) {
         try {
            GestaoEmprestimos gestaoEmp = new GestaoEmprestimos(); // Assume que existe
            gestaoEmp.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
            gestaoEmp.setVisible(true);
         } catch (NoClassDefFoundError e) {
             mostrarErroClasseNaoEncontrada("GestaoEmprestimos");
         } catch (Exception e) {
             mostrarErroGenerico("abrir Gestão de Empréstimos", e);
         }
    }

    private void mostrarErroClasseNaoEncontrada(String nomeClasse) {
        String msg = "Erro: A classe '" + nomeClasse + "' não foi encontrada.\nVerifique o pacote 'gui' e a compilação.";
        System.err.println(msg);
        JOptionPane.showMessageDialog(this, msg, "Erro de Classe", JOptionPane.ERROR_MESSAGE);
    }

    private void mostrarErroGenerico(String acao, Exception e) {
         String msg = "Erro inesperado ao tentar " + acao + ":\n" + e.getMessage();
         System.err.println(msg);
         e.printStackTrace(); // Importante para debug detalhado
         JOptionPane.showMessageDialog(this, msg, "Erro Inesperado", JOptionPane.ERROR_MESSAGE);
    }

     private void mostrarErroFatal(String mensagem, String titulo) {
        JOptionPane.showMessageDialog(this, mensagem, titulo, JOptionPane.ERROR_MESSAGE);
        System.err.println("PaginaInicial ERRO FATAL: " + mensagem);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea2 = new javax.swing.JTextArea();
        jButton1 = new javax.swing.JButton();
        btnAtualizar = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        jMenuItem2 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel1.setText("Empréstimos Recentes (Últimos 5)");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel2.setText("Devoluções Próximas (Próximos 7 dias - Máx. 5)");

        jTextArea1.setEditable(false);
        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jTextArea1.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        jScrollPane1.setViewportView(jTextArea1);

        jTextArea2.setEditable(false);
        jTextArea2.setColumns(20);
        jTextArea2.setRows(5);
        jTextArea2.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        jScrollPane2.setViewportView(jTextArea2);

        jButton1.setText("jButton1"); // Este botão é tornado invisível no construtor

        btnAtualizar.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        btnAtualizar.setText("Atualizar Informação");

        jMenu1.setText("Membros");

        jMenuItem1.setText("Gestão de Membros");

        jMenu1.add(jMenuItem1);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Livros");

        jMenuItem3.setText("Gestão de Livros");

        jMenu2.add(jMenuItem3);

        jMenuBar1.add(jMenu2);

        jMenu3.setText("Empréstimos");

        jMenuItem2.setText("Gestão de Empréstimos");

        jMenu3.add(jMenuItem2);

        jMenuBar1.add(jMenu3);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 560, Short.MAX_VALUE)
                    .addComponent(jScrollPane2)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnAtualizar))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(20, 20, 20))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(btnAtualizar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(20, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(PaginaInicial.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(() -> {
            try {
                 new PaginaInicial().setVisible(true);
            } catch (Exception e) {
                 String msg = "Erro fatal ao iniciar a aplicação:\n" + e.getMessage();
                 System.err.println(msg);
                 e.printStackTrace();
                 JOptionPane.showMessageDialog(null, msg, "Erro Crítico", JOptionPane.ERROR_MESSAGE);
                 System.exit(1);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAtualizar;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextArea jTextArea2;
    // End of variables declaration//GEN-END:variables
}