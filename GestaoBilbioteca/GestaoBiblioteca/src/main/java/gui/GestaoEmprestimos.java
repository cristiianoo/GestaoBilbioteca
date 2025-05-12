package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import modelo.Emprestimo;

public class GestaoEmprestimos extends javax.swing.JFrame {

    private static final String ARQUIVO_LIVROS = "livros.csv";
    private static final String ARQUIVO_MEMBROS = "membros.csv";
    private static final String ARQUIVO_EMPRESTIMOS = "emprestimos.csv";

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    private Map<Integer, String> livros = new HashMap<>();
    private Map<Integer, String> membros = new HashMap<>();
    private List<Emprestimo> emprestimos = new ArrayList<>();

    private DefaultComboBoxModel<String> modeloMembrosCB = new DefaultComboBoxModel<>();
    private DefaultComboBoxModel<String> modeloLivrosCB = new DefaultComboBoxModel<>();
    private DefaultComboBoxModel<String> modeloFiltrosCB = new DefaultComboBoxModel<>();
    private DefaultComboBoxModel<String> modeloMembrosDevCB = new DefaultComboBoxModel<>();
    private DefaultComboBoxModel<String> modeloLivrosDevCB = new DefaultComboBoxModel<>();

    private DefaultTableModel modeloTabela;

    private Map<String, Integer> mapeamentoMembros = new HashMap<>();
    private Map<String, Integer> mapeamentoLivros = new HashMap<>();

    public GestaoEmprestimos() {
        initComponents();
        setLocationRelativeTo(null);
        configurarTabela();
        configurarComboBoxes();

        if (!verificarArquivosCSVExistem()) {
             JOptionPane.showMessageDialog(this,
                "Um ou mais arquivos CSV não foram encontrados na raiz do projeto.\nVerifique o console para detalhes.\nA aplicação pode não funcionar corretamente.",
                "Erro de Arquivo(s)", JOptionPane.ERROR_MESSAGE);
        } else {
            carregarDados();
        }

        configurarListeners();
        atualizarTabelaEmprestimos();

        jComboBox4.addActionListener(e -> atualizarLivrosParaDevolver());

        setTitle("Gestão de Empréstimos - Biblioteca");
    }

    private boolean verificarArquivo(String nomeArquivo) {
        File f = new File(nomeArquivo);
        if (!f.exists() || f.isDirectory()) {
            return false;
        }
        return true;
    }

    private boolean verificarArquivosCSVExistem() {
        boolean livrosOk = verificarArquivo(ARQUIVO_LIVROS);
        boolean membrosOk = verificarArquivo(ARQUIVO_MEMBROS);
        boolean emprestimosOk = verificarArquivo(ARQUIVO_EMPRESTIMOS);
        return livrosOk && membrosOk && emprestimosOk;
    }


    private void configurarTabela() {
        String[] colunas = {"Livro", "Membro", "Estado", "Empréstimo", "Retorno"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        jTable1.setModel(modeloTabela);
    }

    private void configurarComboBoxes() {
        jComboBox1.setModel(modeloMembrosCB);
        jComboBox2.setModel(modeloLivrosCB);

        modeloFiltrosCB.addElement("Todos");
        modeloFiltrosCB.addElement("Ativos");
        modeloFiltrosCB.addElement("Atrasados");
        modeloFiltrosCB.addElement("Devolvidos");
        modeloFiltrosCB.addElement("Por Membro");
        modeloFiltrosCB.addElement("Por Livro");
        jComboBox3.setModel(modeloFiltrosCB);

        jComboBox4.setModel(modeloMembrosDevCB);
        jComboBox5.setModel(modeloLivrosDevCB);
    }

    private void configurarListeners() {
        jButton1.addActionListener(e -> registrarEmprestimo());
        jButton2.addActionListener(e -> filtrarEmprestimos());
        jButton3.addActionListener(e -> exportarParaArquivo());
        jButton4.addActionListener(e -> registrarDevolucao());
    }

    private void carregarDados() {
        livros.clear();
        modeloLivrosCB.removeAllElements();
        mapeamentoLivros.clear();

        membros.clear();
        modeloMembrosCB.removeAllElements();
        modeloMembrosDevCB.removeAllElements();
        mapeamentoMembros.clear();

        emprestimos.clear();

        carregarLivros();
        carregarMembros();
        carregarEmprestimos();
    }

    private void carregarLivros() {
        if (!new File(ARQUIVO_LIVROS).exists()) {
             return;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(ARQUIVO_LIVROS))) {
            String linha = br.readLine(); 
             if (linha == null) { return; }

            while ((linha = br.readLine()) != null) {
                if (linha.trim().isEmpty()) continue;
                String[] partes = linha.split(",");
                if (partes.length >= 2) {
                    try {
                        int id = Integer.parseInt(partes[0].trim());
                        String titulo = partes[1].trim();
                        livros.put(id, titulo);

                        String entradaComboBox = id + " - " + titulo;
                        modeloLivrosCB.addElement(entradaComboBox);
                        mapeamentoLivros.put(entradaComboBox, id);
                    } catch (NumberFormatException e) {
                       
                    }
                } else {
                    
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                "Erro de IO ao ler livros: " + e.getMessage(),
                "Erro de Leitura", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void carregarMembros() {
         if (!new File(ARQUIVO_MEMBROS).exists()) {
             return;
         }
        try (BufferedReader br = new BufferedReader(new FileReader(ARQUIVO_MEMBROS))) {
            String linha = br.readLine(); 
            if (linha == null) { return; }

            while ((linha = br.readLine()) != null) {
                 if (linha.trim().isEmpty()) continue;
                String[] partes = linha.split(",");
                if (partes.length >= 2) {
                     try {
                        int id = Integer.parseInt(partes[0].trim());
                        String nome = partes[1].trim();
                        membros.put(id, nome);

                        String entradaComboBox = id + " - " + nome;
                        modeloMembrosCB.addElement(entradaComboBox);
                        modeloMembrosDevCB.addElement(entradaComboBox);
                        mapeamentoMembros.put(entradaComboBox, id);
                    } catch (NumberFormatException e) {
                        
                    }
                } else {
                     
                }
            }
        } catch (IOException e) {
             JOptionPane.showMessageDialog(this,
                "Erro de IO ao ler membros: " + e.getMessage(),
                "Erro de Leitura", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void carregarEmprestimos() {
         if (!new File(ARQUIVO_EMPRESTIMOS).exists()) {
             return;
         }
        try (BufferedReader br = new BufferedReader(new FileReader(ARQUIVO_EMPRESTIMOS))) {
            String linha = br.readLine(); 
            if (linha == null) { return; }

            while ((linha = br.readLine()) != null) {
                 if (linha.trim().isEmpty()) continue;
                String[] partes = linha.split(",");
                if (partes.length >= 5) {
                    try {
                        int id = Integer.parseInt(partes[0].trim());
                        int idLivro = Integer.parseInt(partes[1].trim());
                        int idMembro = Integer.parseInt(partes[2].trim());
                        Date dataEmprestimo = dateFormat.parse(partes[3].trim());
                        Date dataDevolucaoPrevista = dateFormat.parse(partes[4].trim());

                        if (!livros.containsKey(idLivro)) {
                            continue; 
                        }
                         if (!membros.containsKey(idMembro)) {
                            continue; 
                        }

                        Emprestimo emp = new Emprestimo(id, idLivro, idMembro, dataEmprestimo, dataDevolucaoPrevista);

                        if (partes.length >= 6 && partes[5] != null && !partes[5].trim().isEmpty() && !partes[5].trim().equals("-")) {
                            Date dataDevolucaoEfetiva = dateFormat.parse(partes[5].trim());
                            emp.setDataDevolucaoEfetiva(dataDevolucaoEfetiva);
                        }

                        emprestimos.add(emp);

                    } catch (ParseException | NumberFormatException e) {
                        
                    }
                } else {
                     
                }
            }
        } catch (IOException e) {
             JOptionPane.showMessageDialog(this,
                "Erro de IO ao ler empréstimos: " + e.getMessage(),
                "Erro de Leitura", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void criarArquivoCSVVazio(String nomeArquivo, String cabecalho) {
        File arquivo = new File(nomeArquivo);
        if (!arquivo.exists()) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(arquivo))) {
                bw.write(cabecalho);
                bw.newLine();
            } catch (IOException e) {
                 JOptionPane.showMessageDialog(this,
                    "Não foi possível criar o arquivo: " + nomeArquivo + "\n" + e.getMessage(),
                    "Erro Crítico", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void salvarEmprestimos() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ARQUIVO_EMPRESTIMOS))) {
            bw.write("ID,IDLivro,IDMembro,DataEmprestimo,DataDevolucaoPrevista,DataDevolucaoEfetiva");
            bw.newLine();

            for (Emprestimo emp : emprestimos) {
                StringBuilder sb = new StringBuilder();
                sb.append(emp.getId()).append(",")
                  .append(emp.getIdLivro()).append(",")
                  .append(emp.getIdMembro()).append(",")
                  .append(dateFormat.format(emp.getDataEmprestimo())).append(",")
                  .append(dateFormat.format(emp.getDataDevolucaoPrevista())).append(",");

                if (emp.getDataDevolucaoEfetiva() != null) {
                    sb.append(dateFormat.format(emp.getDataDevolucaoEfetiva()));
                } else {
                    sb.append("-");
                }

                bw.write(sb.toString());
                bw.newLine();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                "Erro ao salvar empréstimos: " + e.getMessage(),
                "Erro de Escrita", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void atualizarTabelaEmprestimos() {
        modeloTabela.setRowCount(0); 

        for (Emprestimo emp : emprestimos) {
            Object[] dados = new Object[5];
            String nomeLivro = livros.get(emp.getIdLivro());
            String nomeMembro = membros.get(emp.getIdMembro());

            dados[0] = (nomeLivro != null) ? nomeLivro : "Livro ID: " + emp.getIdLivro() + " (Não encontrado)";
            dados[1] = (nomeMembro != null) ? nomeMembro : "Membro ID: " + emp.getIdMembro() + " (Não encontrado)";
            dados[2] = emp.getEstadoTexto();
            dados[3] = Emprestimo.formatarData(emp.getDataEmprestimo());

            if (emp.getDataDevolucaoEfetiva() != null) {
                dados[4] = Emprestimo.formatarData(emp.getDataDevolucaoEfetiva());
            } else {
                dados[4] = Emprestimo.formatarData(emp.getDataDevolucaoPrevista()) + " (prev.)";
            }
            modeloTabela.addRow(dados);
        }
    }

    private void registrarEmprestimo() {
        try {
            String membroSelecionadoStr = (String) jComboBox1.getSelectedItem();
            if (membroSelecionadoStr == null || membroSelecionadoStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Selecione um membro.", "Aviso", JOptionPane.WARNING_MESSAGE); return;
            }
            Integer idMembro = mapeamentoMembros.get(membroSelecionadoStr);
             if (idMembro == null) {
                 JOptionPane.showMessageDialog(this, "Erro interno: Não foi possível encontrar o ID para o membro selecionado.", "Erro", JOptionPane.ERROR_MESSAGE); return;
             }

            String livroSelecionadoStr = (String) jComboBox2.getSelectedItem();
            if (livroSelecionadoStr == null || livroSelecionadoStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Selecione um livro.", "Aviso", JOptionPane.WARNING_MESSAGE); return;
            }
             Integer idLivro = mapeamentoLivros.get(livroSelecionadoStr);
             if (idLivro == null) {
                 JOptionPane.showMessageDialog(this, "Erro interno: Não foi possível encontrar o ID para o livro selecionado.", "Erro", JOptionPane.ERROR_MESSAGE); return;
             }

            for (Emprestimo emp : emprestimos) {
                if (emp.getIdLivro() == idLivro && emp.isAtivo()) {
                    JOptionPane.showMessageDialog(this,
                        "Este livro já está emprestado para " + membros.getOrDefault(emp.getIdMembro(), "ID " + emp.getIdMembro()) + ".",
                        "Livro Indisponível", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }

            String dataTexto = jTextField1.getText().trim();
            Date dataDevolucaoPrevista;
             if (dataTexto.isEmpty()) { 
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DAY_OF_MONTH, 14); 
                dataDevolucaoPrevista = cal.getTime();
            } else { 
                 try {
                    dataDevolucaoPrevista = dateFormat.parse(dataTexto);
                    Date hojeNormalizada = dateFormat.parse(dateFormat.format(new Date()));
                    Date dataDevNormalizada = dateFormat.parse(dateFormat.format(dataDevolucaoPrevista));
                    if (dataDevNormalizada.before(hojeNormalizada) ) {
                        JOptionPane.showMessageDialog(this,
                            "A data de devolução deve ser hoje ou uma data futura.",
                            "Data Inválida", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                } catch (ParseException e) { 
                     JOptionPane.showMessageDialog(this,
                        "Data de devolução inválida. Use o formato dd/MM/yyyy.",
                        "Erro de Formato", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            Date dataEmprestimo = new Date();
            int novoId = gerarProximoIdEmprestimo();
            Emprestimo novoEmprestimo = new Emprestimo(novoId, idLivro, idMembro, dataEmprestimo, dataDevolucaoPrevista);

            emprestimos.add(novoEmprestimo);
            salvarEmprestimos();
            atualizarTabelaEmprestimos();
            atualizarLivrosParaDevolver(); 

            JOptionPane.showMessageDialog(this, "Empréstimo registrado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            jTextField1.setText(""); 

        } catch (Exception e) { 
            JOptionPane.showMessageDialog(this, "Erro ao registrar empréstimo:\n" + e.getMessage(), "Erro Inesperado", JOptionPane.ERROR_MESSAGE);
        }
    }

    private int gerarProximoIdEmprestimo() {
        int maxId = 0;
        for (Emprestimo emp : emprestimos) {
            if (emp.getId() > maxId) {
                maxId = emp.getId();
            }
        }
        return maxId + 1;
    }

    private void filtrarEmprestimos() {
        String filtroSelecionado = (String) jComboBox3.getSelectedItem();
        String textoBusca = jTextField2.getText().trim().toLowerCase();

        List<Emprestimo> emprestimosFiltrados = new ArrayList<>(); 

        for (Emprestimo emp : this.emprestimos) { 
            boolean incluir = false;
            String nomeMembro = membros.getOrDefault(emp.getIdMembro(), "").toLowerCase();
            String tituloLivro = livros.getOrDefault(emp.getIdLivro(), "").toLowerCase();

            switch (filtroSelecionado) {
                case "Todos": incluir = true; break;
                case "Ativos": incluir = emp.isAtivo() && !emp.isAtrasado(); break;
                case "Atrasados": incluir = emp.isAtrasado(); break;
                case "Devolvidos": incluir = !emp.isAtivo(); break;
                case "Por Membro": incluir = nomeMembro.contains(textoBusca); break;
                case "Por Livro": incluir = tituloLivro.contains(textoBusca); break;
            }

            if (incluir) {
                emprestimosFiltrados.add(emp);
            }
        }

        modeloTabela.setRowCount(0); 
         for (Emprestimo emp : emprestimosFiltrados) { 
            Object[] dados = new Object[5];
            String nomeLivro = livros.get(emp.getIdLivro());
            String nomeMembro = membros.get(emp.getIdMembro());

            dados[0] = (nomeLivro != null) ? nomeLivro : "Livro ID: " + emp.getIdLivro() + " (Não encontrado)";
            dados[1] = (nomeMembro != null) ? nomeMembro : "Membro ID: " + emp.getIdMembro() + " (Não encontrado)";
            dados[2] = emp.getEstadoTexto();
            dados[3] = Emprestimo.formatarData(emp.getDataEmprestimo());
            if (emp.getDataDevolucaoEfetiva() != null) {
                dados[4] = Emprestimo.formatarData(emp.getDataDevolucaoEfetiva());
            } else {
                dados[4] = Emprestimo.formatarData(emp.getDataDevolucaoPrevista()) + " (prev.)";
            }
            modeloTabela.addRow(dados);
        }
    }

    private void exportarParaArquivo() {
        if (modeloTabela.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Não há dados na tabela para exportar.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Exportar Tabela para CSV");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Arquivos CSV (*.csv)", "csv"));
        fileChooser.setSelectedFile(new File("export_emprestimos_filtrados.csv"));

        int resultado = fileChooser.showSaveDialog(this);
        if (resultado == JFileChooser.APPROVE_OPTION) {
            File arquivo = fileChooser.getSelectedFile();
             if (!arquivo.getName().toLowerCase().endsWith(".csv")) {
                arquivo = new File(arquivo.getAbsolutePath() + ".csv");
            }
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(arquivo))) {
                for (int j = 0; j < modeloTabela.getColumnCount(); j++) { 
                     bw.write("\"" + modeloTabela.getColumnName(j) + "\""); 
                    if (j < modeloTabela.getColumnCount() - 1) bw.write(",");
                }
                bw.newLine();
                for (int i = 0; i < modeloTabela.getRowCount(); i++) { 
                    for (int j = 0; j < modeloTabela.getColumnCount(); j++) {
                        Object valor = modeloTabela.getValueAt(i, j);
                        String valorStr = (valor == null) ? "" : valor.toString();
                        bw.write("\"" + valorStr.replace("\"", "\"\"") + "\"");
                        if (j < modeloTabela.getColumnCount() - 1) bw.write(",");
                    }
                    bw.newLine();
                }
                JOptionPane.showMessageDialog(this, "Dados da tabela exportados com sucesso!", "Exportação Concluída", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) { 
                 JOptionPane.showMessageDialog(this, "Erro ao exportar dados: " + e.getMessage(), "Erro de Exportação", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void atualizarLivrosParaDevolver() {
        modeloLivrosDevCB.removeAllElements(); 
        String membroSelecionadoStr = (String) jComboBox4.getSelectedItem();
        if (membroSelecionadoStr == null || membroSelecionadoStr.isEmpty()) {
            return; 
        }

        Integer idMembro = mapeamentoMembros.get(membroSelecionadoStr);
        if (idMembro == null) {
            return;
        }

        for (Emprestimo emp : emprestimos) {
            if (emp.getIdMembro() == idMembro && emp.isAtivo()) {
                String tituloLivro = livros.get(emp.getIdLivro()); 
                if (tituloLivro != null) {
                    String entradaComboBox = emp.getIdLivro() + " - " + tituloLivro;
                    modeloLivrosDevCB.addElement(entradaComboBox);
                } else {
                     String entradaComboBox = emp.getIdLivro() + " - [Livro não encontrado]";
                     modeloLivrosDevCB.addElement(entradaComboBox);
                }
            }
        }
    }

    private void registrarDevolucao() {
         try {
            String membroSelecionadoStr = (String) jComboBox4.getSelectedItem();
            if (membroSelecionadoStr == null || membroSelecionadoStr.isEmpty()) { 
                 JOptionPane.showMessageDialog(this, "Selecione um membro.", "Aviso", JOptionPane.WARNING_MESSAGE); return;
            }
             Integer idMembro = mapeamentoMembros.get(membroSelecionadoStr);
             if (idMembro == null) { 
                 JOptionPane.showMessageDialog(this, "Erro interno: Não foi possível encontrar o ID para o membro selecionado.", "Erro", JOptionPane.ERROR_MESSAGE); return;
             }

            String livroSelecionadoStr = (String) jComboBox5.getSelectedItem();
            if (livroSelecionadoStr == null || livroSelecionadoStr.isEmpty()) { 
                 JOptionPane.showMessageDialog(this, "Selecione um livro para devolver.", "Aviso", JOptionPane.WARNING_MESSAGE); return;
            }

            int idLivro = -1;
            try {
                String idStr = livroSelecionadoStr.split(" - ")[0].trim();
                idLivro = Integer.parseInt(idStr);
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                 JOptionPane.showMessageDialog(this, "Formato inválido para o item do livro selecionado: '" + livroSelecionadoStr + "'", "Erro de Formato", JOptionPane.ERROR_MESSAGE);
                return;
            }
             if (idLivro == -1) return; 

            Emprestimo emprestimoParaDevolver = null;
            for (Emprestimo emp : emprestimos) {
                if (emp.getIdLivro() == idLivro && emp.getIdMembro() == idMembro && emp.isAtivo()) {
                    emprestimoParaDevolver = emp;
                    break;
                }
            }

            if (emprestimoParaDevolver == null) { 
                 JOptionPane.showMessageDialog(this, "Não foi encontrado empréstimo ativo deste livro para este membro.", "Empréstimo Não Encontrado", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Date dataDevolucaoEfetiva = new Date();
            emprestimoParaDevolver.setDataDevolucaoEfetiva(dataDevolucaoEfetiva);

            salvarEmprestimos();
            atualizarTabelaEmprestimos();
            atualizarLivrosParaDevolver(); 

            String mensagem = "Devolução registrada com sucesso!";
            long diasAtraso = emprestimoParaDevolver.getDiasAtraso();
            if (diasAtraso > 0) {
                mensagem += "\nLivro devolvido com " + diasAtraso + " dia(s) de atraso.";
            }
            JOptionPane.showMessageDialog(this, mensagem, "Devolução Concluída", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) { 
             JOptionPane.showMessageDialog(this, "Erro ao registrar devolução:\n" + e.getMessage(), "Erro Inesperado", JOptionPane.ERROR_MESSAGE);
        }
    }


    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    // Código gerado pelo NetBeans (deve permanecer o mesmo)
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        jComboBox2 = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jComboBox3 = new javax.swing.JComboBox<>();
        jButton2 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButton3 = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JSeparator();
        jSeparator4 = new javax.swing.JSeparator();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jComboBox4 = new javax.swing.JComboBox<>();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jComboBox5 = new javax.swing.JComboBox<>();
        jButton4 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel1.setText("Novo Empréstimo");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel2.setText("Membro");

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel3.setText("Livro");

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel4.setText("Data Devolução (dd/mm/aaaa)");

        jTextField1.setToolTipText("Deixe em branco para 14 dias padrão");

        jButton1.setText("Validar Empréstimo");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel5.setText("Histórico de Empréstimos");

        jTextField2.setToolTipText("Termo para busca em Membro ou Livro (se filtro selecionado)");
        jTextField2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField2ActionPerformed(evt);
            }
        });

        jComboBox3.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBox3.setToolTipText("Selecione o tipo de filtro");

        jButton2.setText("Procurar / Filtrar");

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Livro", "Membro", "Estado", "Empréstimo", "Retorno"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        jButton3.setText("Exportar Tabela Visível");

        jLabel6.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel6.setText("Devolução de Empréstimo");

        jLabel7.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel7.setText("Membro");

        jComboBox4.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel9.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel9.setText("Livro Emprestado");

        jComboBox5.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jButton4.setText("Validar Devolução");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator4)
                    .addComponent(jSeparator3)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextField2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel5)
                            .addComponent(jLabel6)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel7)
                                    .addComponent(jLabel9))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jComboBox4, 0, 350, Short.MAX_VALUE)
                                    .addComponent(jComboBox5, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(18, 18, 18)
                                .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel2)
                                    .addComponent(jLabel3))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jComboBox1, 0, 350, Short.MAX_VALUE)
                                    .addComponent(jComboBox2, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jLabel4))))
                        .addGap(0, 116, Short.MAX_VALUE)))
                .addGap(20, 20, 20))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 460, Short.MAX_VALUE)
                    .addComponent(jLabel8)
                    .addGap(0, 460, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1))
                .addGap(18, 18, 18)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel6)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jComboBox4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBox5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addGap(18, 18, 18)
                .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton2)
                    .addComponent(jButton3))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE) 
                .addGap(20, 20, 20))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 340, Short.MAX_VALUE)
                    .addComponent(jLabel8)
                    .addGap(0, 340, Short.MAX_VALUE)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTextField2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField2ActionPerformed
       filtrarEmprestimos(); 
    }//GEN-LAST:event_jTextField2ActionPerformed

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(GestaoEmprestimos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(GestaoEmprestimos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(GestaoEmprestimos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GestaoEmprestimos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(() -> {
            new GestaoEmprestimos().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JComboBox<String> jComboBox2;
    private javax.swing.JComboBox<String> jComboBox3;
    private javax.swing.JComboBox<String> jComboBox4;
    private javax.swing.JComboBox<String> jComboBox5;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    // End of variables declaration//GEN-END:variables
}